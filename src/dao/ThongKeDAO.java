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
     * Thống kê theo tháng trong năm
     * @return List<Object[]> với mỗi phần tử: [thang, tongBaiThi, diemTB, tyLeDat]
     */
    public List<Object[]> getThongKeTheoThang(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                MONTH(ngay_thi) as thang,
                COUNT(*) as tong_bai_thi,
                AVG(diem_so) as diem_tb,
                SUM(CASE WHEN diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as ty_le_dat
            FROM BaiThi 
            WHERE YEAR(ngay_thi) = ?
            GROUP BY MONTH(ngay_thi)
            ORDER BY thang
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("thang"),
                    rs.getInt("tong_bai_thi"),
                    rs.getFloat("diem_tb"),
                    rs.getFloat("ty_le_dat")
                });
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
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as ty_le_dat
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
     * Thống kê theo khoa và quý (cross-tabulation)
     * @return List<Object[]> với mỗi phần tử: [tenKhoa, q1, q2, q3, q4, tb]
     */
    public List<Object[]> getThongKeKhoaTheoQuy(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                k.ten_khoa,
                AVG(CASE WHEN QUARTER(bt.ngay_thi) = 1 THEN bt.diem_so END) as q1,
                AVG(CASE WHEN QUARTER(bt.ngay_thi) = 2 THEN bt.diem_so END) as q2,
                AVG(CASE WHEN QUARTER(bt.ngay_thi) = 3 THEN bt.diem_so END) as q3,
                AVG(CASE WHEN QUARTER(bt.ngay_thi) = 4 THEN bt.diem_so END) as q4,
                AVG(bt.diem_so) as tb
            FROM Khoa k
            LEFT JOIN HocPhan hp ON k.ma_khoa = hp.ma_khoa
            LEFT JOIN DeThi dt ON hp.ma_hoc_phan = dt.ma_hoc_phan
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND YEAR(bt.ngay_thi) = ?
            GROUP BY k.ma_khoa, k.ten_khoa
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY k.ten_khoa
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nam);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getString("ten_khoa"),
                    rs.getObject("q1"),
                    rs.getObject("q2"),
                    rs.getObject("q3"),
                    rs.getObject("q4"),
                    rs.getFloat("tb")
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
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as ty_le_dat
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
                SUM(CASE WHEN bt.diem_so >= 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as ty_le_dat
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
     * Thống kê theo giảng viên (số đề tạo, điểm TB đề của GV)
     */
    public List<Object[]> getThongKeTheoGiangVien(Date tuNgay, Date denNgay) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        
        String sql = """
            SELECT 
                gv.ma_gv,
                CONCAT(gv.ho, ' ', gv.ten) as ho_ten,
                k.ten_khoa,
                COUNT(DISTINCT dt.ma_de_thi) as so_de_thi,
                COUNT(bt.ma_bai_thi) as tong_bai_thi,
                AVG(bt.diem_so) as diem_tb
            FROM GiangVien gv
            LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa
            LEFT JOIN DeThi dt ON gv.ma_gv = dt.ma_gv
            LEFT JOIN BaiThi bt ON dt.ma_de_thi = bt.ma_de_thi 
                AND bt.ngay_thi BETWEEN ? AND ?
            WHERE gv.ma_vai_tro = 2
            GROUP BY gv.ma_gv, gv.ho, gv.ten, k.ten_khoa
            HAVING COUNT(bt.ma_bai_thi) > 0
            ORDER BY k.ten_khoa, gv.ten
        """;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, tuNgay);
            pstmt.setDate(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                result.add(new Object[]{
                    rs.getInt("ma_gv"),
                    rs.getString("ho_ten"),
                    rs.getString("ten_khoa"),
                    rs.getInt("so_de_thi"),
                    rs.getInt("tong_bai_thi"),
                    rs.getFloat("diem_tb")
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
    
    /**
     * Lấy danh sách các năm có dữ liệu
     */
    public List<Integer> getDanhSachNam() throws SQLException {
        List<Integer> result = new ArrayList<>();
        
        String sql = "SELECT DISTINCT YEAR(ngay_thi) as nam FROM BaiThi WHERE ngay_thi IS NOT NULL ORDER BY nam DESC";
        
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                result.add(rs.getInt("nam"));
            }
        }
        
        // Nếu không có dữ liệu, thêm năm hiện tại
        if (result.isEmpty()) {
            result.add(java.time.Year.now().getValue());
        }
        
        return result;
    }
}
