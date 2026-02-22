/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: ThongTinGiangVienPanel - Panel thông tin cá nhân Giảng viên
 * 
 * MÔ TẢ:
 *   - Hiển thị thông tin cá nhân của Giảng viên đang đăng nhập
 *   - Cho phép đổi mật khẩu
 * 
 * THÔNG TIN HIỂN THỊ:
 *   - Mã GV, Họ tên, Email
 *   - Khoa, Vai trò
 *   - Tên đăng nhập
 * 
 * COMPONENTS SỬ DỤNG:
 *   - HeaderLabel: Tiêu đề
 *   - InfoDisplayPanel: Hiển thị thông tin
 *   - CustomButton: Nút đổi mật khẩu
 *   - ChangePasswordDialog: Dialog đổi mật khẩu
 * 
 * @see GiangVienBUS - Business logic giảng viên
 * @see ChangePasswordDialog - Dialog đổi mật khẩu
 * ===========================================================================
 */
package gui.teacher;

import bus.GiangVienBUS;
import bus.KhoaBUS;
import bus.VaiTroBUS;
import config.Constants;
import dto.GiangVienDTO;
import dto.KhoaDTO;
import dto.VaiTroDTO;
import gui.components.*;
import java.awt.*;
import javax.swing.*;

public class ThongTinGiangVienPanel extends JPanel {
    private GiangVienDTO nguoiDung;
    private GiangVienBUS giangVienBUS;
    private KhoaBUS khoaBUS;
    private VaiTroBUS vaiTroBUS;

    public ThongTinGiangVienPanel(GiangVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.giangVienBUS = new GiangVienBUS();
        this.khoaBUS = new KhoaBUS();
        this.vaiTroBUS = new VaiTroBUS();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE, 
                                                   Constants.PADDING_LARGE, Constants.PADDING_LARGE));
        
        // Tiêu đề - sử dụng HeaderLabel
        add(HeaderLabel.createWithIcon("👤", "THÔNG TIN CÁ NHÂN"), BorderLayout.NORTH);
        
        // Lấy tên khoa và vai trò
        String tenKhoa = "";
        String tenVaiTro = "Giảng viên";
        KhoaDTO khoa = khoaBUS.getById(nguoiDung.getMaKhoa());
        VaiTroDTO vaiTro = vaiTroBUS.getById(nguoiDung.getMaVaiTro());
        if (khoa != null) tenKhoa = khoa.getTenKhoa();
        if (vaiTro != null) tenVaiTro = vaiTro.getTenVaiTro();
        
        // Panel thông tin - sử dụng InfoDisplayPanel
        String[][] info = {
            {"Mã giảng viên:", String.valueOf(nguoiDung.getMaGV())},
            {"Họ:", nguoiDung.getHo()},
            {"Tên:", nguoiDung.getTen()},
            {"Email:", nguoiDung.getEmail() != null ? nguoiDung.getEmail() : ""},
            {"Khoa:", tenKhoa},
            {"Vai trò:", tenVaiTro}
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
