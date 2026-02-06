/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: KhoaBUS - Xử lý logic nghiệp vụ Khoa
 */
package bus;

import dao.KhoaDAO;
import dao.NganhDAO;
import dto.KhoaDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.SearchCondition;

public class KhoaBUS {
    private KhoaDAO khoaDAO;
    private NganhDAO nganhDAO;
    
    // Cache
    private static ArrayList<KhoaDTO> danhSachKhoa = null;

    public KhoaBUS() {
        this.khoaDAO = new KhoaDAO();
        this.nganhDAO = new NganhDAO();
    }

    /**
     * Lấy danh sách khoa
     */
    public List<KhoaDTO> getDanhSachKhoa() {
        if (danhSachKhoa == null) {
            try {
                danhSachKhoa = new ArrayList<>(khoaDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachKhoa;
    }

    /**
     * Lấy khoa theo mã
     */
    public KhoaDTO getById(int maKhoa) {
        getDanhSachKhoa();
        for (KhoaDTO k : danhSachKhoa) {
            if (k.getMaKhoa() == maKhoa) {
                return k;
            }
        }
        return null;
    }

    /**
     * Thêm khoa mới
     */
    public boolean themKhoa(KhoaDTO khoa) {
        try {
            if (khoaDAO.insert(khoa)) {
                danhSachKhoa = new ArrayList<>(khoaDAO.getAll());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật khoa
     */
    public boolean capNhatKhoa(KhoaDTO khoa) {
        try {
            if (khoaDAO.update(khoa)) {
                // Cập nhật cache
                for (int i = 0; i < danhSachKhoa.size(); i++) {
                    if (danhSachKhoa.get(i).getMaKhoa() == khoa.getMaKhoa()) {
                        danhSachKhoa.set(i, khoa);
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
     * Xóa khoa - kiểm tra ràng buộc
     */
    public boolean xoaKhoa(int maKhoa) {
        try {
            // Kiểm tra còn ngành thuộc khoa không
            if (nganhDAO.countByKhoa(maKhoa) > 0) {
                return false; // Không xóa được vì còn ngành
            }
            
            if (khoaDAO.delete(maKhoa)) {
                danhSachKhoa.removeIf(k -> k.getMaKhoa() == maKhoa);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra có thể xóa khoa không
     */
    public boolean coTheXoaKhoa(int maKhoa) {
        try {
            return nganhDAO.countByKhoa(maKhoa) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm khoa
     */
    public List<KhoaDTO> timKiem(String keyword) {
        try {
            return khoaDAO.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm khoa theo loại
     */
    public List<KhoaDTO> timKiem(String keyword, String loai) {
        List<KhoaDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachKhoa();
            for (KhoaDTO k : danhSachKhoa) {
                if (matchFilter(k, keyword, loai)) {
                    result.add(k);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchFilter(KhoaDTO k, String keyword, String loai) {
        if (keyword.isEmpty()) return true;
        switch (loai) {
            case "Mã Khoa":
                return String.valueOf(k.getMaKhoa()).contains(keyword);
            case "Tên Khoa":
                return k.getTenKhoa() != null && k.getTenKhoa().toLowerCase().contains(keyword);
            case "Tất cả":
                return String.valueOf(k.getMaKhoa()).contains(keyword) ||
                       (k.getTenKhoa() != null && k.getTenKhoa().toLowerCase().contains(keyword));
            default:
                return true;
        }
    }

    /**
     * Reload cache
     */
    public static void reloadCache() {
        danhSachKhoa = null;
    }

    /**
     * Tìm kiếm nâng cao với nhiều điều kiện
     */
    public List<KhoaDTO> timKiemNangCao(List<SearchCondition> conditions, String logic) {
        List<KhoaDTO> result = new ArrayList<>();
        try {
            getDanhSachKhoa();
            for (KhoaDTO k : danhSachKhoa) {
                if (evaluateConditions(k, conditions, logic)) {
                    result.add(k);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean evaluateConditions(KhoaDTO k, List<SearchCondition> conditions, String logic) {
        if (conditions.isEmpty()) return true;
        boolean result = "AND".equals(logic);
        for (SearchCondition cond : conditions) {
            boolean condResult = evaluateSingleCondition(k, cond);
            if ("AND".equals(logic)) {
                result = result && condResult;
                if (!result) return false;
            } else if ("OR".equals(logic)) {
                result = result || condResult;
            } else if ("NOT".equals(logic)) {
                result = !condResult;
            }
        }
        return result;
    }

    private boolean evaluateSingleCondition(KhoaDTO k, SearchCondition cond) {
        String fieldValue = switch (cond.getField()) {
            case "Mã Khoa" -> String.valueOf(k.getMaKhoa());
            case "Tên Khoa" -> k.getTenKhoa();
            default -> "";
        };
        if (fieldValue == null) fieldValue = "";
        return cond.evaluate(fieldValue);
    }
}
