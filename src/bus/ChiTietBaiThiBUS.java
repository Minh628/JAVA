/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: ChiTietBaiThiBUS - Xử lý logic nghiệp vụ Chi tiết bài thi
 * CHỈ gọi ChiTietBaiThiDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.ChiTietBaiThiDAO;
import dto.ChiTietBaiThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietBaiThiBUS {
    private ChiTietBaiThiDAO chiTietBaiThiDAO;

    public ChiTietBaiThiBUS() {
        this.chiTietBaiThiDAO = new ChiTietBaiThiDAO();
    }

    /**
     * Lấy chi tiết bài thi theo mã bài thi
     */
    public List<ChiTietBaiThiDTO> getByBaiThi(int maBaiThi) {
        try {
            return chiTietBaiThiDAO.getByBaiThi(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Thêm một chi tiết bài thi
     */
    public boolean them(ChiTietBaiThiDTO chiTiet) {
        try {
            return chiTietBaiThiDAO.insert(chiTiet);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm nhiều chi tiết bài thi
     */
    public boolean themBatch(List<ChiTietBaiThiDTO> danhSach) {
        try {
            return chiTietBaiThiDAO.insertBatch(danhSach);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật đáp án sinh viên
     */
    public boolean capNhatDapAn(int maBaiThi, int maCauHoi, String dapAnSV) {
        try {
            return chiTietBaiThiDAO.updateDapAn(maBaiThi, maCauHoi, dapAnSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa chi tiết bài thi theo mã bài thi
     */
    public boolean xoaByBaiThi(int maBaiThi) {
        try {
            return chiTietBaiThiDAO.deleteByBaiThi(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
            if (ct.isLaDung()) {
                soCauDung++;
            } else {
                soCauSai++;
            }
        }

        int tongSoCau = soCauDung + soCauSai;
        float diemSo = tongSoCau > 0 ? (float) soCauDung / tongSoCau * 10 : 0;

        return new float[]{soCauDung, soCauSai, diemSo};
    }
}
