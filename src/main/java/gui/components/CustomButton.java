/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: CustomButton - Nút bấm tùy chỉnh với hiệu ứng đẹp
 * 
 * MÔ TẢ:
 *   - Nút bấm hiện đại với bo góc (rounded corners)
 *   - Tự động có hiệu ứng hover (sáng lên khi di chuột)
 *   - Hiệu ứng nhấn (pressed) - tối đi khi click
 *   - Có shadow tạo chiều sâu
 *   - Có gradient highlight ở nửa trên
 * 
 * CÁCH SỬ DỤNG:
 *   // Tạo nút với màu mặc định (PRIMARY_COLOR)
 *   CustomButton btn = new CustomButton("Xác nhận");
 *   
 *   // Tạo nút với màu tùy chỉnh
 *   CustomButton btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Color.WHITE);
 *   
 *   // Thay đổi màu sau khi tạo
 *   btn.setMauNen(Constants.SUCCESS_COLOR);
 *   
 *   // Thay đổi bo góc
 *   btn.setBorderRadius(20); // Bo tròn hơn
 * 
 * CHÚ Ý:
 *   - Nên sử dụng CustomButton thay vì JButton thông thường
 *   - Đảm bảo đồng bộ style trên toàn hệ thống
 *   - Không cần gọi setFont, setBackground, setFocusPainted - đã xử lý tự động
 * 
 * @see CrudButtonPanel - Panel chứa nhóm nút CRUD sử dụng CustomButton
 * @see config.Constants - Màu sắc hệ thống
 * ===========================================================================
 */
package gui.components;

import config.Constants;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class CustomButton extends JButton {
    private Color mauNen;
    private Color mauChu;
    private Color mauHover;
    private Color mauPressed;
    private boolean isHovering = false;
    private boolean isPressed = false;
    private int borderRadius = 10;
    
    public CustomButton(String text) {
        this(text, Constants.PRIMARY_COLOR, Color.WHITE);
    }
    
    public CustomButton(String text, Color mauNen, Color mauChu) {
        super(text);
        this.mauNen = mauNen;
        this.mauChu = mauChu;
        this.mauHover = brighten(mauNen, 0.2f);
        this.mauPressed = mauNen.darker();
        
        initStyle();
        addHoverEffect();
    }
    
    private void initStyle() {
        setForeground(mauChu);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(130, 38));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Xác định màu nền
        Color bgColor;
        if (isPressed) {
            bgColor = mauPressed;
        } else if (isHovering) {
            bgColor = mauHover;
        } else {
            bgColor = mauNen;
        }
        
        // Vẽ shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 4, getHeight() - 4, borderRadius, borderRadius));
        
        // Vẽ nền
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 3, borderRadius, borderRadius));
        
        // Vẽ gradient highlight
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 255, 255, 50),
            0, getHeight()/2, new Color(255, 255, 255, 0)
        );
        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight()/2, borderRadius, borderRadius));
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    private void addHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovering = false;
                isPressed = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    private Color brighten(Color color, float fraction) {
        int red = Math.min(255, (int)(color.getRed() + 255 * fraction));
        int green = Math.min(255, (int)(color.getGreen() + 255 * fraction));
        int blue = Math.min(255, (int)(color.getBlue() + 255 * fraction));
        return new Color(red, green, blue);
    }
    
    public void setMauNen(Color mau) {
        this.mauNen = mau;
        this.mauHover = brighten(mau, 0.2f);
        this.mauPressed = mau.darker();
        repaint();
    }
    
    public void setMauChu(Color mau) {
        this.mauChu = mau;
        setForeground(mau);
    }
    
    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
        repaint();
    }
}
