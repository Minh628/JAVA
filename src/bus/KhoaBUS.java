/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: KhoaBUS - Xử lý logic nghiệp vụ Khoa
 */
package bus;

import dao.KhoaDAO;
import dto.KhoaDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhoaBUS {
    private KhoaDAO khoaDAO;
    
    // Cache
    private static ArrayList<KhoaDTO> danhSachKhoa = null;

    public KhoaBUS() {
        this.khoaDAO = new KhoaDAO();
    }

    /**
     * Lấy danh sách khoa
     */
    public List<KhoaDTO> getDanhSachKhoa() {
        if (danhSachKhoa == null) {
            try {
                danhSachKhoa = new ArrayList<>(khoaDAO.getAll());
            } catch (SQLException e) {
                throw new BusinessException("Lỗi lấy danh sách khoa: " + e.getMessage(), e);
            }
        }
        return danhSachKhoa;
    }

    /**
     * Lấy khoa theo mã
     */
    public KhoaDTO getById(int maKhoa) {
        getDanhSachKhoa();
        for (KhoaDTO k : danhSachKhoa) {
            if (k.getMaKhoa() == maKhoa) {
                return k;
            }
        }
        return null;
    }

    /**
     * Thêm khoa mới
     */
    public boolean themKhoa(KhoaDTO khoa) {
        try {
            if (khoaDAO.insert(khoa)) {
                danhSachKhoa = new ArrayList<>(khoaDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm khoa mới: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật khoa
     */
    public boolean capNhatKhoa(KhoaDTO khoa) {
        try {
            if (khoaDAO.update(khoa)) {
                // Cập nhật cache
                for (int i = 0; i < danhSachKhoa.size(); i++) {
                    if (danhSachKhoa.get(i).getMaKhoa() == khoa.getMaKhoa()) {
                        danhSachKhoa.set(i, khoa);
                        break;
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật khoa: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa khoa - kiểm tra ràng buộc
     */
    public boolean xoaKhoa(int maKhoa) {
        try {
            // Kiểm tra còn ngành thuộc khoa không
            if (khoaDAO.countNganhByKhoa(maKhoa) > 0) {
                return false; // Không xóa được vì còn ngành
            }
            
            if (khoaDAO.delete(maKhoa)) {
                danhSachKhoa.removeIf(k -> k.getMaKhoa() == maKhoa);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi xóa khoa: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra có thể xóa khoa không
     */
    public boolean coTheXoaKhoa(int maKhoa) {
        try {
            return khoaDAO.countNganhByKhoa(maKhoa) == 0;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi kiểm tra khả năng xóa khoa: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm khoa
     */
    public List<KhoaDTO> timKiem(String keyword) {
        try {
            return khoaDAO.search(keyword);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi tìm kiếm khoa: " + e.getMessage(), e);
        }
    }

    /**
     * Reload cache
     */
    public static void reloadCache() {
        danhSachKhoa = null;
    }
}
