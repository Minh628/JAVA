/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: ChiTietDeThiDAO - Data Access Object cho bảng ChiTietDeThi (N-N đề thi - câu hỏi)
 */
package dao;

import config.DatabaseHelper;
import dto.ChiTietDeThiDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDeThiDAO {

    /**
     * Lấy danh sách câu hỏi trong đề thi
     */
    public List<ChiTietDeThiDTO> getByDeThi(int maDeThi) throws SQLException {
        List<ChiTietDeThiDTO> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietDeThi WHERE ma_de_thi = ? ORDER BY IFNULL(thu_tu, ma_cau_hoi)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            ResultSet rs = pstmt.executeQuery();

            int thuTu = 1;
            while (rs.next()) {
                ChiTietDeThiDTO ct = new ChiTietDeThiDTO();
                ct.setMaDeThi(rs.getInt("ma_de_thi"));
                ct.setMaCauHoi(rs.getInt("ma_cau_hoi"));
                try {
                    ct.setThuTu(rs.getInt("thu_tu"));
                } catch (SQLException e) {
                    ct.setThuTu(thuTu++);
                }
                danhSach.add(ct);
            }
        }
        return danhSach;
    }

    /**
     * Thêm câu hỏi vào đề thi
     */
    public boolean insert(ChiTietDeThiDTO chiTiet) throws SQLException {
        String sql = "INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi, thu_tu) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, chiTiet.getMaDeThi());
            pstmt.setInt(2, chiTiet.getMaCauHoi());
            pstmt.setInt(3, chiTiet.getThuTu());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Thêm nhiều câu hỏi vào đề thi cùng lúc
     */
    public boolean insertBatch(List<ChiTietDeThiDTO> danhSach) throws SQLException {
        String sql = "INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi, thu_tu) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (ChiTietDeThiDTO ct : danhSach) {
                pstmt.setInt(1, ct.getMaDeThi());
                pstmt.setInt(2, ct.getMaCauHoi());
                pstmt.setInt(3, ct.getThuTu());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            conn.commit();

            for (int result : results) {
                if (result < 0) return false;
            }
            return true;
        }
    }

    /**
     * Xóa tất cả câu hỏi trong đề thi
     */
    public boolean deleteByDeThi(int maDeThi) throws SQLException {
        String sql = "DELETE FROM ChiTietDeThi WHERE ma_de_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            return pstmt.executeUpdate() >= 0;
        }
    }

    /**
     * Đếm số câu hỏi trong đề thi
     */
    public int countByDeThi(int maDeThi) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChiTietDeThi WHERE ma_de_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Lấy danh sách mã câu hỏi trong đề thi
     */
    public List<Integer> getMaCauHoiByDeThi(int maDeThi) throws SQLException {
        List<Integer> danhSach = new ArrayList<>();
        String sql = "SELECT ma_cau_hoi FROM ChiTietDeThi WHERE ma_de_thi = ? ORDER BY thu_tu";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(rs.getInt("ma_cau_hoi"));
            }
        }
        return danhSach;
    }

    /**
     * Lấy thứ tự lớn nhất trong đề thi
     */
    public int getMaxThuTu(int maDeThi) throws SQLException {
        String sql = "SELECT COALESCE(MAX(thu_tu), 0) FROM ChiTietDeThi WHERE ma_de_thi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Xóa một câu hỏi khỏi đề thi
     */
    public boolean delete(int maDeThi, int maCauHoi) throws SQLException {
        String sql = "DELETE FROM ChiTietDeThi WHERE ma_de_thi = ? AND ma_cau_hoi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDeThi);
            pstmt.setInt(2, maCauHoi);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Kiểm tra câu hỏi có nằm trong đề thi nào không
     */
    public boolean isCauHoiInAnyDeThi(int maCauHoi) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChiTietDeThi WHERE ma_cau_hoi = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maCauHoi);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

}
