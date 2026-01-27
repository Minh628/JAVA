/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: TruongKhoaBUS - Xử lý logic nghiệp vụ trưởng khoa
 * Sử dụng static ArrayList để cache dữ liệu, chỉ load từ DB 1 lần
 */
package bus;

import dao.*;
import dto.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TruongKhoaBUS {
    // Non-static DAO instances
    private GiangVienDAO giangVienDAO;
    private SinhVienDAO sinhVienDAO;
    private KhoaDAO khoaDAO;
    private NganhDAO nganhDAO;
    private HocPhanDAO hocPhanDAO;
    private KyThiDAO kyThiDAO;
    private DeThiDAO deThiDAO;
    private BaiThiDAO baiThiDAO;

    // Static ArrayList cache - chỉ load 1 lần từ DB
    private static ArrayList<GiangVienDTO> danhSachGiangVien = null;
    private static ArrayList<SinhVienDTO> danhSachSinhVien = null;
    private static ArrayList<KhoaDTO> danhSachKhoa = null;
    private static ArrayList<NganhDTO> danhSachNganh = null;
    private static ArrayList<HocPhanDTO> danhSachHocPhan = null;
    private static ArrayList<KyThiDTO> danhSachKyThi = null;
    private static ArrayList<DeThiDTO> danhSachDeThi = null;

    public TruongKhoaBUS() {
        this.giangVienDAO = new GiangVienDAO();
        this.sinhVienDAO = new SinhVienDAO();
        this.khoaDAO = new KhoaDAO();
        this.nganhDAO = new NganhDAO();
        this.hocPhanDAO = new HocPhanDAO();
        this.kyThiDAO = new KyThiDAO();
        this.deThiDAO = new DeThiDAO();
        this.baiThiDAO = new BaiThiDAO();
    }

    // ==================== QUẢN LÝ GIẢNG VIÊN ====================

    /**
     * Lấy danh sách giảng viên - load từ DB nếu chưa có cache
     */
    public List<GiangVienDTO> getDanhSachGiangVien() {
        if (danhSachGiangVien == null) {
            try {
                danhSachGiangVien = new ArrayList<>(giangVienDAO.getAllGiangVien());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachGiangVien;
    }

    /**
     * Lấy giảng viên theo khoa - lọc từ cache
     */
    public List<GiangVienDTO> getGiangVienTheoKhoa(int maKhoa) {
        getDanhSachGiangVien(); // Đảm bảo cache đã load
        ArrayList<GiangVienDTO> ketQua = new ArrayList<>();
        for (GiangVienDTO gv : danhSachGiangVien) {
            if (gv.getMaKhoa() == maKhoa) {
                ketQua.add(gv);
            }
        }
        return ketQua;
    }

    /**
     * Thêm giảng viên mới - cập nhật DB và cache
     */
    public boolean themGiangVien(GiangVienDTO giangVien) {
        try {
            // Kiểm tra tên đăng nhập đã tồn tại
            if (giangVienDAO.checkTenDangNhapExists(giangVien.getTenDangNhap())) {
                return false;
            }
            giangVien.setMaVaiTro(VaiTroDTO.GIANG_VIEN);
            if (giangVienDAO.insert(giangVien)) {
                // Reload cache sau khi thêm để lấy mã mới
                danhSachGiangVien = new ArrayList<>(giangVienDAO.getAllGiangVien());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật giảng viên - cập nhật DB và cache
     */
    public boolean capNhatGiangVien(GiangVienDTO giangVien) {
        try {
            if (giangVienDAO.update(giangVien)) {
                // Cập nhật trong cache
                if (danhSachGiangVien != null) {
                    for (int i = 0; i < danhSachGiangVien.size(); i++) {
                        if (danhSachGiangVien.get(i).getMaGV() == giangVien.getMaGV()) {
                            // Reload thông tin mới từ DB
                            GiangVienDTO updated = giangVienDAO.getById(giangVien.getMaGV());
                            if (updated != null) {
                                danhSachGiangVien.set(i, updated);
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
     * Xóa giảng viên - cập nhật DB và cache
     */
    public boolean xoaGiangVien(int maGV) {
        try {
            if (giangVienDAO.delete(maGV)) {
                // Xóa khỏi cache
                if (danhSachGiangVien != null) {
                    danhSachGiangVien.removeIf(gv -> gv.getMaGV() == maGV);
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
     * Reset mật khẩu giảng viên
     */
    public boolean resetMatKhauGV(int maGV, String matKhauMoi) {
        try {
            return giangVienDAO.updatePassword(maGV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== QUẢN LÝ SINH VIÊN ====================

    /**
     * Lấy danh sách sinh viên - load từ DB nếu chưa có cache
     */
    public List<SinhVienDTO> getDanhSachSinhVien() {
        if (danhSachSinhVien == null) {
            try {
                danhSachSinhVien = new ArrayList<>(sinhVienDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachSinhVien;
    }

    /**
     * Lấy sinh viên theo ngành - lọc từ cache
     */
    public List<SinhVienDTO> getSinhVienTheoNganh(int maNganh) {
        getDanhSachSinhVien(); // Đảm bảo cache đã load
        ArrayList<SinhVienDTO> ketQua = new ArrayList<>();
        for (SinhVienDTO sv : danhSachSinhVien) {
            if (sv.getMaNganh() == maNganh) {
                ketQua.add(sv);
            }
        }
        return ketQua;
    }

    /**
     * Thêm sinh viên mới - cập nhật DB và cache
     */
    public boolean themSinhVien(SinhVienDTO sinhVien) {
        try {
            // Kiểm tra tên đăng nhập đã tồn tại
            if (sinhVienDAO.checkTenDangNhapExists(sinhVien.getTenDangNhap())) {
                return false;
            }
            if (sinhVienDAO.insert(sinhVien)) {
                // Reload cache sau khi thêm để lấy mã mới
                danhSachSinhVien = new ArrayList<>(sinhVienDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật sinh viên - cập nhật DB và cache
     */
    public boolean capNhatSinhVien(SinhVienDTO sinhVien) {
        try {
            if (sinhVienDAO.update(sinhVien)) {
                // Cập nhật trong cache
                if (danhSachSinhVien != null) {
                    for (int i = 0; i < danhSachSinhVien.size(); i++) {
                        if (danhSachSinhVien.get(i).getMaSV() == sinhVien.getMaSV()) {
                            // Reload thông tin mới từ DB
                            SinhVienDTO updated = sinhVienDAO.getById(sinhVien.getMaSV());
                            if (updated != null) {
                                danhSachSinhVien.set(i, updated);
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
     * Xóa sinh viên - cập nhật DB và cache
     */
    public boolean xoaSinhVien(int maSV) {
        try {
            if (sinhVienDAO.delete(maSV)) {
                // Xóa khỏi cache
                if (danhSachSinhVien != null) {
                    danhSachSinhVien.removeIf(sv -> sv.getMaSV() == maSV);
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
     * Reset mật khẩu sinh viên
     */
    public boolean resetMatKhauSV(int maSV, String matKhauMoi) {
        try {
            return sinhVienDAO.updatePassword(maSV, matKhauMoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== QUẢN LÝ KHOA ====================

    /**
     * Lấy danh sách khoa - load từ DB nếu chưa có cache
     */
    public List<KhoaDTO> getDanhSachKhoa() {
        if (danhSachKhoa == null) {
            try {
                danhSachKhoa = new ArrayList<>(khoaDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachKhoa;
    }

    /**
     * Thêm khoa mới - cập nhật DB và cache
     */
    public boolean themKhoa(KhoaDTO khoa) {
        try {
            if (khoaDAO.insert(khoa)) {
                // Reload cache sau khi thêm để lấy mã mới
                danhSachKhoa = new ArrayList<>(khoaDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật khoa - cập nhật DB và cache
     */
    public boolean capNhatKhoa(KhoaDTO khoa) {
        try {
            if (khoaDAO.update(khoa)) {
                // Cập nhật trong cache
                if (danhSachKhoa != null) {
                    for (int i = 0; i < danhSachKhoa.size(); i++) {
                        if (danhSachKhoa.get(i).getMaKhoa() == khoa.getMaKhoa()) {
                            danhSachKhoa.set(i, khoa);
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
     * Đếm số ngành thuộc khoa
     */
    public int demNganhTheoKhoa(int maKhoa) {
        try {
            return khoaDAO.countNganhByKhoa(maKhoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Kiểm tra có thể xóa khoa không (không có ngành nào)
     */
    public boolean coTheXoaKhoa(int maKhoa) {
        return demNganhTheoKhoa(maKhoa) == 0;
    }

    /**
     * Xóa khoa - cập nhật DB và cache
     * Trả về:
     * - 1: Thành công
     * - 0: Thất bại (lỗi DB)
     * - -1: Không thể xóa vì có ngành thuộc khoa
     */
    public int xoaKhoaAnToan(int maKhoa) {
        try {
            // Kiểm tra có ngành không
            int soNganh = khoaDAO.countNganhByKhoa(maKhoa);
            if (soNganh > 0) {
                return -1; // Có ngành, không cho xóa
            }
            if (khoaDAO.delete(maKhoa)) {
                // Xóa khỏi cache
                if (danhSachKhoa != null) {
                    danhSachKhoa.removeIf(k -> k.getMaKhoa() == maKhoa);
                }
                return 1; // Thành công
            }
            return 0; // Thất bại
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Xóa khoa - cập nhật DB và cache (legacy - không kiểm tra ràng buộc)
     */
    public boolean xoaKhoa(int maKhoa) {
        try {
            if (khoaDAO.delete(maKhoa)) {
                // Xóa khỏi cache
                if (danhSachKhoa != null) {
                    danhSachKhoa.removeIf(k -> k.getMaKhoa() == maKhoa);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== QUẢN LÝ NGÀNH ====================

    /**
     * Lấy danh sách ngành - load từ DB nếu chưa có cache
     */
    public List<NganhDTO> getDanhSachNganh() {
        if (danhSachNganh == null) {
            try {
                danhSachNganh = new ArrayList<>(nganhDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachNganh;
    }

    /**
     * Thêm ngành mới - cập nhật DB và cache
     */
    public boolean themNganh(NganhDTO nganh) {
        try {
            if (nganhDAO.insert(nganh)) {
                // Reload cache sau khi thêm để lấy mã mới và tên khoa
                danhSachNganh = new ArrayList<>(nganhDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật ngành - cập nhật DB và cache
     */
    public boolean capNhatNganh(NganhDTO nganh) {
        try {
            if (nganhDAO.update(nganh)) {
                // Reload cache để lấy tên khoa mới
                danhSachNganh = new ArrayList<>(nganhDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa ngành - cập nhật DB và cache
     */
    public boolean xoaNganh(int maNganh) {
        try {
            if (nganhDAO.delete(maNganh)) {
                // Xóa khỏi cache
                if (danhSachNganh != null) {
                    danhSachNganh.removeIf(n -> n.getMaNganh() == maNganh);
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
     * Lấy ngành theo khoa - lọc từ cache
     */
    public List<NganhDTO> getNganhTheoKhoa(int maKhoa) {
        getDanhSachNganh(); // Đảm bảo cache đã load
        ArrayList<NganhDTO> ketQua = new ArrayList<>();
        for (NganhDTO nganh : danhSachNganh) {
            if (nganh.getMaKhoa() == maKhoa) {
                ketQua.add(nganh);
            }
        }
        return ketQua;
    }

    // ==================== QUẢN LÝ HỌC PHẦN ====================

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
     * Thêm học phần mới - cập nhật DB và cache
     */
    public boolean themHocPhan(HocPhanDTO hocPhan) {
        try {
            if (hocPhanDAO.insert(hocPhan)) {
                // Reload cache sau khi thêm để lấy mã mới
                danhSachHocPhan = new ArrayList<>(hocPhanDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật học phần - cập nhật DB và cache
     */
    public boolean capNhatHocPhan(HocPhanDTO hocPhan) {
        try {
            if (hocPhanDAO.update(hocPhan)) {
                // Cập nhật trong cache
                if (danhSachHocPhan != null) {
                    for (int i = 0; i < danhSachHocPhan.size(); i++) {
                        if (danhSachHocPhan.get(i).getMaHocPhan() == hocPhan.getMaHocPhan()) {
                            danhSachHocPhan.set(i, hocPhan);
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
     * Xóa học phần - cập nhật DB và cache
     */
    public boolean xoaHocPhan(int maHocPhan) {
        try {
            if (hocPhanDAO.delete(maHocPhan)) {
                // Xóa khỏi cache
                if (danhSachHocPhan != null) {
                    danhSachHocPhan.removeIf(hp -> hp.getMaHocPhan() == maHocPhan);
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
     * Đổi mật khẩu giảng viên
     */
    public boolean doiMatKhauGiangVien(int maGV, String matKhauCu, String matKhauMoi) {
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

    // ==================== QUẢN LÝ KỲ THI ====================

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

    /**
     * Thêm kỳ thi mới - cập nhật DB và cache
     */
    public boolean themKyThi(KyThiDTO kyThi) {
        try {
            if (kyThiDAO.insert(kyThi)) {
                // Reload cache sau khi thêm để lấy mã mới
                danhSachKyThi = new ArrayList<>(kyThiDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật kỳ thi - cập nhật DB và cache
     */
    public boolean capNhatKyThi(KyThiDTO kyThi) {
        try {
            if (kyThiDAO.update(kyThi)) {
                // Cập nhật trong cache
                if (danhSachKyThi != null) {
                    for (int i = 0; i < danhSachKyThi.size(); i++) {
                        if (danhSachKyThi.get(i).getMaKyThi() == kyThi.getMaKyThi()) {
                            danhSachKyThi.set(i, kyThi);
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
     * Xóa kỳ thi - cập nhật DB và cache
     */
    public boolean xoaKyThi(int maKyThi) {
        try {
            if (kyThiDAO.delete(maKyThi)) {
                // Xóa khỏi cache
                if (danhSachKyThi != null) {
                    danhSachKyThi.removeIf(kt -> kt.getMaKyThi() == maKyThi);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== QUẢN LÝ ĐỀ THI ====================

    /**
     * Lấy danh sách đề thi - load từ DB nếu chưa có cache
     */
    public List<DeThiDTO> getDanhSachDeThi() {
        if (danhSachDeThi == null) {
            try {
                danhSachDeThi = new ArrayList<>(deThiDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachDeThi;
    }

    // ==================== THỐNG KÊ ====================

    /**
     * Lấy kết quả thi theo đề thi
     */
    public List<BaiThiDTO> getKetQuaTheoDeThi(int maDeThi) {
        try {
            return baiThiDAO.getByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== STATIC METHODS ĐỂ RELOAD CACHE ====================

    /**
     * Reload cache giảng viên từ DB
     */
    public static void reloadGiangVien() {
        danhSachGiangVien = null;
    }

    /**
     * Reload cache sinh viên từ DB
     */
    public static void reloadSinhVien() {
        danhSachSinhVien = null;
    }

    /**
     * Reload cache khoa từ DB
     */
    public static void reloadKhoa() {
        danhSachKhoa = null;
    }

    /**
     * Reload cache ngành từ DB
     */
    public static void reloadNganh() {
        danhSachNganh = null;
    }

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
     * Reload cache đề thi từ DB
     */
    public static void reloadDeThi() {
        danhSachDeThi = null;
    }

    /**
     * Reload tất cả cache
     */
    public static void reloadAll() {
        danhSachGiangVien = null;
        danhSachSinhVien = null;
        danhSachKhoa = null;
        danhSachNganh = null;
        danhSachHocPhan = null;
        danhSachKyThi = null;
        danhSachDeThi = null;
    }

    // ==================== TÌM KIẾM ====================

    /**
     * Tìm kiếm sinh viên theo từ khóa
     */
    public List<SinhVienDTO> timKiemSinhVien(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachSinhVien();
        }
        try {
            return sinhVienDAO.search(keyword.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm giảng viên theo từ khóa
     */
    public List<GiangVienDTO> timKiemGiangVien(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachGiangVien();
        }
        try {
            return giangVienDAO.search(keyword.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm khoa theo từ khóa
     */
    public List<KhoaDTO> timKiemKhoa(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachKhoa();
        }
        try {
            return khoaDAO.search(keyword.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm ngành theo từ khóa
     */
    public List<NganhDTO> timKiemNganh(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachNganh();
        }
        try {
            return nganhDAO.search(keyword.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm học phần theo từ khóa
     */
    public List<HocPhanDTO> timKiemHocPhan(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachHocPhan();
        }
        try {
            return hocPhanDAO.search(keyword.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm kỳ thi theo từ khóa
     */
    public List<KyThiDTO> timKiemKyThi(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachKyThi();
        }
        try {
            return kyThiDAO.search(keyword.trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ==================== LẤY MÃ DUY NHẤT TIẾP THEO ====================

    /**
     * Lấy mã sinh viên tiếp theo (mã duy nhất)
     */
    public int getNextMaSinhVien() {
        try {
            return sinhVienDAO.getNextMaSV();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Lấy mã giảng viên tiếp theo (mã duy nhất)
     */
    public int getNextMaGiangVien() {
        try {
            return giangVienDAO.getNextMaGV();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Lấy mã khoa tiếp theo (mã duy nhất)
     */
    public int getNextMaKhoa() {
        try {
            return khoaDAO.getNextMaKhoa();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Lấy mã ngành tiếp theo (mã duy nhất)
     */
    public int getNextMaNganh() {
        try {
            return nganhDAO.getNextMaNganh();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Lấy mã học phần tiếp theo (mã duy nhất)
     */
    public int getNextMaHocPhan() {
        try {
            return hocPhanDAO.getNextMaHocPhan();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Lấy mã kỳ thi tiếp theo (mã duy nhất)
     */
    public int getNextMaKyThi() {
        try {
            return kyThiDAO.getNextMaKyThi();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
