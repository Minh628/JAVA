/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: HocPhanBUS - Xử lý logic nghiệp vụ Học phần
 */
package bus;

import dao.HocPhanDAO;
import dto.HocPhanDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HocPhanBUS {
    private HocPhanDAO hocPhanDAO;
    
    // Cache
    private static ArrayList<HocPhanDTO> danhSachHocPhan = null;

    public HocPhanBUS() {
        this.hocPhanDAO = new HocPhanDAO();
    }

    /**
     * Lấy danh sách học phần
     */
    public List<HocPhanDTO> getDanhSachHocPhan() {
        if (danhSachHocPhan == null) {
            try {
                danhSachHocPhan = new ArrayList<>(hocPhanDAO.getAll());
            } catch (SQLException e) {
                throw new BusinessException("Lỗi lấy danh sách học phần: " + e.getMessage(), e);
            }
        }
        return danhSachHocPhan;
    }

    /**
     * Lấy học phần theo mã
     */
    public HocPhanDTO getById(int maHocPhan) {
        getDanhSachHocPhan();
        for (HocPhanDTO hp : danhSachHocPhan) {
            if (hp.getMaHocPhan() == maHocPhan) {
                return hp;
            }
        }
        return null;
    }

    /**
     * Thêm học phần mới
     */
    public boolean themHocPhan(HocPhanDTO hocPhan) {
        try {
            if (hocPhanDAO.insert(hocPhan)) {
                danhSachHocPhan = new ArrayList<>(hocPhanDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm học phần mới: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật học phần
     */
    public boolean capNhatHocPhan(HocPhanDTO hocPhan) {
        try {
            if (hocPhanDAO.update(hocPhan)) {
                for (int i = 0; i < danhSachHocPhan.size(); i++) {
                    if (danhSachHocPhan.get(i).getMaHocPhan() == hocPhan.getMaHocPhan()) {
                        danhSachHocPhan.set(i, hocPhan);
                        break;
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật học phần: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa học phần
     */
    public boolean xoaHocPhan(int maHocPhan) {
        try {
            if (hocPhanDAO.delete(maHocPhan)) {
                danhSachHocPhan.removeIf(hp -> hp.getMaHocPhan() == maHocPhan);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi xóa học phần: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm học phần
     */
    public List<HocPhanDTO> timKiem(String keyword) {
        try {
            return hocPhanDAO.search(keyword);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi tìm kiếm học phần: " + e.getMessage(), e);
        }
    }

    public static void reloadCache() {
        danhSachHocPhan = null;
    }
}
