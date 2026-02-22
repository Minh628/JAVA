/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: SinhVienExcelImporter - Import danh sách sinh viên từ Excel
 * Sử dụng Apache POI
 * 
 * Cấp: Admin
 */
package util;

import dto.SinhVienDTO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Import danh sách sinh viên từ file Excel (.xlsx, .xls)
 * 
 * Định dạng file Excel yêu cầu:
 * Cột A: Tên đăng nhập (MSSV)
 * Cột B: Họ
 * Cột C: Tên
 * Cột D: Mật khẩu (nếu trống sẽ dùng mặc định)
 * Cột E: Email
 * Cột F: Mã ngành (số)
 */
public class SinhVienExcelImporter {
    
    private static final int COL_TEN_DANG_NHAP = 0;
    private static final int COL_HO = 1;
    private static final int COL_TEN = 2;
    private static final int COL_MAT_KHAU = 3;
    private static final int COL_EMAIL = 4;
    private static final int COL_MA_NGANH = 5;
    
    private static final String DEFAULT_PASSWORD = "123456";
    
    /**
     * Hiển thị dialog chọn file và import sinh viên
     * 
     * @param parent Panel cha cho dialog
     * @return Danh sách sinh viên đọc được, hoặc null nếu hủy
     */
    public static List<SinhVienDTO> importFromExcel(JPanel parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để import");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Excel Files (*.xlsx, *.xls)", "xlsx", "xls"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return readExcelFile(parent, file);
        }
        
        return null;
    }
    
    /**
     * Đọc file Excel và trả về danh sách sinh viên
     */
    public static List<SinhVienDTO> readExcelFile(JPanel parent, File file) {
        List<SinhVienDTO> danhSach = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = createWorkbook(fis, file.getName())) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            
            // Bỏ qua dòng tiêu đề (row 0)
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }
                
                try {
                    SinhVienDTO sv = parseRow(row, i + 1);
                    if (sv != null) {
                        danhSach.add(sv);
                    }
                } catch (Exception e) {
                    errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            // Hiển thị kết quả
            if (!errors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Đã import ").append(danhSach.size()).append(" sinh viên.\n");
                sb.append("Có ").append(errors.size()).append(" lỗi:\n\n");
                for (int i = 0; i < Math.min(errors.size(), 10); i++) {
                    sb.append("• ").append(errors.get(i)).append("\n");
                }
                if (errors.size() > 10) {
                    sb.append("... và ").append(errors.size() - 10).append(" lỗi khác.");
                }
                
                JOptionPane.showMessageDialog(parent, sb.toString(),
                    "Kết quả Import", JOptionPane.WARNING_MESSAGE);
            } else if (!danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(parent,
                    "Đã đọc " + danhSach.size() + " sinh viên từ file Excel.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            
            return danhSach;
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                "Lỗi đọc file: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Tạo Workbook phù hợp với định dạng file
     */
    private static Workbook createWorkbook(FileInputStream fis, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else {
            return new HSSFWorkbook(fis);
        }
    }
    
    /**
     * Kiểm tra dòng có trống không
     */
    private static boolean isEmptyRow(Row row) {
        Cell firstCell = row.getCell(COL_TEN_DANG_NHAP);
        return firstCell == null || getCellValueAsString(firstCell).trim().isEmpty();
    }
    
    /**
     * Parse một dòng Excel thành SinhVienDTO
     */
    private static SinhVienDTO parseRow(Row row, int rowNum) throws Exception {
        SinhVienDTO sv = new SinhVienDTO();
        
        // Tên đăng nhập (bắt buộc)
        String tenDangNhap = getCellValueAsString(row.getCell(COL_TEN_DANG_NHAP)).trim();
        if (tenDangNhap.isEmpty()) {
            throw new Exception("Tên đăng nhập không được trống");
        }
        sv.setTenDangNhap(tenDangNhap);
        
        // Họ (bắt buộc)
        String ho = getCellValueAsString(row.getCell(COL_HO)).trim();
        if (ho.isEmpty()) {
            throw new Exception("Họ không được trống");
        }
        sv.setHo(ho);
        
        // Tên (bắt buộc)
        String ten = getCellValueAsString(row.getCell(COL_TEN)).trim();
        if (ten.isEmpty()) {
            throw new Exception("Tên không được trống");
        }
        sv.setTen(ten);
        
        // Mật khẩu (tùy chọn, mặc định "123456")
        String matKhau = getCellValueAsString(row.getCell(COL_MAT_KHAU)).trim();
        if (matKhau.isEmpty()) {
            matKhau = DEFAULT_PASSWORD;
        }
        sv.setMatKhau(matKhau);
        
        // Email (tùy chọn)
        String email = getCellValueAsString(row.getCell(COL_EMAIL)).trim();
        sv.setEmail(email);
        
        // Mã ngành (bắt buộc)
        int maNganh = getCellValueAsInt(row.getCell(COL_MA_NGANH));
        if (maNganh <= 0) {
            throw new Exception("Mã ngành không hợp lệ");
        }
        sv.setMaNganh(maNganh);
        
        // Mặc định
        sv.setMaVaiTro(3); // Sinh viên
        sv.setTrangThai(true);
        
        return sv;
    }
    
    /**
     * Lấy giá trị ô dưới dạng String
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                // Chuyển số thành string, tránh format khoa học
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
    
    /**
     * Lấy giá trị ô dưới dạng int
     */
    private static int getCellValueAsInt(Cell cell) {
        if (cell == null) {
            return 0;
        }
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }
    
    /**
     * Tạo file Excel mẫu để hướng dẫn người dùng
     */
    public static void createTemplateFile(JPanel parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file mẫu");
        fileChooser.setSelectedFile(new File("MauImportSinhVien.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("DanhSachSinhVien");
                
                // Style cho header
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font headerFont = workbook.createFont();
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                
                // Tạo header
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Tên đăng nhập (MSSV)*", "Họ*", "Tên*", 
                    "Mật khẩu", "Email", "Mã ngành*"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, 20 * 256);
                }
                
                // Dòng ví dụ
                Row sampleRow = sheet.createRow(1);
                sampleRow.createCell(0).setCellValue("SV001");
                sampleRow.createCell(1).setCellValue("Nguyễn Văn");
                sampleRow.createCell(2).setCellValue("An");
                sampleRow.createCell(3).setCellValue("123456");
                sampleRow.createCell(4).setCellValue("an.nv@email.com");
                sampleRow.createCell(5).setCellValue(1);
                
                // Ghi file
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                    workbook.write(fos);
                }
                
                JOptionPane.showMessageDialog(parent,
                    "Đã tạo file mẫu thành công!\n" + file.getAbsolutePath() +
                    "\n\nGhi chú: Các cột có dấu * là bắt buộc.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Lỗi tạo file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
