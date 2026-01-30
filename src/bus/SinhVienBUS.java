/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: SinhVienBUS - Xử lý logic nghiệp vụ Sinh viên
 * CHỈ gọi SinhVienDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.SinhVienDAO;
import dto.SinhVienDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SinhVienBUS {
    private SinhVienDAO sinhVienDAO;

    // Cache
    private static ArrayList<SinhVienDTO> danhSachSinhVien = null;

    public SinhVienBUS() {
        this.sinhVienDAO = new SinhVienDAO();
    }

    /**
     * Lấy danh sách tất cả sinh viên
     */
    public List<SinhVienDTO> getDanhSachSinhVien() {
        if (danhSachSinhVien == null) {
            try {
                danhSachSinhVien = new ArrayList<>(sinhVienDAO.getAll());
            } catch (SQLException e) {
                throw new BusinessException("Lỗi lấy danh sách sinh viên: " + e.getMessage(), e);
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
            throw new BusinessException("Lỗi lấy sinh viên theo mã: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy thông tin sinh viên (alias cho getById)
     */
    public SinhVienDTO getThongTin(int maSV) {
        return getById(maSV);
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
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm sinh viên mới: " + e.getMessage(), e);
        }
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
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật sinh viên: " + e.getMessage(), e);
        }
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
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi xóa sinh viên: " + e.getMessage(), e);
        }
    }

    /**
     * Reset mật khẩu
     */
    public boolean resetMatKhau(int maSV, String matKhauMoi) {
        try {
            return sinhVienDAO.updatePassword(maSV, matKhauMoi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi reset mật khẩu sinh viên: " + e.getMessage(), e);
        }
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
            throw new BusinessException("Lỗi đổi mật khẩu sinh viên: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm sinh viên
     */
    public List<SinhVienDTO> timKiem(String keyword) {
        try {
            return sinhVienDAO.search(keyword);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi tìm kiếm sinh viên: " + e.getMessage(), e);
        }
    }

    public static void reloadCache() {
        danhSachSinhVien = null;
    }
}
