/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: GiangVienDAO - Data Access Object cho bảng GiangVien
 */
package dao;

import config.DatabaseHelper;
import dto.GiangVienDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiangVienDAO {

    /**
     * Lấy tất cả giảng viên
     */
    public List<GiangVienDTO> getAll() throws SQLException {
        List<GiangVienDTO> danhSachGV = new ArrayList<>();
        String sql = "SELECT gv.*, k.ten_khoa, vt.ten_vai_tro FROM GiangVien gv " +
                "LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro " +
                "ORDER BY gv.ho, gv.ten";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSachGV.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachGV;
    }

    /**
     * Lấy tất cả giảng viên (không bao gồm trưởng khoa)
     */
    public List<GiangVienDTO> getAllGiangVien() throws SQLException {
        List<GiangVienDTO> danhSachGV = new ArrayList<>();
        String sql = "SELECT gv.*, k.ten_khoa, vt.ten_vai_tro FROM GiangVien gv " +
                "LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro " +
                "WHERE gv.ma_vai_tro = ? ORDER BY gv.ho, gv.ten";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, 2); // Vai trò giảng viên
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSachGV.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachGV;
    }

    /**
     * Lấy giảng viên theo khoa
     */
    public List<GiangVienDTO> getByKhoa(int maKhoa) throws SQLException {
        List<GiangVienDTO> danhSachGV = new ArrayList<>();
        String sql = "SELECT gv.*, k.ten_khoa, vt.ten_vai_tro FROM GiangVien gv " +
                "LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro " +
                "WHERE gv.ma_khoa = ? ORDER BY gv.ho, gv.ten";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKhoa);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSachGV.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachGV;
    }

    /**
     * Lấy giảng viên theo mã
     */
    public GiangVienDTO getById(int maGV) throws SQLException {
        String sql = "SELECT gv.*, k.ten_khoa, vt.ten_vai_tro FROM GiangVien gv " +
                "LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro " +
                "WHERE gv.ma_gv = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maGV);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }

    /**
     * Lấy giảng viên theo tên đăng nhập
     */
    public GiangVienDTO getByTenDangNhap(String tenDangNhap) throws SQLException {
        String sql = "SELECT gv.*, k.ten_khoa, vt.ten_vai_tro FROM GiangVien gv " +
                "LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro " +
                "WHERE gv.ten_dang_nhap = ?";

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
     * Thêm giảng viên mới
     */
    public boolean insert(GiangVienDTO giangVien) throws SQLException {
        String sql = "INSERT INTO GiangVien (ma_khoa, ma_vai_tro, ten_dang_nhap, mat_khau, " +
                "ho, ten, email, ngay_tao, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, giangVien.getMaKhoa());
            pstmt.setInt(2, giangVien.getMaVaiTro());
            pstmt.setString(3, giangVien.getTenDangNhap());
            pstmt.setString(4, giangVien.getMatKhau());
            pstmt.setString(5, giangVien.getHo());
            pstmt.setString(6, giangVien.getTen());
            pstmt.setString(7, giangVien.getEmail());
            pstmt.setBoolean(8, giangVien.isTrangThai());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    giangVien.setMaGV(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Cập nhật giảng viên
     */
    public boolean update(GiangVienDTO giangVien) throws SQLException {
        String sql = "UPDATE GiangVien SET ma_khoa = ?, ho = ?, ten = ?, " +
                "email = ?, trang_thai = ? WHERE ma_gv = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, giangVien.getMaKhoa());
            pstmt.setString(2, giangVien.getHo());
            pstmt.setString(3, giangVien.getTen());
            pstmt.setString(4, giangVien.getEmail());
            pstmt.setBoolean(5, giangVien.isTrangThai());
            pstmt.setInt(6, giangVien.getMaGV());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật mật khẩu
     */
    public boolean updatePassword(int maGV, String matKhauMoi) throws SQLException {
        String sql = "UPDATE GiangVien SET mat_khau = ? WHERE ma_gv = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matKhauMoi);
            pstmt.setInt(2, maGV);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Xóa giảng viên
     */
    public boolean delete(int maGV) throws SQLException {
        String sql = "DELETE FROM GiangVien WHERE ma_gv = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maGV);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại
     */
    public boolean checkTenDangNhapExists(String tenDangNhap) throws SQLException {
        String sql = "SELECT COUNT(*) FROM GiangVien WHERE ten_dang_nhap = ?";

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
     * Tìm kiếm giảng viên theo từ khóa
     * Tìm trong: mã gv, tên đăng nhập, họ, tên, email
     */
    public List<GiangVienDTO> search(String keyword) throws SQLException {
        List<GiangVienDTO> danhSachGV = new ArrayList<>();
        String sql = "SELECT gv.*, k.ten_khoa, vt.ten_vai_tro FROM GiangVien gv " +
                "LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro " +
                "WHERE gv.ma_gv LIKE ? OR gv.ten_dang_nhap LIKE ? OR gv.ho LIKE ? " +
                "OR gv.ten LIKE ? OR gv.email LIKE ? OR CONCAT(gv.ho, ' ', gv.ten) LIKE ? " +
                "ORDER BY gv.ho, gv.ten";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 6; i++) {
                pstmt.setString(i, searchPattern);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                danhSachGV.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachGV;
    }

    /**
     * Lấy mã giảng viên tiếp theo (mã duy nhất)
     */
    public int getNextMaGV() throws SQLException {
        String sql = "SELECT COALESCE(MAX(ma_gv), 0) + 1 AS next_ma FROM GiangVien";

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
    private GiangVienDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        GiangVienDTO gv = new GiangVienDTO();
        gv.setMaGV(rs.getInt("ma_gv"));
        gv.setMaKhoa(rs.getInt("ma_khoa"));
        gv.setMaVaiTro(rs.getInt("ma_vai_tro"));
        gv.setTenDangNhap(rs.getString("ten_dang_nhap"));
        gv.setMatKhau(rs.getString("mat_khau"));
        gv.setHo(rs.getString("ho"));
        gv.setTen(rs.getString("ten"));
        gv.setEmail(rs.getString("email"));
        gv.setNgayTao(rs.getTimestamp("ngay_tao"));
        gv.setTrangThai(rs.getBoolean("trang_thai"));
        gv.setTenKhoa(rs.getString("ten_khoa"));
        gv.setTenVaiTro(rs.getString("ten_vai_tro"));
        return gv;
    }
}
