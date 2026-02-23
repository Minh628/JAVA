/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * Component: SearchPanel - Panel tìm kiếm + bảng dữ liệu
 * 
 * MÔ TẢ:
 *   - Panel trừu tượng (abstract) chứa thanh tìm kiếm và bảng dữ liệu
 *   - Hỗ trợ 2 chế độ: 1 bảng hoặc 2 bảng (Master-Detail)
 *   - Tự động gọi loadData() sau khi component được add vào container
 * 
 * CẤU TRÚC:
 *   ┌─────────────────────────────────────────────────┐
 *   │  [ComboBox loại tìm kiếm] [TextField] [Nút tìm]  │
 *   ├─────────────────────────────────────────────────┤
 *   │                                                 │
 *   │           CustomTable (Bảng chính)              │
 *   │                                                 │
 *   └─────────────────────────────────────────────────┘
 * 
 * CÁC METHOD ABSTRACT (subclass PHẢI implement):
 *   - loadData(): Tải dữ liệu vào bảng
 *   - timKiem(): Xử lý logic tìm kiếm
 *   - hienThiThongTin(): Hiển thị thông tin khi chọn dòng
 * 
 * @see BaseCrudPanel - Kế thừa SearchPanel + thêm CRUD
 * @see CustomTable - Bảng tùy chỉnh
 * ===========================================================================
 */
package gui.components;

import config.Constants;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public abstract class SearchPanel extends JPanel {
    protected CustomTable table;
    protected DefaultTableModel tableModel;
    protected CustomTable secondaryTable;
    protected DefaultTableModel secondaryTableModel;
    protected JTextField txtTimKiem;
    protected JComboBox<String> cboLoaiTimKiem;
    private boolean dataLoaded = false;

    protected SearchPanel(String[] columns, String[] searchOptions) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        initSingleTable(columns, searchOptions);
    }

    protected SearchPanel(String leftTitle, String[] leftColumns,
            String rightTitle, String[] rightColumns, String[] searchOptions) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        initTwoTables(leftTitle, leftColumns, rightTitle, rightColumns, searchOptions);
    }

    /**
     * Override addNotify de goi loadData() sau khi init xong.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        if (!dataLoaded) {
            dataLoaded = true;
            loadData();
        }
    }

    private void initSingleTable(String[] columns, String[] searchOptions) {
        tableModel = createTableModel(columns);
        table = new CustomTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        JPanel panelCenter = new JPanel(new BorderLayout(0, 5));
        panelCenter.setBackground(Constants.CONTENT_BG);
        panelCenter.add(createSearchPanel(searchOptions), BorderLayout.NORTH);
        panelCenter.add(scrollPane, BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);
    }

    private void initTwoTables(String leftTitle, String[] leftColumns,
            String rightTitle, String[] rightColumns, String[] searchOptions) {
        tableModel = createTableModel(leftColumns);
        table = new CustomTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        secondaryTableModel = createTableModel(rightColumns);
        secondaryTable = new CustomTable(secondaryTableModel);

        JPanel panelTables = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTables.setBackground(Constants.CONTENT_BG);

        JPanel leftPanel = buildTablePanel(leftTitle, table, true, searchOptions, getPrimaryTableTitleColor());
        JPanel rightPanel = buildTablePanel(rightTitle, secondaryTable, false, null, getSecondaryTableTitleColor());

        panelTables.add(leftPanel);
        panelTables.add(rightPanel);
        add(panelTables, BorderLayout.CENTER);
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

        private JPanel buildTablePanel(String title, CustomTable targetTable,
            boolean withSearch, String[] searchOptions, Color titleColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Constants.CONTENT_BG);

        if (title != null && !title.isBlank()) {
            JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
            lblTitle.setFont(Constants.TITLE_FONT);
            lblTitle.setForeground(titleColor);
            panel.add(lblTitle, BorderLayout.NORTH);
        }

        JScrollPane scrollPane = new JScrollPane(targetTable);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        JPanel content = new JPanel(new BorderLayout(0, 5));
        content.setBackground(Constants.CONTENT_BG);
        if (withSearch) {
            content.add(createSearchPanel(searchOptions), BorderLayout.NORTH);
        }
        content.add(scrollPane, BorderLayout.CENTER);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSearchPanel(String[] searchOptions) {
        JPanel panel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 5));
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

        addExtraSearchComponents(panel);

        return panel;
    }

    protected JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Constants.NORMAL_FONT);
        return lbl;
    }

    protected Color getPrimaryTableTitleColor() {
        return Constants.PRIMARY_COLOR;
    }

    protected Color getSecondaryTableTitleColor() {
        return Constants.SECONDARY_COLOR;
    }

    /**
     * Hook method de subclass co the them component tim kiem bo sung.
     */
    protected void addExtraSearchComponents(JPanel searchPanel) {
        // Default: khong them gi
    }

    protected abstract void loadData();

    protected abstract void hienThiThongTin();

    protected abstract void timKiem();
}
