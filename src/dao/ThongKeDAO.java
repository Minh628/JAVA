/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: ThongKeDAO - Data Access Object cho thống kê
 */
package dao;

import config.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDAO {
    
    /**
     * Thống kê tổng quan theo khoảng thời gian
     * @return Map với keys: tongBaiThi, tongDeThi, tyLeDat, diemTrungBinh
     */
    public Map<String, Object> getThongKeTongQuan(Date tuNgay, Date denNgay) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = """
            SELECT 
                COUNT(*) as tong_bai_thi,
                AVG(diem_so) as diem_tb,
                SUM(CASE WHEN diem_so >= 5 THEN 1 ELSE 0 END) as so_dat,
                SUM(CASE WHEN diem_so < 5 THEN 1 ELSE 0 END) as so_rot
            FROM BaiThi 
            WHERE ngay_thi BETWEEN ? AND ?
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int tongBaiThi = rs.getInt("tong_bai_thi");
                result.put("tongBaiThi", tongBaiThi);
                result.put("diemTrungBinh", rs.getFloat("diem_tb"));
                result.put("soDat", rs.getInt("so_dat"));
                result.put("soRot", rs.getInt("so_rot"));
                result.put("tyLeDat", tongBaiThi > 0 ? 
                    (rs.getInt("so_dat") * 100.0 / tongBaiThi) : 0);
            }
        }
        
        // Đếm số đề thi
        String sqlDeThi = """
            SELECT COUNT(DISTINCT dt.ma_de_thi) as tong_de_thi
            FROM DeThi dt
            INNER JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi
            WHERE bt.ngay_thi BETWEEN ? AND ?
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeThi)) {
            
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                result.put("tongDeThi", rs.getInt("tong_de_thi"));
            }
        }
        
        return result;
    }
    
    
    /**
     * Thống kê theo quý trong năm
     * @return List<Object[]> với mỗi phần tử: [quy, tongBaiThi, diemTB, tyLeDat]
     */
    public List<Object[]> getThongKeTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                QUARTER(ngay_thi) as quy,
                COUNT(*) as tong_bai_thi,
                AVG(diem_so) as diem_tb,
                SUM(CASE WHEN diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as ty_le_dat
            FROM BaiThi 
            WHERE YEAR(ngay_thi) = ?
            GROUP BY QUARTER(ngay_thi)
            ORDER BY quy
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("quy"),
                    rs.getInt("tong_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê theo khoa
     * @return List<Object[]> với mỗi phần tử: [maKhoa, tenKhoa, tongBaiThi, diemTB, tyLeDat]
     */
    public List<Object[]> getThongKeTheoKhoa(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                k.ma_khoa,
                k.ten_khoa,
                COUNT(bt.ma_bai_thi) as tong_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM Khoa k
            LEFT JOIN HocPhan hp ON k.ma_khoa = hp.ma_khoa
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY k.ma_khoa, k.ten_khoa
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY k.ten_khoa
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("ma_khoa"),
                    rs.getString("ten_khoa"),
                    rs.getInt("tong_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    

    /**
     * Thống kê theo ngành
     */
    public List<Object[]> getThongKeTheoNganh(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                n.ma_nganh,
                n.ten_nganh,
                k.ten_khoa,
                COUNT(bt.ma_bai_thi) as tong_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM Nganh n
            INNER JOIN Khoa k ON n.ma_khoa = k.ma_khoa
            LEFT JOIN SinhVien sv ON n.ma_nganh = sv.ma_nganh
            LEFT JOIN BaiThi bt ON sv.ma_sv = bt.ma_sv 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY n.ma_nganh, n.ten_nganh, k.ten_khoa
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY k.ten_khoa, n.ten_nganh
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("ma_nganh"),
                    rs.getString("ten_nganh"),
                    rs.getString("ten_khoa"),
                    rs.getInt("tong_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê theo học phần
     */
    public List<Object[]> getThongKeTheoHocPhan(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ma_hoc_phan,
                hp.ten_mon,
                k.ten_khoa,
                COUNT(bt.ma_bai_thi) as tong_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM HocPhan hp
            INNER JOIN Khoa k ON hp.ma_khoa = k.ma_khoa
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon, k.ten_khoa
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY k.ten_khoa, hp.ten_mon
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("ma_hoc_phan"),
                    rs.getString("ten_mon"),
                    rs.getString("ten_khoa"),
                    rs.getInt("tong_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê theo kỳ thi
     */
    public List<Object[]> getThongKeTheoKyThi(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                kt.ma_ky_thi,
                kt.ten_ky_thi,
                COUNT(bt.ma_bai_thi) as tong_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as ty_le_dat
            FROM KyThi kt
            LEFT JOIN DeThi dt ON kt.ma_ky_thi = dt.ma_ky_thi
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY kt.ma_ky_thi, kt.ten_ky_thi
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY kt.thoi_gian_bat_dau DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("ma_ky_thi"),
                    rs.getString("ten_ky_thi"),
                    rs.getInt("tong_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
  
    // ==================== THỐNG KÊ THEO QUÝ (MỤC 12.a) ====================
    
    /**
     * Thống kê Giảng viên theo Quý (số lượng bài thi)
     * Cấu trúc: Tên GV | Q1 | Q2 | Q3 | Q4 | TC
     */
    public List<Object[]> getThongKeGiangVienTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                CONCAT(gv.ho, ' ', gv.ten) as ho_ten,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 1 THEN 1 ELSE 0 END) as Q1,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 2 THEN 1 ELSE 0 END) as Q2,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 3 THEN 1 ELSE 0 END) as Q3,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 4 THEN 1 ELSE 0 END) as Q4,
                COUNT(bt.ma_bai_thi) as TC
            FROM GiangVien gv
            LEFT JOIN DeThi dt ON gv.ma_gv = dt.ma_gv
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi AND YEAR(bt.ngay_thi) = ?
            WHERE gv.ma_vai_tro = 2
            GROUP BY gv.ma_gv, gv.ho, gv.ten
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY TC DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ho_ten"),
                    rs.getInt("Q1"),
                    rs.getInt("Q2"),
                    rs.getInt("Q3"),
                    rs.getInt("Q4"),
                    rs.getInt("TC")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Sinh viên theo Quý (số lượng bài thi)
     * Cấu trúc: Tên SV | Q1 | Q2 | Q3 | Q4 | TC
     */
    public List<Object[]> getThongKeSinhVienTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                CONCAT(sv.ho, ' ', sv.ten) as ho_ten,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 1 THEN 1 ELSE 0 END) as Q1,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 2 THEN 1 ELSE 0 END) as Q2,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 3 THEN 1 ELSE 0 END) as Q3,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 4 THEN 1 ELSE 0 END) as Q4,
                COUNT(bt.ma_bai_thi) as TC
            FROM SinhVien sv
            LEFT JOIN BaiThi bt ON sv.ma_sv = bt.ma_sv AND YEAR(bt.ngay_thi) = ?
            GROUP BY sv.ma_sv, sv.ho, sv.ten
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY TC DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ho_ten"),
                    rs.getInt("Q1"),
                    rs.getInt("Q2"),
                    rs.getInt("Q3"),
                    rs.getInt("Q4"),
                    rs.getInt("TC")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Học phần theo Quý (số lượng bài thi)
     * Cấu trúc: Tên môn | Q1 | Q2 | Q3 | Q4 | TC
     */
    public List<Object[]> getThongKeHocPhanTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 1 THEN 1 ELSE 0 END) as Q1,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 2 THEN 1 ELSE 0 END) as Q2,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 3 THEN 1 ELSE 0 END) as Q3,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 4 THEN 1 ELSE 0 END) as Q4,
                COUNT(bt.ma_bai_thi) as TC
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi AND YEAR(bt.ngay_thi) = ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY TC DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getInt("Q1"),
                    rs.getInt("Q2"),
                    rs.getInt("Q3"),
                    rs.getInt("Q4"),
                    rs.getInt("TC")
                });
            }
        }
        return result;
    }
    
    // ==================== THỐNG KÊ NHIỀU KHÓA (MỤC 12.b.ii và 12.b.iii) ====================
    
    /**
     * Thống kê Sinh viên và Học phần (12.b.ii - Khách hàng và Sản phẩm)
     * Cấu trúc: Mã SV | Họ tên SV | Tên Môn | Số bài thi | Điểm TB
     */
    public List<Object[]> getThongKeSinhVienVaHocPhan(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                sv.ten_dang_nhap,
                CONCAT(sv.ho, ' ', sv.ten) as ho_ten,
                hp.ten_mon,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                AVG(bt.diem_so) as diem_tb
            FROM SinhVien sv
            JOIN BaiThi bt ON sv.ma_sv = bt.ma_sv
            JOIN DeThi dt ON bt.ma_de_thi = dt.ma_de_thi
            JOIN HocPhan hp ON dt.ma_hoc_phan = hp.ma_hoc_phan
            WHERE bt.ngay_thi BETWEEN ? AND ?
            GROUP BY sv.ma_sv, sv.ten_dang_nhap, sv.ho, sv.ten, hp.ma_hoc_phan, hp.ten_mon
            ORDER BY sv.ten, hp.ten_mon
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_dang_nhap"),
                    rs.getString("ho_ten"),
                    rs.getString("ten_mon"),
                    rs.getInt("so_bai_thi"),
                    rs.getFloat("diem_tb")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Giảng viên và Học phần theo Năm (12.b.iii - Nhân viên và Sản phẩm và Năm)
     * Cấu trúc: Họ tên GV | Tên Môn | Năm | Số đề thi | Số bài thi | Điểm TB
     */
    public List<Object[]> getThongKeGiangVienVaHocPhanTheoNam(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                CONCAT(gv.ho, ' ', gv.ten) as ho_ten_gv,
                hp.ten_mon,
                ? as nam,
                COUNT(DISTINCT dt.ma_de_thi) as so_de_thi,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                AVG(bt.diem_so) as diem_tb
            FROM GiangVien gv
            JOIN DeThi dt ON gv.ma_gv = dt.ma_gv
            JOIN HocPhan hp ON dt.ma_hoc_phan = hp.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi AND YEAR(bt.ngay_thi) = ?
            WHERE gv.ma_vai_tro = 2
            GROUP BY gv.ma_gv, gv.ho, gv.ten, hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY gv.ten, hp.ten_mon
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nam);
            pstmt.setInt(2, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ho_ten_gv"),
                    rs.getString("ten_mon"),
                    rs.getInt("nam"),
                    rs.getInt("so_de_thi"),
                    rs.getInt("so_bai_thi"),
                    rs.getFloat("diem_tb")
                });
            }
        }
        return result;
    }
    
    // ==================== THỐNG KÊ ĐỀ THI, BÀI THI, TỈ LỆ ĐẠT THEO QUÝ (MỤC 12.a) ====================
    
    /**
     * Thống kê Đề thi theo Quý (số lượng đề thi được sử dụng)
     * Cấu trúc: Học phần | Q1 | Q2 | Q3 | Q4 | TC
     */
    public List<Object[]> getThongKeDeThiTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                COUNT(DISTINCT CASE WHEN QUARTER(bt.ngay_thi) = 1 THEN dt.ma_de_thi END) as Q1,
                COUNT(DISTINCT CASE WHEN QUARTER(bt.ngay_thi) = 2 THEN dt.ma_de_thi END) as Q2,
                COUNT(DISTINCT CASE WHEN QUARTER(bt.ngay_thi) = 3 THEN dt.ma_de_thi END) as Q3,
                COUNT(DISTINCT CASE WHEN QUARTER(bt.ngay_thi) = 4 THEN dt.ma_de_thi END) as Q4,
                COUNT(DISTINCT dt.ma_de_thi) as TC
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi AND YEAR(bt.ngay_thi) = ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(DISTINCT dt.ma_de_thi) > 0
            ORDER BY TC DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getInt("Q1"),
                    rs.getInt("Q2"),
                    rs.getInt("Q3"),
                    rs.getInt("Q4"),
                    rs.getInt("TC")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Đề thi theo khoảng thời gian
     */
    public List<Object[]> getThongKeDeThiTheoThoiGian(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                COUNT(DISTINCT dt.ma_de_thi) as so_de_thi,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                AVG(bt.diem_so) as diem_tb
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(DISTINCT dt.ma_de_thi) > 0
            ORDER BY so_de_thi DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getInt("so_de_thi"),
                    rs.getInt("so_bai_thi"),
                    rs.getFloat("diem_tb")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Bài thi theo Quý
     * Cấu trúc: Học phần | Q1 | Q2 | Q3 | Q4 | TC
     */
    public List<Object[]> getThongKeBaiThiTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 1 THEN 1 ELSE 0 END) as Q1,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 2 THEN 1 ELSE 0 END) as Q2,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 3 THEN 1 ELSE 0 END) as Q3,
                SUM(CASE WHEN QUARTER(bt.ngay_thi) = 4 THEN 1 ELSE 0 END) as Q4,
                COUNT(bt.ma_bai_thi) as TC
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi AND YEAR(bt.ngay_thi) = ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY TC DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getInt("Q1"),
                    rs.getInt("Q2"),
                    rs.getInt("Q3"),
                    rs.getInt("Q4"),
                    rs.getInt("TC")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Bài thi theo khoảng thời gian
     */
    public List<Object[]> getThongKeBaiThiTheoThoiGian(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY so_bai_thi DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getInt("so_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Tỉ lệ đạt theo Quý
     * Cấu trúc: Học phần | Q1 (%) | Q2 (%) | Q3 (%) | Q4 (%) | TB (%)
     */
    public List<Object[]> getThongKeTyLeDatTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                COALESCE(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 1 AND bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / 
                    NULLIF(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 1 THEN 1 ELSE 0 END), 0), 0) as Q1,
                COALESCE(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 2 AND bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / 
                    NULLIF(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 2 THEN 1 ELSE 0 END), 0), 0) as Q2,
                COALESCE(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 3 AND bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / 
                    NULLIF(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 3 THEN 1 ELSE 0 END), 0), 0) as Q3,
                COALESCE(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 4 AND bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / 
                    NULLIF(SUM(CASE WHEN QUARTER(bt.ngay_thi) = 4 THEN 1 ELSE 0 END), 0), 0) as Q4,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as TB
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi AND YEAR(bt.ngay_thi) = ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY TB DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getFloat("Q1"),
                    rs.getFloat("Q2"),
                    rs.getFloat("Q3"),
                    rs.getFloat("Q4"),
                    rs.getFloat("TB")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Tỉ lệ đạt theo khoảng thời gian
     */
    public List<Object[]> getThongKeTyLeDatTheoThoiGian(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) as so_dat,
                SUM(CASE WHEN bt.diem_so < 5 THEN 1 ELSE 0 END) as so_rot,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY ty_le_dat DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getInt("so_bai_thi"),
                    rs.getInt("so_dat"),
                    rs.getInt("so_rot"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
    // ==================== THỐNG KÊ THEO KHOẢNG THỜI GIAN (CHO MỤC 12.a) ====================
    
    /**
     * Thống kê Giảng viên theo khoảng thời gian
     */
    public List<Object[]> getThongKeGiangVienTheoThoiGian(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                CONCAT(gv.ho, ' ', gv.ten) as ho_ten,
                COUNT(DISTINCT dt.ma_de_thi) as so_de_thi,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM GiangVien gv
            LEFT JOIN DeThi dt ON gv.ma_gv = dt.ma_gv
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            WHERE gv.ma_vai_tro = 2
            GROUP BY gv.ma_gv, gv.ho, gv.ten
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY so_bai_thi DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ho_ten"),
                    rs.getInt("so_de_thi"),
                    rs.getInt("so_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Sinh viên theo khoảng thời gian
     */
    public List<Object[]> getThongKeSinhVienTheoThoiGian(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                CONCAT(sv.ho, ' ', sv.ten) as ho_ten,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM SinhVien sv
            LEFT JOIN BaiThi bt ON sv.ma_sv = bt.ma_sv 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY sv.ma_sv, sv.ho, sv.ten
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY so_bai_thi DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ho_ten"),
                    rs.getInt("so_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
    /**
     * Thống kê Học phần theo khoảng thời gian
     */
    public List<Object[]> getThongKeHocPhanTheoThoiGian(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                hp.ten_mon,
                COUNT(bt.ma_bai_thi) as so_bai_thi,
                AVG(bt.diem_so) as diem_tb,
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(bt.ma_bai_thi) as ty_le_dat
            FROM HocPhan hp
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            GROUP BY hp.ma_hoc_phan, hp.ten_mon
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY so_bai_thi DESC
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_mon"),
                    rs.getInt("so_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
            }
        }
        return result;
    }
    
}
