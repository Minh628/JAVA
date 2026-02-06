/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI Component: AdvancedSearchDialog
 * Dialog tìm kiếm nâng cao với các điều kiện phức tạp
 */
package gui.components;

import bus.SearchCondition;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchDialog extends JDialog {

    private static final String[] OPERATORS = {"=", "<>", ">", ">=", "<", "<=", "LIKE"};
    private static final String[] LOGIC_OPTIONS = {"AND", "OR", "NOT"};

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
        setSize(600, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Logic selection panel
        JPanel logicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logicPanel.add(new JLabel("Kết hợp điều kiện:"));
        cboLogic = new JComboBox<>(LOGIC_OPTIONS);
        cboLogic.setSelectedIndex(0);
        logicPanel.add(cboLogic);
        
        JButton btnAddCondition = new JButton("+ Thêm điều kiện");
        btnAddCondition.addActionListener(e -> addCondition());
        logicPanel.add(Box.createHorizontalStrut(20));
        logicPanel.add(btnAddCondition);
        
        mainPanel.add(logicPanel, BorderLayout.NORTH);

        // Conditions container
        conditionsContainer = new JPanel();
        conditionsContainer.setLayout(new BoxLayout(conditionsContainer, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(conditionsContainer);
        scrollPane.setBorder(new TitledBorder("Điều kiện tìm kiếm"));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add initial condition
        addCondition();

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnCancel = new JButton("Hủy");
        JButton btnClear = new JButton("Xóa tất cả");
        
        btnSearch.addActionListener(e -> doSearch());
        btnCancel.addActionListener(e -> dispose());
        btnClear.addActionListener(e -> clearAllConditions());
        
        buttonPanel.add(btnClear);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSearch);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addCondition() {
        ConditionPanel panel = new ConditionPanel(searchFields, conditionPanels.size() + 1);
        panel.setRemoveAction(e -> removeCondition(panel));
        conditionPanels.add(panel);
        conditionsContainer.add(panel);
        conditionsContainer.revalidate();
        conditionsContainer.repaint();
    }

    private void removeCondition(ConditionPanel panel) {
        if (conditionPanels.size() > 1) {
            conditionPanels.remove(panel);
            conditionsContainer.remove(panel);
            // Renumber remaining conditions
            for (int i = 0; i < conditionPanels.size(); i++) {
                conditionPanels.get(i).setNumber(i + 1);
            }
            conditionsContainer.revalidate();
            conditionsContainer.repaint();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Phải có ít nhất một điều kiện tìm kiếm!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
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
                "Vui lòng nhập ít nhất một điều kiện tìm kiếm!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        selectedLogic = (String) cboLogic.getSelectedItem();
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<SearchCondition> getConditions() {
        return result;
    }

    public String getLogic() {
        return selectedLogic;
    }

    /**
     * Inner class for a single condition row
     */
    private static class ConditionPanel extends JPanel {
        private JLabel lblNumber;
        private final JComboBox<String> cboField;
        private final JComboBox<String> cboOperator;
        private final JTextField txtValue;
        private JButton btnRemove;

        public ConditionPanel(String[] fields, int number) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

            lblNumber = new JLabel(number + ".");
            lblNumber.setPreferredSize(new Dimension(25, 25));
            add(lblNumber);

            cboField = new JComboBox<>(fields);
            cboField.setPreferredSize(new Dimension(120, 25));
            add(cboField);

            cboOperator = new JComboBox<>(OPERATORS);
            cboOperator.setPreferredSize(new Dimension(70, 25));
            add(cboOperator);

            txtValue = new JTextField();
            txtValue.setPreferredSize(new Dimension(200, 25));
            add(txtValue);

            btnRemove = new JButton("X");
            btnRemove.setForeground(Color.RED);
            btnRemove.setPreferredSize(new Dimension(45, 25));
            btnRemove.setToolTipText("Xóa điều kiện này");
            add(btnRemove);
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
            
            if (value.isEmpty()) {
                return null;
            }
            return new SearchCondition(field, operator, value);
        }
    }
}
