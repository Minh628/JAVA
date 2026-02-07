package gui.components;

import config.Constants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class SelectEntityDialog<T> extends JDialog {

    // Kết quả trả về (static để dễ truy cập)
    private static int selectedId = -1;
    private static String selectedLabel = "";
    private static String selectedType = "";

    // Dữ liệu đầu vào
    private final List<T> items;
    private final Function<T, Integer> getId;
    private final Function<T, String> getLabel;
    private final String typeKey;

    // Components
    private JComboBox<String> cboFilter;
    private JTextField txtKeyword;
    private JTable table;
    private DefaultTableModel tableModel;

    public SelectEntityDialog(Frame parent, String title, String typeKey,
            List<T> items, Function<T, Integer> getId, Function<T, String> getLabel) {
        super(parent, title, true);
        this.items = items != null ? items : new ArrayList<>();
        this.getId = getId;
        this.getLabel = getLabel;
        this.typeKey = typeKey != null ? typeKey : "";

        initUI();
        
        // Kích thước mặc định hợp lý hơn
        setSize(600, 450);
        setLocationRelativeTo(parent);
    }

    // --- Các phương thức Static giữ nguyên để tương thích code cũ ---
    public static void clearSelection() {
        selectedId = -1;
        selectedLabel = "";
        selectedType = "";
    }

    public static int getSelectedId() {
        return selectedId;
    }

    public static String getSelectedLabel() {
        return selectedLabel;
    }

    public static String getSelectedType() {
        return selectedType;
    }

    // --- Giao diện mới ---
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Constants.CONTENT_BG);

        // 1. Panel Tìm kiếm (North)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(Constants.CONTENT_BG);
        topPanel.setBorder(new EmptyBorder(5, 5, 0, 5));

        JLabel lblFilter = new JLabel("Tìm theo:");
        lblFilter.setFont(Constants.NORMAL_FONT);
        topPanel.add(lblFilter);

        cboFilter = new JComboBox<>(new String[] { "Tất cả", "Mã", "Tên" });
        cboFilter.setFont(Constants.NORMAL_FONT);
        cboFilter.setPreferredSize(new Dimension(100, 32));
        topPanel.add(cboFilter);

        txtKeyword = new JTextField(20);
        txtKeyword.setFont(Constants.NORMAL_FONT);
        txtKeyword.setPreferredSize(new Dimension(200, 32));
        txtKeyword.addActionListener(e -> applyFilter());
        topPanel.add(txtKeyword);

        CustomButton btnSearch = new CustomButton("Tìm kiếm", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnSearch.setPreferredSize(new Dimension(100, 32));
        btnSearch.addActionListener(e -> applyFilter());
        topPanel.add(btnSearch);

        add(topPanel, BorderLayout.NORTH);

        // 2. Bảng dữ liệu (Center) - Thay thế JList bằng JTable
        String[] columnNames = {"Mã", "Tên hiển thị"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp
            }
        };

        table = new JTable(tableModel);
        styleTable(table); // Áp dụng style đẹp

        // Sự kiện double click
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Constants.CONTENT_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding xung quanh bảng
        add(scrollPane, BorderLayout.CENTER);

        // 3. Panel Nút bấm (South)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Constants.CONTENT_BG);

        CustomButton btnCancel = new CustomButton("Hủy bỏ", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());

        CustomButton btnChoose = new CustomButton("Chọn", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnChoose.setPreferredSize(new Dimension(100, 35));
        btnChoose.addActionListener(e -> confirmSelection());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnChoose);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load dữ liệu ban đầu
        loadAll();
    }

    /**
     * Hàm trang trí bảng cho đẹp
     */
    private void styleTable(JTable table) {
        table.setFont(Constants.NORMAL_FONT);
        table.setRowHeight(30); // Tăng chiều cao dòng cho thoáng
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(Constants.LIGHT_COLOR);
        
        // Header style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Constants.PRIMARY_COLOR); // Dùng màu chủ đạo của app
        header.setForeground(Constants.TEXT_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Căn giữa cột Mã (Cột 0)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(100); // Mã không cần quá rộng
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        for (T item : items) {
            tableModel.addRow(new Object[]{
                getId.apply(item),
                getLabel.apply(item)
            });
        }
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    private void applyFilter() {
        String keyword = txtKeyword.getText().trim().toLowerCase();
        String filter = (String) cboFilter.getSelectedItem();
        tableModel.setRowCount(0);

        for (T item : items) {
            String idText = String.valueOf(getId.apply(item));
            String labelText = getLabel.apply(item) != null ? getLabel.apply(item) : "";
            String labelLower = labelText.toLowerCase();

            boolean match = false;
            // Logic tìm kiếm tiếng Việt
            if ("Mã".equals(filter)) { // Đã sửa thành tiếng Việt
                match = idText.contains(keyword);
            } else if ("Tên".equals(filter)) { // Đã sửa thành tiếng Việt
                match = labelLower.contains(keyword);
            } else { // Tất cả
                match = idText.contains(keyword) || labelLower.contains(keyword);
            }

            if (keyword.isEmpty() || match) {
                tableModel.addRow(new Object[]{
                    getId.apply(item),
                    labelText // Giữ nguyên chữ hoa thường khi hiển thị
                });
            }
        }

        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    private void confirmSelection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy giá trị từ model bảng
        Object idObj = table.getValueAt(row, 0);
        Object nameObj = table.getValueAt(row, 1);

        selectedId = Integer.parseInt(idObj.toString());
        selectedLabel = nameObj.toString();
        selectedType = typeKey;
        
        dispose();
    }
}