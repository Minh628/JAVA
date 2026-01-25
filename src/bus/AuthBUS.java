/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: AuthBUS - Xử lý xác thực đăng nhập
 */
package bus;

import dao.GiangVienDAO;
import dao.SinhVienDAO;
import dto.GiangVienDTO;
import dto.SinhVienDTO;
import dto.VaiTroDTO;
import java.sql.SQLException;

public class AuthBUS {
    private GiangVienDAO giangVienDAO;
    private SinhVienDAO sinhVienDAO;
    
    public AuthBUS() {
        this.giangVienDAO = new GiangVienDAO();
        this.sinhVienDAO = new SinhVienDAO();
    }
    
    /**
     * Đăng nhập hệ thống
     * @param tenDangNhap Tên đăng nhập
     * @param matKhau Mật khẩu
     * @return Object (GiangVienDTO hoặc SinhVienDTO) nếu thành công, null nếu thất bại
     */
    public Object dangNhap(String tenDangNhap, String matKhau) {
        try {
            // Thử đăng nhập với tài khoản giảng viên/trưởng khoa
            GiangVienDTO giangVien = giangVienDAO.getByTenDangNhap(tenDangNhap);
            if (giangVien != null && kiemTraMatKhau(matKhau, giangVien.getMatKhau())) {
                if (giangVien.isTrangThai()) {
                    return giangVien;
                }
            }
            
            // Thử đăng nhập với tài khoản sinh viên
            SinhVienDTO sinhVien = sinhVienDAO.getByTenDangNhap(tenDangNhap);
            if (sinhVien != null && kiemTraMatKhau(matKhau, sinhVien.getMatKhau())) {
                if (sinhVien.isTrangThai()) {
                    return sinhVien;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Kiểm tra mật khẩu
     */
    private boolean kiemTraMatKhau(String matKhauNhap, String matKhauDB) {
        // Kiểm tra mật khẩu plain text (cho đơn giản)
        if (matKhauNhap.equals(matKhauDB)) {
            return true;
        }
        return false;
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
        if (user instanceof GiangVienDTO) {
            return ((GiangVienDTO) user).getMaVaiTro() == VaiTroDTO.TRUONG_KHOA;
        }
        return false;
    }
    
    /**
     * Kiểm tra có phải giảng viên
     */
    public boolean laGiangVien(Object user) {
        if (user instanceof GiangVienDTO) {
            return ((GiangVienDTO) user).getMaVaiTro() == VaiTroDTO.GIANG_VIEN;
        }
        return false;
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
                    return giangVienDAO.updatePassword(gv.getMaGV(), matKhauMoi);
                }
            } else if (user instanceof SinhVienDTO) {
                SinhVienDTO sv = (SinhVienDTO) user;
                if (kiemTraMatKhau(matKhauCu, sv.getMatKhau())) {
                    return sinhVienDAO.updatePassword(sv.getMaSV(), matKhauMoi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
