/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyDeThiPanel - Panel quản lý đề thi cho giảng viên
 * 
 * Sử dụng BUS chuyên biệt:
 * - DeThiBUS: Quản lý đề thi và chi tiết đề thi
 * - HocPhanBUS: Lấy danh sách học phần
 * - KyThiBUS: Lấy danh sách kỳ thi
 * - CauHoiBUS: Lấy danh sách câu hỏi
 * 
 * Luồng thao tác:
 * 1. Thêm đề thi: Tạo vỏ đề thi (metadata) trước
 * 2. Chọn đề thi -> Bấm "Quản lý câu hỏi" để thêm/xóa câu hỏi
 * 3. Xóa đề thi: Kiểm tra có bài thi chưa -> Nếu có thì không cho xóa
 */
package gui.teacher;

import bus.BaiThiBUS;
import bus.CauHoiBUS;
import bus.ChiTietDeThiBUS;
import bus.DeThiBUS;
import bus.HocPhanBUS;
import bus.KyThiBUS;
import config.Constants;
import dto.CauHoiDTO;
import dto.DeThiDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import dto.KyThiDTO;
import gui.components.CustomButton;
import gui.components.CustomTable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class QuanLyDeThiPanel extends JPanel {
    private GiangVienDTO nguoiDung;
    private DeThiBUS deThiBUS;
    private HocPhanBUS hocPhanBUS;
    private KyThiBUS kyThiBUS;
    private CauHoiBUS cauHoiBUS;
    private BaiThiBUS baiThiBUS;
    private ChiTietDeThiBUS chiTietDeThiBUS;

    private CustomTable tblDeThi;
    private DefaultTableModel modelDeThi;

    // Form fields
    private JTextField txtTenDeThi;
    private JComboBox<HocPhanDTO> cboHocPhan;
    private JComboBox<KyThiDTO> cboKyThi;
    private JSpinner spnThoiGian;

    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;
    private CustomButton btnTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;
    private CustomButton btnQuanLyCauHoi;

    private int selectedMaDeThi = -1;

    public QuanLyDeThiPanel(GiangVienDTO nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.deThiBUS = new DeThiBUS();
        this.hocPhanBUS = new HocPhanBUS();
        this.kyThiBUS = new KyThiBUS();
        this.cauHoiBUS = new CauHoiBUS();
        this.baiThiBUS = new BaiThiBUS();
        this.chiTietDeThiBUS = new ChiTietDeThiBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.CONTENT_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ ĐỀ THI", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.CARD_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Constants.LIGHT_COLOR),
                "Thông tin đề thi"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Tên đề thi
        gbc.gridx = 0;
        gbc.gridy = 0;
        addLabel(panelForm, "Tên đề thi:", gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenDeThi = new JTextField(30);
        txtTenDeThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenDeThi, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        addLabel(panelForm, "Học phần:", gbc);
        gbc.gridx = 3;
        cboHocPhan = new JComboBox<>();
        cboHocPhan.setPreferredSize(new Dimension(200, 28));
        cboHocPhan.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboHocPhan, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        addLabel(panelForm, "Kỳ thi:", gbc);
        gbc.gridx = 1;
        cboKyThi = new JComboBox<>();
        cboKyThi.setPreferredSize(new Dimension(200, 28));
        cboKyThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboKyThi, gbc);

        gbc.gridx = 2;
        addLabel(panelForm, "Thời gian (phút):", gbc);
        gbc.gridx = 3;
        spnThoiGian = new JSpinner(new SpinnerNumberModel(45, 10, 180, 5));
        spnThoiGian.setPreferredSize(new Dimension(80, 28));
        spnThoiGian.setFont(Constants.NORMAL_FONT);
        panelForm.add(spnThoiGian, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelNut.setBackground(Constants.CARD_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);
        btnQuanLyCauHoi = new CustomButton("Quản lý câu hỏi", new Color(128, 0, 128), Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themDeThi());
        btnSua.addActionListener(e -> suaDeThi());
        btnXoa.addActionListener(e -> xoaDeThi());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnQuanLyCauHoi.addActionListener(e -> moQuanLyCauHoi());

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);
        panelNut.add(btnQuanLyCauHoi);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForm.add(panelNut, gbc);

        // Panel trên: Tiêu đề + Form
        JPanel panelTren = new JPanel(new BorderLayout(0, 10));
        panelTren.setBackground(Constants.CONTENT_BG);
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);

        // Bảng
        String[] columns = { "Mã đề", "Tên đề thi", "Học phần", "Kỳ thi", "Số câu", "Thời gian" };
        modelDeThi = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDeThi = new CustomTable(modelDeThi);
        tblDeThi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblDeThi);
        scrollPane.getViewport().setBackground(Constants.CARD_COLOR);

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.CONTENT_BG);

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã đề", "Tên đề thi", "Học phần", "Kỳ thi" });
        cboLoaiTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(cboLoaiTimKiem);

        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(Constants.NORMAL_FONT);
        txtTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(txtTimKiem);

        btnTimKiem = new CustomButton("Tìm", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnTimKiem.addActionListener(e -> timKiem());
        panelTimKiem.add(btnTimKiem);

        CustomButton btnHienTatCa = new CustomButton("Hiện tất cả", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);
        btnHienTatCa.addActionListener(e -> {
            txtTimKiem.setText("");
            loadDeThi();
        });
        panelTimKiem.add(btnHienTatCa);

        // Panel center chứa tìm kiếm và bảng
        JPanel panelCenter = new JPanel(new BorderLayout(0, 5));
        panelCenter.setBackground(Constants.CONTENT_BG);
        panelCenter.add(panelTimKiem, BorderLayout.NORTH);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Constants.NORMAL_FONT);
        panel.add(lbl, gbc);
    }

    private void loadData() {
        loadHocPhan();
        loadKyThi();
        loadDeThi();
    }

    private void loadHocPhan() {
        cboHocPhan.removeAllItems();
        List<HocPhanDTO> danhSach = hocPhanBUS.getDanhSachHocPhan();
        if (danhSach != null) {
            for (HocPhanDTO hp : danhSach) {
                cboHocPhan.addItem(hp);
            }
        }
    }

    private void loadKyThi() {
        cboKyThi.removeAllItems();
        List<KyThiDTO> danhSach = kyThiBUS.getDanhSachKyThi();
        if (danhSach != null) {
            for (KyThiDTO kt : danhSach) {
                cboKyThi.addItem(kt);
            }
        }
    }

    private void loadDeThi() {
        modelDeThi.setRowCount(0);
        List<DeThiDTO> danhSach = deThiBUS.getDanhSachDeThi(nguoiDung.getMaGV());
        if (danhSach != null) {
            for (DeThiDTO dt : danhSach) {
                modelDeThi.addRow(new Object[] {
                        dt.getMaDeThi(), dt.getTenDeThi(), dt.getTenHocPhan(),
                        dt.getTenKyThi(), dt.getSoCauHoi(), dt.getThoiGianLam() + " phút"
                });
            }
        }
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelDeThi.setRowCount(0);

        List<DeThiDTO> danhSach = deThiBUS.getDanhSachDeThi(nguoiDung.getMaGV());
        if (danhSach != null) {
            for (DeThiDTO dt : danhSach) {
                boolean match = true;
                if (!keyword.isEmpty() && !loaiTimKiem.equals("Tất cả")) {
                    String keyLower = keyword.toLowerCase();
                    switch (loaiTimKiem) {
                        case "Mã đề":
                            match = String.valueOf(dt.getMaDeThi()).contains(keyword);
                            break;
                        case "Tên đề thi":
                            match = dt.getTenDeThi() != null && dt.getTenDeThi().toLowerCase().contains(keyLower);
                            break;
                        case "Học phần":
                            match = dt.getTenHocPhan() != null && dt.getTenHocPhan().toLowerCase().contains(keyLower);
                            break;
                        case "Kỳ thi":
                            match = dt.getTenKyThi() != null && dt.getTenKyThi().toLowerCase().contains(keyLower);
                            break;
                    }
                } else if (!keyword.isEmpty()) {
                    String keyLower = keyword.toLowerCase();
                    match = String.valueOf(dt.getMaDeThi()).contains(keyword)
                            || (dt.getTenDeThi() != null && dt.getTenDeThi().toLowerCase().contains(keyLower))
                            || (dt.getTenHocPhan() != null && dt.getTenHocPhan().toLowerCase().contains(keyLower))
                            || (dt.getTenKyThi() != null && dt.getTenKyThi().toLowerCase().contains(keyLower));
                }
                if (match) {
                    modelDeThi.addRow(new Object[] {
                            dt.getMaDeThi(), dt.getTenDeThi(), dt.getTenHocPhan(),
                            dt.getTenKyThi(), dt.getSoCauHoi(), dt.getThoiGianLam() + " phút"
                    });
                }
            }
        }
    }

    private void hienThiThongTin() {
        int row = tblDeThi.getSelectedRow();
        if (row >= 0) {
            selectedMaDeThi = (int) modelDeThi.getValueAt(row, 0);
            txtTenDeThi.setText((String) modelDeThi.getValueAt(row, 1));

            String tenHocPhan = (String) modelDeThi.getValueAt(row, 2);
            for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
                if (cboHocPhan.getItemAt(i).getTenMon().equals(tenHocPhan)) {
                    cboHocPhan.setSelectedIndex(i);
                    break;
                }
            }

            String tenKyThi = (String) modelDeThi.getValueAt(row, 3);
            for (int i = 0; i < cboKyThi.getItemCount(); i++) {
                if (cboKyThi.getItemAt(i).getTenKyThi().equals(tenKyThi)) {
                    cboKyThi.setSelectedIndex(i);
                    break;
                }
            }

            String thoiGian = (String) modelDeThi.getValueAt(row, 5);
            spnThoiGian.setValue(Integer.parseInt(thoiGian.replace(" phút", "")));
        }
    }

    private void themDeThi() {
        if (!validateInput())
            return;

        DeThiDTO deThi = new DeThiDTO();
        deThi.setTenDeThi(txtTenDeThi.getText().trim());
        deThi.setMaGV(nguoiDung.getMaGV());

        HocPhanDTO hp = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hp != null)
            deThi.setMaHocPhan(hp.getMaHocPhan());

        KyThiDTO kt = (KyThiDTO) cboKyThi.getSelectedItem();
        if (kt != null)
            deThi.setMaKyThi(kt.getMaKyThi());

        deThi.setSoCauHoi(0); // Ban đầu chưa có câu hỏi
        deThi.setThoiGianLam((Integer) spnThoiGian.getValue());

        if (deThiBUS.themDeThi(deThi)) {
            JOptionPane.showMessageDialog(this,
                    "Thêm đề thi thành công!\nBấm 'Quản lý câu hỏi' để thêm câu hỏi vào đề thi.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDeThi();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm đề thi thất bại!");
        }
    }

    private void suaDeThi() {
        if (selectedMaDeThi == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        // Kiểm tra có bài thi chưa - gọi BaiThiBUS
        int soBaiThi = baiThiBUS.demBaiThiTheoDeThi(selectedMaDeThi);
        if (soBaiThi > 0) {
            JOptionPane.showMessageDialog(this,
                    "Không thể sửa đề thi!\nĐã có " + soBaiThi + " bài thi sử dụng đề thi này.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DeThiDTO deThi = new DeThiDTO();
        deThi.setMaDeThi(selectedMaDeThi);
        deThi.setTenDeThi(txtTenDeThi.getText().trim());
        deThi.setMaGV(nguoiDung.getMaGV());

        HocPhanDTO hp = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hp != null)
            deThi.setMaHocPhan(hp.getMaHocPhan());

        KyThiDTO kt = (KyThiDTO) cboKyThi.getSelectedItem();
        if (kt != null)
            deThi.setMaKyThi(kt.getMaKyThi());

        // Giữ nguyên số câu hỏi
        int row = tblDeThi.getSelectedRow();
        deThi.setSoCauHoi((Integer) modelDeThi.getValueAt(row, 4));
        deThi.setThoiGianLam((Integer) spnThoiGian.getValue());

        if (deThiBUS.capNhatDeThi(deThi)) {
            JOptionPane.showMessageDialog(this, "Cập nhật đề thi thành công!");
            loadDeThi();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật đề thi thất bại!");
        }
    }

    private void xoaDeThi() {
        if (selectedMaDeThi == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa đề thi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Kiểm tra có bài thi chưa - gọi BaiThiBUS
            int soBaiThi = baiThiBUS.demBaiThiTheoDeThi(selectedMaDeThi);
            if (soBaiThi > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Không thể xóa. Đề thi đã có " + soBaiThi + " sinh viên làm bài.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Xóa chi tiết đề thi trước - gọi ChiTietDeThiBUS
            chiTietDeThiBUS.xoaTatCaCauHoiTrongDeThi(selectedMaDeThi);
            // Xóa đề thi
            if (deThiBUS.xoaDeThi(selectedMaDeThi)) {
                JOptionPane.showMessageDialog(this, "Xóa đề thi thành công!");
                loadDeThi();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa đề thi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Mở dialog quản lý câu hỏi trong đề thi
     */
    private void moQuanLyCauHoi() {
        if (selectedMaDeThi == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đề thi để quản lý câu hỏi!");
            return;
        }

        // Kiểm tra có bài thi chưa - gọi BaiThiBUS
        int soBaiThi = baiThiBUS.demBaiThiTheoDeThi(selectedMaDeThi);
        if (soBaiThi > 0) {
            JOptionPane.showMessageDialog(this,
                    "Không thể sửa đổi câu hỏi!\nĐã có " + soBaiThi + " bài thi sử dụng đề thi này.",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy mã học phần của đề thi để lọc câu hỏi
        int row = tblDeThi.getSelectedRow();
        String tenHocPhan = (String) modelDeThi.getValueAt(row, 2);
        int maHocPhan = -1;
        for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
            if (cboHocPhan.getItemAt(i).getTenMon().equals(tenHocPhan)) {
                maHocPhan = cboHocPhan.getItemAt(i).getMaHocPhan();
                break;
            }
        }

        String tenDeThi = (String) modelDeThi.getValueAt(row, 1);
        QuanLyCauHoiDeThiDialog dialog = new QuanLyCauHoiDeThiDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                deThiBUS, cauHoiBUS, chiTietDeThiBUS, nguoiDung.getMaGV(), selectedMaDeThi, tenDeThi, maHocPhan);
        dialog.setVisible(true);

        // Reload sau khi đóng dialog
        loadDeThi();
    }

    private void lamMoi() {
        txtTenDeThi.setText("");
        if (cboHocPhan.getItemCount() > 0)
            cboHocPhan.setSelectedIndex(0);
        if (cboKyThi.getItemCount() > 0)
            cboKyThi.setSelectedIndex(0);
        spnThoiGian.setValue(45);
        tblDeThi.clearSelection();
        selectedMaDeThi = -1;
    }

    private boolean validateInput() {
        if (txtTenDeThi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đề thi!");
            txtTenDeThi.requestFocus();
            return false;
        }
        if (cboHocPhan.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học phần!");
            return false;
        }
        if (cboKyThi.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kỳ thi!");
            return false;
        }
        return true;
    }
}

/**
 * Dialog quản lý câu hỏi trong đề thi
 * Có 2 table: câu hỏi trong đề thi và câu hỏi có thể thêm
 * 
 * Sử dụng BUS chuyên biệt:
 * - DeThiBUS: Quản lý chi tiết đề thi
 * - CauHoiBUS: Lấy danh sách câu hỏi
 */
class QuanLyCauHoiDeThiDialog extends JDialog {
    private DeThiBUS deThiBUS;
    private CauHoiBUS cauHoiBUS;
    private ChiTietDeThiBUS chiTietDeThiBUS;
    private int maGV;
    private int maDeThi;
    private int maHocPhan;

    // Table câu hỏi đã có trong đề thi
    private CustomTable tblCauHoiTrongDeThi;
    private DefaultTableModel modelCauHoiTrongDeThi;

    // Table câu hỏi có thể thêm
    private CustomTable tblCauHoiCoTheThem;
    private DefaultTableModel modelCauHoiCoTheThem;

    private CustomButton btnThem;
    private CustomButton btnXoa;
    private CustomButton btnDong;

    private JLabel lblSoCauHoi;

    public QuanLyCauHoiDeThiDialog(JFrame parent, DeThiBUS deThiBUS, CauHoiBUS cauHoiBUS,
            ChiTietDeThiBUS chiTietDeThiBUS, int maGV, int maDeThi, String tenDeThi, int maHocPhan) {
        super(parent, "Quản lý câu hỏi - " + tenDeThi, true);
        this.deThiBUS = deThiBUS;
        this.cauHoiBUS = cauHoiBUS;
        this.chiTietDeThiBUS = chiTietDeThiBUS;
        this.maGV = maGV;
        this.maDeThi = maDeThi;
        this.maHocPhan = maHocPhan;

        initComponents();
        loadData();

        setSize(1000, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(Constants.CONTENT_BG);

        // Panel chứa 2 bảng
        JPanel panelTables = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTables.setBackground(Constants.CONTENT_BG);

        // === Bảng câu hỏi trong đề thi (bên trái) ===
        JPanel panelTrongDeThi = new JPanel(new BorderLayout(0, 5));
        panelTrongDeThi.setBackground(Constants.CONTENT_BG);

        JPanel panelTitleLeft = new JPanel(new BorderLayout());
        panelTitleLeft.setBackground(Constants.CONTENT_BG);
        JLabel lblTitleLeft = new JLabel("Câu hỏi trong đề thi", SwingConstants.CENTER);
        lblTitleLeft.setFont(Constants.TITLE_FONT);
        lblTitleLeft.setForeground(Constants.PRIMARY_COLOR);
        panelTitleLeft.add(lblTitleLeft, BorderLayout.CENTER);

        lblSoCauHoi = new JLabel("(0 câu)", SwingConstants.CENTER);
        lblSoCauHoi.setFont(Constants.NORMAL_FONT);
        panelTitleLeft.add(lblSoCauHoi, BorderLayout.SOUTH);
        panelTrongDeThi.add(panelTitleLeft, BorderLayout.NORTH);

        String[] columnsLeft = { "Mã CH", "Nội dung", "Mức độ", "Loại" };
        modelCauHoiTrongDeThi = new DefaultTableModel(columnsLeft, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCauHoiTrongDeThi = new CustomTable(modelCauHoiTrongDeThi);
        tblCauHoiTrongDeThi.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCauHoiTrongDeThi.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblCauHoiTrongDeThi.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblCauHoiTrongDeThi.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane scrollLeft = new JScrollPane(tblCauHoiTrongDeThi);
        scrollLeft.getViewport().setBackground(Constants.CARD_COLOR);
        panelTrongDeThi.add(scrollLeft, BorderLayout.CENTER);

        // === Bảng câu hỏi có thể thêm (bên phải) ===
        JPanel panelCoTheThem = new JPanel(new BorderLayout(0, 5));
        panelCoTheThem.setBackground(Constants.CONTENT_BG);

        JLabel lblTitleRight = new JLabel("Câu hỏi có thể thêm (cùng môn học)", SwingConstants.CENTER);
        lblTitleRight.setFont(Constants.TITLE_FONT);
        lblTitleRight.setForeground(Constants.SECONDARY_COLOR);
        panelCoTheThem.add(lblTitleRight, BorderLayout.NORTH);

        String[] columnsRight = { "Mã CH", "Nội dung", "Mức độ", "Loại" };
        modelCauHoiCoTheThem = new DefaultTableModel(columnsRight, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCauHoiCoTheThem = new CustomTable(modelCauHoiCoTheThem);
        tblCauHoiCoTheThem.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCauHoiCoTheThem.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblCauHoiCoTheThem.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblCauHoiCoTheThem.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane scrollRight = new JScrollPane(tblCauHoiCoTheThem);
        scrollRight.getViewport().setBackground(Constants.CARD_COLOR);
        panelCoTheThem.add(scrollRight, BorderLayout.CENTER);

        panelTables.add(panelTrongDeThi);
        panelTables.add(panelCoTheThem);
        add(panelTables, BorderLayout.CENTER);

        // Panel nút
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelButtons.setBackground(Constants.CONTENT_BG);

        btnThem = new CustomButton("<< Thêm vào đề thi", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa khỏi đề thi >>", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnDong = new CustomButton("Đóng", Constants.SECONDARY_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themCauHoiVaoDeThi());
        btnXoa.addActionListener(e -> xoaCauHoiKhoiDeThi());
        btnDong.addActionListener(e -> dispose());

        panelButtons.add(btnThem);
        panelButtons.add(btnXoa);
        panelButtons.add(btnDong);

        add(panelButtons, BorderLayout.SOUTH);
    }

    private void loadData() {
        loadCauHoiTrongDeThi();
        loadCauHoiCoTheThem();
    }

    private void loadCauHoiTrongDeThi() {
        modelCauHoiTrongDeThi.setRowCount(0);

        // Lấy danh sách mã câu hỏi trong đề thi - gọi ChiTietDeThiBUS
        List<Integer> danhSachMaCH = chiTietDeThiBUS.getMaCauHoiByDeThi(maDeThi);

        // Lấy thông tin chi tiết từ danh sách câu hỏi của giảng viên
        List<CauHoiDTO> danhSachCauHoi = cauHoiBUS.getDanhSachCauHoi(maGV);

        int count = 0;
        for (Integer maCH : danhSachMaCH) {
            for (CauHoiDTO ch : danhSachCauHoi) {
                if (ch.getMaCauHoi() == maCH) {
                    String noiDung = ch.getNoiDungCauHoi();
                    if (noiDung.length() > 80) {
                        noiDung = noiDung.substring(0, 80) + "...";
                    }
                    modelCauHoiTrongDeThi.addRow(new Object[] {
                            ch.getMaCauHoi(), noiDung, ch.getMucDo(), ch.getLoaiCauHoi()
                    });
                    count++;
                    break;
                }
            }
        }

        lblSoCauHoi.setText("(" + count + " câu)");
    }

    private void loadCauHoiCoTheThem() {
        modelCauHoiCoTheThem.setRowCount(0);

        // Lấy danh sách mã câu hỏi đã có trong đề thi - gọi ChiTietDeThiBUS
        Set<Integer> danhSachDaCo = new HashSet<>(chiTietDeThiBUS.getMaCauHoiByDeThi(maDeThi));

        // Lấy danh sách câu hỏi của giảng viên theo môn học
        List<CauHoiDTO> danhSachCauHoi;
        if (maHocPhan > 0) {
            // Lọc theo môn học
            danhSachCauHoi = cauHoiBUS.getCauHoiTheoMon(maHocPhan);
        } else {
            danhSachCauHoi = cauHoiBUS.getDanhSachCauHoi(maGV);
        }

        for (CauHoiDTO ch : danhSachCauHoi) {
            // Chỉ hiện câu hỏi chưa có trong đề thi
            if (!danhSachDaCo.contains(ch.getMaCauHoi())) {
                String noiDung = ch.getNoiDungCauHoi();
                if (noiDung.length() > 80) {
                    noiDung = noiDung.substring(0, 80) + "...";
                }
                modelCauHoiCoTheThem.addRow(new Object[] {
                        ch.getMaCauHoi(), noiDung, ch.getMucDo(), ch.getLoaiCauHoi()
                });
            }
        }
    }

    private void themCauHoiVaoDeThi() {
        int[] selectedRows = tblCauHoiCoTheThem.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần thêm!");
            return;
        }

        List<Integer> danhSachMaCH = new ArrayList<>();
        for (int row : selectedRows) {
            int maCH = (int) modelCauHoiCoTheThem.getValueAt(row, 0);
            danhSachMaCH.add(maCH);
        }

        boolean success = chiTietDeThiBUS.themNhieuCauHoiVaoDeThi(maDeThi, danhSachMaCH);
        if (success) {
            // Cập nhật số câu hỏi trong đề thi
            int soCauMoi = modelCauHoiTrongDeThi.getRowCount() + danhSachMaCH.size();
            deThiBUS.capNhatSoCauHoi(maDeThi, soCauMoi);

            JOptionPane.showMessageDialog(this, "Đã thêm " + danhSachMaCH.size() + " câu hỏi vào đề thi!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm câu hỏi thất bại!");
        }
    }

    private void xoaCauHoiKhoiDeThi() {
        int[] selectedRows = tblCauHoiTrongDeThi.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa " + selectedRows.length + " câu hỏi khỏi đề thi?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int successCount = 0;
        for (int row : selectedRows) {
            int maCH = (int) modelCauHoiTrongDeThi.getValueAt(row, 0);
            if (chiTietDeThiBUS.xoaCauHoiKhoiDeThi(maDeThi, maCH)) {
                successCount++;
            }
        }

        // Cập nhật số câu hỏi trong đề thi
        int soCauMoi = modelCauHoiTrongDeThi.getRowCount() - successCount;
        deThiBUS.capNhatSoCauHoi(maDeThi, soCauMoi);

        JOptionPane.showMessageDialog(this, "Đã xóa " + successCount + " câu hỏi khỏi đề thi!");
        loadData();
    }
}
