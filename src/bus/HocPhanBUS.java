/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: HocPhanBUS - Xử lý logic nghiệp vụ Học phần
 */
package bus;

import dao.HocPhanDAO;
import dao.KhoaDAO;
import dto.HocPhanDTO;
import dto.KhoaDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.SearchCondition;

public class HocPhanBUS {
    private HocPhanDAO hocPhanDAO;
    private KhoaDAO khoaDAO;
    
    // Cache
    private static ArrayList<HocPhanDTO> danhSachHocPhan = null;

    public HocPhanBUS() {
        this.hocPhanDAO = new HocPhanDAO();
        this.khoaDAO = new KhoaDAO();
    }

    /**
     * Lấy danh sách học phần
     */
    public List<HocPhanDTO> getDanhSachHocPhan() {
        if (danhSachHocPhan == null) {
            try {
                danhSachHocPhan = new ArrayList<>(hocPhanDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachHocPhan;
    }

    /**
     * Lấy học phần theo mã
     */
    public HocPhanDTO getById(int maHocPhan) {
        getDanhSachHocPhan();
        for (HocPhanDTO hp : danhSachHocPhan) {
            if (hp.getMaHocPhan() == maHocPhan) {
                return hp;
            }
        }
        return null;
    }

    /**
     * Thêm học phần mới
     */
    public boolean themHocPhan(HocPhanDTO hocPhan) {
        try {
            if (hocPhanDAO.insert(hocPhan)) {
                danhSachHocPhan = new ArrayList<>(hocPhanDAO.getAll());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật học phần
     */
    public boolean capNhatHocPhan(HocPhanDTO hocPhan) {
        try {
            if (hocPhanDAO.update(hocPhan)) {
                for (int i = 0; i < danhSachHocPhan.size(); i++) {
                    if (danhSachHocPhan.get(i).getMaHocPhan() == hocPhan.getMaHocPhan()) {
                        danhSachHocPhan.set(i, hocPhan);
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
     * Xóa học phần
     */
    public boolean xoaHocPhan(int maHocPhan) {
        try {
            if (hocPhanDAO.delete(maHocPhan)) {
                danhSachHocPhan.removeIf(hp -> hp.getMaHocPhan() == maHocPhan);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tìm kiếm học phần
     */
    public List<HocPhanDTO> timKiem(String keyword, String loai) {
        List<HocPhanDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachHocPhan(); // đảm bảo cache đã load

            for (HocPhanDTO hp : danhSachHocPhan) {
                if (matchFilter(hp, keyword, loai)) {
                    result.add(hp);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }

    private boolean matchFilter(HocPhanDTO hp, String keyword, String loai) {
        try{
            if (keyword.isEmpty())
            return true;

            return switch (loai) {
                case "Mã HP" -> String.valueOf(hp.getMaHocPhan()).contains(keyword);
                case "Tên Học Phần" -> hp.getTenMon() != null &&
                        hp.getTenMon().toLowerCase().contains(keyword);
                case "Số TC" -> String.valueOf(hp.getSoTin()).contains(keyword);
                case "Khoa" -> {
                    KhoaDTO khoa = khoaDAO.getById(hp.getMaKhoa());
                    yield khoa != null &&
                        khoa.getTenKhoa().toLowerCase().contains(keyword);
                }
                case "Tất cả" -> {
                    if (String.valueOf(hp.getMaHocPhan()).contains(keyword))
                        yield true;
                    if (hp.getTenMon() != null &&
                        hp.getTenMon().toLowerCase().contains(keyword))
                        yield true;
                    if (String.valueOf(hp.getSoTin()).contains(keyword))
                        yield true;
                    KhoaDTO khoa = khoaDAO.getById(hp.getMaKhoa());
                    if (khoa != null &&
                        khoa.getTenKhoa().toLowerCase().contains(keyword))
                        yield true;
                    yield false;
                }
                default -> true;
            };
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
        
    }

    public static void reloadCache() {
        danhSachHocPhan = null;
    }

    /**
     * Tìm kiếm nâng cao với nhiều điều kiện
     */
    public List<HocPhanDTO> timKiemNangCao(List<SearchCondition> conditions, String logic) {
        List<HocPhanDTO> result = new ArrayList<>();
        try {
            getDanhSachHocPhan();
            for (HocPhanDTO hp : danhSachHocPhan) {
                if (evaluateConditions(hp, conditions, logic)) {
                    result.add(hp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean evaluateConditions(HocPhanDTO hp, List<SearchCondition> conditions, String logic) {
        if (conditions.isEmpty()) return true;

        // --- TRƯỜNG HỢP 1: AND (VÀ) ---
        // Tất cả điều kiện phải đúng. Chỉ cần 1 cái sai => SAI HẾT.
        if ("AND".equals(logic)) {
            for (SearchCondition cond : conditions) {
                boolean isMatch = evaluateSingleCondition(hp, cond);
                if (!isMatch) {
                    return false; // Gặp sai là dừng ngay
                }
            }
            return true; // Chạy hết vòng lặp mà không sai cái nào => ĐÚNG
        }

        // --- TRƯỜNG HỢP 2: OR (HOẶC) ---
        // Chỉ cần 1 điều kiện đúng => ĐÚNG HẾT.
        if ("OR".equals(logic)) {
            for (SearchCondition cond : conditions) {
                boolean isMatch = evaluateSingleCondition(hp, cond);
                if (isMatch) {
                    return true; // Gặp đúng là dừng ngay (lấy luôn)
                }
            }
            return false; // Chạy hết vòng lặp mà không có cái nào đúng => SAI
        }

        // --- TRƯỜNG HỢP 3: NOT (KHÔNG / NOR) ---
        // Không được thỏa mãn bất kỳ điều kiện nào.
        // Ví dụ: Tìm môn KHÔNG phải khoa CNTT và KHÔNG có 3 tín chỉ.
        // Chỉ cần dính 1 điều kiện => LOẠI NGAY.
        if ("NOT".equals(logic)) {
            for (SearchCondition cond : conditions) {
                boolean isMatch = evaluateSingleCondition(hp, cond);
                if (isMatch) {
                    return false; // Dính điều kiện cấm => SAI (Loại bỏ bản ghi này)
                }
            }
            return true; // Không dính điều kiện nào => ĐÚNG (Giữ lại)
        }

        return false; // Mặc định trả về false nếu logic không hợp lệ
    }

    private boolean evaluateSingleCondition(HocPhanDTO hp, SearchCondition cond) {
        String fieldValue = switch (cond.getField()) {
            
            case "Mã HP" -> String.valueOf(hp.getMaHocPhan());
            case "Tên Học Phần" -> hp.getTenMon();
            case "Số TC" -> String.valueOf(hp.getSoTin());
            case "Khoa" -> {
                try {
                    KhoaDTO khoa = khoaDAO.getById(hp.getMaKhoa());
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
