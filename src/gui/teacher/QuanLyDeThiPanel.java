/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyDeThiPanel - Panel quản lý đề thi cho giảng viên
 */
package gui.teacher;

import bus.GiangVienBUS;
import config.Constants;
import dto.DeThiDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import dto.KyThiDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLyDeThiPanel extends JPanel {
    private GiangVienDTO nguoiDung;
    private GiangVienBUS giangVienBUS;
    
    private CustomTable tblDeThi;
    private DefaultTableModel modelDeThi;
    
    // Form fields
    private JTextField txtTenDeThi;
    private JComboBox<HocPhanDTO> cboHocPhan;
    private JComboBox<KyThiDTO> cboKyThi;
    private JSpinner spnSoCau;
    private JSpinner spnThoiGian;
    
    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    
    private int selectedMaDeThi = -1;

    public QuanLyDeThiPanel(GiangVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.giangVienBUS = new GiangVienBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ ĐỀ THI", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);
        
        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
            "Thông tin đề thi"
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Tên đề thi
        gbc.gridx = 0; gbc.gridy = 0;
        addLabel(panelForm, "Tên đề thi:", gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenDeThi = new JTextField(30);
        txtTenDeThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenDeThi, gbc);
        
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        addLabel(panelForm, "Học phần:", gbc);
        gbc.gridx = 3;
        cboHocPhan = new JComboBox<>();
        cboHocPhan.setPreferredSize(new Dimension(200, 28));
        cboHocPhan.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboHocPhan, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        addLabel(panelForm, "Kỳ thi:", gbc);
        gbc.gridx = 1;
        cboKyThi = new JComboBox<>();
        cboKyThi.setPreferredSize(new Dimension(200, 28));
        cboKyThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboKyThi, gbc);
        
        gbc.gridx = 2;
        addLabel(panelForm, "Số câu hỏi:", gbc);
        gbc.gridx = 3;
        spnSoCau = new JSpinner(new SpinnerNumberModel(20, 5, 100, 5));
        spnSoCau.setPreferredSize(new Dimension(80, 28));
        spnSoCau.setFont(Constants.NORMAL_FONT);
        panelForm.add(spnSoCau, gbc);
        
        gbc.gridx = 4;
        addLabel(panelForm, "Thời gian (phút):", gbc);
        gbc.gridx = 5;
        spnThoiGian = new JSpinner(new SpinnerNumberModel(45, 10, 180, 5));
        spnThoiGian.setPreferredSize(new Dimension(80, 28));
        spnThoiGian.setFont(Constants.NORMAL_FONT);
        panelForm.add(spnThoiGian, gbc);
        
        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);
        
        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);
        
        btnThem.addActionListener(e -> themDeThi());
        btnSua.addActionListener(e -> suaDeThi());
        btnXoa.addActionListener(e -> xoaDeThi());
        btnLamMoi.addActionListener(e -> lamMoi());
        
        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForm.add(panelNut, gbc);
        
        // Panel trên: Tiêu đề + Form
        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.CONTENT_BG);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);
        
        // Bảng
        String[] columns = {"Mã đề", "Tên đề thi", "Học phần", "Kỳ thi", "Số câu", "Thời gian"};
        modelDeThi = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDeThi = new CustomTable(modelDeThi);
        tblDeThi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblDeThi);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Constants.NORMAL_FONT);
        panel.add(lbl, gbc);
    }
    
    private void loadData() {
        loadHocPhan();
        loadKyThi();
        loadDeThi();
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
    
    private void loadKyThi() {
        cboKyThi.removeAllItems();
        List<KyThiDTO> danhSach = giangVienBUS.getDanhSachKyThi();
        if (danhSach != null) {
            for (KyThiDTO kt : danhSach) {
                cboKyThi.addItem(kt);
            }
        }
    }
    
    private void loadDeThi() {
        modelDeThi.setRowCount(0);
        List<DeThiDTO> danhSach = giangVienBUS.getDanhSachDeThi(nguoiDung.getMaGV());
        if (danhSach != null) {
            for (DeThiDTO dt : danhSach) {
                modelDeThi.addRow(new Object[]{
                    dt.getMaDeThi(), dt.getTenDeThi(), dt.getTenHocPhan(), 
                    dt.getTenKyThi(), dt.getSoCauHoi(), dt.getThoiGianLam() + " phút"
                });
            }
        }
    }
    
    private void hienThiThongTin() {
        int row = tblDeThi.getSelectedRow();
        if (row >= 0) {
            selectedMaDeThi = (int) modelDeThi.getValueAt(row, 0);
            txtTenDeThi.setText((String) modelDeThi.getValueAt(row, 1));
            
            String tenHocPhan = (String) modelDeThi.getValueAt(row, 2);
            for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
                if (cboHocPhan.getItemAt(i).getTenMon().equals(tenHocPhan)) {
                    cboHocPhan.setSelectedIndex(i);
                    break;
                }
            }
            
            String tenKyThi = (String) modelDeThi.getValueAt(row, 3);
            for (int i = 0; i < cboKyThi.getItemCount(); i++) {
                if (cboKyThi.getItemAt(i).getTenKyThi().equals(tenKyThi)) {
                    cboKyThi.setSelectedIndex(i);
                    break;
                }
            }
            
            spnSoCau.setValue(modelDeThi.getValueAt(row, 4));
            String thoiGian = (String) modelDeThi.getValueAt(row, 5);
            spnThoiGian.setValue(Integer.parseInt(thoiGian.replace(" phút", "")));
        }
    }
    
    private void themDeThi() {
        if (!validateInput()) return;
        
        DeThiDTO deThi = new DeThiDTO();
        deThi.setTenDeThi(txtTenDeThi.getText().trim());
        deThi.setMaGV(nguoiDung.getMaGV());
        
        HocPhanDTO hp = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hp != null) deThi.setMaHocPhan(hp.getMaHocPhan());
        
        KyThiDTO kt = (KyThiDTO) cboKyThi.getSelectedItem();
        if (kt != null) deThi.setMaKyThi(kt.getMaKyThi());
        
        deThi.setSoCauHoi((Integer) spnSoCau.getValue());
        deThi.setThoiGianLam((Integer) spnThoiGian.getValue());
        
        if (giangVienBUS.themDeThi(deThi)) {
            JOptionPane.showMessageDialog(this, "Thêm đề thi thành công!");
            loadDeThi();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm đề thi thất bại!");
        }
    }
    
    private void suaDeThi() {
        if (selectedMaDeThi == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi cần sửa!");
            return;
        }
        if (!validateInput()) return;
        
        DeThiDTO deThi = new DeThiDTO();
        deThi.setMaDeThi(selectedMaDeThi);
        deThi.setTenDeThi(txtTenDeThi.getText().trim());
        deThi.setMaGV(nguoiDung.getMaGV());
        
        HocPhanDTO hp = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hp != null) deThi.setMaHocPhan(hp.getMaHocPhan());
        
        KyThiDTO kt = (KyThiDTO) cboKyThi.getSelectedItem();
        if (kt != null) deThi.setMaKyThi(kt.getMaKyThi());
        
        deThi.setSoCauHoi((Integer) spnSoCau.getValue());
        deThi.setThoiGianLam((Integer) spnThoiGian.getValue());
        
        if (giangVienBUS.capNhatDeThi(deThi)) {
            JOptionPane.showMessageDialog(this, "Cập nhật đề thi thành công!");
            loadDeThi();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật đề thi thất bại!");
        }
    }
    
    private void xoaDeThi() {
        if (selectedMaDeThi == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa đề thi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (giangVienBUS.xoaDeThi(selectedMaDeThi)) {
                JOptionPane.showMessageDialog(this, "Xóa đề thi thành công!");
                loadDeThi();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa đề thi thất bại!");
            }
        }
    }
    
    private void lamMoi() {
        txtTenDeThi.setText("");
        if (cboHocPhan.getItemCount() > 0) cboHocPhan.setSelectedIndex(0);
        if (cboKyThi.getItemCount() > 0) cboKyThi.setSelectedIndex(0);
        spnSoCau.setValue(20);
        spnThoiGian.setValue(45);
        tblDeThi.clearSelection();
        selectedMaDeThi = -1;
    }
    
    private boolean validateInput() {
        if (txtTenDeThi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đề thi!");
            txtTenDeThi.requestFocus();
            return false;
        }
        if (cboHocPhan.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học phần!");
            return false;
        }
        if (cboKyThi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kỳ thi!");
            return false;
        }
        return true;
    }
}
