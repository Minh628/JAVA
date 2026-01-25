/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: SinhVienThiBUS - Xử lý logic làm bài thi
 */
package bus;

import dao.BaiThiDAO;
import dao.CauHoiDAO;
import dao.DeThiDAO;
import dao.KetQuaDAO;
import dto.BaiThiDTO;
import dto.CauHoiDTO;
import dto.ChiTietBaiThiDTO;
import dto.DeThiDTO;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class SinhVienThiBUS {
    private BaiThiDAO baiThiDAO;
    private CauHoiDAO cauHoiDAO;
    private DeThiDAO deThiDAO;
    private KetQuaDAO ketQuaDAO;
    
    public SinhVienThiBUS() {
        this.baiThiDAO = new BaiThiDAO();
        this.cauHoiDAO = new CauHoiDAO();
        this.deThiDAO = new DeThiDAO();
        this.ketQuaDAO = new KetQuaDAO();
    }
    
    /**
     * Bắt đầu làm bài thi
     * @return Mã bài thi mới, -1 nếu thất bại
     */
    public int batDauLamBai(int maDeThi, int maSV) {
        try {
            // Kiểm tra đã thi chưa
            if (baiThiDAO.checkDaThi(maDeThi, maSV)) {
                return -1;
            }
            
            // Tạo bài thi mới
            BaiThiDTO baiThi = new BaiThiDTO();
            baiThi.setMaDeThi(maDeThi);
            baiThi.setMaSV(maSV);
            baiThi.setThoiGianBatDau(new Timestamp(System.currentTimeMillis()));
            baiThi.setNgayThi(new Date(System.currentTimeMillis()));
            baiThi.setSoCauDung(0);
            baiThi.setSoCauSai(0);
            baiThi.setDiemSo(0);
            
            if (baiThiDAO.insert(baiThi)) {
                return baiThi.getMaBaiThi();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Lấy danh sách câu hỏi cho đề thi
     */
    public List<CauHoiDTO> getCauHoiDeThi(int maDeThi) {
        try {
            // Lấy thông tin đề thi
            DeThiDTO deThi = deThiDAO.getById(maDeThi);
            if (deThi == null) return null;
            
            // Lấy câu hỏi theo môn học của đề thi
            List<CauHoiDTO> tatCaCauHoi = cauHoiDAO.getByMon(deThi.getMaHocPhan());
            
            // Giới hạn số câu hỏi theo đề thi
            if (tatCaCauHoi.size() > deThi.getSoCauHoi()) {
                return tatCaCauHoi.subList(0, deThi.getSoCauHoi());
            }
            return tatCaCauHoi;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lưu đáp án sinh viên
     */
    public boolean luuDapAn(int maBaiThi, int maCauHoi, String dapAn) {
        try {
            return ketQuaDAO.updateDapAn(maBaiThi, maCauHoi, dapAn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Nộp bài thi
     */
    public BaiThiDTO nopBai(int maBaiThi, List<ChiTietBaiThiDTO> danhSachDapAn) {
        try {
            // Lưu tất cả đáp án
            for (ChiTietBaiThiDTO ct : danhSachDapAn) {
                ct.setMaBaiThi(maBaiThi);
            }
            ketQuaDAO.insertChiTietBatch(danhSachDapAn);
            
            // Tính điểm
            BaiThiDTO baiThi = baiThiDAO.getById(maBaiThi);
            List<ChiTietBaiThiDTO> chiTiet = ketQuaDAO.getChiTietBaiThi(maBaiThi);
            
            int soCauDung = 0;
            int soCauSai = 0;
            
            for (ChiTietBaiThiDTO ct : chiTiet) {
                if (ct.isLaDung()) {
                    soCauDung++;
                } else {
                    soCauSai++;
                }
            }
            
            // Tính điểm (thang 10)
            int tongSoCau = soCauDung + soCauSai;
            float diemSo = tongSoCau > 0 ? (float) soCauDung / tongSoCau * 10 : 0;
            
            // Cập nhật kết quả
            baiThi.setThoiGianNop(new Timestamp(System.currentTimeMillis()));
            baiThi.setSoCauDung(soCauDung);
            baiThi.setSoCauSai(soCauSai);
            baiThi.setDiemSo(diemSo);
            
            baiThiDAO.updateKetQua(baiThi);
            
            return baiThi;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy chi tiết kết quả bài thi
     */
    public List<ChiTietBaiThiDTO> getChiTietKetQua(int maBaiThi) {
        try {
            return ketQuaDAO.getChiTietBaiThi(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy thông tin đề thi
     */
    public DeThiDTO getThongTinDeThi(int maDeThi) {
        try {
            return deThiDAO.getById(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy thông tin bài thi
     */
    public BaiThiDTO getThongTinBaiThi(int maBaiThi) {
        try {
            return baiThiDAO.getById(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
