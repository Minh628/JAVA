/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: ChiTietDeThiBUS - Xử lý logic nghiệp vụ Chi tiết đề thi
 * CHỈ gọi ChiTietDeThiDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.ChiTietDeThiDAO;
import dto.ChiTietDeThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDeThiBUS {
    private ChiTietDeThiDAO chiTietDeThiDAO;

    public ChiTietDeThiBUS() {
        this.chiTietDeThiDAO = new ChiTietDeThiDAO();
    }

    /**
     * Lấy danh sách chi tiết đề thi theo mã đề thi
     */
    public List<ChiTietDeThiDTO> getByDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.getByDeThi(maDeThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy danh sách chi tiết đề thi theo mã đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách mã câu hỏi trong đề thi
     */
    public List<Integer> getMaCauHoiByDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.getMaCauHoiByDeThi(maDeThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy danh sách mã câu hỏi theo mã đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm một câu hỏi vào đề thi
     */
    public boolean themCauHoiVaoDeThi(ChiTietDeThiDTO chiTiet) {
        try {
            return chiTietDeThiDAO.insert(chiTiet);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm câu hỏi vào đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm nhiều câu hỏi vào đề thi
     */
    public boolean themNhieuCauHoiVaoDeThi(int maDeThi, List<Integer> danhSachMaCauHoi) {
        try {
            List<ChiTietDeThiDTO> chiTietList = new ArrayList<>();
            int thuTu = chiTietDeThiDAO.getMaxThuTu(maDeThi) + 1;
            for (int maCauHoi : danhSachMaCauHoi) {
                ChiTietDeThiDTO ct = new ChiTietDeThiDTO();
                ct.setMaDeThi(maDeThi);
                ct.setMaCauHoi(maCauHoi);
                ct.setThuTu(thuTu++);
                chiTietList.add(ct);
            }
            return chiTietDeThiDAO.insertBatch(chiTietList);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm nhiều câu hỏi vào đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm batch chi tiết đề thi
     */
    public boolean insertBatch(List<ChiTietDeThiDTO> danhSach) {
        try {
            return chiTietDeThiDAO.insertBatch(danhSach);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm batch chi tiết đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa một câu hỏi khỏi đề thi
     */
    public boolean xoaCauHoiKhoiDeThi(int maDeThi, int maCauHoi) {
        try {
            return chiTietDeThiDAO.delete(maDeThi, maCauHoi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi xóa câu hỏi khỏi đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa tất cả câu hỏi trong đề thi
     */
    public boolean xoaTatCaCauHoiTrongDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.deleteByDeThi(maDeThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi xóa tất cả câu hỏi trong đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Đếm số câu hỏi trong đề thi
     */
    public int demCauHoiTrongDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.countByDeThi(maDeThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi đếm số câu hỏi trong đề thi: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy thứ tự lớn nhất trong đề thi
     */
    public int getMaxThuTu(int maDeThi) {
        try {
            return chiTietDeThiDAO.getMaxThuTu(maDeThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy thứ tự lớn nhất trong đề thi: " + e.getMessage(), e);
        }
    }
}
