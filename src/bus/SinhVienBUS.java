/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: SinhVienBUS - Xử lý logic nghiệp vụ Sinh viên
 * CHỈ gọi SinhVienDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.NganhDAO;
import dao.SinhVienDAO;
import dto.NganhDTO;
import dto.SinhVienDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.SearchCondition;

public class SinhVienBUS {
    private SinhVienDAO sinhVienDAO;
    private NganhDAO nganhDAO = new NganhDAO();

    // Cache
    private static ArrayList<SinhVienDTO> danhSachSinhVien = null;

    public SinhVienBUS() {
        this.sinhVienDAO = new SinhVienDAO();
        this.nganhDAO = new NganhDAO();
    }

    /**
     * Lấy danh sách tất cả sinh viên
     */
    public List<SinhVienDTO> getDanhSachSinhVien() {
        if (danhSachSinhVien == null) {
            try {
                danhSachSinhVien = new ArrayList<>(sinhVienDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachSinhVien;
    }

    /**
     * Lấy sinh viên theo ngành
     */
    public List<SinhVienDTO> getSinhVienTheoNganh(int maNganh) {
        getDanhSachSinhVien();
        ArrayList<SinhVienDTO> ketQua = new ArrayList<>();
        if (danhSachSinhVien != null) {
            for (SinhVienDTO sv : danhSachSinhVien) {
                if (sv.getMaNganh() == maNganh) {
                    ketQua.add(sv);
                }
            }
        }
        return ketQua;
    }

    /**
     * Lấy sinh viên theo mã
     */
    public SinhVienDTO getById(int maSV) {
        try {
            return sinhVienDAO.getById(maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Thêm sinh viên mới
     */
    public boolean themSinhVien(SinhVienDTO sinhVien) {
        try {
            if (sinhVienDAO.checkTenDangNhapExists(sinhVien.getTenDangNhap())) {
                return false;
            }
            if (sinhVienDAO.insert(sinhVien)) {
                danhSachSinhVien = new ArrayList<>(sinhVienDAO.getAll());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật sinh viên
     */
    public boolean capNhatSinhVien(SinhVienDTO sinhVien) {
        try {
            if (sinhVienDAO.update(sinhVien)) {
                SinhVienDTO updated = sinhVienDAO.getById(sinhVien.getMaSV());
                if (danhSachSinhVien != null) {
                    for (int i = 0; i < danhSachSinhVien.size(); i++) {
                        if (danhSachSinhVien.get(i).getMaSV() == sinhVien.getMaSV()) {
                            danhSachSinhVien.set(i, updated);
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
     * Cập nhật thông tin sinh viên (alias cho capNhatSinhVien)
     */
    public boolean capNhatThongTin(SinhVienDTO sinhVien) {
        return capNhatSinhVien(sinhVien);
    }

    /**
     * Xóa sinh viên
     */
    public boolean xoaSinhVien(int maSV) {
        try {
            if (sinhVienDAO.delete(maSV)) {
                if (danhSachSinhVien != null) {
                    danhSachSinhVien.removeIf(sv -> sv.getMaSV() == maSV);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Đổi mật khẩu sinh viên
     */
    public boolean doiMatKhau(int maSV, String matKhauCu, String matKhauMoi) {
        try {
            SinhVienDTO sv = sinhVienDAO.getById(maSV);
            if (sv == null || !sv.getMatKhau().equals(matKhauCu)) {
                return false;
            }
            return sinhVienDAO.updatePassword(maSV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm sinh viên
     */
    public List<SinhVienDTO> timKiem(String keyword) {
        try {
            return sinhVienDAO.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm sinh viên theo loại
     */
    public List<SinhVienDTO> timKiem(String keyword, String loai) {
        List<SinhVienDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachSinhVien();
            for (SinhVienDTO sv : danhSachSinhVien) {
                if (matchFilter(sv, keyword, loai)) {
                    result.add(sv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchFilter(SinhVienDTO sv, String keyword, String loai) {
        if (keyword.isEmpty()) return true;
        try {
            switch (loai) {
            case "Mã SV":
                return String.valueOf(sv.getMaSV()).contains(keyword);
            case "Tên đăng nhập":
                return sv.getTenDangNhap() != null && sv.getTenDangNhap().toLowerCase().contains(keyword);
            case "Họ tên":
                return (sv.getHo() + " " + sv.getTen()).toLowerCase().contains(keyword);
            case "Email":
                return sv.getEmail() != null && sv.getEmail().toLowerCase().contains(keyword);
            case "Ngành": {
                NganhDTO nganh = nganhDAO.getById(sv.getMaNganh());
                if (nganh != null &&  nganh.getTenNganh().toLowerCase().contains(keyword)) {
                    return true;
                }
            }
            case "Tất cả":
                return String.valueOf(sv.getMaSV()).contains(keyword) ||
                       (sv.getTenDangNhap() != null && sv.getTenDangNhap().toLowerCase().contains(keyword)) ||
                       (sv.getHo() + " " + sv.getTen()).toLowerCase().contains(keyword) ||
                       (sv.getEmail() != null && sv.getEmail().toLowerCase().contains(keyword)) ||
                       (nganhDAO.getById(sv.getMaNganh()) != null &&
                        nganhDAO.getById(sv.getMaNganh()).getTenNganh().toLowerCase().contains(keyword));
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
        danhSachSinhVien = null;
    }

    /**
     * Tìm kiếm nâng cao với nhiều điều kiện
     */
    public List<SinhVienDTO> timKiemNangCao(List<SearchCondition> conditions, String logic) {
        List<SinhVienDTO> result = new ArrayList<>();
        try {
            getDanhSachSinhVien();
            for (SinhVienDTO sv : danhSachSinhVien) {
                if (evaluateConditions(sv, conditions, logic)) {
                    result.add(sv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean evaluateConditions(SinhVienDTO sv, List<SearchCondition> conditions, String logic) {
        if (conditions.isEmpty()) return true;
        boolean result = "AND".equals(logic);
        for (SearchCondition cond : conditions) {
            boolean condResult = evaluateSingleCondition(sv, cond);
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

    private boolean evaluateSingleCondition(SinhVienDTO sv, SearchCondition cond) {
        String fieldValue = switch (cond.getField()) {
            case "Mã SV" -> String.valueOf(sv.getMaSV());
            case "Tên đăng nhập" -> sv.getTenDangNhap();
            case "Họ tên" -> (sv.getHo() != null ? sv.getHo() : "") + " " + (sv.getTen() != null ? sv.getTen() : "");
            case "Email" -> sv.getEmail();
            case "Ngành" -> {
                try {
                    NganhDTO nganh = nganhDAO.getById(sv.getMaNganh());
                    yield nganh != null ? nganh.getTenNganh() : "";
                } catch (Exception e) {
                    yield "";
                }
            }
            case "Trạng thái" -> sv.isTrangThai() ? "Hoạt động" : "Khóa";
            default -> "";
        };
        if (fieldValue == null) fieldValue = "";
        return cond.evaluate(fieldValue);
    }
}
