/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: QuanLyDeThiPanel - Panel quản lý đề thi cho giảng viên
 * 
 * Kế thừa BaseCrudPanel để tái sử dụng code CRUD
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
import bus.DeThiBUS;
import bus.HocPhanBUS;
import bus.KyThiBUS;
import config.Constants;
import dto.CauHoiDTO;
import dto.DeThiDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import dto.KyThiDTO;
import gui.components.AdvancedSearchDialog;
import gui.components.BaseCrudPanel;
import gui.components.CustomButton;
import gui.components.CustomTable;
import gui.components.SelectEntityDialog;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.SearchCondition;

public class QuanLyDeThiPanel extends BaseCrudPanel {
    private GiangVienDTO nguoiDung;
    private DeThiBUS deThiBUS;
    private HocPhanBUS hocPhanBUS;
    private KyThiBUS kyThiBUS;
    private CauHoiBUS cauHoiBUS;
    private BaiThiBUS baiThiBUS;

    // Cache để lookup tên từ mã
    private List<HocPhanDTO> danhSachHocPhan;
    private List<KyThiDTO> danhSachKyThi;

    // Form fields
    private JTextField txtTenDeThi;
    private JComboBox<HocPhanDTO> cboHocPhan;
    private JComboBox<KyThiDTO> cboKyThi;
    private JSpinner spnThoiGian;

    private CustomButton btnChonHocPhan;
    private CustomButton btnChonKyThi;

    private CustomButton btnQuanLyCauHoi;

    private int selectedMaDeThi = -1;

    private static final String[] COLUMNS = { "Mã đề", "Tên đề thi", "Học phần", "Kỳ thi", "Số câu", "Thời gian" };
    private static final String[] SEARCH_OPTIONS = { "Tất cả", "Mã đề", "Tên đề thi", "Học phần", "Kỳ thi" };

    public QuanLyDeThiPanel(GiangVienDTO nguoiDung) {
        super("QUẢN LÝ ĐỀ THI", COLUMNS, SEARCH_OPTIONS);
        this.nguoiDung = nguoiDung;
        this.deThiBUS = new DeThiBUS();
        this.hocPhanBUS = new HocPhanBUS();
        this.kyThiBUS = new KyThiBUS();
        this.cauHoiBUS = new CauHoiBUS();
        this.baiThiBUS = new BaiThiBUS();
    }

