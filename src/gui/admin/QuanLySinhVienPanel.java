/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLySinhVienPanel - Panel quản lý Sinh viên với form inline
 */
package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.NganhDTO;
import dto.SinhVienDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLySinhVienPanel extends JPanel {
    private TruongKhoaBUS truongKhoaBUS;

    private CustomTable tblSinhVien;
    private DefaultTableModel modelSinhVien;

    private JTextField txtMaSV;
    private JTextField txtTenDangNhap;
    private JTextField txtHo;
    private JTextField txtTen;
    private JTextField txtEmail;
    private JPasswordField txtMatKhau;
    private JComboBox<NganhDTO> cboNganh;
    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    private CustomButton btnTimKiem;

    public QuanLySinhVienPanel() {
        this.truongKhoaBUS = new TruongKhoaBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ SINH VIÊN", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
                "Thông tin sinh viên"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        addLabel(panelForm, "Mã SV:", gbc);
        gbc.gridx = 1;
        txtMaSV = new JTextField(12);
        txtMaSV.setEditable(false);
        txtMaSV.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtMaSV, gbc);

        gbc.gridx = 2;
        addLabel(panelForm, "Tên đăng nhập:", gbc);
        gbc.gridx = 3;
        txtTenDangNhap = new JTextField(15);
        txtTenDangNhap.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenDangNhap, gbc);

        gbc.gridx = 4;
        addLabel(panelForm, "Ngành:", gbc);
        gbc.gridx = 5;
        cboNganh = new JComboBox<>();
        cboNganh.setPreferredSize(new Dimension(180, 28));
        cboNganh.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboNganh, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        addLabel(panelForm, "Họ:", gbc);
        gbc.gridx = 1;
        txtHo = new JTextField(12);
        txtHo.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtHo, gbc);

        gbc.gridx = 2;
        addLabel(panelForm, "Tên:", gbc);
        gbc.gridx = 3;
        txtTen = new JTextField(15);
        txtTen.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTen, gbc);

        gbc.gridx = 4;
        addLabel(panelForm, "Email:", gbc);
        gbc.gridx = 5;
        txtEmail = new JTextField(20);
        txtEmail.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtEmail, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        addLabel(panelForm, "Mật khẩu:", gbc);
        gbc.gridx = 1;
        txtMatKhau = new JPasswordField(12);
        txtMatKhau.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtMatKhau, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themSinhVien());
        btnSua.addActionListener(e -> suaSinhVien());
        btnXoa.addActionListener(e -> xoaSinhVien());
        btnLamMoi.addActionListener(e -> lamMoi());

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForm.add(panelNut, gbc);

        // Panel trên: Tiêu đề + Form
        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.CONTENT_BG);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.CONTENT_BG);

        JLabel lblTimKiem = new JLabel("Tìm theo:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(
                new String[] { "Tất cả", "Mã SV", "Tên đăng nhập", "Họ tên", "Email", "Ngành" });
        cboLoaiTimKiem.setFont(Constants.NORMAL_FONT);
        cboLoaiTimKiem.setPreferredSize(new Dimension(130, 28));
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
            cboLoaiTimKiem.setSelectedIndex(0);
            loadSinhVien();
        });
        panelTimKiem.add(btnHienTatCa);

        // Bảng
        String[] columns = { "Mã SV", "Họ", "Tên", "Tên đăng nhập", "Mật khẩu", "Email", "Ngành", "Trạng thái" };
        modelSinhVien = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSinhVien = new CustomTable(modelSinhVien);
        tblSinhVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblSinhVien);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        // Panel center chứa tìm kiếm và bảng
        JPanel panelCenter = new JPanel(new BorderLayout(0, 5));
        panelCenter.setBackground(Constants.CONTENT_BG);
        panelCenter.add(panelTimKiem, BorderLayout.NORTH);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Constants.NORMAL_FONT);
        panel.add(lbl, gbc);
    }

    private void loadData() {
        loadNganh();
        loadSinhVien();
    }

    private void loadNganh() {
        cboNganh.removeAllItems();
        List<NganhDTO> danhSach = truongKhoaBUS.getDanhSachNganh();
        if (danhSach != null) {
            for (NganhDTO nganh : danhSach) {
                cboNganh.addItem(nganh);
            }
        }
    }

    private void loadSinhVien() {
        modelSinhVien.setRowCount(0);
        List<SinhVienDTO> danhSach = truongKhoaBUS.getDanhSachSinhVien();
        if (danhSach != null) {
            for (SinhVienDTO sv : danhSach) {
                modelSinhVien.addRow(new Object[] {
                        sv.getMaSV(), sv.getHo(), sv.getTen(), sv.getTenDangNhap(),
                        sv.getMatKhau() != null ? sv.getMatKhau() : "",
                        sv.getEmail(), sv.getTenNganh(),
                        sv.isTrangThai() ? "Hoạt động" : "Khóa"
                });
            }
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelSinhVien.setRowCount(0);

        List<SinhVienDTO> danhSach;
        if (keyword.isEmpty() || loaiTimKiem.equals("Tất cả")) {
            danhSach = truongKhoaBUS.timKiemSinhVien(keyword);
        } else {
            danhSach = truongKhoaBUS.getDanhSachSinhVien();
        }

        if (danhSach != null) {
            for (SinhVienDTO sv : danhSach) {
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã SV":
                            match = String.valueOf(sv.getMaSV()).contains(keyword);
                            break;
                        case "Tên đăng nhập":
                            match = sv.getTenDangNhap() != null && sv.getTenDangNhap().toLowerCase().contains(keyLower);
                            break;
                        case "Họ tên":
                            String hoTen = (sv.getHo() + " " + sv.getTen()).toLowerCase();
                            match = hoTen.contains(keyLower);
                            break;
                        case "Email":
                            match = sv.getEmail() != null && sv.getEmail().toLowerCase().contains(keyLower);
                            break;
                        case "Ngành":
                            match = sv.getTenNganh() != null && sv.getTenNganh().toLowerCase().contains(keyLower);
                            break;
                    }
                }
                if (match) {
                    modelSinhVien.addRow(new Object[] {
                            sv.getMaSV(), sv.getHo(), sv.getTen(), sv.getTenDangNhap(),
                            sv.getMatKhau() != null ? sv.getMatKhau() : "",
                            sv.getEmail(), sv.getTenNganh(),
                            sv.isTrangThai() ? "Hoạt động" : "Khóa"
                    });
                }
            }
        }
    }

    private void hienThiThongTin() {
        int row = tblSinhVien.getSelectedRow();
        if (row >= 0) {
            txtMaSV.setText(String.valueOf(modelSinhVien.getValueAt(row, 0)));
            txtHo.setText((String) modelSinhVien.getValueAt(row, 1));
            txtTen.setText((String) modelSinhVien.getValueAt(row, 2));
            txtTenDangNhap.setText((String) modelSinhVien.getValueAt(row, 3));
            txtMatKhau.setText((String) modelSinhVien.getValueAt(row, 4));
            txtEmail.setText((String) modelSinhVien.getValueAt(row, 5));

            String tenNganh = (String) modelSinhVien.getValueAt(row, 6);
            for (int i = 0; i < cboNganh.getItemCount(); i++) {
                if (cboNganh.getItemAt(i).getTenNganh().equals(tenNganh)) {
                    cboNganh.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void themSinhVien() {
        if (!validateInput())
            return;

        SinhVienDTO sv = new SinhVienDTO();
        sv.setTenDangNhap(txtTenDangNhap.getText().trim());
        sv.setMatKhau(new String(txtMatKhau.getPassword()));
        sv.setHo(txtHo.getText().trim());
        sv.setTen(txtTen.getText().trim());
        sv.setEmail(txtEmail.getText().trim());
        NganhDTO nganh = (NganhDTO) cboNganh.getSelectedItem();
        if (nganh != null) {
            sv.setMaNganh(nganh.getMaNganh());
        }

        if (truongKhoaBUS.themSinhVien(sv)) {
            JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
            loadSinhVien();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm sinh viên thất bại!");
        }
    }

    private void suaSinhVien() {
        if (txtMaSV.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần sửa!");
            return;
        }
        if (!validateInputForUpdate())
            return;

        SinhVienDTO sv = new SinhVienDTO();
        sv.setMaSV(Integer.parseInt(txtMaSV.getText()));
        sv.setTenDangNhap(txtTenDangNhap.getText().trim());
        sv.setHo(txtHo.getText().trim());
        sv.setTen(txtTen.getText().trim());
        sv.setEmail(txtEmail.getText().trim());
        NganhDTO nganh = (NganhDTO) cboNganh.getSelectedItem();
        if (nganh != null) {
            sv.setMaNganh(nganh.getMaNganh());
        }

        // Chỉ cập nhật mật khẩu nếu có nhập
        String matKhauMoi = new String(txtMatKhau.getPassword());
        if (!matKhauMoi.isEmpty()) {
            sv.setMatKhau(matKhauMoi);
        }

        if (truongKhoaBUS.capNhatSinhVien(sv)) {
            JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!");
            loadSinhVien();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thất bại!");
        }
    }

    private void xoaSinhVien() {
        if (txtMaSV.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sinh viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int maSV = Integer.parseInt(txtMaSV.getText());
            if (truongKhoaBUS.xoaSinhVien(maSV)) {
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!");
                loadSinhVien();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thất bại!");
            }
        }
    }

    private void lamMoi() {
        txtMaSV.setText("");
        txtTenDangNhap.setText("");
        txtHo.setText("");
        txtTen.setText("");
        txtEmail.setText("");
        txtMatKhau.setText("");
        if (cboNganh.getItemCount() > 0) {
            cboNganh.setSelectedIndex(0);
        }
        tblSinhVien.clearSelection();
    }

    private boolean validateInput() {
        if (txtTenDangNhap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!");
            txtTenDangNhap.requestFocus();
            return false;
        }
        if (txtHo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ!");
            txtHo.requestFocus();
            return false;
        }
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên!");
            txtTen.requestFocus();
            return false;
        }
        if (txtMaSV.getText().isEmpty() && txtMatKhau.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!");
            txtMatKhau.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateInputForUpdate() {
        if (txtTenDangNhap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!");
            txtTenDangNhap.requestFocus();
            return false;
        }
        if (txtHo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ!");
            txtHo.requestFocus();
            return false;
        }
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên!");
            txtTen.requestFocus();
            return false;
        }
        return true;
    }
}
