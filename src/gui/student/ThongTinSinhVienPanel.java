/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: ThongTinSinhVienPanel - Panel thông tin cá nhân sinh viên (sử dụng components)
 */
package gui.student;

import bus.SinhVienBUS;
import config.Constants;
import dto.SinhVienDTO;
import gui.components.*;
import java.awt.*;
import javax.swing.*;

public class ThongTinSinhVienPanel extends JPanel {
    private SinhVienDTO nguoiDung;
    private SinhVienBUS sinhVienBUS;

    public ThongTinSinhVienPanel(SinhVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.sinhVienBUS = new SinhVienBUS();
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
            {"Ngành:", nguoiDung.getTenNganh() != null ? nguoiDung.getTenNganh() : ""}
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
}
