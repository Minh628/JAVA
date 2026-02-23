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
 *   HeaderLabel title = HeaderLabel.createPrimary("THỐNG KÊ");
 *   HeaderLabel darkTitle = HeaderLabel.createDark("Danh sách");
 *   HeaderLabel withIcon = HeaderLabel.createWithIcon(Constants.ICON_BOOK, "QUẢN LÝ HỌC PHẦN");
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
import org.kordamp.ikonli.Ikon;
import util.IconHelper;

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
    
    /**
     * Tạo HeaderLabel với icon từ Ikonli
     * @param icon Icon code từ FontAwesome (VD: Constants.ICON_BOOK)
     * @param text Nội dung text
     * @return HeaderLabel với icon và text
     */
    public static HeaderLabel createWithIcon(Ikon icon, String text) {
        HeaderLabel label = new HeaderLabel(text, Constants.PRIMARY_COLOR);
        label.setIcon(IconHelper.createIcon(icon, Constants.ICON_SIZE_HEADER, Constants.PRIMARY_COLOR));
        label.setIconTextGap(10);
        return label;
    }
    
    /**
     * Tạo HeaderLabel với icon và màu tùy chỉnh
     */
    public static HeaderLabel createWithIcon(Ikon icon, String text, Color color) {
        HeaderLabel label = new HeaderLabel(text, color);
        label.setIcon(IconHelper.createIcon(icon, Constants.ICON_SIZE_HEADER, color));
        label.setIconTextGap(10);
        return label;
    }
}
