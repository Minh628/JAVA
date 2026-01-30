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
     * Lấy chi tiết bài thi theo mã bài thi
     */
    public List<ChiTietBaiThiDTO> getByBaiThi(int maBaiThi) throws SQLException {
        List<ChiTietBaiThiDTO> danhSachCT = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietBaiThi WHERE ma_bai_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maBaiThi);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChiTietBaiThiDTO ct = mapResultSetToDTO(rs);
                danhSachCT.add(ct);
            }
        }
        return danhSachCT;
    }

    /**
     * Lấy chi tiết bài thi theo mã bài thi và mã câu hỏi
     */
    public ChiTietBaiThiDTO getById(int maBaiThi, int maCauHoi) throws SQLException {
        String sql = "SELECT * FROM ChiTietBaiThi WHERE ma_bai_thi = ? AND ma_cau_hoi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maBaiThi);
            pstmt.setInt(2, maCauHoi);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }

    /**
     * Map ResultSet thành DTO
     */
    private ChiTietBaiThiDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        ChiTietBaiThiDTO ct = new ChiTietBaiThiDTO();
        ct.setMaBaiThi(rs.getInt("ma_bai_thi"));
        ct.setMaCauHoi(rs.getInt("ma_cau_hoi"));
        ct.setDapAnSV(rs.getString("dap_an_sv"));
        return ct;
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

    /**
     * Đếm số lần câu hỏi xuất hiện trong các bài thi
     */
    public int countByCauHoi(int maCauHoi) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChiTietBaiThi WHERE ma_cau_hoi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maCauHoi);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
