/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: SinhVienBUS - Xử lý logic nghiệp vụ sinh viên
 */
package bus;

import dao.BaiThiDAO;
import dao.DeThiDAO;
import dao.KyThiDAO;
import dao.SinhVienDAO;
import dto.BaiThiDTO;
import dto.DeThiDTO;
import dto.KyThiDTO;
import dto.SinhVienDTO;
import java.sql.SQLException;
import java.util.List;

public class SinhVienBUS {
    private SinhVienDAO sinhVienDAO;
    private BaiThiDAO baiThiDAO;
    private DeThiDAO deThiDAO;
    private KyThiDAO kyThiDAO;
    
    public SinhVienBUS() {
        this.sinhVienDAO = new SinhVienDAO();
        this.baiThiDAO = new BaiThiDAO();
        this.deThiDAO = new DeThiDAO();
        this.kyThiDAO = new KyThiDAO();
    }
    
    /**
     * Lấy thông tin sinh viên
     */
    public SinhVienDTO getThongTin(int maSV) {
        try {
            return sinhVienDAO.getById(maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Cập nhật thông tin sinh viên
     */
    public boolean capNhatThongTin(SinhVienDTO sinhVien) {
        try {
            return sinhVienDAO.update(sinhVien);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy lịch sử bài thi
     */
    public List<BaiThiDTO> getLichSuBaiThi(int maSV) {
        try {
            return baiThiDAO.getBySinhVien(maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy danh sách kỳ thi đang diễn ra
     */
    public List<KyThiDTO> getKyThiDangDienRa() {
        try {
            return kyThiDAO.getKyThiDangDienRa();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy danh sách đề thi trong kỳ thi
     */
    public List<DeThiDTO> getDeThiTrongKyThi(int maKyThi) {
        try {
            return deThiDAO.getByKyThi(maKyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Kiểm tra sinh viên đã thi đề này chưa
     */
    public boolean daDuThi(int maDeThi, int maSV) {
        try {
            return baiThiDAO.checkDaThi(maDeThi, maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Mặc định trả về true để an toàn
        }
    }
    
    /**
     * Lấy chi tiết đề thi
     */
    public DeThiDTO getChiTietDeThi(int maDeThi) {
        try {
            return deThiDAO.getById(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy chi tiết bài thi
     */
    public BaiThiDTO getChiTietBaiThi(int maBaiThi) {
        try {
            return baiThiDAO.getById(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Đổi mật khẩu sinh viên
     */
    public boolean doiMatKhau(int maSV, String matKhauCu, String matKhauMoi) {
        try {
            // Kiểm tra mật khẩu cũ
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
}
