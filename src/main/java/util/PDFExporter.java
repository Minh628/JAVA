/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: PDFExporter - Xuất báo cáo PDF cho thống kê
 * Sử dụng thư viện iText 7
 */
package util;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import java.awt.Desktop;
import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * PDFExporter - Xuất báo cáo thống kê ra file PDF
 * Sử dụng iText 7 với hỗ trợ font tiếng Việt
 */
public class PDFExporter {
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    // Màu sắc
    private static final DeviceRgb HEADER_BG_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb TITLE_COLOR = new DeviceRgb(44, 62, 80);
    private static final DeviceRgb ALTERNATE_ROW_COLOR = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(189, 195, 199);
    private static final DeviceRgb INFO_COLOR = new DeviceRgb(127, 140, 141);
    
    /**
     * Load font hỗ trợ tiếng Việt
     */
    private static PdfFont loadVietnameseFont() throws IOException {
        try {
            // Thử load font Arial từ Windows
            String fontPath = "C:/Windows/Fonts/arial.ttf";
            return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
        } catch (Exception e) {
            try {
                // Fallback: Times New Roman
                String fontPath = "C:/Windows/Fonts/times.ttf";
                return PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
            } catch (Exception e2) {
                // Fallback cuối: Helvetica
                return PdfFontFactory.createFont();
            }
        }
    }
    
    /**
     * Xuất thống kê tổng quan ra file PDF
     */
    public static boolean exportThongKeTongQuan(
            JPanel parent,
            Map<String, Object> data,
            Date tuNgay,
            Date denNgay) {
        
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu để xuất!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu báo cáo PDF");
        fc.setSelectedFile(new File("ThongKe_TongQuan_" + sdfFile.format(new java.util.Date()) + ".pdf"));
        fc.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
        
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }
        
        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(40, 40, 40, 40);
            
            PdfFont font = loadVietnameseFont();
            
            // Tiêu đề
            document.add(new Paragraph("BÁO CÁO THỐNG KÊ TỔNG QUAN")
                    .setFont(font)
                    .setFontSize(18)
                    .setBold()
                    .setFontColor(TITLE_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));
            
            document.add(new Paragraph("Hệ thống thi trắc nghiệm trực tuyến")
                    .setFont(font)
                    .setFontSize(11)
                    .setFontColor(INFO_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));
            
            // Thông tin thời gian
            document.add(new Paragraph("Từ ngày: " + sdf.format(tuNgay) + "  |  Đến ngày: " + sdf.format(denNgay))
                    .setFont(font)
                    .setFontSize(11)
                    .setMarginBottom(15));
            
            // Bảng thống kê
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Header
            addHeaderCell(table, "Chỉ số", font);
            addHeaderCell(table, "Giá trị", font);
            
            // Data rows
            int row = 0;
            addDataRow(table, font, "Tổng số bài thi", formatValue(data.get("tongBaiThi")), row++);
            addDataRow(table, font, "Tổng số đề thi", formatValue(data.get("tongDeThi")), row++);
            addDataRow(table, font, "Điểm trung bình", formatNumber(data.get("diemTrungBinh"), 2), row++);
            addDataRow(table, font, "Số sinh viên đạt", formatValue(data.get("soDat")), row++);
            addDataRow(table, font, "Số sinh viên rớt", formatValue(data.get("soRot")), row++);
            addDataRow(table, font, "Tỷ lệ đạt", formatNumber(data.get("tyLeDat"), 1) + "%", row++);
            
            document.add(table);
            
            // Footer
            addFooter(document, font);
            
            document.close();
            
