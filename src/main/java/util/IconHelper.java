/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Util: IconHelper - Tiện ích tạo Icon từ Ikonli
 * 
 * MÔ TẢ:
 *   - Sử dụng thư viện Ikonli (FontAwesome) để tạo các icon
 *   - Cung cấp các phương thức tiện ích để tạo icon với màu và kích thước
 *   - Thay thế việc sử dụng emoji trực tiếp trong GUI
 * 
 * THƯ VIỆN:
 *   - ikonli-core-12.4.0.jar
 *   - ikonli-swing-12.4.0.jar
 *   - ikonli-fontawesome-pack-12.4.0.jar
 * 
 * SỬ DỤNG:
 *   Icon icon = IconHelper.createIcon(FontAwesome.USER, 20, Color.WHITE);
 *   JLabel label = IconHelper.createIconLabel(FontAwesome.BOOK, 24, Color.BLUE);
 * 
 * @see config.Constants - Các hằng số icon
 * ===========================================================================
 */
package util;

import config.Constants;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JLabel;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.swing.FontIcon;

public class IconHelper {
    
    // Kích thước icon mặc định
    public static final int ICON_SIZE_SMALL = 14;
    public static final int ICON_SIZE_NORMAL = 18;
    public static final int ICON_SIZE_LARGE = 24;
    public static final int ICON_SIZE_XLARGE = 32;
    public static final int ICON_SIZE_STAT_CARD = 40;
    public static final int ICON_SIZE_HEADER = 28;
    public static final int ICON_SIZE_LOGO = 32;
    
    /**
     * Tạo Icon từ Ikonli với màu và kích thước
     * @param ikon Icon code từ FontAwesome
     * @param size Kích thước icon
     * @param color Màu icon
     * @return Icon object
     */
    public static Icon createIcon(Ikon ikon, int size, Color color) {
        FontIcon icon = FontIcon.of(ikon, size);
        icon.setIconColor(color);
        return icon;
    }

    /**
     * Tạo Image từ Ikonli để dùng làm icon cửa sổ
     */
    public static java.awt.Image createIconImage(Ikon ikon, int size, Color color) {
        FontIcon icon = FontIcon.of(ikon, size);
        icon.setIconColor(color);

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        return image;
    }
    
    /**
     * Tạo Icon với kích thước mặc định
     */
    public static Icon createIcon(Ikon ikon, Color color) {
        return createIcon(ikon, ICON_SIZE_NORMAL, color);
    }
    
    /**
     * Tạo Icon với màu mặc định (TEXT_COLOR)
     */
    public static Icon createIcon(Ikon ikon, int size) {
        return createIcon(ikon, size, Constants.TEXT_COLOR);
    }
    
    /**
     * Tạo JLabel chứa icon
     */
    public static JLabel createIconLabel(Ikon ikon, int size, Color color) {
        JLabel label = new JLabel();
        label.setIcon(createIcon(ikon, size, color));
        return label;
    }
    
    /**
     * Tạo JLabel với icon và text
     */
    public static JLabel createIconLabel(Ikon ikon, String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setIcon(createIcon(ikon, size, color));
        label.setIconTextGap(8);
        return label;
    }
    
    // =====================================================
    // CÁC ICON THƯỜNG DÙNG - Dashboard
    // =====================================================
    
    /** Icon Dashboard/Tổng quan */
    public static Icon getDashboardIcon(int size, Color color) {
        return createIcon(FontAwesome.DASHBOARD, size, color);
    }
    
    /** Icon Thống kê/Biểu đồ */
    public static Icon getChartIcon(int size, Color color) {
        return createIcon(FontAwesome.BAR_CHART, size, color);
    }
    
    /** Icon Sách/Học phần */
    public static Icon getBookIcon(int size, Color color) {
        return createIcon(FontAwesome.BOOK, size, color);
    }
    
    /** Icon Khoa/Tòa nhà */
    public static Icon getBuildingIcon(int size, Color color) {
        return createIcon(FontAwesome.UNIVERSITY, size, color);
    }
    
    /** Icon Ngành/Tốt nghiệp */
    public static Icon getGraduationIcon(int size, Color color) {
        return createIcon(FontAwesome.GRADUATION_CAP, size, color);
    }
    
    /** Icon Lịch/Kỳ thi */
    public static Icon getCalendarIcon(int size, Color color) {
        return createIcon(FontAwesome.CALENDAR, size, color);
    }
    
    /** Icon Giảng viên/Người dạy */
    public static Icon getTeacherIcon(int size, Color color) {
        return createIcon(FontAwesome.USERS, size, color);
    }
    
