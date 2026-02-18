/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: PDFExporter - Xuất báo cáo PDF cho thống kê
 * Lưu ý: Cần thêm thư viện iText vào lib/ để sử dụng đầy đủ
 */
package util;

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
 * PDFExporter - Xuất báo cáo thống kê ra file PDF/HTML
 * Hiện tại sử dụng HTML thay vì PDF do chưa có iText
 */
public class PDFExporter {
    
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    /**
     * Xuất thống kê tổng quan ra file HTML
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
        
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("Báo cáo thống kê tổng quan"));
        
        // Thông tin thời gian
        html.append("<div class='info'>");
        html.append("<p><strong>Từ ngày:</strong> ").append(sdf.format(tuNgay)).append("</p>");
        html.append("<p><strong>Đến ngày:</strong> ").append(sdf.format(denNgay)).append("</p>");
        html.append("</div>");
        
        // Bảng thống kê
        html.append("<table>");
        html.append("<tr><th>Chỉ số</th><th>Giá trị</th></tr>");
        
        appendRow(html, "Tổng số bài thi", data.get("tongBaiThi"));
        appendRow(html, "Tổng số đề thi", data.get("tongDeThi"));
        appendRow(html, "Điểm trung bình", formatNumber(data.get("diemTrungBinh"), 2));
        appendRow(html, "Số sinh viên đạt", data.get("soDat"));
        appendRow(html, "Số sinh viên rớt", data.get("soRot"));
        appendRow(html, "Tỷ lệ đạt", formatNumber(data.get("tyLeDat"), 1) + "%");
        
        html.append("</table>");
        html.append(getHtmlFooter());
        
        return saveAndOpen(parent, html.toString(), "ThongKe_TongQuan");
    }
    
    /**
     * Xuất thống kê theo nhóm (Khoa/Ngành/Học phần...) ra file HTML
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
        
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader(tieuDe));
        
        // Thông tin thời gian
        html.append("<div class='info'>");
        html.append("<p><strong>Từ ngày:</strong> ").append(sdf.format(tuNgay)).append("</p>");
        html.append("<p><strong>Đến ngày:</strong> ").append(sdf.format(denNgay)).append("</p>");
        html.append("<p><strong>Tổng số dòng:</strong> ").append(data.size()).append("</p>");
        html.append("</div>");
        
        // Bảng dữ liệu
        html.append("<table>");
        
        // Header
        html.append("<tr>");
        html.append("<th>STT</th>");
        for (String col : columnNames) {
            html.append("<th>").append(col).append("</th>");
        }
        html.append("</tr>");
        
        // Data rows
        int stt = 1;
        for (Object[] row : data) {
            html.append("<tr>");
            html.append("<td class='center'>").append(stt++).append("</td>");
            for (int i = 0; i < row.length; i++) {
                Object value = row[i];
                String className = (value instanceof Number && i > 0) ? "number" : "";
                html.append("<td class='").append(className).append("'>");
                
                if (value instanceof Float || value instanceof Double) {
                    html.append(formatNumber(value, 2));
                } else {
                    html.append(value != null ? value.toString() : "-");
                }
                
                html.append("</td>");
            }
            html.append("</tr>");
        }
        
        html.append("</table>");
        html.append(getHtmlFooter());
        
        // Tạo tên file từ tiêu đề
        String fileName = tieuDe.replaceAll("[^a-zA-Z0-9]", "_");
        return saveAndOpen(parent, html.toString(), fileName);
    }
    
    /**
     * Xuất thống kê khoa theo quý (cross-tabulation)
     */
    public static boolean exportThongKeKhoaTheoQuy(
            JPanel parent,
            List<Object[]> data,
            int nam) {
        
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu để xuất!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        StringBuilder html = new StringBuilder();
        html.append(getHtmlHeader("Thống kê điểm TB theo Khoa và Quý - Năm " + nam));
        
        // Bảng cross-tabulation
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>Tên Khoa</th>");
        html.append("<th>Q1</th>");
        html.append("<th>Q2</th>");
        html.append("<th>Q3</th>");
        html.append("<th>Q4</th>");
        html.append("<th>TB Năm</th>");
        html.append("</tr>");
        
        for (Object[] row : data) {
            html.append("<tr>");
            html.append("<td>").append(row[0]).append("</td>");
            for (int i = 1; i <= 5; i++) {
                html.append("<td class='number'>");
                html.append(row[i] != null ? formatNumber(row[i], 2) : "-");
                html.append("</td>");
            }
            html.append("</tr>");
        }
        
        html.append("</table>");
        html.append(getHtmlFooter());
        
        return saveAndOpen(parent, html.toString(), "ThongKe_Khoa_Quy_" + nam);
    }
    
    // ==================== Helper Methods ====================
    
    private static String getHtmlHeader(String title) {
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Arial, sans-serif; 
                        margin: 40px;
                        color: #333;
                    }
                    h1 { 
                        color: #2980b9; 
                        text-align: center;
                        border-bottom: 2px solid #2980b9;
                        padding-bottom: 15px;
                    }
                    .info {
                        background: #f8f9fa;
                        padding: 15px;
                        border-radius: 5px;
                        margin-bottom: 20px;
                    }
                    .info p { margin: 5px 0; }
                    table { 
                        width: 100%%; 
                        border-collapse: collapse; 
                        margin-top: 20px;
                    }
                    th, td { 
                        border: 1px solid #ddd; 
                        padding: 12px 8px; 
                        text-align: left;
                    }
                    th { 
                        background: #2980b9; 
                        color: white; 
                        font-weight: bold;
                    }
                    tr:nth-child(even) { background: #f8f9fa; }
                    tr:hover { background: #e8f4f8; }
                    .center { text-align: center; }
                    .number { text-align: right; }
                    .footer {
                        margin-top: 30px;
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                        border-top: 1px solid #ddd;
                        padding-top: 15px;
                    }
                </style>
            </head>
            <body>
                <h1>%s</h1>
            """.formatted(title, title);
    }
    
    private static String getHtmlFooter() {
        return """
                <div class="footer">
                    <p>Hệ thống thi trắc nghiệm trực tuyến - EXAM MANAGEMENT</p>
                    <p>Xuất lúc: %s</p>
                </div>
            </body>
            </html>
            """.formatted(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
    }
    
    private static void appendRow(StringBuilder html, String label, Object value) {
        html.append("<tr>");
        html.append("<td><strong>").append(label).append("</strong></td>");
        html.append("<td>").append(value != null ? value.toString() : "-").append("</td>");
        html.append("</tr>");
    }
    
    private static String formatNumber(Object value, int decimals) {
        if (value == null) return "-";
        if (value instanceof Number) {
            return String.format("%." + decimals + "f", ((Number) value).doubleValue());
        }
        return value.toString();
    }
    
    private static boolean saveAndOpen(JPanel parent, String content, String defaultName) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu báo cáo");
        fc.setSelectedFile(new File(defaultName + "_" + sdfFile.format(new java.util.Date()) + ".html"));
        fc.setFileFilter(new FileNameExtensionFilter("HTML Files (*.html)", "html"));
        
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }
            
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.print(content);
                writer.flush();
                
                JOptionPane.showMessageDialog(parent, 
                    "Đã xuất báo cáo thành công!\n" + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Mở file trong trình duyệt
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(file.toURI());
                }
                
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, 
                    "Lỗi khi lưu file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
    }
}
