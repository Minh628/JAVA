/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * Component: CrudButtonPanel - Panel chứa các nút CRUD dùng chung
 */
package gui.components;

import config.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CrudButtonPanel extends JPanel {
    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;

    public CrudButtonPanel() {
        this(Constants.CARD_COLOR);
    }
    
    public CrudButtonPanel(Color bgColor) {
        setLayout(new FlowLayout(FlowLayout.CENTER, Constants.PADDING_MEDIUM, Constants.PADDING_SMALL));
        setBackground(bgColor);
        
        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);
        
        add(btnThem);
        add(btnSua);
        add(btnXoa);
        add(btnLamMoi);
    }
    
    public void setThemListener(ActionListener listener) {
        btnThem.addActionListener(listener);
    }
    
    public void setSuaListener(ActionListener listener) {
        btnSua.addActionListener(listener);
    }
    
    public void setXoaListener(ActionListener listener) {
        btnXoa.addActionListener(listener);
    }
    
    public void setLamMoiListener(ActionListener listener) {
        btnLamMoi.addActionListener(listener);
    }
    
    // Thiết lập tất cả listener cùng lúc
    public void setAllListeners(ActionListener themListener, ActionListener suaListener, 
                                 ActionListener xoaListener, ActionListener lamMoiListener) {
        btnThem.addActionListener(themListener);
        btnSua.addActionListener(suaListener);
        btnXoa.addActionListener(xoaListener);
        btnLamMoi.addActionListener(lamMoiListener);
    }
    
    // Getter cho các button nếu cần customize thêm
    public CustomButton getBtnThem() { return btnThem; }
    public CustomButton getBtnSua() { return btnSua; }
    public CustomButton getBtnXoa() { return btnXoa; }
    public CustomButton getBtnLamMoi() { return btnLamMoi; }
    
    // Enable/Disable buttons
    public void setThemEnabled(boolean enabled) { btnThem.setEnabled(enabled); }
    public void setSuaEnabled(boolean enabled) { btnSua.setEnabled(enabled); }
    public void setXoaEnabled(boolean enabled) { btnXoa.setEnabled(enabled); }
}
