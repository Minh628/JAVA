/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyKhoaPanel - Panel quản lý Khoa và Ngành
 * Có 2 JTable: Click vào Khoa -> hiện danh sách Ngành thuộc Khoa đó
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

public class QuanLyKhoaPanel extends JPanel {
    private TruongKhoaBUS truongKhoaBUS;

    // Table Khoa
    private CustomTable tblKhoa;
    private DefaultTableModel modelKhoa;

    // Table Ngành (hiển thị ngành thuộc khoa được chọn)
    private CustomTable tblNganh;
    private DefaultTableModel modelNganh;

    private JTextField txtTenKhoa;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    private CustomButton btnTimKiem;

    private int selectedMaKhoa = -1;

    public QuanLyKhoaPanel() {
        this.truongKhoaBUS = new TruongKhoaBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHOA", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
                "Thông tin khoa"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Tên khoa
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTenKhoa = new JLabel("Tên khoa:");
        lblTenKhoa.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblTenKhoa, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenKhoa = new JTextField(40);
        txtTenKhoa.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenKhoa, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themKhoa());
        btnSua.addActionListener(e -> suaKhoa());
        btnXoa.addActionListener(e -> xoaKhoa());
        btnLamMoi.addActionListener(e -> lamMoi());

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForm.add(panelNut, gbc);

