package gui.components;

import config.Constants;
import java.awt.*;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Base class cho tất cả các panel quản lý CRUD
 * Giúp tái sử dụng code cho phần tìm kiếm, bảng, và các nút chức năng
 */
public abstract class BaseCrudPanel extends JPanel {
    protected CustomTable table;
    protected DefaultTableModel tableModel;
    protected JTextField txtTimKiem;
    protected JComboBox<String> cboLoaiTimKiem;
    private boolean dataLoaded = false; // Flag để tránh load nhiều lần

    public BaseCrudPanel(String title, String[] columns, String[] searchOptions) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI(title, columns, searchOptions);
        // Không gọi loadData() ở đây vì subclass chưa khởi tạo xong
    }

    /**
     * Override addNotify để gọi loadData() SAU KHI tất cả initialization hoàn tất
     * addNotify() được gọi khi component được add vào container
     */
    @Override
    public void addNotify() {
        super.addNotify();
        if (!dataLoaded) {
            dataLoaded = true;
            loadData();
        }
    }

    private void initUI(String title, String[] columns, String[] searchOptions) {
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

        // Bảng
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new CustomTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                hienThiThongTin();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        // Panel center (search + table)
        JPanel panelCenter = new JPanel(new BorderLayout(0, 5));
        panelCenter.setBackground(Constants.CONTENT_BG);
        panelCenter.add(createSearchPanel(searchOptions), BorderLayout.NORTH);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel(String[] searchOptions) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Constants.CONTENT_BG);

        panel.add(createLabel("Tìm theo:"));
        cboLoaiTimKiem = new JComboBox<>(searchOptions);
        cboLoaiTimKiem.setFont(Constants.NORMAL_FONT);
        cboLoaiTimKiem.setPreferredSize(new Dimension(130, 28));
        panel.add(cboLoaiTimKiem);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(Constants.NORMAL_FONT);
        txtTimKiem.addActionListener(e -> timKiem());
        panel.add(txtTimKiem);

        CustomButton btnTim = new CustomButton("Tìm", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnTim.addActionListener(e -> timKiem());
        panel.add(btnTim);

        CustomButton btnTatCa = new CustomButton("Hiện tất cả", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        btnTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            cboLoaiTimKiem.setSelectedIndex(0);
            loadData();
        });
        panel.add(btnTatCa);

        // Hook for subclass to add extra search components
        addExtraSearchComponents(panel);

        return panel;
    }

    /**
     * Hook method để subclass có thể thêm các component tìm kiếm bổ sung
     * (ví dụ: nút tìm kiếm nâng cao)
     */
    protected void addExtraSearchComponents(JPanel searchPanel) {
        // Default: không thêm gì
    }

    /**
     * Hook method để subclass có thể thêm các nút chức năng bổ sung
     * (ví dụ: Quản lý câu hỏi, Xuất Excel/PDF)
     */
    protected void addExtraButtons(JPanel buttonPanel) {
        // Default: không thêm gì
    }

    // === Utility methods ===
    protected JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Constants.NORMAL_FONT);
        return lbl;
    }

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

    protected abstract void loadData();

    protected abstract void hienThiThongTin();

    protected abstract void timKiem();

    protected abstract void them();

    protected abstract void sua();

    protected abstract void xoa();

    protected abstract void lamMoi();
}
