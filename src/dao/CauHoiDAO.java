/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DAO: CauHoiDAO - Data Access Object cho bảng CauHoi
 */
package dao;

import config.DatabaseHelper;
import dto.CauHoiDKDTO;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CauHoiDAO {
    
    /**
     * Lấy tất cả câu hỏi
     */
    public List<CauHoiDTO> getAll() throws SQLException {
        List<CauHoiDTO> danhSachCH = new ArrayList<>();
        String sql = "SELECT ch.*, " +
                     "mc.noi_dung_A, mc.noi_dung_B, mc.noi_dung_C, mc.noi_dung_D, mc.noi_dung_dung AS dap_an_mc, " +
                     "dk.danh_sach_tu, dk.noi_dung_dung AS dap_an_dk " +
                     "FROM CauHoi ch " +
                     "LEFT JOIN CauHoiMC mc ON ch.ma_cau_hoi = mc.ma_cau_hoi_MC " +
                     "LEFT JOIN CauHoiDK dk ON ch.ma_cau_hoi = dk.ma_cau_hoi_DK " +
                     "ORDER BY ch.ma_cau_hoi";
        
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                danhSachCH.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachCH;
    }
    
    /**
     * Lấy câu hỏi theo môn học
     */
    public List<CauHoiDTO> getByMon(int maMon) throws SQLException {
        List<CauHoiDTO> danhSachCH = new ArrayList<>();
        String sql = "SELECT ch.*, " +
                     "mc.noi_dung_A, mc.noi_dung_B, mc.noi_dung_C, mc.noi_dung_D, mc.noi_dung_dung AS dap_an_mc, " +
                     "dk.danh_sach_tu, dk.noi_dung_dung AS dap_an_dk " +
                     "FROM CauHoi ch " +
                     "LEFT JOIN CauHoiMC mc ON ch.ma_cau_hoi = mc.ma_cau_hoi_MC " +
                     "LEFT JOIN CauHoiDK dk ON ch.ma_cau_hoi = dk.ma_cau_hoi_DK " +
                     "WHERE ch.ma_mon = ? ORDER BY ch.ma_cau_hoi";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maMon);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSachCH.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachCH;
    }
    
    /**
     * Lấy câu hỏi theo giảng viên
     */
    public List<CauHoiDTO> getByGiangVien(int maGV) throws SQLException {
        List<CauHoiDTO> danhSachCH = new ArrayList<>();
        String sql = "SELECT ch.*, " +
                     "mc.noi_dung_A, mc.noi_dung_B, mc.noi_dung_C, mc.noi_dung_D, mc.noi_dung_dung AS dap_an_mc, " +
                     "dk.danh_sach_tu, dk.noi_dung_dung AS dap_an_dk " +
                     "FROM CauHoi ch " +
                     "LEFT JOIN CauHoiMC mc ON ch.ma_cau_hoi = mc.ma_cau_hoi_MC " +
                     "LEFT JOIN CauHoiDK dk ON ch.ma_cau_hoi = dk.ma_cau_hoi_DK " +
                     "WHERE ch.ma_gv = ? ORDER BY ch.ma_cau_hoi";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maGV);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSachCH.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachCH;
    }
    
    /**
     * Lấy câu hỏi theo mức độ
     */
    public List<CauHoiDTO> getByMucDo(int maMon, String mucDo) throws SQLException {
        List<CauHoiDTO> danhSachCH = new ArrayList<>();
        String sql = "SELECT ch.*, " +
                     "mc.noi_dung_A, mc.noi_dung_B, mc.noi_dung_C, mc.noi_dung_D, mc.noi_dung_dung AS dap_an_mc, " +
                     "dk.danh_sach_tu, dk.noi_dung_dung AS dap_an_dk " +
                     "FROM CauHoi ch " +
                     "LEFT JOIN CauHoiMC mc ON ch.ma_cau_hoi = mc.ma_cau_hoi_MC " +
                     "LEFT JOIN CauHoiDK dk ON ch.ma_cau_hoi = dk.ma_cau_hoi_DK " +
                     "WHERE ch.ma_mon = ? AND ch.muc_do = ? ORDER BY RAND()";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maMon);
            pstmt.setString(2, mucDo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                danhSachCH.add(mapResultSetToDTO(rs));
            }
        }
        return danhSachCH;
    }
    
    /**
     * Lấy câu hỏi theo mã
     */
    public CauHoiDTO getById(int maCauHoi) throws SQLException {
        String sql = "SELECT ch.*, " +
                     "mc.noi_dung_A, mc.noi_dung_B, mc.noi_dung_C, mc.noi_dung_D, mc.noi_dung_dung AS dap_an_mc, " +
                     "dk.danh_sach_tu, dk.noi_dung_dung AS dap_an_dk " +
                     "FROM CauHoi ch " +
                     "LEFT JOIN CauHoiMC mc ON ch.ma_cau_hoi = mc.ma_cau_hoi_MC " +
                     "LEFT JOIN CauHoiDK dk ON ch.ma_cau_hoi = dk.ma_cau_hoi_DK " +
                     "WHERE ch.ma_cau_hoi = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maCauHoi);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDTO(rs);
            }
        }
        return null;
    }
    
    /**
     * Thêm câu hỏi mới
     */
    public boolean insert(CauHoiDTO cauHoi) throws SQLException {
        String sqlCauHoi = "INSERT INTO CauHoi (ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) " +
                          "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCauHoi, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, cauHoi.getMaMon());
                pstmt.setInt(2, cauHoi.getMaGV());
                pstmt.setString(3, cauHoi.getNoiDungCauHoi());
                pstmt.setString(4, cauHoi.getMucDo());
                pstmt.setString(5, cauHoi.getLoaiCauHoi());
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        cauHoi.setMaCauHoi(rs.getInt(1));
                    }
                    
                    // Thêm chi tiết câu hỏi
                    if (CauHoiDTO.LOAI_TRAC_NGHIEM.equals(cauHoi.getLoaiCauHoi())) {
                        insertCauHoiMC(conn, cauHoi);
                    } else {
                        insertCauHoiDK(conn, cauHoi);
                    }
                    
                    conn.commit();
                    return true;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return false;
    }
    
    /**
     * Thêm chi tiết câu hỏi trắc nghiệm
     */
    private void insertCauHoiMC(Connection conn, CauHoiDTO cauHoi) throws SQLException {
        String sql = "INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cauHoi.getMaCauHoi());
            pstmt.setString(2, cauHoi.getNoiDungA());
            pstmt.setString(3, cauHoi.getNoiDungB());
            pstmt.setString(4, cauHoi.getNoiDungC());
            pstmt.setString(5, cauHoi.getNoiDungD());
            pstmt.setString(6, cauHoi.getNoiDungDung());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Thêm chi tiết câu hỏi điền khuyết
     */
    private void insertCauHoiDK(Connection conn, CauHoiDTO cauHoi) throws SQLException {
        String sql = "INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cauHoi.getMaCauHoi());
            pstmt.setString(2, cauHoi.getDanhSachTu());
            pstmt.setString(3, cauHoi.getNoiDungDung());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Cập nhật câu hỏi
     */
    public boolean update(CauHoiDTO cauHoi) throws SQLException {
        String sqlCauHoi = "UPDATE CauHoi SET ma_mon = ?, noi_dung_cau_hoi = ?, muc_do = ? " +
                          "WHERE ma_cau_hoi = ?";
        
        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCauHoi)) {
                pstmt.setInt(1, cauHoi.getMaMon());
                pstmt.setString(2, cauHoi.getNoiDungCauHoi());
                pstmt.setString(3, cauHoi.getMucDo());
                pstmt.setInt(4, cauHoi.getMaCauHoi());
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Cập nhật chi tiết câu hỏi
                    if (CauHoiDTO.LOAI_TRAC_NGHIEM.equals(cauHoi.getLoaiCauHoi())) {
                        updateCauHoiMC(conn, cauHoi);
                    } else {
                        updateCauHoiDK(conn, cauHoi);
                    }
                    
                    conn.commit();
                    return true;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return false;
    }
    
    /**
     * Cập nhật chi tiết câu hỏi trắc nghiệm
     */
    private void updateCauHoiMC(Connection conn, CauHoiDTO cauHoi) throws SQLException {
        String sql = "UPDATE CauHoiMC SET noi_dung_A = ?, noi_dung_B = ?, noi_dung_C = ?, " +
                     "noi_dung_D = ?, noi_dung_dung = ? WHERE ma_cau_hoi_MC = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cauHoi.getNoiDungA());
            pstmt.setString(2, cauHoi.getNoiDungB());
            pstmt.setString(3, cauHoi.getNoiDungC());
            pstmt.setString(4, cauHoi.getNoiDungD());
            pstmt.setString(5, cauHoi.getNoiDungDung());
            pstmt.setInt(6, cauHoi.getMaCauHoi());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Cập nhật chi tiết câu hỏi điền khuyết
     */
    private void updateCauHoiDK(Connection conn, CauHoiDTO cauHoi) throws SQLException {
        String sql = "UPDATE CauHoiDK SET danh_sach_tu = ?, noi_dung_dung = ? WHERE ma_cau_hoi_DK = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cauHoi.getDanhSachTu());
            pstmt.setString(2, cauHoi.getNoiDungDung());
            pstmt.setInt(3, cauHoi.getMaCauHoi());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Xóa câu hỏi
     */
    public boolean delete(int maCauHoi) throws SQLException {
        String sql = "DELETE FROM CauHoi WHERE ma_cau_hoi = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maCauHoi);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Map ResultSet sang DTO - tạo đúng loại object dựa vào loại câu hỏi
     */
    private CauHoiDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        String loaiCauHoi = rs.getString("loai_cau_hoi");
        CauHoiDTO ch;
        
        // Tạo đúng loại object dựa vào loại câu hỏi
        if (CauHoiDTO.LOAI_TRAC_NGHIEM.equals(loaiCauHoi)) {
            CauHoiMCDTO mc = new CauHoiMCDTO();
            mc.setNoiDungA(rs.getString("noi_dung_A"));
            mc.setNoiDungB(rs.getString("noi_dung_B"));
            mc.setNoiDungC(rs.getString("noi_dung_C"));
            mc.setNoiDungD(rs.getString("noi_dung_D"));
            mc.setNoiDungDung(rs.getString("dap_an_mc"));
            ch = mc;
        } else {
            CauHoiDKDTO dk = new CauHoiDKDTO();
            dk.setDanhSachTu(rs.getString("danh_sach_tu"));
            dk.setNoiDungDung(rs.getString("dap_an_dk"));
            ch = dk;
        }
        
        // Set các trường chung
        ch.setMaCauHoi(rs.getInt("ma_cau_hoi"));
        ch.setMaMon(rs.getInt("ma_mon"));
        ch.setMaGV(rs.getInt("ma_gv"));
        ch.setNoiDungCauHoi(rs.getString("noi_dung_cau_hoi"));
        ch.setMucDo(rs.getString("muc_do"));
        
        return ch;
    }
}
