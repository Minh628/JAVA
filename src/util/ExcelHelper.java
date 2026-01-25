/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: ExcelHelper - Xuất/Nhập dữ liệu Excel
 */
package util;

import dto.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {
    
    /**
     * Xuất danh sách sinh viên ra file CSV
     */
    public static boolean exportSinhVienToCSV(List<SinhVienDTO> danhSach, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("Mã SV,Họ,Tên,Tên đăng nhập,Email,Ngành,Trạng thái");
            
            // Data
            for (SinhVienDTO sv : danhSach) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                    sv.getMaSV(),
                    sv.getHo(),
                    sv.getTen(),
                    sv.getTenDangNhap(),
                    sv.getEmail(),
                    sv.getTenNganh(),
                    sv.isTrangThai() ? "Hoạt động" : "Khóa"
                );
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xuất danh sách giảng viên ra file CSV
     */
    public static boolean exportGiangVienToCSV(List<GiangVienDTO> danhSach, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("Mã GV,Họ,Tên,Tên đăng nhập,Email,Khoa,Vai trò,Trạng thái");
            
            // Data
            for (GiangVienDTO gv : danhSach) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                    gv.getMaGV(),
                    gv.getHo(),
                    gv.getTen(),
                    gv.getTenDangNhap(),
                    gv.getEmail(),
                    gv.getTenKhoa(),
                    gv.getTenVaiTro(),
                    gv.isTrangThai() ? "Hoạt động" : "Khóa"
                );
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xuất kết quả thi ra file CSV
     */
    public static boolean exportKetQuaThiToCSV(List<BaiThiDTO> danhSach, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println("Mã bài thi,Mã SV,Tên sinh viên,Đề thi,Ngày thi,Số câu đúng,Số câu sai,Điểm số");
            
            // Data
            for (BaiThiDTO bt : danhSach) {
                writer.printf("%d,%s,%s,%s,%s,%d,%d,%.2f%n",
                    bt.getMaBaiThi(),
                    bt.getMaSoSV(),
                    bt.getTenSV(),
                    bt.getTenDeThi(),
                    bt.getNgayThi() != null ? bt.getNgayThi().toString() : "",
                    bt.getSoCauDung(),
                    bt.getSoCauSai(),
                    bt.getDiemSo()
                );
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Nhập danh sách sinh viên từ file CSV
     */
    public static List<SinhVienDTO> importSinhVienFromCSV(String filePath) {
        List<SinhVienDTO> danhSach = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    SinhVienDTO sv = new SinhVienDTO();
                    sv.setHo(parts[0].trim());
                    sv.setTen(parts[1].trim());
                    sv.setTenDangNhap(parts[2].trim());
                    sv.setEmail(parts[3].trim());
                    sv.setMatKhau(parts[4].trim()); // Mật khẩu mặc định
                    sv.setTrangThai(true);
                    danhSach.add(sv);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return danhSach;
    }
    
    /**
     * Xuất câu hỏi ra file CSV
     */
    public static boolean exportCauHoiToCSV(List<CauHoiDTO> danhSach, String filePath) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {
            // BOM for UTF-8
            writer.print('\ufeff');
            
            // Header
            writer.println("Mã câu hỏi,Nội dung,Loại,Mức độ,Đáp án A,Đáp án B,Đáp án C,Đáp án D,Đáp án đúng");
            
            // Data
            for (CauHoiDTO ch : danhSach) {
                writer.printf("%d,\"%s\",%s,%s,\"%s\",\"%s\",\"%s\",\"%s\",%s%n",
                    ch.getMaCauHoi(),
                    escapeCSV(ch.getNoiDungCauHoi()),
                    ch.getLoaiCauHoi(),
                    ch.getMucDo(),
                    escapeCSV(ch.getNoiDungA()),
                    escapeCSV(ch.getNoiDungB()),
                    escapeCSV(ch.getNoiDungC()),
                    escapeCSV(ch.getNoiDungD()),
                    ch.getNoiDungDung()
                );
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Escape ký tự đặc biệt trong CSV
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"").replace("\n", " ");
    }
}
