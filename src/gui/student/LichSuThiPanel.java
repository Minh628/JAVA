/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: LichSuThiPanel - Panel hiển thị lịch sử thi của sinh viên
 */
package gui.student;

import bus.BaiThiBUS;
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
    private BaiThiBUS baiThiBUS;

    private CustomTable tblLichSu;
    private DefaultTableModel modelLichSu;

    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;
    private CustomButton btnTimKiem;

    public LichSuThiPanel(SinhVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.sinhVienBUS = new SinhVienBUS();
        this.baiThiBUS = new BaiThiBUS();
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
        String[] columns = { "Mã bài thi", "Đề thi", "Môn học", "Ngày thi", "Số câu đúng", "Điểm" };
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

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.CONTENT_BG);

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã bài thi", "Đề thi", "Môn học" });
        cboLoaiTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(cboLoaiTimKiem);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(Constants.NORMAL_FONT);
        txtTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(txtTimKiem);

        btnTimKiem = new CustomButton("Tìm", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(btnTimKiem);

        CustomButton btnHienTatCa = new CustomButton("Hiện tất cả", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });
        panelTimKiem.add(btnHienTatCa);

        // Panel center chứa tìm kiếm và bảng
        JPanel panelCenter = new JPanel(new BorderLayout(0, 5));
        panelCenter.setBackground(Constants.CONTENT_BG);
        panelCenter.add(panelTimKiem, BorderLayout.NORTH);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // Nút chức năng
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNut.setBackground(Constants.CONTENT_BG);

        CustomButton btnXemChiTiet = new CustomButton("🔍  Xem chi tiết", Constants.PRIMARY_COLOR,
                Constants.TEXT_COLOR);
        btnXemChiTiet.addActionListener(e -> xemChiTiet());
        panelNut.add(btnXemChiTiet);

        CustomButton btnLamMoi = new CustomButton("🔄  Làm mới", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnLamMoi.addActionListener(e -> loadData());
        panelNut.add(btnLamMoi);

        add(panelNut, BorderLayout.SOUTH);
    }

    public void loadData() {
        modelLichSu.setRowCount(0);
        // Gọi BaiThiBUS để lấy lịch sử bài thi
        List<BaiThiDTO> danhSach = baiThiBUS.getLichSuBaiThi(nguoiDung.getMaSV());
        if (danhSach != null) {
            for (BaiThiDTO bt : danhSach) {
                modelLichSu.addRow(new Object[] {
                        bt.getMaBaiThi(), bt.getTenDeThi(), bt.getTenHocPhan(),
                        bt.getNgayThi(), bt.getSoCauDung() + "/" + bt.getTongSoCau(),
                        String.format("%.2f", bt.getDiemSo())
                });
            }
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelLichSu.setRowCount(0);

        // Gọi BaiThiBUS để lấy lịch sử bài thi
        List<BaiThiDTO> danhSach = baiThiBUS.getLichSuBaiThi(nguoiDung.getMaSV());
        if (danhSach != null) {
            for (BaiThiDTO bt : danhSach) {
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã bài thi":
                            match = String.valueOf(bt.getMaBaiThi()).contains(keyword);
                            break;
                        case "Đề thi":
                            match = bt.getTenDeThi() != null && bt.getTenDeThi().toLowerCase().contains(keyLower);
                            break;
                        case "Môn học":
                            match = bt.getTenHocPhan() != null && bt.getTenHocPhan().toLowerCase().contains(keyLower);
                            break;
                    }
                } else if (!keyword.isEmpty()) {
                    String keyLower = keyword.toLowerCase();
                    match = String.valueOf(bt.getMaBaiThi()).contains(keyword)
                            || (bt.getTenDeThi() != null && bt.getTenDeThi().toLowerCase().contains(keyLower))
                            || (bt.getTenHocPhan() != null && bt.getTenHocPhan().toLowerCase().contains(keyLower));
                }
                if (match) {
                    modelLichSu.addRow(new Object[] {
                            bt.getMaBaiThi(), bt.getTenDeThi(), bt.getTenHocPhan(),
                            bt.getNgayThi(), bt.getSoCauDung() + "/" + bt.getTongSoCau(),
                            String.format("%.2f", bt.getDiemSo())
                    });
                }
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
                tenDeThi, soCauDung, diem);
        JOptionPane.showMessageDialog(this, message, "Chi tiết bài thi", JOptionPane.INFORMATION_MESSAGE);
    }
}
