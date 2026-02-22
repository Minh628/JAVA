/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: TeacherDashboard - Giao diện chính cho Giảng viên
 * 
 * MÔ TẢ:
 *   - Dashboard chính của Giảng viên sau khi đăng nhập
 *   - Sử dụng layout: Header + Sidebar + Content Area
 * 
 * CÁC MENU:
 *   - Soạn câu hỏi: SoanCauHoiPanel - Tạo/sửa/xóa câu hỏi trắc nghiệm
 *   - Quản lý đề thi: QuanLyDeThiPanel - Tạo/quản lý đề thi
 *   - Xem điểm SV: XemDiemSinhVienPanel - Xem kết quả thi của SV
 *   - Thông tin cá nhân: ThongTinGiangVienPanel
 * 
 * KẾ THỮA:
 *   - BaseDashboardFrame: Layout chung, sidebar, header
 * 
 * REFACTORED:
 *   - Tách riêng các Panel
 *   - Giống cấu trúc AdminDashboard
 * 
 * @see BaseDashboardFrame - Khung dashboard chung
 * @see SoanCauHoiPanel - Soạn câu hỏi
 * @see QuanLyDeThiPanel - Quản lý đề thi
 * ===========================================================================
 */
package gui.teacher;

import dto.GiangVienDTO;
import gui.components.BaseDashboardFrame;
import gui.login.LoginFrame;
import javax.swing.*;

public class TeacherDashboard extends BaseDashboardFrame {
    private GiangVienDTO nguoiDung;

    public TeacherDashboard(GiangVienDTO nguoiDung) {
        super("EXAM MANAGEMENT - Giảng viên");
        this.nguoiDung = nguoiDung;
        initUI();
    }

    @Override
    protected String getUserName() {
        return nguoiDung.getHo() + " " + nguoiDung.getTen();
    }

    @Override
    protected String getRoleName() {
        return "Giảng viên";
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
        // Nhóm: QUẢN LÝ
        addMenuGroup(sidebar, "QUẢN LÝ");
        JButton btnCauHoi = addMenuItem(sidebar, "❓", "Quản lý Câu hỏi", "CAU_HOI");
        addMenuItem(sidebar, "📝", "Quản lý Đề thi", "DE_THI");
        addMenuItem(sidebar, "📊", "Xem điểm SV", "XEM_DIEM");
        
        // Nhóm: CÁ NHÂN
        sidebar.add(Box.createVerticalStrut(15));
        addMenuGroup(sidebar, "CÁ NHÂN");
        addMenuItem(sidebar, "👤", "Thông tin cá nhân", "THONG_TIN");
        
        setActiveButton(btnCauHoi);
    }

    @Override
    protected void initContentPanels() {
        // Sử dụng các Panel riêng biệt
        panelNoiDung.add(new SoanCauHoiPanel(nguoiDung), "CAU_HOI");
        panelNoiDung.add(new QuanLyDeThiPanel(nguoiDung), "DE_THI");
        panelNoiDung.add(new XemDiemSinhVienPanel(nguoiDung), "XEM_DIEM");
        panelNoiDung.add(new ThongTinGiangVienPanel(nguoiDung), "THONG_TIN");
    }
}
