/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: PDFExporter - Xuất báo cáo PDF
 */
package util;

import dto.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PDFExporter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Xuất kết quả thi ra file HTML (có thể in thành PDF)
     */
    public static boolean exportKetQuaThi(BaiThiDTO baiThi, List<ChiTietBaiThiDTO> chiTiet, String filePath) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {
            
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='vi'>");
            writer.println("<head>");
            writer.println("<meta charset='UTF-8'>");
            writer.println("<title>Kết quả thi</title>");
            writer.println("<style>");
            writer.println("body { font-family: 'Times New Roman', serif; margin: 40px; }");
            writer.println("h1 { text-align: center; color: #333; }");
            writer.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            writer.println("th, td { border: 1px solid #333; padding: 10px; text-align: left; }");
            writer.println("th { background-color: #f0f0f0; }");
            writer.println(".info { margin-bottom: 20px; }");
            writer.println(".info p { margin: 5px 0; }");
            writer.println(".dung { color: green; }");
            writer.println(".sai { color: red; }");
            writer.println(".footer { margin-top: 30px; text-align: center; font-style: italic; }");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            
            // Header
            writer.println("<h1>KẾT QUẢ BÀI THI</h1>");
            
            // Thông tin bài thi
            writer.println("<div class='info'>");
            writer.printf("<p><strong>Họ tên:</strong> %s</p>%n", baiThi.getTenSV());
            writer.printf("<p><strong>Mã số SV:</strong> %s</p>%n", baiThi.getMaSoSV());
            writer.printf("<p><strong>Đề thi:</strong> %s</p>%n", baiThi.getTenDeThi());
            writer.printf("<p><strong>Môn học:</strong> %s</p>%n", baiThi.getTenHocPhan());
            writer.printf("<p><strong>Ngày thi:</strong> %s</p>%n", 
                baiThi.getNgayThi() != null ? baiThi.getNgayThi().toString() : "");
            writer.printf("<p><strong>Số câu đúng:</strong> %d/%d</p>%n", 
                baiThi.getSoCauDung(), baiThi.getTongSoCau());
            writer.printf("<p><strong>Điểm số:</strong> <span style='font-size: 1.5em; font-weight: bold;'>%.2f/10</span></p>%n", 
                baiThi.getDiemSo());
            writer.println("</div>");
            
            // Bảng chi tiết
            writer.println("<table>");
            writer.println("<tr><th>STT</th><th>Câu hỏi</th><th>Đáp án của bạn</th><th>Đáp án đúng</th><th>Kết quả</th></tr>");
            
            int stt = 1;
            for (ChiTietBaiThiDTO ct : chiTiet) {
                String ketQua = ct.isLaDung() ? "<span class='dung'>Đúng</span>" : "<span class='sai'>Sai</span>";
                writer.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>%n",
                    stt++,
                    ct.getNoiDungCauHoi(),
                    ct.getDapAnSV() != null ? ct.getDapAnSV() : "-",
                    ct.getDapAnDung(),
                    ketQua
                );
            }
            writer.println("</table>");
            
            // Footer
            writer.printf("<div class='footer'>Xuất ngày: %s</div>%n", DATE_FORMAT.format(new Date()));
            
            writer.println("</body>");
            writer.println("</html>");
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xuất bảng điểm lớp ra file HTML
     */
    public static boolean exportBangDiem(List<BaiThiDTO> danhSach, String tenDeThi, String filePath) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "UTF-8"))) {
            
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='vi'>");
            writer.println("<head>");
            writer.println("<meta charset='UTF-8'>");
            writer.println("<title>Bảng điểm</title>");
            writer.println("<style>");
            writer.println("body { font-family: 'Times New Roman', serif; margin: 40px; }");
            writer.println("h1, h2 { text-align: center; }");
            writer.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            writer.println("th, td { border: 1px solid #333; padding: 8px; text-align: center; }");
            writer.println("th { background-color: #f0f0f0; }");
            writer.println(".dat { color: green; }");
            writer.println(".truot { color: red; }");
            writer.println(".footer { margin-top: 30px; text-align: right; }");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            
            writer.println("<h1>BẢNG ĐIỂM THI</h1>");
            writer.printf("<h2>%s</h2>%n", tenDeThi);
            
            writer.println("<table>");
            writer.println("<tr><th>STT</th><th>Mã SV</th><th>Họ tên</th><th>Số câu đúng</th><th>Điểm</th><th>Kết quả</th></tr>");
            
            int stt = 1;
            for (BaiThiDTO bt : danhSach) {
                String ketQua = bt.getDiemSo() >= 5 ? 
                    "<span class='dat'>Đạt</span>" : "<span class='truot'>Không đạt</span>";
                writer.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%d/%d</td><td>%.2f</td><td>%s</td></tr>%n",
                    stt++,
                    bt.getMaSoSV(),
                    bt.getTenSV(),
                    bt.getSoCauDung(),
                    bt.getTongSoCau(),
                    bt.getDiemSo(),
                    ketQua
                );
            }
            writer.println("</table>");
            
            // Thống kê
            int tongSV = danhSach.size();
            long soDat = danhSach.stream().filter(bt -> bt.getDiemSo() >= 5).count();
            
            writer.println("<div class='footer'>");
            writer.printf("<p>Tổng số sinh viên: %d</p>%n", tongSV);
            writer.printf("<p>Số sinh viên đạt: %d (%.1f%%)</p>%n", soDat, (float)soDat/tongSV*100);
            writer.printf("<p>Xuất ngày: %s</p>%n", DATE_FORMAT.format(new Date()));
            writer.println("</div>");
            
            writer.println("</body>");
            writer.println("</html>");
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
