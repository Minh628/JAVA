/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: ChiTietBaiThiBUS - Xử lý logic nghiệp vụ Chi tiết bài thi
 */
package bus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.CauHoiDAO;
import dao.ChiTietBaiThiDAO;
import dto.CauHoiDKDTO;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;
import dto.ChiTietBaiThiDTO;

public class ChiTietBaiThiBUS {
    private ChiTietBaiThiDAO chiTietBaiThiDAO;
    private CauHoiDAO cauHoiDAO;

    public ChiTietBaiThiBUS() {
        this.chiTietBaiThiDAO = new ChiTietBaiThiDAO();
        this.cauHoiDAO = new CauHoiDAO();
    }

    /**
     * Lấy chi tiết bài thi theo mã bài thi
     */
    public List<ChiTietBaiThiDTO> getChiTietByBaiThi(int maBaiThi) {
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
    public boolean themChiTiet(ChiTietBaiThiDTO chiTiet) {
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
    public boolean themChiTietBatch(List<ChiTietBaiThiDTO> danhSach) {
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
    public boolean capNhatDapAnChiTiet(int maBaiThi, int maCauHoi, String dapAnSV) {
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
    public boolean xoaChiTietByBaiThi(int maBaiThi) {
        try {
            return chiTietBaiThiDAO.deleteByBaiThi(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả chi tiết bài thi theo sinh viên
     */
    public boolean xoaChiTietBySinhVien(int maSV) {
        try {
            return chiTietBaiThiDAO.deleteBySinhVien(maSV);
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
        List<ChiTietBaiThiDTO> chiTiet = getChiTietByBaiThi(maBaiThi);
        int soCauDung = 0;
        int soCauSai = 0;

        for (ChiTietBaiThiDTO ct : chiTiet) {
            try {
                CauHoiDTO cauHoi = cauHoiDAO.getById(ct.getMaCauHoi());
                if (cauHoi != null && kiemTraDapAn(cauHoi, ct.getDapAnSV())) {
                    soCauDung++;
                } else {
                    soCauSai++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
            String noiDungDapAnSV = null;

            // Nếu đáp án là ký hiệu A/B/C/D, chuyển đổi sang nội dung
            String dapAnUpper = dapAnSV.trim().toUpperCase();
            if (dapAnUpper.equals("A") || dapAnUpper.equals("B")
                    || dapAnUpper.equals("C") || dapAnUpper.equals("D")) {
                switch (dapAnUpper) {
                    case "A":
                        noiDungDapAnSV = mc.getNoiDungA();
                        break;
                    case "B":
                        noiDungDapAnSV = mc.getNoiDungB();
                        break;
                    case "C":
                        noiDungDapAnSV = mc.getNoiDungC();
                        break;
                    case "D":
                        noiDungDapAnSV = mc.getNoiDungD();
                        break;
                }
            } else {
                // Nếu đáp án là nội dung trực tiếp, so sánh trực tiếp
                noiDungDapAnSV = dapAnSV.trim();
            }

            return noiDungDapAnSV != null && dapAnDung != null
                    && noiDungDapAnSV.trim().equalsIgnoreCase(dapAnDung.trim());
        } else if (cauHoi instanceof CauHoiDKDTO) {
            CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
            return dk.kiemTraDapAn(dapAnSV);
        }
        return false;
    }
}
