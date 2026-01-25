/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: KetQuaDAO - Data Access Object cho kết quả thi
 */
package dao;

import config.DatabaseHelper;
import dto.ChiTietBaiThiDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KetQuaDAO {
    
    /**
     * Lấy chi tiết bài thi
     */
    public List<ChiTietBaiThiDTO> getChiTietBaiThi(int maBaiThi) throws SQLException {
        List<ChiTietBaiThiDTO> danhSachCT = new ArrayList<>();
        String sql = "SELECT ctbt.*, ch.noi_dung_cau_hoi, ch.loai_cau_hoi, " +
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
                ct.setDapAnDung(rs.getString("dap_an_dung"));
                ct.setLaDung(ct.kiemTraDapAn());
                danhSachCT.add(ct);
            }
        }
        return danhSachCT;
    }
    
    /**
     * Thêm chi tiết bài thi
     */
    public boolean insertChiTiet(ChiTietBaiThiDTO chiTiet) throws SQLException {
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
     * Thêm nhiều chi tiết bài thi cùng lúc
     */
    public boolean insertChiTietBatch(List<ChiTietBaiThiDTO> danhSachChiTiet) throws SQLException {
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
}
