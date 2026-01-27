/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: SoanCauHoiPanel - Panel soạn câu hỏi
 */
package gui.teacher;

import bus.GiangVienBUS;
import config.Constants;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SoanCauHoiPanel extends JPanel {
    private GiangVienDTO giangVien;
    private GiangVienBUS giangVienBUS;

    private CustomTable tblCauHoi;
    private DefaultTableModel modelCauHoi;

    private JTextField txtMaCauHoi;
    private JTextArea txtNoiDung;
    private JTextField txtDapAnA;
    private JTextField txtDapAnB;
    private JTextField txtDapAnC;
    private JTextField txtDapAnD;
    private JComboBox<String> cboDapAnDung;
    private JComboBox<HocPhanDTO> cboHocPhan;
    private JComboBox<String> cboMucDo;

    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;
    private CustomButton btnTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;

    public SoanCauHoiPanel(GiangVienDTO giangVien) {
        this.giangVien = giangVien;
        this.giangVienBUS = new GiangVienBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("SOẠN CÂU HỎI TRẮC NGHIỆM", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.BACKGROUND_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin câu hỏi"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Mã câu hỏi, Học phần
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("Mã câu hỏi:"), gbc);
        gbc.gridx = 1;
        txtMaCauHoi = new JTextField(10);
        txtMaCauHoi.setEditable(false);
        panelForm.add(txtMaCauHoi, gbc);

        gbc.gridx = 2;
        panelForm.add(new JLabel("Học phần:"), gbc);
        gbc.gridx = 3;
        cboHocPhan = new JComboBox<>();
        cboHocPhan.setPreferredSize(new Dimension(200, 25));
        panelForm.add(cboHocPhan, gbc);

        gbc.gridx = 4;
        panelForm.add(new JLabel("Mức độ:"), gbc);
        gbc.gridx = 5;
        cboMucDo = new JComboBox<>(new String[] { "Dễ", "Trung bình", "Khó" });
        panelForm.add(cboMucDo, gbc);

        // Row 2: Nội dung câu hỏi
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNoiDung = new JTextArea(3, 50);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        JScrollPane scrollNoiDung = new JScrollPane(txtNoiDung);
        panelForm.add(scrollNoiDung, gbc);

        // Row 3: Đáp án A, B
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelForm.add(new JLabel("Đáp án A:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnA = new JTextField(25);
        panelForm.add(txtDapAnA, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 3;
        panelForm.add(new JLabel("Đáp án B:"), gbc);
        gbc.gridx = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnB = new JTextField(25);
        panelForm.add(txtDapAnB, gbc);

        // Row 4: Đáp án C, D
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelForm.add(new JLabel("Đáp án C:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnC = new JTextField(25);
        panelForm.add(txtDapAnC, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 3;
        panelForm.add(new JLabel("Đáp án D:"), gbc);
        gbc.gridx = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnD = new JTextField(25);
        panelForm.add(txtDapAnD, gbc);

        // Row 5: Đáp án đúng
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelForm.add(new JLabel("Đáp án đúng:"), gbc);
        gbc.gridx = 1;
        cboDapAnDung = new JComboBox<>(new String[] { "A", "B", "C", "D" });
        panelForm.add(cboDapAnDung, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout());
        panelNut.setBackground(Constants.BACKGROUND_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themCauHoi());
        btnSua.addActionListener(e -> suaCauHoi());
        btnXoa.addActionListener(e -> xoaCauHoi());
        btnLamMoi.addActionListener(e -> lamMoi());

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);

        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        panelForm.add(panelNut, gbc);

        // Panel trên
        JPanel panelTren = new JPanel(new BorderLayout());
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);

        // Bảng câu hỏi
        String[] columns = { "Mã", "Nội dung câu hỏi", "Môn học", "Mức độ", "Đáp án đúng" };
        modelCauHoi = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCauHoi = new CustomTable(modelCauHoi);
        tblCauHoi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.BACKGROUND_COLOR);

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã", "Nội dung", "Môn học", "Mức độ" });
        cboLoaiTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(cboLoaiTimKiem);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(Constants.NORMAL_FONT);
        txtTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(txtTimKiem);

        btnTimKiem = new CustomButton("Tìm", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(btnTimKiem);

        CustomButton btnHienTatCa = new CustomButton("Hiện tất cả", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            loadCauHoi();
        });
        panelTimKiem.add(btnHienTatCa);

        // Panel center chứa tìm kiếm và bảng
        JPanel panelCenter = new JPanel(new BorderLayout(0, 5));
        panelCenter.setBackground(Constants.BACKGROUND_COLOR);
        panelCenter.add(panelTimKiem, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tblCauHoi);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);
    }

    private void loadData() {
        loadHocPhan();
        loadCauHoi();
    }

    private void loadHocPhan() {
        cboHocPhan.removeAllItems();
        List<HocPhanDTO> danhSach = giangVienBUS.getDanhSachHocPhan();
        if (danhSach != null) {
            for (HocPhanDTO hp : danhSach) {
                cboHocPhan.addItem(hp);
            }
        }
    }

    private void loadCauHoi() {
        modelCauHoi.setRowCount(0);
        List<CauHoiDTO> danhSach = giangVienBUS.getDanhSachCauHoi(giangVien.getMaGV());
        if (danhSach != null) {
            for (CauHoiDTO ch : danhSach) {
                String noiDung = ch.getNoiDungCauHoi();
                if (noiDung.length() > 60) {
                    noiDung = noiDung.substring(0, 60) + "...";
                }
                modelCauHoi.addRow(new Object[] {
                        ch.getMaCauHoi(), noiDung, ch.getTenMon(),
                        ch.getMucDo(), ch.getDapAnDung()
                });
            }
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelCauHoi.setRowCount(0);

        List<CauHoiDTO> danhSach = giangVienBUS.getDanhSachCauHoi(giangVien.getMaGV());
        if (danhSach != null) {
            for (CauHoiDTO ch : danhSach) {
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã":
                            match = String.valueOf(ch.getMaCauHoi()).contains(keyword);
                            break;
                        case "Nội dung":
                            match = ch.getNoiDungCauHoi() != null
                                    && ch.getNoiDungCauHoi().toLowerCase().contains(keyLower);
                            break;
                        case "Môn học":
                            match = ch.getTenMon() != null && ch.getTenMon().toLowerCase().contains(keyLower);
                            break;
                        case "Mức độ":
                            match = ch.getMucDo() != null && ch.getMucDo().toLowerCase().contains(keyLower);
                            break;
                    }
                } else if (!keyword.isEmpty()) {
                    String keyLower = keyword.toLowerCase();
                    match = String.valueOf(ch.getMaCauHoi()).contains(keyword)
                            || (ch.getNoiDungCauHoi() != null && ch.getNoiDungCauHoi().toLowerCase().contains(keyLower))
                            || (ch.getTenMon() != null && ch.getTenMon().toLowerCase().contains(keyLower))
                            || (ch.getMucDo() != null && ch.getMucDo().toLowerCase().contains(keyLower));
                }
                if (match) {
                    String noiDung = ch.getNoiDungCauHoi();
                    if (noiDung.length() > 60) {
                        noiDung = noiDung.substring(0, 60) + "...";
                    }
                    modelCauHoi.addRow(new Object[] {
                            ch.getMaCauHoi(), noiDung, ch.getTenMon(),
                            ch.getMucDo(), ch.getDapAnDung()
                    });
                }
            }
        }
    }

    private void hienThiThongTin() {
        int row = tblCauHoi.getSelectedRow();
        if (row >= 0) {
            int maCauHoi = (int) modelCauHoi.getValueAt(row, 0);
            CauHoiDTO cauHoi = giangVienBUS.getCauHoiById(maCauHoi);

            if (cauHoi != null) {
                txtMaCauHoi.setText(String.valueOf(cauHoi.getMaCauHoi()));
                txtNoiDung.setText(cauHoi.getNoiDungCauHoi());
                txtDapAnA.setText(cauHoi.getNoiDungA());
                txtDapAnB.setText(cauHoi.getNoiDungB());
                txtDapAnC.setText(cauHoi.getNoiDungC());
                txtDapAnD.setText(cauHoi.getNoiDungD());
                cboDapAnDung.setSelectedItem(cauHoi.getDapAnDung());

                // Chọn mức độ
                String mucDo = cauHoi.getMucDo();
                for (int i = 0; i < cboMucDo.getItemCount(); i++) {
                    if (cboMucDo.getItemAt(i).equals(mucDo)) {
                        cboMucDo.setSelectedIndex(i);
                        break;
                    }
                }

                // Chọn học phần
                for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
                    if (cboHocPhan.getItemAt(i).getMaHocPhan() == cauHoi.getMaMon()) {
                        cboHocPhan.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void themCauHoi() {
        if (!validateInput())
            return;

        CauHoiMCDTO cauHoi = new CauHoiMCDTO();
        cauHoi.setMaGV(giangVien.getMaGV());
        HocPhanDTO hocPhan = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hocPhan != null) {
            cauHoi.setMaMon(hocPhan.getMaHocPhan());
        }
        cauHoi.setNoiDungCauHoi(txtNoiDung.getText().trim());
        cauHoi.setNoiDungA(txtDapAnA.getText().trim());
        cauHoi.setNoiDungB(txtDapAnB.getText().trim());
        cauHoi.setNoiDungC(txtDapAnC.getText().trim());
        cauHoi.setNoiDungD(txtDapAnD.getText().trim());
        cauHoi.setDapAnDung((String) cboDapAnDung.getSelectedItem());
        cauHoi.setMucDo((String) cboMucDo.getSelectedItem());

        if (giangVienBUS.themCauHoi(cauHoi)) {
            JOptionPane.showMessageDialog(this, "Thêm câu hỏi thành công!");
            loadCauHoi();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm câu hỏi thất bại!");
        }
    }

    private void suaCauHoi() {
        if (txtMaCauHoi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        CauHoiMCDTO cauHoi = new CauHoiMCDTO();
        cauHoi.setMaCauHoi(Integer.parseInt(txtMaCauHoi.getText()));
        cauHoi.setMaGV(giangVien.getMaGV());
        HocPhanDTO hocPhan = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hocPhan != null) {
            cauHoi.setMaMon(hocPhan.getMaHocPhan());
        }
        cauHoi.setNoiDungCauHoi(txtNoiDung.getText().trim());
        cauHoi.setNoiDungA(txtDapAnA.getText().trim());
        cauHoi.setNoiDungB(txtDapAnB.getText().trim());
        cauHoi.setNoiDungC(txtDapAnC.getText().trim());
        cauHoi.setNoiDungD(txtDapAnD.getText().trim());
        cauHoi.setDapAnDung((String) cboDapAnDung.getSelectedItem());
        cauHoi.setMucDo((String) cboMucDo.getSelectedItem());

        if (giangVienBUS.capNhatCauHoi(cauHoi)) {
            JOptionPane.showMessageDialog(this, "Cập nhật câu hỏi thành công!");
            loadCauHoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật câu hỏi thất bại!");
        }
    }

    private void xoaCauHoi() {
        if (txtMaCauHoi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa câu hỏi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int maCauHoi = Integer.parseInt(txtMaCauHoi.getText());
            if (giangVienBUS.xoaCauHoi(maCauHoi)) {
                JOptionPane.showMessageDialog(this, "Xóa câu hỏi thành công!");
                loadCauHoi();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa câu hỏi thất bại!");
            }
        }
    }

    private void lamMoi() {
        txtMaCauHoi.setText("");
        txtNoiDung.setText("");
        txtDapAnA.setText("");
        txtDapAnB.setText("");
        txtDapAnC.setText("");
        txtDapAnD.setText("");
        cboDapAnDung.setSelectedIndex(0);
        cboMucDo.setSelectedIndex(0);
        tblCauHoi.clearSelection();
    }

    private boolean validateInput() {
        if (txtNoiDung.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung câu hỏi!");
            txtNoiDung.requestFocus();
            return false;
        }
        if (txtDapAnA.getText().trim().isEmpty() || txtDapAnB.getText().trim().isEmpty() ||
                txtDapAnC.getText().trim().isEmpty() || txtDapAnD.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ 4 đáp án!");
            return false;
        }
        return true;
    }
}
