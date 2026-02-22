/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: XemDiemSinhVienPanel - Panel xem điểm sinh viên cho giảng viên
 * 
 * Giảng viên xem được tất cả điểm của sinh viên thuộc khoa mình
 * Kế thừa SearchPanel để tái sử dụng code GUI
 */
package gui.teacher;

import bus.BaiThiBUS;
import bus.DeThiBUS;
import bus.HocPhanBUS;
import bus.SinhVienBUS;
import config.Constants;
import dto.BaiThiDTO;
import dto.DeThiDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import dto.SinhVienDTO;
import gui.components.HeaderLabel;
import gui.components.SearchPanel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;

public class XemDiemSinhVienPanel extends SearchPanel {
    private GiangVienDTO nguoiDung;
    private BaiThiBUS baiThiBUS;
    private SinhVienBUS sinhVienBUS;
    private DeThiBUS deThiBUS;
    private HocPhanBUS hocPhanBUS;
    
    private JLabel lblThongKe;
    
    private static final String[] COLUMNS = {
        "Mã BT", "MSSV", "Họ tên SV", "Đề thi", "Môn học", 
        "Ngày thi", "Số câu đúng", "Số câu sai", "Điểm"
    };
    
    private static final String[] SEARCH_OPTIONS = {
        "Tất cả", "MSSV", "Họ tên SV", "Đề thi", "Môn học"
    };
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public XemDiemSinhVienPanel(GiangVienDTO nguoiDung) {
        super(COLUMNS, SEARCH_OPTIONS);
        this.nguoiDung = nguoiDung;
        this.baiThiBUS = new BaiThiBUS();
        this.sinhVienBUS = new SinhVienBUS();
        this.deThiBUS = new DeThiBUS();
        this.hocPhanBUS = new HocPhanBUS();
        
        initCustomUI();
    }
    
    private void initCustomUI() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header với tiêu đề và thống kê
        JPanel panelHeader = new JPanel(new BorderLayout(10, 10));
        panelHeader.setBackground(Constants.CONTENT_BG);
        
        HeaderLabel lblTieuDe = HeaderLabel.createPrimary("XEM ĐIỂM SINH VIÊN TRONG KHOA");
        panelHeader.add(lblTieuDe, BorderLayout.NORTH);
        
        // Thống kê
        lblThongKe = new JLabel("Tổng số bài thi: 0 | Điểm trung bình: 0.00");
        lblThongKe.setFont(Constants.NORMAL_FONT);
        lblThongKe.setForeground(Constants.PRIMARY_COLOR);
        
        JPanel panelThongKe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelThongKe.setBackground(Constants.CONTENT_BG);
        panelThongKe.add(lblThongKe);
        panelHeader.add(panelThongKe, BorderLayout.SOUTH);
        
        add(panelHeader, BorderLayout.NORTH);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // Mã BT
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // MSSV
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Họ tên
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Đề thi
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Môn học
        table.getColumnModel().getColumn(5).setPreferredWidth(90);  // Ngày thi
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // Số câu đúng
        table.getColumnModel().getColumn(7).setPreferredWidth(70);  // Số câu sai
        table.getColumnModel().getColumn(8).setPreferredWidth(60);  // Điểm
    }
    
    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        List<BaiThiDTO> danhSachBaiThi = baiThiBUS.getDanhSachBaiThiTheoKhoa(nguoiDung.getMaKhoa());
        
        float tongDiem = 0;
        int soBaiThi = danhSachBaiThi.size();
        
        for (BaiThiDTO bt : danhSachBaiThi) {
            addRowToTable(bt);
            tongDiem += bt.getDiemSo();
        }
        
        // Cập nhật thống kê
        float diemTrungBinh = soBaiThi > 0 ? tongDiem / soBaiThi : 0;
        lblThongKe.setText(String.format(
            "Tổng số bài thi: %d | Điểm trung bình: %.2f", 
            soBaiThi, diemTrungBinh
        ));
    }
    
    /**
     * Thêm một dòng vào bảng
     */
    private void addRowToTable(BaiThiDTO bt) {
        SinhVienDTO sv = sinhVienBUS.getById(bt.getMaSV());
        String hoTenSV = sv != null ? sv.getHoTen() : "";
        String mssv = sv != null ? sv.getTenDangNhap() : "";
        
        DeThiDTO deThi = deThiBUS.getById(bt.getMaDeThi());
        String tenDeThi = deThi != null ? deThi.getTenDeThi() : "";
        String tenHocPhan = deThi != null ? getTenHocPhan(deThi.getMaHocPhan()) : "";
        int tongSoCau = deThi != null ? deThi.getSoCauHoi() : 0;
        
        String ngayThi = bt.getNgayThi() != null ? dateFormat.format(bt.getNgayThi()) : "";
        
        String soCauDung = bt.getSoCauDung() + "/" + tongSoCau;
        
        tableModel.addRow(new Object[] {
            bt.getMaBaiThi(),
            mssv,
            hoTenSV,
            tenDeThi,
            tenHocPhan,
            ngayThi,
            soCauDung,
            bt.getSoCauSai(),
            String.format("%.1f", bt.getDiemSo())
        });
    }
    
    /**
     * Lấy tên học phần theo mã
     */
    private String getTenHocPhan(int maHocPhan) {
        HocPhanDTO hocPhan = hocPhanBUS.getById(maHocPhan);
        return hocPhan != null ? hocPhan.getTenMon() : "";
    }
    
    @Override
    protected void hienThiThongTin() {
        // Panel chỉ hiển thị, không cần xử lý chọn row
    }
    
    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loai = (String) cboLoaiTimKiem.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        // Gọi BUS để tìm kiếm
        List<BaiThiDTO> danhSach = baiThiBUS.timKiemDiemTheoKhoa(nguoiDung.getMaKhoa(), keyword, loai);
        
        float tongDiem = 0;
        int count = danhSach.size();
        
        for (BaiThiDTO bt : danhSach) {
            addRowToTable(bt);
            tongDiem += bt.getDiemSo();
        }
        
        // Cập nhật thống kê cho kết quả tìm kiếm
        float diemTrungBinh = count > 0 ? tongDiem / count : 0;
        String prefix = keyword.isEmpty() ? "Tổng số bài thi" : "Kết quả tìm kiếm";
        lblThongKe.setText(String.format(
            "%s: %d bài thi | Điểm trung bình: %.2f", 
            prefix, count, diemTrungBinh
        ));
    }
}
