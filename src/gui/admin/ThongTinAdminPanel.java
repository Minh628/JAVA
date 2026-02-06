/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: ThongTinGiangVienPanel - Panel thông tin cá nhân giảng viên (sử dụng components)
 */
package gui.admin;

import bus.GiangVienBUS;
import bus.KhoaBUS;
import config.Constants;
import dto.GiangVienDTO;
import dto.KhoaDTO;
import gui.components.*;
import java.awt.*;
import javax.swing.*;

public class ThongTinAdminPanel extends JPanel{
    private GiangVienDTO nguoiDung;
    private GiangVienBUS giangVienBUS;
    private KhoaBUS khoaBUS;

    public ThongTinAdminPanel(GiangVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.giangVienBUS = new GiangVienBUS();
        this.khoaBUS = new KhoaBUS();
        initComponents();
    }
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE, 
                                                   Constants.PADDING_LARGE, Constants.PADDING_LARGE)); 
        // Tiêu đề - sử dụng HeaderLabel
        add(HeaderLabel.createWithIcon("👤", "THÔNG TIN CÁ NHÂN"), BorderLayout.NORTH);

        
        // Lấy tên khoa
        String tenKhoa = "";
        KhoaDTO khoa = khoaBUS.getById(nguoiDung.getMaKhoa());
        if (khoa != null) tenKhoa = khoa.getTenKhoa();
        
        // Panel thông tin - sử dụng InfoDisplayPanel
        String[][] info = {
            {"Mã giảng viên:", String.valueOf(nguoiDung.getMaGV())},
            {"Họ:", nguoiDung.getHo()},
            {"Tên:", nguoiDung.getTen()},
            {"Email:", nguoiDung.getEmail() != null ? nguoiDung.getEmail() : ""},
            {"Khoa:", tenKhoa},
            {"Vai trò:", "ADMIN"}
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
            giangVienBUS.resetMatKhau(nguoiDung.getMaGV(), newPwd)
        );
    }
}
    