/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: DeThiBUS - Xử lý logic nghiệp vụ Đề thi
 * Gọi DeThiDAO, ChiTietDeThiDAO và BaiThiDAO để quản lý đề thi, chi tiết đề thi và ràng buộc bài thi
 */
package bus;

import dao.BaiThiDAO;
import dao.ChiTietDeThiDAO;
import dao.DeThiDAO;
import dto.ChiTietDeThiDTO;
import dto.DeThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeThiBUS {
    private DeThiDAO deThiDAO;
    private ChiTietDeThiDAO chiTietDeThiDAO;
    private BaiThiDAO baiThiDAO;

    // Cache theo giảng viên
    private static ArrayList<DeThiDTO> danhSachDeThi = null;
    private static int lastMaGV = -1;

    public DeThiBUS() {
        this.deThiDAO = new DeThiDAO();
        this.chiTietDeThiDAO = new ChiTietDeThiDAO();
        this.baiThiDAO = new BaiThiDAO();
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
            // Không cho xóa nếu đã có bài thi sử dụng đề thi này
            if (baiThiDAO.countByDeThi(maDeThi) > 0) {
                return false;
            }
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
     * Kiểm tra có thể xóa đề thi không
     */
    public boolean coTheXoaDeThi(int maDeThi) {
        try {
            return baiThiDAO.countByDeThi(maDeThi) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    // ============== Quản lý Chi tiết đề thi ==============

    /**
     * Lấy danh sách chi tiết đề thi theo mã đề thi
     */
    public List<ChiTietDeThiDTO> getChiTietByDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.getByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách mã câu hỏi trong đề thi
     */
    public List<Integer> getMaCauHoiByDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.getMaCauHoiByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Thêm một câu hỏi vào đề thi
     */
    public boolean themCauHoiVaoDeThi(ChiTietDeThiDTO chiTiet) {
        try {
            return chiTietDeThiDAO.insert(chiTiet);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm batch chi tiết đề thi
     */
    public boolean themChiTietBatch(List<ChiTietDeThiDTO> danhSach) {
        try {
            return chiTietDeThiDAO.insertBatch(danhSach);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một câu hỏi khỏi đề thi
     */
    public boolean xoaCauHoiKhoiDeThi(int maDeThi, int maCauHoi) {
        try {
            return chiTietDeThiDAO.delete(maDeThi, maCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả câu hỏi trong đề thi
     */
    public boolean xoaTatCaCauHoiTrongDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.deleteByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đếm số câu hỏi trong đề thi
     */
    public int demCauHoiTrongDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.countByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy thứ tự lớn nhất trong đề thi
     */
    public int getMaxThuTuChiTiet(int maDeThi) {
        try {
            return chiTietDeThiDAO.getMaxThuTu(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


}
