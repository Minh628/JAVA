/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: BangDiemExcelExporter - Xuất bảng điểm bài thi ra Excel
 * Sử dụng Apache POI
 * 
 * Cấp: Giảng viên
 */
package util;

import bus.DeThiBUS;
import bus.HocPhanBUS;
import bus.SinhVienBUS;
import dto.BaiThiDTO;
import dto.DeThiDTO;
import dto.HocPhanDTO;
import dto.SinhVienDTO;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Xuất bảng điểm bài thi ra file Excel (.xlsx)
 * 
 * Bảng điểm bao gồm:
 * - Thông tin đề thi, môn học
 * - Danh sách sinh viên với điểm số
 * - Thống kê tổng quan
 */
public class BangDiemExcelExporter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    private SinhVienBUS sinhVienBUS = new SinhVienBUS();
    private DeThiBUS deThiBUS = new DeThiBUS();
    private HocPhanBUS hocPhanBUS = new HocPhanBUS();
    
    /**
     * Xuất danh sách bài thi ra file Excel
     * 
     * @param parent Panel cha cho dialog
     * @param danhSachBaiThi Danh sách bài thi cần xuất
     * @param tieuDe Tiêu đề báo cáo
     * @return true nếu xuất thành công
     */
    public boolean exportToExcel(JPanel parent, List<BaiThiDTO> danhSachBaiThi, String tieuDe) {
        if (danhSachBaiThi == null || danhSachBaiThi.isEmpty()) {
            JOptionPane.showMessageDialog(parent, 
                "Không có dữ liệu để xuất!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu bảng điểm");
        String fileName = "BangDiem_" + FILE_DATE_FORMAT.format(new Date()) + ".xlsx";
        fileChooser.setSelectedFile(new File(fileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            return writeExcelFile(parent, file, danhSachBaiThi, tieuDe);
        }
        
        return false;
    }
    
    /**
     * Ghi dữ liệu ra file Excel
     */
    private boolean writeExcelFile(JPanel parent, File file, List<BaiThiDTO> danhSachBaiThi, String tieuDe) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("BangDiem");
            
            // Tạo các styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle passStyle = createPassStyle(workbook);
            CellStyle failStyle = createFailStyle(workbook);
            CellStyle infoStyle = createInfoStyle(workbook);
            
            int rowNum = 0;
            
            // === TIÊU ĐỀ ===
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(tieuDe != null ? tieuDe : "BẢNG ĐIỂM BÀI THI");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
            
            // Dòng trống
            rowNum++;
            
            // === THÔNG TIN CHUNG ===
            Row infoRow1 = sheet.createRow(rowNum++);
            Cell infoCell1 = infoRow1.createCell(0);
            infoCell1.setCellValue("Ngày xuất: " + DATE_FORMAT.format(new Date()));
            infoCell1.setCellStyle(infoStyle);
            
            Row infoRow2 = sheet.createRow(rowNum++);
            Cell infoCell2 = infoRow2.createCell(0);
            infoCell2.setCellValue("Tổng số bài thi: " + danhSachBaiThi.size());
            infoCell2.setCellStyle(infoStyle);
            
            // Tính thống kê
            float tongDiem = 0;
            int soDat = 0, soRot = 0;
            for (BaiThiDTO bt : danhSachBaiThi) {
                tongDiem += bt.getDiemSo();
                if (bt.getDiemSo() >= 5.0f) {
                    soDat++;
                } else {
                    soRot++;
                }
            }
            float diemTB = tongDiem / danhSachBaiThi.size();
            
            Row infoRow3 = sheet.createRow(rowNum++);
            Cell infoCell3 = infoRow3.createCell(0);
            infoCell3.setCellValue(String.format("Điểm trung bình: %.2f | Đạt: %d | Rớt: %d | Tỷ lệ đạt: %.1f%%", 
                diemTB, soDat, soRot, (soDat * 100.0 / danhSachBaiThi.size())));
            infoCell3.setCellStyle(infoStyle);
            
            // Dòng trống
            rowNum++;
            
            // === HEADER BẢNG ===
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"STT", "MSSV", "Họ và Tên", "Đề thi", "Môn học", 
                "Ngày thi", "Số câu đúng", "Số câu sai", "Điểm"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // === DỮ LIỆU ===
            int stt = 1;
            for (BaiThiDTO bt : danhSachBaiThi) {
                Row dataRow = sheet.createRow(rowNum++);
                
                // Lấy thông tin liên quan
                SinhVienDTO sv = sinhVienBUS.getById(bt.getMaSV());
                DeThiDTO deThi = deThiBUS.getById(bt.getMaDeThi());
                HocPhanDTO hocPhan = deThi != null ? hocPhanBUS.getById(deThi.getMaHocPhan()) : null;
                
                // STT
                Cell cellSTT = dataRow.createCell(0);
                cellSTT.setCellValue(stt++);
                cellSTT.setCellStyle(numberStyle);
                
                // MSSV
                Cell cellMSSV = dataRow.createCell(1);
                cellMSSV.setCellValue(sv != null ? sv.getTenDangNhap() : "");
                cellMSSV.setCellStyle(dataStyle);
                
                // Họ tên
                Cell cellHoTen = dataRow.createCell(2);
                cellHoTen.setCellValue(sv != null ? sv.getHoTen() : "");
                cellHoTen.setCellStyle(dataStyle);
                
                // Đề thi
                Cell cellDeThi = dataRow.createCell(3);
                cellDeThi.setCellValue(deThi != null ? deThi.getTenDeThi() : "");
                cellDeThi.setCellStyle(dataStyle);
                
                // Môn học
                Cell cellMon = dataRow.createCell(4);
                cellMon.setCellValue(hocPhan != null ? hocPhan.getTenMon() : "");
                cellMon.setCellStyle(dataStyle);
                
                // Ngày thi
                Cell cellNgay = dataRow.createCell(5);
                cellNgay.setCellValue(bt.getNgayThi() != null ? DATE_FORMAT.format(bt.getNgayThi()) : "");
                cellNgay.setCellStyle(dataStyle);
                
                // Số câu đúng
                Cell cellDung = dataRow.createCell(6);
                int tongSoCau = deThi != null ? deThi.getSoCauHoi() : 0;
                cellDung.setCellValue(bt.getSoCauDung() + "/" + tongSoCau);
                cellDung.setCellStyle(numberStyle);
                
                // Số câu sai
                Cell cellSai = dataRow.createCell(7);
                cellSai.setCellValue(bt.getSoCauSai());
                cellSai.setCellStyle(numberStyle);
                
                // Điểm
                Cell cellDiem = dataRow.createCell(8);
                cellDiem.setCellValue(bt.getDiemSo());
                cellDiem.setCellStyle(bt.getDiemSo() >= 5.0f ? passStyle : failStyle);
            }
            
            // === ĐIỀU CHỈNH ĐỘ RỘNG CỘT ===
            sheet.setColumnWidth(0, 6 * 256);   // STT
            sheet.setColumnWidth(1, 12 * 256);  // MSSV
            sheet.setColumnWidth(2, 25 * 256);  // Họ tên
            sheet.setColumnWidth(3, 25 * 256);  // Đề thi
            sheet.setColumnWidth(4, 20 * 256);  // Môn học
            sheet.setColumnWidth(5, 12 * 256);  // Ngày thi
            sheet.setColumnWidth(6, 12 * 256);  // Số câu đúng
            sheet.setColumnWidth(7, 10 * 256);  // Số câu sai
            sheet.setColumnWidth(8, 8 * 256);   // Điểm
            
            // Ghi file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            
            JOptionPane.showMessageDialog(parent,
                "Đã xuất bảng điểm thành công!\n" + file.getAbsolutePath(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Mở file
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    // Bỏ qua nếu không mở được
                }
            }
            
            return true;
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent,
                "Lỗi xuất file: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ==================== STYLES ====================
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createPassStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFont(font);
        
        return style;
    }
    
    private CellStyle createFailStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        
        return style;
    }
    
    private CellStyle createInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        return style;
    }
}
