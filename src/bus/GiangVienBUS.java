/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: GiangVienBUS - Xử lý logic nghiệp vụ giảng viên
 */
package bus;

import dao.CauHoiDAO;
import dao.DeThiDAO;
import dao.GiangVienDAO;
import dao.HocPhanDAO;
import dao.KyThiDAO;
import dto.CauHoiDTO;
import dto.DeThiDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import dto.KyThiDTO;
import java.sql.SQLException;
import java.util.List;

public class GiangVienBUS {
    private GiangVienDAO giangVienDAO;
    private CauHoiDAO cauHoiDAO;
    private DeThiDAO deThiDAO;
    private HocPhanDAO hocPhanDAO;
    private KyThiDAO kyThiDAO;
    
    public GiangVienBUS() {
        this.giangVienDAO = new GiangVienDAO();
        this.cauHoiDAO = new CauHoiDAO();
        this.deThiDAO = new DeThiDAO();
        this.hocPhanDAO = new HocPhanDAO();
        this.kyThiDAO = new KyThiDAO();
    }
    
    // ==================== QUẢN LÝ CÂU HỎI ====================
    
    /**
     * Lấy danh sách câu hỏi của giảng viên
     */
    public List<CauHoiDTO> getDanhSachCauHoi(int maGV) {
        try {
            return cauHoiDAO.getByGiangVien(maGV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy câu hỏi theo môn
     */
    public List<CauHoiDTO> getCauHoiTheoMon(int maMon) {
        try {
            return cauHoiDAO.getByMon(maMon);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm câu hỏi mới
     */
    public boolean themCauHoi(CauHoiDTO cauHoi) {
        try {
            return cauHoiDAO.insert(cauHoi);
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
            return cauHoiDAO.update(cauHoi);
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
            return cauHoiDAO.delete(maCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy câu hỏi theo ID
     */
    public CauHoiDTO getCauHoiById(int maCauHoi) {
        try {
            return cauHoiDAO.getById(maCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== QUẢN LÝ ĐỀ THI ====================
    
    /**
     * Lấy danh sách đề thi của giảng viên
     */
    public List<DeThiDTO> getDanhSachDeThi(int maGV) {
        try {
            return deThiDAO.getByGiangVien(maGV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm đề thi mới
     */
    public boolean themDeThi(DeThiDTO deThi) {
        try {
            return deThiDAO.insert(deThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật đề thi
     */
    public boolean capNhatDeThi(DeThiDTO deThi) {
        try {
            return deThiDAO.update(deThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa đề thi
     */
    public boolean xoaDeThi(int maDeThi) {
        try {
            return deThiDAO.delete(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== DANH MỤC ====================
    
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
    
    // ==================== THÔNG TIN CÁ NHÂN ====================
    
    /**
     * Lấy thông tin giảng viên
     */
    public GiangVienDTO getThongTin(int maGV) {
        try {
            return giangVienDAO.getById(maGV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Cập nhật thông tin cá nhân
     */
    public boolean capNhatThongTin(GiangVienDTO giangVien) {
        try {
            return giangVienDAO.update(giangVien);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Đổi mật khẩu giảng viên
     */
    public boolean doiMatKhau(int maGV, String matKhauCu, String matKhauMoi) {
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
}
