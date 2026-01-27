package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.HocPhanDTO;
import gui.components.BaseCrudPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuanLyHocPhanPanel extends BaseCrudPanel {
    private TruongKhoaBUS truongKhoaBUS = new TruongKhoaBUS();
    private JTextField txtTenMon;
    private JSpinner spnSoTin;
    private int selectedMaHocPhan = -1;

    public QuanLyHocPhanPanel() {
        super("QUẢN LÝ HỌC PHẦN",
                new String[] { "Mã HP", "Tên Học Phần", "Số TC" },
                new String[] { "Tất cả", "Mã HP", "Tên Học Phần" });
        
    
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

        txtTenMon = createTextField(35, true);
        spnSoTin = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spnSoTin.setPreferredSize(new Dimension(80, 28));
        spnSoTin.setFont(Constants.NORMAL_FONT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Tên môn:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(txtTenMon, gbc);
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(createLabel("Số tín chỉ:"), gbc);
        gbc.gridx = 3;
        panel.add(spnSoTin, gbc);

        return panel;
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        List<HocPhanDTO> danhSach = truongKhoaBUS.getDanhSachHocPhan();
        if (danhSach != null) {
            danhSach.forEach(hp -> tableModel.addRow(new Object[] {
                    hp.getMaHocPhan(), hp.getTenMon(), hp.getSoTin()
            }));
        }
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();
        tableModel.setRowCount(0);

        List<HocPhanDTO> danhSach = keyword.isEmpty() || "Tất cả".equals(loai)
                ? truongKhoaBUS.timKiemHocPhan(keyword)
                : truongKhoaBUS.getDanhSachHocPhan();

        if (danhSach != null) {
            danhSach.stream().filter(hp -> matchFilter(hp, keyword, loai))
                    .forEach(hp -> tableModel.addRow(new Object[] {
                            hp.getMaHocPhan(), hp.getTenMon(), hp.getSoTin()
                    }));
        }
    }

    private boolean matchFilter(HocPhanDTO hp, String keyword, String loai) {
        if (keyword.isEmpty() || "Tất cả".equals(loai))
            return true;
        return switch (loai) {
            case "Mã HP" -> String.valueOf(hp.getMaHocPhan()).contains(keyword);
            case "Tên Học Phần" -> hp.getTenMon() != null && hp.getTenMon().toLowerCase().contains(keyword);
            default -> true;
        };
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedMaHocPhan = (int) tableModel.getValueAt(row, 0);
            txtTenMon.setText((String) tableModel.getValueAt(row, 1));
            spnSoTin.setValue(tableModel.getValueAt(row, 2));
        }
    }

    @Override
    protected void them() {
        if (!validateNotEmpty(txtTenMon, "tên học phần"))
            return;

        HocPhanDTO hp = new HocPhanDTO();
        hp.setTenMon(txtTenMon.getText().trim());
        hp.setSoTin((Integer) spnSoTin.getValue());

        if (truongKhoaBUS.themHocPhan(hp)) {
            showMessage("Thêm học phần thành công!");
            loadData();
            lamMoi();
        } else
            showMessage("Thêm học phần thất bại!");
    }

    @Override
    protected void sua() {
        if (selectedMaHocPhan == -1) {
            showMessage("Vui lòng chọn học phần cần sửa!");
            return;
        }
        if (!validateNotEmpty(txtTenMon, "tên học phần"))
            return;

        HocPhanDTO hp = new HocPhanDTO();
        hp.setMaHocPhan(selectedMaHocPhan);
        hp.setTenMon(txtTenMon.getText().trim());
        hp.setSoTin((Integer) spnSoTin.getValue());

        if (truongKhoaBUS.capNhatHocPhan(hp)) {
            showMessage("Cập nhật học phần thành công!");
            loadData();
            lamMoi();
        } else
            showMessage("Cập nhật học phần thất bại!");
    }

    @Override
    protected void xoa() {
        if (selectedMaHocPhan == -1) {
            showMessage("Vui lòng chọn học phần cần xóa!");
            return;
        }
        if (confirmDelete("học phần")) {
            if (truongKhoaBUS.xoaHocPhan(selectedMaHocPhan)) {
                showMessage("Xóa học phần thành công!");
                loadData();
                lamMoi();
            } else
                showMessage("Xóa học phần thất bại! Học phần có thể đang được sử dụng.");
        }
    }

    @Override
    protected void lamMoi() {
        txtTenMon.setText("");
        spnSoTin.setValue(3);
        table.clearSelection();
        selectedMaHocPhan = -1;
    }
}
