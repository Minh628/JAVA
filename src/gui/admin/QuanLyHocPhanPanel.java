/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyHocPhanPanel - Panel quản lý Học phần với form inline
 */
package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.HocPhanDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLyHocPhanPanel extends JPanel {
    private TruongKhoaBUS truongKhoaBUS;

    private CustomTable tblHocPhan;
    private DefaultTableModel modelHocPhan;

    private JTextField txtTenMon;
    private JSpinner spnSoTin;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    private CustomButton btnTimKiem;

    private int selectedMaHocPhan = -1;

    public QuanLyHocPhanPanel() {
        this.truongKhoaBUS = new TruongKhoaBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ HỌC PHẦN", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
                "Thông tin học phần"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Tên môn
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTenMon = new JLabel("Tên môn:");
        lblTenMon.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblTenMon, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenMon = new JTextField(35);
        txtTenMon.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenMon, gbc);

        // Row 1: Số tín chỉ
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblSoTin = new JLabel("Số tín chỉ:");
        lblSoTin.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblSoTin, gbc);

        gbc.gridx = 3;
        spnSoTin = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spnSoTin.setPreferredSize(new Dimension(80, 28));
        spnSoTin.setFont(Constants.NORMAL_FONT);
        panelForm.add(spnSoTin, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themHocPhan());
        btnSua.addActionListener(e -> suaHocPhan());
        btnXoa.addActionListener(e -> xoaHocPhan());
        btnLamMoi.addActionListener(e -> lamMoi());

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);

        gbc.gridx = 0;
        gbc.gridy = 1;
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
        String[] columns = { "Mã HP", "Tên Học Phần", "Số TC" };
        modelHocPhan = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHocPhan = new CustomTable(modelHocPhan);
        tblHocPhan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblHocPhan);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.CONTENT_BG);

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã HP", "Tên Học Phần" });
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
        modelHocPhan.setRowCount(0);
        List<HocPhanDTO> danhSach = truongKhoaBUS.getDanhSachHocPhan();
        if (danhSach != null) {
            for (HocPhanDTO hp : danhSach) {
                modelHocPhan.addRow(new Object[] {
                        hp.getMaHocPhan(), hp.getTenMon(), hp.getSoTin()
                });
            }
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelHocPhan.setRowCount(0);

        List<HocPhanDTO> danhSach;
        if (keyword.isEmpty() || loaiTimKiem.equals("Tất cả")) {
            danhSach = truongKhoaBUS.timKiemHocPhan(keyword);
        } else {
            danhSach = truongKhoaBUS.getDanhSachHocPhan();
        }

        if (danhSach != null) {
            for (HocPhanDTO hp : danhSach) {
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã HP":
                            match = String.valueOf(hp.getMaHocPhan()).contains(keyword);
                            break;
                        case "Tên Học Phần":
                            match = hp.getTenMon() != null && hp.getTenMon().toLowerCase().contains(keyLower);
                            break;
                    }
                }
                if (match) {
                    modelHocPhan.addRow(new Object[] {
                            hp.getMaHocPhan(), hp.getTenMon(), hp.getSoTin()
                    });
                }
            }
        }
    }

    private void hienThiThongTin() {
        int row = tblHocPhan.getSelectedRow();
        if (row >= 0) {
            selectedMaHocPhan = (int) modelHocPhan.getValueAt(row, 0);
            txtTenMon.setText((String) modelHocPhan.getValueAt(row, 1));
            spnSoTin.setValue(modelHocPhan.getValueAt(row, 2));
        }
    }

    private void themHocPhan() {
        if (!validateInput())
            return;

        HocPhanDTO hp = new HocPhanDTO();
        hp.setTenMon(txtTenMon.getText().trim());
        hp.setSoTin((Integer) spnSoTin.getValue());

        if (truongKhoaBUS.themHocPhan(hp)) {
            JOptionPane.showMessageDialog(this, "Thêm học phần thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm học phần thất bại!");
        }
    }

    private void suaHocPhan() {
        if (selectedMaHocPhan == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học phần cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        HocPhanDTO hp = new HocPhanDTO();
        hp.setMaHocPhan(selectedMaHocPhan);
        hp.setTenMon(txtTenMon.getText().trim());
        hp.setSoTin((Integer) spnSoTin.getValue());

        if (truongKhoaBUS.capNhatHocPhan(hp)) {
            JOptionPane.showMessageDialog(this, "Cập nhật học phần thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật học phần thất bại!");
        }
    }

    private void xoaHocPhan() {
        if (selectedMaHocPhan == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học phần cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa học phần này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (truongKhoaBUS.xoaHocPhan(selectedMaHocPhan)) {
                JOptionPane.showMessageDialog(this, "Xóa học phần thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa học phần thất bại! Học phần có thể đang được sử dụng.");
            }
        }
    }

    private void lamMoi() {
        txtTenMon.setText("");
        spnSoTin.setValue(3);
        tblHocPhan.clearSelection();
        selectedMaHocPhan = -1;
    }

    private boolean validateInput() {
        if (txtTenMon.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên học phần!");
            txtTenMon.requestFocus();
            return false;
        }
        return true;
    }
}
