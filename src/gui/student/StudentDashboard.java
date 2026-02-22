/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: StudentDashboard - Giao diện chính cho Sinh viên
 * 
 * MÔ TẢ:
 *   - Dashboard chính của Sinh viên sau khi đăng nhập
 *   - Sử dụng layout: Header + Sidebar + Content Area
 * 
 * CÁC MENU:
 *   - Thi trắc nghiệm: ThiTracNghiemPanel - Chọn đề thi và bắt đầu làm bài
 *   - Lịch sử thi: LichSuThiPanel - Xem kết quả các bài thi đã làm
 *   - Thông tin cá nhân: ThongTinSinhVienPanel
 * 
 * KẾ THỮA:
 *   - BaseDashboardFrame: Layout chung, sidebar, header
 * 
 * LÔGIC ĐẶC BIỆT:
 *   - Khi làm bài xong (LamBaiThiFrame), cần refresh danh sách lịch sử thi
 *   - ThiTracNghiemPanel và LichSuThiPanel được giữ tham chiếu để refresh
 * 
 * @see BaseDashboardFrame - Khung dashboard chung
 * @see ThiTracNghiemPanel - Panel thi trắc nghiệm
 * @see LichSuThiPanel - Panel lịch sử thi
 * @see LamBaiThiFrame - Màn hình làm bài thi
 * ===========================================================================
 */
package gui.student;

import bus.SinhVienBUS;
import dto.SinhVienDTO;
import gui.components.BaseDashboardFrame;
import gui.login.LoginFrame;
import javax.swing.*;

public class StudentDashboard extends BaseDashboardFrame {
    private SinhVienDTO nguoiDung;
    private SinhVienBUS sinhVienBUS;
    
    private ThiTracNghiemPanel thiTracNghiemPanel;
    private LichSuThiPanel lichSuThiPanel;

    public StudentDashboard(SinhVienDTO nguoiDung) {
        super("EXAM MANAGEMENT - Sinh viên");
        this.nguoiDung = nguoiDung;
        this.sinhVienBUS = new SinhVienBUS();
        initUI();
    }

    @Override
    protected String getUserName() {
        return nguoiDung.getHo() + " " + nguoiDung.getTen();
    }

    @Override
    protected String getRoleName() {
        return "Sinh viên";
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
        // Nhóm: THI CỬ
        addMenuGroup(sidebar, "THI CỬ");
        JButton btnThi = addMenuItem(sidebar, "📝", "Thi trắc nghiệm", "THI");
        addMenuItem(sidebar, "📜", "Lịch sử thi", "LICH_SU");
        
        // Nhóm: CÁ NHÂN
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "CÁ NHÂN");
        addMenuItem(sidebar, "👤", "Thông tin cá nhân", "THONG_TIN");
        
        setActiveButton(btnThi);
    }

    @Override
    protected void initContentPanels() {
        // Sử dụng các Panel riêng biệt
        thiTracNghiemPanel = new ThiTracNghiemPanel(nguoiDung, this);
        lichSuThiPanel = new LichSuThiPanel(nguoiDung);
        
        panelNoiDung.add(thiTracNghiemPanel, "THI");
        panelNoiDung.add(lichSuThiPanel, "LICH_SU");
        panelNoiDung.add(new ThongTinSinhVienPanel(nguoiDung), "THONG_TIN");
    }
    
    // Override để load lịch sử khi chuyển panel
    @Override
    protected void showCard(String cardName) {
        super.showCard(cardName);
        if ("LICH_SU".equals(cardName)) {
            lichSuThiPanel.loadData();
        }
    }
    
    // Gọi khi hoàn thành thi
    public void hoanThanhThi() {
        this.setVisible(true);
        thiTracNghiemPanel.loadDeThi();
        lichSuThiPanel.loadData();
    }
}
