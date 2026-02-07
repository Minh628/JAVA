package gui.components;

import config.Constants; // Giả sử bạn đã có class này
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import util.SearchCondition;

public class AdvancedSearchDialog extends JDialog {

    private static final String[] OPERATORS = {"=", "<>", ">", ">=", "<", "<=", "LIKE"};
    private static final String[] LOGIC_OPTIONS = {"AND (Và)", "OR (Hoặc)"};

    private final String[] searchFields;
    private JComboBox<String> cboLogic;
    private final List<ConditionPanel> conditionPanels = new ArrayList<>();
    private JPanel conditionsContainer;
    private boolean confirmed = false;
    private List<SearchCondition> result = new ArrayList<>();
    private String selectedLogic = "AND";

    public AdvancedSearchDialog(Frame parent, String title, String[] fields) {
        super(parent, title, true);
        this.searchFields = fields;
        initComponents();
        setSize(700, 500); // Tăng kích thước mặc định
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Constants.CONTENT_BG);

        // --- 1. HEADER PANEL (Màu nền chủ đạo) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Tùy chọn điều kiện kết hợp:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        
        JPanel logicControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logicControlPanel.setOpaque(false); // Trong suốt để thấy nền header
        
        cboLogic = new JComboBox<>(LOGIC_OPTIONS);
        cboLogic.setFont(Constants.NORMAL_FONT);
        cboLogic.setPreferredSize(new Dimension(120, 30));
        
        CustomButton btnAddCondition = new CustomButton("+ Thêm điều kiện", Constants.SUCCESS_COLOR, Color.WHITE);
        btnAddCondition.setPreferredSize(new Dimension(140, 30));
        btnAddCondition.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddCondition.addActionListener(e -> addCondition());

