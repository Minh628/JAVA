/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: CauHoiExcelImporter - Import ngân hàng câu hỏi từ Excel
 * Sử dụng Apache POI
 * 
 * Cấp: Giảng viên
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import dto.CauHoiDKDTO;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;

/**
 * Import ngân hàng câu hỏi từ file Excel (.xlsx, .xls)
 * 
 * Định dạng file Excel cho câu hỏi TRẮC NGHIỆM:
 * Cột A: Loại câu hỏi (MC: Trắc nghiệm, DK: Điền khuyết)
 * Cột B: Nội dung câu hỏi
 * Cột C: Đáp án A
 * Cột D: Đáp án B
 * Cột E: Đáp án C
 * Cột F: Đáp án D
 * Cột G: Đáp án đúng (A/B/C/D)
 * Cột H: Mức độ (De/TrungBinh/Kho)
 * Cột I: Mã học phần (số)
 * 
 * Định dạng file Excel cho câu hỏi ĐIỀN KHUYẾT:
 * Cột A: Loại câu hỏi (DK)
 * Cột B: Nội dung câu hỏi (dùng [...] để đánh dấu chỗ trống)
 * Cột C: Đáp án đúng (các từ cách nhau bởi dấu phẩy nếu nhiều chỗ trống)
 * Cột D: Danh sách từ gợi ý (cách nhau bởi dấu phẩy)
 * Cột E-F: Để trống
 * Cột G: Để trống
 * Cột H: Mức độ (De/TrungBinh/Kho)
 * Cột I: Mã học phần (số)
 */
public class CauHoiExcelImporter {
    
    private static final int COL_LOAI = 0;
    private static final int COL_NOI_DUNG = 1;
    private static final int COL_DAP_AN_A = 2;
    private static final int COL_DAP_AN_B = 3;
    private static final int COL_DAP_AN_C = 4;
    private static final int COL_DAP_AN_D = 5;
    private static final int COL_DAP_AN_DUNG = 6;
    private static final int COL_MUC_DO = 7;
    private static final int COL_MA_HOC_PHAN = 8;
    
