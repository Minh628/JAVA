package gui.admin;

import bus.NganhBUS;
import bus.SinhVienBUS;
import config.Constants;
import dto.NganhDTO;
import dto.SinhVienDTO;
import gui.components.AdvancedSearchDialog;
import gui.components.BaseCrudPanel;
import gui.components.CustomButton;
import gui.components.SelectEntityDialog;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import util.SearchCondition;

public class QuanLySinhVienPanel extends BaseCrudPanel {
    private SinhVienBUS sinhVienBUS = new SinhVienBUS();
    private NganhBUS nganhBUS = new NganhBUS();
    private JTextField txtMaSV, txtTenDangNhap, txtHo, txtTen, txtEmail;
    private JPasswordField txtMatKhau;
    private JComboBox<NganhDTO> cboNganh;
    private CustomButton btnChonNganh;
    private Map<Integer, String> nganhMap = new HashMap<>();

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
        gbc.gridx = 2;
        btnChonNganh = new CustomButton("...", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnChonNganh.setPreferredSize(new Dimension(45, 28));
        btnChonNganh.addActionListener(e -> moChonNganh());
        panel.add(btnChonNganh, gbc);

        return panel;
    }

    @Override
    protected void loadData() {
        cboNganh.removeAllItems();
        nganhMap.clear();
        List<NganhDTO> nganhList = nganhBUS.getDanhSachNganh();
        if (nganhList != null) {
            nganhList.forEach(n -> {
                cboNganh.addItem(n);
                nganhMap.put(n.getMaNganh(), n.getTenNganh());
            });
        }

        tableModel.setRowCount(0);
        List<SinhVienDTO> danhSach = sinhVienBUS.getDanhSachSinhVien();
        if (danhSach != null) {
            danhSach.forEach(sv -> tableModel.addRow(new Object[] {
                    sv.getMaSV(), sv.getHo(), sv.getTen(), sv.getTenDangNhap(),
                    sv.getMatKhau() != null ? sv.getMatKhau() : "",
                    sv.getEmail(), getTenNganh(sv.getMaNganh()),
                    sv.isTrangThai() ? "Hoạt động" : "Khóa"
            }));
        }
    }

    private String getTenNganh(int maNganh) {
        return nganhMap.getOrDefault(maNganh, String.valueOf(maNganh));
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();

        tableModel.setRowCount(0);

        List<SinhVienDTO> danhSach = sinhVienBUS.timKiem(keyword, loai);

        for (SinhVienDTO sv : danhSach) {
            tableModel.addRow(new Object[] {
                sv.getMaSV(), sv.getHo(), sv.getTen(), sv.getTenDangNhap(),
                sv.getMatKhau() != null ? sv.getMatKhau() : "",
                sv.getEmail(), getTenNganh(sv.getMaNganh()),
                sv.isTrangThai() ? "Hoạt động" : "Khóa"
            });
        }
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

        if (sinhVienBUS.themSinhVien(sv)) {
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

        if (sinhVienBUS.capNhatSinhVien(sv)) {
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
            if (sinhVienBUS.xoaSinhVien(Integer.parseInt(txtMaSV.getText()))) {
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

    @Override
    protected void addExtraSearchComponents(JPanel searchPanel) {
        CustomButton btnTimNangCao = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnTimNangCao.addActionListener(e -> moTimKiemNangCao());
        searchPanel.add(btnTimNangCao);
    }

    private void moChonNganh() {
        List<NganhDTO> nganhList = nganhBUS.getDanhSachNganh();
        SelectEntityDialog.clearSelection();
        SelectEntityDialog<NganhDTO> dialog = new SelectEntityDialog<>(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chọn ngành",
                "NGANH",
                nganhList,
                NganhDTO::getMaNganh,
                NganhDTO::getTenNganh
        );
        dialog.setVisible(true);

        if ("NGANH".equals(SelectEntityDialog.getSelectedType())) {
            int maNganh = SelectEntityDialog.getSelectedId();
            if (maNganh >= 0) {
                selectNganhById(maNganh);
            }
        }
    }

    private void selectNganhById(int maNganh) {
        for (int i = 0; i < cboNganh.getItemCount(); i++) {
            NganhDTO n = cboNganh.getItemAt(i);
            if (n != null && n.getMaNganh() == maNganh) {
                cboNganh.setSelectedIndex(i);
                return;
            }
        }
    }

    private void moTimKiemNangCao() {
        String[] searchFields = { "Mã SV", "Tên đăng nhập", "Họ tên", "Email", "Ngành", "Trạng thái" };
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tìm kiếm sinh viên nâng cao",
                searchFields
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            List<SearchCondition> conditions = dialog.getConditions();
            String logic = dialog.getLogic();
            timKiemNangCao(conditions, logic);
        }
    }

    private void timKiemNangCao(List<SearchCondition> conditions, String logic) {
        tableModel.setRowCount(0);
        List<SinhVienDTO> danhSach = sinhVienBUS.timKiemNangCao(conditions, logic);
        for (SinhVienDTO sv : danhSach) {
            tableModel.addRow(new Object[] {
                sv.getMaSV(), sv.getHo(), sv.getTen(), sv.getTenDangNhap(),
                sv.getMatKhau() != null ? sv.getMatKhau() : "",
                sv.getEmail(), getTenNganh(sv.getMaNganh()),
                sv.isTrangThai() ? "Hoạt động" : "Khóa"
            });
        }
    }
}
