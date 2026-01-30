/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: DangNhapBUS - Xử lý xác thực đăng nhập
 * (Thay thế AuthBUS cũ)
 */
package bus;

import dao.DangNhapDAO;
import dto.GiangVienDTO;
import dto.SinhVienDTO;
import dto.VaiTroDTO;
import java.sql.SQLException;

public class DangNhapBUS {
    private DangNhapDAO dangNhapDAO;

    public DangNhapBUS() {
        this.dangNhapDAO = new DangNhapDAO();
    }

    /**
     * Đăng nhập hệ thống
     * @return Object (GiangVienDTO hoặc SinhVienDTO) nếu thành công, null nếu thất bại
     */
    public Object dangNhap(String tenDangNhap, String matKhau) {
        try {
            // Thử đăng nhập với tài khoản giảng viên/trưởng khoa
            GiangVienDTO giangVien = dangNhapDAO.getGiangVienByTenDangNhap(tenDangNhap);
            if (giangVien != null && kiemTraMatKhau(matKhau, giangVien.getMatKhau())) {
                return giangVien;
            }

            // Thử đăng nhập với tài khoản sinh viên
            SinhVienDTO sinhVien = dangNhapDAO.getSinhVienByTenDangNhap(tenDangNhap);
            if (sinhVien != null && kiemTraMatKhau(matKhau, sinhVien.getMatKhau())) {
                return sinhVien;
            }
        } catch (SQLException e) {
            throw new BusinessException("Lỗi đăng nhập: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Kiểm tra mật khẩu
     */
    private boolean kiemTraMatKhau(String matKhauNhap, String matKhauDB) {
        return matKhauNhap != null && matKhauNhap.equals(matKhauDB);
    }

    /**
     * Lấy vai trò của user
     */
    public int getVaiTro(Object user) {
        if (user instanceof GiangVienDTO) {
            return ((GiangVienDTO) user).getMaVaiTro();
        } else if (user instanceof SinhVienDTO) {
            return VaiTroDTO.SINH_VIEN;
        }
        return -1;
    }

    /**
     * Kiểm tra có phải trưởng khoa
     */
    public boolean laTruongKhoa(Object user) {
        return user instanceof GiangVienDTO && 
               ((GiangVienDTO) user).getMaVaiTro() == VaiTroDTO.ADMIN;
    }

    /**
     * Kiểm tra có phải giảng viên
     */
    public boolean laGiangVien(Object user) {
        return user instanceof GiangVienDTO && 
               ((GiangVienDTO) user).getMaVaiTro() == VaiTroDTO.GIANG_VIEN;
    }

    /**
     * Kiểm tra có phải sinh viên
     */
    public boolean laSinhVien(Object user) {
        return user instanceof SinhVienDTO;
    }

    /**
     * Đổi mật khẩu
     */
    public boolean doiMatKhau(Object user, String matKhauCu, String matKhauMoi) {
        try {
            if (user instanceof GiangVienDTO) {
                GiangVienDTO gv = (GiangVienDTO) user;
                if (kiemTraMatKhau(matKhauCu, gv.getMatKhau())) {
                    boolean result = dangNhapDAO.updatePasswordGV(gv.getMaGV(), matKhauMoi);
                    if (result) {
                        gv.setMatKhau(matKhauMoi); // Cập nhật object hiện tại
                    }
                    return result;
                }
            } else if (user instanceof SinhVienDTO) {
                SinhVienDTO sv = (SinhVienDTO) user;
                if (kiemTraMatKhau(matKhauCu, sv.getMatKhau())) {
                    boolean result = dangNhapDAO.updatePasswordSV(sv.getMaSV(), matKhauMoi);
                    if (result) {
                        sv.setMatKhau(matKhauMoi);
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new BusinessException("Lỗi đổi mật khẩu: " + e.getMessage(), e);
        }
        return false;
    }
}
