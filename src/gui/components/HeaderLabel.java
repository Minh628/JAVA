/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Component: HeaderLabel - Label tiêu đề dùng chung
 */
package gui.components;

import config.Constants;
import javax.swing.*;
import java.awt.*;

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
