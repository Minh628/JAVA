/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: HocPhanDAO - Data Access Object cho bảng HocPhan
 */
package dao;

import config.DatabaseHelper;
import dto.HocPhanDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HocPhanDAO {

    /**
     * Lấy tất cả học phần
     */
    public List<HocPhanDTO> getAll() throws SQLException {
        List<HocPhanDTO> danhSachHP = new ArrayList<>();
        String sql = "SELECT * FROM HocPhan ORDER BY ten_mon";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSachHP.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachHP;
    }

    /**
     * Lấy học phần theo mã
     */
    public HocPhanDTO getById(int maHocPhan) throws SQLException {
        String sql = "SELECT * FROM HocPhan WHERE ma_hoc_phan = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maHocPhan);
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
    private HocPhanDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        HocPhanDTO hp = new HocPhanDTO();
        hp.setMaHocPhan(rs.getInt("ma_hoc_phan"));
        hp.setMaKhoa(rs.getInt("ma_khoa"));
        hp.setTenMon(rs.getString("ten_mon"));
        hp.setSoTin(rs.getInt("so_tin"));
        return hp;
    }

    /**
     * Thêm học phần mới
     */
    public boolean insert(HocPhanDTO hocPhan) throws SQLException {
        String sql = "INSERT INTO HocPhan (ma_khoa, ten_mon, so_tin) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, hocPhan.getMaKhoa());
            pstmt.setString(2, hocPhan.getTenMon());
            pstmt.setInt(3, hocPhan.getSoTin());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    hocPhan.setMaHocPhan(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Cập nhật học phần
     */
    public boolean update(HocPhanDTO hocPhan) throws SQLException {
        String sql = "UPDATE HocPhan SET ma_khoa = ?, ten_mon = ?, so_tin = ? WHERE ma_hoc_phan = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, hocPhan.getMaKhoa());
            pstmt.setString(2, hocPhan.getTenMon());
            pstmt.setInt(3, hocPhan.getSoTin());
            pstmt.setInt(4, hocPhan.getMaHocPhan());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa học phần
     */
    public boolean delete(int maHocPhan) throws SQLException {
        String sql = "DELETE FROM HocPhan WHERE ma_hoc_phan = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maHocPhan);
            return pstmt.executeUpdate() > 0;
        }
    }

}
