/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: WrapLayout - FlowLayout tự động xuống dòng
 * 
 * MÔ TẢ:
 *   - Mở rộng FlowLayout với khả năng tự động wrap xuống dòng mới
 *   - Khi các component vượt quá chiều rộng container
 *   - Tự động tính toán lại chiều cao preferred
 * 
 * CÁCH SỬ DỤNG:
 *   // Thay FlowLayout bằng WrapLayout
 *   JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
 *   
 *   // Thêm các component - tự động xuống dòng khi hết chỗ
 *   for (int i = 0; i < 20; i++) {
 *       panel.add(new JButton("Button " + i));
 *   }
 * 
 * SỬ DỤNG TRONG HỆ THỐNG:
 *   - LamBaiThiFrame: Bảng số câu hỏi (xuống dòng khi nhiều câu)
 * 
 * @see LamBaiThiFrame - Sử dụng WrapLayout
 * ===========================================================================
 */
package gui.components;

import java.awt.*;
public class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            Insets insets = target.getInsets();
            int hgap = getHgap();
            int vgap = getVgap();
            int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

            int width = 0;
            int height = insets.top + insets.bottom + vgap * 2;
            int rowWidth = 0;
            int rowHeight = 0;

            int count = target.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component comp = target.getComponent(i);
                if (!comp.isVisible()) {
                    continue;
                }

                Dimension d = preferred ? comp.getPreferredSize() : comp.getMinimumSize();
                if (rowWidth == 0) {
                    rowWidth = d.width;
                } else if (rowWidth + hgap + d.width <= maxWidth) {
                    rowWidth += hgap + d.width;
                } else {
                    width = Math.max(width, rowWidth);
                    height += rowHeight + vgap;
                    rowWidth = d.width;
                    rowHeight = 0;
                }

                rowHeight = Math.max(rowHeight, d.height);
            }

            width = Math.max(width, rowWidth);
            height += rowHeight;

            return new Dimension(
                    width + insets.left + insets.right + hgap * 2,
                    height
            );
        }
    }
}
