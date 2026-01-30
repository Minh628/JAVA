/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: CauHoiBUS - Xử lý logic nghiệp vụ Câu hỏi
 * CHỈ gọi CauHoiDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 */
package bus;

import dao.CauHoiDAO;
import dao.ChiTietBaiThiDAO;
import dao.ChiTietDeThiDAO;
import dto.CauHoiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CauHoiBUS {
    private CauHoiDAO cauHoiDAO;
    private ChiTietDeThiDAO chiTietDeThiDAO;
    private ChiTietBaiThiDAO chiTietBaiThiDAO;

    // Cache theo giảng viên
    private static ArrayList<CauHoiDTO> danhSachCauHoi = null;
    private static int lastMaGV = -1;

    public CauHoiBUS() {
        this.cauHoiDAO = new CauHoiDAO();
        this.chiTietDeThiDAO = new ChiTietDeThiDAO();
        this.chiTietBaiThiDAO = new ChiTietBaiThiDAO();
    }

    /**
     * Lấy danh sách câu hỏi theo giảng viên
     */
    public List<CauHoiDTO> getDanhSachCauHoi(int maGV) {
        if (danhSachCauHoi == null || lastMaGV != maGV) {
            try {
                danhSachCauHoi = new ArrayList<>(cauHoiDAO.getByGiangVien(maGV));
                lastMaGV = maGV;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachCauHoi;
    }

    /**
     * Lấy tất cả câu hỏi
     */
    public List<CauHoiDTO> getAllCauHoi() {
        try {
            return cauHoiDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy câu hỏi theo môn học
     */
    public List<CauHoiDTO> getCauHoiTheoMon(int maMon) {
        try {
            return cauHoiDAO.getByMon(maMon);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy câu hỏi theo môn và mức độ (cho tạo đề thi)
     */
    public List<CauHoiDTO> getCauHoiTheoMucDo(int maMon, String mucDo) {
        try {
            return cauHoiDAO.getByMucDo(maMon, mucDo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy câu hỏi theo ID
     */
    public CauHoiDTO getById(int maCauHoi) {
        try {
            return cauHoiDAO.getById(maCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy danh sách câu hỏi theo danh sách mã
     */
    public List<CauHoiDTO> getByIds(List<Integer> danhSachMaCauHoi) {
        List<CauHoiDTO> ketQua = new ArrayList<>();
        for (int maCauHoi : danhSachMaCauHoi) {
            CauHoiDTO ch = getById(maCauHoi);
            if (ch != null) {
                ketQua.add(ch);
            }
        }
        return ketQua;
    }

    /**
     * Thêm câu hỏi mới
     */
    public boolean themCauHoi(CauHoiDTO cauHoi) {
        try {
            if (cauHoiDAO.insert(cauHoi)) {
                // Reload cache
                if (lastMaGV == cauHoi.getMaGV()) {
                    danhSachCauHoi = new ArrayList<>(cauHoiDAO.getByGiangVien(cauHoi.getMaGV()));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật câu hỏi
     */
    public boolean capNhatCauHoi(CauHoiDTO cauHoi) {
        try {
            if (cauHoiDAO.update(cauHoi)) {
                // Cập nhật cache
                if (danhSachCauHoi != null) {
                    CauHoiDTO updated = cauHoiDAO.getById(cauHoi.getMaCauHoi());
                    for (int i = 0; i < danhSachCauHoi.size(); i++) {
                        if (danhSachCauHoi.get(i).getMaCauHoi() == cauHoi.getMaCauHoi()) {
                            danhSachCauHoi.set(i, updated);
                            break;
                        }
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa câu hỏi
     */
    public boolean xoaCauHoi(int maCauHoi) {
        try {
            // Không cho xóa nếu câu hỏi đã nằm trong đề thi
            if (chiTietDeThiDAO.isCauHoiInAnyDeThi(maCauHoi)) {
                return false;
            }
            // Không cho xóa nếu câu hỏi đã xuất hiện trong bài thi
            if (chiTietBaiThiDAO.countByCauHoi(maCauHoi) > 0) {
                return false;
            }
            if (cauHoiDAO.delete(maCauHoi)) {
                if (danhSachCauHoi != null) {
                    danhSachCauHoi.removeIf(ch -> ch.getMaCauHoi() == maCauHoi);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra có thể xóa câu hỏi không
     */
    public boolean coTheXoaCauHoi(int maCauHoi) {
        try {
            return !chiTietDeThiDAO.isCauHoiInAnyDeThi(maCauHoi)
                    && chiTietBaiThiDAO.countByCauHoi(maCauHoi) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reloadCache() {
        danhSachCauHoi = null;
        lastMaGV = -1;
    }
}
