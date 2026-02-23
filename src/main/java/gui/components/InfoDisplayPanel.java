/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: InfoDisplayPanel - Panel hiển thị thông tin cá nhân
 * 
 * MÔ TẢ:
 *   - Hiển thị thông tin dạng Label: Value (chỉ đọc)
 *   - Sử dụng cho các panel Thông tin cá nhân
 *   - Dữ liệu truyền vào là mảng 2 chiều [[label, value], ...]
 * 
 * CÁCH SỬ DỤNG:
 *   String[][] data = {
 *       {"Họ tên:", "Nguyễn Văn A"},
 *       {"Email:", "a@example.com"},
 *       {"Số điện thoại:", "0123456789"}
 *   };
 *   InfoDisplayPanel panel = new InfoDisplayPanel(data);
 *   
 *   // Hoặc với số cột tùy chỉnh:
 *   InfoDisplayPanel panel = new InfoDisplayPanel(data, 30);
 * 
 * @see ThongTinAdminPanel - Sử dụng InfoDisplayPanel
 * @see ThongTinGiangVienPanel - Sử dụng InfoDisplayPanel
 * @see ThongTinSinhVienPanel - Sử dụng InfoDisplayPanel
 * ===========================================================================
 */
package gui.components;

import config.Constants;
import java.awt.*;
import javax.swing.*;

public class InfoDisplayPanel extends JPanel {
    
    public InfoDisplayPanel(String[][] data) {
        this(data, Constants.TEXT_FIELD_COLUMNS);
    }
    
    public InfoDisplayPanel(String[][] data, int fieldColumns) {
        setLayout(new GridBagLayout());
        setBackground(Constants.CARD_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
            BorderFactory.createEmptyBorder(Constants.PADDING_XLARGE, Constants.PADDING_XLARGE, 
                                            Constants.PADDING_XLARGE, Constants.PADDING_XLARGE)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constants.INSET_MEDIUM, Constants.INSET_MEDIUM, 
                                Constants.INSET_MEDIUM, Constants.INSET_MEDIUM);
        gbc.anchor = GridBagConstraints.WEST;
        
        for (int i = 0; i < data.length; i++) {
            // Label
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel lbl = new JLabel(data[i][0]);
            lbl.setFont(Constants.NORMAL_FONT);
            lbl.setForeground(Constants.TEXT_SECONDARY);
            add(lbl, gbc);
            
            // Value (readonly TextField)
            gbc.gridx = 1;
            JTextField txt = new JTextField(data[i][1], fieldColumns);
            txt.setEditable(false);
            txt.setFont(Constants.NORMAL_FONT);
            add(txt, gbc);
        }
    }
    
    /**
     * Tạo wrapper panel với background và padding phù hợp
     */
    public static JPanel createWrapper(InfoDisplayPanel infoPanel) {
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapperPanel.setBackground(Constants.CONTENT_BG);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(Constants.PADDING_LARGE, 0, 0, 0));
        wrapperPanel.add(infoPanel);
        return wrapperPanel;
    }
}
