/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: ChiTietDiemDialog - Dialog xem chi tiết kết quả bài thi
 * 
 * MÔ TẢ:
 *   - Hiển thị chi tiết từng câu hỏi và đáp án
 *   - So sánh đáp án sinh viên chọn với đáp án đúng
 *   - Đánh dấu màu: Xanh = đúng, Đỏ = sai
 * 
 * CẤU TRÚC GIAO DIỆN:
 *   ┌─────────────────────────────────────────────────┐
 *   │           KếT QUẢ CHI TIẾT                         │
 *   ├─────────────────────────────────────────────────┤
 *   │ Điểm: 8.5/10     Số câu đúng: 17/20               │
 *   ├─────────────────────────────────────────────────┤
 *   │ STT | Nội dung | Đáp án SV | Đáp án đúng | Kết quả │
 *   │ 1   | Câu hỏi...| B         | B            | Đúng   │
 *   │ 2   | Câu hỏi...| A         | C            | Sai    │
 *   │ ... | ...       | ...       | ...          | ...    │
 *   ├─────────────────────────────────────────────────┤
 *   │                   [Đóng]                          │
 *   └─────────────────────────────────────────────────┘
 * 
 * COMPONENTS SỬ DỤNG:
 *   - CustomTable: Bảng chi tiết từng câu
 *   - CustomButton: Nút Đóng
 *   - Tùy chỉnh CellRenderer để tô màu
 * 
 * @see LichSuThiPanel - Gọi để mở dialog
 * @see BaiThiBUS - Lấy thông tin bài thi
 * @see ChiTietBaiThiDTO - Dữ liệu chi tiết bài thi
 * ===========================================================================
 */
package gui.student;

