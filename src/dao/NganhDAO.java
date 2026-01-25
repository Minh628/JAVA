/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: NganhDAO - Data Access Object cho bảng Nganh
 */
package dao;

import config.DatabaseHelper;
import dto.NganhDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NganhDAO {
    
    /**
     * Lấy tất cả ngành
     */
    public List<NganhDTO> getAll() throws SQLException {
        List<NganhDTO> danhSachNganh = new ArrayList<>();
        String sql = "SELECT n.*, k.ten_khoa FROM Nganh n " +
                     "LEFT JOIN Khoa k ON n.ma_khoa = k.ma_khoa " +
                     "ORDER BY n.ten_nganh";
        
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                danhSachNganh.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachNganh;
    }
    
    /**
     * Lấy ngành theo khoa
     */
    public List<NganhDTO> getByKhoa(int maKhoa) throws SQLException {
        List<NganhDTO> danhSachNganh = new ArrayList<>();
        String sql = "SELECT n.*, k.ten_khoa FROM Nganh n " +
                     "LEFT JOIN Khoa k ON n.ma_khoa = k.ma_khoa " +
                     "WHERE n.ma_khoa = ? ORDER BY n.ten_nganh";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maKhoa);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSachNganh.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachNganh;
    }
    
    /**
     * Lấy ngành theo mã
     */
    public NganhDTO getById(int maNganh) throws SQLException {
        String sql = "SELECT n.*, k.ten_khoa FROM Nganh n " +
                     "LEFT JOIN Khoa k ON n.ma_khoa = k.ma_khoa " +
                     "WHERE n.ma_nganh = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maNganh);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }
    
    /**
     * Thêm ngành mới
     */
    public boolean insert(NganhDTO nganh) throws SQLException {
        String sql = "INSERT INTO Nganh (ma_khoa, ten_nganh) VALUES (?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, nganh.getMaKhoa());
            pstmt.setString(2, nganh.getTenNganh());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    nganh.setMaNganh(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cập nhật ngành
     */
    public boolean update(NganhDTO nganh) throws SQLException {
        String sql = "UPDATE Nganh SET ma_khoa = ?, ten_nganh = ? WHERE ma_nganh = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nganh.getMaKhoa());
            pstmt.setString(2, nganh.getTenNganh());
            pstmt.setInt(3, nganh.getMaNganh());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa ngành
     */
    public boolean delete(int maNganh) throws SQLException {
        String sql = "DELETE FROM Nganh WHERE ma_nganh = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maNganh);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map ResultSet sang DTO
     */
    private NganhDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        NganhDTO nganh = new NganhDTO();
        nganh.setMaNganh(rs.getInt("ma_nganh"));
        nganh.setMaKhoa(rs.getInt("ma_khoa"));
        nganh.setTenNganh(rs.getString("ten_nganh"));
        nganh.setTenKhoa(rs.getString("ten_khoa"));
        return nganh;
    }
}
