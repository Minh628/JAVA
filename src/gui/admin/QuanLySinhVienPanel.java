package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.NganhDTO;
import dto.SinhVienDTO;
import gui.components.BaseCrudPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuanLySinhVienPanel extends BaseCrudPanel {
    private TruongKhoaBUS truongKhoaBUS = new TruongKhoaBUS();
    private JTextField txtMaSV, txtTenDangNhap, txtHo, txtTen, txtEmail;
    private JPasswordField txtMatKhau;
    private JComboBox<NganhDTO> cboNganh;

    public QuanLySinhVienPanel() {
        super("QUẢN LÝ SINH VIÊN",
                new String[] { "Mã SV", "Họ", "Tên", "Tên đăng nhập", "Mật khẩu", "Email", "Ngành", "Trạng thái" },
                new String[] { "Tất cả", "Mã SV", "Tên đăng nhập", "Họ tên", "Email", "Ngành" });
        
    
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Constants.CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtMaSV = createTextField(10, false);
        txtTenDangNhap = createTextField(15, true);
        txtMatKhau = new JPasswordField(15);
        txtHo = createTextField(15, true);
        txtTen = createTextField(15, true);
        txtEmail = createTextField(20, true);
        cboNganh = new JComboBox<>();
        cboNganh.setPreferredSize(new Dimension(150, 30));

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Mã SV:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMaSV, gbc);
        gbc.gridx = 2;
        panel.add(createLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 3;
        panel.add(txtTenDangNhap, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel("Họ:"), gbc);
        gbc.gridx = 1;
        panel.add(txtHo, gbc);
        gbc.gridx = 2;
        panel.add(createLabel("Tên:"), gbc);
        gbc.gridx = 3;
        panel.add(txtTen, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMatKhau, gbc);
        gbc.gridx = 2;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 3;
        panel.add(txtEmail, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createLabel("Ngành:"), gbc);
        gbc.gridx = 1;
        panel.add(cboNganh, gbc);

        return panel;
    }

    @Override
    protected void loadData() {
        cboNganh.removeAllItems();
        List<NganhDTO> nganhList = truongKhoaBUS.getDanhSachNganh();
        if (nganhList != null)
            nganhList.forEach(cboNganh::addItem);

        tableModel.setRowCount(0);
        List<SinhVienDTO> danhSach = truongKhoaBUS.getDanhSachSinhVien();
        if (danhSach != null) {
            danhSach.forEach(sv -> tableModel.addRow(new Object[] {
                    sv.getMaSV(), sv.getHo(), sv.getTen(), sv.getTenDangNhap(),
                    sv.getMatKhau() != null ? sv.getMatKhau() : "",
                    sv.getEmail(), sv.getTenNganh(),
                    sv.isTrangThai() ? "Hoạt động" : "Khóa"
            }));
        }
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();
        tableModel.setRowCount(0);

        List<SinhVienDTO> danhSach = keyword.isEmpty() || "Tất cả".equals(loai)
                ? truongKhoaBUS.timKiemSinhVien(keyword)
                : truongKhoaBUS.getDanhSachSinhVien();

        if (danhSach != null) {
            danhSach.stream().filter(sv -> matchFilter(sv, keyword, loai))
                    .forEach(sv -> tableModel.addRow(new Object[] {
                            sv.getMaSV(), sv.getHo(), sv.getTen(), sv.getTenDangNhap(),
                            sv.getMatKhau() != null ? sv.getMatKhau() : "",
                            sv.getEmail(), sv.getTenNganh(), sv.isTrangThai() ? "Hoạt động" : "Khóa"
                    }));
        }
    }

    private boolean matchFilter(SinhVienDTO sv, String keyword, String loai) {
        if (keyword.isEmpty() || "Tất cả".equals(loai))
            return true;
        return switch (loai) {
            case "Mã SV" -> String.valueOf(sv.getMaSV()).contains(keyword);
            case "Tên đăng nhập" -> sv.getTenDangNhap() != null && sv.getTenDangNhap().toLowerCase().contains(keyword);
            case "Họ tên" -> (sv.getHo() + " " + sv.getTen()).toLowerCase().contains(keyword);
            case "Email" -> sv.getEmail() != null && sv.getEmail().toLowerCase().contains(keyword);
            case "Ngành" -> sv.getTenNganh() != null && sv.getTenNganh().toLowerCase().contains(keyword);
            default -> true;
        };
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaSV.setText(String.valueOf(tableModel.getValueAt(row, 0)));
            txtHo.setText((String) tableModel.getValueAt(row, 1));
            txtTen.setText((String) tableModel.getValueAt(row, 2));
            txtTenDangNhap.setText((String) tableModel.getValueAt(row, 3));
            txtMatKhau.setText((String) tableModel.getValueAt(row, 4));
            txtEmail.setText((String) tableModel.getValueAt(row, 5));
            selectComboByName(cboNganh, (String) tableModel.getValueAt(row, 6), NganhDTO::getTenNganh);
        }
    }

    @Override
    protected void them() {
        if (!validateNotEmpty(txtTenDangNhap, "tên đăng nhập") || !validateNotEmpty(txtHo, "họ") ||
                !validateNotEmpty(txtTen, "tên"))
            return;
        if (txtMaSV.getText().isEmpty() && txtMatKhau.getPassword().length == 0) {
            showMessage("Vui lòng nhập mật khẩu!");
            txtMatKhau.requestFocus();
            return;
        }

        SinhVienDTO sv = new SinhVienDTO();
        sv.setTenDangNhap(txtTenDangNhap.getText().trim());
        sv.setMatKhau(new String(txtMatKhau.getPassword()));
        sv.setHo(txtHo.getText().trim());
        sv.setTen(txtTen.getText().trim());
        sv.setEmail(txtEmail.getText().trim());
        NganhDTO nganh = (NganhDTO) cboNganh.getSelectedItem();
        if (nganh != null)
            sv.setMaNganh(nganh.getMaNganh());

        if (truongKhoaBUS.themSinhVien(sv)) {
            showMessage("Thêm sinh viên thành công!");
            loadData();
            lamMoi();
        } else
            showMessage("Thêm sinh viên thất bại!");
    }

    @Override
    protected void sua() {
        if (txtMaSV.getText().isEmpty()) {
            showMessage("Vui lòng chọn sinh viên cần sửa!");
            return;
        }
        if (!validateNotEmpty(txtTenDangNhap, "tên đăng nhập") || !validateNotEmpty(txtHo, "họ") ||
                !validateNotEmpty(txtTen, "tên"))
            return;

        SinhVienDTO sv = new SinhVienDTO();
        sv.setMaSV(Integer.parseInt(txtMaSV.getText()));
        sv.setTenDangNhap(txtTenDangNhap.getText().trim());
        sv.setHo(txtHo.getText().trim());
        sv.setTen(txtTen.getText().trim());
        sv.setEmail(txtEmail.getText().trim());
        NganhDTO nganh = (NganhDTO) cboNganh.getSelectedItem();
        if (nganh != null)
            sv.setMaNganh(nganh.getMaNganh());
        String matKhauMoi = new String(txtMatKhau.getPassword());
        if (!matKhauMoi.isEmpty())
            sv.setMatKhau(matKhauMoi);

        if (truongKhoaBUS.capNhatSinhVien(sv)) {
            showMessage("Cập nhật sinh viên thành công!");
            loadData();
        } else
            showMessage("Cập nhật sinh viên thất bại!");
    }

    @Override
    protected void xoa() {
        if (txtMaSV.getText().isEmpty()) {
            showMessage("Vui lòng chọn sinh viên cần xóa!");
            return;
        }
        if (confirmDelete("sinh viên")) {
            if (truongKhoaBUS.xoaSinhVien(Integer.parseInt(txtMaSV.getText()))) {
                showMessage("Xóa sinh viên thành công!");
                loadData();
                lamMoi();
            } else
                showMessage("Xóa sinh viên thất bại!");
        }
    }

    @Override
    protected void lamMoi() {
        txtMaSV.setText("");
        txtTenDangNhap.setText("");
        txtHo.setText("");
        txtTen.setText("");
        txtEmail.setText("");
        txtMatKhau.setText("");
        if (cboNganh.getItemCount() > 0)
            cboNganh.setSelectedIndex(0);
        table.clearSelection();
    }
}
