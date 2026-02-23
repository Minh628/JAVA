/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: ThiTracNghiemPanel - Panel chọn đề thi và vào thi
 * 
 * MÔ TẢ:
 *   - Hiển thị danh sách đề thi sinh viên có thể làm
 *   - Lọc theo kỳ thi
 *   - Bấm "Vào thi" để mở LamBaiThiFrame
 * 
 * LUỒNG THAO TÁC:
 *   1. Chọn kỳ thi (ComboBox) -> Load danh sách đề thi
 *   2. Chọn đề thi trong bảng
 *   3. Bấm "Vào thi" -> Kiểm tra đã thi chưa
 *   4. Nếu chưa thi -> Tạo bài thi mới + Mở LamBaiThiFrame
 *   5. Nếu đã thi -> Thông báo không cho thi lại
 * 
 * ĐIỀU KIỆN HIỂN ĐỀ THI:
 *   - Đề thi đã publish (trangThai = true)
 *   - Thuộc học phần của ngành sinh viên đang học
 *   - Kỳ thi đang diễn ra (ngày hiện tại nằm trong khoảng)
 * 
 * COMPONENTS SỬ DỤNG:
 *   - CustomTable: Bảng danh sách đề thi
 *   - CustomButton: Nút "Vào thi", "Làm mới"
 * 
 * @see LamBaiThiFrame - Màn hình làm bài thi
 * @see BaiThiBUS - Tạo bài thi mới
 * ===========================================================================
 */
package gui.student;

import bus.BaiThiBUS;
import bus.DeThiBUS;
import bus.HocPhanBUS;
import bus.KyThiBUS;
import bus.NganhBUS;
import bus.SinhVienBUS;
import config.Constants;
import dto.DeThiDTO;
import dto.HocPhanDTO;
import dto.KyThiDTO;
import dto.NganhDTO;
import dto.SinhVienDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.IconHelper;

public class ThiTracNghiemPanel extends JPanel {
    private SinhVienDTO nguoiDung;
    private SinhVienBUS sinhVienBUS;
    private BaiThiBUS baiThiBUS;
    private KyThiBUS kyThiBUS;
    private DeThiBUS deThiBUS;
    private HocPhanBUS hocPhanBUS;
    private NganhBUS nganhBUS;
    private StudentDashboard parentFrame;

    private CustomTable tblDeThi;
    private DefaultTableModel modelDeThi;
    private JComboBox<KyThiDTO> cboKyThi;

    public ThiTracNghiemPanel(SinhVienDTO nguoiDung, StudentDashboard parentFrame) {
        this.nguoiDung = nguoiDung;
        this.parentFrame = parentFrame;
        this.sinhVienBUS = new SinhVienBUS();
        this.baiThiBUS = new BaiThiBUS();
        this.kyThiBUS = new KyThiBUS();
        this.deThiBUS = new DeThiBUS();
        this.hocPhanBUS = new HocPhanBUS();
        this.nganhBUS = new NganhBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header với combobox kỳ thi
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelHeader.setBackground(Constants.CONTENT_BG);

        JLabel lblTieuDe = new JLabel("DANH SÁCH ĐỀ THI");
        lblTieuDe.setIcon(IconHelper.createIcon(Constants.ICON_FILE_TEXT, Constants.ICON_SIZE_HEADER, Constants.PRIMARY_COLOR));
        lblTieuDe.setIconTextGap(10);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);
        panelHeader.add(lblTieuDe);

        panelHeader.add(Box.createHorizontalStrut(30));
        JLabel lblKyThi = new JLabel("Kỳ thi:");
        lblKyThi.setFont(Constants.NORMAL_FONT);
        panelHeader.add(lblKyThi);

        cboKyThi = new JComboBox<>();
        cboKyThi.setPreferredSize(new Dimension(300, 32));
        cboKyThi.setFont(Constants.NORMAL_FONT);
        cboKyThi.addActionListener(e -> loadDeThi());
        panelHeader.add(cboKyThi);

        add(panelHeader, BorderLayout.NORTH);

        // Bảng đề thi
        String[] columns = { "Mã đề", "Tên đề thi", "Môn học", "Số câu", "Thời gian (phút)", "Trạng thái" };
        modelDeThi = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDeThi = new CustomTable(modelDeThi);

        JScrollPane scrollPane = new JScrollPane(tblDeThi);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        // Nút vào thi
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNut.setBackground(Constants.CONTENT_BG);

