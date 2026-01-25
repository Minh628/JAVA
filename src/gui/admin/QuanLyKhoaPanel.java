/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyKhoaPanel - Panel quản lý Khoa
 */
package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.KhoaDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLyKhoaPanel extends JPanel {
    private TruongKhoaBUS truongKhoaBUS;
    
    private CustomTable tblKhoa;
    private DefaultTableModel modelKhoa;
    
    private JTextField txtTenKhoa;
    
    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    
    private int selectedMaKhoa = -1;

    public QuanLyKhoaPanel() {
        this.truongKhoaBUS = new TruongKhoaBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHOA", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);
        
        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
            "Thông tin khoa"
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Tên khoa
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblTenKhoa = new JLabel("Tên khoa:");
        lblTenKhoa.setFont(Constants.NORMAL_FONT);
        panelForm.add(lblTenKhoa, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenKhoa = new JTextField(40);
        txtTenKhoa.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenKhoa, gbc);
        
        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);
        
        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);
        
        btnThem.addActionListener(e -> themKhoa());
        btnSua.addActionListener(e -> suaKhoa());
        btnXoa.addActionListener(e -> xoaKhoa());
        btnLamMoi.addActionListener(e -> lamMoi());
        
        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForm.add(panelNut, gbc);
        
        // Panel trên: Tiêu đề + Form
        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.CONTENT_BG);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);
        
        // Bảng
        String[] columns = {"Mã Khoa", "Tên Khoa"};
        modelKhoa = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKhoa = new CustomTable(modelKhoa);
        tblKhoa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblKhoa);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadData() {
        modelKhoa.setRowCount(0);
        List<KhoaDTO> danhSach = truongKhoaBUS.getDanhSachKhoa();
        if (danhSach != null) {
            for (KhoaDTO khoa : danhSach) {
                modelKhoa.addRow(new Object[]{
                    khoa.getMaKhoa(), khoa.getTenKhoa()
                });
            }
        }
    }
    
    private void hienThiThongTin() {
        int row = tblKhoa.getSelectedRow();
        if (row >= 0) {
            selectedMaKhoa = (int) modelKhoa.getValueAt(row, 0);
            txtTenKhoa.setText((String) modelKhoa.getValueAt(row, 1));
        }
    }
    
    private void themKhoa() {
        if (!validateInput()) return;
        
        KhoaDTO khoa = new KhoaDTO();
        khoa.setTenKhoa(txtTenKhoa.getText().trim());
        
        if (truongKhoaBUS.themKhoa(khoa)) {
            JOptionPane.showMessageDialog(this, "Thêm khoa thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khoa thất bại!");
        }
    }
    
    private void suaKhoa() {
        if (selectedMaKhoa == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoa cần sửa!");
            return;
        }
        if (!validateInput()) return;
        
        KhoaDTO khoa = new KhoaDTO();
        khoa.setMaKhoa(selectedMaKhoa);
        khoa.setTenKhoa(txtTenKhoa.getText().trim());
        
        if (truongKhoaBUS.capNhatKhoa(khoa)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khoa thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khoa thất bại!");
        }
    }
    
    private void xoaKhoa() {
        if (selectedMaKhoa == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoa cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa khoa này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (truongKhoaBUS.xoaKhoa(selectedMaKhoa)) {
                JOptionPane.showMessageDialog(this, "Xóa khoa thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khoa thất bại! Khoa có thể đang được sử dụng.");
            }
        }
    }
    
    private void lamMoi() {
        txtTenKhoa.setText("");
        tblKhoa.clearSelection();
        selectedMaKhoa = -1;
    }
    
    private boolean validateInput() {
        if (txtTenKhoa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khoa!");
            txtTenKhoa.requestFocus();
            return false;
        }
        return true;
    }
}
