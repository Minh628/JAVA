/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: DeThiDAO - Data Access Object cho bảng DeThi
 */
package dao;

import config.DatabaseHelper;
import dto.DeThiDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeThiDAO {

    /**
     * Lấy tất cả đề thi
     */
    public List<DeThiDTO> getAll() throws SQLException {
        List<DeThiDTO> danhSachDT = new ArrayList<>();
        String sql = "SELECT * FROM DeThi ORDER BY ngay_tao DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSachDT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachDT;
    }

    /**
     * Lấy đề thi theo kỳ thi
     */
    public List<DeThiDTO> getByKyThi(int maKyThi) throws SQLException {
        List<DeThiDTO> danhSachDT = new ArrayList<>();
        String sql = "SELECT * FROM DeThi WHERE ma_ky_thi = ? ORDER BY ten_de_thi";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKyThi);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSachDT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachDT;
    }

    /**
     * Lấy đề thi theo kỳ thi VÀ khoa (sinh viên chỉ thấy đề thi của khoa mình)
     */
    public List<DeThiDTO> getByKyThiAndKhoa(int maKyThi, int maKhoa) throws SQLException {
        List<DeThiDTO> danhSachDT = new ArrayList<>();
        String sql = "SELECT dt.* FROM DeThi dt " +
                "INNER JOIN HocPhan hp ON dt.ma_hoc_phan = hp.ma_hoc_phan " +
                "WHERE dt.ma_ky_thi = ? AND hp.ma_khoa = ? ORDER BY dt.ten_de_thi";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKyThi);
            pstmt.setInt(2, maKhoa);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSachDT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachDT;
    }

    /**
     * Lấy đề thi theo giảng viên
     */
    public List<DeThiDTO> getByGiangVien(int maGV) throws SQLException {
        List<DeThiDTO> danhSachDT = new ArrayList<>();
        String sql = "SELECT * FROM DeThi WHERE ma_gv = ? ORDER BY ngay_tao DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maGV);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSachDT.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachDT;
    }

    /**
     * Lấy đề thi theo mã
     */
    public DeThiDTO getById(int maDeThi) throws SQLException {
        String sql = "SELECT * FROM DeThi WHERE ma_de_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }

    /**
     * Thêm đề thi mới
     */
    public boolean insert(DeThiDTO deThi) throws SQLException {
        String sql = "INSERT INTO DeThi (ma_hoc_phan, ma_ky_thi, ma_gv, ten_de_thi, thoi_gian_lam, ngay_tao, so_cau_hoi) "
                +
                "VALUES (?, ?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, deThi.getMaHocPhan());
            pstmt.setInt(2, deThi.getMaKyThi());
            pstmt.setInt(3, deThi.getMaGV());
            pstmt.setString(4, deThi.getTenDeThi());
            pstmt.setInt(5, deThi.getThoiGianLam());
            pstmt.setInt(6, deThi.getSoCauHoi());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    deThi.setMaDeThi(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Cập nhật đề thi
     */
    public boolean update(DeThiDTO deThi) throws SQLException {
        String sql = "UPDATE DeThi SET ma_hoc_phan = ?, ma_ky_thi = ?, ten_de_thi = ?, " +
                "thoi_gian_lam = ?, so_cau_hoi = ? WHERE ma_de_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deThi.getMaHocPhan());
            pstmt.setInt(2, deThi.getMaKyThi());
            pstmt.setString(3, deThi.getTenDeThi());
            pstmt.setInt(4, deThi.getThoiGianLam());
            pstmt.setInt(5, deThi.getSoCauHoi());
            pstmt.setInt(6, deThi.getMaDeThi());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa đề thi
     */
    public boolean delete(int maDeThi) throws SQLException {
        String sql = "DELETE FROM DeThi WHERE ma_de_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Thêm câu hỏi vào đề thi
     */
    public boolean themCauHoiVaoDeThi(int maDeThi, int maCauHoi) throws SQLException {
        String sql = "INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES (?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            pstmt.setInt(2, maCauHoi);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Thêm nhiều câu hỏi vào đề thi (batch insert)
     */
    public boolean themNhieuCauHoiVaoDeThi(int maDeThi, List<Integer> danhSachMaCauHoi) throws SQLException {
        String sql = "INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES (?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            try {
                for (int maCauHoi : danhSachMaCauHoi) {
                    pstmt.setInt(1, maDeThi);
                    pstmt.setInt(2, maCauHoi);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Xóa câu hỏi khỏi đề thi
     */
    public boolean xoaCauHoiKhoiDeThi(int maDeThi, int maCauHoi) throws SQLException {
        String sql = "DELETE FROM ChiTietDeThi WHERE ma_de_thi = ? AND ma_cau_hoi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            pstmt.setInt(2, maCauHoi);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Map ResultSet sang DTO
     */
    private DeThiDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        DeThiDTO dt = new DeThiDTO();
        dt.setMaDeThi(rs.getInt("ma_de_thi"));
        dt.setMaHocPhan(rs.getInt("ma_hoc_phan"));
        dt.setMaKyThi(rs.getInt("ma_ky_thi"));
        dt.setMaGV(rs.getInt("ma_gv"));
        dt.setTenDeThi(rs.getString("ten_de_thi"));
        dt.setThoiGianLam(rs.getInt("thoi_gian_lam"));
        dt.setNgayTao(rs.getTimestamp("ngay_tao"));
        dt.setSoCauHoi(rs.getInt("so_cau_hoi"));
        return dt;
    }
}