    /** Icon Sinh viên/Học sinh */
    public static Icon getStudentIcon(int size, Color color) {
        return createIcon(FontAwesome.USER, size, color);
    }
    
    /** Icon Người dùng/Profile */
    public static Icon getUserIcon(int size, Color color) {
        return createIcon(FontAwesome.USER_CIRCLE, size, color);
    }
    
    /** Icon Câu hỏi */
    public static Icon getQuestionIcon(int size, Color color) {
        return createIcon(FontAwesome.QUESTION_CIRCLE, size, color);
    }
    
    /** Icon Đề thi/Tài liệu */
    public static Icon getFileTextIcon(int size, Color color) {
        return createIcon(FontAwesome.FILE_TEXT, size, color);
    }
    
    /** Icon Lịch sử */
    public static Icon getHistoryIcon(int size, Color color) {
        return createIcon(FontAwesome.HISTORY, size, color);
    }
    
    /** Icon Tìm kiếm */
    public static Icon getSearchIcon(int size, Color color) {
        return createIcon(FontAwesome.SEARCH, size, color);
    }
    
    /** Icon Làm mới */
    public static Icon getRefreshIcon(int size, Color color) {
        return createIcon(FontAwesome.REFRESH, size, color);
    }
    
    /** Icon Thêm mới */
    public static Icon getPlusIcon(int size, Color color) {
        return createIcon(FontAwesome.PLUS, size, color);
    }
    
    /** Icon Sửa */
    public static Icon getEditIcon(int size, Color color) {
        return createIcon(FontAwesome.EDIT, size, color);
    }
    
    /** Icon Xóa */
    public static Icon getTrashIcon(int size, Color color) {
        return createIcon(FontAwesome.TRASH, size, color);
    }
    
    /** Icon Lưu */
    public static Icon getSaveIcon(int size, Color color) {
        return createIcon(FontAwesome.SAVE, size, color);
    }
    
    /** Icon Đăng xuất/Thoát */
    public static Icon getSignOutIcon(int size, Color color) {
        return createIcon(FontAwesome.SIGN_OUT, size, color);
    }
    
    /** Icon Danh sách */
    public static Icon getListIcon(int size, Color color) {
        return createIcon(FontAwesome.LIST, size, color);
    }
    
    /** Icon Clipboard/Bài thi */
    public static Icon getClipboardIcon(int size, Color color) {
        return createIcon(FontAwesome.CLIPBOARD, size, color);
    }
    
    /** Icon Logo ứng dụng */
    public static Icon getLogoIcon(int size, Color color) {
        return createIcon(FontAwesome.BOOK, size, color);
    }
    
    /** Icon Điểm số/Số liệu */
    public static Icon getScoreIcon(int size, Color color) {
        return createIcon(FontAwesome.LINE_CHART, size, color);
    }
    
    /** Icon Tổng số */
    public static Icon getTotalIcon(int size, Color color) {
        return createIcon(FontAwesome.CALCULATOR, size, color);
    }
    
    /** Icon Thành công */
    public static Icon getCheckIcon(int size, Color color) {
        return createIcon(FontAwesome.CHECK, size, color);
    }
    
    /** Icon Cảnh báo */
    public static Icon getWarningIcon(int size, Color color) {
        return createIcon(FontAwesome.EXCLAMATION_TRIANGLE, size, color);
    }
    
    /** Icon Lỗi */
    public static Icon getErrorIcon(int size, Color color) {
        return createIcon(FontAwesome.TIMES_CIRCLE, size, color);
    }
    
    /** Icon Thông tin */
    public static Icon getInfoIcon(int size, Color color) {
        return createIcon(FontAwesome.INFO_CIRCLE, size, color);
    }
    
    /** Icon In */
    public static Icon getPrintIcon(int size, Color color) {
        return createIcon(FontAwesome.PRINT, size, color);
    }
    
    /** Icon Export Excel */
    public static Icon getExcelIcon(int size, Color color) {
        return createIcon(FontAwesome.FILE_EXCEL_O, size, color);
    }
    
    /** Icon Import */
    public static Icon getImportIcon(int size, Color color) {
        return createIcon(FontAwesome.UPLOAD, size, color);
    }
    
    /** Icon Export/Tải xuống */
    public static Icon getDownloadIcon(int size, Color color) {
        return createIcon(FontAwesome.DOWNLOAD, size, color);
    }
    
    /** Icon Cài đặt */
    public static Icon getSettingsIcon(int size, Color color) {
        return createIcon(FontAwesome.COG, size, color);
    }
    
    /** Icon Khóa/Mật khẩu */
    public static Icon getLockIcon(int size, Color color) {
        return createIcon(FontAwesome.LOCK, size, color);
    }
}
