/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: KhoaDAO - Data Access Object cho bảng Khoa
 */
package dao;

import config.DatabaseHelper;
import dto.KhoaDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhoaDAO {

    /**
     * Lấy tất cả khoa
     */
    public List<KhoaDTO> getAll() throws SQLException {
        List<KhoaDTO> danhSachKhoa = new ArrayList<>();
        String sql = "SELECT * FROM Khoa ORDER BY ten_khoa";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                KhoaDTO khoa = new KhoaDTO();
                khoa.setMaKhoa(rs.getInt("ma_khoa"));
                khoa.setTenKhoa(rs.getString("ten_khoa"));
                danhSachKhoa.add(khoa);
            }
        }
        return danhSachKhoa;
    }

    /**
     * Lấy khoa theo mã
     */
    public KhoaDTO getById(int maKhoa) throws SQLException {
        String sql = "SELECT * FROM Khoa WHERE ma_khoa = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKhoa);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                KhoaDTO khoa = new KhoaDTO();
                khoa.setMaKhoa(rs.getInt("ma_khoa"));
                khoa.setTenKhoa(rs.getString("ten_khoa"));
                return khoa;
            }
        }
        return null;
    }

    /**
     * Thêm khoa mới
     */
    public boolean insert(KhoaDTO khoa) throws SQLException {
        String sql = "INSERT INTO Khoa (ten_khoa) VALUES (?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, khoa.getTenKhoa());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    khoa.setMaKhoa(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Cập nhật khoa
     */
    public boolean update(KhoaDTO khoa) throws SQLException {
        String sql = "UPDATE Khoa SET ten_khoa = ? WHERE ma_khoa = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, khoa.getTenKhoa());
            pstmt.setInt(2, khoa.getMaKhoa());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa khoa
     */
    public boolean delete(int maKhoa) throws SQLException {
        String sql = "DELETE FROM Khoa WHERE ma_khoa = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKhoa);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Đếm số ngành thuộc khoa
     */
    public int countNganhByKhoa(int maKhoa) throws SQLException {
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
}
