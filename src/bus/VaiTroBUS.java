/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: VaiTroBUS - Xử lý logic nghiệp vụ Vai trò
 * CHỈ gọi VaiTroDAO - tuân thủ nguyên tắc 1 BUS : 1 DAO
 * 
 * Lưu ý: Bảng VaiTro thường chỉ có 3 vai trò cố định (Admin, GiangVien, SinhVien)
 * nên chỉ cần các chức năng đọc cơ bản
 */
package bus;

import dao.VaiTroDAO;
import dto.VaiTroDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VaiTroBUS {
    private VaiTroDAO vaiTroDAO;

    // Cache
    private static ArrayList<VaiTroDTO> danhSachVaiTro = null;

    public VaiTroBUS() {
        this.vaiTroDAO = new VaiTroDAO();
    }

    /**
     * Lấy danh sách tất cả vai trò
     */
    public List<VaiTroDTO> getDanhSachVaiTro() {
        if (danhSachVaiTro == null) {
            try {
                danhSachVaiTro = new ArrayList<>(vaiTroDAO.getAll());
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachVaiTro;
    }

    /**
     * Lấy vai trò theo mã
     */
    public VaiTroDTO getById(int maVaiTro) {
        try {
            return vaiTroDAO.getById(maVaiTro);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tên vai trò theo mã (tiện ích)
     */
    public String getTenVaiTro(int maVaiTro) {
        VaiTroDTO vt = getById(maVaiTro);
        return vt != null ? vt.getTenVaiTro() : "";
    }

    public static void reloadCache() {
        danhSachVaiTro = null;
    }
}
