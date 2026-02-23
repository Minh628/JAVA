/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: GiangVienExcelImporter - Import danh sách giảng viên từ Excel
 * Sử dụng Apache POI
 * 
 * Cấp: Admin
 */
package util;

import dto.GiangVienDTO;
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
 * Import danh sách giảng viên từ file Excel (.xlsx, .xls)
 * 
 * Định dạng file Excel yêu cầu:
 * Cột A: Tên đăng nhập (mã giảng viên)
 * Cột B: Họ
 * Cột C: Tên
 * Cột D: Mật khẩu (nếu trống sẽ dùng mặc định)
 * Cột E: Email
 * Cột F: Mã khoa (số)
 */
public class GiangVienExcelImporter {
    
    private static final int COL_TEN_DANG_NHAP = 0;
    private static final int COL_HO = 1;
    private static final int COL_TEN = 2;
    private static final int COL_MAT_KHAU = 3;
    private static final int COL_EMAIL = 4;
    private static final int COL_MA_KHOA = 5;
    
    private static final String DEFAULT_PASSWORD = "123456";
    
    /**
     * Hiển thị dialog chọn file và import giảng viên
     * 
     * @param parent Panel cha cho dialog
     * @return Danh sách giảng viên đọc được, hoặc null nếu hủy
     */
    public static List<GiangVienDTO> importFromExcel(JPanel parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để import giảng viên");
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
     * Đọc file Excel và trả về danh sách giảng viên
     */
    public static List<GiangVienDTO> readExcelFile(JPanel parent, File file) {
        List<GiangVienDTO> danhSach = new ArrayList<>();
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
                    GiangVienDTO gv = parseRow(row, i + 1);
                    if (gv != null) {
                        danhSach.add(gv);
                    }
                } catch (Exception e) {
                    errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            // Hiển thị kết quả
            if (!errors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Đã import ").append(danhSach.size()).append(" giảng viên.\n");
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
                    "Đã đọc " + danhSach.size() + " giảng viên từ file Excel.",
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
     * Parse một dòng Excel thành GiangVienDTO
     */
    private static GiangVienDTO parseRow(Row row, int rowNum) throws Exception {
        // Tên đăng nhập (bắt buộc)
        String tenDangNhap = getCellString(row.getCell(COL_TEN_DANG_NHAP));
        if (tenDangNhap == null || tenDangNhap.isEmpty()) {
            throw new Exception("Thiếu tên đăng nhập");
        }
        
        // Họ (bắt buộc)
        String ho = getCellString(row.getCell(COL_HO));
        if (ho == null || ho.isEmpty()) {
            throw new Exception("Thiếu họ");
        }
        
        // Tên (bắt buộc)
        String ten = getCellString(row.getCell(COL_TEN));
        if (ten == null || ten.isEmpty()) {
            throw new Exception("Thiếu tên");
        }
        
        // Mật khẩu (tùy chọn, mặc định 123456)
        String matKhau = getCellString(row.getCell(COL_MAT_KHAU));
        if (matKhau == null || matKhau.isEmpty()) {
            matKhau = DEFAULT_PASSWORD;
        }
        
        // Email (tùy chọn)
        String email = getCellString(row.getCell(COL_EMAIL));
        
        // Mã khoa (bắt buộc)
        int maKhoa = getCellInt(row.getCell(COL_MA_KHOA));
        if (maKhoa <= 0) {
            throw new Exception("Mã khoa không hợp lệ");
        }
        
        // Tạo DTO
        GiangVienDTO gv = new GiangVienDTO();
        gv.setTenDangNhap(tenDangNhap);
        gv.setHo(ho);
        gv.setTen(ten);
        gv.setMatKhau(matKhau);
        gv.setEmail(email);
        gv.setMaKhoa(maKhoa);
        gv.setMaVaiTro(2);  // Mặc định vai trò Giảng viên
        
        return gv;
    }
    
    /**
     * Tạo Workbook từ file .xlsx hoặc .xls
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
        for (int i = 0; i < 6; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Lấy giá trị String từ Cell
     */
    private static String getCellString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
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
                return null;
        }
    }
    
    /**
     * Lấy giá trị int từ Cell
     */
    private static int getCellInt(Cell cell) {
        if (cell == null) return 0;
        
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
        fileChooser.setDialogTitle("Lưu file mẫu giảng viên");
        fileChooser.setSelectedFile(new File("MauImportGiangVien.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("DanhSachGiangVien");
                
                // Style cho header
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font headerFont = workbook.createFont();
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                
                // Tạo header
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Tên đăng nhập*", "Họ*", "Tên*", 
                    "Mật khẩu", "Email", "Mã khoa*"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, 20 * 256);
                }
                
                // Dòng ví dụ 1
                Row sampleRow1 = sheet.createRow(1);
                sampleRow1.createCell(0).setCellValue("GV001");
                sampleRow1.createCell(1).setCellValue("Trần Thị");
                sampleRow1.createCell(2).setCellValue("Bình");
                sampleRow1.createCell(3).setCellValue("123456");
                sampleRow1.createCell(4).setCellValue("binh.tt@email.com");
                sampleRow1.createCell(5).setCellValue(1);
                
                // Dòng ví dụ 2
                Row sampleRow2 = sheet.createRow(2);
                sampleRow2.createCell(0).setCellValue("GV002");
                sampleRow2.createCell(1).setCellValue("Lê Văn");
                sampleRow2.createCell(2).setCellValue("Cường");
                sampleRow2.createCell(3).setCellValue("");  // Để trống -> mặc định 123456
                sampleRow2.createCell(4).setCellValue("cuong.lv@email.com");
                sampleRow2.createCell(5).setCellValue(2);
                
                // Ghi file
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                    workbook.write(fos);
                }
                
                JOptionPane.showMessageDialog(parent,
                    "Đã tạo file mẫu thành công!\n" + file.getAbsolutePath() +
                    "\n\nGhi chú:\n" +
                    "• Các cột có dấu * là bắt buộc\n" +
                    "• Mật khẩu để trống sẽ tự động dùng '123456'\n" +
                    "• Mã khoa phải tồn tại trong hệ thống",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Lỗi tạo file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
