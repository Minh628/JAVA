package gui.admin;

import bus.GiangVienBUS;
import bus.KhoaBUS;
import config.Constants;
import dto.GiangVienDTO;
import dto.KhoaDTO;
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

public class QuanLyGiangVienPanel extends BaseCrudPanel {
    private GiangVienBUS giangVienBUS = new GiangVienBUS();
    private KhoaBUS khoaBUS = new KhoaBUS();
    private JTextField txtMaGV, txtTenDangNhap, txtHo, txtTen, txtEmail;
    private JPasswordField txtMatKhau;
    private JComboBox<KhoaDTO> cboKhoa;
    private CustomButton btnChonKhoa;
    private Map<Integer, String> khoaMap = new HashMap<>();

    public QuanLyGiangVienPanel() {
        super("QUẢN LÝ GIẢNG VIÊN",
                new String[] { "Mã GV", "Tên đăng nhập", "Mật khẩu", "Họ", "Tên", "Email", "Khoa", "Trạng thái" },
                new String[] { "Tất cả", "Mã GV", "Tên đăng nhập", "Họ tên", "Email", "Khoa" });
        
    
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

        txtMaGV = createTextField(10, false);
        txtTenDangNhap = createTextField(15, true);
        txtMatKhau = new JPasswordField(15);
        txtHo = createTextField(15, true);
        txtTen = createTextField(15, true);
        txtEmail = createTextField(20, true);
        cboKhoa = new JComboBox<>();
        cboKhoa.setPreferredSize(new Dimension(150, 30));

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Mã GV:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMaGV, gbc);
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
        panel.add(createLabel("Khoa:"), gbc);
        gbc.gridx = 1;
        panel.add(cboKhoa, gbc);
        gbc.gridx = 2;
        btnChonKhoa = new CustomButton("...", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnChonKhoa.setPreferredSize(new Dimension(45, 28));
        btnChonKhoa.addActionListener(e -> moChonKhoa());
        panel.add(btnChonKhoa, gbc);

        return panel;
    }

    @Override
    protected void loadData() {
        cboKhoa.removeAllItems();
        khoaMap.clear();
        List<KhoaDTO> khoaList = khoaBUS.getDanhSachKhoa();
        if (khoaList != null) {
            khoaList.forEach(k -> {
                cboKhoa.addItem(k);
                khoaMap.put(k.getMaKhoa(), k.getTenKhoa());
            });
        }

        tableModel.setRowCount(0);
        List<GiangVienDTO> danhSach = giangVienBUS.getDanhSachGiangVien();
        if (danhSach != null) {
            danhSach.forEach(gv -> tableModel.addRow(new Object[] {
                    gv.getMaGV(), gv.getTenDangNhap(),
                    gv.getMatKhau() != null ? gv.getMatKhau() : "",
                    gv.getHo(), gv.getTen(), gv.getEmail(), getTenKhoa(gv.getMaKhoa()),
                    gv.isTrangThai() ? "Hoạt động" : "Khóa"
            }));
        }
    }

