/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI Component: CrudToolbar - Thanh công cụ CRUD tái sử dụng
 */
package gui.components;

import config.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Component toolbar cho các thao tác CRUD (Thêm, Sửa, Xóa)
 * Có thể tái sử dụng cho nhiều panel khác nhau
 */
public class CrudToolbar extends JPanel {
    
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    
    /**
     * Tạo toolbar với các nút mặc định: Thêm, Sửa, Xóa
     */
    public CrudToolbar() {
        this("Thêm", "Sửa", "Xóa");
    }
    
    /**
     * Tạo toolbar với text tùy chỉnh
     * @param addText Text cho nút Thêm
     * @param editText Text cho nút Sửa
     * @param deleteText Text cho nút Xóa
     */
    public CrudToolbar(String addText, String editText, String deleteText) {
        initComponents(addText, editText, deleteText);
    }
    
    private void initComponents(String addText, String editText, String deleteText) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 15));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        
        btnAdd = createButton("➕", addText, Constants.SUCCESS_COLOR);
        btnEdit = createButton("✏️", editText, Constants.PRIMARY_COLOR);
        btnDelete = createButton("🗑️", deleteText, Constants.DANGER_COLOR);
        
        add(btnAdd);
        add(btnEdit);
        add(btnDelete);
    }
    
    /**
     * Tạo nút với style chuẩn
     */
    private JButton createButton(String icon, String text, Color bgColor) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(Constants.BUTTON_FONT);
        btn.setForeground(Constants.TEXT_COLOR);
        btn.setBackground(bgColor);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color hoverColor = bgColor.darker();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    /**
     * Thêm nút tùy chỉnh vào toolbar
     * @param icon Icon emoji
     * @param text Text hiển thị
     * @param bgColor Màu nền
     * @return JButton đã tạo
     */
    public JButton addCustomButton(String icon, String text, Color bgColor) {
        JButton btn = createButton(icon, text, bgColor);
        add(btn);
        return btn;
    }
    
    /**
     * Thêm nút với màu mặc định (TOOLBAR_BTN)
     */
    public JButton addCustomButton(String icon, String text) {
        return addCustomButton(icon, text, Constants.TOOLBAR_BTN);
    }
    
    // === Getter cho các nút ===
    public JButton getAddButton() { return btnAdd; }
    public JButton getEditButton() { return btnEdit; }
    public JButton getDeleteButton() { return btnDelete; }
    
    // === Setter cho ActionListener ===
    public void setAddAction(ActionListener action) {
        btnAdd.addActionListener(action);
    }
    
    public void setEditAction(ActionListener action) {
        btnEdit.addActionListener(action);
    }
    
    public void setDeleteAction(ActionListener action) {
        btnDelete.addActionListener(action);
    }
    
    /**
     * Set tất cả action cùng lúc
     */
    public void setActions(ActionListener addAction, ActionListener editAction, ActionListener deleteAction) {
        if (addAction != null) btnAdd.addActionListener(addAction);
        if (editAction != null) btnEdit.addActionListener(editAction);
        if (deleteAction != null) btnDelete.addActionListener(deleteAction);
    }
}
