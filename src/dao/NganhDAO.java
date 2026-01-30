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
        String sql = "SELECT * FROM Nganh ORDER BY ten_nganh";

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
        String sql = "SELECT * FROM Nganh WHERE ma_khoa = ? ORDER BY ten_nganh";

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
        String sql = "SELECT * FROM Nganh WHERE ma_nganh = ?";

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
     * Tìm kiếm ngành theo từ khóa
     * Tìm trong: mã ngành, tên ngành
     */
    public List<NganhDTO> search(String keyword) throws SQLException {
        List<NganhDTO> danhSachNganh = new ArrayList<>();
        String sql = "SELECT * FROM Nganh " +
                "WHERE ma_nganh LIKE ? OR ten_nganh LIKE ? " +
                "ORDER BY ten_nganh";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSachNganh.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachNganh;
    }

    /**
     * Đếm số ngành thuộc khoa
     */
    public int countByKhoa(int maKhoa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Nganh WHERE ma_khoa = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKhoa);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Map ResultSet sang DTO
     */
    private NganhDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        NganhDTO nganh = new NganhDTO();
        nganh.setMaNganh(rs.getInt("ma_nganh"));
        nganh.setMaKhoa(rs.getInt("ma_khoa"));
        nganh.setTenNganh(rs.getString("ten_nganh"));
        return nganh;
    }
}
