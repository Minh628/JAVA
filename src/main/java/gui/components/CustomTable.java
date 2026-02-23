/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: CustomTable - Bảng dữ liệu tùy chỉnh
 * 
 * MÔ TẢ:
 *   - Bảng JTable với style hiện đại, đẹp mắt
 *   - Header màu PRIMARY_COLOR với chữ trắng, căn giữa
 *   - Các dòng xát (zebra stripes) dễ nhìn
 *   - Hiệu ứng hover khi di chuột qua dòng
 *   - Row cao 38px để dễ click
 * 
 * CÁCH SỬ DỤNG:
 *   // Cách 1: Với TableModel
 *   DefaultTableModel model = new DefaultTableModel(columns, 0);
 *   CustomTable table = new CustomTable(model);
 *   
 *   // Cách 2: Với data trực tiếp
 *   CustomTable table = new CustomTable(data, columns);
 *   
 *   // Cách 3: Rỗng, add data sau
 *   CustomTable table = new CustomTable();
 * 
 * TÍNH NĂNG:
 *   - Auto-resize columns
 *   - Selection background hiệu ứng mờ
 *   - Grid lines tùy chỉnh
 *   - Intercell spacing đẹp
 * 
 * @see SearchPanel - Sử dụng CustomTable
 * @see BaseCrudPanel - Sử dụng CustomTable
 * ===========================================================================
 */
package gui.components;

import config.Constants;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class CustomTable extends JTable {
    
    public CustomTable() {
        super();
        initStyle();
    }
    
    public CustomTable(TableModel model) {
        super(model);
        initStyle();
    }
    
    public CustomTable(Object[][] data, Object[] columns) {
        super(data, columns);
        initStyle();
    }
    
    private void initStyle() {
        // Header đẹp hơn
        JTableHeader header = getTableHeader();
        header.setBackground(Constants.PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        
        // Custom header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setBackground(Constants.PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, Constants.SECONDARY_COLOR),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                return label;
            }
        });
        
        // Rows
        setRowHeight(38);
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setSelectionBackground(new Color(52, 152, 219, 100));
        setSelectionForeground(Color.BLACK);
        setGridColor(new Color(230, 230, 230));
        setShowVerticalLines(true);
        setShowHorizontalLines(true);
        setIntercellSpacing(new Dimension(0, 0));
        
        // Border
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // Alternating row colors với renderer đẹp hơn
        setDefaultRenderer(Object.class, new ModernRowRenderer());
    }
    
    // Renderer hiện đại cho các hàng
    private class ModernRowRenderer extends DefaultTableCellRenderer {
        private final Color mauLe = Color.WHITE;
        private final Color mauChan = new Color(248, 249, 250);
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(new Color(52, 152, 219, 120));
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(row % 2 == 0 ? mauChan : mauLe);
                c.setForeground(new Color(50, 50, 50));
            }
            
            // Padding
            if (c instanceof JLabel) {
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            }
            
            return c;
        }
    }
}
