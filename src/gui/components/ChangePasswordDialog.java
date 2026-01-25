/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Component: ChangePasswordDialog - Dialog đổi mật khẩu dùng chung
 */
package gui.components;

import config.Constants;
import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog {
    
    public interface PasswordChangeCallback {
        boolean doChangePassword(String oldPassword, String newPassword);
    }
    
    /**
     * Hiển thị dialog đổi mật khẩu
     * @param parent Component cha
     * @param callback Callback để xử lý đổi mật khẩu
     */
    public static void show(Component parent, PasswordChangeCallback callback) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        JPasswordField txtMKCu = new JPasswordField(20);
        JPasswordField txtMKMoi = new JPasswordField(20);
        JPasswordField txtXacNhan = new JPasswordField(20);
        
        txtMKCu.setFont(Constants.NORMAL_FONT);
        txtMKMoi.setFont(Constants.NORMAL_FONT);
        txtXacNhan.setFont(Constants.NORMAL_FONT);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lbl1 = new JLabel("Mật khẩu cũ:");
        lbl1.setFont(Constants.NORMAL_FONT);
        panel.add(lbl1, gbc);
        gbc.gridx = 1;
        panel.add(txtMKCu, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lbl2 = new JLabel("Mật khẩu mới:");
        lbl2.setFont(Constants.NORMAL_FONT);
        panel.add(lbl2, gbc);
        gbc.gridx = 1;
        panel.add(txtMKMoi, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lbl3 = new JLabel("Xác nhận mật khẩu:");
        lbl3.setFont(Constants.NORMAL_FONT);
        panel.add(lbl3, gbc);
        gbc.gridx = 1;
        panel.add(txtXacNhan, gbc);
        
        int result = JOptionPane.showConfirmDialog(parent, panel, "Đổi mật khẩu", 
                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String mkCu = new String(txtMKCu.getPassword());
            String mkMoi = new String(txtMKMoi.getPassword());
            String xacNhan = new String(txtXacNhan.getPassword());
            
            // Validate
            if (mkCu.isEmpty() || mkMoi.isEmpty() || xacNhan.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Vui lòng điền đầy đủ thông tin!", 
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!mkMoi.equals(xacNhan)) {
                JOptionPane.showMessageDialog(parent, "Mật khẩu mới không khớp!", 
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (mkMoi.length() < 6) {
                JOptionPane.showMessageDialog(parent, "Mật khẩu mới phải có ít nhất 6 ký tự!", 
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Gọi callback để đổi mật khẩu
            if (callback.doChangePassword(mkCu, mkMoi)) {
                JOptionPane.showMessageDialog(parent, "Đổi mật khẩu thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, "Đổi mật khẩu thất bại! Mật khẩu cũ không đúng.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
