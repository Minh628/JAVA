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
    
    /**
     * Lấy tên quý
     */
    public String getTenQuy(int quy) {
        return "Quý " + quy;
    }
    
    /**
     * Lấy các tháng trong quý
     */
    public int[] getThangTrongQuy(int quy) {
        int thangDau = (quy - 1) * 3 + 1;
        return new int[]{thangDau, thangDau + 1, thangDau + 2};
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
     * Thống kê theo tháng
     */
    public List<Object[]> getThongKeTheoThang(int nam) {
        try {
            return thongKeDAO.getThongKeTheoThang(nam);
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
     * Thống kê theo khoa và quý (cross-tabulation điểm TB)
     */
    public List<Object[]> getThongKeKhoaTheoQuy(int nam) {
        try {
            return thongKeDAO.getThongKeKhoaTheoQuy(nam);
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
     * Thống kê theo giảng viên
     */
    public List<Object[]> getThongKeTheoGiangVien(Date tuNgay, Date denNgay) {
        try {
            return thongKeDAO.getThongKeTheoGiangVien(tuNgay, denNgay);
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
    
    /**
     * Lấy danh sách năm có dữ liệu
     */
    public List<Integer> getDanhSachNam() {
        try {
            return thongKeDAO.getDanhSachNam();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== Tổng hợp tính toán ====================
    
    /**
     * Tính hàng tổng cộng cho bảng thống kê
     */
    public Object[] tinhTongCong(List<Object[]> data, int[] cotSo) {
        if (data == null || data.isEmpty()) return null;
        
        int soCot = data.get(0).length;
        Object[] tongCong = new Object[soCot];
        tongCong[0] = "Tổng cộng";
        
        // Khởi tạo tổng
        double[] sums = new double[soCot];
        int[] counts = new int[soCot];
        
        for (Object[] row : data) {
            for (int cot : cotSo) {
                if (row[cot] != null) {
                    if (row[cot] instanceof Number) {
                        sums[cot] += ((Number) row[cot]).doubleValue();
                        counts[cot]++;
                    }
                }
            }
        }
        
        // Gán giá trị trung bình (phù hợp với điểm) hoặc tổng (phù hợp với số lượng)
        for (int cot : cotSo) {
            if (counts[cot] > 0) {
                // Với cột điểm/tỷ lệ thì lấy trung bình, với cột số lượng thì lấy tổng
                // Giả sử cột >= 3 là điểm/tỷ lệ, cột < 3 là số lượng
                if (cot >= 3) {
                    tongCong[cot] = Math.round(sums[cot] / counts[cot] * 100) / 100.0;
                } else {
                    tongCong[cot] = (int) sums[cot];
                }
            }
        }
        
        return tongCong;
    }
    
    /**
     * Format số thập phân
     */
    public String formatSo(Object value, int soLe) {
        if (value == null) return "-";
        if (value instanceof Number) {
            return String.format("%." + soLe + "f", ((Number) value).doubleValue());
        }
        return value.toString();
    }
}
