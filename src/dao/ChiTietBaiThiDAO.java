/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: ChiTietBaiThiDAO - Data Access Object cho bảng ChiTietBaiThi
 * (Thay thế KetQuaDAO cũ)
 */
package dao;

import config.DatabaseHelper;
import dto.ChiTietBaiThiDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietBaiThiDAO {

    /**
     * Lấy chi tiết bài thi
     * Chấm điểm bằng cách so sánh đáp án sinh viên (A/B/C/D) với nội dung đáp án đúng
     */
    public List<ChiTietBaiThiDTO> getByBaiThi(int maBaiThi) throws SQLException {
        List<ChiTietBaiThiDTO> danhSachCT = new ArrayList<>();
        String sql = "SELECT ctbt.*, ch.noi_dung_cau_hoi, ch.loai_cau_hoi, " +
                     "mc.noi_dung_A, mc.noi_dung_B, mc.noi_dung_C, mc.noi_dung_D, " +
                     "COALESCE(mc.noi_dung_dung, dk.noi_dung_dung) AS dap_an_dung " +
                     "FROM ChiTietBaiThi ctbt " +
                     "LEFT JOIN CauHoi ch ON ctbt.ma_cau_hoi = ch.ma_cau_hoi " +
                     "LEFT JOIN CauHoiMC mc ON ch.ma_cau_hoi = mc.ma_cau_hoi_MC " +
                     "LEFT JOIN CauHoiDK dk ON ch.ma_cau_hoi = dk.ma_cau_hoi_DK " +
                     "WHERE ctbt.ma_bai_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maBaiThi);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChiTietBaiThiDTO ct = new ChiTietBaiThiDTO();
                ct.setMaBaiThi(rs.getInt("ma_bai_thi"));
                ct.setMaCauHoi(rs.getInt("ma_cau_hoi"));
                ct.setDapAnSV(rs.getString("dap_an_sv"));
                ct.setNoiDungCauHoi(rs.getString("noi_dung_cau_hoi"));
                ct.setLoaiCauHoi(rs.getString("loai_cau_hoi"));
                
                String dapAnDung = rs.getString("dap_an_dung");
                ct.setDapAnDung(dapAnDung);
                
                // Chấm điểm cho câu hỏi trắc nghiệm (MC)
                String loai = rs.getString("loai_cau_hoi");
                String dapAnSV = rs.getString("dap_an_sv");
                
                if ("MC".equals(loai) && dapAnSV != null && !dapAnSV.isEmpty()) {
                    // Lấy nội dung đáp án tương ứng với lựa chọn của SV
                    String noiDungDapAnSV = null;
                    switch (dapAnSV.toUpperCase()) {
                        case "A": noiDungDapAnSV = rs.getString("noi_dung_A"); break;
                        case "B": noiDungDapAnSV = rs.getString("noi_dung_B"); break;
                        case "C": noiDungDapAnSV = rs.getString("noi_dung_C"); break;
                        case "D": noiDungDapAnSV = rs.getString("noi_dung_D"); break;
                    }
                    // So sánh nội dung đáp án SV chọn với đáp án đúng
                    ct.setLaDung(noiDungDapAnSV != null && dapAnDung != null && 
                                 noiDungDapAnSV.trim().equalsIgnoreCase(dapAnDung.trim()));
                } else {
                    // Câu hỏi điền khuyết: so sánh trực tiếp
                    ct.setLaDung(ct.kiemTraDapAn());
                }
                
                danhSachCT.add(ct);
            }
        }
        return danhSachCT;
    }

    /**
     * Thêm chi tiết bài thi
     */
    public boolean insert(ChiTietBaiThiDTO chiTiet) throws SQLException {
        String sql = "INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, chiTiet.getMaBaiThi());
            pstmt.setInt(2, chiTiet.getMaCauHoi());
            pstmt.setString(3, chiTiet.getDapAnSV());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Thêm nhiều chi tiết bài thi cùng lúc
     */
    public boolean insertBatch(List<ChiTietBaiThiDTO> danhSachChiTiet) throws SQLException {
        String sql = "INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (ChiTietBaiThiDTO ct : danhSachChiTiet) {
                pstmt.setInt(1, ct.getMaBaiThi());
                pstmt.setInt(2, ct.getMaCauHoi());
                pstmt.setString(3, ct.getDapAnSV());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            conn.commit();

            for (int result : results) {
                if (result < 0) return false;
            }
            return true;
        }
    }

    /**
     * Cập nhật đáp án sinh viên
     */
    public boolean updateDapAn(int maBaiThi, int maCauHoi, String dapAnSV) throws SQLException {
        String sql = "UPDATE ChiTietBaiThi SET dap_an_sv = ? WHERE ma_bai_thi = ? AND ma_cau_hoi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dapAnSV);
            pstmt.setInt(2, maBaiThi);
            pstmt.setInt(3, maCauHoi);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa chi tiết bài thi
     */
    public boolean deleteByBaiThi(int maBaiThi) throws SQLException {
        String sql = "DELETE FROM ChiTietBaiThi WHERE ma_bai_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maBaiThi);
            return pstmt.executeUpdate() >= 0;
        }
    }
}