            JOptionPane.showMessageDialog(parent, 
                "Đã xuất báo cáo thành công!\n" + file.getAbsolutePath(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Mở file
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, 
                "Không thể xuất file PDF!\nVui lòng kiểm tra xem file có đang được mở ở phần mềm khác không.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Xuất thống kê theo nhóm (Khoa/Ngành/Học phần...) ra file PDF
     */
    public static boolean exportThongKeTheoNhom(
            JPanel parent,
            String tieuDe,
            String[] columnNames,
            List<Object[]> data,
            Date tuNgay,
            Date denNgay) {
        
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu để xuất!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu báo cáo PDF");
        String fileName = tieuDe.replaceAll("[^a-zA-Z0-9]", "_");
        fc.setSelectedFile(new File(fileName + "_" + sdfFile.format(new java.util.Date()) + ".pdf"));
        fc.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
        
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }
        
        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4.rotate()); // Landscape cho bảng rộng
            document.setMargins(30, 30, 30, 30);
            
            PdfFont font = loadVietnameseFont();
            
            // Tiêu đề
            document.add(new Paragraph(tieuDe.toUpperCase())
                    .setFont(font)
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(TITLE_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));
            
            // Thông tin thời gian
            document.add(new Paragraph("Từ ngày: " + sdf.format(tuNgay) + "  |  Đến ngày: " + sdf.format(denNgay) + 
                    "  |  Tổng số: " + data.size() + " dòng")
                    .setFont(font)
                    .setFontSize(10)
                    .setFontColor(INFO_COLOR)
                    .setMarginBottom(15));
            
            // Tạo bảng với số cột = STT + columnNames
            float[] colWidths = new float[columnNames.length + 1];
            colWidths[0] = 1; // STT
            for (int i = 0; i < columnNames.length; i++) {
                colWidths[i + 1] = 2;
            }
            Table table = new Table(UnitValue.createPercentArray(colWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Header
            addHeaderCell(table, "STT", font);
            for (String col : columnNames) {
                addHeaderCell(table, col, font);
            }
            
            // Data rows
            int stt = 1;
            for (Object[] row : data) {
                boolean isAlternate = (stt % 2 == 0);
                
                // STT
                addDataCellWithStyle(table, String.valueOf(stt), font, isAlternate, TextAlignment.CENTER);
                
                // Data columns
                for (int i = 0; i < row.length; i++) {
                    Object value = row[i];
                    String cellValue;
                    TextAlignment align = TextAlignment.LEFT;
                    
                    if (value instanceof Float || value instanceof Double) {
                        cellValue = formatNumber(value, 2);
                        align = TextAlignment.RIGHT;
                    } else if (value instanceof Number) {
                        cellValue = value.toString();
                        align = TextAlignment.RIGHT;
                    } else {
                        cellValue = value != null ? value.toString() : "-";
                    }
                    
                    addDataCellWithStyle(table, cellValue, font, isAlternate, align);
                }
                
                stt++;
            }
            
            document.add(table);
            addFooter(document, font);
            document.close();
            
            JOptionPane.showMessageDialog(parent, 
                "Đã xuất báo cáo thành công!\n" + file.getAbsolutePath(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, 
                "Không thể xuất file PDF!\nVui lòng kiểm tra xem file có đang được mở ở phần mềm khác không.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // ==================== Helper Methods cho iText ====================
    
    /**
     * Thêm cell header vào bảng
     */
    private static void addHeaderCell(Table table, String content, PdfFont font) {
        Cell cell = new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(10).setBold())
                .setBackgroundColor(HEADER_BG_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(8)
                .setBorder(new SolidBorder(ColorConstants.WHITE, 1));
        table.addHeaderCell(cell);
    }
    
    /**
     * Thêm data row vào bảng (key-value)
     */
    private static void addDataRow(Table table, PdfFont font, String label, String value, int rowIndex) {
        boolean isAlternate = (rowIndex % 2 == 1);
        
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFont(font).setFontSize(10).setBold())
                .setTextAlignment(TextAlignment.LEFT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(6)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f));
        
        Cell valueCell = new Cell()
                .add(new Paragraph(value).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(6)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f));
        
        if (isAlternate) {
            labelCell.setBackgroundColor(ALTERNATE_ROW_COLOR);
            valueCell.setBackgroundColor(ALTERNATE_ROW_COLOR);
        }
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    /**
     * Thêm data cell với style
     */
    private static void addDataCellWithStyle(Table table, String content, PdfFont font, 
            boolean isAlternate, TextAlignment alignment) {
        Cell cell = new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(9))
                .setTextAlignment(alignment)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(5)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f));
        
        if (isAlternate) {
            cell.setBackgroundColor(ALTERNATE_ROW_COLOR);
        }
        
        table.addCell(cell);
    }
    
    /**
     * Thêm footer vào document
     */
    private static void addFooter(Document document, PdfFont font) {
        document.add(new Paragraph("\nHệ thống thi trắc nghiệm trực tuyến - EXAM MANAGEMENT")
                .setFont(font)
                .setFontSize(9)
                .setFontColor(INFO_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));
        
        document.add(new Paragraph("Xuất lúc: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()))
                .setFont(font)
                .setFontSize(9)
                .setFontColor(INFO_COLOR)
                .setTextAlignment(TextAlignment.CENTER));
    }
    
    /**
     * Format giá trị object thành string
     */
    private static String formatValue(Object value) {
        if (value == null) return "-";
        return value.toString();
    }
    
    /**
     * Format số với số chữ số thập phân
     */
    private static String formatNumber(Object value, int decimals) {
        if (value == null) return "-";
        if (value instanceof Number) {
            return String.format("%." + decimals + "f", ((Number) value).doubleValue());
        }
        return value.toString();
    }
}
