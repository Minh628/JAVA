/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: GiangVienBUS - Xử lý logic nghiệp vụ giảng viên
 * Sử dụng static ArrayList để cache dữ liệu, chỉ load từ DB 1 lần
 */
package bus;

import dao.CauHoiDAO;
import dao.DeThiDAO;
import dao.GiangVienDAO;
import dao.HocPhanDAO;
import dao.KyThiDAO;
import dto.CauHoiDTO;
import dto.DeThiDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import dto.KyThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GiangVienBUS {
    // Non-static DAO instances
    private GiangVienDAO giangVienDAO;
    private CauHoiDAO cauHoiDAO;
    private DeThiDAO deThiDAO;
    private HocPhanDAO hocPhanDAO;
    private KyThiDAO kyThiDAO;

    // Static ArrayList cache - chỉ load 1 lần từ DB
    private static ArrayList<HocPhanDTO> danhSachHocPhan = null;
    private static ArrayList<KyThiDTO> danhSachKyThi = null;
    // Câu hỏi và đề thi lưu theo giảng viên (Map có thể dùng nhưng để đơn giản dùng
    // ArrayList)
    private static ArrayList<CauHoiDTO> danhSachCauHoi = null;
    private static int lastMaGVCauHoi = -1;
    private static ArrayList<DeThiDTO> danhSachDeThi = null;
    private static int lastMaGVDeThi = -1;

    public GiangVienBUS() {
        this.giangVienDAO = new GiangVienDAO();
        this.cauHoiDAO = new CauHoiDAO();
        this.deThiDAO = new DeThiDAO();
        this.hocPhanDAO = new HocPhanDAO();
        this.kyThiDAO = new KyThiDAO();
    }

    // ==================== QUẢN LÝ CÂU HỎI ====================

    /**
     * Lấy danh sách câu hỏi của giảng viên - load từ DB nếu chưa có cache hoặc khác
     * giảng viên
     */
    public List<CauHoiDTO> getDanhSachCauHoi(int maGV) {
        if (danhSachCauHoi == null || lastMaGVCauHoi != maGV) {
            try {
                danhSachCauHoi = new ArrayList<>(cauHoiDAO.getByGiangVien(maGV));
                lastMaGVCauHoi = maGV;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachCauHoi;
    }

    /**
     * Lấy câu hỏi theo môn - lọc từ cache
     */
    public List<CauHoiDTO> getCauHoiTheoMon(int maMon) {
        if (danhSachCauHoi == null) {
            return new ArrayList<>();
        }
        ArrayList<CauHoiDTO> ketQua = new ArrayList<>();
        for (CauHoiDTO ch : danhSachCauHoi) {
            if (ch.getMaMon() == maMon) {
                ketQua.add(ch);
            }
        }
        return ketQua;
    }

    /**
     * Thêm câu hỏi mới - cập nhật DB và cache
     */
    public boolean themCauHoi(CauHoiDTO cauHoi) {
        try {
            if (cauHoiDAO.insert(cauHoi)) {
                // Reload cache sau khi thêm
                if (lastMaGVCauHoi == cauHoi.getMaGV()) {
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
     * Cập nhật câu hỏi - cập nhật DB và cache
     */
    public boolean capNhatCauHoi(CauHoiDTO cauHoi) {
        try {
            if (cauHoiDAO.update(cauHoi)) {
                // Cập nhật trong cache
                if (danhSachCauHoi != null) {
                    for (int i = 0; i < danhSachCauHoi.size(); i++) {
                        if (danhSachCauHoi.get(i).getMaCauHoi() == cauHoi.getMaCauHoi()) {
                            // Reload thông tin mới từ DB
                            CauHoiDTO updated = cauHoiDAO.getById(cauHoi.getMaCauHoi());
                            if (updated != null) {
                                danhSachCauHoi.set(i, updated);
                            }
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
     * Xóa câu hỏi - cập nhật DB và cache
     */
    public boolean xoaCauHoi(int maCauHoi) {
        try {
            if (cauHoiDAO.delete(maCauHoi)) {
                // Xóa khỏi cache
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
     * Lấy câu hỏi theo ID
     */
    public CauHoiDTO getCauHoiById(int maCauHoi) {
        // Tìm trong cache trước
        if (danhSachCauHoi != null) {
            for (CauHoiDTO ch : danhSachCauHoi) {
                if (ch.getMaCauHoi() == maCauHoi) {
                    return ch;
                }
            }
        }
        // Nếu không có trong cache thì lấy từ DB
        try {
            return cauHoiDAO.getById(maCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== QUẢN LÝ ĐỀ THI ====================

    /**
     * Lấy danh sách đề thi của giảng viên - load từ DB nếu chưa có cache hoặc khác
     * giảng viên
     */
    public List<DeThiDTO> getDanhSachDeThi(int maGV) {
        if (danhSachDeThi == null || lastMaGVDeThi != maGV) {
            try {
                danhSachDeThi = new ArrayList<>(deThiDAO.getByGiangVien(maGV));
                lastMaGVDeThi = maGV;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachDeThi;
    }

    /**
     * Thêm đề thi mới - cập nhật DB và cache
     */
    public boolean themDeThi(DeThiDTO deThi) {
        try {
            if (deThiDAO.insert(deThi)) {
                // Reload cache sau khi thêm
                if (lastMaGVDeThi == deThi.getMaGV()) {
                    danhSachDeThi = new ArrayList<>(deThiDAO.getByGiangVien(deThi.getMaGV()));
                }
                // Cũng reload cache đề thi trong TruongKhoaBUS
                TruongKhoaBUS.reloadDeThi();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật đề thi - cập nhật DB và cache
     */
    public boolean capNhatDeThi(DeThiDTO deThi) {
        try {
            if (deThiDAO.update(deThi)) {
                // Cập nhật trong cache
                if (danhSachDeThi != null) {
                    for (int i = 0; i < danhSachDeThi.size(); i++) {
                        if (danhSachDeThi.get(i).getMaDeThi() == deThi.getMaDeThi()) {
                            // Reload thông tin mới từ DB
                            DeThiDTO updated = deThiDAO.getById(deThi.getMaDeThi());
                            if (updated != null) {
                                danhSachDeThi.set(i, updated);
                            }
                            break;
                        }
                    }
                }
                // Cũng reload cache đề thi trong TruongKhoaBUS
                TruongKhoaBUS.reloadDeThi();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa đề thi - cập nhật DB và cache
     * Trả về:
     * - 1: Thành công
     * - 0: Thất bại (lỗi DB)
     * - -1: Không thể xóa vì có bài thi
     */
    public int xoaDeThiAnToan(int maDeThi) {
        try {
            // Kiểm tra có bài thi không
            int soBaiThi = deThiDAO.countBaiThiByDeThi(maDeThi);
            if (soBaiThi > 0) {
                return -1; // Có bài thi, không cho xóa
            }
            if (deThiDAO.delete(maDeThi)) {
                // Xóa khỏi cache
                if (danhSachDeThi != null) {
                    danhSachDeThi.removeIf(dt -> dt.getMaDeThi() == maDeThi);
                }
                // Cũng reload cache đề thi trong TruongKhoaBUS
                TruongKhoaBUS.reloadDeThi();
                return 1; // Thành công
            }
            return 0; // Thất bại
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Đếm số bài thi của đề thi
     */
    public int demBaiThiTheoDeThi(int maDeThi) {
        try {
            return deThiDAO.countBaiThiByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Xóa đề thi - cập nhật DB và cache (legacy - không kiểm tra ràng buộc)
     */
    public boolean xoaDeThi(int maDeThi) {
        try {
            if (deThiDAO.delete(maDeThi)) {
                // Xóa khỏi cache
                if (danhSachDeThi != null) {
                    danhSachDeThi.removeIf(dt -> dt.getMaDeThi() == maDeThi);
                }
                // Cũng reload cache đề thi trong TruongKhoaBUS
                TruongKhoaBUS.reloadDeThi();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== QUẢN LÝ CÂU HỎI TRONG ĐỀ THI ====================

    /**
     * Lấy danh sách mã câu hỏi trong đề thi
     */
    public List<Integer> getMaCauHoiTrongDeThi(int maDeThi) {
        try {
            return deThiDAO.getCauHoiInDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Thêm câu hỏi vào đề thi
     */
    public boolean themCauHoiVaoDeThi(int maDeThi, int maCauHoi) {
        try {
            return deThiDAO.themCauHoiVaoDeThi(maDeThi, maCauHoi);
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
            return deThiDAO.themNhieuCauHoiVaoDeThi(maDeThi, danhSachMaCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa câu hỏi khỏi đề thi
     */
    public boolean xoaCauHoiKhoiDeThi(int maDeThi, int maCauHoi) {
        try {
            return deThiDAO.xoaCauHoiKhoiDeThi(maDeThi, maCauHoi);
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
            if (deThiDAO.updateSoCauHoi(maDeThi, soCauHoi)) {
                // Cập nhật cache
                if (danhSachDeThi != null) {
                    for (DeThiDTO dt : danhSachDeThi) {
                        if (dt.getMaDeThi() == maDeThi) {
                            dt.setSoCauHoi(soCauHoi);
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

    // ==================== DANH MỤC ====================

    /**
     * Lấy danh sách học phần - load từ DB nếu chưa có cache
     */
    public List<HocPhanDTO> getDanhSachHocPhan() {
        if (danhSachHocPhan == null) {
            try {
                danhSachHocPhan = new ArrayList<>(hocPhanDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachHocPhan;
    }

    /**
     * Lấy danh sách kỳ thi - load từ DB nếu chưa có cache
     */
    public List<KyThiDTO> getDanhSachKyThi() {
        if (danhSachKyThi == null) {
            try {
                danhSachKyThi = new ArrayList<>(kyThiDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachKyThi;
    }

    // ==================== THÔNG TIN CÁ NHÂN ====================

    /**
     * Lấy thông tin giảng viên
     */
    public GiangVienDTO getThongTin(int maGV) {
        try {
            return giangVienDAO.getById(maGV);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật thông tin cá nhân
     */
    public boolean capNhatThongTin(GiangVienDTO giangVien) {
        try {
            if (giangVienDAO.update(giangVien)) {
                // Cũng reload cache giảng viên trong TruongKhoaBUS
                TruongKhoaBUS.reloadGiangVien();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đổi mật khẩu giảng viên
     */
    public boolean doiMatKhau(int maGV, String matKhauCu, String matKhauMoi) {
        try {
            // Kiểm tra mật khẩu cũ
            GiangVienDTO gv = giangVienDAO.getById(maGV);
            if (gv == null || !gv.getMatKhau().equals(matKhauCu)) {
                return false;
            }
            return giangVienDAO.updatePassword(maGV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== STATIC METHODS ĐỂ RELOAD CACHE ====================

    /**
     * Reload cache học phần từ DB
     */
    public static void reloadHocPhan() {
        danhSachHocPhan = null;
    }

    /**
     * Reload cache kỳ thi từ DB
     */
    public static void reloadKyThi() {
        danhSachKyThi = null;
    }

    /**
     * Reload cache câu hỏi từ DB
     */
    public static void reloadCauHoi() {
        danhSachCauHoi = null;
        lastMaGVCauHoi = -1;
    }

    /**
     * Reload cache đề thi từ DB
     */
    public static void reloadDeThi() {
        danhSachDeThi = null;
        lastMaGVDeThi = -1;
    }

    /**
     * Reload tất cả cache
     */
    public static void reloadAll() {
        danhSachHocPhan = null;
        danhSachKyThi = null;
        danhSachCauHoi = null;
        lastMaGVCauHoi = -1;
        danhSachDeThi = null;
        lastMaGVDeThi = -1;
    }
}
