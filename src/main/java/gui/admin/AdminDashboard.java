/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: AdminDashboard - Giao diện chính cho Admin
 * 
 * MÔ TẢ:
 *   - Dashboard chính của Admin sau khi đăng nhập
 *   - Sử dụng layout: Header + Sidebar + Content Area
 *   - Chuyển đổi giữa các panel bằng CardLayout
 * 
 * CÁC MENU:
 *   HỆ THỐNG:
 *     - Tổng Quan: Hiển thị thống kê nhanh (số GV, SV, HP, Khoa, Ngành, Kỳ thi)
 *     - Thống Kê: ThongKePanel - Thống kê kết quả thi
 *   
 *   QUẢN LÝ ĐÀO TẠO:
 *     - Học Phần: QuanLyHocPhanPanel
 *     - Khoa: QuanLyKhoaPanel
 *     - Ngành: QuanLyNganhPanel
 *     - Kỳ Thi: QuanLyKyThiPanel
 *   
 *   QUẢN LÝ NHÂN SỰ:
 *     - Giảng Viên: QuanLyGiangVienPanel
 *     - Sinh Viên: QuanLySinhVienPanel
 *   
 *   CÁ NHÂN:
 *     - Thông tin cá nhân: ThongTinAdminPanel
 * 
 * KẾ THỮA:
 *   - BaseDashboardFrame: Layout chung, sidebar, header
 * 
 * REFACTORED:
 *   - Tách Khoa và Ngành ra 2 panel riêng
 *   - Thêm panel Thông tin cá nhân
 *   - Sử dụng BaseDashboardFrame
 * 
 * @see BaseDashboardFrame - Khung dashboard chung
 * @see ThongKePanel - Panel thống kê
 * ===========================================================================
 */
package gui.admin;

import bus.*;
import config.Constants;
import dto.*;
import gui.components.*;
import gui.login.LoginFrame;
import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends BaseDashboardFrame {
    private GiangVienDTO nguoiDung;
    private KhoaBUS khoaBUS;
    private NganhBUS nganhBUS;
    private HocPhanBUS hocPhanBUS;
    private KyThiBUS kyThiBUS;
    private GiangVienBUS giangVienBUS;
    private SinhVienBUS sinhVienBUS;
    
    public AdminDashboard(GiangVienDTO nguoiDung) {
        super("EXAM MANAGEMENT - Hệ thống quản lý thi trắc nghiệm");
        this.nguoiDung = nguoiDung;
        this.khoaBUS = new KhoaBUS();
        this.nganhBUS = new NganhBUS();
        this.hocPhanBUS = new HocPhanBUS();
        this.kyThiBUS = new KyThiBUS();
        this.giangVienBUS = new GiangVienBUS();
        this.sinhVienBUS = new SinhVienBUS();
        initUI();     
    }
    
    @Override
    protected String getUserName() {
        return nguoiDung.getHo() + " " + nguoiDung.getTen();
    }
    
    @Override
    protected String getRoleName() {
        return "ADMIN";
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
        JButton btnTongQuan = addMenuItem(sidebar, Constants.ICON_DASHBOARD, "Tổng Quan", "TONG_QUAN");
        addMenuItem(sidebar, Constants.ICON_CHART, "Thống Kê", "THONG_KE");
        
        // Nhóm: QUẢN LÝ ĐÀO TẠO
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "QUẢN LÝ ĐÀO TẠO");
        addMenuItem(sidebar, Constants.ICON_BOOK, "Học Phần", "HOC_PHAN");
        addMenuItem(sidebar, Constants.ICON_UNIVERSITY, "Khoa", "KHOA");
        addMenuItem(sidebar, Constants.ICON_GRADUATION, "Ngành", "NGANH");
        addMenuItem(sidebar, Constants.ICON_CALENDAR, "Kỳ Thi", "KY_THI");
        
        // Nhóm: QUẢN LÝ NHÂN SỰ
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "QUẢN LÝ NHÂN SỰ");
        addMenuItem(sidebar, Constants.ICON_USERS, "Giảng Viên", "GIANG_VIEN");
        addMenuItem(sidebar, Constants.ICON_USER, "Sinh Viên", "SINH_VIEN");
        
        // Nhóm: CÁ NHÂN
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "CÁ NHÂN");
        addMenuItem(sidebar, Constants.ICON_USER_CIRCLE, "Thông tin cá nhân", "THONG_TIN");
        
        setActiveButton(btnTongQuan);
    }
    
    @Override
    protected void initContentPanels() {
        panelNoiDung.add(createTongQuanPanel(), "TONG_QUAN");
        panelNoiDung.add(new ThongKePanel(), "THONG_KE");
        panelNoiDung.add(new QuanLyHocPhanPanel(), "HOC_PHAN");
        panelNoiDung.add(new QuanLyKhoaPanel(), "KHOA");
        panelNoiDung.add(new QuanLyNganhPanel(), "NGANH");
        panelNoiDung.add(new QuanLyKyThiPanel(), "KY_THI");
        panelNoiDung.add(new QuanLyGiangVienPanel(), "GIANG_VIEN");
        panelNoiDung.add(new QuanLySinhVienPanel(), "SINH_VIEN");
        panelNoiDung.add(new ThongTinAdminPanel(nguoiDung), "THONG_TIN");
    }
    
    private JPanel createTongQuanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        HeaderLabel lblTitle = HeaderLabel.createWithIcon(Constants.ICON_DASHBOARD, "TỔNG QUAN HỆ THỐNG");
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Cards thống kê
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Load số liệu thực tế
        int soGiangVien = giangVienBUS.getDanhSachGiangVien() != null ? 
                          giangVienBUS.getDanhSachGiangVien().size() : 0;
        int soSinhVien = sinhVienBUS.getDanhSachSinhVien() != null ? 
                         sinhVienBUS.getDanhSachSinhVien().size() : 0;
        int soHocPhan = hocPhanBUS.getDanhSachHocPhan() != null ? 
                        hocPhanBUS.getDanhSachHocPhan().size() : 0;
        int soKhoa = khoaBUS.getDanhSachKhoa() != null ? 
                     khoaBUS.getDanhSachKhoa().size() : 0;
        int soNganh = nganhBUS.getDanhSachNganh() != null ? 
                      nganhBUS.getDanhSachNganh().size() : 0;
        int soKyThi = kyThiBUS.getDanhSachKyThi() != null ? 
                      kyThiBUS.getDanhSachKyThi().size() : 0;
        
        cardsPanel.add(createStatCard(Constants.ICON_USERS, "Giảng Viên", String.valueOf(soGiangVien), Constants.SECONDARY_COLOR));
        cardsPanel.add(createStatCard(Constants.ICON_USER, "Sinh Viên", String.valueOf(soSinhVien), Constants.SUCCESS_COLOR));
        cardsPanel.add(createStatCard(Constants.ICON_BOOK, "Học Phần", String.valueOf(soHocPhan), Constants.PURPLE_COLOR));
        cardsPanel.add(createStatCard(Constants.ICON_UNIVERSITY, "Khoa", String.valueOf(soKhoa), Constants.ORANGE_COLOR));
        cardsPanel.add(createStatCard(Constants.ICON_GRADUATION, "Ngành", String.valueOf(soNganh), Constants.PRIMARY_COLOR));
        cardsPanel.add(createStatCard(Constants.ICON_CALENDAR, "Kỳ Thi", String.valueOf(soKyThi), Constants.DANGER_COLOR));
        
        panel.add(cardsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    
}
