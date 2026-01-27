/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: KyThiDAO - Data Access Object cho bảng KyThi
 */
package dao;

import config.DatabaseHelper;
import dto.KyThiDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KyThiDAO {

    /**
     * Lấy tất cả kỳ thi
     */
    public List<KyThiDTO> getAll() throws SQLException {
        List<KyThiDTO> danhSachKT = new ArrayList<>();
        String sql = "SELECT * FROM KyThi ORDER BY thoi_gian_bat_dau DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSachKT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachKT;
    }

    /**
     * Lấy kỳ thi đang diễn ra
     */
    public List<KyThiDTO> getKyThiDangDienRa() throws SQLException {
        List<KyThiDTO> danhSachKT = new ArrayList<>();
        String sql = "SELECT * FROM KyThi WHERE thoi_gian_bat_dau <= NOW() AND thoi_gian_ket_thuc >= NOW()";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSachKT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachKT;
    }

    /**
     * Lấy kỳ thi theo mã
     */
    public KyThiDTO getById(int maKyThi) throws SQLException {
        String sql = "SELECT * FROM KyThi WHERE ma_ky_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKyThi);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }

    /**
     * Thêm kỳ thi mới
     */
    public boolean insert(KyThiDTO kyThi) throws SQLException {
        String sql = "INSERT INTO KyThi (ten_ky_thi, thoi_gian_bat_dau, thoi_gian_ket_thuc) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, kyThi.getTenKyThi());
            pstmt.setTimestamp(2, kyThi.getThoiGianBatDau());
            pstmt.setTimestamp(3, kyThi.getThoiGianKetThuc());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    kyThi.setMaKyThi(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Cập nhật kỳ thi
     */
    public boolean update(KyThiDTO kyThi) throws SQLException {
        String sql = "UPDATE KyThi SET ten_ky_thi = ?, thoi_gian_bat_dau = ?, thoi_gian_ket_thuc = ? WHERE ma_ky_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kyThi.getTenKyThi());
            pstmt.setTimestamp(2, kyThi.getThoiGianBatDau());
            pstmt.setTimestamp(3, kyThi.getThoiGianKetThuc());
            pstmt.setInt(4, kyThi.getMaKyThi());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa kỳ thi
     */
    public boolean delete(int maKyThi) throws SQLException {
        String sql = "DELETE FROM KyThi WHERE ma_ky_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKyThi);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Tìm kiếm kỳ thi theo từ khóa
     * Tìm trong: mã kỳ thi, tên kỳ thi
     */
    public List<KyThiDTO> search(String keyword) throws SQLException {
        List<KyThiDTO> danhSachKT = new ArrayList<>();
        String sql = "SELECT * FROM KyThi WHERE ma_ky_thi LIKE ? OR ten_ky_thi LIKE ? ORDER BY thoi_gian_bat_dau DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSachKT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachKT;
    }

    /**
     * Lấy mã kỳ thi tiếp theo (mã duy nhất)
     */
    public int getNextMaKyThi() throws SQLException {
        String sql = "SELECT COALESCE(MAX(ma_ky_thi), 0) + 1 AS next_ma FROM KyThi";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("next_ma");
            }
        }
        return 1;
    }

    /**
     * Map ResultSet sang DTO
     */
    private KyThiDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        KyThiDTO kt = new KyThiDTO();
        kt.setMaKyThi(rs.getInt("ma_ky_thi"));
        kt.setTenKyThi(rs.getString("ten_ky_thi"));
        kt.setThoiGianBatDau(rs.getTimestamp("thoi_gian_bat_dau"));
        kt.setThoiGianKetThuc(rs.getTimestamp("thoi_gian_ket_thuc"));
        return kt;
    }
}
