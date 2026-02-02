package gui.admin;

import bus.HocPhanBUS;
import bus.KhoaBUS;
import config.Constants;
import dto.HocPhanDTO;
import dto.KhoaDTO;
import gui.components.BaseCrudPanel;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class QuanLyHocPhanPanel extends BaseCrudPanel {
    private HocPhanBUS hocPhanBUS = new HocPhanBUS();
    private KhoaBUS khoaBUS = new KhoaBUS();
    private JTextField txtTenMon;
    private JSpinner spnSoTin;
    private JComboBox<KhoaDTO> cboKhoa;
    private int selectedMaHocPhan = -1;

    public QuanLyHocPhanPanel() {
        super("QUẢN LÝ HỌC PHẦN",
                new String[] { "Mã HP", "Tên Học Phần", "Số TC", "Khoa" },
                new String[] { "Tất cả", "Mã HP", "Tên Học Phần", "Khoa" });
        
        loadKhoaData();
    }

    private void loadKhoaData() {
        cboKhoa.removeAllItems();
        List<KhoaDTO> danhSachKhoa = khoaBUS.getDanhSachKhoa();
        if (danhSachKhoa != null) {
            for (KhoaDTO khoa : danhSachKhoa) {
                cboKhoa.addItem(khoa);
            }
        }
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

        txtTenMon = createTextField(25, true);
        spnSoTin = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spnSoTin.setPreferredSize(new Dimension(80, 28));
        spnSoTin.setFont(Constants.NORMAL_FONT);
        
        cboKhoa = new JComboBox<>();
        cboKhoa.setPreferredSize(new Dimension(200, 28));
        cboKhoa.setFont(Constants.NORMAL_FONT);

        // Row 0: Tên môn và Số tín chỉ
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
        
        // Row 1: Khoa
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel("Khoa:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(cboKhoa, gbc);

        return panel;
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        List<HocPhanDTO> danhSach = hocPhanBUS.getDanhSachHocPhan();
        if (danhSach != null) {
            danhSach.forEach(hp -> {
                String tenKhoa = getTenKhoa(hp.getMaKhoa());
                tableModel.addRow(new Object[] {
                    hp.getMaHocPhan(), hp.getTenMon(), hp.getSoTin(), tenKhoa
                });
            });
        }
    }
    
    private String getTenKhoa(int maKhoa) {
        KhoaDTO khoa = khoaBUS.getById(maKhoa);
        return khoa != null ? khoa.getTenKhoa() : "";
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();
        tableModel.setRowCount(0);

        List<HocPhanDTO> danhSach = hocPhanBUS.getDanhSachHocPhan();

        if (danhSach != null) {
            danhSach.stream().filter(hp -> matchFilter(hp, keyword, loai))
                    .forEach(hp -> {
                        String tenKhoa = getTenKhoa(hp.getMaKhoa());
                        tableModel.addRow(new Object[] {
                            hp.getMaHocPhan(), hp.getTenMon(), hp.getSoTin(), tenKhoa
                        });
                    });
        }
    }

    private boolean matchFilter(HocPhanDTO hp, String keyword, String loai) {
        if (keyword.isEmpty() || "Tất cả".equals(loai))
            return true;
        return switch (loai) {
            case "Mã HP" -> String.valueOf(hp.getMaHocPhan()).contains(keyword);
            case "Tên Học Phần" -> hp.getTenMon() != null && hp.getTenMon().toLowerCase().contains(keyword);
            case "Khoa" -> {
                String tenKhoa = getTenKhoa(hp.getMaKhoa());
                yield tenKhoa != null && tenKhoa.toLowerCase().contains(keyword);
            }
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
            
            // Tìm và chọn khoa tương ứng trong combobox
            String tenKhoa = (String) tableModel.getValueAt(row, 3);
            for (int i = 0; i < cboKhoa.getItemCount(); i++) {
                KhoaDTO khoa = cboKhoa.getItemAt(i);
                if (khoa != null && khoa.getTenKhoa().equals(tenKhoa)) {
                    cboKhoa.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void them() {
        if (!validateNotEmpty(txtTenMon, "tên học phần"))
            return;
        if (cboKhoa.getSelectedItem() == null) {
            showMessage("Vui lòng chọn khoa!");
            return;
        }

        HocPhanDTO hp = new HocPhanDTO();
        hp.setTenMon(txtTenMon.getText().trim());
        hp.setSoTin((Integer) spnSoTin.getValue());
        hp.setMaKhoa(((KhoaDTO) cboKhoa.getSelectedItem()).getMaKhoa());

        if (hocPhanBUS.themHocPhan(hp)) {
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
        if (cboKhoa.getSelectedItem() == null) {
            showMessage("Vui lòng chọn khoa!");
            return;
        }

        HocPhanDTO hp = new HocPhanDTO();
        hp.setMaHocPhan(selectedMaHocPhan);
        hp.setTenMon(txtTenMon.getText().trim());
        hp.setSoTin((Integer) spnSoTin.getValue());
        hp.setMaKhoa(((KhoaDTO) cboKhoa.getSelectedItem()).getMaKhoa());

        if (hocPhanBUS.capNhatHocPhan(hp)) {
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
            if (hocPhanBUS.xoaHocPhan(selectedMaHocPhan)) {
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
        if (cboKhoa.getItemCount() > 0) {
            cboKhoa.setSelectedIndex(0);
        }
        table.clearSelection();
        selectedMaHocPhan = -1;
    }
}
