/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: SinhVienDAO - Data Access Object cho bảng SinhVien
 */
package dao;

import config.DatabaseHelper;
import dto.SinhVienDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {
    
    /**
     * Lấy tất cả sinh viên
     */
    public List<SinhVienDTO> getAll() throws SQLException {
        List<SinhVienDTO> danhSachSV = new ArrayList<>();
        String sql = "SELECT sv.*, n.ten_nganh, vt.ten_vai_tro FROM SinhVien sv " +
                     "LEFT JOIN Nganh n ON sv.ma_nganh = n.ma_nganh " +
                     "LEFT JOIN VaiTro vt ON sv.ma_vai_tro = vt.ma_vai_tro " +
                     "ORDER BY sv.ho, sv.ten";
        
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                danhSachSV.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachSV;
    }
    
    /**
     * Lấy sinh viên theo mã
     */
    public SinhVienDTO getById(int maSV) throws SQLException {
        String sql = "SELECT sv.*, n.ten_nganh, vt.ten_vai_tro FROM SinhVien sv " +
                     "LEFT JOIN Nganh n ON sv.ma_nganh = n.ma_nganh " +
                     "LEFT JOIN VaiTro vt ON sv.ma_vai_tro = vt.ma_vai_tro " +
                     "WHERE sv.ma_sv = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maSV);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }
    
    /**
     * Lấy sinh viên theo tên đăng nhập
     */
    public SinhVienDTO getByTenDangNhap(String tenDangNhap) throws SQLException {
        String sql = "SELECT sv.*, n.ten_nganh, vt.ten_vai_tro FROM SinhVien sv " +
                     "LEFT JOIN Nganh n ON sv.ma_nganh = n.ma_nganh " +
                     "LEFT JOIN VaiTro vt ON sv.ma_vai_tro = vt.ma_vai_tro " +
                     "WHERE sv.ten_dang_nhap = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tenDangNhap);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }
    
    /**
     * Lấy sinh viên theo ngành
     */
    public List<SinhVienDTO> getByNganh(int maNganh) throws SQLException {
        List<SinhVienDTO> danhSachSV = new ArrayList<>();
        String sql = "SELECT sv.*, n.ten_nganh, vt.ten_vai_tro FROM SinhVien sv " +
                     "LEFT JOIN Nganh n ON sv.ma_nganh = n.ma_nganh " +
                     "LEFT JOIN VaiTro vt ON sv.ma_vai_tro = vt.ma_vai_tro " +
                     "WHERE sv.ma_nganh = ? ORDER BY sv.ho, sv.ten";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maNganh);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSachSV.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachSV;
    }
    
    /**
     * Thêm sinh viên mới
     */
    public boolean insert(SinhVienDTO sinhVien) throws SQLException {
        String sql = "INSERT INTO SinhVien (ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, " +
                     "ho, ten, email, ngay_tao, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, 3); // Vai trò sinh viên
            pstmt.setInt(2, sinhVien.getMaNganh());
            pstmt.setString(3, sinhVien.getTenDangNhap());
            pstmt.setString(4, sinhVien.getMatKhau());
            pstmt.setString(5, sinhVien.getHo());
            pstmt.setString(6, sinhVien.getTen());
            pstmt.setString(7, sinhVien.getEmail());
            pstmt.setBoolean(8, sinhVien.isTrangThai());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    sinhVien.setMaSV(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cập nhật sinh viên
     */
    public boolean update(SinhVienDTO sinhVien) throws SQLException {
        String sql = "UPDATE SinhVien SET ma_nganh = ?, ho = ?, ten = ?, " +
                     "email = ?, trang_thai = ? WHERE ma_sv = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sinhVien.getMaNganh());
            pstmt.setString(2, sinhVien.getHo());
            pstmt.setString(3, sinhVien.getTen());
            pstmt.setString(4, sinhVien.getEmail());
            pstmt.setBoolean(5, sinhVien.isTrangThai());
            pstmt.setInt(6, sinhVien.getMaSV());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật mật khẩu
     */
    public boolean updatePassword(int maSV, String matKhauMoi) throws SQLException {
        String sql = "UPDATE SinhVien SET mat_khau = ? WHERE ma_sv = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matKhauMoi);
            pstmt.setInt(2, maSV);
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa sinh viên
     */
    public boolean delete(int maSV) throws SQLException {
        String sql = "DELETE FROM SinhVien WHERE ma_sv = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maSV);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Kiểm tra tên đăng nhập đã tồn tại
     */
    public boolean checkTenDangNhapExists(String tenDangNhap) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SinhVien WHERE ten_dang_nhap = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tenDangNhap);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    /**
     * Map ResultSet sang DTO
     */
    private SinhVienDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        SinhVienDTO sv = new SinhVienDTO();
        sv.setMaSV(rs.getInt("ma_sv"));
        sv.setMaVaiTro(rs.getInt("ma_vai_tro"));
        sv.setMaNganh(rs.getInt("ma_nganh"));
        sv.setTenDangNhap(rs.getString("ten_dang_nhap"));
        sv.setMatKhau(rs.getString("mat_khau"));
        sv.setHo(rs.getString("ho"));
        sv.setTen(rs.getString("ten"));
        sv.setEmail(rs.getString("email"));
        sv.setNgayTao(rs.getTimestamp("ngay_tao"));
        sv.setTrangThai(rs.getBoolean("trang_thai"));
        sv.setTenNganh(rs.getString("ten_nganh"));
        sv.setTenVaiTro(rs.getString("ten_vai_tro"));
        return sv;
    }
}
