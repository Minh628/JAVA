/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: KyThiBUS - Xử lý logic nghiệp vụ Kỳ thi
 */
package bus;

import dao.KyThiDAO;
import dto.KyThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KyThiBUS {
    private KyThiDAO kyThiDAO;
    
    // Cache
    private static ArrayList<KyThiDTO> danhSachKyThi = null;

    public KyThiBUS() {
        this.kyThiDAO = new KyThiDAO();
    }

    /**
     * Lấy danh sách kỳ thi
     */
    public List<KyThiDTO> getDanhSachKyThi() {
        if (danhSachKyThi == null) {
            try {
                danhSachKyThi = new ArrayList<>(kyThiDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachKyThi;
    }

    /**
     * Lấy kỳ thi đang diễn ra
     */
    public List<KyThiDTO> getKyThiDangDienRa() {
        try {
            return kyThiDAO.getKyThiDangDienRa();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy kỳ thi theo mã
     */
    public KyThiDTO getById(int maKyThi) {
        try {
            return kyThiDAO.getById(maKyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Thêm kỳ thi mới
     */
    public boolean themKyThi(KyThiDTO kyThi) {
        try {
            if (kyThiDAO.insert(kyThi)) {
                danhSachKyThi = new ArrayList<>(kyThiDAO.getAll());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật kỳ thi
     */
    public boolean capNhatKyThi(KyThiDTO kyThi) {
        try {
            if (kyThiDAO.update(kyThi)) {
                KyThiDTO updated = kyThiDAO.getById(kyThi.getMaKyThi());
                for (int i = 0; i < danhSachKyThi.size(); i++) {
                    if (danhSachKyThi.get(i).getMaKyThi() == kyThi.getMaKyThi()) {
                        danhSachKyThi.set(i, updated);
                        break;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa kỳ thi
     */
    public boolean xoaKyThi(int maKyThi) {
        try {
            if (kyThiDAO.delete(maKyThi)) {
                danhSachKyThi.removeIf(kt -> kt.getMaKyThi() == maKyThi);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tìm kiếm kỳ thi
     */
    public List<KyThiDTO> timKiem(String keyword) {
        try {
            return kyThiDAO.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm kỳ thi theo loại
     */
    public List<KyThiDTO> timKiem(String keyword, String loai) {
        List<KyThiDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachKyThi();
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            for (KyThiDTO kt : danhSachKyThi) {
                if (matchFilter(kt, keyword, loai, now)) {
                    result.add(kt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchFilter(KyThiDTO kt, String keyword, String loai, java.sql.Timestamp now) {
        if (keyword.isEmpty()) return true;
        String trangThai = getTrangThai(kt, now);
        switch (loai) {
            case "Mã KT":
                return String.valueOf(kt.getMaKyThi()).contains(keyword);
            case "Tên Kỳ Thi":
                return kt.getTenKyThi() != null && kt.getTenKyThi().toLowerCase().contains(keyword);
            case "Trạng thái":
                return trangThai.toLowerCase().contains(keyword);
            case "Tất cả":
                return String.valueOf(kt.getMaKyThi()).contains(keyword) ||
                       (kt.getTenKyThi() != null && kt.getTenKyThi().toLowerCase().contains(keyword)) ||
                       trangThai.toLowerCase().contains(keyword);
            default:
                return true;
        }
    }

    private String getTrangThai(KyThiDTO kt, java.sql.Timestamp now) {
        if (kt.getThoiGianBatDau() == null || kt.getThoiGianKetThuc() == null) {
            return "Chưa xác định";
        }
        if (now.before(kt.getThoiGianBatDau())) {
            return "Sắp diễn ra";
        } else if (now.after(kt.getThoiGianKetThuc())) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }

    public static void reloadCache() {
        danhSachKyThi = null;
    }
}
