/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyNganhPanel - Panel quản lý Ngành
 */
package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.KhoaDTO;
import dto.NganhDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLyNganhPanel extends JPanel {
    private TruongKhoaBUS truongKhoaBUS;

    private CustomTable tblNganh;
    private DefaultTableModel modelNganh;

    private JTextField txtTenNganh;
    private JComboBox<KhoaDTO> cboKhoa;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    private CustomButton btnTimKiem;

    private int selectedMaNganh = -1;

    public QuanLyNganhPanel() {
        this.truongKhoaBUS = new TruongKhoaBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ NGÀNH", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
                "Thông tin ngành"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Tên ngành
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTenNganh = new JLabel("Tên ngành:");
        lblTenNganh.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblTenNganh, gbc);

        gbc.gridx = 1;
        txtTenNganh = new JTextField(30);
        txtTenNganh.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenNganh, gbc);

        // Row 1: Thuộc khoa
        gbc.gridx = 2;
        JLabel lblKhoa = new JLabel("Thuộc khoa:");
        lblKhoa.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblKhoa, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cboKhoa = new JComboBox<>();
        cboKhoa.setPreferredSize(new Dimension(250, 28));
        cboKhoa.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboKhoa, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themNganh());
        btnSua.addActionListener(e -> suaNganh());
        btnXoa.addActionListener(e -> xoaNganh());
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
        String[] columns = { "Mã Ngành", "Tên Ngành", "Thuộc Khoa" };
        modelNganh = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblNganh = new CustomTable(modelNganh);
        tblNganh.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblNganh);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.CONTENT_BG);

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã Ngành", "Tên Ngành", "Thuộc Khoa" });
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
            loadNganh();
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
        loadKhoa();
        loadNganh();
    }

    private void loadKhoa() {
        cboKhoa.removeAllItems();
        List<KhoaDTO> danhSach = truongKhoaBUS.getDanhSachKhoa();
        if (danhSach != null) {
            for (KhoaDTO khoa : danhSach) {
                cboKhoa.addItem(khoa);
            }
        }
    }

    private void loadNganh() {
        modelNganh.setRowCount(0);
        List<NganhDTO> danhSach = truongKhoaBUS.getDanhSachNganh();
        if (danhSach != null) {
            for (NganhDTO nganh : danhSach) {
                modelNganh.addRow(new Object[] {
                        nganh.getMaNganh(),
                        nganh.getTenNganh(),
                        nganh.getTenKhoa() != null ? nganh.getTenKhoa() : String.valueOf(nganh.getMaKhoa())
                });
            }
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelNganh.setRowCount(0);

        List<NganhDTO> danhSach;
        if (keyword.isEmpty() || loaiTimKiem.equals("Tất cả")) {
            danhSach = truongKhoaBUS.timKiemNganh(keyword);
        } else {
            danhSach = truongKhoaBUS.getDanhSachNganh();
        }

        if (danhSach != null) {
            for (NganhDTO nganh : danhSach) {
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã Ngành":
                            match = String.valueOf(nganh.getMaNganh()).contains(keyword);
                            break;
                        case "Tên Ngành":
                            match = nganh.getTenNganh() != null && nganh.getTenNganh().toLowerCase().contains(keyLower);
                            break;
                        case "Thuộc Khoa":
                            match = nganh.getTenKhoa() != null && nganh.getTenKhoa().toLowerCase().contains(keyLower);
                            break;
                    }
                }
                if (match) {
                    modelNganh.addRow(new Object[] {
                            nganh.getMaNganh(),
                            nganh.getTenNganh(),
                            nganh.getTenKhoa() != null ? nganh.getTenKhoa() : String.valueOf(nganh.getMaKhoa())
                    });
                }
            }
        }
    }

    private void hienThiThongTin() {
        int row = tblNganh.getSelectedRow();
        if (row >= 0) {
            selectedMaNganh = (int) modelNganh.getValueAt(row, 0);
            txtTenNganh.setText((String) modelNganh.getValueAt(row, 1));

            String tenKhoa = (String) modelNganh.getValueAt(row, 2);
            for (int i = 0; i < cboKhoa.getItemCount(); i++) {
                KhoaDTO k = cboKhoa.getItemAt(i);
                if (k.getTenKhoa().equals(tenKhoa) || String.valueOf(k.getMaKhoa()).equals(tenKhoa)) {
                    cboKhoa.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void themNganh() {
        if (!validateInput())
            return;

        NganhDTO nganh = new NganhDTO();
        nganh.setTenNganh(txtTenNganh.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null) {
            nganh.setMaKhoa(khoa.getMaKhoa());
        }

        if (truongKhoaBUS.themNganh(nganh)) {
            JOptionPane.showMessageDialog(this, "Thêm ngành thành công!");
            loadNganh();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm ngành thất bại!");
        }
    }

    private void suaNganh() {
        if (selectedMaNganh == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngành cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        NganhDTO nganh = new NganhDTO();
        nganh.setMaNganh(selectedMaNganh);
        nganh.setTenNganh(txtTenNganh.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null) {
            nganh.setMaKhoa(khoa.getMaKhoa());
        }

        if (truongKhoaBUS.capNhatNganh(nganh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật ngành thành công!");
            loadNganh();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật ngành thất bại!");
        }
    }

    private void xoaNganh() {
        if (selectedMaNganh == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngành cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa ngành này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (truongKhoaBUS.xoaNganh(selectedMaNganh)) {
                JOptionPane.showMessageDialog(this, "Xóa ngành thành công!");
                loadNganh();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa ngành thất bại! Ngành có thể đang được sử dụng.");
            }
        }
    }

    private void lamMoi() {
        txtTenNganh.setText("");
        if (cboKhoa.getItemCount() > 0) {
            cboKhoa.setSelectedIndex(0);
        }
        tblNganh.clearSelection();
        selectedMaNganh = -1;
    }

    private boolean validateInput() {
        if (txtTenNganh.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên ngành!");
            txtTenNganh.requestFocus();
            return false;
        }
        if (cboKhoa.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoa!");
            cboKhoa.requestFocus();
            return false;
        }
        return true;
    }
}
