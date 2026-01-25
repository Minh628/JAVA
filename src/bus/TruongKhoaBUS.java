/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: TruongKhoaBUS - Xử lý logic nghiệp vụ trưởng khoa
 */
package bus;

import dao.*;
import dto.*;
import java.sql.SQLException;
import java.util.List;

public class TruongKhoaBUS {
    private GiangVienDAO giangVienDAO;
    private SinhVienDAO sinhVienDAO;
    private KhoaDAO khoaDAO;
    private NganhDAO nganhDAO;
    private HocPhanDAO hocPhanDAO;
    private KyThiDAO kyThiDAO;
    private DeThiDAO deThiDAO;
    private BaiThiDAO baiThiDAO;
    
    public TruongKhoaBUS() {
        this.giangVienDAO = new GiangVienDAO();
        this.sinhVienDAO = new SinhVienDAO();
        this.khoaDAO = new KhoaDAO();
        this.nganhDAO = new NganhDAO();
        this.hocPhanDAO = new HocPhanDAO();
        this.kyThiDAO = new KyThiDAO();
        this.deThiDAO = new DeThiDAO();
        this.baiThiDAO = new BaiThiDAO();
    }
    
    // ==================== QUẢN LÝ GIẢNG VIÊN ====================
    
    /**
     * Lấy danh sách giảng viên
     */
    public List<GiangVienDTO> getDanhSachGiangVien() {
        try {
            return giangVienDAO.getAllGiangVien();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy giảng viên theo khoa
     */
    public List<GiangVienDTO> getGiangVienTheoKhoa(int maKhoa) {
        try {
            return giangVienDAO.getByKhoa(maKhoa);
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
            // Kiểm tra tên đăng nhập đã tồn tại
            if (giangVienDAO.checkTenDangNhapExists(giangVien.getTenDangNhap())) {
                return false;
            }
            // Mã hóa mật khẩu
            giangVien.setMatKhau(giangVien.getMatKhau());
            giangVien.setMaVaiTro(VaiTroDTO.GIANG_VIEN);
            return giangVienDAO.insert(giangVien);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật giảng viên
     */
    public boolean capNhatGiangVien(GiangVienDTO giangVien) {
        try {
            return giangVienDAO.update(giangVien);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa giảng viên
     */
    public boolean xoaGiangVien(int maGV) {
        try {
            return giangVienDAO.delete(maGV);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reset mật khẩu giảng viên
     */
    public boolean resetMatKhauGV(int maGV, String matKhauMoi) {
        try {
            return giangVienDAO.updatePassword(maGV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== QUẢN LÝ SINH VIÊN ====================
    
    /**
     * Lấy danh sách sinh viên
     */
    public List<SinhVienDTO> getDanhSachSinhVien() {
        try {
            return sinhVienDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy sinh viên theo ngành
     */
    public List<SinhVienDTO> getSinhVienTheoNganh(int maNganh) {
        try {
            return sinhVienDAO.getByNganh(maNganh);
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
            // Kiểm tra tên đăng nhập đã tồn tại
            if (sinhVienDAO.checkTenDangNhapExists(sinhVien.getTenDangNhap())) {
                return false;
            }
            // Mã hóa mật khẩu
            sinhVien.setMatKhau(sinhVien.getMatKhau());
            return sinhVienDAO.insert(sinhVien);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật sinh viên
     */
    public boolean capNhatSinhVien(SinhVienDTO sinhVien) {
        try {
            return sinhVienDAO.update(sinhVien);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa sinh viên
     */
    public boolean xoaSinhVien(int maSV) {
        try {
            return sinhVienDAO.delete(maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reset mật khẩu sinh viên
     */
    public boolean resetMatKhauSV(int maSV, String matKhauMoi) {
        try {
            return sinhVienDAO.updatePassword(maSV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== DANH MỤC ====================
    
    /**
     * Lấy danh sách khoa
     */
    public List<KhoaDTO> getDanhSachKhoa() {
        try {
            return khoaDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm khoa mới
     */
    public boolean themKhoa(KhoaDTO khoa) {
        try {
            return khoaDAO.insert(khoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật khoa
     */
    public boolean capNhatKhoa(KhoaDTO khoa) {
        try {
            return khoaDAO.update(khoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa khoa
     */
    public boolean xoaKhoa(int maKhoa) {
        try {
            return khoaDAO.delete(maKhoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy danh sách ngành
     */
    public List<NganhDTO> getDanhSachNganh() {
        try {
            return nganhDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm ngành mới
     */
    public boolean themNganh(NganhDTO nganh) {
        try {
            return nganhDAO.insert(nganh);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật ngành
     */
    public boolean capNhatNganh(NganhDTO nganh) {
        try {
            return nganhDAO.update(nganh);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa ngành
     */
    public boolean xoaNganh(int maNganh) {
        try {
            return nganhDAO.delete(maNganh);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy ngành theo khoa
     */
    public List<NganhDTO> getNganhTheoKhoa(int maKhoa) {
        try {
            return nganhDAO.getByKhoa(maKhoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy danh sách học phần
     */
    public List<HocPhanDTO> getDanhSachHocPhan() {
        try {
            return hocPhanDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm học phần mới
     */
    public boolean themHocPhan(HocPhanDTO hocPhan) {
        try {
            return hocPhanDAO.insert(hocPhan);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật học phần
     */
    public boolean capNhatHocPhan(HocPhanDTO hocPhan) {
        try {
            return hocPhanDAO.update(hocPhan);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa học phần
     */
    public boolean xoaHocPhan(int maHocPhan) {
        try {
            return hocPhanDAO.delete(maHocPhan);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Đổi mật khẩu giảng viên
     */
    public boolean doiMatKhauGiangVien(int maGV, String matKhauCu, String matKhauMoi) {
        try {
            // Kiểm tra mật khẩu cũ
            GiangVienDTO gv = giangVienDAO.getById(maGV);
            if (gv == null || !gv.getMatKhau().equals(matKhauCu)) {
                return false;
            }
            return giangVienDAO.updatePassword(maGV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== QUẢN LÝ KỲ THI ====================
    
    /**
     * Lấy danh sách kỳ thi
     */
    public List<KyThiDTO> getDanhSachKyThi() {
        try {
            return kyThiDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm kỳ thi mới
     */
    public boolean themKyThi(KyThiDTO kyThi) {
        try {
            return kyThiDAO.insert(kyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật kỳ thi
     */
    public boolean capNhatKyThi(KyThiDTO kyThi) {
        try {
            return kyThiDAO.update(kyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa kỳ thi
     */
    public boolean xoaKyThi(int maKyThi) {
        try {
            return kyThiDAO.delete(maKyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== THỐNG KÊ ====================
    
    /**
     * Lấy kết quả thi theo đề thi
     */
    public List<BaiThiDTO> getKetQuaTheoDeThi(int maDeThi) {
        try {
            return baiThiDAO.getByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy danh sách đề thi
     */
    public List<DeThiDTO> getDanhSachDeThi() {
        try {
            return deThiDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
