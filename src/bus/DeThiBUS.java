/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: DeThiBUS - Xử lý logic nghiệp vụ Đề thi
 * CHỈ gọi DeThiDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.DeThiDAO;
import dto.DeThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeThiBUS {
    private DeThiDAO deThiDAO;

    // Cache theo giảng viên
    private static ArrayList<DeThiDTO> danhSachDeThi = null;
    private static int lastMaGV = -1;

    public DeThiBUS() {
        this.deThiDAO = new DeThiDAO();
    }

    /**
     * Lấy danh sách đề thi theo giảng viên
     */
    public List<DeThiDTO> getDanhSachDeThi(int maGV) {
        if (danhSachDeThi == null || lastMaGV != maGV) {
            try {
                danhSachDeThi = new ArrayList<>(deThiDAO.getByGiangVien(maGV));
                lastMaGV = maGV;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachDeThi;
    }

    /**
     * Lấy tất cả đề thi
     */
    public List<DeThiDTO> getAllDeThi() {
        try {
            return deThiDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy đề thi theo kỳ thi
     */
    public List<DeThiDTO> getDeThiTheoKyThi(int maKyThi) {
        try {
            return deThiDAO.getByKyThi(maKyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy đề thi theo kỳ thi và khoa (cho sinh viên)
     */
    public List<DeThiDTO> getDeThiTheoKyThiVaKhoa(int maKyThi, int maKhoa) {
        try {
            return deThiDAO.getByKyThiAndKhoa(maKyThi, maKhoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy đề thi theo mã
     */
    public DeThiDTO getById(int maDeThi) {
        try {
            return deThiDAO.getById(maDeThi);
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
            if (deThiDAO.insert(deThi)) {
                if (lastMaGV == deThi.getMaGV()) {
                    danhSachDeThi = new ArrayList<>(deThiDAO.getByGiangVien(deThi.getMaGV()));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật đề thi
     */
    public boolean capNhatDeThi(DeThiDTO deThi) {
        try {
            if (deThiDAO.update(deThi)) {
                DeThiDTO updated = deThiDAO.getById(deThi.getMaDeThi());
                if (danhSachDeThi != null) {
                    for (int i = 0; i < danhSachDeThi.size(); i++) {
                        if (danhSachDeThi.get(i).getMaDeThi() == deThi.getMaDeThi()) {
                            danhSachDeThi.set(i, updated);
                            break;
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa đề thi
     */
    public boolean xoaDeThi(int maDeThi) {
        try {
            if (deThiDAO.delete(maDeThi)) {
                if (danhSachDeThi != null) {
                    danhSachDeThi.removeIf(dt -> dt.getMaDeThi() == maDeThi);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật số câu hỏi trong đề thi
     */
    public boolean capNhatSoCauHoi(int maDeThi, int soCauHoi) {
        try {
            DeThiDTO deThi = deThiDAO.getById(maDeThi);
            if (deThi != null) {
                deThi.setSoCauHoi(soCauHoi);
                return deThiDAO.update(deThi);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reloadCache() {
        danhSachDeThi = null;
        lastMaGV = -1;
    }
}
