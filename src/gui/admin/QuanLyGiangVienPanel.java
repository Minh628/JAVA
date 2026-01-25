/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyGiangVienPanel - Panel quản lý giảng viên
 */
package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.GiangVienDTO;
import dto.KhoaDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class QuanLyGiangVienPanel extends JPanel {
    private TruongKhoaBUS truongKhoaBUS;
    
    private CustomTable tblGiangVien;
    private DefaultTableModel modelGiangVien;
    
    private JTextField txtMaGV;
    private JTextField txtTenDangNhap;
    private JTextField txtHo;
    private JTextField txtTen;
    private JTextField txtEmail;
    private JPasswordField txtMatKhau;
    private JComboBox<KhoaDTO> cboKhoa;
    
    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;

    public QuanLyGiangVienPanel() {
        this.truongKhoaBUS = new TruongKhoaBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ GIẢNG VIÊN", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);
        
        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.BACKGROUND_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin giảng viên"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Mã GV:"), gbc);
        gbc.gridx = 1;
        txtMaGV = new JTextField(15);
        txtMaGV.setEditable(false);
        panelForm.add(txtMaGV, gbc);
        
        gbc.gridx = 2;
        panelForm.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 3;
        txtTenDangNhap = new JTextField(15);
        panelForm.add(txtTenDangNhap, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(new JLabel("Họ:"), gbc);
        gbc.gridx = 1;
        txtHo = new JTextField(15);
        panelForm.add(txtHo, gbc);
        
        gbc.gridx = 2;
        panelForm.add(new JLabel("Tên:"), gbc);
        gbc.gridx = 3;
        txtTen = new JTextField(15);
        panelForm.add(txtTen, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        panelForm.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        panelForm.add(txtEmail, gbc);
        
        gbc.gridx = 2;
        panelForm.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 3;
        txtMatKhau = new JPasswordField(15);
        panelForm.add(txtMatKhau, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        panelForm.add(new JLabel("Khoa:"), gbc);
        gbc.gridx = 1;
        cboKhoa = new JComboBox<>();
        cboKhoa.setPreferredSize(new Dimension(170, 25));
        panelForm.add(cboKhoa, gbc);
        
        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout());
        panelNut.setBackground(Constants.BACKGROUND_COLOR);
        
        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);
        
        btnThem.addActionListener(e -> themGiangVien());
        btnSua.addActionListener(e -> suaGiangVien());
        btnXoa.addActionListener(e -> xoaGiangVien());
        btnLamMoi.addActionListener(e -> lamMoi());
        
        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        panelForm.add(panelNut, gbc);
        
        // Panel trên: Tiêu đề + Form
        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.BACKGROUND_COLOR);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);
        
        // Bảng
        String[] columns = {"Mã GV", "Tên đăng nhập", "Họ", "Tên", "Email", "Khoa", "Trạng thái"};
        modelGiangVien = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGiangVien = new CustomTable(modelGiangVien);
        tblGiangVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblGiangVien);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadData() {
        loadKhoa();
        loadGiangVien();
    }
    
    private void loadKhoa() {
        cboKhoa.removeAllItems();
        List<KhoaDTO> danhSach = truongKhoaBUS.getDanhSachKhoa();
        if (danhSach != null) {
            for (KhoaDTO khoa : danhSach) {
                cboKhoa.addItem(khoa);
            }
        }
    }
    
    private void loadGiangVien() {
        modelGiangVien.setRowCount(0);
        List<GiangVienDTO> danhSach = truongKhoaBUS.getDanhSachGiangVien();
        if (danhSach != null) {
            for (GiangVienDTO gv : danhSach) {
                modelGiangVien.addRow(new Object[]{
                    gv.getMaGV(), gv.getTenDangNhap(), gv.getHo(), gv.getTen(),
                    gv.getEmail(), gv.getTenKhoa(), 
                    gv.getTrangThai() == 1 ? "Hoạt động" : "Khóa"
                });
            }
        }
    }
    
    private void hienThiThongTin() {
        int row = tblGiangVien.getSelectedRow();
        if (row >= 0) {
            txtMaGV.setText(String.valueOf(modelGiangVien.getValueAt(row, 0)));
            txtTenDangNhap.setText((String) modelGiangVien.getValueAt(row, 1));
            txtHo.setText((String) modelGiangVien.getValueAt(row, 2));
            txtTen.setText((String) modelGiangVien.getValueAt(row, 3));
            txtEmail.setText((String) modelGiangVien.getValueAt(row, 4));
            
            String tenKhoa = (String) modelGiangVien.getValueAt(row, 5);
            for (int i = 0; i < cboKhoa.getItemCount(); i++) {
                if (cboKhoa.getItemAt(i).getTenKhoa().equals(tenKhoa)) {
                    cboKhoa.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void themGiangVien() {
        if (!validateInput()) return;
        
        GiangVienDTO gv = new GiangVienDTO();
        gv.setTenDangNhap(txtTenDangNhap.getText().trim());
        gv.setMatKhau(new String(txtMatKhau.getPassword()));
        gv.setHo(txtHo.getText().trim());
        gv.setTen(txtTen.getText().trim());
        gv.setEmail(txtEmail.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null) {
            gv.setMaKhoa(khoa.getMaKhoa());
        }
        
        if (truongKhoaBUS.themGiangVien(gv)) {
            JOptionPane.showMessageDialog(this, "Thêm giảng viên thành công!");
            loadGiangVien();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm giảng viên thất bại!");
        }
    }
    
    private void suaGiangVien() {
        if (txtMaGV.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giảng viên cần sửa!");
            return;
        }
        if (!validateInput()) return;
        
        GiangVienDTO gv = new GiangVienDTO();
        gv.setMaGV(Integer.parseInt(txtMaGV.getText()));
        gv.setTenDangNhap(txtTenDangNhap.getText().trim());
        gv.setHo(txtHo.getText().trim());
        gv.setTen(txtTen.getText().trim());
        gv.setEmail(txtEmail.getText().trim());
        KhoaDTO khoa = (KhoaDTO) cboKhoa.getSelectedItem();
        if (khoa != null) {
            gv.setMaKhoa(khoa.getMaKhoa());
        }
        
        // Chỉ cập nhật mật khẩu nếu có nhập
        String matKhauMoi = new String(txtMatKhau.getPassword());
        if (!matKhauMoi.isEmpty()) {
            gv.setMatKhau(matKhauMoi);
        }
        
        if (truongKhoaBUS.capNhatGiangVien(gv)) {
            JOptionPane.showMessageDialog(this, "Cập nhật giảng viên thành công!");
            loadGiangVien();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật giảng viên thất bại!");
        }
    }
    
    private void xoaGiangVien() {
        if (txtMaGV.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giảng viên cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa giảng viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int maGV = Integer.parseInt(txtMaGV.getText());
            if (truongKhoaBUS.xoaGiangVien(maGV)) {
                JOptionPane.showMessageDialog(this, "Xóa giảng viên thành công!");
                loadGiangVien();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa giảng viên thất bại!");
            }
        }
    }
    
    private void lamMoi() {
        txtMaGV.setText("");
        txtTenDangNhap.setText("");
        txtHo.setText("");
        txtTen.setText("");
        txtEmail.setText("");
        txtMatKhau.setText("");
        cboKhoa.setSelectedIndex(0);
        tblGiangVien.clearSelection();
    }
    
    private boolean validateInput() {
        if (txtTenDangNhap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!");
            txtTenDangNhap.requestFocus();
            return false;
        }
        if (txtHo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ!");
            txtHo.requestFocus();
            return false;
        }
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên!");
            txtTen.requestFocus();
            return false;
        }
        if (txtMaGV.getText().isEmpty() && txtMatKhau.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!");
            txtMatKhau.requestFocus();
            return false;
        }
        return true;
    }
}
