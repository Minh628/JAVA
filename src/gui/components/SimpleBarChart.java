/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Component: SimpleBarChart - Biểu đồ cột đơn giản
 */
package gui.components;

import config.Constants;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class SimpleBarChart extends JPanel {
    private List<String> labels;
    private List<Double> values;
    private String title;
    private String yAxisLabel;
    private Color[] barColors;
    private double maxValue = 10.0; // Mặc định cho điểm số
    
    // Màu mặc định cho các cột
    private static final Color[] DEFAULT_COLORS = {
        Constants.PRIMARY_COLOR,
        Constants.SUCCESS_COLOR,
        Constants.PURPLE_COLOR,
        Constants.ORANGE_COLOR,
        Constants.SECONDARY_COLOR,
        Constants.DANGER_COLOR,
        Constants.INFO_COLOR,
        new Color(241, 196, 15),  // Vàng
        new Color(26, 188, 156),  // Teal
        new Color(149, 165, 166)  // Xám
    };
    
    public SimpleBarChart() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 300));
    }
    
    public SimpleBarChart(String title) {
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
    
    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
        repaint();
    }
    
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        repaint();
    }
    
    public void setBarColors(Color[] colors) {
        this.barColors = colors;
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
        int padding = 60;
        int chartX = padding + 30;
        int chartY = padding;
        int chartWidth = width - chartX - padding;
        int chartHeight = height - chartY - padding - 20;
        
        // Vẽ tiêu đề
        if (title != null && !title.isEmpty()) {
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.setColor(Constants.TEXT_COLOR);
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (width - fm.stringWidth(title)) / 2;
            g2d.drawString(title, titleX, 30);
        }
        
        // Vẽ trục Y
        g2d.setColor(Constants.BORDER_COLOR);
        g2d.drawLine(chartX, chartY, chartX, chartY + chartHeight);
        
        // Vẽ trục X  
        g2d.drawLine(chartX, chartY + chartHeight, chartX + chartWidth, chartY + chartHeight);
        
        // Vẽ các đường kẻ ngang và nhãn trục Y
        g2d.setFont(Constants.SMALL_FONT);
        int numGridLines = 5;
        for (int i = 0; i <= numGridLines; i++) {
            int y = chartY + chartHeight - (i * chartHeight / numGridLines);
            double value = (maxValue * i) / numGridLines;
            
            // Đường kẻ ngang
            g2d.setColor(new Color(230, 230, 230));
            if (i > 0) {
                g2d.drawLine(chartX + 1, y, chartX + chartWidth, y);
            }
            
            // Nhãn giá trị
            g2d.setColor(Constants.TEXT_SECONDARY);
            String label = String.format("%.1f", value);
            g2d.drawString(label, chartX - 35, y + 4);
        }
        
        // Vẽ nhãn trục Y
        if (yAxisLabel != null && !yAxisLabel.isEmpty()) {
            g2d.setColor(Constants.TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            Graphics2D g2dCopy = (Graphics2D) g2d.create();
            g2dCopy.rotate(-Math.PI / 2);
            g2dCopy.drawString(yAxisLabel, -height / 2 - 30, 20);
            g2dCopy.dispose();
        }
        
        // Vẽ các cột
        int numBars = labels.size();
        int barWidth = Math.min(50, (chartWidth - 20) / numBars - 10);
        int gap = (chartWidth - (numBars * barWidth)) / (numBars + 1);
        
        Color[] colors = (barColors != null) ? barColors : DEFAULT_COLORS;
        
        for (int i = 0; i < numBars; i++) {
            double value = values.get(i) != null ? values.get(i) : 0;
            int barHeight = (int) ((value / maxValue) * chartHeight);
            barHeight = Math.max(barHeight, 1); // Tối thiểu 1px
            
            int x = chartX + gap + i * (barWidth + gap);
            int y = chartY + chartHeight - barHeight;
            
            // Vẽ cột với gradient
            Color barColor = colors[i % colors.length];
            GradientPaint gradient = new GradientPaint(
                x, y, barColor.brighter(),
                x, y + barHeight, barColor
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, y, barWidth, barHeight, 5, 5);
            
            // Viền cột
            g2d.setColor(barColor.darker());
            g2d.drawRoundRect(x, y, barWidth, barHeight, 5, 5);
            
            // Giá trị trên cột
            g2d.setColor(Constants.TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String valueStr = String.format("%.1f", value);
            FontMetrics fm = g2d.getFontMetrics();
            int valueX = x + (barWidth - fm.stringWidth(valueStr)) / 2;
            g2d.drawString(valueStr, valueX, y - 5);
            
            // Nhãn dưới cột
            g2d.setFont(Constants.SMALL_FONT);
            g2d.setColor(Constants.TEXT_SECONDARY);
            String label = labels.get(i);
            
            // Xử lý nhãn dài
            if (fm.stringWidth(label) > barWidth + gap - 5) {
                label = label.length() > 8 ? label.substring(0, 6) + ".." : label;
            }
            int labelX = x + (barWidth - fm.stringWidth(label)) / 2;
            g2d.drawString(label, labelX, chartY + chartHeight + 15);
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