import bus.BaiThiBUS;
import bus.CauHoiBUS;
import bus.DeThiBUS;
import config.Constants;
import dto.BaiThiDTO;
import dto.CauHoiDKDTO;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;
import dto.ChiTietBaiThiDTO;
import dto.DeThiDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ChiTietDiemDialog extends JDialog {
    private int maBaiThi;
    private BaiThiBUS baiThiBUS;
    private CauHoiBUS cauHoiBUS;
    private DeThiBUS deThiBUS;
    
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    private JLabel lblTongKet;
    
    public ChiTietDiemDialog(JFrame parent, int maBaiThi) {
        super(parent, "Chi tiết điểm bài thi", true);
        this.maBaiThi = maBaiThi;
        this.baiThiBUS = new BaiThiBUS();
        this.cauHoiBUS = new CauHoiBUS();
        this.deThiBUS = new DeThiBUS();
        
        initComponents();
        loadData();
        
        setSize(900, 600);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Constants.BACKGROUND_COLOR);
        
        // Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Constants.PRIMARY_COLOR);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTieuDe = new JLabel(" CHI TIẾT ĐIỂM BÀI THI #" + maBaiThi);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Color.WHITE);
        panelHeader.add(lblTieuDe, BorderLayout.WEST);
        
        lblTongKet = new JLabel();
        lblTongKet.setFont(Constants.HEADER_FONT);
        lblTongKet.setForeground(Color.WHITE);
        panelHeader.add(lblTongKet, BorderLayout.EAST);
        
        add(panelHeader, BorderLayout.NORTH);
        
        // Bảng chi tiết
        String[] columns = {"STT", "Loại", "Nội dung câu hỏi", "Đáp án đúng", "Đáp án của bạn", "Kết quả", "Điểm"};
        modelChiTiet = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblChiTiet = new CustomTable(modelChiTiet);
        
        // Renderer để tô màu kết quả
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("Đúng".equals(value)) {
                    c.setForeground(new Color(34, 139, 34));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("Sai".equals(value)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.GRAY);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        // Renderer cho cột điểm
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String diemStr = value.toString();
                if (diemStr.startsWith("+")) {
                    c.setForeground(new Color(34, 139, 34));
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.RED);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblChiTiet);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel nút
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNut.setBackground(Constants.BACKGROUND_COLOR);
        
        CustomButton btnDong = new CustomButton("Đóng", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnDong.setPreferredSize(new Dimension(120, 40));
        btnDong.addActionListener(e -> dispose());
        panelNut.add(btnDong);
        
        add(panelNut, BorderLayout.SOUTH);
    }
    
    private void loadData() {
        modelChiTiet.setRowCount(0);
        
        // Lấy thông tin bài thi
        BaiThiDTO baiThi = baiThiBUS.getById(maBaiThi);
        if (baiThi == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin bài thi!");
            return;
        }
        
        // Lấy đề thi để tính điểm mỗi câu
        DeThiDTO deThi = deThiBUS.getById(baiThi.getMaDeThi());
        int tongSoCau = deThi != null ? deThi.getSoCauHoi() : 1;
        float diemMoiCau = 10.0f / tongSoCau;
        
        // Lấy chi tiết bài thi
        List<ChiTietBaiThiDTO> danhSachChiTiet = baiThiBUS.getChiTietByBaiThi(maBaiThi);
        
        int stt = 1;
        int soCauDung = 0;
        int soCauSai = 0;
        float tongDiem = 0;
        
        for (ChiTietBaiThiDTO ct : danhSachChiTiet) {
            CauHoiDTO cauHoi = cauHoiBUS.getById(ct.getMaCauHoi());
            if (cauHoi == null) continue;
            
            String loaiCauHoi = cauHoi.getLoaiCauHoi();
            String loaiHienThi = CauHoiDTO.LOAI_TRAC_NGHIEM.equals(loaiCauHoi) ? "Trắc nghiệm" : "Điền khuyết";
            
            String noiDung = cauHoi.getNoiDungCauHoi();
            if (noiDung.length() > 50) {
                noiDung = noiDung.substring(0, 50) + "...";
            }
            
            String dapAnDung = getDapAnDungHienThi(cauHoi);
            String dapAnSV = getDapAnSVHienThi(cauHoi, ct.getDapAnSV());
            
            boolean ketQua = kiemTraDapAn(cauHoi, ct.getDapAnSV());
            String ketQuaStr = ketQua ? "Đúng" : "Sai";
            String diemStr;
            
            if (ketQua) {
                soCauDung++;
                tongDiem += diemMoiCau;
                diemStr = String.format("+%.2f", diemMoiCau);
            } else {
                soCauSai++;
                diemStr = "0.00";
            }
            
            modelChiTiet.addRow(new Object[]{
                stt++, loaiHienThi, noiDung, dapAnDung, dapAnSV, ketQuaStr, diemStr
            });
        }
        
        // Cập nhật tổng kết
        lblTongKet.setText(String.format("Đúng: %d | Sai: %d | Điểm: %.2f/10", 
            soCauDung, soCauSai, tongDiem));
    }
    
    /**
     * Lấy đáp án đúng để hiển thị
     */
    private String getDapAnDungHienThi(CauHoiDTO cauHoi) {
        if (cauHoi instanceof CauHoiMCDTO) {
            CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
            String dapAnDung = mc.getDapAnDung(); 
            String noiDungDapAn = "";
            return dapAnDung + ". " + (noiDungDapAn != null ? noiDungDapAn : "");
        } else if (cauHoi instanceof CauHoiDKDTO) {
            CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
            return dk.getDapAnDung() != null ? dk.getDapAnDung().replace("|", ", ") : "";
        }
        return "";
    }
    
    /**
     * Lấy đáp án sinh viên để hiển thị
     */
    private String getDapAnSVHienThi(CauHoiDTO cauHoi, String dapAnSV) {
        if (dapAnSV == null || dapAnSV.trim().isEmpty()) {
            return "(Chưa trả lời)";
        }
        
        if (cauHoi instanceof CauHoiMCDTO) {
            CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
            String dapAnUpper = dapAnSV.trim().toUpperCase();
            
            // Nếu đáp án là ký hiệu A/B/C/D, lấy nội dung tương ứng
            if (dapAnUpper.equals("A") || dapAnUpper.equals("B") || 
                dapAnUpper.equals("C") || dapAnUpper.equals("D")) {
                String noiDung = "";
                switch (dapAnUpper) {
                    case "A": noiDung = mc.getNoiDungA(); break;
                    case "B": noiDung = mc.getNoiDungB(); break;
                    case "C": noiDung = mc.getNoiDungC(); break;
                    case "D": noiDung = mc.getNoiDungD(); break;
                }
                return dapAnSV + ". " + (noiDung != null ? noiDung : "");
            } else {
                // Nếu đáp án là nội dung trực tiếp, xác định ký hiệu tương ứng
                String kyHieu = "";
                if (dapAnSV.equals(mc.getNoiDungA())) kyHieu = "A. ";
                else if (dapAnSV.equals(mc.getNoiDungB())) kyHieu = "B. ";
                else if (dapAnSV.equals(mc.getNoiDungC())) kyHieu = "C. ";
                else if (dapAnSV.equals(mc.getNoiDungD())) kyHieu = "D. ";
                return kyHieu + dapAnSV;
            }
        } else {
            return dapAnSV.replace("|", ", ");
        }
    }
    
    /**
     * Kiểm tra đáp án
     */
    private boolean kiemTraDapAn(CauHoiDTO cauHoi, String dapAnSV) {
        if (dapAnSV == null || dapAnSV.trim().isEmpty()) {
            return false;
        }
        
        if (cauHoi instanceof CauHoiMCDTO) {
            CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
            String dapAnDung = mc.getNoiDungDung();
            String noiDungDapAnSV = null;
            
            // Nếu đáp án là ký hiệu A/B/C/D, chuyển đổi sang nội dung
            String dapAnUpper = dapAnSV.trim().toUpperCase();
            if (dapAnUpper.equals("A") || dapAnUpper.equals("B") || 
                dapAnUpper.equals("C") || dapAnUpper.equals("D")) {
                switch (dapAnUpper) {
                    case "A": noiDungDapAnSV = mc.getNoiDungA(); break;
                    case "B": noiDungDapAnSV = mc.getNoiDungB(); break;
                    case "C": noiDungDapAnSV = mc.getNoiDungC(); break;
                    case "D": noiDungDapAnSV = mc.getNoiDungD(); break;
                }
            } else {
                // Nếu đáp án là nội dung trực tiếp, so sánh trực tiếp
                noiDungDapAnSV = dapAnSV.trim();
            }
            
            return noiDungDapAnSV != null && dapAnDung != null && 
                   noiDungDapAnSV.trim().equalsIgnoreCase(dapAnDung.trim());
        } else if (cauHoi instanceof CauHoiDKDTO) {
            CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
            return dk.kiemTraDapAn(dapAnSV);
        }
        return false;
    }
}
