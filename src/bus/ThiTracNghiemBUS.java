/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: ThiTracNghiemBUS - Xử lý logic nghiệp vụ thi trắc nghiệm
 */
package bus;

import dao.*;
import dto.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThiTracNghiemBUS {
    private CauHoiDAO cauHoiDAO;
    private DeThiDAO deThiDAO;
    private KyThiDAO kyThiDAO;
    
    public ThiTracNghiemBUS() {
        this.cauHoiDAO = new CauHoiDAO();
        this.deThiDAO = new DeThiDAO();
        this.kyThiDAO = new KyThiDAO();
    }
    
    /**
     * Tạo đề thi tự động
     * @param maHocPhan Mã học phần
     * @param maKyThi Mã kỳ thi
     * @param maGV Mã giảng viên
     * @param tenDeThi Tên đề thi
     * @param soCauDe Số câu dễ
     * @param soCauTB Số câu trung bình
     * @param soCauKho Số câu khó
     * @param thoiGianLam Thời gian làm bài (phút)
     * @return DeThiDTO đã tạo, null nếu thất bại
     */
    public DeThiDTO taoDeThi(int maHocPhan, int maKyThi, int maGV, 
            String tenDeThi, int soCauDe, int soCauTB, int soCauKho, int thoiGianLam) {
        try {
            // Lấy câu hỏi theo mức độ
            List<CauHoiDTO> cauHoiDe = cauHoiDAO.getByMucDo(maHocPhan, CauHoiDTO.MUC_DO_DE);
            List<CauHoiDTO> cauHoiTB = cauHoiDAO.getByMucDo(maHocPhan, CauHoiDTO.MUC_DO_TRUNG_BINH);
            List<CauHoiDTO> cauHoiKho = cauHoiDAO.getByMucDo(maHocPhan, CauHoiDTO.MUC_DO_KHO);
            
            // Kiểm tra đủ câu hỏi không
            if (cauHoiDe.size() < soCauDe || cauHoiTB.size() < soCauTB || cauHoiKho.size() < soCauKho) {
                return null; // Không đủ câu hỏi
            }
            
            // Trộn ngẫu nhiên và lấy số lượng cần thiết
            Collections.shuffle(cauHoiDe);
            Collections.shuffle(cauHoiTB);
            Collections.shuffle(cauHoiKho);
            
            List<CauHoiDTO> danhSachCauHoi = new ArrayList<>();
            danhSachCauHoi.addAll(cauHoiDe.subList(0, soCauDe));
            danhSachCauHoi.addAll(cauHoiTB.subList(0, soCauTB));
            danhSachCauHoi.addAll(cauHoiKho.subList(0, soCauKho));
            
            // Trộn lại toàn bộ
            Collections.shuffle(danhSachCauHoi);
            
            // Tạo đề thi
            DeThiDTO deThi = new DeThiDTO();
            deThi.setMaHocPhan(maHocPhan);
            deThi.setMaKyThi(maKyThi);
            deThi.setMaGV(maGV);
            deThi.setTenDeThi(tenDeThi);
            deThi.setThoiGianLam(thoiGianLam);
            deThi.setSoCauHoi(danhSachCauHoi.size());
            
            if (deThiDAO.insert(deThi)) {
                return deThi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
     * Lấy danh sách đề thi theo kỳ thi
     */
    public List<DeThiDTO> getDeThiTheoKyThi(int maKyThi) {
        try {
            return deThiDAO.getByKyThi(maKyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thống kê số câu hỏi theo mức độ
     */
    public int[] thongKeCauHoiTheoMucDo(int maHocPhan) {
        int[] thongKe = new int[3]; // [Dễ, TB, Khó]
        try {
            List<CauHoiDTO> cauHoiDe = cauHoiDAO.getByMucDo(maHocPhan, CauHoiDTO.MUC_DO_DE);
            List<CauHoiDTO> cauHoiTB = cauHoiDAO.getByMucDo(maHocPhan, CauHoiDTO.MUC_DO_TRUNG_BINH);
            List<CauHoiDTO> cauHoiKho = cauHoiDAO.getByMucDo(maHocPhan, CauHoiDTO.MUC_DO_KHO);
            
            thongKe[0] = cauHoiDe.size();
            thongKe[1] = cauHoiTB.size();
            thongKe[2] = cauHoiKho.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thongKe;
    }
}