    @Override
    protected JPanel createFormPanel() {
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
        panelForm.add(createLabel("Tên đề thi:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTenDeThi = new JTextField(30);
        txtTenDeThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(txtTenDeThi, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelForm.add(createLabel("Học phần:"), gbc);
        gbc.gridx = 3;
        cboHocPhan = new JComboBox<>();
        cboHocPhan.setPreferredSize(new Dimension(200, 28));
        cboHocPhan.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboHocPhan, gbc);
        gbc.gridx = 4;
        btnChonHocPhan = new CustomButton("...", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnChonHocPhan.setPreferredSize(new Dimension(45, 28));
        btnChonHocPhan.addActionListener(e -> moChonHocPhan());
        panelForm.add(btnChonHocPhan, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(createLabel("Kỳ thi:"), gbc);
        gbc.gridx = 1;
        cboKyThi = new JComboBox<>();
        cboKyThi.setPreferredSize(new Dimension(200, 28));
        cboKyThi.setFont(Constants.NORMAL_FONT);
        panelForm.add(cboKyThi, gbc);
        gbc.gridx = 2;
        btnChonKyThi = new CustomButton("...", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnChonKyThi.setPreferredSize(new Dimension(45, 28));
        btnChonKyThi.addActionListener(e -> moChonKyThi());
        panelForm.add(btnChonKyThi, gbc);

        gbc.gridx = 3;
        panelForm.add(createLabel("Thời gian (phút):"), gbc);
        gbc.gridx = 4;
        spnThoiGian = new JSpinner(new SpinnerNumberModel(45, 10, 180, 5));
        spnThoiGian.setPreferredSize(new Dimension(80, 28));
        spnThoiGian.setFont(Constants.NORMAL_FONT);
        panelForm.add(spnThoiGian, gbc);

        return panelForm;
    }

    @Override
    protected void addExtraButtons(JPanel buttonPanel) {
        btnQuanLyCauHoi = new CustomButton("Quản lý câu hỏi", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnQuanLyCauHoi.addActionListener(e -> moQuanLyCauHoi());
        buttonPanel.add(btnQuanLyCauHoi);
    }

    @Override
    protected void loadData() {
        loadHocPhan();
        loadKyThi();
        loadDeThi();
    }

    private void loadHocPhan() {
        cboHocPhan.removeAllItems();
        danhSachHocPhan = hocPhanBUS.getDanhSachHocPhan();
        if (danhSachHocPhan != null) {
            for (HocPhanDTO hp : danhSachHocPhan) {
                cboHocPhan.addItem(hp);
            }
        }
    }

    private void loadKyThi() {
        cboKyThi.removeAllItems();
        danhSachKyThi = kyThiBUS.getDanhSachKyThi();
        if (danhSachKyThi != null) {
            for (KyThiDTO kt : danhSachKyThi) {
                cboKyThi.addItem(kt);
            }
        }
    }

    // Helper methods để lookup tên từ mã
    private String getTenHocPhanByMa(int maHocPhan) {
        if (danhSachHocPhan != null) {
            for (HocPhanDTO hp : danhSachHocPhan) {
                if (hp.getMaHocPhan() == maHocPhan) {
                    return hp.getTenMon();
                }
            }
        }
        return "";
    }

    private String getTenKyThiByMa(int maKyThi) {
        if (danhSachKyThi != null) {
            for (KyThiDTO kt : danhSachKyThi) {
                if (kt.getMaKyThi() == maKyThi) {
                    return kt.getTenKyThi();
                }
            }
        }
        return "";
    }

    private void moChonHocPhan() {
        SelectEntityDialog.clearSelection();
        SelectEntityDialog<HocPhanDTO> dialog = new SelectEntityDialog<>(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chọn học phần",
                "HOC_PHAN",
                danhSachHocPhan,
                HocPhanDTO::getMaHocPhan,
                HocPhanDTO::getTenMon
        );
        dialog.setVisible(true);

        if ("HOC_PHAN".equals(SelectEntityDialog.getSelectedType())) {
            int maHocPhan = SelectEntityDialog.getSelectedId();
            if (maHocPhan >= 0) {
                selectHocPhanById(maHocPhan);
            }
        }
    }

    private void moChonKyThi() {
        SelectEntityDialog.clearSelection();
        SelectEntityDialog<KyThiDTO> dialog = new SelectEntityDialog<>(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chọn kỳ thi",
                "KY_THI",
                danhSachKyThi,
                KyThiDTO::getMaKyThi,
                KyThiDTO::getTenKyThi
        );
        dialog.setVisible(true);

        if ("KY_THI".equals(SelectEntityDialog.getSelectedType())) {
            int maKyThi = SelectEntityDialog.getSelectedId();
            if (maKyThi >= 0) {
                selectKyThiById(maKyThi);
            }
        }
    }

    private void selectHocPhanById(int maHocPhan) {
        for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
            HocPhanDTO hp = cboHocPhan.getItemAt(i);
            if (hp != null && hp.getMaHocPhan() == maHocPhan) {
                cboHocPhan.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectKyThiById(int maKyThi) {
        for (int i = 0; i < cboKyThi.getItemCount(); i++) {
            KyThiDTO kt = cboKyThi.getItemAt(i);
            if (kt != null && kt.getMaKyThi() == maKyThi) {
                cboKyThi.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadDeThi() {
        tableModel.setRowCount(0);
        List<DeThiDTO> danhSach = deThiBUS.getDanhSachDeThi(nguoiDung.getMaGV());
        if (danhSach != null) {
            for (DeThiDTO dt : danhSach) {
                String tenHocPhan = getTenHocPhanByMa(dt.getMaHocPhan());
                String tenKyThi = getTenKyThiByMa(dt.getMaKyThi());
                tableModel.addRow(new Object[] {
                        dt.getMaDeThi(), dt.getTenDeThi(), tenHocPhan,
                        tenKyThi, dt.getSoCauHoi(), dt.getThoiGianLam() + " phút"
                });
            }
        }
    }

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        tableModel.setRowCount(0);

        // Sử dụng BUS để tìm kiếm
        List<DeThiDTO> danhSach = deThiBUS.timKiem(
                nguoiDung.getMaGV(), 
                keyword, 
                loaiTimKiem,
                this::getTenHocPhanByMa,
                this::getTenKyThiByMa
        );
        
        if (danhSach != null) {
            for (DeThiDTO dt : danhSach) {
                String tenHocPhan = getTenHocPhanByMa(dt.getMaHocPhan());
                String tenKyThi = getTenKyThiByMa(dt.getMaKyThi());
                tableModel.addRow(new Object[] {
                        dt.getMaDeThi(), dt.getTenDeThi(), tenHocPhan,
                        tenKyThi, dt.getSoCauHoi(), dt.getThoiGianLam() + " phút"
                });
            }
        }
    }

    @Override
    protected void addExtraSearchComponents(JPanel searchPanel) {
        CustomButton btnAdvanced = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnAdvanced.addActionListener(e -> moTimKiemNangCao());
        searchPanel.add(btnAdvanced);
    }

    private void moTimKiemNangCao() {
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tìm kiếm đề thi nâng cao",
                SEARCH_OPTIONS
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            List<SearchCondition> conditions = dialog.getConditions();
            String logic = dialog.getLogic();
            timKiemNangCao(conditions, logic);
        }
    }

    private void timKiemNangCao(List<SearchCondition> conditions, String logic) {
        tableModel.setRowCount(0);
        
        List<DeThiDTO> danhSach = deThiBUS.timKiemNangCao(
                nguoiDung.getMaGV(),
                conditions,
                logic,
                this::getTenHocPhanByMa,
                this::getTenKyThiByMa
        );
        
        if (danhSach != null) {
            for (DeThiDTO dt : danhSach) {
                String tenHocPhan = getTenHocPhanByMa(dt.getMaHocPhan());
                String tenKyThi = getTenKyThiByMa(dt.getMaKyThi());
                tableModel.addRow(new Object[] {
                        dt.getMaDeThi(), dt.getTenDeThi(), tenHocPhan,
                        tenKyThi, dt.getSoCauHoi(), dt.getThoiGianLam() + " phút"
                });
            }
        }
        
        showMessage("Tìm thấy " + tableModel.getRowCount() + " kết quả.");
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedMaDeThi = (int) tableModel.getValueAt(row, 0);
            txtTenDeThi.setText((String) tableModel.getValueAt(row, 1));

            String tenHocPhan = (String) tableModel.getValueAt(row, 2);
            for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
                if (cboHocPhan.getItemAt(i).getTenMon().equals(tenHocPhan)) {
                    cboHocPhan.setSelectedIndex(i);
                    break;
                }
            }

            String tenKyThi = (String) tableModel.getValueAt(row, 3);
            for (int i = 0; i < cboKyThi.getItemCount(); i++) {
                if (cboKyThi.getItemAt(i).getTenKyThi().equals(tenKyThi)) {
                    cboKyThi.setSelectedIndex(i);
                    break;
                }
            }

            String thoiGian = (String) tableModel.getValueAt(row, 5);
            spnThoiGian.setValue(Integer.parseInt(thoiGian.replace(" phút", "")));
        }
    }

    @Override
    protected void them() {
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
            showMessage("Thêm đề thi thất bại!");
        }
    }

    @Override
    protected void sua() {
        if (selectedMaDeThi == -1) {
            showMessage("Vui lòng chọn đề thi cần sửa!");
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
        int row = table.getSelectedRow();
        deThi.setSoCauHoi((Integer) tableModel.getValueAt(row, 4));
        deThi.setThoiGianLam((Integer) spnThoiGian.getValue());

        if (deThiBUS.capNhatDeThi(deThi)) {
            showMessage("Cập nhật đề thi thành công!");
            loadDeThi();
            lamMoi();
        } else {
            showMessage("Cập nhật đề thi thất bại!");
        }
    }

    @Override
    protected void xoa() {
        if (selectedMaDeThi == -1) {
            showMessage("Vui lòng chọn đề thi cần xóa!");
            return;
        }

        if (confirmDelete("đề thi")) {
            // Xóa chi tiết đề thi trước - gọi DeThiBUS
            deThiBUS.xoaTatCaCauHoiTrongDeThi(selectedMaDeThi);
            // Xóa đề thi
            if (deThiBUS.xoaDeThi(selectedMaDeThi)) {
                showMessage("Xóa đề thi thành công!");
                loadDeThi();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa đề thi!\nĐề thi đã có sinh viên làm bài hoặc có lỗi xảy ra.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Mở dialog quản lý câu hỏi trong đề thi
     */
    private void moQuanLyCauHoi() {
        if (selectedMaDeThi == -1) {
            showMessage("Vui lòng chọn đề thi để quản lý câu hỏi!");
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
        int row = table.getSelectedRow();
        String tenHocPhan = (String) tableModel.getValueAt(row, 2);
        int maHocPhan = -1;
        for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
            if (cboHocPhan.getItemAt(i).getTenMon().equals(tenHocPhan)) {
                maHocPhan = cboHocPhan.getItemAt(i).getMaHocPhan();
                break;
            }
        }

        String tenDeThi = (String) tableModel.getValueAt(row, 1);
        QuanLyCauHoiDeThiDialog dialog = new QuanLyCauHoiDeThiDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                deThiBUS, cauHoiBUS, nguoiDung.getMaGV(), selectedMaDeThi, tenDeThi, maHocPhan);
        dialog.setVisible(true);

        // Reload sau khi đóng dialog
        loadDeThi();
    }

    @Override
    protected void lamMoi() {
        txtTenDeThi.setText("");
        if (cboHocPhan.getItemCount() > 0)
            cboHocPhan.setSelectedIndex(0);
        if (cboKyThi.getItemCount() > 0)
            cboKyThi.setSelectedIndex(0);
        spnThoiGian.setValue(45);
        table.clearSelection();
        selectedMaDeThi = -1;
    }

    private boolean validateInput() {
        if (txtTenDeThi.getText().trim().isEmpty()) {
            showMessage("Vui lòng nhập tên đề thi!");
            txtTenDeThi.requestFocus();
            return false;
        }
        if (cboHocPhan.getSelectedItem() == null) {
            showMessage("Vui lòng chọn học phần!");
            return false;
        }
        if (cboKyThi.getSelectedItem() == null) {
            showMessage("Vui lòng chọn kỳ thi!");
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
            int maGV, int maDeThi, String tenDeThi, int maHocPhan) {
        super(parent, "Quản lý câu hỏi - " + tenDeThi, true);
        this.deThiBUS = deThiBUS;
        this.cauHoiBUS = cauHoiBUS;
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

        // Lấy danh sách mã câu hỏi trong đề thi - gọi DeThiBUS
        List<Integer> danhSachMaCH = deThiBUS.getMaCauHoiByDeThi(maDeThi);

        // Lấy thông tin chi tiết câu hỏi theo danh sách mã (không giới hạn giảng viên)
        List<CauHoiDTO> danhSachCauHoi = cauHoiBUS.getByIds(danhSachMaCH);

        int count = 0;
        for (CauHoiDTO ch : danhSachCauHoi) {
            String noiDung = ch.getNoiDungCauHoi();
            if (noiDung.length() > 80) {
                noiDung = noiDung.substring(0, 80) + "...";
            }
            modelCauHoiTrongDeThi.addRow(new Object[] {
                    ch.getMaCauHoi(), noiDung, ch.getMucDo(), ch.getLoaiCauHoi()
            });
            count++;
        }

        lblSoCauHoi.setText("(" + count + " câu)");
    }

    private void loadCauHoiCoTheThem() {
        modelCauHoiCoTheThem.setRowCount(0);

        // Lấy danh sách mã câu hỏi đã có trong đề thi - gọi DeThiBUS
        Set<Integer> danhSachDaCo = new HashSet<>(deThiBUS.getMaCauHoiByDeThi(maDeThi));

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

        boolean success = deThiBUS.themNhieuCauHoiVaoDeThi(maDeThi, danhSachMaCH);
        if (success) {
            // Cập nhật số câu hỏi trong đề thi
            int soCauMoi = modelCauHoiTrongDeThi.getRowCount() + danhSachMaCH.size();
            deThiBUS.capNhatSoCauHoi(maDeThi, soCauMoi);

            // Reload cache DeThiBUS để hiển thị số câu hỏi mới
            DeThiBUS.reloadCache();

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
            if (deThiBUS.xoaCauHoiKhoiDeThi(maDeThi, maCH)) {
                successCount++;
            }
        }

        // Cập nhật số câu hỏi trong đề thi
        int soCauMoi = modelCauHoiTrongDeThi.getRowCount() - successCount;
        deThiBUS.capNhatSoCauHoi(maDeThi, soCauMoi);

        // Reload cache DeThiBUS để hiển thị số câu hỏi mới
        DeThiBUS.reloadCache();

        JOptionPane.showMessageDialog(this, "Đã xóa " + successCount + " câu hỏi khỏi đề thi!");
        loadData();
    }
}
