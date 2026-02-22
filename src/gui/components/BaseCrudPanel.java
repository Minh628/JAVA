/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: BaseCrudPanel - Panel CRUD chung
 * 
 * MÔ TẢ:
 *   - Class trừu tượng (abstract) là base cho các panel quản lý
 *   - Kế thừa SearchPanel và thêm các chức năng CRUD
 *   - Tự động tạo form nhập liệu + các nút Thêm/Sửa/Xóa/Làm mới
 * 
 * CẤU TRÚC GIAO DIỆN:
 *   ┌─────────────────────────────────────────────────┐
 *   │                 TIÊU ĐỀ PANEL                       │
 *   ├─────────────────────────────────────────────────┤
 *   │  Form nhập liệu (createFormPanel)                  │
 *   ├─────────────────────────────────────────────────┤
 *   │  [Thêm] [Sửa] [Xóa] [Làm mới] [Nút bổ sung...]     │
 *   ├─────────────────────────────────────────────────┤
 *   │  Thanh tìm kiếm (từ SearchPanel)                   │
 *   ├─────────────────────────────────────────────────┤
 *   │                                                 │
 *   │           Bảng dữ liệu (CustomTable)              │
 *   │                                                 │
 *   └─────────────────────────────────────────────────┘
 * 
 * CÁC METHOD ABSTRACT (subclass PHẢI implement):
 *   - createFormPanel(): Tạo form nhập liệu
 *   - loadData(): Tải dữ liệu vào bảng
 *   - timKiem(): Logic tìm kiếm
 *   - hienThiThongTin(): Hiển thị thông tin khi chọn dòng
 *   - them(): Thêm mới
 *   - sua(): Cập nhật
 *   - xoa(): Xóa
 *   - lamMoi(): Reset form
 * 
 * CÁC METHOD HỖ TRỢ (sẵn có):
 *   - showMessage(): Hiển thị thông báo
 *   - confirmDelete(): Xác nhận xóa
 *   - validateNotEmpty(): Kiểm tra rỗng
 *   - selectComboByName(): Chọn item trong ComboBox theo tên
 *   - createLabel(): Tạo label chuẩn
 *   - createTextField(): Tạo text field chuẩn
 * 
 * HOOK METHOD:
 *   - addExtraButtons(): Override để thêm nút bổ sung (ví dụ: Export Excel)
 * 
 * @see QuanLyGiangVienPanel - Ví dụ sử dụng BaseCrudPanel
 * @see QuanLySinhVienPanel - Ví dụ sử dụng BaseCrudPanel
 * @see CrudButtonPanel - Panel nút CRUD
 * ===========================================================================
 */
package gui.components;

import config.Constants;
import java.awt.*;
import java.util.function.Function;
import javax.swing.*;
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
