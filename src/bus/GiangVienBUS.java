/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: GiangVienBUS - Xử lý logic nghiệp vụ Giảng viên
 * CHỈ quản lý: thông tin giảng viên
 * 
 * Lưu ý: Các chức năng khác đã được tách ra:
 * - Quản lý câu hỏi: CauHoiBUS
 * - Quản lý đề thi: DeThiBUS
 * - Quản lý học phần: HocPhanBUS
 * - Quản lý kỳ thi: KyThiBUS
 */
package bus;

import dao.GiangVienDAO;
import dao.KhoaDAO;
import dto.GiangVienDTO;
import dto.KhoaDTO;
import dto.VaiTroDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.SearchCondition;

public class GiangVienBUS {
    private GiangVienDAO giangVienDAO;
    private KhoaDAO khoaDAO;

    // Cache
    private static ArrayList<GiangVienDTO> danhSachGiangVien = null;

    public GiangVienBUS() {
        this.giangVienDAO = new GiangVienDAO();
        this.khoaDAO = new KhoaDAO();
    }

    /**
     * Lấy danh sách tất cả giảng viên (không bao gồm trưởng khoa)
     */
    public List<GiangVienDTO> getDanhSachGiangVien() {
        if (danhSachGiangVien == null) {
            try {
                danhSachGiangVien = new ArrayList<>(giangVienDAO.getAllGiangVien());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachGiangVien;
    }

    /**
     * Lấy tất cả (bao gồm trưởng khoa)
     */
    public List<GiangVienDTO> getAll() {
        try {
            return giangVienDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy giảng viên theo khoa
     */
    public List<GiangVienDTO> getGiangVienTheoKhoa(int maKhoa) {
        getDanhSachGiangVien();
        ArrayList<GiangVienDTO> ketQua = new ArrayList<>();
        if (danhSachGiangVien != null) {
            for (GiangVienDTO gv : danhSachGiangVien) {
                if (gv.getMaKhoa() == maKhoa) {
                    ketQua.add(gv);
                }
            }
        }
        return ketQua;
    }

    /**
     * Lấy giảng viên theo mã
     */
    public GiangVienDTO getById(int maGV) {
        try {
            return giangVienDAO.getById(maGV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Thêm giảng viên mới
     */
    public boolean themGiangVien(GiangVienDTO giangVien) {
        try {
            if (giangVienDAO.checkTenDangNhapExists(giangVien.getTenDangNhap())) {
                return false;
            }
            giangVien.setMaVaiTro(VaiTroDTO.GIANG_VIEN);
            if (giangVienDAO.insert(giangVien)) {
                danhSachGiangVien = new ArrayList<>(giangVienDAO.getAllGiangVien());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật giảng viên
     */
    public boolean capNhatGiangVien(GiangVienDTO giangVien) {
        try {
            if (giangVienDAO.update(giangVien)) {
                GiangVienDTO updated = giangVienDAO.getById(giangVien.getMaGV());
                if (danhSachGiangVien != null) {
                    for (int i = 0; i < danhSachGiangVien.size(); i++) {
                        if (danhSachGiangVien.get(i).getMaGV() == giangVien.getMaGV()) {
                            danhSachGiangVien.set(i, updated);
                            break;
                        }
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
     * Xóa giảng viên
     */
    public boolean xoaGiangVien(int maGV) {
        try {
            if (giangVienDAO.delete(maGV)) {
                if (danhSachGiangVien != null) {
                    danhSachGiangVien.removeIf(gv -> gv.getMaGV() == maGV);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Reset mật khẩu (Admin dùng)
     */
    public boolean resetMatKhau(int maGV, String matKhauMoi) {
        try {
            return giangVienDAO.updatePassword(maGV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đổi mật khẩu (Giảng viên tự đổi)
     */
    public boolean doiMatKhau(int maGV, String matKhauCu, String matKhauMoi) {
        try {
            GiangVienDTO gv = giangVienDAO.getById(maGV);
            if (gv == null) return false;
            
            // Kiểm tra mật khẩu cũ
            if (!matKhauCu.equals(gv.getMatKhau())) {
                return false;
            }
            
            // Cập nhật mật khẩu mới
            return giangVienDAO.updatePassword(maGV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm giảng viên
     */
    public List<GiangVienDTO> timKiem(String keyword) {
        try {
            return giangVienDAO.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm giảng viên theo loại
     */
    public List<GiangVienDTO> timKiem(String keyword, String loai) {
        List<GiangVienDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachGiangVien();
            for (GiangVienDTO gv : danhSachGiangVien) {
                if (matchFilter(gv, keyword, loai)) {
                    result.add(gv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchFilter(GiangVienDTO gv, String keyword, String loai) {
        if (keyword.isEmpty()) return true;
        try {
            switch (loai) {
                case "Mã GV":
                    return String.valueOf(gv.getMaGV()).contains(keyword);
                case "Tên đăng nhập":
                    return gv.getTenDangNhap() != null && gv.getTenDangNhap().toLowerCase().contains(keyword);
                case "Họ tên":
                    return (gv.getHo() + " " + gv.getTen()).toLowerCase().contains(keyword);
                case "Email":
                    return gv.getEmail() != null && gv.getEmail().toLowerCase().contains(keyword);
                case "Khoa": {
                    KhoaDTO khoa = khoaDAO.getById(gv.getMaKhoa());
                    if (khoa != null && khoa.getTenKhoa().toLowerCase().contains(keyword)) {
                        return true;
                    }
                }

                case "Tất cả":
                    return String.valueOf(gv.getMaGV()).contains(keyword) ||
                            (gv.getTenDangNhap() != null && gv.getTenDangNhap().toLowerCase().contains(keyword)) ||
                            (gv.getHo() + " " + gv.getTen()).toLowerCase().contains(keyword) ||
                            (gv.getEmail() != null && gv.getEmail().toLowerCase().contains(keyword)) ||
                            (khoaDAO.getById(gv.getMaKhoa()) != null &&
                             khoaDAO.getById(gv.getMaKhoa()).getTenKhoa().toLowerCase().contains(keyword));
                default:
                    return true;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reloadCache() {
        danhSachGiangVien = null;
    }

    /**
     * Tìm kiếm nâng cao với nhiều điều kiện
     */
    public List<GiangVienDTO> timKiemNangCao(List<SearchCondition> conditions, String logic) {
        List<GiangVienDTO> result = new ArrayList<>();
        try {
            getDanhSachGiangVien();
            for (GiangVienDTO gv : danhSachGiangVien) {
                if (evaluateConditions(gv, conditions, logic)) {
                    result.add(gv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean evaluateConditions(GiangVienDTO gv, List<SearchCondition> conditions, String logic) {
        if (conditions.isEmpty()) return true;
        boolean result = "AND".equals(logic);
        for (SearchCondition cond : conditions) {
            boolean condResult = evaluateSingleCondition(gv, cond);
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

    private boolean evaluateSingleCondition(GiangVienDTO gv, SearchCondition cond) {
        String fieldValue = switch (cond.getField()) {
            case "Mã GV" -> String.valueOf(gv.getMaGV());
            case "Tên đăng nhập" -> gv.getTenDangNhap();
            case "Họ tên" -> (gv.getHo() != null ? gv.getHo() : "") + " " + (gv.getTen() != null ? gv.getTen() : "");
            case "Email" -> gv.getEmail();
            case "Khoa" -> {
                try {
                    KhoaDTO khoa = khoaDAO.getById(gv.getMaKhoa());
                    yield khoa != null ? khoa.getTenKhoa() : "";
                } catch (Exception e) {
                    yield "";
                }
            }
            case "Trạng thái" -> gv.isTrangThai() ? "Hoạt động" : "Khóa";
            default -> "";
        };
        if (fieldValue == null) fieldValue = "";
        return cond.evaluate(fieldValue);
    }
}
