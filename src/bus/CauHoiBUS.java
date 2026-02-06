/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: CauHoiBUS - Xử lý logic nghiệp vụ Câu hỏi
 * CHỈ gọi CauHoiDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.CauHoiDAO;
import dao.ChiTietBaiThiDAO;
import dao.ChiTietDeThiDAO;
import dto.CauHoiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.SearchCondition;
public class CauHoiBUS {
    private CauHoiDAO cauHoiDAO;
    private ChiTietDeThiDAO chiTietDeThiDAO;
    private ChiTietBaiThiDAO chiTietBaiThiDAO;

    // Cache theo giảng viên
    private static ArrayList<CauHoiDTO> danhSachCauHoi = null;
    private static int lastMaGV = -1;

    public CauHoiBUS() {
        this.cauHoiDAO = new CauHoiDAO();
        this.chiTietDeThiDAO = new ChiTietDeThiDAO();
        this.chiTietBaiThiDAO = new ChiTietBaiThiDAO();
    }

    /**
     * Lấy danh sách câu hỏi theo giảng viên
     */
    public List<CauHoiDTO> getDanhSachCauHoi(int maGV) {
        if (danhSachCauHoi == null || lastMaGV != maGV) {
            try {
                danhSachCauHoi = new ArrayList<>(cauHoiDAO.getByGiangVien(maGV));
                lastMaGV = maGV;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachCauHoi;
    }

    /**
     * Lấy tất cả câu hỏi
     */
    public List<CauHoiDTO> getAllCauHoi() {
        try {
            return cauHoiDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy câu hỏi theo môn học
     */
    public List<CauHoiDTO> getCauHoiTheoMon(int maMon) {
        try {
            return cauHoiDAO.getByMon(maMon);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy câu hỏi theo môn và mức độ (cho tạo đề thi)
     */
    public List<CauHoiDTO> getCauHoiTheoMucDo(int maMon, String mucDo) {
        try {
            return cauHoiDAO.getByMucDo(maMon, mucDo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy câu hỏi theo ID
     */
    public CauHoiDTO getById(int maCauHoi) {
        try {
            return cauHoiDAO.getById(maCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy danh sách câu hỏi theo danh sách mã
     */
    public List<CauHoiDTO> getByIds(List<Integer> danhSachMaCauHoi) {
        List<CauHoiDTO> ketQua = new ArrayList<>();
        for (int maCauHoi : danhSachMaCauHoi) {
            CauHoiDTO ch = getById(maCauHoi);
            if (ch != null) {
                ketQua.add(ch);
            }
        }
        return ketQua;
    }

    /**
     * Thêm câu hỏi mới
     */
    public boolean themCauHoi(CauHoiDTO cauHoi) {
        try {
            if (cauHoiDAO.insert(cauHoi)) {
                // Reload cache
                if (lastMaGV == cauHoi.getMaGV()) {
                    danhSachCauHoi = new ArrayList<>(cauHoiDAO.getByGiangVien(cauHoi.getMaGV()));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật câu hỏi
     */
    public boolean capNhatCauHoi(CauHoiDTO cauHoi) {
        try {
            if (cauHoiDAO.update(cauHoi)) {
                // Cập nhật cache
                if (danhSachCauHoi != null) {
                    CauHoiDTO updated = cauHoiDAO.getById(cauHoi.getMaCauHoi());
                    for (int i = 0; i < danhSachCauHoi.size(); i++) {
                        if (danhSachCauHoi.get(i).getMaCauHoi() == cauHoi.getMaCauHoi()) {
                            danhSachCauHoi.set(i, updated);
                            break;
                        }
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa câu hỏi
     */
    public boolean xoaCauHoi(int maCauHoi) {
        try {
            // Không cho xóa nếu câu hỏi đã nằm trong đề thi
            if (chiTietDeThiDAO.isCauHoiInAnyDeThi(maCauHoi)) {
                return false;
            }
            // Không cho xóa nếu câu hỏi đã xuất hiện trong bài thi
            if (chiTietBaiThiDAO.countByCauHoi(maCauHoi) > 0) {
                return false;
            }
            if (cauHoiDAO.delete(maCauHoi)) {
                if (danhSachCauHoi != null) {
                    danhSachCauHoi.removeIf(ch -> ch.getMaCauHoi() == maCauHoi);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra có thể xóa câu hỏi không
     */
    public boolean coTheXoaCauHoi(int maCauHoi) {
        try {
            return !chiTietDeThiDAO.isCauHoiInAnyDeThi(maCauHoi)
                    && chiTietBaiThiDAO.countByCauHoi(maCauHoi) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reloadCache() {
        danhSachCauHoi = null;
        lastMaGV = -1;
    }

    /**
     * Tìm kiếm câu hỏi theo keyword và loại tìm kiếm
     */
    public List<CauHoiDTO> timKiem(int maGV, String keyword, String loai,
            java.util.function.Function<Integer, String> getTenMon) {
        List<CauHoiDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachCauHoi(maGV);

            for (CauHoiDTO ch : danhSachCauHoi) {
                if (matchFilter(ch, keyword, loai, getTenMon)) {
                    result.add(ch);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Tìm kiếm nâng cao với nhiều điều kiện
     */
    public List<CauHoiDTO> timKiemNangCao(int maGV, List<SearchCondition> conditions, String logic,
            java.util.function.Function<Integer, String> getTenMon) {
        List<CauHoiDTO> result = new ArrayList<>();
        try {
            getDanhSachCauHoi(maGV);
            
            for (CauHoiDTO ch : danhSachCauHoi) {
                boolean match = evaluateConditions(ch, conditions, logic, getTenMon);
                if (match) {
                    result.add(ch);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchFilter(CauHoiDTO ch, String keyword, String loai,
            java.util.function.Function<Integer, String> getTenMon) {
        if (keyword.isEmpty()) return true;
        
        String tenMon = getTenMon.apply(ch.getMaMon());
        String loaiCH = CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) ? "Điền khuyết" : "Trắc nghiệm";
        
        return switch (loai) {
            case "Mã" -> String.valueOf(ch.getMaCauHoi()).contains(keyword);
            case "Nội dung" -> ch.getNoiDungCauHoi() != null && 
                    ch.getNoiDungCauHoi().toLowerCase().contains(keyword);
            case "Môn học" -> tenMon != null && 
                    tenMon.toLowerCase().contains(keyword);
            case "Mức độ" -> ch.getMucDo() != null && 
                    ch.getMucDo().toLowerCase().contains(keyword);
            case "Loại" -> loaiCH.toLowerCase().contains(keyword);
            case "Tất cả" -> {
                if (String.valueOf(ch.getMaCauHoi()).contains(keyword)) yield true;
                if (ch.getNoiDungCauHoi() != null && 
                        ch.getNoiDungCauHoi().toLowerCase().contains(keyword)) yield true;
                if (tenMon != null && 
                        tenMon.toLowerCase().contains(keyword)) yield true;
                if (ch.getMucDo() != null && 
                        ch.getMucDo().toLowerCase().contains(keyword)) yield true;
                if (loaiCH.toLowerCase().contains(keyword)) yield true;
                yield false;
            }
            default -> true;
        };
    }

    private boolean evaluateConditions(CauHoiDTO ch, List<SearchCondition> conditions, String logic,
            java.util.function.Function<Integer, String> getTenMon) {
        if (conditions.isEmpty()) return true;
        
        boolean result = "AND".equals(logic);
        
        for (SearchCondition cond : conditions) {
            boolean condResult = evaluateSingleCondition(ch, cond, getTenMon);
            
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

    private boolean evaluateSingleCondition(CauHoiDTO ch, SearchCondition cond,
            java.util.function.Function<Integer, String> getTenMon) {
        String loaiCH = CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) ? "Điền khuyết" : "Trắc nghiệm";
        
        String fieldValue = switch (cond.getField()) {
            case "Mã" -> String.valueOf(ch.getMaCauHoi());
            case "Nội dung" -> ch.getNoiDungCauHoi();
            case "Môn học" -> getTenMon.apply(ch.getMaMon());
            case "Mức độ" -> ch.getMucDo();
            case "Loại" -> loaiCH;
            case "Đáp án đúng" -> ch.getDapAnDung();
            default -> "";
        };
        
        if (fieldValue == null) fieldValue = "";
        return cond.evaluate(fieldValue);
    }
}
