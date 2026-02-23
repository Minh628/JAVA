/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: ThongKeBUS - Xử lý logic nghiệp vụ Thống kê
 */
package bus;

import dao.ThongKeDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class ThongKeBUS {
    private ThongKeDAO thongKeDAO;
    
    public ThongKeBUS() {
        this.thongKeDAO = new ThongKeDAO();
    }
    
    // ==================== Enum cho loại thời gian ====================
    public enum LoaiThoiGian {
        KHOANG_NGAY,  // Từ ngày - đến ngày
        THANG,        // Chọn 1 tháng
        QUY,          // Chọn 1 quý
        NAM           // Chọn 1 năm (hiển thị 4 quý)
    }
    
    // ==================== Chuyển đổi thời gian ====================
    
    /**
     * Tính ngày đầu và cuối của tháng
     * @param thang 1-12
     * @param nam năm
     * @return Date[] {tuNgay, denNgay}
     */
    public Date[] getNgayTheoThang(int thang, int nam) {
        YearMonth ym = YearMonth.of(nam, thang);
        LocalDate ngayDau = ym.atDay(1);
        LocalDate ngayCuoi = ym.atEndOfMonth();
        return new Date[]{
            Date.valueOf(ngayDau),
            Date.valueOf(ngayCuoi)
        };
    }
    
    /**
     * Tính ngày đầu và cuối của quý
     * @param quy 1-4
     * @param nam năm
     * @return Date[] {tuNgay, denNgay}
     */
    public Date[] getNgayTheoQuy(int quy, int nam) {
        int thangDau = (quy - 1) * 3 + 1;
        int thangCuoi = quy * 3;
        
        LocalDate ngayDau = LocalDate.of(nam, thangDau, 1);
        YearMonth ymCuoi = YearMonth.of(nam, thangCuoi);
        LocalDate ngayCuoi = ymCuoi.atEndOfMonth();
        
        return new Date[]{
            Date.valueOf(ngayDau),
            Date.valueOf(ngayCuoi)
        };
    }
    
    /**
     * Tính ngày đầu và cuối của năm
     */
    public Date[] getNgayTheoNam(int nam) {
        return new Date[]{
            Date.valueOf(LocalDate.of(nam, 1, 1)),
            Date.valueOf(LocalDate.of(nam, 12, 31))
        };
    }
    
    

    // ==================== Gọi DAO ====================
    
    /**
     * Thống kê tổng quan
     */
    public Map<String, Object> getThongKeTongQuan(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeTongQuan(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * Thống kê theo quý
     */
    public List<Object[]> getThongKeTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeTheoQuy(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê theo khoa
     */
    public List<Object[]> getThongKeTheoKhoa(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeTheoKhoa(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    /**
     * Thống kê theo ngành
     */
    public List<Object[]> getThongKeTheoNganh(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeTheoNganh(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê theo học phần
     */
    public List<Object[]> getThongKeTheoHocPhan(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeTheoHocPhan(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê theo kỳ thi
     */
    public List<Object[]> getThongKeTheoKyThi(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeTheoKyThi(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    // ==================== THỐNG KÊ THEO QUÝ (MỤC 12.a) ====================
    
    /**
     * Thống kê Giảng viên theo Quý (cross-tabulation số lượng bài thi)
     */
    public List<Object[]> getThongKeGiangVienTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeGiangVienTheoQuy(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Sinh viên theo Quý (cross-tabulation số lượng bài thi)
     */
    public List<Object[]> getThongKeSinhVienTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeSinhVienTheoQuy(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Học phần theo Quý (cross-tabulation số lượng bài thi)
     */
    public List<Object[]> getThongKeHocPhanTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeHocPhanTheoQuy(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== THỐNG KÊ ĐỀ THI, BÀI THI, TỈ LỆ ĐẠT THEO QUÝ (MỤC 12.a) ====================
    
    /**
     * Thống kê Đề thi theo Quý
     */
    public List<Object[]> getThongKeDeThiTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeDeThiTheoQuy(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Đề thi theo khoảng thời gian
     */
    public List<Object[]> getThongKeDeThiTheoThoiGian(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeDeThiTheoThoiGian(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Bài thi theo Quý
     */
    public List<Object[]> getThongKeBaiThiTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeBaiThiTheoQuy(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Bài thi theo khoảng thời gian
     */
    public List<Object[]> getThongKeBaiThiTheoThoiGian(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeBaiThiTheoThoiGian(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Tỉ lệ đạt theo Quý
     */
    public List<Object[]> getThongKeTyLeDatTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeTyLeDatTheoQuy(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Tỉ lệ đạt theo khoảng thời gian
     */
    public List<Object[]> getThongKeTyLeDatTheoThoiGian(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeTyLeDatTheoThoiGian(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Giảng viên theo khoảng thời gian
     */
    public List<Object[]> getThongKeGiangVienTheoThoiGian(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeGiangVienTheoThoiGian(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Sinh viên theo khoảng thời gian
     */
    public List<Object[]> getThongKeSinhVienTheoThoiGian(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeSinhVienTheoThoiGian(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Học phần theo khoảng thời gian
     */
    public List<Object[]> getThongKeHocPhanTheoThoiGian(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeHocPhanTheoThoiGian(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== THỐNG KÊ NHIỀU KHÓA (MỤC 12.b) ====================
    
    /**
     * Thống kê Sinh viên và Học phần (12.b.ii)
     */
    public List<Object[]> getThongKeSinhVienVaHocPhan(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeSinhVienVaHocPhan(tuNgay, denNgay);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê Giảng viên và Học phần theo Năm (12.b.iii)
     */
    public List<Object[]> getThongKeGiangVienVaHocPhanTheoNam(int nam) {
        try {
            return thongKeDAO.getThongKeGiangVienVaHocPhanTheoNam(nam);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
}
