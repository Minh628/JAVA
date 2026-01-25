/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: ThongTinGiangVienPanel - Panel thông tin cá nhân giảng viên (sử dụng components)
 */
package gui.teacher;

import bus.GiangVienBUS;
import config.Constants;
import dto.GiangVienDTO;
import gui.components.*;
import java.awt.*;
import javax.swing.*;

public class ThongTinGiangVienPanel extends JPanel {
    private GiangVienDTO nguoiDung;
    private GiangVienBUS giangVienBUS;

    public ThongTinGiangVienPanel(GiangVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.giangVienBUS = new GiangVienBUS();
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
            {"Mã giảng viên:", String.valueOf(nguoiDung.getMaGV())},
            {"Họ:", nguoiDung.getHo()},
            {"Tên:", nguoiDung.getTen()},
            {"Email:", nguoiDung.getEmail() != null ? nguoiDung.getEmail() : ""},
            {"Khoa:", nguoiDung.getTenKhoa() != null ? nguoiDung.getTenKhoa() : ""},
            {"Vai trò:", nguoiDung.getTenVaiTro() != null ? nguoiDung.getTenVaiTro() : "Giảng viên"}
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
            giangVienBUS.doiMatKhau(nguoiDung.getMaGV(), oldPwd, newPwd)
        );
    }
}
