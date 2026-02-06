package gui.components;

import config.Constants;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class BaseDashboardFrame extends JFrame {
    protected JPanel panelNoiDung;
    protected CardLayout cardLayout;
    protected JPanel sidebarPanel;
    protected JButton btnActive = null;
    
    // Màu sắc theme lấy từ Constants
    protected static final Color HEADER_BG = Constants.HEADER_BG;
    protected static final Color SIDEBAR_BG = Constants.SIDEBAR_BG;
    protected static final Color SIDEBAR_HOVER = Constants.SIDEBAR_HOVER;
    protected static final Color SIDEBAR_ACTIVE = Constants.SIDEBAR_ACTIVE;
    protected static final Color CONTENT_BG = Constants.CONTENT_BG;
    
    public BaseDashboardFrame(String title) {
        setTitle(title);
        // Không gọi initComponents() ở đây vì lớp con chưa kịp set biến
        // Lớp con sẽ gọi initUI() sau khi đã set các biến cần thiết
    }
    
    // Gọi method này từ constructor của lớp con sau khi đã set các biến
    protected void initUI() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 650));

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        
        // 1. HEADER
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // 2. Content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout(0, 0));
        
        // 2.1. SIDEBAR
        sidebarPanel = createSidebarPanel();
        contentWrapper.add(sidebarPanel, BorderLayout.WEST);
        
        // 2.2. MAIN CONTENT
        cardLayout = new CardLayout();
        panelNoiDung = new JPanel(cardLayout);
        panelNoiDung.setBackground(CONTENT_BG);
        
        initContentPanels();
        
        contentWrapper.add(panelNoiDung, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        add(mainPanel);
    }
    
    // Abstract method để lớp con implement nội dung bên trong
    protected abstract void initContentPanels();
    protected abstract String getUserName();
    protected abstract String getRoleName();
    protected abstract void onLogout();
    
    // Header chung (có thể override nếu cần)
    protected JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        // Left: Logo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftPanel.setOpaque(false);
        JLabel lblLogo = new JLabel("📚");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel lblAppName = new JLabel("EXAM MANAGEMENT");
        lblAppName.setFont(Constants.TITLE_FONT);
        lblAppName.setForeground(Color.WHITE);
        leftPanel.add(lblLogo);
        leftPanel.add(lblAppName);
        header.add(leftPanel, BorderLayout.WEST);
        
        // Right: User Info & Logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);
        
        JLabel lblUserIcon = new JLabel("👤");
        lblUserIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        rightPanel.add(lblUserIcon);
        
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        
        JLabel lblUserName = new JLabel(getUserName());
        lblUserName.setFont(Constants.BUTTON_FONT);
        lblUserName.setForeground(Color.WHITE);
        
        JLabel lblRole = new JLabel(getRoleName());
        lblRole.setFont(Constants.SMALL_FONT);
        lblRole.setForeground(Constants.TEXT_SECONDARY);
        
        userInfo.add(lblUserName);
        userInfo.add(lblRole);
        rightPanel.add(userInfo);
        
        JButton btnThoat = new JButton("⏻ Thoát");
        btnThoat.setFont(Constants.BUTTON_FONT);
        btnThoat.setForeground(Constants.TEXT_COLOR);
        btnThoat.setBackground(Constants.LOGOUT_BTN);
        btnThoat.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnThoat.setFocusPainted(false);
        btnThoat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThoat.addActionListener(e -> onLogout());
        
        rightPanel.add(btnThoat);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    // Sidebar cơ bản, lớp con sẽ add item vào đây thông qua method addMenuItem
    protected JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Lớp con sẽ populate items bằng cách gọi addMenuGroup/addMenuItem
        // Chúng ta tạo callback hoặc abstract method gọi ở đây nếu muốn strict structure
        // Nhưng để linh hoạt, ta để lớp con tự gọi trong constructor hoặc method init riêng, 
        // nhưng method này đang được gọi trong constructor của Base.
        // -> Ta sẽ để method này gọi 1 method abstract khác: initSidebarItems(sidebar)
        initSidebarItems(sidebar);
        
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }
    
    protected abstract void initSidebarItems(JPanel sidebar);
    
    // Helper helpers cho Sidebar
    protected void addMenuGroup(JPanel sidebar, String title) {
        JLabel lblGroup = new JLabel("  " + title);
        lblGroup.setFont(Constants.SMALL_FONT.deriveFont(Font.BOLD));
        lblGroup.setForeground(Constants.TEXT_SECONDARY);
        lblGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblGroup.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 0));
        sidebar.add(lblGroup);
    }
    
    protected JButton addMenuItem(JPanel sidebar, String icon, String text, String cardName) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(Constants.NORMAL_FONT);
        btn.setForeground(Constants.TEXT_COLOR);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != btnActive) {
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != btnActive) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });
        
        btn.addActionListener(e -> {
            setActiveButton(btn);
            if (cardName != null) {
                showCard(cardName);
            }
        });
        
        sidebar.add(btn);
        return btn;
    }
    
    // Method để hiển thị card - lớp con có thể override để thêm logic
    protected void showCard(String cardName) {
        cardLayout.show(panelNoiDung, cardName);
    }
    
    protected void setActiveButton(JButton btn) {
        if (btnActive != null) {
            btnActive.setBackground(SIDEBAR_BG);
            btnActive.setFont(Constants.NORMAL_FONT);
        }
        btnActive = btn;
        btnActive.setBackground(SIDEBAR_ACTIVE);
        btnActive.setFont(Constants.NORMAL_FONT.deriveFont(Font.BOLD));
    }
    
    // Helper cho Stat Card
    protected JPanel createStatCard(String icon, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Constants.CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.LIGHT_COLOR, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(Constants.HEADER_FONT);
        lblValue.setForeground(color);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Constants.NORMAL_FONT);
        lblTitle.setForeground(Constants.TEXT_SECONDARY);
        
        textPanel.add(lblValue);
        textPanel.add(lblTitle);
        
        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
}
