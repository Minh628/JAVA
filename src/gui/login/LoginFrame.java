/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: LoginFrame - Màn hình đăng nhập
 */
package gui.login;

import bus.DangNhapBUS;
import config.Constants;
import dto.GiangVienDTO;
import dto.SinhVienDTO;
import dto.VaiTroDTO;
import gui.admin.AdminDashboard;
import gui.student.StudentDashboard;
import gui.teacher.TeacherDashboard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JComboBox<String> cboVaiTro;
    private DangNhapBUS dangNhapBUS;

    public LoginFrame() {
        this.dangNhapBUS = new DangNhapBUS();
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng nhập - Hệ thống thi trắc nghiệm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel với 2 phần: Left (hình ảnh) và Right (form)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        
        // ============ LEFT PANEL - Branding ============
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 55, 109),
                    getWidth(), getHeight(), new Color(41, 128, 185)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth() - 100, getHeight() - 150, 250, 250);
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.fillOval(100, getHeight() - 100, 150, 150);
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        
        JPanel brandingContent = new JPanel();
        brandingContent.setLayout(new BoxLayout(brandingContent, BoxLayout.Y_AXIS));
        brandingContent.setOpaque(false);
        
        // Logo icon
        JLabel lblIcon = new JLabel("📚");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandingContent.add(lblIcon);
        
        brandingContent.add(Box.createVerticalStrut(20));
        
        // App name
        JLabel lblAppName = new JLabel("EXAM MANAGEMENT");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandingContent.add(lblAppName);
        
        // Subtitle
        JLabel lblSubtitle = new JLabel("Hệ thống thi trắc nghiệm trực tuyến");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(200, 220, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandingContent.add(lblSubtitle);
        
        brandingContent.add(Box.createVerticalStrut(40));
        
        // Features
        String[] features = {
            "  Quản lý đề thi thông minh",
            "  Thi trực tuyến an toàn",
            "  Chấm điểm tự động",
            "  Thống kê kết quả chi tiết"
        };
        
        for (String feature : features) {
            JLabel lblFeature = new JLabel(feature);
            lblFeature.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblFeature.setForeground(new Color(180, 200, 230));
            lblFeature.setAlignmentX(Component.CENTER_ALIGNMENT);
            brandingContent.add(lblFeature);
            brandingContent.add(Box.createVerticalStrut(8));
        }
        
        leftPanel.add(brandingContent);
        
        // ============ RIGHT PANEL - Login Form ============
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        
        JPanel formContainer = new JPanel();
        formContainer.setBackground(Color.WHITE);
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // Welcome text
        JLabel lblWelcome = new JLabel("Chào mừng trở lại!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblWelcome.setForeground(Constants.TEXT_COLOR);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblWelcome);
        
        JLabel lblDesc = new JLabel("Đăng nhập để tiếp tục");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDesc.setForeground(Constants.TEXT_SECONDARY);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblDesc);
        
        formContainer.add(Box.createVerticalStrut(40));
        
        // Vai trò field
        JLabel lblVaiTro = new JLabel("Vai trò");
        lblVaiTro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVaiTro.setForeground(Constants.TEXT_COLOR);
        lblVaiTro.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblVaiTro);
        formContainer.add(Box.createVerticalStrut(8));
        
        cboVaiTro = new JComboBox<>(new String[]{"  Sinh viên", "  Giảng viên", "  Admin"});
        cboVaiTro.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cboVaiTro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cboVaiTro.setPreferredSize(new Dimension(300, 50));
        cboVaiTro.setBackground(Color.WHITE);
        cboVaiTro.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        cboVaiTro.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(cboVaiTro);
        
        formContainer.add(Box.createVerticalStrut(20));
        
        // Username field
        JLabel lblUsername = new JLabel("Tên đăng nhập");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(Constants.TEXT_COLOR);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblUsername);
        formContainer.add(Box.createVerticalStrut(8));
        
        txtTenDangNhap = createStyledTextField("Nhập tên đăng nhập...");
        formContainer.add(txtTenDangNhap);
        
        formContainer.add(Box.createVerticalStrut(20));
        
        // Password field
        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(Constants.TEXT_COLOR);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblPassword);
        formContainer.add(Box.createVerticalStrut(8));
        
        txtMatKhau = createStyledPasswordField("Nhập mật khẩu...");
        formContainer.add(txtMatKhau);
        
        formContainer.add(Box.createVerticalStrut(35));
        
        // Login button
        btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDangNhap.setForeground(Constants.TEXT_COLOR);
        btnDangNhap.setBackground(Constants.SUCCESS_COLOR);
        btnDangNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btnDangNhap.setPreferredSize(new Dimension(300, 55));
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDangNhap.addActionListener(e -> dangNhap());
        
        // Hover effect
        Color normalColor = Constants.SUCCESS_COLOR;
        Color hoverColor = normalColor.darker();
        btnDangNhap.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnDangNhap.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                btnDangNhap.setBackground(normalColor);
            }
        });
        
        formContainer.add(btnDangNhap);
        
        formContainer.add(Box.createVerticalStrut(30));
        
        // Footer
        JLabel lblFooter = new JLabel("© 2026 Exam Management System");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(Constants.TEXT_SECONDARY);
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblFooter);
        
        // Enter key listener
        txtMatKhau.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dangNhap();
                }
            }
        });
        txtTenDangNhap.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtMatKhau.requestFocus();
                }
            }
        });
        
        rightPanel.add(formContainer);
        
        // Add panels to main
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel);
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, getInsets().left + 5, g.getFontMetrics().getMaxAscent() + getInsets().top + 5);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setPreferredSize(new Dimension(300, 50));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(11, 10, 18, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Focus effect
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(11, 10, 18, 10)
                ));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(11, 10, 18, 10)
                ));
            }
        });
        
        return field;
    }
    
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, getInsets().left + 5, g.getFontMetrics().getMaxAscent() + getInsets().top + 5);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setPreferredSize(new Dimension(300, 50));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(11, 10, 18, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Focus effect
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(11, 10, 18, 10)
                ));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(11, 10, 18, 10)
                ));
            }
        });
        
        return field;
    }

    private void dangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        int vaiTroIndex = cboVaiTro.getSelectedIndex();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng điền đầy đủ thông tin!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác thực đăng nhập
        Object user = dangNhapBUS.dangNhap(tenDangNhap, matKhau);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this, 
                "Tên đăng nhập hoặc mật khẩu không đúng!", 
                "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra vai trò
        int maVaiTro = dangNhapBUS.getVaiTro(user);
        
        // vaiTroIndex: 0 = Sinh viên, 1 = Giảng viên, 2 = ADMIN
        boolean vaiTroHopLe = false;
        
        if (vaiTroIndex == 0 && maVaiTro == VaiTroDTO.SINH_VIEN) {
            vaiTroHopLe = true;
        } else if (vaiTroIndex == 1 && maVaiTro == VaiTroDTO.GIANG_VIEN) {
            vaiTroHopLe = true;
        } else if (vaiTroIndex == 2 && maVaiTro == VaiTroDTO.ADMIN) {
            vaiTroHopLe = true;
        }
        
        if (!vaiTroHopLe) {
            JOptionPane.showMessageDialog(this, 
                "Vai trò không khớp với tài khoản!", 
                "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đăng nhập thành công
        JOptionPane.showMessageDialog(this, 
            "Đăng nhập thành công!", 
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        
        this.dispose();
        
        // Mở dashboard tương ứng
        switch (maVaiTro) {
            case VaiTroDTO.ADMIN:
                new AdminDashboard((GiangVienDTO) user).setVisible(true);
                break;
            case VaiTroDTO.GIANG_VIEN:
                new TeacherDashboard((GiangVienDTO) user).setVisible(true);
                break;
            case VaiTroDTO.SINH_VIEN:
                new StudentDashboard((SinhVienDTO) user).setVisible(true);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
