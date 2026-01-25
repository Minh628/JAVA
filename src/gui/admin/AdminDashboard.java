/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: AdminDashboard - Trưởng khoa
 * Refactored: Tách Khoa và Ngành, thêm thông tin cá nhân
 */
package gui.admin;

import bus.TruongKhoaBUS;
import config.Constants;
import dto.*;
import gui.components.*;
import gui.login.LoginFrame;
import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends BaseDashboardFrame {
    private GiangVienDTO nguoiDung;
    private TruongKhoaBUS truongKhoaBUS;
    
    public AdminDashboard(GiangVienDTO nguoiDung) {
        super("EXAM MANAGEMENT - Hệ thống quản lý thi trắc nghiệm");
        this.nguoiDung = nguoiDung;
        this.truongKhoaBUS = new TruongKhoaBUS();
        initUI();
    }
    
    @Override
    protected String getUserName() {
        return nguoiDung.getHo() + " " + nguoiDung.getTen();
    }
    
    @Override
    protected String getRoleName() {
        return "Trưởng Khoa";
    }
    
    @Override
    protected void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    @Override
    protected void initSidebarItems(JPanel sidebar) {
        // Nhóm: HỆ THỐNG
        addMenuGroup(sidebar, "HỆ THỐNG");
        JButton btnTongQuan = addMenuItem(sidebar, "📊", "Tổng Quan", "TONG_QUAN");
        
        // Nhóm: QUẢN LÝ ĐÀO TẠO
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "QUẢN LÝ ĐÀO TẠO");
        addMenuItem(sidebar, "📖", "Học Phần", "HOC_PHAN");
        addMenuItem(sidebar, "🏛️", "Khoa", "KHOA");
        addMenuItem(sidebar, "🎓", "Ngành", "NGANH");
        addMenuItem(sidebar, "📅", "Kỳ Thi", "KY_THI");
        
        // Nhóm: QUẢN LÝ NHÂN SỰ
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "QUẢN LÝ NHÂN SỰ");
        addMenuItem(sidebar, "👨‍🏫", "Giảng Viên", "GIANG_VIEN");
        addMenuItem(sidebar, "👨‍🎓", "Sinh Viên", "SINH_VIEN");
        
        // Nhóm: CÁ NHÂN
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "CÁ NHÂN");
        addMenuItem(sidebar, "👤", "Thông tin cá nhân", "THONG_TIN");
        
        setActiveButton(btnTongQuan);
    }
    
    @Override
    protected void initContentPanels() {
        panelNoiDung.add(createTongQuanPanel(), "TONG_QUAN");
        panelNoiDung.add(new QuanLyHocPhanPanel(), "HOC_PHAN");
        panelNoiDung.add(new QuanLyKhoaPanel(), "KHOA");
        panelNoiDung.add(new QuanLyNganhPanel(), "NGANH");
        panelNoiDung.add(new QuanLyKyThiPanel(), "KY_THI");
        panelNoiDung.add(new QuanLyGiangVienPanel(), "GIANG_VIEN");
        panelNoiDung.add(new QuanLySinhVienPanel(), "SINH_VIEN");
        panelNoiDung.add(createThongTinPanel(), "THONG_TIN");
    }
    
    private JPanel createTongQuanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel lblTitle = new JLabel("📊 TỔNG QUAN HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(Constants.HEADER_FONT);
        lblTitle.setForeground(Constants.PRIMARY_COLOR);
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Cards thống kê
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Load số liệu thực tế
        int soGiangVien = truongKhoaBUS.getDanhSachGiangVien() != null ? 
                          truongKhoaBUS.getDanhSachGiangVien().size() : 0;
        int soSinhVien = truongKhoaBUS.getDanhSachSinhVien() != null ? 
                         truongKhoaBUS.getDanhSachSinhVien().size() : 0;
        int soHocPhan = truongKhoaBUS.getDanhSachHocPhan() != null ? 
                        truongKhoaBUS.getDanhSachHocPhan().size() : 0;
        int soKhoa = truongKhoaBUS.getDanhSachKhoa() != null ? 
                     truongKhoaBUS.getDanhSachKhoa().size() : 0;
        int soNganh = truongKhoaBUS.getDanhSachNganh() != null ? 
                      truongKhoaBUS.getDanhSachNganh().size() : 0;
        int soKyThi = truongKhoaBUS.getDanhSachKyThi() != null ? 
                      truongKhoaBUS.getDanhSachKyThi().size() : 0;
        
        cardsPanel.add(createStatCard("👨‍🏫", "Giảng Viên", String.valueOf(soGiangVien), Constants.SECONDARY_COLOR));
        cardsPanel.add(createStatCard("👨‍🎓", "Sinh Viên", String.valueOf(soSinhVien), Constants.SUCCESS_COLOR));
        cardsPanel.add(createStatCard("📖", "Học Phần", String.valueOf(soHocPhan), Constants.PURPLE_COLOR));
        cardsPanel.add(createStatCard("🏛️", "Khoa", String.valueOf(soKhoa), Constants.ORANGE_COLOR));
        cardsPanel.add(createStatCard("🎓", "Ngành", String.valueOf(soNganh), Constants.PRIMARY_COLOR));
        cardsPanel.add(createStatCard("📅", "Kỳ Thi", String.valueOf(soKyThi), Constants.DANGER_COLOR));
        
        panel.add(cardsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createThongTinPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE, 
                                                         Constants.PADDING_LARGE, Constants.PADDING_LARGE));
        
        // Tiêu đề - sử dụng HeaderLabel
        panel.add(HeaderLabel.createWithIcon("👤", "THÔNG TIN CÁ NHÂN"), BorderLayout.NORTH);
        
        // Panel thông tin - sử dụng InfoDisplayPanel
        String[][] info = {
            {"Mã giảng viên:", String.valueOf(nguoiDung.getMaGV())},
            {"Họ:", nguoiDung.getHo()},
            {"Tên:", nguoiDung.getTen()},
            {"Email:", nguoiDung.getEmail() != null ? nguoiDung.getEmail() : ""},
            {"Khoa:", nguoiDung.getTenKhoa() != null ? nguoiDung.getTenKhoa() : ""},
            {"Vai trò:", "Trưởng khoa"}
        };
        
        panel.add(InfoDisplayPanel.createWrapper(new InfoDisplayPanel(info)), BorderLayout.CENTER);
        
        // Nút đổi mật khẩu
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNut.setBackground(CONTENT_BG);
        
        CustomButton btnDoiMK = new CustomButton("🔑  Đổi mật khẩu", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnDoiMK.addActionListener(e -> doiMatKhau());
        panelNut.add(btnDoiMK);
        
        panel.add(panelNut, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void doiMatKhau() {
        ChangePasswordDialog.show(this, (oldPwd, newPwd) -> 
            truongKhoaBUS.doiMatKhauGiangVien(nguoiDung.getMaGV(), oldPwd, newPwd)
        );
    }
}