    /**
     * Hiển thị dialog chọn file và import câu hỏi
     * 
     * @param parent Panel cha cho dialog
     * @param maGV Mã giảng viên (để gán cho câu hỏi)
     * @param defaultMaHocPhan Mã học phần mặc định (nếu không có trong file)
     * @return Danh sách câu hỏi đọc được, hoặc null nếu hủy
     */
    public static List<CauHoiDTO> importFromExcel(JPanel parent, int maGV, int defaultMaHocPhan) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để import câu hỏi");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Excel Files (*.xlsx, *.xls)", "xlsx", "xls"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return readExcelFile(parent, file, maGV, defaultMaHocPhan);
        }
        
        return null;
    }
    
    /**
     * Đọc file Excel và trả về danh sách câu hỏi
     */
    public static List<CauHoiDTO> readExcelFile(JPanel parent, File file, int maGV, int defaultMaHocPhan) {
        List<CauHoiDTO> danhSach = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        int countMC = 0, countDK = 0;
        
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
                    CauHoiDTO cauHoi = parseRow(row, i + 1, maGV, defaultMaHocPhan);
                    if (cauHoi != null) {
                        danhSach.add(cauHoi);
                        if (cauHoi instanceof CauHoiMCDTO) {
                            countMC++;
                        } else {
                            countDK++;
                        }
                    }
                } catch (Exception e) {
                    errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }
            
            // Hiển thị kết quả
            StringBuilder sb = new StringBuilder();
            sb.append("Kết quả import câu hỏi:\n\n");
            sb.append("• Trắc nghiệm: ").append(countMC).append(" câu\n");
            sb.append("• Điền khuyết: ").append(countDK).append(" câu\n");
            sb.append("• Tổng cộng: ").append(danhSach.size()).append(" câu\n");
            
            if (!errors.isEmpty()) {
                sb.append("\nCó ").append(errors.size()).append(" lỗi:\n");
                for (int i = 0; i < Math.min(errors.size(), 10); i++) {
                    sb.append("• ").append(errors.get(i)).append("\n");
                }
                if (errors.size() > 10) {
                    sb.append("... và ").append(errors.size() - 10).append(" lỗi khác.");
                }
                JOptionPane.showMessageDialog(parent, sb.toString(),
                    "Kết quả Import", JOptionPane.WARNING_MESSAGE);
            } else if (!danhSach.isEmpty()) {
                JOptionPane.showMessageDialog(parent, sb.toString(),
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
        Cell noiDungCell = row.getCell(COL_NOI_DUNG);
        return noiDungCell == null || getCellValueAsString(noiDungCell).trim().isEmpty();
    }
    
    /**
     * Parse một dòng Excel thành CauHoiDTO
     */
    private static CauHoiDTO parseRow(Row row, int rowNum, int maGV, int defaultMaHocPhan) throws Exception {
        // Xác định loại câu hỏi
        String loai = getCellValueAsString(row.getCell(COL_LOAI)).trim().toUpperCase();
        if (loai.isEmpty()) {
            loai = "MC"; // Mặc định là trắc nghiệm
        }
        
        // Nội dung câu hỏi (bắt buộc)
        String noiDung = getCellValueAsString(row.getCell(COL_NOI_DUNG)).trim();
        if (noiDung.isEmpty()) {
            throw new Exception("Nội dung câu hỏi không được trống");
        }
        
        // Mức độ
        String mucDo = getCellValueAsString(row.getCell(COL_MUC_DO)).trim();
        if (mucDo.isEmpty()) {
            mucDo = CauHoiDTO.MUC_DO_TRUNG_BINH;
        } else {
            // Chuẩn hóa mức độ
            mucDo = normalizeMucDo(mucDo);
        }
        
        // Mã học phần
        int maHocPhan = getCellValueAsInt(row.getCell(COL_MA_HOC_PHAN));
        if (maHocPhan <= 0) {
            maHocPhan = defaultMaHocPhan;
        }
        
        if (loai.equals("DK")) {
            // Câu hỏi điền khuyết
            return parseDienKhuyet(row, noiDung, mucDo, maGV, maHocPhan);
        } else {
            // Câu hỏi trắc nghiệm (mặc định)
            return parseTracNghiem(row, noiDung, mucDo, maGV, maHocPhan);
        }
    }
    
    /**
     * Parse câu hỏi trắc nghiệm
     */
    private static CauHoiMCDTO parseTracNghiem(Row row, String noiDung, String mucDo, 
            int maGV, int maHocPhan) throws Exception {
        
        CauHoiMCDTO cauHoi = new CauHoiMCDTO();
        cauHoi.setNoiDungCauHoi(noiDung);
        cauHoi.setMucDo(mucDo);
        cauHoi.setMaGV(maGV);
        cauHoi.setMaMon(maHocPhan);
        
        // Đáp án A (bắt buộc)
        String dapAnA = getCellValueAsString(row.getCell(COL_DAP_AN_A)).trim();
        if (dapAnA.isEmpty()) {
            throw new Exception("Đáp án A không được trống");
        }
        cauHoi.setNoiDungA(dapAnA);
        
        // Đáp án B (bắt buộc)
        String dapAnB = getCellValueAsString(row.getCell(COL_DAP_AN_B)).trim();
        if (dapAnB.isEmpty()) {
            throw new Exception("Đáp án B không được trống");
        }
        cauHoi.setNoiDungB(dapAnB);
        
        // Đáp án C (tùy chọn)
        String dapAnC = getCellValueAsString(row.getCell(COL_DAP_AN_C)).trim();
        cauHoi.setNoiDungC(dapAnC.isEmpty() ? null : dapAnC);
        
        // Đáp án D (tùy chọn)
        String dapAnD = getCellValueAsString(row.getCell(COL_DAP_AN_D)).trim();
        cauHoi.setNoiDungD(dapAnD.isEmpty() ? null : dapAnD);
        
        // Đáp án đúng (bắt buộc) - lưu nội dung thực của đáp án
        String dapAnDungInput = getCellValueAsString(row.getCell(COL_DAP_AN_DUNG)).trim();
        if (dapAnDungInput.isEmpty()) {
            throw new Exception("Đáp án đúng không được trống");
        }
        
        String dapAnDung;
        // Nếu nhập A/B/C/D thì lấy nội dung tương ứng
        if (dapAnDungInput.toUpperCase().matches("[ABCD]")) {
            switch (dapAnDungInput.toUpperCase()) {
                case "A":
                    dapAnDung = dapAnA;
                    break;
                case "B":
                    dapAnDung = dapAnB;
                    break;
                case "C":
                    if (dapAnC == null || dapAnC.isEmpty()) {
                        throw new Exception("Đáp án C không tồn tại");
                    }
                    dapAnDung = dapAnC;
                    break;
                case "D":
                    if (dapAnD == null || dapAnD.isEmpty()) {
                        throw new Exception("Đáp án D không tồn tại");
                    }
                    dapAnDung = dapAnD;
                    break;
                default:
                    dapAnDung = dapAnDungInput;
            }
        } else {
            // Nếu nhập nội dung trực tiếp, kiểm tra xem có khớp với đáp án nào không
            if (dapAnDungInput.equalsIgnoreCase(dapAnA) ||
                dapAnDungInput.equalsIgnoreCase(dapAnB) ||
                (dapAnC != null && dapAnDungInput.equalsIgnoreCase(dapAnC)) ||
                (dapAnD != null && dapAnDungInput.equalsIgnoreCase(dapAnD))) {
                dapAnDung = dapAnDungInput;
            } else {
                throw new Exception("Đáp án đúng '" + dapAnDungInput + "' không khớp với A, B, C hoặc D");
            }
        }
        cauHoi.setDapAnDung(dapAnDung);
        
        return cauHoi;
    }
    
    /**
     * Parse câu hỏi điền khuyết
     */
    private static CauHoiDKDTO parseDienKhuyet(Row row, String noiDung, String mucDo, 
            int maGV, int maHocPhan) throws Exception {
        
        CauHoiDKDTO cauHoi = new CauHoiDKDTO();
        cauHoi.setNoiDungCauHoi(noiDung);
        cauHoi.setMucDo(mucDo);
        cauHoi.setMaGV(maGV);
        cauHoi.setMaMon(maHocPhan);
        
        // Đáp án đúng (cột C cho điền khuyết)
        String dapAnDung = getCellValueAsString(row.getCell(COL_DAP_AN_A)).trim();
        if (dapAnDung.isEmpty()) {
            throw new Exception("Đáp án đúng không được trống");
        }
        cauHoi.setDapAnDung(dapAnDung);
        
        // Danh sách từ gợi ý (cột D)
        String tuGoiY = getCellValueAsString(row.getCell(COL_DAP_AN_B)).trim();
        cauHoi.setDanhSachTu(tuGoiY);
        
        return cauHoi;
    }
    
    /**
     * Chuẩn hóa mức độ
     */
    private static String normalizeMucDo(String input) {
        input = input.toLowerCase();
        if (input.contains("de") || input.contains("dễ") || input.equals("1")) {
            return CauHoiDTO.MUC_DO_DE;
        } else if (input.contains("kho") || input.contains("khó") || input.equals("3")) {
            return CauHoiDTO.MUC_DO_KHO;
        } else {
            return CauHoiDTO.MUC_DO_TRUNG_BINH;
        }
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
        fileChooser.setDialogTitle("Lưu file mẫu câu hỏi");
        fileChooser.setSelectedFile(new File("MauImportCauHoi.xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            try (Workbook workbook = new XSSFWorkbook()) {
                // === Sheet 1: Câu hỏi mẫu (cả MC và DK) ===
                Sheet sheetCauHoi = workbook.createSheet("CauHoiMau");
                createTracNghiemTemplate(workbook, sheetCauHoi);
                
                // === Sheet 2: Hướng dẫn ===
                Sheet sheetGuide = workbook.createSheet("HuongDan");
                createGuideSheet(workbook, sheetGuide);
                
                // Ghi file
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                    workbook.write(fos);
                }
                
                JOptionPane.showMessageDialog(parent,
                    "Đã tạo file mẫu thành công!\n" + file.getAbsolutePath() +
                    "\n\nFile có 2 sheet:\n" +
                    "1. CauHoiMau - Mẫu câu hỏi (Trắc nghiệm + Điền khuyết)\n" +
                    "2. HuongDan - Hướng dẫn sử dụng",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Lỗi tạo file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void createTracNghiemTemplate(Workbook workbook, Sheet sheet) {
        // Style cho header
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Loại*", "Nội dung câu hỏi*", "Đáp án A*", "Đáp án B*", 
            "Đáp án C", "Đáp án D", "Đáp án đúng*", "Mức độ", "Mã HP"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // === CÂU HỎI TRẮC NGHIỆM (MC) ===
        
        // Dữ liệu mẫu - Câu 1: Câu hỏi địa lý (Dễ)
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("MC");
        row1.createCell(1).setCellValue("Thủ đô của Việt Nam là gì?");
        row1.createCell(2).setCellValue("Hà Nội");
        row1.createCell(3).setCellValue("TP.HCM");
        row1.createCell(4).setCellValue("Đà Nẵng");
        row1.createCell(5).setCellValue("Huế");
        row1.createCell(6).setCellValue("Hà Nội");
        row1.createCell(7).setCellValue("De");
        row1.createCell(8).setCellValue(1);
        
        // Câu 2: Câu hỏi toán (Dễ)
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("MC");
        row2.createCell(1).setCellValue("Kết quả của phép tính 15 x 4 là bao nhiêu?");
        row2.createCell(2).setCellValue("45");
        row2.createCell(3).setCellValue("50");
        row2.createCell(4).setCellValue("60");
        row2.createCell(5).setCellValue("55");
        row2.createCell(6).setCellValue("60");
        row2.createCell(7).setCellValue("De");
        row2.createCell(8).setCellValue(1);
        
        // Câu 3: Câu hỏi lập trình (Trung bình)
        Row row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("MC");
        row3.createCell(1).setCellValue("Trong Java, từ khóa nào dùng để khai báo hằng số?");
        row3.createCell(2).setCellValue("const");
        row3.createCell(3).setCellValue("final");
        row3.createCell(4).setCellValue("static");
        row3.createCell(5).setCellValue("constant");
        row3.createCell(6).setCellValue("final");
        row3.createCell(7).setCellValue("TrungBinh");
        row3.createCell(8).setCellValue(1);
        
        // Câu 4: Câu hỏi lập trình (Khó)
        Row row4 = sheet.createRow(4);
        row4.createCell(0).setCellValue("MC");
        row4.createCell(1).setCellValue("Design Pattern nào thuộc nhóm Creational?");
        row4.createCell(2).setCellValue("Observer");
        row4.createCell(3).setCellValue("Strategy");
        row4.createCell(4).setCellValue("Singleton");
        row4.createCell(5).setCellValue("Decorator");
        row4.createCell(6).setCellValue("Singleton");
        row4.createCell(7).setCellValue("Kho");
        row4.createCell(8).setCellValue(1);
        
        // === CÂU HỎI ĐIỀN KHUYẾT (DK) ===
        
        // Câu 5: Điền khuyết - Địa lý (Dễ)
        Row row5 = sheet.createRow(5);
        row5.createCell(0).setCellValue("DK");
        row5.createCell(1).setCellValue("Thủ đô của Việt Nam là [...]");
        row5.createCell(2).setCellValue("Hà Nội");
        row5.createCell(3).setCellValue("Hà Nội|TP.HCM|Đà Nẵng|Huế");
        row5.createCell(4).setCellValue("");
        row5.createCell(5).setCellValue("");
        row5.createCell(6).setCellValue("");
        row5.createCell(7).setCellValue("De");
        row5.createCell(8).setCellValue(1);
        
        // Câu 6: Điền khuyết - Lập trình (Trung bình)
        Row row6 = sheet.createRow(6);
        row6.createCell(0).setCellValue("DK");
        row6.createCell(1).setCellValue("Java là ngôn ngữ lập trình [...]");
        row6.createCell(2).setCellValue("hướng đối tượng");
        row6.createCell(3).setCellValue("hướng đối tượng|thủ tục|hàm|kịch bản");
        row6.createCell(4).setCellValue("");
        row6.createCell(5).setCellValue("");
        row6.createCell(6).setCellValue("");
        row6.createCell(7).setCellValue("TrungBinh");
        row6.createCell(8).setCellValue(1);
        
        // Câu 7: Điền khuyết - OOP (Khó)
        Row row7 = sheet.createRow(7);
        row7.createCell(0).setCellValue("DK");
        row7.createCell(1).setCellValue("Bốn tính chất của OOP là: Đóng gói, Kế thừa, Đa hình và [...]");
        row7.createCell(2).setCellValue("Trừu tượng");
        row7.createCell(3).setCellValue("Trừu tượng|Abstraction|Interface|Abstract");
        row7.createCell(4).setCellValue("");
        row7.createCell(5).setCellValue("");
        row7.createCell(6).setCellValue("");
        row7.createCell(7).setCellValue("Kho");
        row7.createCell(8).setCellValue(1);
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, i == 1 ? 60 * 256 : 20 * 256);
        }
    }
    

    
    private static void createGuideSheet(Workbook workbook, Sheet sheet) {
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        
        int row = 0;
        
        Row r0 = sheet.createRow(row++);
        Cell c0 = r0.createCell(0);
        c0.setCellValue("HƯỚNG DẪN IMPORT CÂU HỎI TỪ EXCEL");
        c0.setCellStyle(titleStyle);
        
        row++;
        sheet.createRow(row++).createCell(0).setCellValue("1. LOẠI CÂU HỎI:");
        sheet.createRow(row++).createCell(0).setCellValue("   - MC: Trắc nghiệm (Multiple Choice) - Chọn 1 trong 2-4 đáp án");
        sheet.createRow(row++).createCell(0).setCellValue("   - DK: Điền khuyết (Fill-in-the-blank) - Điền từ vào chỗ trống");
        
        row++;
        sheet.createRow(row++).createCell(0).setCellValue("2. MỨC ĐỘ:");
        sheet.createRow(row++).createCell(0).setCellValue("   - De hoặc Dễ: Câu hỏi dễ");
        sheet.createRow(row++).createCell(0).setCellValue("   - TrungBinh hoặc Trung bình: Câu hỏi trung bình");
        sheet.createRow(row++).createCell(0).setCellValue("   - Kho hoặc Khó: Câu hỏi khó");
        
        row++;
        sheet.createRow(row++).createCell(0).setCellValue("3. CÂU HỎI TRẮC NGHIỆM (MC):");
        sheet.createRow(row++).createCell(0).setCellValue("   - Bắt buộc: Loại, Nội dung, Đáp án A, Đáp án B, Đáp án đúng");
        sheet.createRow(row++).createCell(0).setCellValue("   - Tùy chọn: Đáp án C, D (có thể để trống nếu chỉ cần 2 đáp án)");
        sheet.createRow(row++).createCell(0).setCellValue("   - Đáp án đúng: Nhập nội dung đáp án đúng (ví dụ: 'Hà Nội') hoặc A/B/C/D");
        
        row++;
        sheet.createRow(row++).createCell(0).setCellValue("4. CÂU HỎI ĐIỀN KHUYẾT (DK):");
        sheet.createRow(row++).createCell(0).setCellValue("   - Sử dụng [...] để đánh dấu chỗ trống trong câu hỏi");
        sheet.createRow(row++).createCell(0).setCellValue("   - Ví dụ 1 chỗ trống: 'Thủ đô của Việt Nam là [...]'");
        sheet.createRow(row++).createCell(0).setCellValue("   - Ví dụ 2 chỗ trống: '[...] dùng để khai báo biến và [...] là hằng'");
        sheet.createRow(row++).createCell(0).setCellValue("   - Đáp án nhiều chỗ trống: cách nhau bằng dấu phẩy (int,final)");
        sheet.createRow(row++).createCell(0).setCellValue("   - Từ gợi ý: danh sách các từ gợi ý, cách nhau bằng dấu phẩy");
        
        row++;
        sheet.createRow(row++).createCell(0).setCellValue("5. LƯU Ý QUAN TRỌNG:");
        sheet.createRow(row++).createCell(0).setCellValue("   - Các cột có dấu * là bắt buộc");
        sheet.createRow(row++).createCell(0).setCellValue("   - Dòng đầu tiên là tiêu đề, KHÔNG được import");
        sheet.createRow(row++).createCell(0).setCellValue("   - Có thể trộn cả 2 loại câu hỏi MC và DK trong 1 sheet");
        sheet.createRow(row++).createCell(0).setCellValue("   - Nếu không điền Mã HP, hệ thống sẽ dùng học phần được chọn");
        
        sheet.setColumnWidth(0, 70 * 256);
    }
}
