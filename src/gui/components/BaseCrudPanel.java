package gui.components;

import config.Constants;
import java.awt.*;
import java.util.function.Function;
import javax.swing.*;

/**
 * Base class cho tất cả các panel quản lý CRUD
 * Giúp tái sử dụng code cho phần tìm kiếm, bảng, và các nút chức năng
 */
public abstract class BaseCrudPanel extends SearchPanel {

    public BaseCrudPanel(String title, String[] columns, String[] searchOptions) {
        super(columns, searchOptions);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI(title);
        // Không gọi loadData() ở đây vì subclass chưa khởi tạo xong
    }

    public BaseCrudPanel(String title, String leftTitle, String[] leftColumns,
            String rightTitle, String[] rightColumns, String[] searchOptions) {
        super(leftTitle, leftColumns, rightTitle, rightColumns, searchOptions);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI(title);
        // Không gọi loadData() ở đây vì subclass chưa khởi tạo xong
    }

    private void initUI(String title) {
        // Tiêu đề
        HeaderLabel lblTieuDe = HeaderLabel.createPrimary(title);

        // Form panel
        JPanel panelForm = createFormPanel();

        // CRUD buttons
        CrudButtonPanel crudButtons = new CrudButtonPanel(Constants.CARD_COLOR);
        crudButtons.setAllListeners(e -> them(), e -> sua(), e -> xoa(), e -> lamMoi());
        addExtraButtons(crudButtons);

        // Panel trên (form + buttons)
        JPanel panelFormWrapper = new JPanel(new BorderLayout(0, 10));
        panelFormWrapper.setBackground(Constants.CONTENT_BG);
        panelFormWrapper.add(panelForm, BorderLayout.CENTER);
        panelFormWrapper.add(crudButtons, BorderLayout.SOUTH);

        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.CONTENT_BG);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelFormWrapper, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);

        // Panel center (search + table) đã được tạo trong SearchPanel
    }

    /**
     * Hook method để subclass có thể thêm các nút chức năng bổ sung
     * (ví dụ: Quản lý câu hỏi, Xuất Excel/PDF)
     */
    protected void addExtraButtons(JPanel buttonPanel) {
        // Default: không thêm gì
    }

    // === Utility methods ===
    protected JTextField createTextField(int columns, boolean editable) {
        JTextField txt = new JTextField(columns);
        txt.setFont(Constants.NORMAL_FONT);
        txt.setEditable(editable);
        return txt;
    }

    protected void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    protected boolean confirmDelete(String itemName) {
        return JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa " + itemName + " này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    protected boolean validateNotEmpty(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập " + fieldName + "!");
            field.requestFocus();
            return false;
        }
        return true;
    }

    protected <T> void selectComboByName(JComboBox<T> combo, String name, Function<T, String> getName) {
        if (name == null || combo == null)
            return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            T item = combo.getItemAt(i);
            if (item != null) {
                String itemName = getName.apply(item);
                if (name.equals(itemName)) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    // === Abstract methods ===
    protected abstract JPanel createFormPanel();

    protected abstract void them();

    protected abstract void sua();

    protected abstract void xoa();

    protected abstract void lamMoi();
}
