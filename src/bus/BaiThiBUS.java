/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: BaiThiBUS - Xử lý logic nghiệp vụ Bài thi
 * CHỈ gọi BaiThiDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.BaiThiDAO;
import dto.BaiThiDTO;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BaiThiBUS {
    private BaiThiDAO baiThiDAO;

    public BaiThiBUS() {
        this.baiThiDAO = new BaiThiDAO();
    }

    /**
     * Bắt đầu làm bài thi - tạo bài thi mới
     * @return Mã bài thi mới, -1 nếu đã thi hoặc thất bại
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
            throw new BusinessException("Lỗi bắt đầu làm bài: " + e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Cập nhật kết quả bài thi
     */
    public boolean capNhatKetQua(BaiThiDTO baiThi) {
        try {
            return baiThiDAO.updateKetQua(baiThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật kết quả: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật kết quả sau khi chấm điểm
     */
    public boolean capNhatKetQua(int maBaiThi, int soCauDung, int soCauSai, float diemSo) {
        try {
            BaiThiDTO baiThi = baiThiDAO.getById(maBaiThi);
            if (baiThi != null) {
                baiThi.setThoiGianNop(new Timestamp(System.currentTimeMillis()));
                baiThi.setSoCauDung(soCauDung);
                baiThi.setSoCauSai(soCauSai);
                baiThi.setDiemSo(diemSo);
                return baiThiDAO.updateKetQua(baiThi);
            }
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật kết quả: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Lấy lịch sử bài thi của sinh viên
     */
    public List<BaiThiDTO> getLichSuBaiThi(int maSV) {
        try {
            return baiThiDAO.getBySinhVien(maSV);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy lịch sử bài thi: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách bài thi theo đề thi
     */
    public List<BaiThiDTO> getBaiThiTheoDeThi(int maDeThi) {
        try {
            return baiThiDAO.getByDeThi(maDeThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy bài thi theo đề: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy bài thi theo mã
     */
    public BaiThiDTO getById(int maBaiThi) {
        try {
            return baiThiDAO.getById(maBaiThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy bài thi: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra sinh viên đã thi đề này chưa
     */
    public boolean daDuThi(int maDeThi, int maSV) {
        try {
            return baiThiDAO.checkDaThi(maDeThi, maSV);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi kiểm tra đã thi: " + e.getMessage(), e);
        }
    }

    /**
     * Đếm số bài thi theo đề thi
     */
    public int demBaiThiTheoDeThi(int maDeThi) {
        try {
            return baiThiDAO.countByDeThi(maDeThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi đếm bài thi: " + e.getMessage(), e);
        }
    }
}
