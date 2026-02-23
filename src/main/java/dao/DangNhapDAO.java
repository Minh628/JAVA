/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: DangNhapDAO - Data Access Object cho xác thực đăng nhập
 */
package dao;

import config.DatabaseHelper;
import dto.GiangVienDTO;
import dto.SinhVienDTO;
import java.sql.*;

public class DangNhapDAO {

    /**
     * Lấy giảng viên theo tên đăng nhập (cho login)
     */
    public GiangVienDTO getGiangVienByTenDangNhap(String tenDangNhap) throws SQLException {
        String sql = "SELECT gv.*, k.ten_khoa, vt.ten_vai_tro FROM GiangVien gv " +
                "LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro " +
                "WHERE gv.ten_dang_nhap = ? AND gv.trang_thai = 1";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenDangNhap);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapGiangVien(rs);
            }
        }
        return null;
    }

    /**
     * Lấy sinh viên theo tên đăng nhập (cho login)
     */
    public SinhVienDTO getSinhVienByTenDangNhap(String tenDangNhap) throws SQLException {
        String sql = "SELECT sv.*, n.ten_nganh, n.ma_khoa, k.ten_khoa, vt.ten_vai_tro FROM SinhVien sv " +
                "LEFT JOIN Nganh n ON sv.ma_nganh = n.ma_nganh " +
                "LEFT JOIN Khoa k ON n.ma_khoa = k.ma_khoa " +
                "LEFT JOIN VaiTro vt ON sv.ma_vai_tro = vt.ma_vai_tro " +
                "WHERE sv.ten_dang_nhap = ? AND sv.trang_thai = 1";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenDangNhap);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapSinhVien(rs);
            }
        }
        return null;
    }

    /**
     * Cập nhật mật khẩu giảng viên
     */
    public boolean updatePasswordGV(int maGV, String matKhauMoi) throws SQLException {
        String sql = "UPDATE GiangVien SET mat_khau = ? WHERE ma_gv = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matKhauMoi);
            pstmt.setInt(2, maGV);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật mật khẩu sinh viên
     */
    public boolean updatePasswordSV(int maSV, String matKhauMoi) throws SQLException {
        String sql = "UPDATE SinhVien SET mat_khau = ? WHERE ma_sv = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matKhauMoi);
            pstmt.setInt(2, maSV);
            return pstmt.executeUpdate() > 0;
        }
    }

    private GiangVienDTO mapGiangVien(ResultSet rs) throws SQLException {
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
        return gv;
    }

    private SinhVienDTO mapSinhVien(ResultSet rs) throws SQLException {
        SinhVienDTO sv = new SinhVienDTO();
        sv.setMaSV(rs.getInt("ma_sv"));
        sv.setMaNganh(rs.getInt("ma_nganh"));
        sv.setMaVaiTro(rs.getInt("ma_vai_tro"));
        sv.setTenDangNhap(rs.getString("ten_dang_nhap"));
        sv.setMatKhau(rs.getString("mat_khau"));
        sv.setHo(rs.getString("ho"));
        sv.setTen(rs.getString("ten"));
        sv.setEmail(rs.getString("email"));
        sv.setNgayTao(rs.getTimestamp("ngay_tao"));
        sv.setTrangThai(rs.getBoolean("trang_thai"));
        return sv;
    }
}
