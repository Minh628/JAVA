/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: NganhBUS - Xử lý logic nghiệp vụ Ngành
 */
package bus;

import dao.NganhDAO;
import dto.NganhDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NganhBUS {
    private NganhDAO nganhDAO;
    
    // Cache
    private static ArrayList<NganhDTO> danhSachNganh = null;

    public NganhBUS() {
        this.nganhDAO = new NganhDAO();
    }

    /**
     * Lấy danh sách ngành
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
     * Lấy ngành theo khoa
     */
    public List<NganhDTO> getNganhTheoKhoa(int maKhoa) {
        getDanhSachNganh();
        ArrayList<NganhDTO> ketQua = new ArrayList<>();
        for (NganhDTO n : danhSachNganh) {
            if (n.getMaKhoa() == maKhoa) {
                ketQua.add(n);
            }
        }
        return ketQua;
    }

    /**
     * Lấy ngành theo mã
     */
    public NganhDTO getById(int maNganh) {
        getDanhSachNganh();
        for (NganhDTO n : danhSachNganh) {
            if (n.getMaNganh() == maNganh) {
                return n;
            }
        }
        return null;
    }

    /**
     * Thêm ngành mới
     */
    public boolean themNganh(NganhDTO nganh) {
        try {
            if (nganhDAO.insert(nganh)) {
                danhSachNganh = new ArrayList<>(nganhDAO.getAll());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật ngành
     */
    public boolean capNhatNganh(NganhDTO nganh) {
        try {
            if (nganhDAO.update(nganh)) {
                NganhDTO updated = nganhDAO.getById(nganh.getMaNganh());
                for (int i = 0; i < danhSachNganh.size(); i++) {
                    if (danhSachNganh.get(i).getMaNganh() == nganh.getMaNganh()) {
                        danhSachNganh.set(i, updated);
                        break;
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
     * Xóa ngành
     */
    public boolean xoaNganh(int maNganh) {
        try {
            if (nganhDAO.delete(maNganh)) {
                danhSachNganh.removeIf(n -> n.getMaNganh() == maNganh);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tìm kiếm ngành
     */
    public List<NganhDTO> timKiem(String keyword) {
        try {
            return nganhDAO.search(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void reloadCache() {
        danhSachNganh = null;
    }
}
