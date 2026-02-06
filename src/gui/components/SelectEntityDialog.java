package gui.components;

import config.Constants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;

public class SelectEntityDialog<T> extends JDialog {
    private static int selectedId = -1;
    private static String selectedLabel = "";
    private static String selectedType = "";

    private final List<T> items;
    private final Function<T, Integer> getId;
    private final Function<T, String> getLabel;
    private final String typeKey;

    private JComboBox<String> cboFilter;
    private JTextField txtKeyword;
    private DefaultListModel<T> listModel;
    private JList<T> list;

    public SelectEntityDialog(Frame parent, String title, String typeKey,
            List<T> items, Function<T, Integer> getId, Function<T, String> getLabel) {
        super(parent, title, true);
        this.items = items != null ? items : new ArrayList<>();
        this.getId = getId;
        this.getLabel = getLabel;
        this.typeKey = typeKey != null ? typeKey : "";
        initUI();
        setSize(520, 360);
        setLocationRelativeTo(parent);
    }

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

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(Constants.CONTENT_BG);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Constants.CONTENT_BG);
        searchPanel.add(new JLabel("Tim theo:"));

        cboFilter = new JComboBox<>(new String[] { "Tat ca", "Ma", "Ten" });
        cboFilter.setPreferredSize(new Dimension(100, 28));
        searchPanel.add(cboFilter);

        txtKeyword = new JTextField(20);
        txtKeyword.addActionListener(e -> applyFilter());
        searchPanel.add(txtKeyword);

        CustomButton btnSearch = new CustomButton("Tim", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnSearch.addActionListener(e -> applyFilter());
        searchPanel.add(btnSearch);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                @SuppressWarnings("unchecked")
                T item = (T) value;
                String text = String.valueOf(getId.apply(item)) + " - " + getLabel.apply(item);
                label.setText(text);
                return label;
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    confirmSelection();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Constants.CONTENT_BG);
        CustomButton btnChoose = new CustomButton("Chon", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        CustomButton btnCancel = new CustomButton("Huy", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        btnChoose.addActionListener(e -> confirmSelection());
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnChoose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        loadAll();
    }

    private void loadAll() {
        listModel.clear();
        for (T item : items) {
            listModel.addElement(item);
        }
        if (!listModel.isEmpty()) {
            list.setSelectedIndex(0);
        }
    }

    private void applyFilter() {
        String keyword = txtKeyword.getText().trim().toLowerCase();
        String filter = (String) cboFilter.getSelectedItem();
        listModel.clear();

        for (T item : items) {
            String idText = String.valueOf(getId.apply(item));
            String labelText = getLabel.apply(item) != null ? getLabel.apply(item) : "";
            String labelLower = labelText.toLowerCase();

            boolean match;
            if ("Ma".equals(filter)) {
                match = idText.contains(keyword);
            } else if ("Ten".equals(filter)) {
                match = labelLower.contains(keyword);
            } else {
                match = idText.contains(keyword) || labelLower.contains(keyword);
            }

            if (keyword.isEmpty() || match) {
                listModel.addElement(item);
            }
        }

        if (!listModel.isEmpty()) {
            list.setSelectedIndex(0);
        }
    }

    private void confirmSelection() {
        T item = list.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot dong!");
            return;
        }
        selectedId = getId.apply(item);
        selectedLabel = getLabel.apply(item);
        selectedType = typeKey;
        dispose();
    }
}