        CustomButton btnVaoThi = new CustomButton("VÀO THI", Constants.SUCCESS_COLOR, Color.BLACK);
        btnVaoThi.setIcon(IconHelper.createIcon(Constants.ICON_CHECK, Constants.ICON_SIZE_NORMAL, Color.BLACK));
        btnVaoThi.setPreferredSize(new Dimension(150, 45));
        btnVaoThi.addActionListener(e -> vaoThi());
        panelNut.add(btnVaoThi);

        CustomButton btnLamMoi = new CustomButton("Làm mới", Constants.PRIMARY_COLOR, Color.BLACK);
        btnLamMoi.setIcon(IconHelper.createIcon(Constants.ICON_REFRESH, Constants.ICON_SIZE_NORMAL, Color.BLACK));
        btnLamMoi.setPreferredSize(new Dimension(150, 45));
        btnLamMoi.addActionListener(e -> loadData());
        panelNut.add(btnLamMoi);

        add(panelNut, BorderLayout.SOUTH);
    }

    private void loadData() {
        loadKyThi();
    }

    private void loadKyThi() {
        cboKyThi.removeAllItems();
        // Gọi KyThiBUS để lấy kỳ thi đang diễn ra
        List<KyThiDTO> danhSach = kyThiBUS.getKyThiDangDienRa();
        if (danhSach != null) {
            for (KyThiDTO kt : danhSach) {
                cboKyThi.addItem(kt);
            }
        }
    }

    public void loadDeThi() {
        modelDeThi.setRowCount(0);
        KyThiDTO kyThiChon = (KyThiDTO) cboKyThi.getSelectedItem();
        if (kyThiChon == null)
            return;

        // Lấy mã khoa từ mã ngành của sinh viên
        int maKhoa = getMaKhoaFromMaNganh(nguoiDung.getMaNganh());
        // Lấy đề thi theo khoa của sinh viên - gọi DeThiBUS
        List<DeThiDTO> danhSach = deThiBUS.getDeThiTheoKyThiVaKhoa(kyThiChon.getMaKyThi(), maKhoa);
        if (danhSach != null) {
            for (DeThiDTO dt : danhSach) {
                // Kiểm tra đã thi chưa - gọi BaiThiBUS
                boolean daThi = baiThiBUS.daDuThi(dt.getMaDeThi(), nguoiDung.getMaSV());
                modelDeThi.addRow(new Object[] {
                        dt.getMaDeThi(), dt.getTenDeThi(), getTenHocPhan(dt.getMaHocPhan()),
                        dt.getSoCauHoi(), dt.getThoiGianLam(),
                        daThi ? "Đã thi" : "Chưa thi"
                });
            }
        }
    }

    private void vaoThi() {
        int row = tblDeThi.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi!");
            return;
        }

        String trangThai = (String) modelDeThi.getValueAt(row, 5);
        if ("Đã thi".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Bạn đã thi đề này rồi!");
            return;
        }

        int maDeThi = (int) modelDeThi.getValueAt(row, 0);
        String tenDeThi = (String) modelDeThi.getValueAt(row, 1);
        int soCau = (int) modelDeThi.getValueAt(row, 3);
        int thoiGian = (int) modelDeThi.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Bạn chuẩn bị thi:\n- Đề thi: %s\n- Số câu: %d\n- Thời gian: %d phút\n\nBắt đầu thi?",
                        tenDeThi, soCau, thoiGian),
                "Xác nhận vào thi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int maBaiThi = baiThiBUS.batDauLamBai(maDeThi, nguoiDung.getMaSV());
            if (maBaiThi > 0) {
                parentFrame.setVisible(false);
                LamBaiThiFrame lamBaiThiFrame = new LamBaiThiFrame(parentFrame, maBaiThi, maDeThi, thoiGian);
                lamBaiThiFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không thể bắt đầu thi. Vui lòng thử lại!");
            }
        }
    }

    private int getMaKhoaFromMaNganh(int maNganh) {
        NganhDTO nganh = nganhBUS.getById(maNganh);
        return nganh != null ? nganh.getMaKhoa() : 0;
    }

    private String getTenHocPhan(int maHocPhan) {
        HocPhanDTO hocPhan = hocPhanBUS.getById(maHocPhan);
        return hocPhan != null ? hocPhan.getTenMon() : "";
    }
}
