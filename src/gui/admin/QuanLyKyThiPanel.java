/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyKyThiPanel - Panel quản lý Kỳ thi
 */
package gui.admin;

import bus.DeThiBUS;
import bus.KyThiBUS;
import config.Constants;
import dto.KyThiDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class QuanLyKyThiPanel extends JPanel {
    private KyThiBUS kyThiBUS;

    private CustomTable tblKyThi;
    private DefaultTableModel modelKyThi;

    private JTextField txtTenKyThi;
    private JSpinner spnNgayBatDau;
    private JSpinner spnNgayKetThuc;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    private CustomButton btnTimKiem;

    private int selectedMaKyThi = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public QuanLyKyThiPanel() {
        this.kyThiBUS = new KyThiBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KỲ THI", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
                "Thông tin kỳ thi"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Tên kỳ thi
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTenKyThi = new JLabel("Tên kỳ thi:");
        lblTenKyThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblTenKyThi, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenKyThi = new JTextField(40);
        txtTenKyThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenKyThi, gbc);

        // Row 2: Thời gian bắt đầu và kết thúc
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblBatDau = new JLabel("Thời gian bắt đầu:");
        lblBatDau.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblBatDau, gbc);

        gbc.gridx = 1;
        spnNgayBatDau = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deBatDau = new JSpinner.DateEditor(spnNgayBatDau, "dd/MM/yyyy HH:mm");
        spnNgayBatDau.setEditor(deBatDau);
        spnNgayBatDau.setPreferredSize(new Dimension(180, 28));
        spnNgayBatDau.setFont(Constants.NORMAL_FONT);
        panelForm.add(spnNgayBatDau, gbc);

        gbc.gridx = 2;
        JLabel lblKetThuc = new JLabel("Thời gian kết thúc:");
        lblKetThuc.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblKetThuc, gbc);

        gbc.gridx = 3;
        spnNgayKetThuc = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deKetThuc = new JSpinner.DateEditor(spnNgayKetThuc, "dd/MM/yyyy HH:mm");
        spnNgayKetThuc.setEditor(deKetThuc);
        spnNgayKetThuc.setPreferredSize(new Dimension(180, 28));
        spnNgayKetThuc.setFont(Constants.NORMAL_FONT);
        panelForm.add(spnNgayKetThuc, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themKyThi());
        btnSua.addActionListener(e -> suaKyThi());
        btnXoa.addActionListener(e -> xoaKyThi());
        btnLamMoi.addActionListener(e -> lamMoi());

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForm.add(panelNut, gbc);

        // Panel trên: Tiêu đề + Form
        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.CONTENT_BG);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);

        // Bảng
        String[] columns = { "Mã KT", "Tên Kỳ Thi", "Thời gian bắt đầu", "Thời gian kết thúc", "Trạng thái" };
        modelKyThi = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKyThi = new CustomTable(modelKyThi);
        tblKyThi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblKyThi);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.CONTENT_BG);

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã KT", "Tên Kỳ Thi", "Trạng thái" });
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
    }

    private void loadData() {
        modelKyThi.setRowCount(0);
        List<KyThiDTO> danhSach = kyThiBUS.getDanhSachKyThi();
        if (danhSach != null) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (KyThiDTO kt : danhSach) {
                String trangThai = getTrangThai(kt, now);
                modelKyThi.addRow(new Object[] {
                        kt.getMaKyThi(),
                        kt.getTenKyThi(),
                        kt.getThoiGianBatDau() != null ? dateFormat.format(kt.getThoiGianBatDau()) : "",
                        kt.getThoiGianKetThuc() != null ? dateFormat.format(kt.getThoiGianKetThuc()) : "",
                        trangThai
                });
            }
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelKyThi.setRowCount(0);

        List<KyThiDTO> danhSach;
        if (keyword.isEmpty() || loaiTimKiem.equals("Tất cả")) {
            danhSach = kyThiBUS.timKiem(keyword);
        } else {
            danhSach = kyThiBUS.getDanhSachKyThi();
        }

        if (danhSach != null) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (KyThiDTO kt : danhSach) {
                String trangThai = getTrangThai(kt, now);
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã KT":
                            match = String.valueOf(kt.getMaKyThi()).contains(keyword);
                            break;
                        case "Tên Kỳ Thi":
                            match = kt.getTenKyThi() != null && kt.getTenKyThi().toLowerCase().contains(keyLower);
                            break;
                        case "Trạng thái":
                            match = trangThai.toLowerCase().contains(keyLower);
                            break;
                    }
                }
                if (match) {
                    modelKyThi.addRow(new Object[] {
                            kt.getMaKyThi(),
                            kt.getTenKyThi(),
                            kt.getThoiGianBatDau() != null ? dateFormat.format(kt.getThoiGianBatDau()) : "",
                            kt.getThoiGianKetThuc() != null ? dateFormat.format(kt.getThoiGianKetThuc()) : "",
                            trangThai
                    });
                }
            }
        }
    }

    private String getTrangThai(KyThiDTO kt, Timestamp now) {
        if (kt.getThoiGianBatDau() == null || kt.getThoiGianKetThuc() == null) {
            return "Chưa xác định";
        }
        if (now.before(kt.getThoiGianBatDau())) {
            return "Sắp diễn ra";
        } else if (now.after(kt.getThoiGianKetThuc())) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }

    private void hienThiThongTin() {
        int row = tblKyThi.getSelectedRow();
        if (row >= 0) {
            selectedMaKyThi = (int) modelKyThi.getValueAt(row, 0);
            txtTenKyThi.setText((String) modelKyThi.getValueAt(row, 1));

            String batDauStr = (String) modelKyThi.getValueAt(row, 2);
            String ketThucStr = (String) modelKyThi.getValueAt(row, 3);

            try {
                if (!batDauStr.isEmpty()) {
                    spnNgayBatDau.setValue(dateFormat.parse(batDauStr));
                }
                if (!ketThucStr.isEmpty()) {
                    spnNgayKetThuc.setValue(dateFormat.parse(ketThucStr));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void themKyThi() {
        if (!validateInput())
            return;

        KyThiDTO kyThi = new KyThiDTO();
        kyThi.setTenKyThi(txtTenKyThi.getText().trim());
        kyThi.setThoiGianBatDau(new Timestamp(((Date) spnNgayBatDau.getValue()).getTime()));
        kyThi.setThoiGianKetThuc(new Timestamp(((Date) spnNgayKetThuc.getValue()).getTime()));

        if (kyThiBUS.themKyThi(kyThi)) {
            JOptionPane.showMessageDialog(this, "Thêm kỳ thi thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm kỳ thi thất bại!");
        }
    }

    private void suaKyThi() {
        if (selectedMaKyThi == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kỳ thi cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        KyThiDTO kyThi = new KyThiDTO();
        kyThi.setMaKyThi(selectedMaKyThi);
        kyThi.setTenKyThi(txtTenKyThi.getText().trim());
        kyThi.setThoiGianBatDau(new Timestamp(((Date) spnNgayBatDau.getValue()).getTime()));
        kyThi.setThoiGianKetThuc(new Timestamp(((Date) spnNgayKetThuc.getValue()).getTime()));

        if (kyThiBUS.capNhatKyThi(kyThi)) {
            JOptionPane.showMessageDialog(this, "Cập nhật kỳ thi thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật kỳ thi thất bại!");
        }
    }

    private void xoaKyThi() {
        if (selectedMaKyThi == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kỳ thi cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa kỳ thi này?\nCác đề thi liên quan cũng sẽ bị xóa!",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (kyThiBUS.xoaKyThi(selectedMaKyThi)) {
                JOptionPane.showMessageDialog(this, "Xóa kỳ thi thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa kỳ thi thất bại! Kỳ thi có thể đang có đề thi.");
            }
        }
    }

    private void lamMoi() {
        txtTenKyThi.setText("");
        spnNgayBatDau.setValue(new Date());
        spnNgayKetThuc.setValue(new Date());
        tblKyThi.clearSelection();
        selectedMaKyThi = -1;
    }

    private boolean validateInput() {
        if (txtTenKyThi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên kỳ thi!");
            txtTenKyThi.requestFocus();
            return false;
        }

        Date batDau = (Date) spnNgayBatDau.getValue();
        Date ketThuc = (Date) spnNgayKetThuc.getValue();

        if (ketThuc.before(batDau)) {
            JOptionPane.showMessageDialog(this, "Thời gian kết thúc phải sau thời gian bắt đầu!");
            return false;
        }

        return true;
    }
}
