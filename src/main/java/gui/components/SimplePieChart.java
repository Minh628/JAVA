/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: SimplePieChart - Biểu đồ tròn đơn giản
 * 
 * MÔ TẢ:
 *   - Vẽ biểu đồ tròn (pie chart) không cần thư viện ngoài
 *   - Tự vẽ bằng Graphics2D
 *   - Hiển thị tỷ lệ phần trăm
 *   - Có chú thích (legend) bên phải
 * 
 * CÁCH SỬ DỤNG:
 *   SimplePieChart chart = new SimplePieChart("Tỷ lệ Đạt/Rớt");
 *   
 *   List<String> labels = Arrays.asList("Đạt", "Rớt");
 *   List<Double> values = Arrays.asList(75.0, 25.0);
 *   chart.setData(labels, values);
 * 
 * TÍNH NĂNG:
 *   - Hiển thị tiêu đề (title) phía trên
 *   - Vẽ biểu đồ tròn với các màu khác nhau
 *   - Hiển thị phần trăm trên mỗi phần
 *   - Legend bên phải với màu + tên + giá trị
 *   - Màu mặc định: Xanh lá (Đạt), Đỏ (Rớt)
 * 
 * @see SimpleBarChart - Biểu đồ cột
 * @see ThongKePanel - Sử dụng SimplePieChart
 * ===========================================================================
 */
package gui.components;

import config.Constants;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class SimplePieChart extends JPanel {
    private List<String> labels;
    private List<Double> values;
    private String title;
    
    // Màu mặc định
    private static final Color[] COLORS = {
        Constants.SUCCESS_COLOR,  // Đạt - Xanh lá
        Constants.DANGER_COLOR,   // Rớt - Đỏ
        Constants.PRIMARY_COLOR,
        Constants.ORANGE_COLOR,
        Constants.PURPLE_COLOR,
        Constants.INFO_COLOR,
        new Color(241, 196, 15),
        new Color(26, 188, 156)
    };
    
    public SimplePieChart() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(300, 300));
    }
    
    public SimplePieChart(String title) {
        this();
        this.title = title;
    }
    
    public void setData(List<String> labels, List<Double> values) {
        this.labels = labels;
        this.values = values;
        repaint();
    }
    
    public void setTitle(String title) {
        this.title = title;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (labels == null || values == null || labels.isEmpty()) {
            drawNoData(g2d);
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        int titleHeight = 40;
        
        // Vẽ tiêu đề
        if (title != null && !title.isEmpty()) {
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.setColor(Constants.TEXT_COLOR);
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (width - fm.stringWidth(title)) / 2;
            g2d.drawString(title, titleX, 25);
        }
        
        // Tính tổng
        double total = 0;
        for (Double v : values) {
            if (v != null) total += v;
        }
        
        if (total == 0) {
            drawNoData(g2d);
            return;
        }
        
        // Kích thước biểu đồ
        int legendWidth = 120;
        int padding = 20;
        int chartSize = Math.min(width - legendWidth - padding * 2, height - titleHeight - padding * 2);
        chartSize = Math.max(chartSize, 100);
        
        int chartX = padding;
        int chartY = titleHeight + padding;
        
        // Vẽ các phần
        int startAngle = 90;
        for (int i = 0; i < values.size(); i++) {
            double value = values.get(i) != null ? values.get(i) : 0;
            int arcAngle = (int) Math.round((value / total) * 360);
            
            // Tránh góc 0
            if (arcAngle == 0 && value > 0) arcAngle = 1;
            
            Color color = COLORS[i % COLORS.length];
            
            // Vẽ phần
            g2d.setColor(color);
            g2d.fillArc(chartX, chartY, chartSize, chartSize, startAngle, -arcAngle);
            
            // Viền
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawArc(chartX, chartY, chartSize, chartSize, startAngle, -arcAngle);
            
            // Vẽ phần trăm trên biểu đồ
            if (arcAngle > 20) {
                double midAngle = Math.toRadians(startAngle - arcAngle / 2.0);
                int labelRadius = chartSize / 3;
                int labelX = chartX + chartSize / 2 + (int) (Math.cos(midAngle) * labelRadius);
                int labelY = chartY + chartSize / 2 - (int) (Math.sin(midAngle) * labelRadius);
                
                String percent = String.format("%.0f%%", (value / total) * 100);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(percent, labelX - fm.stringWidth(percent) / 2, labelY + 4);
            }
            
            startAngle -= arcAngle;
        }
        
        // Vẽ chú thích (legend)
        int legendX = chartX + chartSize + 20;
        int legendY = chartY + 20;
        g2d.setFont(Constants.SMALL_FONT);
        
        for (int i = 0; i < labels.size(); i++) {
            Color color = COLORS[i % COLORS.length];
            double value = values.get(i) != null ? values.get(i) : 0;
            double percent = (value / total) * 100;
            
            // Ô màu
            g2d.setColor(color);
            g2d.fillRoundRect(legendX, legendY + i * 25, 15, 15, 3, 3);
            
            // Text
            g2d.setColor(Constants.TEXT_COLOR);
            String legendText = String.format("%s (%.0f%%)", labels.get(i), percent);
            g2d.drawString(legendText, legendX + 22, legendY + i * 25 + 12);
        }
    }
    
    private void drawNoData(Graphics2D g2d) {
        g2d.setColor(Constants.TEXT_SECONDARY);
        g2d.setFont(Constants.NORMAL_FONT);
        String msg = "Không có dữ liệu";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(msg, x, y);
    }
}