    private String getTenKhoa(int maKhoa) {
        return khoaMap.getOrDefault(maKhoa, String.valueOf(maKhoa));
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();

        tableModel.setRowCount(0);

        List<GiangVienDTO> danhSach = giangVienBUS.timKiem(keyword, loai);

        for (GiangVienDTO gv : danhSach) {
            tableModel.addRow(new Object[] {
                gv.getMaGV(), gv.getTenDangNhap(),
                gv.getMatKhau() != null ? gv.getMatKhau() : "",
                gv.getHo(), gv.getTen(), gv.getEmail(), getTenKhoa(gv.getMaKhoa()),
                gv.isTrangThai() ? "Hoạt động" : "Khóa"
            });
        }
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMaGV.setText(String.valueOf(tableModel.getValueAt(row, 0)));
            txtTenDangNhap.setText((String) tableModel.getValueAt(row, 1));
            txtMatKhau.setText((String) tableModel.getValueAt(row, 2));
            txtHo.setText((String) tableModel.getValueAt(row, 3));
            txtTen.setText((String) tableModel.getValueAt(row, 4));
            txtEmail.setText((String) tableModel.getValueAt(row, 5));
            selectComboByName(cboKhoa, (String) tableModel.getValueAt(row, 6), KhoaDTO::getTenKhoa);
        }
    }

    @Override
    protected void them() {
        if (!validateNotEmpty(txtTenDangNhap, "tên đăng nhập") || !validateNotEmpty(txtHo, "họ") ||
                !validateNotEmpty(txtTen, "tên"))
            return;
        if (txtMaGV.getText().isEmpty() && txtMatKhau.getPassword().length == 0) {
            showMessage("Vui lòng nhập mật khẩu!");
            txtMatKhau.requestFocus();
            return;
        }

        GiangVienDTO gv = new GiangVienDTO();
        gv.setTenDangNhap(txtTenDangNhap.getText().trim());
        gv.setMatKhau(new String(txtMatKhau.getPassword()));
        gv.setHo(txtHo.getText().trim());
        gv.setTen(txtTen.getText().trim());
        gv.setEmail(txtEmail.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null)
            gv.setMaKhoa(khoa.getMaKhoa());

        if (giangVienBUS.themGiangVien(gv)) {
            showMessage("Thêm giảng viên thành công!");
            loadData();
            lamMoi();
        } else
            showMessage("Thêm giảng viên thất bại!");
    }

    @Override
    protected void sua() {
        if (txtMaGV.getText().isEmpty()) {
            showMessage("Vui lòng chọn giảng viên cần sửa!");
            return;
        }
        if (!validateNotEmpty(txtTenDangNhap, "tên đăng nhập") || !validateNotEmpty(txtHo, "họ") ||
                !validateNotEmpty(txtTen, "tên"))
            return;

        GiangVienDTO gv = new GiangVienDTO();
        gv.setMaGV(Integer.parseInt(txtMaGV.getText()));
        gv.setTenDangNhap(txtTenDangNhap.getText().trim());
        gv.setHo(txtHo.getText().trim());
        gv.setTen(txtTen.getText().trim());
        gv.setEmail(txtEmail.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null)
            gv.setMaKhoa(khoa.getMaKhoa());
        String matKhauMoi = new String(txtMatKhau.getPassword());
        if (!matKhauMoi.isEmpty())
            gv.setMatKhau(matKhauMoi);

        if (giangVienBUS.capNhatGiangVien(gv)) {
            showMessage("Cập nhật giảng viên thành công!");
            loadData();
        } else
            showMessage("Cập nhật giảng viên thất bại!");
    }

    @Override
    protected void xoa() {
        if (txtMaGV.getText().isEmpty()) {
            showMessage("Vui lòng chọn giảng viên cần xóa!");
            return;
        }
        if (confirmDelete("giảng viên")) {
            if (giangVienBUS.xoaGiangVien(Integer.parseInt(txtMaGV.getText()))) {
                showMessage("Xóa giảng viên thành công!");
                loadData();
                lamMoi();
            } else
                showMessage("Xóa giảng viên thất bại!");
        }
    }

    @Override
    protected void lamMoi() {
        txtMaGV.setText("");
        txtTenDangNhap.setText("");
        txtHo.setText("");
        txtTen.setText("");
        txtEmail.setText("");
        txtMatKhau.setText("");
        if (cboKhoa.getItemCount() > 0)
            cboKhoa.setSelectedIndex(0);
        table.clearSelection();
    }

    @Override
    protected void addExtraSearchComponents(JPanel searchPanel) {
        CustomButton btnTimNangCao = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnTimNangCao.addActionListener(e -> moTimKiemNangCao());
        searchPanel.add(btnTimNangCao);
    }

    private void moChonKhoa() {
        List<KhoaDTO> khoaList = khoaBUS.getDanhSachKhoa();
        SelectEntityDialog.clearSelection();
        SelectEntityDialog<KhoaDTO> dialog = new SelectEntityDialog<>(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chọn khoa",
                "KHOA",
                khoaList,
                KhoaDTO::getMaKhoa,
                KhoaDTO::getTenKhoa
        );
        dialog.setVisible(true);

        if ("KHOA".equals(SelectEntityDialog.getSelectedType())) {
            int maKhoa = SelectEntityDialog.getSelectedId();
            if (maKhoa >= 0) {
                selectKhoaById(maKhoa);
            }
        }
    }

    private void selectKhoaById(int maKhoa) {
        for (int i = 0; i < cboKhoa.getItemCount(); i++) {
            KhoaDTO k = cboKhoa.getItemAt(i);
            if (k != null && k.getMaKhoa() == maKhoa) {
                cboKhoa.setSelectedIndex(i);
                return;
            }
        }
    }

    private void moTimKiemNangCao() {
        String[] searchFields = { "Mã GV", "Tên đăng nhập", "Họ tên", "Email", "Khoa", "Trạng thái" };
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tìm kiếm giảng viên nâng cao",
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
        List<GiangVienDTO> danhSach = giangVienBUS.timKiemNangCao(conditions, logic);
        for (GiangVienDTO gv : danhSach) {
            tableModel.addRow(new Object[] {
                gv.getMaGV(), gv.getTenDangNhap(),
                gv.getMatKhau() != null ? gv.getMatKhau() : "",
                gv.getHo(), gv.getTen(), gv.getEmail(), getTenKhoa(gv.getMaKhoa()),
                gv.isTrangThai() ? "Hoạt động" : "Khóa"
            });
        }
    }
}
