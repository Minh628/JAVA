/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: NganhBUS - Xử lý logic nghiệp vụ Ngành
 */
package bus;


import dao.KhoaDAO;
import dao.NganhDAO;
import dto.KhoaDTO;
import dto.NganhDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.SearchCondition;

public class NganhBUS {
    private NganhDAO nganhDAO;
    private KhoaDAO khoaDAO;
    
    // Cache
    private static ArrayList<NganhDTO> danhSachNganh = null;

    public NganhBUS() {
        this.nganhDAO = new NganhDAO();
        this.khoaDAO = new KhoaDAO();
    }

    /**
     * Lấy danh sách ngành
     */
    public List<NganhDTO> getDanhSachNganh() {
        if (danhSachNganh == null) {
            try {
                danhSachNganh = new ArrayList<>(nganhDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachNganh;
    }

    /**
     * Lấy ngành theo khoa
     */
    public List<NganhDTO> getNganhTheoKhoa(int maKhoa) {
        getDanhSachNganh();
        ArrayList<NganhDTO> ketQua = new ArrayList<>();
        for (NganhDTO n : danhSachNganh) {
            if (n.getMaKhoa() == maKhoa) {
                ketQua.add(n);
            }
        }
        return ketQua;
    }

    /**
     * Lấy ngành theo mã
     */
    public NganhDTO getById(int maNganh) {
        getDanhSachNganh();
        for (NganhDTO n : danhSachNganh) {
            if (n.getMaNganh() == maNganh) {
                return n;
            }
        }
        return null;
    }

    /**
     * Thêm ngành mới
     */
    public boolean themNganh(NganhDTO nganh) {
        try {
            if (nganhDAO.insert(nganh)) {
                danhSachNganh = new ArrayList<>(nganhDAO.getAll());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật ngành
     */
    public boolean capNhatNganh(NganhDTO nganh) {
        try {
            if (nganhDAO.update(nganh)) {
                NganhDTO updated = nganhDAO.getById(nganh.getMaNganh());
                for (int i = 0; i < danhSachNganh.size(); i++) {
                    if (danhSachNganh.get(i).getMaNganh() == nganh.getMaNganh()) {
                        danhSachNganh.set(i, updated);
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
     * Xóa ngành
     */
    public boolean xoaNganh(int maNganh) {
        try {
            if (nganhDAO.delete(maNganh)) {
                danhSachNganh.removeIf(n -> n.getMaNganh() == maNganh);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Tìm kiếm ngành theo loại
     */
    public List<NganhDTO> timKiem(String keyword, String loai) {
        List<NganhDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachNganh();
            for (NganhDTO n : danhSachNganh) {
                if (matchFilter(n, keyword, loai)) {
                    result.add(n);
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchFilter(NganhDTO n, String keyword, String loai) {
        if (keyword.isEmpty())
            return true;
        try {
            switch (loai) {
                case "Mã Ngành":
                    return String.valueOf(n.getMaNganh()).contains(keyword);
                case "Tên Ngành":
                    return n.getTenNganh() != null && n.getTenNganh().toLowerCase().contains(keyword);
                case "Thuộc Khoa": {
                    KhoaDTO khoa = khoaDAO.getById(n.getMaKhoa());
                    if (khoa != null && khoa.getTenKhoa().toLowerCase().contains(keyword)) {
                        return true;
                    }
                }
                case "Tất cả":
                    return String.valueOf(n.getMaNganh()).contains(keyword) ||
                            (n.getTenNganh() != null && n.getTenNganh().toLowerCase().contains(keyword)) ||
                            (khoaDAO.getById(n.getMaKhoa()) != null &&
                             khoaDAO.getById(n.getMaKhoa()).getTenKhoa().toLowerCase().contains(keyword));
                default:
                    return true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reloadCache() {
        danhSachNganh = null;
    }

    /**
     * Tìm kiếm nâng cao với nhiều điều kiện
     */
    public List<NganhDTO> timKiemNangCao(List<SearchCondition> conditions, String logic) {
        List<NganhDTO> result = new ArrayList<>();
        try {
            getDanhSachNganh();
            for (NganhDTO n : danhSachNganh) {
                if (evaluateConditions(n, conditions, logic)) {
                    result.add(n);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean evaluateConditions(NganhDTO n, List<SearchCondition> conditions, String logic) {
        if (conditions.isEmpty()) return true;
        if ("AND".equals(logic)) {
            for (SearchCondition cond : conditions) {
                if (!evaluateSingleCondition(n, cond)) {
                    return false;
                }
            }
            return true;
        }

        if ("OR".equals(logic)) {
            for (SearchCondition cond : conditions) {
                if (evaluateSingleCondition(n, cond)) {
                    return true;
                }
            }
            return false;
        }

        if ("NOT".equals(logic)) {
            for (SearchCondition cond : conditions) {
                if (evaluateSingleCondition(n, cond)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean evaluateSingleCondition(NganhDTO n, SearchCondition cond) {
        String fieldValue = switch (cond.getField()) {
            case "Mã Ngành" -> String.valueOf(n.getMaNganh());
            case "Tên Ngành" -> n.getTenNganh();
            case "Thuộc Khoa" -> {
                try {
                    KhoaDTO khoa = khoaDAO.getById(n.getMaKhoa());
                    yield khoa != null ? khoa.getTenKhoa() : "";
                } catch (Exception e) {
                    yield "";
                }
            }
            default -> "";
        };
        if (fieldValue == null) fieldValue = "";
        return cond.evaluate(fieldValue);
    }
}
