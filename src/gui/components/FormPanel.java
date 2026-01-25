/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Component: FormPanel - Panel form nhập liệu dùng chung
 */
package gui.components;

import config.Constants;
import javax.swing.*;
import java.awt.*;

public class FormPanel extends JPanel {
    private GridBagConstraints gbc;
    private int currentRow = 0;
    
    public FormPanel(String title) {
        setLayout(new GridBagLayout());
        setBackground(Constants.CARD_COLOR);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
            title
        ));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constants.INSET_SMALL, Constants.PADDING_SMALL, 
                                Constants.INSET_SMALL, Constants.PADDING_SMALL);
        gbc.anchor = GridBagConstraints.WEST;
    }
    
    /**
     * Thêm một label với font chuẩn
     */
    public JLabel addLabel(String text, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        
        JLabel label = new JLabel(text);
        label.setFont(Constants.NORMAL_FONT);
        add(label, gbc);
        return label;
    }
    
    /**
     * Thêm một text field
     */
    public JTextField addTextField(int columns, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        
        JTextField textField = new JTextField(columns);
        textField.setFont(Constants.NORMAL_FONT);
        add(textField, gbc);
        return textField;
    }
    
    /**
     * Thêm một password field
     */
    public JPasswordField addPasswordField(int columns, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(Constants.NORMAL_FONT);
        add(passwordField, gbc);
        return passwordField;
    }
    
    /**
     * Thêm một combobox
     */
    public <T> JComboBox<T> addComboBox(int width, int gridx, int gridy) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setPreferredSize(new Dimension(width, 28));
        comboBox.setFont(Constants.NORMAL_FONT);
        add(comboBox, gbc);
        return comboBox;
    }
    
    /**
     * Thêm một row gồm label + field
     */
    public JTextField addFormRow(String labelText, int fieldColumns) {
        addLabel(labelText, 0, currentRow);
        JTextField field = addTextField(fieldColumns, 1, currentRow);
        currentRow++;
        return field;
    }
    
    /**
     * Thêm component vào vị trí chỉ định
     */
    public void addComponent(Component component, int gridx, int gridy, int gridwidth) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.anchor = GridBagConstraints.CENTER;
        add(component, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
    }
    
    /**
     * Lấy GridBagConstraints để tùy chỉnh thêm
     */
    public GridBagConstraints getGbc() {
        return gbc;
    }
    
    /**
     * Lấy row hiện tại
     */
    public int getCurrentRow() {
        return currentRow;
    }
    
    /**
     * Set row hiện tại
     */
    public void setCurrentRow(int row) {
        this.currentRow = row;
    }
}
