package gui.admin;

import bus.BaiThiBUS;
import bus.DeThiBUS;
import bus.HocPhanBUS;
import bus.NganhBUS;
import bus.SinhVienBUS;
import config.Constants;
import dto.BaiThiDTO;
import dto.DeThiDTO;
import dto.HocPhanDTO;
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
import util.SinhVienExcelImporter;

public class QuanLySinhVienPanel extends BaseCrudPanel {
    private SinhVienBUS sinhVienBUS = new SinhVienBUS();
    private NganhBUS nganhBUS = new NganhBUS();
    private BaiThiBUS baiThiBUS = new BaiThiBUS();
    private DeThiBUS deThiBUS = new DeThiBUS();
    private HocPhanBUS hocPhanBUS = new HocPhanBUS();
    
    private JTextField txtMaSV, txtTenDangNhap, txtHo, txtTen, txtEmail;
    private JPasswordField txtMatKhau;
    private JComboBox<NganhDTO> cboNganh;
    private CustomButton btnChonNganh;
    private Map<Integer, String> nganhMap = new HashMap<>();
    
    private int selectedMaSV = -1;

    public QuanLySinhVienPanel() {
        super("QUẢN LÝ SINH VIÊN",
                "Danh sách Sinh viên",
                new String[] { "Mã SV", "Họ", "Tên", "Tên đăng nhập", "Mật khẩu", "Email", "Ngành", "Trạng thái" },
                "Điểm thi của Sinh viên",
                new String[] { "Mã bài thi", "Đề thi", "Môn học", "Ngày thi", "Số câu đúng", "Điểm" },
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
        // Clear bảng điểm
        secondaryTableModel.setRowCount(0);
        selectedMaSV = -1;
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
        // Clear bảng điểm
        secondaryTableModel.setRowCount(0);
        selectedMaSV = -1;
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedMaSV = (int) tableModel.getValueAt(row, 0);
            txtMaSV.setText(String.valueOf(selectedMaSV));
            txtHo.setText((String) tableModel.getValueAt(row, 1));
            txtTen.setText((String) tableModel.getValueAt(row, 2));
            txtTenDangNhap.setText((String) tableModel.getValueAt(row, 3));
            txtMatKhau.setText((String) tableModel.getValueAt(row, 4));
            txtEmail.setText((String) tableModel.getValueAt(row, 5));
            selectComboByName(cboNganh, (String) tableModel.getValueAt(row, 6), NganhDTO::getTenNganh);
            loadDiemTheoSinhVien();
        }
    }

    /**
     * Load danh sách điểm thi của sinh viên đang được chọn
     */
    private void loadDiemTheoSinhVien() {
        secondaryTableModel.setRowCount(0);
        if (selectedMaSV == -1) {
            return;
        }

        List<BaiThiDTO> danhSachBaiThi = baiThiBUS.getLichSuBaiThi(selectedMaSV);
        if (danhSachBaiThi != null) {
            for (BaiThiDTO bt : danhSachBaiThi) {
                DeThiDTO deThi = deThiBUS.getById(bt.getMaDeThi());
                String tenDeThi = deThi != null ? deThi.getTenDeThi() : "";
                String tenHocPhan = "";
                int tongSoCau = 0;
                if (deThi != null) {
                    tongSoCau = deThi.getSoCauHoi();
                    HocPhanDTO hocPhan = hocPhanBUS.getById(deThi.getMaHocPhan());
                    tenHocPhan = hocPhan != null ? hocPhan.getTenMon() : "";
                }
                secondaryTableModel.addRow(new Object[] {
                        bt.getMaBaiThi(), tenDeThi, tenHocPhan,
                        bt.getNgayThi(), bt.getSoCauDung() + "/" + tongSoCau,
                        String.format("%.2f", bt.getDiemSo())
                });
            }
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
        if (selectedMaSV == -1) {
            showMessage("Vui lòng chọn sinh viên cần xóa!");
            return;
        }
        if (confirmDelete("sinh viên (bao gồm tất cả bài thi và chi tiết bài thi)")) {
            // Xóa bài thi và chi tiết bài thi của sinh viên trước
            baiThiBUS.xoaBaiThiTheoSinhVien(selectedMaSV);
            // Xóa sinh viên
            if (sinhVienBUS.xoaSinhVien(selectedMaSV)) {
                showMessage("Xóa sinh viên thành công!");
                loadData();
                lamMoi();
            } else {
                showMessage("Xóa sinh viên thất bại!");
            }
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
        selectedMaSV = -1;
        secondaryTableModel.setRowCount(0); // Clear bảng điểm
    }

    @Override
    protected void addExtraSearchComponents(JPanel searchPanel) {
        CustomButton btnTimNangCao = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnTimNangCao.addActionListener(e -> moTimKiemNangCao());
        searchPanel.add(btnTimNangCao);
        
        // Nút Import từ Excel
        CustomButton btnImportExcel = new CustomButton("Import Excel", new Color(34, 139, 34), Constants.TEXT_COLOR);
        btnImportExcel.addActionListener(e -> importSinhVienTuExcel());
        searchPanel.add(btnImportExcel);
        
        // Nút tạo file mẫu
        CustomButton btnTaoMau = new CustomButton("Tải mẫu Excel", new Color(70, 130, 180), Constants.TEXT_COLOR);
        btnTaoMau.addActionListener(e -> SinhVienExcelImporter.createTemplateFile(this));
        searchPanel.add(btnTaoMau);
    }
    
    /**
     * Import sinh viên từ file Excel
     */
    private void importSinhVienTuExcel() {
        List<SinhVienDTO> danhSach = SinhVienExcelImporter.importFromExcel(this);
        
        if (danhSach == null || danhSach.isEmpty()) {
            return;
        }
        
        // Xác nhận import
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có muốn import " + danhSach.size() + " sinh viên vào hệ thống?",
            "Xác nhận Import",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Thực hiện import
        int success = 0, fail = 0;
        StringBuilder errors = new StringBuilder();
        
        for (SinhVienDTO sv : danhSach) {
            try {
                if (sinhVienBUS.themSinhVien(sv)) {
                    success++;
                } else {
                    fail++;
                    errors.append("• ").append(sv.getTenDangNhap()).append(": Thêm thất bại\n");
                }
            } catch (Exception e) {
                fail++;
                errors.append("• ").append(sv.getTenDangNhap()).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        // Hiển thị kết quả
        StringBuilder result = new StringBuilder();
        result.append("Kết quả import:\n\n");
        result.append("• Thành công: ").append(success).append(" sinh viên\n");
        result.append("• Thất bại: ").append(fail).append(" sinh viên\n");
        
        if (fail > 0 && errors.length() > 0) {
            result.append("\nChi tiết lỗi:\n").append(errors.toString().substring(0, Math.min(errors.length(), 500)));
        }
        
        JOptionPane.showMessageDialog(this, result.toString(),
            fail > 0 ? "Import hoàn tất với lỗi" : "Import thành công",
            fail > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        
        // Refresh danh sách
        loadData();
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
        // Clear bảng điểm
        secondaryTableModel.setRowCount(0);
        selectedMaSV = -1;
    }
}
