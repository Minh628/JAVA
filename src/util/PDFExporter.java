/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Util: PDFExporter - Xuất báo cáo PDF
 * Lưu ý: Các method cần thông tin bổ sung (tên SV, tên đề thi, etc.) phải được truyền vào
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
     * @param baiThi thông tin bài thi
     * @param chiTiet danh sách chi tiết bài thi (mã bài thi, mã câu hỏi, đáp án SV)
     * @param danhSachCauHoi danh sách câu hỏi tương ứng để lấy nội dung và đáp án đúng
     * @param sinhVien thông tin sinh viên
     * @param deThi thông tin đề thi
     * @param hocPhan thông tin học phần
     * @param filePath đường dẫn file xuất
     */
    public static boolean exportKetQuaThi(BaiThiDTO baiThi, List<ChiTietBaiThiDTO> chiTiet,
            List<CauHoiDTO> danhSachCauHoi, SinhVienDTO sinhVien, DeThiDTO deThi,
            HocPhanDTO hocPhan, String filePath) {
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
            String tenSV = sinhVien != null ? sinhVien.getHo() + " " + sinhVien.getTen() : "";
            String maSoSV = sinhVien != null ? String.valueOf(sinhVien.getMaSV()) : "";
            String tenDeThi = deThi != null ? deThi.getTenDeThi() : "";
            String tenHocPhan = hocPhan != null ? hocPhan.getTenMon() : "";
            int tongSoCau = chiTiet.size();
            
            writer.println("<div class='info'>");
            writer.printf("<p><strong>Họ tên:</strong> %s</p>%n", tenSV);
            writer.printf("<p><strong>Mã số SV:</strong> %s</p>%n", maSoSV);
            writer.printf("<p><strong>Đề thi:</strong> %s</p>%n", tenDeThi);
            writer.printf("<p><strong>Môn học:</strong> %s</p>%n", tenHocPhan);
            writer.printf("<p><strong>Ngày thi:</strong> %s</p>%n", 
                baiThi.getNgayThi() != null ? baiThi.getNgayThi().toString() : "");
            writer.printf("<p><strong>Số câu đúng:</strong> %d/%d</p>%n", 
                baiThi.getSoCauDung(), tongSoCau);
            writer.printf("<p><strong>Điểm số:</strong> <span style='font-size: 1.5em; font-weight: bold;'>%.2f/10</span></p>%n", 
                baiThi.getDiemSo());
            writer.println("</div>");
            
            // Bảng chi tiết
            writer.println("<table>");
            writer.println("<tr><th>STT</th><th>Câu hỏi</th><th>Đáp án của bạn</th><th>Đáp án đúng</th><th>Kết quả</th></tr>");
            
            int stt = 1;
            for (ChiTietBaiThiDTO ct : chiTiet) {
                // Tìm câu hỏi tương ứng
                CauHoiDTO cauHoi = null;
                for (CauHoiDTO ch : danhSachCauHoi) {
                    if (ch.getMaCauHoi() == ct.getMaCauHoi()) {
                        cauHoi = ch;
                        break;
                    }
                }
                
                String noiDungCauHoi = cauHoi != null ? cauHoi.getNoiDungCauHoi() : "";
                String dapAnDung = cauHoi != null ? cauHoi.getDapAnDung() : "";
                boolean laDung = kiemTraDapAn(cauHoi, ct.getDapAnSV());
                
                String ketQua = laDung ? "<span class='dung'>Đúng</span>" : "<span class='sai'>Sai</span>";
                writer.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>%n",
                    stt++,
                    noiDungCauHoi,
                    ct.getDapAnSV() != null ? ct.getDapAnSV() : "-",
                    dapAnDung,
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
     * Kiểm tra đáp án
     */
    private static boolean kiemTraDapAn(CauHoiDTO cauHoi, String dapAnSV) {
        if (cauHoi == null || dapAnSV == null || dapAnSV.trim().isEmpty()) {
            return false;
        }
        
        if (cauHoi instanceof CauHoiMCDTO) {
            CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
            String dapAnDung = mc.getNoiDungDung();
            String noiDungDapAnSV = null;
            switch (dapAnSV.toUpperCase()) {
                case "A": noiDungDapAnSV = mc.getNoiDungA(); break;
                case "B": noiDungDapAnSV = mc.getNoiDungB(); break;
                case "C": noiDungDapAnSV = mc.getNoiDungC(); break;
                case "D": noiDungDapAnSV = mc.getNoiDungD(); break;
            }
            return noiDungDapAnSV != null && dapAnDung != null && 
                   noiDungDapAnSV.trim().equalsIgnoreCase(dapAnDung.trim());
        } else if (cauHoi instanceof CauHoiDKDTO) {
            CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
            String dapAnDung = dk.getNoiDungDung();
            return dapAnDung != null && dapAnSV.trim().equalsIgnoreCase(dapAnDung.trim());
        }
        return false;
    }
    
    /**
     * Xuất bảng điểm lớp ra file HTML
     * @param danhSach danh sách bài thi
     * @param danhSachSinhVien danh sách sinh viên tương ứng
     * @param tenDeThi tên đề thi
     * @param tongSoCau tổng số câu hỏi trong đề thi
     * @param filePath đường dẫn file xuất
     */
    public static boolean exportBangDiem(List<BaiThiDTO> danhSach, List<SinhVienDTO> danhSachSinhVien,
            String tenDeThi, int tongSoCau, String filePath) {
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
                // Tìm sinh viên tương ứng
                SinhVienDTO sv = null;
                for (SinhVienDTO s : danhSachSinhVien) {
                    if (s.getMaSV() == bt.getMaSV()) {
                        sv = s;
                        break;
                    }
                }
                String tenSV = sv != null ? sv.getHo() + " " + sv.getTen() : "";
                
                String ketQua = bt.getDiemSo() >= 5 ? 
                    "<span class='dat'>Đạt</span>" : "<span class='truot'>Không đạt</span>";
                writer.printf("<tr><td>%d</td><td>%d</td><td>%s</td><td>%d/%d</td><td>%.2f</td><td>%s</td></tr>%n",
                    stt++,
                    bt.getMaSV(),
                    tenSV,
                    bt.getSoCauDung(),
                    tongSoCau,
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
            writer.printf("<p>Số sinh viên đạt: %d (%.1f%%)</p>%n", soDat, tongSV > 0 ? (float)soDat/tongSV*100 : 0);
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
