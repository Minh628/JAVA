/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: ThongKePanel - Panel thống kê cho Admin
 */
package gui.admin;

import bus.ThongKeBUS;
import com.toedter.calendar.JDateChooser;
import config.Constants;
import gui.components.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import util.PDFExporter;

public class ThongKePanel extends JPanel {
    private ThongKeBUS thongKeBUS;
    
    // Data hiện tại để export
    private Map<String, Object> currentTongQuanData;
    private List<Object[]> currentTableData;
    private String[] currentColumnNames;
    private String currentTieuDe;
    private Date currentTuNgay;
    private Date currentDenNgay;
    
    // Components bộ lọc
    private JComboBox<String> cboLoaiThoiGian;
    private JPanel pnlBoLoc;
    private CardLayout cardLayoutBoLoc;
    
    // Bộ lọc: Từ ngày - đến ngày
    private JDateChooser dcTuNgay;
    private JDateChooser dcDenNgay;
    
    // Bộ lọc: Tháng
    private JComboBox<String> cboThang;
    private JComboBox<Integer> cboNamThang;
    
    // Bộ lọc: Quý
    private JComboBox<String> cboQuy;
    private JComboBox<Integer> cboNamQuy;
    
    // Bộ lọc: Năm
    private JComboBox<Integer> cboNam;
    
    // Loại thống kê
    private JComboBox<String> cboLoaiThongKe;
    
    // Hiển thị
    private JPanel pnlKetQua;
    private CardLayout cardLayoutKetQua;
    private JTable tblThongKe;
    private DefaultTableModel modelThongKe;
    private SimpleBarChart barChart;
    private SimplePieChart pieChart;
    private JPanel pnlTongQuan;
    
    // Định dạng
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public ThongKePanel() {
        this.thongKeBUS = new ThongKeBUS();
        initComponents();
        loadDefaultData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ==================== HEADER ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        
        HeaderLabel lblTitle = HeaderLabel.createPrimary("📊 THỐNG KÊ KẾT QUẢ THI");
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);
        
        // ==================== BỘ LỌC ====================
        JPanel pnlBoLocWrapper = new JPanel(new BorderLayout(10, 10));
        pnlBoLocWrapper.setOpaque(false);
        pnlBoLocWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        pnlBoLocWrapper.setBackground(Color.WHITE);
        
        // Panel chứa các bộ lọc
        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlFilters.setOpaque(false);
        
        // 1. Loại thời gian
        pnlFilters.add(createLabel("Chọn theo:"));
        cboLoaiThoiGian = new JComboBox<>(new String[]{
            "Từ ngày → đến ngày", 
            "Tháng", 
            "Quý", 
            "Năm (4 quý)"
        });
        cboLoaiThoiGian.setFont(Constants.NORMAL_FONT);
        cboLoaiThoiGian.setPreferredSize(new Dimension(160, 30));
        cboLoaiThoiGian.addActionListener(e -> onLoaiThoiGianChanged());
        pnlFilters.add(cboLoaiThoiGian);
        
        // 2. Panel bộ lọc động (CardLayout)
        cardLayoutBoLoc = new CardLayout();
        pnlBoLoc = new JPanel(cardLayoutBoLoc);
        pnlBoLoc.setOpaque(false);
        
        pnlBoLoc.add(createPanelKhoangNgay(), "KHOANG_NGAY");
        pnlBoLoc.add(createPanelThang(), "THANG");
        pnlBoLoc.add(createPanelQuy(), "QUY");
        pnlBoLoc.add(createPanelNam(), "NAM");
        
        pnlFilters.add(pnlBoLoc);
        
        // 3. Loại thống kê
        pnlFilters.add(Box.createHorizontalStrut(20));
        pnlFilters.add(createLabel("Thống kê theo:"));
        cboLoaiThongKe = new JComboBox<>(new String[]{
            "Tổng quan",
            "Theo Khoa",
            "Theo Ngành", 
            "Theo Học phần",
            "Theo Kỳ thi",
            "Đề thi theo Quý",
            "Bài thi theo Quý",
            "Tỉ lệ đạt theo Quý",
            "Giảng viên theo Quý",
            "Sinh viên theo Quý",
            "Học phần theo Quý",
            "Sinh viên & Học phần",
            "Giảng viên & Học phần theo Năm"
            
        });
        cboLoaiThongKe.setFont(Constants.NORMAL_FONT);
        cboLoaiThongKe.setPreferredSize(new Dimension(220, 30));
        pnlFilters.add(cboLoaiThongKe);
        
        // 4. Nút thống kê
        JButton btnThongKe = new JButton("🔍 Thống kê");
        btnThongKe.setFont(Constants.BUTTON_FONT);
        btnThongKe.setBackground(Constants.PRIMARY_COLOR);
        btnThongKe.setForeground(Color.WHITE);
        btnThongKe.setFocusPainted(false);
        btnThongKe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThongKe.setPreferredSize(new Dimension(120, 32));
        btnThongKe.addActionListener(e -> executeThongKe());
        pnlFilters.add(btnThongKe);
        
        // 5. Nút xuất PDF
        JButton btnExportPDF = new JButton("📄 Xuất PDF");
        btnExportPDF.setFont(Constants.BUTTON_FONT);
        btnExportPDF.setBackground(Constants.DANGER_COLOR);
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.setFocusPainted(false);
        btnExportPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportPDF.setPreferredSize(new Dimension(120, 32));
        btnExportPDF.addActionListener(e -> exportPDF());
        pnlFilters.add(btnExportPDF);
        
