/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: ThongTinSinhVienPanel - Panel thông tin cá nhân Sinh viên
 * 
 * MÔ TẢ:
 *   - Hiển thị thông tin cá nhân của Sinh viên đang đăng nhập
 *   - Cho phép đổi mật khẩu
 * 
 * THÔNG TIN HIỂN THỊ:
 *   - Mã SV, Họ tên, Email
 *   - Ngành học
 *   - Tên đăng nhập
 * 
 * COMPONENTS SỬ DỤNG:
 *   - HeaderLabel: Tiêu đề
 *   - InfoDisplayPanel: Hiển thị thông tin
 *   - CustomButton: Nút đổi mật khẩu
 *   - ChangePasswordDialog: Dialog đổi mật khẩu
 * 
 * @see SinhVienBUS - Business logic sinh viên
 * @see ChangePasswordDialog - Dialog đổi mật khẩu
 * ===========================================================================
 */
package gui.student;

import bus.NganhBUS;
import bus.SinhVienBUS;
import config.Constants;
import dto.NganhDTO;
import dto.SinhVienDTO;
import gui.components.*;
import java.awt.*;
import javax.swing.*;

public class ThongTinSinhVienPanel extends JPanel {
    private SinhVienDTO nguoiDung;
    private SinhVienBUS sinhVienBUS;
    private NganhBUS nganhBUS;

    public ThongTinSinhVienPanel(SinhVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.sinhVienBUS = new SinhVienBUS();
        this.nganhBUS = new NganhBUS();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE, 
                                                   Constants.PADDING_LARGE, Constants.PADDING_LARGE));
        
        // Tiêu đề - sử dụng HeaderLabel
        add(HeaderLabel.createWithIcon("👤", "THÔNG TIN CÁ NHÂN"), BorderLayout.NORTH);
        
        // Panel thông tin - sử dụng InfoDisplayPanel
        String[][] info = {
            {"Mã sinh viên:", String.valueOf(nguoiDung.getMaSV())},
            {"Tên đăng nhập:", nguoiDung.getTenDangNhap()},
            {"Họ:", nguoiDung.getHo()},
            {"Tên:", nguoiDung.getTen()},
            {"Email:", nguoiDung.getEmail() != null ? nguoiDung.getEmail() : ""},
            {"Ngành:", getTenNganh(nguoiDung.getMaNganh())}
        };
        
        add(InfoDisplayPanel.createWrapper(new InfoDisplayPanel(info)), BorderLayout.CENTER);
        
        // Nút đổi mật khẩu
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNut.setBackground(Constants.CONTENT_BG);
        
        CustomButton btnDoiMK = new CustomButton("🔑  Đổi mật khẩu", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnDoiMK.addActionListener(e -> doiMatKhau());
        panelNut.add(btnDoiMK);
        
        add(panelNut, BorderLayout.SOUTH);
    }
    
    private void doiMatKhau() {
        ChangePasswordDialog.show(this, (oldPwd, newPwd) -> 
            sinhVienBUS.doiMatKhau(nguoiDung.getMaSV(), oldPwd, newPwd)
        );
    }

    private String getTenNganh(int maNganh) {
        NganhDTO nganh = nganhBUS.getById(maNganh);
        return nganh != null ? nganh.getTenNganh() : "";
    }
}