        // Panel trên: Tiêu đề + Form
        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.CONTENT_BG);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);

        // Panel chứa 2 bảng
        JPanel panelTables = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTables.setBackground(Constants.CONTENT_BG);

        // === Bảng Khoa (bên trái) ===
        JPanel panelKhoa = new JPanel(new BorderLayout(0, 5));
        panelKhoa.setBackground(Constants.CONTENT_BG);

        JLabel lblTableKhoa = new JLabel("Danh sách Khoa", SwingConstants.CENTER);
        lblTableKhoa.setFont(Constants.TITLE_FONT);
        lblTableKhoa.setForeground(Constants.PRIMARY_COLOR);
        panelKhoa.add(lblTableKhoa, BorderLayout.NORTH);

        // Panel tìm kiếm khoa
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelTimKiem.setBackground(Constants.CONTENT_BG);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã Khoa", "Tên Khoa" });
        cboLoaiTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(cboLoaiTimKiem);

        txtTimKiem = new JTextField(15);
        txtTimKiem.setFont(Constants.NORMAL_FONT);
        txtTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(txtTimKiem);

        btnTimKiem = new CustomButton("Tìm", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(btnTimKiem);

        CustomButton btnHienTatCa = new CustomButton("Tất cả", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            loadData();
        });
        panelTimKiem.add(btnHienTatCa);

        String[] columnsKhoa = { "Mã Khoa", "Tên Khoa", "Số ngành" };
        modelKhoa = new DefaultTableModel(columnsKhoa, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKhoa = new CustomTable(modelKhoa);
        tblKhoa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
                loadNganhTheoKhoa(); // Load ngành khi chọn khoa
            }
        });

        JScrollPane scrollKhoa = new JScrollPane(tblKhoa);
        scrollKhoa.getViewport().setBackground(Constants.CARD_COLOR);

        JPanel panelKhoaContent = new JPanel(new BorderLayout(0, 5));
        panelKhoaContent.setBackground(Constants.CONTENT_BG);
        panelKhoaContent.add(panelTimKiem, BorderLayout.NORTH);
        panelKhoaContent.add(scrollKhoa, BorderLayout.CENTER);
        panelKhoa.add(panelKhoaContent, BorderLayout.CENTER);

        // === Bảng Ngành (bên phải) ===
        JPanel panelNganh = new JPanel(new BorderLayout(0, 5));
        panelNganh.setBackground(Constants.CONTENT_BG);

        JLabel lblTableNganh = new JLabel("Danh sách Ngành thuộc Khoa", SwingConstants.CENTER);
        lblTableNganh.setFont(Constants.TITLE_FONT);
        lblTableNganh.setForeground(Constants.SECONDARY_COLOR);
        panelNganh.add(lblTableNganh, BorderLayout.NORTH);

        String[] columnsNganh = { "Mã Ngành", "Tên Ngành" };
        modelNganh = new DefaultTableModel(columnsNganh, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblNganh = new CustomTable(modelNganh);

        JScrollPane scrollNganh = new JScrollPane(tblNganh);
        scrollNganh.getViewport().setBackground(Constants.CARD_COLOR);
        panelNganh.add(scrollNganh, BorderLayout.CENTER);

        // Thêm 2 panel vào panel chứa tables
        panelTables.add(panelKhoa);
        panelTables.add(panelNganh);

        add(panelTables, BorderLayout.CENTER);
    }

    private void loadData() {
        modelKhoa.setRowCount(0);
        List<KhoaDTO> danhSach = truongKhoaBUS.getDanhSachKhoa();
        if (danhSach != null) {
            for (KhoaDTO khoa : danhSach) {
                // Đếm số ngành thuộc khoa
                int soNganh = truongKhoaBUS.demNganhTheoKhoa(khoa.getMaKhoa());
                modelKhoa.addRow(new Object[] {
                        khoa.getMaKhoa(), khoa.getTenKhoa(), soNganh
                });
            }
        }
        // Clear bảng ngành
        modelNganh.setRowCount(0);
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelKhoa.setRowCount(0);

        List<KhoaDTO> danhSach;
        if (keyword.isEmpty() || loaiTimKiem.equals("Tất cả")) {
            danhSach = truongKhoaBUS.timKiemKhoa(keyword);
        } else {
            danhSach = truongKhoaBUS.getDanhSachKhoa();
        }

        if (danhSach != null) {
            for (KhoaDTO khoa : danhSach) {
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã Khoa":
                            match = String.valueOf(khoa.getMaKhoa()).contains(keyword);
                            break;
                        case "Tên Khoa":
                            match = khoa.getTenKhoa() != null && khoa.getTenKhoa().toLowerCase().contains(keyLower);
                            break;
                    }
                }
                if (match) {
                    int soNganh = truongKhoaBUS.demNganhTheoKhoa(khoa.getMaKhoa());
                    modelKhoa.addRow(new Object[] {
                            khoa.getMaKhoa(), khoa.getTenKhoa(), soNganh
                    });
                }
            }
        }
        modelNganh.setRowCount(0);
    }

    /**
     * Load danh sách ngành thuộc khoa đang được chọn
     */
    private void loadNganhTheoKhoa() {
        modelNganh.setRowCount(0);
        if (selectedMaKhoa == -1) {
            return;
        }

        List<NganhDTO> danhSachNganh = truongKhoaBUS.getNganhTheoKhoa(selectedMaKhoa);
        if (danhSachNganh != null) {
            for (NganhDTO nganh : danhSachNganh) {
                modelNganh.addRow(new Object[] {
                        nganh.getMaNganh(), nganh.getTenNganh()
                });
            }
        }
    }

    private void hienThiThongTin() {
        int row = tblKhoa.getSelectedRow();
        if (row >= 0) {
            selectedMaKhoa = (int) modelKhoa.getValueAt(row, 0);
            txtTenKhoa.setText((String) modelKhoa.getValueAt(row, 1));
        }
    }

    private void themKhoa() {
        if (!validateInput())
            return;

        KhoaDTO khoa = new KhoaDTO();
        khoa.setTenKhoa(txtTenKhoa.getText().trim());

        if (truongKhoaBUS.themKhoa(khoa)) {
            JOptionPane.showMessageDialog(this, "Thêm khoa thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khoa thất bại!");
        }
    }

    private void suaKhoa() {
        if (selectedMaKhoa == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoa cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        KhoaDTO khoa = new KhoaDTO();
        khoa.setMaKhoa(selectedMaKhoa);
        khoa.setTenKhoa(txtTenKhoa.getText().trim());

        if (truongKhoaBUS.capNhatKhoa(khoa)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khoa thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khoa thất bại!");
        }
    }

    private void xoaKhoa() {
        if (selectedMaKhoa == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoa cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa khoa này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Sử dụng xoaKhoaAnToan để kiểm tra ràng buộc
            int ketQua = truongKhoaBUS.xoaKhoaAnToan(selectedMaKhoa);
            switch (ketQua) {
                case 1:
                    JOptionPane.showMessageDialog(this, "Xóa khoa thành công!");
                    loadData();
                    lamMoi();
                    break;
                case -1:
                    JOptionPane.showMessageDialog(this,
                            "Không thể xóa khoa!\nKhoa này đang có ngành học thuộc về. Vui lòng xóa các ngành trước.",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Xóa khoa thất bại!");
            }
        }
    }

    private void lamMoi() {
        txtTenKhoa.setText("");
        tblKhoa.clearSelection();
        selectedMaKhoa = -1;
        modelNganh.setRowCount(0); // Clear bảng ngành
    }

    private boolean validateInput() {
        if (txtTenKhoa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khoa!");
            txtTenKhoa.requestFocus();
            return false;
        }
        return true;
    }
}
