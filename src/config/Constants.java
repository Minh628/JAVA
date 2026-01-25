/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Config: Constants - Các hằng số hệ thống
 */
package config;

import java.awt.Color;
import java.awt.Font;

public class Constants {
    // Màu sắc giao diện - Modern Blue Theme
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Xanh dương chính
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Xanh dương nhạt
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);  // Xám nhạt sáng
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Xanh lá tươi
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // Đỏ
    public static final Color WARNING_COLOR = new Color(241, 196, 15);      // Vàng
    public static final Color DARK_COLOR = new Color(44, 62, 80);           // Xanh đen
    public static final Color LIGHT_COLOR = new Color(236, 240, 241);       // Xám sáng
    public static final Color CARD_COLOR = Color.WHITE;                      // Màu card
    public static final Color TEXT_COLOR = new Color(50, 50, 50);           // Màu chữ chính
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);    // Màu chữ phụ
    
    // Màu sắc Dashboard
    public static final Color HEADER_BG = new Color(25, 55, 109);           // Header xanh đậm
    public static final Color SIDEBAR_BG = new Color(34, 45, 65);           // Sidebar xanh đen
    public static final Color SIDEBAR_HOVER = new Color(44, 62, 88);        // Sidebar hover
    public static final Color SIDEBAR_ACTIVE = new Color(52, 152, 219);     // Sidebar active
    public static final Color CONTENT_BG = new Color(245, 247, 250);        // Nền content
    public static final Color TOOLBAR_BTN = new Color(52, 73, 94);          // Nút toolbar
    public static final Color TOOLBAR_HOVER = new Color(41, 128, 185);      // Nút toolbar hover
    public static final Color LOGOUT_BTN = new Color(192, 57, 43);          // Nút đăng xuất
    
    // Màu phụ cho biểu đồ/thống kê
    public static final Color PURPLE_COLOR = new Color(155, 89, 182);       // Tím
    public static final Color ORANGE_COLOR = new Color(230, 126, 34);       // Cam
    public static final Color BORDER_COLOR = new Color(230, 230, 230);      // Viền xám nhạt
    
    // Font chữ - Segoe UI cho Windows
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    
    // Vai trò người dùng
    public static final int VAI_TRO_TRUONG_KHOA = 1;
    public static final int VAI_TRO_GIANG_VIEN = 2;
    public static final int VAI_TRO_SINH_VIEN = 3;
    
    // Mức độ câu hỏi
    public static final String MUC_DO_DE = "De";
    public static final String MUC_DO_TRUNG_BINH = "TrungBinh";
    public static final String MUC_DO_KHO = "Kho";
    
    // Loại câu hỏi
    public static final String LOAI_TRAC_NGHIEM = "MC";
    public static final String LOAI_DIEN_KHUYET = "DK";
    
    // Thời gian mặc định
    public static final int THOI_GIAN_LAM_BAI_MAC_DINH = 45; // phút
    public static final int SO_CAU_HOI_MAC_DINH = 30;
    
    // Điểm số
    public static final float DIEM_TOI_DA = 10.0f;
    public static final float DIEM_DAT = 5.0f;
    
    // Kích thước và Padding tái sử dụng
    public static final int PADDING_SMALL = 10;
    public static final int PADDING_MEDIUM = 15;
    public static final int PADDING_LARGE = 20;
    public static final int PADDING_XLARGE = 25;
    
    // Insets cho GridBagLayout
    public static final int INSET_SMALL = 8;
    public static final int INSET_MEDIUM = 12;
    
    // Độ rộng TextField mặc định
    public static final int TEXT_FIELD_COLUMNS = 25;
    public static final int TEXT_FIELD_COLUMNS_LONG = 40;
    
    // Khoảng cách dọc trong menu
    public static final int MENU_VERTICAL_GAP = 15;
}
