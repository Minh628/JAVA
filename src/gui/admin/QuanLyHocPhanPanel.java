package gui.admin;

import bus.HocPhanBUS;
import bus.KhoaBUS;
import config.Constants;
import dto.HocPhanDTO;
import dto.KhoaDTO;
import gui.components.AdvancedSearchDialog;
import gui.components.BaseCrudPanel;
import gui.components.CustomButton;
import gui.components.SelectEntityDialog;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import util.SearchCondition;

public class QuanLyHocPhanPanel extends BaseCrudPanel {
    private HocPhanBUS hocPhanBUS = new HocPhanBUS();
    private KhoaBUS khoaBUS = new KhoaBUS();
    private JTextField txtMaHocPhan;
    private JTextField txtTenMon;
    private JSpinner spnSoTin;
    private JComboBox<KhoaDTO> cboKhoa;
    private CustomButton btnChonKhoa;
    private int selectedMaHocPhan = -1;

    public QuanLyHocPhanPanel() {
        super("QUẢN LÝ HỌC PHẦN",
                new String[] { "Mã HP", "Tên Học Phần", "Số TC", "Khoa" },
                new String[] { "Tất cả", "Mã HP", "Tên Học Phần","Số TC", "Khoa" });
        
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

        txtMaHocPhan = createTextField(10, false);
        txtTenMon = createTextField(25, true);
        spnSoTin = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spnSoTin.setPreferredSize(new Dimension(80, 28));
        spnSoTin.setFont(Constants.NORMAL_FONT);
        
        cboKhoa = new JComboBox<>();
        cboKhoa.setPreferredSize(new Dimension(200, 28));
        cboKhoa.setFont(Constants.NORMAL_FONT);

        // Row 0: Mã môn và Tên môn
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel("Mã HP:"), gbc);
        gbc.gridx = 1;
        panel.add(txtMaHocPhan, gbc);

        gbc.gridx = 2;
        panel.add(createLabel("Tên môn:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(txtTenMon, gbc);

        // Row 1: Số tín chỉ và Khoa
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(createLabel("Số tín chỉ:"), gbc);
        gbc.gridx = 1;
        panel.add(spnSoTin, gbc);
        
        gbc.gridx = 2;
        panel.add(createLabel("Khoa:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(cboKhoa, gbc);
        gbc.gridx = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        btnChonKhoa = new CustomButton("...", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnChonKhoa.setPreferredSize(new Dimension(45, 28));
        btnChonKhoa.addActionListener(e -> moChonKhoa());
        panel.add(btnChonKhoa, gbc);
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
        String keyword = txtTimKiem.getText().trim();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();

        tableModel.setRowCount(0);

        List<HocPhanDTO> danhSach = hocPhanBUS.timKiem(keyword, loai);

        for (HocPhanDTO hp : danhSach) {
            String tenKhoa = getTenKhoa(hp.getMaKhoa());
            tableModel.addRow(new Object[] {
                hp.getMaHocPhan(),
                hp.getTenMon(),
                hp.getSoTin(),
                tenKhoa
            });
        }
    }


    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedMaHocPhan = (int) tableModel.getValueAt(row, 0);
            txtMaHocPhan.setText(String.valueOf(selectedMaHocPhan));
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
        txtMaHocPhan.setText("");
        txtTenMon.setText("");
        spnSoTin.setValue(3);
        if (cboKhoa.getItemCount() > 0) {
            cboKhoa.setSelectedIndex(0);
        }
        table.clearSelection();
        selectedMaHocPhan = -1;
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
        String[] searchFields = { "Mã HP", "Tên Học Phần", "Số TC", "Khoa" };
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tìm kiếm học phần nâng cao",
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
        List<HocPhanDTO> danhSach = hocPhanBUS.timKiemNangCao(conditions, logic);
        for (HocPhanDTO hp : danhSach) {
            String tenKhoa = getTenKhoa(hp.getMaKhoa());
            tableModel.addRow(new Object[] {
                hp.getMaHocPhan(), hp.getTenMon(), hp.getSoTin(), tenKhoa
            });
        }
    }
}
