/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: LichSuThiPanel - Panel hiển thị lịch sử thi của sinh viên
 */
package gui.student;

import bus.SinhVienBUS;
import config.Constants;
import dto.BaiThiDTO;
import dto.SinhVienDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LichSuThiPanel extends JPanel {
    private SinhVienDTO nguoiDung;
    private SinhVienBUS sinhVienBUS;
    
    private CustomTable tblLichSu;
    private DefaultTableModel modelLichSu;

    public LichSuThiPanel(SinhVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.sinhVienBUS = new SinhVienBUS();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel("📜 LỊCH SỬ THI");
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);
        add(lblTieuDe, BorderLayout.NORTH);
        
        // Bảng lịch sử thi
        String[] columns = {"Mã bài thi", "Đề thi", "Môn học", "Ngày thi", "Số câu đúng", "Điểm"};
        modelLichSu = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblLichSu = new CustomTable(modelLichSu);
        
        JScrollPane scrollPane = new JScrollPane(tblLichSu);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);
        add(scrollPane, BorderLayout.CENTER);
        
        // Nút chức năng
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNut.setBackground(Constants.CONTENT_BG);
        
        CustomButton btnXemChiTiet = new CustomButton("🔍  Xem chi tiết", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXemChiTiet.addActionListener(e -> xemChiTiet());
        panelNut.add(btnXemChiTiet);
        
        CustomButton btnLamMoi = new CustomButton("🔄  Làm mới", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnLamMoi.addActionListener(e -> loadData());
        panelNut.add(btnLamMoi);
        
        add(panelNut, BorderLayout.SOUTH);
    }
    
    public void loadData() {
        modelLichSu.setRowCount(0);
        List<BaiThiDTO> danhSach = sinhVienBUS.getLichSuBaiThi(nguoiDung.getMaSV());
        if (danhSach != null) {
            for (BaiThiDTO bt : danhSach) {
                modelLichSu.addRow(new Object[]{
                    bt.getMaBaiThi(), bt.getTenDeThi(), bt.getTenHocPhan(),
                    bt.getNgayThi(), bt.getSoCauDung() + "/" + bt.getTongSoCau(),
                    String.format("%.2f", bt.getDiemSo())
                });
            }
        }
    }
    
    private void xemChiTiet() {
        int row = tblLichSu.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài thi cần xem!");
            return;
        }
        
        int maBaiThi = (int) modelLichSu.getValueAt(row, 0);
        String tenDeThi = (String) modelLichSu.getValueAt(row, 1);
        String soCauDung = (String) modelLichSu.getValueAt(row, 4);
        String diem = (String) modelLichSu.getValueAt(row, 5);
        
        // Hiển thị thông tin chi tiết
        String message = String.format(
            "Thông tin bài thi:\n\n" +
            "- Đề thi: %s\n" +
            "- Số câu đúng: %s\n" +
            "- Điểm: %s\n\n" +
            "(Chức năng xem chi tiết câu trả lời đang phát triển)",
            tenDeThi, soCauDung, diem
        );
        JOptionPane.showMessageDialog(this, message, "Chi tiết bài thi", JOptionPane.INFORMATION_MESSAGE);
    }
}
