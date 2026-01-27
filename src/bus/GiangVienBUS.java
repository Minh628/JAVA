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
import dto.GiangVienDTO;
import dto.VaiTroDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.PasswordEncoder;

public class GiangVienBUS {
    private GiangVienDAO giangVienDAO;

    // Cache
    private static ArrayList<GiangVienDTO> danhSachGiangVien = null;

    public GiangVienBUS() {
        this.giangVienDAO = new GiangVienDAO();
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
            String hashedOld = PasswordEncoder.encode(matKhauCu);
            if (!hashedOld.equals(gv.getMatKhau())) {
                return false;
            }
            
            // Cập nhật mật khẩu mới
            return giangVienDAO.updatePassword(maGV, PasswordEncoder.encode(matKhauMoi));
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

    public static void reloadCache() {
        danhSachGiangVien = null;
    }
}
