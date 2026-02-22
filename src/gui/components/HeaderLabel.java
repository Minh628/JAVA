/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: HeaderLabel - Tiêu đề panel dùng chung
 * 
 * MÔ TẢ:
 *   - Label tiêu đề với font lớn và màu sắc đẹp
 *   - Tự động căn giữa (CENTER)
 *   - Sử dụng font HEADER_FONT từ Constants
 * 
 * CÁCH SỬ DỤNG:
 *   // Cách 1: Factory method (KHUYẾN KHÍCH)
 *   HeaderLabel title = HeaderLabel.createPrimary("📊 THỐNG KÊ");
 *   HeaderLabel darkTitle = HeaderLabel.createDark("Danh sách");
 *   HeaderLabel withIcon = HeaderLabel.createWithIcon("📚", "QUẢN LÝ HỌC PHẦN");
 *   
 *   // Cách 2: Constructor trực tiếp
 *   HeaderLabel custom = new HeaderLabel("Tiêu đề", Constants.SUCCESS_COLOR);
 * 
 * @see config.Constants#HEADER_FONT - Font tiêu đề
 * @see config.Constants#PRIMARY_COLOR - Màu chính
 * ===========================================================================
 */
package gui.components;

import config.Constants;
import java.awt.*;
import javax.swing.*;

public class HeaderLabel extends JLabel {
    
    public HeaderLabel(String text) {
        this(text, Constants.PRIMARY_COLOR);
    }
    
    public HeaderLabel(String text, Color foregroundColor) {
        super(text, SwingConstants.CENTER);
        setFont(Constants.HEADER_FONT);
        setForeground(foregroundColor);
    }
    
    // Factory methods cho các loại header phổ biến
    public static HeaderLabel createPrimary(String text) {
        return new HeaderLabel(text, Constants.PRIMARY_COLOR);
    }
    
    public static HeaderLabel createDark(String text) {
        return new HeaderLabel(text, Constants.TEXT_COLOR);
    }
    
    public static HeaderLabel createWithIcon(String icon, String text) {
        return new HeaderLabel(icon + " " + text, Constants.PRIMARY_COLOR);
    }
}
