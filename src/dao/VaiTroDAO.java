/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: VaiTroDAO - Data Access Object cho bảng VaiTro
 */
package dao;

import config.DatabaseHelper;
import dto.VaiTroDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VaiTroDAO {

    /**
     * Lấy tất cả vai trò
     */
    public List<VaiTroDTO> getAll() throws SQLException {
        List<VaiTroDTO> danhSachVT = new ArrayList<>();
        String sql = "SELECT * FROM VaiTro ORDER BY ma_vai_tro";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                VaiTroDTO vt = new VaiTroDTO();
                vt.setMaVaiTro(rs.getInt("ma_vai_tro"));
                vt.setTenVaiTro(rs.getString("ten_vai_tro"));
                danhSachVT.add(vt);
            }
        }
        return danhSachVT;
    }

    /**
     * Lấy vai trò theo mã
     */
    public VaiTroDTO getById(int maVaiTro) throws SQLException {
        String sql = "SELECT * FROM VaiTro WHERE ma_vai_tro = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maVaiTro);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                VaiTroDTO vt = new VaiTroDTO();
                vt.setMaVaiTro(rs.getInt("ma_vai_tro"));
                vt.setTenVaiTro(rs.getString("ten_vai_tro"));
                return vt;
            }
        }
        return null;
    }
}
