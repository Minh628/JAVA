/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: KyThiBUS - Xử lý logic nghiệp vụ Kỳ thi
 */
package bus;

import dao.KyThiDAO;
import dto.KyThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KyThiBUS {
    private KyThiDAO kyThiDAO;
    
    // Cache
    private static ArrayList<KyThiDTO> danhSachKyThi = null;

    public KyThiBUS() {
        this.kyThiDAO = new KyThiDAO();
    }

    /**
     * Lấy danh sách kỳ thi
     */
    public List<KyThiDTO> getDanhSachKyThi() {
        if (danhSachKyThi == null) {
            try {
                danhSachKyThi = new ArrayList<>(kyThiDAO.getAll());
            } catch (SQLException e) {
                throw new BusinessException("Lỗi lấy danh sách kỳ thi: " + e.getMessage(), e);
            }
        }
        return danhSachKyThi;
    }

    /**
     * Lấy kỳ thi đang diễn ra
     */
    public List<KyThiDTO> getKyThiDangDienRa() {
        try {
            return kyThiDAO.getKyThiDangDienRa();
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy kỳ thi đang diễn ra: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy kỳ thi theo mã
     */
    public KyThiDTO getById(int maKyThi) {
        try {
            return kyThiDAO.getById(maKyThi);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi lấy kỳ thi theo mã: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm kỳ thi mới
     */
    public boolean themKyThi(KyThiDTO kyThi) {
        try {
            if (kyThiDAO.insert(kyThi)) {
                danhSachKyThi = new ArrayList<>(kyThiDAO.getAll());
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi thêm kỳ thi mới: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật kỳ thi
     */
    public boolean capNhatKyThi(KyThiDTO kyThi) {
        try {
            if (kyThiDAO.update(kyThi)) {
                KyThiDTO updated = kyThiDAO.getById(kyThi.getMaKyThi());
                for (int i = 0; i < danhSachKyThi.size(); i++) {
                    if (danhSachKyThi.get(i).getMaKyThi() == kyThi.getMaKyThi()) {
                        danhSachKyThi.set(i, updated);
                        break;
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật kỳ thi: " + e.getMessage(), e);
        }
    }

    /**
     * Xóa kỳ thi
     */
    public boolean xoaKyThi(int maKyThi) {
        try {
            if (kyThiDAO.delete(maKyThi)) {
                danhSachKyThi.removeIf(kt -> kt.getMaKyThi() == maKyThi);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BusinessException("Lỗi xóa kỳ thi: " + e.getMessage(), e);
        }
    }

    /**
     * Tìm kiếm kỳ thi
     */
    public List<KyThiDTO> timKiem(String keyword) {
        try {
            return kyThiDAO.search(keyword);
        } catch (SQLException e) {
            throw new BusinessException("Lỗi tìm kiếm kỳ thi: " + e.getMessage(), e);
        }
    }

    public static void reloadCache() {
        danhSachKyThi = null;
    }
}