        pnlBoLocWrapper.add(pnlFilters, BorderLayout.CENTER);
        add(pnlBoLocWrapper, BorderLayout.NORTH);
        
        // Wrapper cho header + bộ lọc
        JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
        pnlTop.setOpaque(false);
        pnlTop.add(pnlHeader, BorderLayout.NORTH);
        pnlTop.add(pnlBoLocWrapper, BorderLayout.CENTER);
        add(pnlTop, BorderLayout.NORTH);
        
        // ==================== KẾT QUẢ ====================
        cardLayoutKetQua = new CardLayout();
        pnlKetQua = new JPanel(cardLayoutKetQua);
        pnlKetQua.setOpaque(false);
        
        // Panel tổng quan với cards và charts
        pnlTongQuan = createPanelTongQuan();
        pnlKetQua.add(pnlTongQuan, "TONG_QUAN");
        
        // Panel bảng chi tiết
        JPanel pnlBang = createPanelBang();
        pnlKetQua.add(pnlBang, "BANG");
        
        add(pnlKetQua, BorderLayout.CENTER);
    }
    
    // ==================== Tạo các panel bộ lọc ====================
    
    private JPanel createPanelKhoangNgay() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        
        panel.add(createLabel("Từ:"));
        dcTuNgay = new JDateChooser();
        dcTuNgay.setDateFormatString("dd/MM/yyyy");
        dcTuNgay.setPreferredSize(new Dimension(130, 30));
        dcTuNgay.setDate(java.util.Date.from(
            LocalDate.now().minusMonths(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        panel.add(dcTuNgay);
        
        panel.add(createLabel("Đến:"));
        dcDenNgay = new JDateChooser();
        dcDenNgay.setDateFormatString("dd/MM/yyyy");
        dcDenNgay.setPreferredSize(new Dimension(130, 30));
        dcDenNgay.setDate(new java.util.Date());
        panel.add(dcDenNgay);
        
        return panel;
    }
    
    private JPanel createPanelThang() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        
        panel.add(createLabel("Tháng:"));
        cboThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cboThang.addItem("Tháng " + i);
        }
        cboThang.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cboThang.setFont(Constants.NORMAL_FONT);
        cboThang.setPreferredSize(new Dimension(100, 30));
        panel.add(cboThang);
        
        panel.add(createLabel("Năm:"));
        cboNamThang = createComboNam();
        panel.add(cboNamThang);
        
        return panel;
    }
    
    private JPanel createPanelQuy() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        
        panel.add(createLabel("Quý:"));
        cboQuy = new JComboBox<>(new String[]{"Quý 1", "Quý 2", "Quý 3", "Quý 4"});
        int currentQuarter = (LocalDate.now().getMonthValue() - 1) / 3;
        cboQuy.setSelectedIndex(currentQuarter);
        cboQuy.setFont(Constants.NORMAL_FONT);
        cboQuy.setPreferredSize(new Dimension(80, 30));
        panel.add(cboQuy);
        
        panel.add(createLabel("Năm:"));
        cboNamQuy = createComboNam();
        panel.add(cboNamQuy);
        
        return panel;
    }
    
    private JPanel createPanelNam() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        
        panel.add(createLabel("Năm:"));
        cboNam = createComboNam();
        panel.add(cboNam);
        
        return panel;
    }
    
    private JComboBox<Integer> createComboNam() {
        JComboBox<Integer> cbo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= currentYear - 5; i--) {
            cbo.addItem(i);
        }
        cbo.setFont(Constants.NORMAL_FONT);
        cbo.setPreferredSize(new Dimension(80, 30));
        return cbo;
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Constants.NORMAL_FONT);
        lbl.setForeground(Constants.TEXT_COLOR);
        return lbl;
    }
    
    // ==================== Panel kết quả ====================
    
    private JPanel createPanelTongQuan() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        
        // Cards thống kê nhanh (phía trên)
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlCards.setOpaque(false);
        pnlCards.setPreferredSize(new Dimension(0, 100));
        panel.add(pnlCards, BorderLayout.NORTH);
        
        // Charts (phía dưới)
        JPanel pnlCharts = new JPanel(new GridLayout(1, 2, 15, 0));
        pnlCharts.setOpaque(false);
        
        // Bar chart
        barChart = new SimpleBarChart("Điểm trung bình theo quý");
        barChart.setYAxisLabel("Điểm");
        barChart.setMaxValue(10.0);
        barChart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        pnlCharts.add(barChart);
        
        // Pie chart
        pieChart = new SimplePieChart("Tỷ lệ Đạt/Rớt");
        pieChart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        pnlCharts.add(pieChart);
        
        panel.add(pnlCharts, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPanelBang() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Bảng
        modelThongKe = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblThongKe = new CustomTable(modelThongKe);
        
        // Căn giữa các cột số
        JScrollPane scrollPane = new JScrollPane(tblThongKe);
        scrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER); 
        // Chart bên phải
        JPanel pnlChartRight = new JPanel(new BorderLayout());
        pnlChartRight.setPreferredSize(new Dimension(350, 0));
        pnlChartRight.setOpaque(false);
        
        SimpleBarChart chartRight = new SimpleBarChart("Biểu đồ");
        chartRight.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        chartRight.setBackground(Color.WHITE);
        pnlChartRight.add(chartRight, BorderLayout.CENTER);
        
        panel.add(pnlChartRight, BorderLayout.EAST);
        
        return panel;
    }
    
    // ==================== Events ====================
    
    private void onLoaiThoiGianChanged() {
        int index = cboLoaiThoiGian.getSelectedIndex();
        switch (index) {
            case 0: cardLayoutBoLoc.show(pnlBoLoc, "KHOANG_NGAY"); break;
            case 1: cardLayoutBoLoc.show(pnlBoLoc, "THANG"); break;
            case 2: cardLayoutBoLoc.show(pnlBoLoc, "QUY"); break;
            case 3: cardLayoutBoLoc.show(pnlBoLoc, "NAM"); break;
        }
    }
    
    private void executeThongKe() {
        // Lấy khoảng thời gian
        Date[] dates = getSelectedDates();
        if (dates == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thời gian hợp lệ!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Date tuNgay = dates[0];
        Date denNgay = dates[1];
        
        int loaiThongKe = cboLoaiThongKe.getSelectedIndex();
        
        switch (loaiThongKe) {
            case 0: // Tổng quan
                showThongKeTongQuan(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "TONG_QUAN");
                break;
            case 1: // Theo Khoa
                showThongKeTheoKhoa(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 2: // Theo Ngành
                showThongKeTheoNganh(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 3: // Theo Học phần
                showThongKeTheoHocPhan(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 4: // Theo Kỳ thi
                showThongKeTheoKyThi(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 5: // Đề thi theo Quý
                showThongKeDeThiTheoQuy(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 6: // Bài thi theo Quý
                showThongKeBaiThiTheoQuy(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 7: // Tỉ lệ đạt theo Quý
                showThongKeTyLeDatTheoQuy(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 8: // Giảng viên theo Quý
                showThongKeGiangVienTheoQuy(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 9: // Sinh viên theo Quý
                showThongKeSinhVienTheoQuy(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 10: // Học phần theo Quý
                showThongKeHocPhanTheoQuy(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 11: // Sinh viên & Học phần
                showThongKeSinhVienVaHocPhan(tuNgay, denNgay);
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
            case 12: // Giảng viên & Học phần theo Năm
                showThongKeGiangVienVaHocPhanTheoNam();
                cardLayoutKetQua.show(pnlKetQua, "BANG");
                break;
        }
    }
    
    private Date[] getSelectedDates() {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        
        switch (loaiThoiGian) {
            case 0: // Từ ngày - đến ngày
                if (dcTuNgay.getDate() == null || dcDenNgay.getDate() == null) {
                    return null;
                }
                return new Date[]{
                    new Date(dcTuNgay.getDate().getTime()),
                    new Date(dcDenNgay.getDate().getTime())
                };
                
            case 1: // Tháng
                int thang = cboThang.getSelectedIndex() + 1;
                int namThang = (Integer) cboNamThang.getSelectedItem();
                return thongKeBUS.getNgayTheoThang(thang, namThang);
                
            case 2: // Quý
                int quy = cboQuy.getSelectedIndex() + 1;
                int namQuy = (Integer) cboNamQuy.getSelectedItem();
                return thongKeBUS.getNgayTheoQuy(quy, namQuy);
                
            case 3: // Năm
                int nam = (Integer) cboNam.getSelectedItem();
                return thongKeBUS.getNgayTheoNam(nam);
        }
        
        return null;
    }
    
    // ==================== Hiển thị thống kê ====================
    
    private void showThongKeTongQuan(Date tuNgay, Date denNgay) {
        Map<String, Object> data = thongKeBUS.getThongKeTongQuan(tuNgay, denNgay);
        
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu trong khoảng thời gian này!");
            return;
        }
        
        // Lưu data để export
        this.currentTongQuanData = data;
        this.currentTuNgay = tuNgay;
        this.currentDenNgay = denNgay;
        this.currentTableData = null;
        
        // Cập nhật cards
        JPanel pnlCards = (JPanel) pnlTongQuan.getComponent(0);
        pnlCards.removeAll();
        
        int tongBaiThi = data.get("tongBaiThi") != null ? (int) data.get("tongBaiThi") : 0;
        int tongDeThi = data.get("tongDeThi") != null ? (int) data.get("tongDeThi") : 0;
        float diemTB = data.get("diemTrungBinh") != null ? ((Number) data.get("diemTrungBinh")).floatValue() : 0;
        double tyLeDat = data.get("tyLeDat") != null ? ((Number) data.get("tyLeDat")).doubleValue() : 0;
        int soDat = data.get("soDat") != null ? (int) data.get("soDat") : 0;
        int soRot = data.get("soRot") != null ? (int) data.get("soRot") : 0;
        
        pnlCards.add(createStatCard("📝", "Tổng bài thi", String.valueOf(tongBaiThi), Constants.PRIMARY_COLOR));
        pnlCards.add(createStatCard("📋", "Số đề thi", String.valueOf(tongDeThi), Constants.PURPLE_COLOR));
        pnlCards.add(createStatCard("📊", "Điểm TB", String.format("%.2f", diemTB), Constants.ORANGE_COLOR));
        pnlCards.add(createStatCard("✅", "Tỷ lệ đạt", String.format("%.1f%%", tyLeDat), Constants.SUCCESS_COLOR));
        
        pnlCards.revalidate();
        pnlCards.repaint();
        
        // Cập nhật pie chart
        pieChart.setData(
            Arrays.asList("Đạt", "Rớt"),
            Arrays.asList((double) soDat, (double) soRot)
        );
        
        // Cập nhật bar chart theo quý (nếu chọn năm)
        if (cboLoaiThoiGian.getSelectedIndex() == 3) {
            int nam = (Integer) cboNam.getSelectedItem();
            List<Object[]> dataQuy = thongKeBUS.getThongKeTheoQuy(nam);
            
            if (dataQuy != null && !dataQuy.isEmpty()) {
                List<String> labels = new ArrayList<>();
                List<Double> values = new ArrayList<>();
                
                for (Object[] row : dataQuy) {
                    labels.add("Q" + row[0]);
                    values.add(row[2] != null ? ((Number) row[2]).doubleValue() : 0.0);
                }
                
                barChart.setTitle("Điểm TB theo quý - Năm " + nam);
                barChart.setData(labels, values);
            }
        } else {
            // Hiển thị theo tháng
            barChart.setTitle("Điểm TB trong khoảng thời gian");
            barChart.setData(null, null);
        }
    }
    
    private void showThongKeTheoKhoa(Date tuNgay, Date denNgay) {
        List<Object[]> data = thongKeBUS.getThongKeTheoKhoa(tuNgay, denNgay);
        
        String[] columns = {"Mã Khoa", "Tên Khoa", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
        modelThongKe.setRowCount(0);
        modelThongKe.setColumnIdentifiers(columns);
        
        // Lưu data để export
        this.currentTableData = data;
        this.currentColumnNames = columns;
        this.currentTieuDe = "Thống kê theo Khoa";
        this.currentTuNgay = tuNgay;
        this.currentDenNgay = denNgay;
        this.currentTongQuanData = null;
        
        if (data != null) {
            for (Object[] row : data) {
                modelThongKe.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    String.format("%.2f", ((Number) row[3]).floatValue()),
                    String.format("%.1f", ((Number) row[4]).floatValue())
                });
            }
            
            updateBarChartFromTable(data, 1, 3, "Điểm TB theo Khoa");
        }
        
        applyCenterRenderer();
    }
    
    private void showThongKeTheoNganh(Date tuNgay, Date denNgay) {
        List<Object[]> data = thongKeBUS.getThongKeTheoNganh(tuNgay, denNgay);
        
        String[] columns = {"Mã Ngành", "Tên Ngành", "Thuộc Khoa", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
        modelThongKe.setRowCount(0);
        modelThongKe.setColumnIdentifiers(columns);
        
        // Lưu data để export
        this.currentTableData = data;
        this.currentColumnNames = columns;
        this.currentTieuDe = "Thống kê theo Ngành";
        this.currentTuNgay = tuNgay;
        this.currentDenNgay = denNgay;
        this.currentTongQuanData = null;
        
        if (data != null) {
            for (Object[] row : data) {
                modelThongKe.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    String.format("%.2f", ((Number) row[4]).floatValue()),
                    String.format("%.1f", ((Number) row[5]).floatValue())
                });
            }
            
            updateBarChartFromTable(data, 1, 4, "Điểm TB theo Ngành");
        }
        
        applyCenterRenderer();
    }
    
    private void showThongKeTheoHocPhan(Date tuNgay, Date denNgay) {
        List<Object[]> data = thongKeBUS.getThongKeTheoHocPhan(tuNgay, denNgay);
        
        String[] columns = {"Mã HP", "Tên Học phần", "Thuộc Khoa", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
        modelThongKe.setRowCount(0);
        modelThongKe.setColumnIdentifiers(columns);
        
        // Lưu data để export
        this.currentTableData = data;
        this.currentColumnNames = columns;
        this.currentTieuDe = "Thống kê theo Học phần";
        this.currentTuNgay = tuNgay;
        this.currentDenNgay = denNgay;
        this.currentTongQuanData = null;
        
        if (data != null) {
            for (Object[] row : data) {
                modelThongKe.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    String.format("%.2f", ((Number) row[4]).floatValue()),
                    String.format("%.1f", ((Number) row[5]).floatValue())
                });
            }
            
            updateBarChartFromTable(data, 1, 4, "Điểm TB theo Học phần");
        }
        
        applyCenterRenderer();
    }
    
    private void showThongKeTheoKyThi(Date tuNgay, Date denNgay) {
        List<Object[]> data = thongKeBUS.getThongKeTheoKyThi(tuNgay, denNgay);
        
        String[] columns = {"Mã Kỳ thi", "Tên Kỳ thi", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
        modelThongKe.setRowCount(0);
        modelThongKe.setColumnIdentifiers(columns);
        
        // Lưu data để export
        this.currentTableData = data;
        this.currentColumnNames = columns;
        this.currentTieuDe = "Thống kê theo Kỳ thi";
        this.currentTuNgay = tuNgay;
        this.currentDenNgay = denNgay;
        this.currentTongQuanData = null;
        
        if (data != null) {
            for (Object[] row : data) {
                modelThongKe.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    String.format("%.2f", ((Number) row[3]).floatValue()),
                    String.format("%.1f", ((Number) row[4]).floatValue())
                });
            }
            
            updateBarChartFromTable(data, 1, 3, "Điểm TB theo Kỳ thi");
        }
        
        applyCenterRenderer();
    }
    
    /**
     * Thống kê Giảng viên theo Quý (Mục 12.a - Nhân viên)
     * Nếu chọn "Năm": Hiển thị cross-tab Q1|Q2|Q3|Q4|TC
     * Nếu chọn loại khác: Hiển thị bảng thống kê theo khoảng thời gian
     */
    private void showThongKeGiangVienTheoQuy(Date tuNgay, Date denNgay) {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        
        if (loaiThoiGian == 3) { // Năm - hiển thị cross-tab 4 quý
            int nam = getSelectedNam();
            List<Object[]> data = thongKeBUS.getThongKeGiangVienTheoQuy(nam);
            
            String[] columns = {"Giảng viên", "Q1", "Q2", "Q3", "Q4", "TC"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Giảng viên theo Quý - Năm " + nam;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                int[] tongQuy = new int[5]; // Q1, Q2, Q3, Q4, TC
                
                for (Object[] row : data) {
                    modelThongKe.addRow(row);
                    for (int i = 1; i <= 5; i++) {
                        tongQuy[i-1] += ((Number) row[i]).intValue();
                    }
                }
                
                // Thêm hàng tổng cộng
                modelThongKe.addRow(new Object[]{"TỔNG CỘNG", tongQuy[0], tongQuy[1], tongQuy[2], tongQuy[3], tongQuy[4]});
                
                updateBarChartCrossTab(data, "Số bài thi theo Giảng viên");
            }
        } else { // Các loại khác: từ ngày-đến ngày, tháng, quý
            List<Object[]> data = thongKeBUS.getThongKeGiangVienTheoThoiGian(tuNgay, denNgay);
            
            String[] columns = {"Giảng viên", "Số đề thi", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Giảng viên (" + sdf.format(tuNgay) + " - " + sdf.format(denNgay) + ")";
            this.currentTuNgay = tuNgay;
            this.currentDenNgay = denNgay;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                for (Object[] row : data) {
                    modelThongKe.addRow(new Object[]{
                        row[0],
                        row[1],
                        row[2],
                        String.format("%.2f", ((Number) row[3]).floatValue()),
                        String.format("%.1f", ((Number) row[4]).floatValue())
                    });
                }
                
                updateBarChartFromTable(data, 0, 2, "Số bài thi theo Giảng viên");
            }
        }
        
        applyCenterRenderer();
    }
    
    /**
     * Thống kê Sinh viên theo Quý (Mục 12.a - Khách hàng)
     * Nếu chọn "Năm": Hiển thị cross-tab Q1|Q2|Q3|Q4|TC
     * Nếu chọn loại khác: Hiển thị bảng thống kê theo khoảng thời gian
     */
    private void showThongKeSinhVienTheoQuy(Date tuNgay, Date denNgay) {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        
        if (loaiThoiGian == 3) { // Năm - hiển thị cross-tab 4 quý
            int nam = getSelectedNam();
            List<Object[]> data = thongKeBUS.getThongKeSinhVienTheoQuy(nam);
            
            String[] columns = {"Sinh viên", "Q1", "Q2", "Q3", "Q4", "TC"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Sinh viên theo Quý - Năm " + nam;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                int[] tongQuy = new int[5]; // Q1, Q2, Q3, Q4, TC
                
                for (Object[] row : data) {
                    modelThongKe.addRow(row);
                    for (int i = 1; i <= 5; i++) {
                        tongQuy[i-1] += ((Number) row[i]).intValue();
                    }
                }
                
                // Thêm hàng tổng cộng
                modelThongKe.addRow(new Object[]{"TỔNG CỘNG", tongQuy[0], tongQuy[1], tongQuy[2], tongQuy[3], tongQuy[4]});
                
                updateBarChartCrossTab(data, "Số bài thi theo Sinh viên");
            }
        } else { // Các loại khác: từ ngày-đến ngày, tháng, quý
            List<Object[]> data = thongKeBUS.getThongKeSinhVienTheoThoiGian(tuNgay, denNgay);
            
            String[] columns = {"Sinh viên", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Sinh viên (" + sdf.format(tuNgay) + " - " + sdf.format(denNgay) + ")";
            this.currentTuNgay = tuNgay;
            this.currentDenNgay = denNgay;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                for (Object[] row : data) {
                    modelThongKe.addRow(new Object[]{
                        row[0],
                        row[1],
                        String.format("%.2f", ((Number) row[2]).floatValue()),
                        String.format("%.1f", ((Number) row[3]).floatValue())
                    });
                }
                
                updateBarChartFromTable(data, 0, 1, "Số bài thi theo Sinh viên");
            }
        }
        
        applyCenterRenderer();
    }
    
    /**
     * Thống kê Học phần theo Quý (Mục 12.a - Sản phẩm)
     * Nếu chọn "Năm": Hiển thị cross-tab Q1|Q2|Q3|Q4|TC
     * Nếu chọn loại khác: Hiển thị bảng thống kê theo khoảng thời gian
     */
    private void showThongKeHocPhanTheoQuy(Date tuNgay, Date denNgay) {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        
        if (loaiThoiGian == 3) { // Năm - hiển thị cross-tab 4 quý
            int nam = getSelectedNam();
            List<Object[]> data = thongKeBUS.getThongKeHocPhanTheoQuy(nam);
            
            String[] columns = {"Học phần", "Q1", "Q2", "Q3", "Q4", "TC"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Học phần theo Quý - Năm " + nam;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                int[] tongQuy = new int[5]; // Q1, Q2, Q3, Q4, TC
                
                for (Object[] row : data) {
                    modelThongKe.addRow(row);
                    for (int i = 1; i <= 5; i++) {
                        tongQuy[i-1] += ((Number) row[i]).intValue();
                    }
                }
                
                // Thêm hàng tổng cộng
                modelThongKe.addRow(new Object[]{"TỔNG CỘNG", tongQuy[0], tongQuy[1], tongQuy[2], tongQuy[3], tongQuy[4]});
            
                updateBarChartCrossTab(data, "Số bài thi theo Học phần");
            }
        } else { // Các loại khác: từ ngày-đến ngày, tháng, quý
            List<Object[]> data = thongKeBUS.getThongKeHocPhanTheoThoiGian(tuNgay, denNgay);
            
            String[] columns = {"Học phần", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Học phần (" + sdf.format(tuNgay) + " - " + sdf.format(denNgay) + ")";
            this.currentTuNgay = tuNgay;
            this.currentDenNgay = denNgay;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                for (Object[] row : data) {
                    modelThongKe.addRow(new Object[]{
                        row[0],
                        row[1],
                        String.format("%.2f", ((Number) row[2]).floatValue()),
                        String.format("%.1f", ((Number) row[3]).floatValue())
                    });
                }
                
                updateBarChartFromTable(data, 0, 1, "Số bài thi theo Học phần");
            }
        }
        
        applyCenterRenderer();
    }
    
    // ==================== THỐNG KÊ ĐỀ THI, BÀI THI, TỈ LỆ ĐẠT THEO QUÝ (MỤC 12.a) ====================
    
    /**
     * Thống kê Đề thi theo Quý
     * Nếu chọn "Năm": Hiển thị cross-tab Q1|Q2|Q3|Q4|TC
     * Nếu chọn loại khác: Hiển thị bảng thống kê theo khoảng thời gian
     */
    private void showThongKeDeThiTheoQuy(Date tuNgay, Date denNgay) {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        
        if (loaiThoiGian == 3) { // Năm - hiển thị cross-tab 4 quý
            int nam = getSelectedNam();
            List<Object[]> data = thongKeBUS.getThongKeDeThiTheoQuy(nam);
            
            String[] columns = {"Học phần", "Q1", "Q2", "Q3", "Q4", "TC"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Đề thi theo Quý - Năm " + nam;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                int[] tongQuy = new int[5]; // Q1, Q2, Q3, Q4, TC
                
                for (Object[] row : data) {
                    modelThongKe.addRow(row);
                    for (int i = 1; i <= 5; i++) {
                        tongQuy[i-1] += ((Number) row[i]).intValue();
                    }
                }
                
                // Thêm hàng tổng cộng
                modelThongKe.addRow(new Object[]{"TỔNG CỘNG", tongQuy[0], tongQuy[1], tongQuy[2], tongQuy[3], tongQuy[4]});
                
                updateBarChartCrossTab(data, "Số đề thi theo Học phần");
            }
        } else { // Các loại khác: từ ngày-đến ngày, tháng, quý
            List<Object[]> data = thongKeBUS.getThongKeDeThiTheoThoiGian(tuNgay, denNgay);
            
            String[] columns = {"Học phần", "Số đề thi", "Số bài thi", "Điểm TB"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Đề thi (" + sdf.format(tuNgay) + " - " + sdf.format(denNgay) + ")";
            this.currentTuNgay = tuNgay;
            this.currentDenNgay = denNgay;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                for (Object[] row : data) {
                    modelThongKe.addRow(new Object[]{
                        row[0],
                        row[1],
                        row[2],
                        String.format("%.2f", ((Number) row[3]).floatValue())
                    });
                }
                
                updateBarChartFromTable(data, 0, 1, "Số đề thi theo Học phần");
            }
        }
        
        applyCenterRenderer();
    }
    
    /**
     * Thống kê Bài thi theo Quý
     * Nếu chọn "Năm": Hiển thị cross-tab Q1|Q2|Q3|Q4|TC
     * Nếu chọn loại khác: Hiển thị bảng thống kê theo khoảng thời gian
     */
    private void showThongKeBaiThiTheoQuy(Date tuNgay, Date denNgay) {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        
        if (loaiThoiGian == 3) { // Năm - hiển thị cross-tab 4 quý
            int nam = getSelectedNam();
            List<Object[]> data = thongKeBUS.getThongKeBaiThiTheoQuy(nam);
            
            String[] columns = {"Học phần", "Q1", "Q2", "Q3", "Q4", "TC"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Bài thi theo Quý - Năm " + nam;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                int[] tongQuy = new int[5]; // Q1, Q2, Q3, Q4, TC
                
                for (Object[] row : data) {
                    modelThongKe.addRow(row);
                    for (int i = 1; i <= 5; i++) {
                        tongQuy[i-1] += ((Number) row[i]).intValue();
                    }
                }
                
                // Thêm hàng tổng cộng
                modelThongKe.addRow(new Object[]{"TỔNG CỘNG", tongQuy[0], tongQuy[1], tongQuy[2], tongQuy[3], tongQuy[4]});
                
                updateBarChartCrossTab(data, "Số bài thi theo Học phần");
            }
        } else { // Các loại khác: từ ngày-đến ngày, tháng, quý
            List<Object[]> data = thongKeBUS.getThongKeBaiThiTheoThoiGian(tuNgay, denNgay);
            
            String[] columns = {"Học phần", "Số bài thi", "Điểm TB", "Tỷ lệ đạt (%)"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Bài thi (" + sdf.format(tuNgay) + " - " + sdf.format(denNgay) + ")";
            this.currentTuNgay = tuNgay;
            this.currentDenNgay = denNgay;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                for (Object[] row : data) {
                    modelThongKe.addRow(new Object[]{
                        row[0],
                        row[1],
                        String.format("%.2f", ((Number) row[2]).floatValue()),
                        String.format("%.1f", ((Number) row[3]).floatValue())
                    });
                }
                
                updateBarChartFromTable(data, 0, 1, "Số bài thi theo Học phần");
            }
        }
        
        applyCenterRenderer();
    }
    
    /**
     * Thống kê Tỉ lệ đạt theo Quý
     * Nếu chọn "Năm": Hiển thị cross-tab Q1|Q2|Q3|Q4|TB (%)
     * Nếu chọn loại khác: Hiển thị bảng thống kê theo khoảng thời gian
     */
    private void showThongKeTyLeDatTheoQuy(Date tuNgay, Date denNgay) {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        
        if (loaiThoiGian == 3) { // Năm - hiển thị cross-tab 4 quý
            int nam = getSelectedNam();
            List<Object[]> data = thongKeBUS.getThongKeTyLeDatTheoQuy(nam);
            
            String[] columns = {"Học phần", "Q1 (%)", "Q2 (%)", "Q3 (%)", "Q4 (%)", "TB (%)"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Tỉ lệ đạt theo Quý - Năm " + nam;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                for (Object[] row : data) {
                    modelThongKe.addRow(new Object[]{
                        row[0],
                        String.format("%.1f", ((Number) row[1]).floatValue()),
                        String.format("%.1f", ((Number) row[2]).floatValue()),
                        String.format("%.1f", ((Number) row[3]).floatValue()),
                        String.format("%.1f", ((Number) row[4]).floatValue()),
                        String.format("%.1f", ((Number) row[5]).floatValue())
                    });
                }
                
                // Cập nhật chart với tỉ lệ đạt trung bình
                List<String> labels = new ArrayList<>();
                List<Double> values = new ArrayList<>();
                int count = Math.min(data.size(), 10);
                for (int i = 0; i < count; i++) {
                    Object[] row = data.get(i);
                    String label = row[0] != null ? row[0].toString() : "-";
                    if (label.length() > 10) {
                        label = label.substring(0, 8) + "..";
                    }
                    labels.add(label);
                    values.add(((Number) row[5]).doubleValue());
                }
                
                JPanel pnlBang = (JPanel) pnlKetQua.getComponent(1);
                JPanel pnlChartRight = (JPanel) pnlBang.getComponent(1);
                SimpleBarChart chart = (SimpleBarChart) pnlChartRight.getComponent(0);
                chart.setTitle("Tỉ lệ đạt theo Học phần (%)");
                chart.setYAxisLabel("%");
                chart.setMaxValue(100.0);
                chart.setData(labels, values);
            }
        } else { // Các loại khác: từ ngày-đến ngày, tháng, quý
            List<Object[]> data = thongKeBUS.getThongKeTyLeDatTheoThoiGian(tuNgay, denNgay);
            
            String[] columns = {"Học phần", "Số bài thi", "Đạt", "Rớt", "Tỷ lệ đạt (%)"};
            modelThongKe.setRowCount(0);
            modelThongKe.setColumnIdentifiers(columns);
            
            // Lưu data để export
            this.currentTableData = data;
            this.currentColumnNames = columns;
            this.currentTieuDe = "Thống kê Tỉ lệ đạt (" + sdf.format(tuNgay) + " - " + sdf.format(denNgay) + ")";
            this.currentTuNgay = tuNgay;
            this.currentDenNgay = denNgay;
            this.currentTongQuanData = null;
            
            if (data != null && !data.isEmpty()) {
                for (Object[] row : data) {
                    modelThongKe.addRow(new Object[]{
                        row[0],
                        row[1],
                        row[2],
                        row[3],
                        String.format("%.1f", ((Number) row[4]).floatValue())
                    });
                }
                
                updateBarChartFromTable(data, 0, 4, "Tỉ lệ đạt theo Học phần (%)");
            }
        }
        
        applyCenterRenderer();
    }
    
    // ==================== THỐNG KÊ NHIỀU KHÓA (MỤC 12.b) ====================
    
    /**
     * Thống kê Sinh viên và Học phần (Mục 12.b.ii)
     */
    private void showThongKeSinhVienVaHocPhan(Date tuNgay, Date denNgay) {
        List<Object[]> data = thongKeBUS.getThongKeSinhVienVaHocPhan(tuNgay, denNgay);
        
        String[] columns = {"Mã SV", "Họ tên SV", "Tên Môn", "Số bài thi", "Điểm TB"};
        modelThongKe.setRowCount(0);
        modelThongKe.setColumnIdentifiers(columns);
        
        // Lưu data để export
        this.currentTableData = data;
        this.currentColumnNames = columns;
        this.currentTieuDe = "Thống kê Sinh viên & Học phần";
        this.currentTuNgay = tuNgay;
        this.currentDenNgay = denNgay;
        this.currentTongQuanData = null;
        
        if (data != null) {
            for (Object[] row : data) {
                modelThongKe.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4] != null ? String.format("%.2f", ((Number) row[4]).floatValue()) : "-"
                });
            }
            
            updateBarChartFromTable(data, 1, 4, "Điểm TB theo SV & Môn");
        }
        
        applyCenterRenderer();
    }
    
    /**
     * Thống kê Giảng viên và Học phần theo Năm (Mục 12.b.iii)
     */
    private void showThongKeGiangVienVaHocPhanTheoNam() {
        int nam = getSelectedNam();
        List<Object[]> data = thongKeBUS.getThongKeGiangVienVaHocPhanTheoNam(nam);
        
        String[] columns = {"Giảng viên", "Học phần", "Năm", "Số đề thi", "Số bài thi", "Điểm TB"};
        modelThongKe.setRowCount(0);
        modelThongKe.setColumnIdentifiers(columns);
        
        // Lưu data để export
        this.currentTableData = data;
        this.currentColumnNames = columns;
        this.currentTieuDe = "Thống kê Giảng viên & Học phần - Năm " + nam;
        this.currentTongQuanData = null;
        
        if (data != null) {
            for (Object[] row : data) {
                modelThongKe.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5] != null ? String.format("%.2f", ((Number) row[5]).floatValue()) : "-"
                });
            }
            
            updateBarChartFromTable(data, 0, 5, "Điểm TB theo GV & Môn");
        }
        
        applyCenterRenderer();
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Lấy năm đã chọn từ các combo box
     */
    private int getSelectedNam() {
        int loaiThoiGian = cboLoaiThoiGian.getSelectedIndex();
        switch (loaiThoiGian) {
            case 1: // Tháng
                return (Integer) cboNamThang.getSelectedItem();
            case 2: // Quý
                return (Integer) cboNamQuy.getSelectedItem();
            case 3: // Năm
                return (Integer) cboNam.getSelectedItem();
            default: // Khoảng ngày - lấy năm hiện tại
                return LocalDate.now().getYear();
        }
    }
    
    /**
     * Cập nhật bar chart cho bảng cross-tabulation
     */
    private void updateBarChartCrossTab(List<Object[]> data, String title) {
        if (data == null || data.isEmpty()) return;
        
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        
        // Lấy tối đa 10 mục, dựa trên cột TC (index 5)
        int count = Math.min(data.size(), 10);
        for (int i = 0; i < count; i++) {
            Object[] row = data.get(i);
            String label = row[0] != null ? row[0].toString() : "-";
            if (label.length() > 10) {
                label = label.substring(0, 8) + "..";
            }
            labels.add(label);
            
            // Lấy giá trị TC (cột cuối)
            double value = row[5] != null ? ((Number) row[5]).doubleValue() : 0;
            values.add(value);
        }
        
        // Tìm chart trong panel BANG
        JPanel pnlBang = (JPanel) pnlKetQua.getComponent(1);
        JPanel pnlChartRight = (JPanel) pnlBang.getComponent(1);
        SimpleBarChart chart = (SimpleBarChart) pnlChartRight.getComponent(0);
        
        chart.setTitle(title);
        chart.setYAxisLabel("Số lượng");
        chart.setData(labels, values);
    }
    
    private void updateBarChartFromTable(List<Object[]> data, int labelIndex, int valueIndex, String title) {
        if (data == null || data.isEmpty()) return;
        
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        
        // Lấy tối đa 10 mục
        int count = Math.min(data.size(), 10);
        for (int i = 0; i < count; i++) {
            Object[] row = data.get(i);
            String label = row[labelIndex] != null ? row[labelIndex].toString() : "-";
            // Rút gọn nhãn
            if (label.length() > 10) {
                label = label.substring(0, 8) + "..";
            }
            labels.add(label);
            
            double value = row[valueIndex] != null ? ((Number) row[valueIndex]).doubleValue() : 0;
            values.add(value);
        }
        
        // Tìm chart trong panel BANG
        JPanel pnlBang = (JPanel) pnlKetQua.getComponent(1);
        JPanel pnlChartRight = (JPanel) pnlBang.getComponent(1);
        SimpleBarChart chart = (SimpleBarChart) pnlChartRight.getComponent(0);
        
        chart.setTitle(title);
        chart.setData(labels, values);
    }
    
    private void applyCenterRenderer() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < tblThongKe.getColumnCount(); i++) {
            // Căn trái cột đầu (tên), căn giữa các cột còn lại
            if (i > 1) {
                tblThongKe.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }
    
    private JPanel createStatCard(String icon, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        
        JPanel pnlText = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlText.setOpaque(false);
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(color);
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(Constants.NORMAL_FONT);
        lblTitle.setForeground(Constants.TEXT_SECONDARY);
        
        pnlText.add(lblValue);
        pnlText.add(lblTitle);
        
        card.add(lblIcon, BorderLayout.WEST);
        card.add(pnlText, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadDefaultData() {
        // Load dữ liệu tổng quan mặc định
        Date[] dates = getSelectedDates();
        if (dates != null) {
            showThongKeTongQuan(dates[0], dates[1]);
        }
    }
    
    private void exportPDF() {
        if (currentTuNgay == null || currentDenNgay == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng thực hiện thống kê trước khi xuất báo cáo!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int loaiThongKe = cboLoaiThongKe.getSelectedIndex();
        
        if (loaiThongKe == 0 && currentTongQuanData != null) {
            // Xuất thống kê tổng quan
            PDFExporter.exportThongKeTongQuan(this, currentTongQuanData, 
                currentTuNgay, currentDenNgay);
        } else if (currentTableData != null && currentColumnNames != null) {
            // Dùng chung cho TẤT CẢ các loại bảng còn lại
            PDFExporter.exportThongKeTheoNhom(this, currentTieuDe, 
                currentColumnNames, currentTableData, currentTuNgay, currentDenNgay);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không có dữ liệu để xuất!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}
