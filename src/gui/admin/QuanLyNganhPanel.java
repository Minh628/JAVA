package gui.admin;

import bus.KhoaBUS;
import bus.NganhBUS;
import bus.SinhVienBUS;
import config.Constants;
import dto.KhoaDTO;
import dto.NganhDTO;
import gui.components.BaseCrudPanel;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class QuanLyNganhPanel extends BaseCrudPanel {
    private NganhBUS nganhBUS = new NganhBUS();
    private KhoaBUS khoaBUS = new KhoaBUS();
    private JTextField txtTenNganh;
    private JComboBox<KhoaDTO> cboKhoa;
    private int selectedMaNganh = -1;

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

        txtTenNganh = createTextField(30, true);
        cboKhoa = new JComboBox<>();
        cboKhoa.setPreferredSize(new Dimension(250, 30));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Tên ngành:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTenNganh, gbc);
        gbc.gridx = 2;
        panel.add(createLabel("Thuộc khoa:"), gbc);
        gbc.gridx = 3;
        panel.add(cboKhoa, gbc);

        return panel;
    }

    @Override
    protected void loadData() {
        cboKhoa.removeAllItems();
        List<KhoaDTO> khoaList = khoaBUS.getDanhSachKhoa();
        if (khoaList != null)
            khoaList.forEach(cboKhoa::addItem);

        tableModel.setRowCount(0);
        List<NganhDTO> danhSach = nganhBUS.getDanhSachNganh();
        if (danhSach != null) {
            danhSach.forEach(n -> tableModel.addRow(new Object[] {
                    n.getMaNganh(), n.getTenNganh(),
                    n.getTenKhoa() != null ? n.getTenKhoa() : String.valueOf(n.getMaKhoa())
            }));
        }
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();
        tableModel.setRowCount(0);

        List<NganhDTO> danhSach = keyword.isEmpty() || "Tất cả".equals(loai)
                ? nganhBUS.timKiem(keyword)
                : nganhBUS.getDanhSachNganh();

        if (danhSach != null) {
            danhSach.stream().filter(n -> matchFilter(n, keyword, loai))
                    .forEach(n -> tableModel.addRow(new Object[] {
                            n.getMaNganh(), n.getTenNganh(),
                            n.getTenKhoa() != null ? n.getTenKhoa() : String.valueOf(n.getMaKhoa())
                    }));
        }
    }

    private boolean matchFilter(NganhDTO n, String keyword, String loai) {
        if (keyword.isEmpty() || "Tất cả".equals(loai))
            return true;
        return switch (loai) {
            case "Mã Ngành" -> String.valueOf(n.getMaNganh()).contains(keyword);
            case "Tên Ngành" -> n.getTenNganh() != null && n.getTenNganh().toLowerCase().contains(keyword);
            case "Thuộc Khoa" -> n.getTenKhoa() != null && n.getTenKhoa().toLowerCase().contains(keyword);
            default -> true;
        };
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedMaNganh = (int) tableModel.getValueAt(row, 0);
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
        txtTenNganh.setText("");
        if (cboKhoa.getItemCount() > 0)
            cboKhoa.setSelectedIndex(0);
        table.clearSelection();
        selectedMaNganh = -1;
    }
}
