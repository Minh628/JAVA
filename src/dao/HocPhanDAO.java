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
                HocPhanDTO hp = new HocPhanDTO();
                hp.setMaHocPhan(rs.getInt("ma_hoc_phan"));
                hp.setTenMon(rs.getString("ten_mon"));
                hp.setSoTin(rs.getInt("so_tin"));
                danhSachHP.add(hp);
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
                HocPhanDTO hp = new HocPhanDTO();
                hp.setMaHocPhan(rs.getInt("ma_hoc_phan"));
                hp.setTenMon(rs.getString("ten_mon"));
                hp.setSoTin(rs.getInt("so_tin"));
                return hp;
            }
        }
        return null;
    }

    /**
     * Thêm học phần mới
     */
    public boolean insert(HocPhanDTO hocPhan) throws SQLException {
        String sql = "INSERT INTO HocPhan (ten_mon, so_tin) VALUES (?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, hocPhan.getTenMon());
            pstmt.setInt(2, hocPhan.getSoTin());

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
        String sql = "UPDATE HocPhan SET ten_mon = ?, so_tin = ? WHERE ma_hoc_phan = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hocPhan.getTenMon());
            pstmt.setInt(2, hocPhan.getSoTin());
            pstmt.setInt(3, hocPhan.getMaHocPhan());

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

    /**
     * Tìm kiếm học phần theo từ khóa
     * Tìm trong: mã học phần, tên môn
     */
    public List<HocPhanDTO> search(String keyword) throws SQLException {
        List<HocPhanDTO> danhSachHP = new ArrayList<>();
        String sql = "SELECT * FROM HocPhan WHERE ma_hoc_phan LIKE ? OR ten_mon LIKE ? ORDER BY ten_mon";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                HocPhanDTO hp = new HocPhanDTO();
                hp.setMaHocPhan(rs.getInt("ma_hoc_phan"));
                hp.setTenMon(rs.getString("ten_mon"));
                hp.setSoTin(rs.getInt("so_tin"));
                danhSachHP.add(hp);
            }
        }
        return danhSachHP;
    }

    /**
     * Lấy mã học phần tiếp theo (mã duy nhất)
     */
    public int getNextMaHocPhan() throws SQLException {
        String sql = "SELECT COALESCE(MAX(ma_hoc_phan), 0) + 1 AS next_ma FROM HocPhan";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("next_ma");
            }
        }
        return 1;
    }
}