        logicControlPanel.add(cboLogic);
        logicControlPanel.add(btnAddCondition);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(logicControlPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CONTAINER CHỨA CÁC DÒNG ĐIỀU KIỆN ---
        conditionsContainer = new JPanel();
        conditionsContainer.setLayout(new BoxLayout(conditionsContainer, BoxLayout.Y_AXIS));
        conditionsContainer.setBackground(Color.WHITE);
        
        // Bọc trong ScrollPane đẹp hơn
        JScrollPane scrollPane = new JScrollPane(conditionsContainer);
        scrollPane.setBorder(null); // Bỏ viền mặc định xấu xí
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll mượt hơn
        
        add(scrollPane, BorderLayout.CENTER);

        // Thêm dòng đầu tiên mặc định
        addCondition();

        // --- 3. FOOTER BUTTONS ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Constants.CONTENT_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.LIGHT_COLOR));

        CustomButton btnClear = new CustomButton("Xóa tất cả", Constants.WARNING_COLOR, Color.WHITE);
        CustomButton btnCancel = new CustomButton("Hủy bỏ", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        CustomButton btnSearch = new CustomButton("Tìm kiếm", Constants.INFO_COLOR, Color.WHITE);
        
        // Chỉnh kích thước nút to đẹp
        Dimension btnSize = new Dimension(120, 38);
        btnClear.setPreferredSize(btnSize);
        btnCancel.setPreferredSize(btnSize);
        btnSearch.setPreferredSize(btnSize);

        btnSearch.addActionListener(e -> doSearch());
        btnCancel.addActionListener(e -> dispose());
        btnClear.addActionListener(e -> clearAllConditions());
        
        buttonPanel.add(btnClear);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSearch);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addCondition() {
        ConditionPanel panel = new ConditionPanel(searchFields, conditionPanels.size() + 1);
        panel.setRemoveAction(e -> removeCondition(panel));
        conditionPanels.add(panel);
        conditionsContainer.add(panel);
        
        // Auto scroll xuống dưới cùng khi thêm mới
        SwingUtilities.invokeLater(() -> {
            conditionsContainer.revalidate();
            conditionsContainer.repaint();
            JScrollBar vertical = ((JScrollPane)conditionsContainer.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void removeCondition(ConditionPanel panel) {
        if (conditionPanels.size() > 1) {
            conditionPanels.remove(panel);
            conditionsContainer.remove(panel);
            // Đánh lại số thứ tự
            for (int i = 0; i < conditionPanels.size(); i++) {
                conditionPanels.get(i).setNumber(i + 1);
            }
            conditionsContainer.revalidate();
            conditionsContainer.repaint();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Phải có ít nhất một điều kiện tìm kiếm!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllConditions() {
        conditionPanels.clear();
        conditionsContainer.removeAll();
        addCondition();
        conditionsContainer.revalidate();
        conditionsContainer.repaint();
    }

    private void doSearch() {
        result.clear();
        for (ConditionPanel panel : conditionPanels) {
            SearchCondition cond = panel.getCondition();
            if (cond != null && !cond.getValue().trim().isEmpty()) {
                result.add(cond);
            }
        }
        
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập ít nhất một giá trị tìm kiếm!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String logicRaw = (String) cboLogic.getSelectedItem();
        selectedLogic = logicRaw.contains("AND") ? "AND" : "OR"; // Xử lý chuỗi "AND (Và)"
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public List<SearchCondition> getConditions() { return result; }
    public String getLogic() { return selectedLogic; }

    /**
     * Inner class: Giao diện cho 1 dòng điều kiện
     */
    private static class ConditionPanel extends JPanel {
        private JLabel lblNumber;
        private final JComboBox<String> cboField;
        private final JComboBox<String> cboOperator;
        private final JTextField txtValue;
        private CustomButton btnRemove;

        public ConditionPanel(String[] fields, int number) {
            setLayout(new GridBagLayout()); // Dùng GridBagLayout để căn chỉnh thẳng hàng hơn FlowLayout
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)), // Đường kẻ mờ ngăn cách
                new EmptyBorder(10, 15, 10, 15) // Padding
            ));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 5, 0, 5); // Khoảng cách giữa các component
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;

            // 1. Số thứ tự
            lblNumber = new JLabel(number + ".");
            lblNumber.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblNumber.setForeground(Constants.SECONDARY_COLOR);
            gbc.gridx = 0; gbc.weightx = 0;
            add(lblNumber, gbc);

            // 2. ComboBox Trường
            cboField = new JComboBox<>(fields);
            styleComponent(cboField);
            gbc.gridx = 1; gbc.weightx = 0.3;
            add(cboField, gbc);

            // 3. ComboBox Toán tử
            cboOperator = new JComboBox<>(OPERATORS);
            styleComponent(cboOperator);
            ((JLabel)cboOperator.getRenderer()).setHorizontalAlignment(JLabel.CENTER); // Căn giữa toán tử
            gbc.gridx = 2; gbc.weightx = 0.15;
            add(cboOperator, gbc);

            // 4. TextField Giá trị
            txtValue = new JTextField();
            txtValue.setFont(Constants.NORMAL_FONT);
            txtValue.setPreferredSize(new Dimension(100, 35)); // Cao hơn cho đẹp
            gbc.gridx = 3; gbc.weightx = 0.55;
            add(txtValue, gbc);

            // 5. Nút Xóa (Dùng CustomButton màu đỏ)
            btnRemove = new CustomButton("X", Constants.DANGER_COLOR, Color.WHITE);
            btnRemove.setPreferredSize(new Dimension(60, 35));
            btnRemove.setToolTipText("Xóa điều kiện này");
            gbc.gridx = 4; gbc.weightx = 0;
            add(btnRemove, gbc);
        }
        
        private void styleComponent(JComponent comp) {
            comp.setFont(Constants.NORMAL_FONT);
            comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, 35));
            comp.setBackground(Color.WHITE);
        }

        public void setNumber(int number) {
            lblNumber.setText(number + ".");
        }

        public void setRemoveAction(ActionListener listener) {
            btnRemove.addActionListener(listener);
        }

        public SearchCondition getCondition() {
            String field = (String) cboField.getSelectedItem();
            String operator = (String) cboOperator.getSelectedItem();
            String value = txtValue.getText().trim();
            
            if (value.isEmpty()) return null;
            return new SearchCondition(field, operator, value);
        }
    }
}