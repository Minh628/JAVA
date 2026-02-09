/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyKhoaPanel - Panel quản lý Khoa và Ngành
 * Có 2 JTable: Click vào Khoa -> hiện danh sách Ngành thuộc Khoa đó
 */
package gui.admin;

import bus.KhoaBUS;
import bus.NganhBUS;
import config.Constants;
import dto.KhoaDTO;
import dto.NganhDTO;
import gui.components.AdvancedSearchDialog;
import gui.components.BaseCrudPanel;
import gui.components.CustomButton;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import util.SearchCondition;

public class QuanLyKhoaPanel extends BaseCrudPanel {
    private final KhoaBUS khoaBUS;
    private final NganhBUS nganhBUS;
    private JTextField txtMaKhoa;
    private JTextField txtTenKhoa;
    private JTextField txtSoNganh;

    private int selectedMaKhoa = -1;

    public QuanLyKhoaPanel() {
        super(
                "QUẢN LÝ KHOA",
                "Danh sách Khoa",
                new String[] { "Mã Khoa", "Tên Khoa", "Số ngành" },
                "Danh sách Ngành thuộc Khoa",
                new String[] { "Mã Ngành", "Tên Ngành" },
                new String[] { "Tất cả", "Mã Khoa", "Tên Khoa" }
        );
        this.khoaBUS = new KhoaBUS();
        this.nganhBUS = new NganhBUS();
    }

    @Override
    protected JPanel createFormPanel() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
                "Thông tin khoa"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Mã khoa
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(createLabel("Mã khoa:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtMaKhoa = createTextField(10, false);
        panelForm.add(txtMaKhoa, gbc);

        // Row 1: Tên khoa
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelForm.add(createLabel("Tên khoa:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenKhoa = createTextField(40, true);
        panelForm.add(txtTenKhoa, gbc);

        // Row 2: Số ngành
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; 
        gbc.weightx = 0;
        panelForm.add(createLabel("Số ngành:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtSoNganh = createTextField(10, false);
        panelForm.add(txtSoNganh, gbc);
        return panelForm;
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        List<KhoaDTO> danhSach = khoaBUS.getDanhSachKhoa();
        if (danhSach != null) {
            for (KhoaDTO khoa : danhSach) {
                // Đếm số ngành thuộc khoa
                int soNganh = nganhBUS.getNganhTheoKhoa(khoa.getMaKhoa()).size();
                tableModel.addRow(new Object[] {
                        khoa.getMaKhoa(), khoa.getTenKhoa(), soNganh
                });
            }
        }
        // Clear bảng ngành
        secondaryTableModel.setRowCount(0);
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();

        tableModel.setRowCount(0);

        List<KhoaDTO> danhSach = khoaBUS.timKiem(keyword, loaiTimKiem);

        for (KhoaDTO khoa : danhSach) {
            int soNganh = nganhBUS.getNganhTheoKhoa(khoa.getMaKhoa()).size();
            tableModel.addRow(new Object[] {
                khoa.getMaKhoa(), khoa.getTenKhoa(), soNganh
            });
        }
        secondaryTableModel.setRowCount(0);
    }

    /**
     * Load danh sách ngành thuộc khoa đang được chọn
     */
    private void loadNganhTheoKhoa() {
        secondaryTableModel.setRowCount(0);
        if (selectedMaKhoa == -1) {
            return;
        }

        List<NganhDTO> danhSachNganh = nganhBUS.getNganhTheoKhoa(selectedMaKhoa);
        if (danhSachNganh != null) {
            for (NganhDTO nganh : danhSachNganh) {
                secondaryTableModel.addRow(new Object[] {
                        nganh.getMaNganh(), nganh.getTenNganh()
                });
            }
        }
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedMaKhoa = (int) tableModel.getValueAt(row, 0);
            txtMaKhoa.setText(String.valueOf(selectedMaKhoa));
            txtTenKhoa.setText((String) tableModel.getValueAt(row, 1));
            txtSoNganh.setText(tableModel.getValueAt(row, 2).toString());
            loadNganhTheoKhoa();
        }
    }

    @Override
    protected void them() {
        if (!validateInput())
            return;

        KhoaDTO khoa = new KhoaDTO();
        khoa.setTenKhoa(txtTenKhoa.getText().trim());

        if (khoaBUS.themKhoa(khoa)) {
            showMessage("Thêm khoa thành công!");
            loadData();
            lamMoi();
        } else {
            showMessage("Thêm khoa thất bại!");
        }
    }

    @Override
    protected void sua() {
        if (selectedMaKhoa == -1) {
            showMessage("Vui lòng chọn khoa cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        KhoaDTO khoa = new KhoaDTO();
        khoa.setMaKhoa(selectedMaKhoa);
        khoa.setTenKhoa(txtTenKhoa.getText().trim());

        if (khoaBUS.capNhatKhoa(khoa)) {
            showMessage("Cập nhật khoa thành công!");
            loadData();
            lamMoi();
        } else {
            showMessage("Cập nhật khoa thất bại!");
        }
    }

    @Override
    protected void xoa() {
        if (selectedMaKhoa == -1) {
            showMessage("Vui lòng chọn khoa cần xóa!");
            return;
        }

        if (confirmDelete("khoa")) {
            if (khoaBUS.xoaKhoa(selectedMaKhoa)) {
                showMessage("Xóa khoa thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa khoa!\nKhoa này đang có ngành học thuộc về hoặc có lỗi xảy ra.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    protected void lamMoi() {
        txtMaKhoa.setText("");
        txtTenKhoa.setText("");
        txtSoNganh.setText("");
        table.clearSelection();
        selectedMaKhoa = -1;
        secondaryTableModel.setRowCount(0); // Clear bảng ngành
    }

    private boolean validateInput() {
        if (txtTenKhoa.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập tên khoa!");
            txtTenKhoa.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void addExtraSearchComponents(JPanel searchPanel) {
        CustomButton btnTimNangCao = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnTimNangCao.addActionListener(e -> moTimKiemNangCao());
        searchPanel.add(btnTimNangCao);
    }

    private void moTimKiemNangCao() {
        String[] searchFields = { "Mã Khoa", "Tên Khoa" };
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tìm kiếm khoa nâng cao",
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
        List<KhoaDTO> danhSach = khoaBUS.timKiemNangCao(conditions, logic);
        for (KhoaDTO khoa : danhSach) {
            int soNganh = nganhBUS.getNganhTheoKhoa(khoa.getMaKhoa()).size();
            tableModel.addRow(new Object[] {
                khoa.getMaKhoa(), khoa.getTenKhoa(), soNganh
            });
        }
        secondaryTableModel.setRowCount(0);
    }
}
