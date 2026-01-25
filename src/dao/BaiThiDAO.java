/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: BaiThiDAO - Data Access Object cho bảng BaiThi
 */
package dao;

import config.DatabaseHelper;
import dto.BaiThiDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaiThiDAO {
    
    /**
     * Lấy bài thi theo sinh viên
     */
    public List<BaiThiDTO> getBySinhVien(int maSV) throws SQLException {
        List<BaiThiDTO> danhSachBT = new ArrayList<>();
        String sql = "SELECT bt.*, dt.ten_de_thi, dt.so_cau_hoi AS tong_so_cau, dt.thoi_gian_lam, " +
                     "hp.ten_mon AS ten_hoc_phan, CONCAT(sv.ho, ' ', sv.ten) AS ten_sv, sv.ten_dang_nhap AS ma_so_sv " +
                     "FROM BaiThi bt " +
                     "LEFT JOIN DeThi dt ON bt.ma_de_thi = dt.ma_de_thi " +
                     "LEFT JOIN HocPhan hp ON dt.ma_hoc_phan = hp.ma_hoc_phan " +
                     "LEFT JOIN SinhVien sv ON bt.ma_sv = sv.ma_sv " +
                     "WHERE bt.ma_sv = ? ORDER BY bt.thoi_gian_bat_dau DESC";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maSV);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSachBT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachBT;
    }
    
    /**
     * Lấy bài thi theo đề thi
     */
    public List<BaiThiDTO> getByDeThi(int maDeThi) throws SQLException {
        List<BaiThiDTO> danhSachBT = new ArrayList<>();
        String sql = "SELECT bt.*, dt.ten_de_thi, dt.so_cau_hoi AS tong_so_cau, dt.thoi_gian_lam, " +
                     "hp.ten_mon AS ten_hoc_phan, CONCAT(sv.ho, ' ', sv.ten) AS ten_sv, sv.ten_dang_nhap AS ma_so_sv " +
                     "FROM BaiThi bt " +
                     "LEFT JOIN DeThi dt ON bt.ma_de_thi = dt.ma_de_thi " +
                     "LEFT JOIN HocPhan hp ON dt.ma_hoc_phan = hp.ma_hoc_phan " +
                     "LEFT JOIN SinhVien sv ON bt.ma_sv = sv.ma_sv " +
                     "WHERE bt.ma_de_thi = ? ORDER BY bt.diem_so DESC";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maDeThi);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSachBT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachBT;
    }
    
    /**
     * Lấy bài thi theo mã
     */
    public BaiThiDTO getById(int maBaiThi) throws SQLException {
        String sql = "SELECT bt.*, dt.ten_de_thi, dt.so_cau_hoi AS tong_so_cau, dt.thoi_gian_lam, " +
                     "hp.ten_mon AS ten_hoc_phan, CONCAT(sv.ho, ' ', sv.ten) AS ten_sv, sv.ten_dang_nhap AS ma_so_sv " +
                     "FROM BaiThi bt " +
                     "LEFT JOIN DeThi dt ON bt.ma_de_thi = dt.ma_de_thi " +
                     "LEFT JOIN HocPhan hp ON dt.ma_hoc_phan = hp.ma_hoc_phan " +
                     "LEFT JOIN SinhVien sv ON bt.ma_sv = sv.ma_sv " +
                     "WHERE bt.ma_bai_thi = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maBaiThi);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }
    
    /**
     * Kiểm tra sinh viên đã thi đề này chưa
     */
    public boolean checkDaThi(int maDeThi, int maSV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BaiThi WHERE ma_de_thi = ? AND ma_sv = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maDeThi);
            pstmt.setInt(2, maSV);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    /**
     * Thêm bài thi mới
     */
    public boolean insert(BaiThiDTO baiThi) throws SQLException {
        String sql = "INSERT INTO BaiThi (ma_de_thi, ma_sv, thoi_gian_bat_dau, ngay_thi, so_cau_dung, so_cau_sai, diem_so) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, baiThi.getMaDeThi());
            pstmt.setInt(2, baiThi.getMaSV());
            pstmt.setTimestamp(3, baiThi.getThoiGianBatDau());
            pstmt.setDate(4, baiThi.getNgayThi());
            pstmt.setInt(5, baiThi.getSoCauDung());
            pstmt.setInt(6, baiThi.getSoCauSai());
            pstmt.setFloat(7, baiThi.getDiemSo());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    baiThi.setMaBaiThi(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cập nhật kết quả bài thi
     */
    public boolean updateKetQua(BaiThiDTO baiThi) throws SQLException {
        String sql = "UPDATE BaiThi SET thoi_gian_nop = ?, so_cau_dung = ?, so_cau_sai = ?, diem_so = ? " +
                     "WHERE ma_bai_thi = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, baiThi.getThoiGianNop());
            pstmt.setInt(2, baiThi.getSoCauDung());
            pstmt.setInt(3, baiThi.getSoCauSai());
            pstmt.setFloat(4, baiThi.getDiemSo());
            pstmt.setInt(5, baiThi.getMaBaiThi());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa bài thi
     */
    public boolean delete(int maBaiThi) throws SQLException {
        String sql = "DELETE FROM BaiThi WHERE ma_bai_thi = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maBaiThi);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map ResultSet sang DTO
     */
    private BaiThiDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        BaiThiDTO bt = new BaiThiDTO();
        bt.setMaBaiThi(rs.getInt("ma_bai_thi"));
        bt.setMaDeThi(rs.getInt("ma_de_thi"));
        bt.setMaSV(rs.getInt("ma_sv"));
        bt.setThoiGianBatDau(rs.getTimestamp("thoi_gian_bat_dau"));
        bt.setThoiGianNop(rs.getTimestamp("thoi_gian_nop"));
        bt.setNgayThi(rs.getDate("ngay_thi"));
        bt.setSoCauDung(rs.getInt("so_cau_dung"));
        bt.setSoCauSai(rs.getInt("so_cau_sai"));
        bt.setDiemSo(rs.getFloat("diem_so"));
        bt.setTenDeThi(rs.getString("ten_de_thi"));
        bt.setTenSV(rs.getString("ten_sv"));
        bt.setMaSoSV(rs.getString("ma_so_sv"));
        bt.setTenHocPhan(rs.getString("ten_hoc_phan"));
        bt.setTongSoCau(rs.getInt("tong_so_cau"));
        bt.setThoiGianLam(rs.getInt("thoi_gian_lam"));
        return bt;
    }
}
