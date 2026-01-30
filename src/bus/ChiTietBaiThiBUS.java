/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: ChiTietBaiThiBUS - Xử lý logic nghiệp vụ Chi tiết bài thi
 * Tuân thủ nguyên tắc 3 tầng: BUS chỉ gọi BUS khác hoặc DAO tương ứng
 * - ChiTietBaiThiDAO: truy cập dữ liệu chi tiết bài thi
 * - CauHoiBUS: lấy thông tin câu hỏi (thay vì gọi CauHoiDAO trực tiếp)
 */
package bus;

import dao.ChiTietBaiThiDAO;
import dto.CauHoiDKDTO;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;
import dto.ChiTietBaiThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietBaiThiBUS {
    private ChiTietBaiThiDAO chiTietBaiThiDAO;
    private CauHoiBUS cauHoiBUS;

    public ChiTietBaiThiBUS() {
        this.chiTietBaiThiDAO = new ChiTietBaiThiDAO();
        this.cauHoiBUS = new CauHoiBUS();
    }

    /**
     * Lấy chi tiết bài thi theo mã bài thi
     */
    public List<ChiTietBaiThiDTO> getByBaiThi(int maBaiThi) {
        try {
            return chiTietBaiThiDAO.getByBaiThi(maBaiThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy chi tiết bài thi: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm một chi tiết bài thi
     */
    public boolean them(ChiTietBaiThiDTO chiTiet) {
        try {
            return chiTietBaiThiDAO.insert(chiTiet);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm chi tiết bài thi: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm nhiều chi tiết bài thi
     */
    public boolean themBatch(List<ChiTietBaiThiDTO> danhSach) {
        try {
            return chiTietBaiThiDAO.insertBatch(danhSach);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm batch chi tiết bài thi: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật đáp án sinh viên
     */
    public boolean capNhatDapAn(int maBaiThi, int maCauHoi, String dapAnSV) {
        try {
            return chiTietBaiThiDAO.updateDapAn(maBaiThi, maCauHoi, dapAnSV);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật đáp án: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa chi tiết bài thi theo mã bài thi
     */
    public boolean xoaByBaiThi(int maBaiThi) {
        try {
            return chiTietBaiThiDAO.deleteByBaiThi(maBaiThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi xóa chi tiết bài thi: " + e.getMessage(), e);
        }
    }

    /**
     * Tính điểm bài thi từ chi tiết
     * @return mảng [soCauDung, soCauSai, diemSo]
     */
    public float[] tinhDiem(int maBaiThi) {
        List<ChiTietBaiThiDTO> chiTiet = getByBaiThi(maBaiThi);
        int soCauDung = 0;
        int soCauSai = 0;

        for (ChiTietBaiThiDTO ct : chiTiet) {
            // Sử dụng CauHoiBUS thay vì CauHoiDAO để tuân thủ kiến trúc 3 tầng
            CauHoiDTO cauHoi = cauHoiBUS.getById(ct.getMaCauHoi());
            if (cauHoi != null && kiemTraDapAn(cauHoi, ct.getDapAnSV())) {
                soCauDung++;
            } else {
                soCauSai++;
            }
        }

        int tongSoCau = soCauDung + soCauSai;
        float diemSo = tongSoCau > 0 ? (float) soCauDung / tongSoCau * 10 : 0;

        return new float[]{soCauDung, soCauSai, diemSo};
    }

    /**
     * Kiểm tra đáp án sinh viên so với đáp án đúng
     */
    private boolean kiemTraDapAn(CauHoiDTO cauHoi, String dapAnSV) {
        if (dapAnSV == null || dapAnSV.trim().isEmpty()) {
            return false;
        }

        if (cauHoi instanceof CauHoiMCDTO) {
            CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
            String dapAnDung = mc.getNoiDungDung();
            // Lấy nội dung đáp án tương ứng với lựa chọn A/B/C/D
            String noiDungDapAnSV = null;
            switch (dapAnSV.toUpperCase()) {
                case "A": noiDungDapAnSV = mc.getNoiDungA(); break;
                case "B": noiDungDapAnSV = mc.getNoiDungB(); break;
                case "C": noiDungDapAnSV = mc.getNoiDungC(); break;
                case "D": noiDungDapAnSV = mc.getNoiDungD(); break;
            }
            return noiDungDapAnSV != null && dapAnDung != null && 
                   noiDungDapAnSV.trim().equalsIgnoreCase(dapAnDung.trim());
        } else if (cauHoi instanceof CauHoiDKDTO) {
            CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
            String dapAnDung = dk.getNoiDungDung();
            return dapAnDung != null && dapAnSV.trim().equalsIgnoreCase(dapAnDung.trim());
        }
        return false;
    }
}
