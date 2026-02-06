package gui.admin;

import bus.KhoaBUS;
import bus.NganhBUS;
import config.Constants;
import dto.KhoaDTO;
import dto.NganhDTO;
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

public class QuanLyNganhPanel extends BaseCrudPanel {
    private NganhBUS nganhBUS = new NganhBUS();
    private KhoaBUS khoaBUS = new KhoaBUS();
    private JTextField txtMaNganh;
    private JTextField txtTenNganh;
    private JComboBox<KhoaDTO> cboKhoa;
    private CustomButton btnChonKhoa;
    private int selectedMaNganh = -1;
    private Map<Integer, String> khoaMap = new HashMap<>();

    public QuanLyNganhPanel() {
        super("QUẢN LÝ NGÀNH",
                new String[] { "Mã Ngành", "Tên Ngành", "Thuộc Khoa" },
                new String[] { "Tất cả", "Mã Ngành", "Tên Ngành", "Thuộc Khoa" });
        
    
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

        txtMaNganh = createTextField(10, false);
        txtTenNganh = createTextField(30, true);
        cboKhoa = new JComboBox<>();
        cboKhoa.setPreferredSize(new Dimension(250, 30));

        // Row 0: Mã ngành
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Mã ngành:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMaNganh, gbc);

        // Row 1: Tên ngành
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel("Tên ngành:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTenNganh, gbc);

        // Row 2: Thuộc khoa
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Thuộc khoa:"), gbc);
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
        List<NganhDTO> danhSach = nganhBUS.getDanhSachNganh();
        if (danhSach != null) {
            danhSach.forEach(n -> tableModel.addRow(new Object[] {
                    n.getMaNganh(), n.getTenNganh(),
                    getTenKhoa(n.getMaKhoa())
            }));
        }
    }

    private String getTenKhoa(int maKhoa) {
        return khoaMap.getOrDefault(maKhoa, String.valueOf(maKhoa));
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

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();

        tableModel.setRowCount(0);

        List<NganhDTO> danhSach = nganhBUS.timKiem(keyword, loai);

        for (NganhDTO n : danhSach) {
            tableModel.addRow(new Object[] {
                n.getMaNganh(), n.getTenNganh(),
                getTenKhoa(n.getMaKhoa())
            });
        }
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedMaNganh = (int) tableModel.getValueAt(row, 0);
            txtMaNganh.setText(String.valueOf(selectedMaNganh));
            txtTenNganh.setText((String) tableModel.getValueAt(row, 1));
            String tenKhoa = (String) tableModel.getValueAt(row, 2);
            for (int i = 0; i < cboKhoa.getItemCount(); i++) {
                KhoaDTO k = cboKhoa.getItemAt(i);
                if (k.getTenKhoa().equals(tenKhoa) || String.valueOf(k.getMaKhoa()).equals(tenKhoa)) {
                    cboKhoa.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void them() {
        if (!validateNotEmpty(txtTenNganh, "tên ngành"))
            return;
        if (cboKhoa.getSelectedItem() == null) {
            showMessage("Vui lòng chọn khoa!");
            return;
        }

        NganhDTO nganh = new NganhDTO();
        nganh.setTenNganh(txtTenNganh.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null)
            nganh.setMaKhoa(khoa.getMaKhoa());

        if (nganhBUS.themNganh(nganh)) {
            showMessage("Thêm ngành thành công!");
            loadData();
            lamMoi();
        } else
            showMessage("Thêm ngành thất bại!");
    }

    @Override
    protected void sua() {
        if (selectedMaNganh == -1) {
            showMessage("Vui lòng chọn ngành cần sửa!");
            return;
        }
        if (!validateNotEmpty(txtTenNganh, "tên ngành"))
            return;

        NganhDTO nganh = new NganhDTO();
        nganh.setMaNganh(selectedMaNganh);
        nganh.setTenNganh(txtTenNganh.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null)
            nganh.setMaKhoa(khoa.getMaKhoa());

        if (nganhBUS.capNhatNganh(nganh)) {
            showMessage("Cập nhật ngành thành công!");
            loadData();
            lamMoi();
        } else
            showMessage("Cập nhật ngành thất bại!");
    }

    @Override
    protected void xoa() {
        if (selectedMaNganh == -1) {
            showMessage("Vui lòng chọn ngành cần xóa!");
            return;
        }
        if (confirmDelete("ngành")) {
            if (nganhBUS.xoaNganh(selectedMaNganh)) {
                showMessage("Xóa ngành thành công!");
                loadData();
                lamMoi();
            } else
                showMessage("Xóa ngành thất bại! Ngành có thể đang được sử dụng.");
        }
    }

    @Override
    protected void lamMoi() {
        txtMaNganh.setText("");
        txtTenNganh.setText("");
        if (cboKhoa.getItemCount() > 0)
            cboKhoa.setSelectedIndex(0);
        table.clearSelection();
        selectedMaNganh = -1;
    }

    @Override
    protected void addExtraSearchComponents(JPanel searchPanel) {
        CustomButton btnTimNangCao = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnTimNangCao.addActionListener(e -> moTimKiemNangCao());
        searchPanel.add(btnTimNangCao);
    }

    private void moTimKiemNangCao() {
        String[] searchFields = { "Mã Ngành", "Tên Ngành", "Thuộc Khoa" };
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tìm kiếm ngành nâng cao",
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
        List<NganhDTO> danhSach = nganhBUS.timKiemNangCao(conditions, logic);
        for (NganhDTO n : danhSach) {
            tableModel.addRow(new Object[] {
                n.getMaNganh(), n.getTenNganh(), getTenKhoa(n.getMaKhoa())
            });
        }
    }
}
