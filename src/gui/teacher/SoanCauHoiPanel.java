

/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: SoanCauHoiPanel - Panel soạn câu hỏi (hỗ trợ cả trắc nghiệm và điền khuyết)
 * 
 * Sử dụng BUS chuyên biệt:
 * - CauHoiBUS: Quản lý câu hỏi
 * - HocPhanBUS: Lấy danh sách học phần
 */
package gui.teacher;

import bus.CauHoiBUS;
import bus.DeThiBUS;
import bus.HocPhanBUS;
import config.Constants;
import dto.CauHoiDKDTO;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import gui.components.AdvancedSearchDialog;
import gui.components.CustomButton;
import gui.components.CustomTable;
import gui.components.SelectEntityDialog;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.SearchCondition;

public class SoanCauHoiPanel extends JPanel {
    private GiangVienDTO giangVien;
    private CauHoiBUS cauHoiBUS;
    private HocPhanBUS hocPhanBUS;
    private DeThiBUS deThiBUS;

    private CustomTable tblCauHoi;
    private DefaultTableModel modelCauHoi;

    private JTextField txtMaCauHoi;
    private JTextArea txtNoiDung;
    
    // Components cho trắc nghiệm
    private JTextField txtDapAnA;
    private JTextField txtDapAnB;
    private JTextField txtDapAnC;
    private JTextField txtDapAnD;
    private JComboBox<String> cboDapAnDung;
    
    // Components cho điền khuyết
    private JTextField txtDapAnDienKhuyet;
    private JTextField txtTuGoiY;
    private JLabel lblHuongDanDK;
    
    // Panel chứa form nhập liệu theo loại
    private JPanel panelFormTracNghiem;
    private JPanel panelFormDienKhuyet;
    private CardLayout cardLayoutForm;
    private JPanel panelFormContainer;
    
    private JComboBox<HocPhanDTO> cboHocPhan;
    private JComboBox<String> cboMucDo;
    private JComboBox<String> cboLoaiCauHoi;

    private JTextField txtTimKiem;
    private JComboBox<String> cboLoaiTimKiem;
    private CustomButton btnTimKiem;

    private CustomButton btnThem;
    private CustomButton btnSua;
    private CustomButton btnXoa;
    private CustomButton btnLamMoi;

    public SoanCauHoiPanel(GiangVienDTO giangVien) {
        this.giangVien = giangVien;
        this.cauHoiBUS = new CauHoiBUS();
        this.hocPhanBUS = new HocPhanBUS();
        this.deThiBUS = new DeThiBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Constants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("SOẠN CÂU HỎI", SwingConstants.CENTER);
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Constants.PRIMARY_COLOR);

        // Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Constants.BACKGROUND_COLOR);
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin câu hỏi"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Mã câu hỏi, Loại câu hỏi, Học phần, Mức độ
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("Mã câu hỏi:"), gbc);
        gbc.gridx = 1;
        txtMaCauHoi = new JTextField(8);
        txtMaCauHoi.setEditable(false);
        panelForm.add(txtMaCauHoi, gbc);

        gbc.gridx = 2;
        panelForm.add(new JLabel("Loại câu hỏi:"), gbc);
        gbc.gridx = 3;
        cboLoaiCauHoi = new JComboBox<>(new String[] { "Trắc nghiệm", "Điền khuyết" });
        cboLoaiCauHoi.addActionListener(e -> chuyenLoaiCauHoi());
        panelForm.add(cboLoaiCauHoi, gbc);

        gbc.gridx = 4;
        panelForm.add(new JLabel("Học phần:"), gbc);
        gbc.gridx = 5;
        cboHocPhan = new JComboBox<>();
        cboHocPhan.setPreferredSize(new Dimension(180, 25));
        panelForm.add(cboHocPhan, gbc);

        gbc.gridx = 6;
        CustomButton btnChonHocPhan = new CustomButton("...", Constants.INFO_COLOR, Constants.TEXT_COLOR);
        btnChonHocPhan.setPreferredSize(new Dimension(45, 25));
        btnChonHocPhan.addActionListener(e -> moChonHocPhan());
        panelForm.add(btnChonHocPhan, gbc);

        gbc.gridx = 7;
        panelForm.add(new JLabel("Mức độ:"), gbc);
        gbc.gridx = 8;
        cboMucDo = new JComboBox<>(new String[] { "Dễ", "Trung bình", "Khó" });
        panelForm.add(cboMucDo, gbc);

        // Row 2: Nội dung câu hỏi
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNoiDung = new JTextArea(3, 50);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        JScrollPane scrollNoiDung = new JScrollPane(txtNoiDung);
        panelForm.add(scrollNoiDung, gbc);

        // Panel container cho form theo loại (CardLayout)
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        cardLayoutForm = new CardLayout();
        panelFormContainer = new JPanel(cardLayoutForm);
        panelFormContainer.setBackground(Constants.BACKGROUND_COLOR);
        
        // Form trắc nghiệm
        panelFormTracNghiem = createFormTracNghiem();
        panelFormContainer.add(panelFormTracNghiem, "TN");
        
        // Form điền khuyết
        panelFormDienKhuyet = createFormDienKhuyet();
        panelFormContainer.add(panelFormDienKhuyet, "DK");
        
        panelForm.add(panelFormContainer, gbc);

        // Buttons
        JPanel panelNut = new JPanel(new FlowLayout());
        panelNut.setBackground(Constants.BACKGROUND_COLOR);

        btnThem = new CustomButton("Thêm", Constants.SUCCESS_COLOR, Constants.TEXT_COLOR);
        btnSua = new CustomButton("Sửa", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnXoa = new CustomButton("Xóa", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        btnLamMoi = new CustomButton("Làm mới", Constants.WARNING_COLOR, Constants.TEXT_COLOR);

        btnThem.addActionListener(e -> themCauHoi());
        btnSua.addActionListener(e -> suaCauHoi());
        btnXoa.addActionListener(e -> xoaCauHoi());
        btnLamMoi.addActionListener(e -> lamMoi());

        panelNut.add(btnThem);
        panelNut.add(btnSua);
        panelNut.add(btnXoa);
        panelNut.add(btnLamMoi);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 9;
        panelForm.add(panelNut, gbc);

        // Panel trên
        JPanel panelTren = new JPanel(new BorderLayout());
        panelTren.add(lblTieuDe, BorderLayout.NORTH);
        panelTren.add(panelForm, BorderLayout.CENTER);
        add(panelTren, BorderLayout.NORTH);

        // Bảng câu hỏi
        String[] columns = { "Mã", "Loại", "Nội dung câu hỏi", "Môn học", "Mức độ", "Đáp án đúng" };
        modelCauHoi = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCauHoi = new CustomTable(modelCauHoi);
        tblCauHoi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTin();
            }
        });

        // Panel tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelTimKiem.setBackground(Constants.BACKGROUND_COLOR);

        JLabel lblTimKiem = new JLabel("Tìm kiếm:");
        lblTimKiem.setFont(Constants.NORMAL_FONT);
        panelTimKiem.add(lblTimKiem);

        cboLoaiTimKiem = new JComboBox<>(new String[] { "Tất cả", "Mã", "Nội dung", "Môn học", "Mức độ", "Loại" });
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
            loadCauHoi();
        });
        panelTimKiem.add(btnHienTatCa);

        CustomButton btnTimNangCao = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnTimNangCao.addActionListener(e -> moTimKiemNangCao());
        panelTimKiem.add(btnTimNangCao);

        // Panel center chứa tìm kiếm và bảng
        JPanel panelCenter = new JPanel(new BorderLayout(0, 5));
        panelCenter.setBackground(Constants.BACKGROUND_COLOR);
        panelCenter.add(panelTimKiem, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tblCauHoi);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);
    }
    
    /**
     * Tạo form nhập liệu cho câu hỏi trắc nghiệm
     */
    private JPanel createFormTracNghiem() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Constants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Đáp án trắc nghiệm"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Đáp án A, B
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Đáp án A:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnA = new JTextField(25);
        panel.add(txtDapAnA, gbc);
        
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Đáp án B:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnB = new JTextField(25);
        panel.add(txtDapAnB, gbc);
        
        // Row 2: Đáp án C, D
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Đáp án C:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnC = new JTextField(25);
        panel.add(txtDapAnC, gbc);
        
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Đáp án D:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnD = new JTextField(25);
        panel.add(txtDapAnD, gbc);
        
        // Row 3: Đáp án đúng
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Đáp án đúng:"), gbc);
        gbc.gridx = 1;
        cboDapAnDung = new JComboBox<>(new String[] { "A", "B", "C", "D" });
        panel.add(cboDapAnDung, gbc);
        
        return panel;
    }
    
    /**
     * Tạo form nhập liệu cho câu hỏi điền khuyết
     */
    private JPanel createFormDienKhuyet() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Constants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Đáp án điền khuyết"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Hướng dẫn
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        lblHuongDanDK = new JLabel("💡 Trong nội dung câu hỏi, dùng _____ (5 dấu gạch dưới) để đánh dấu chỗ trống");
        lblHuongDanDK.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHuongDanDK.setForeground(new Color(100, 100, 100));
        panel.add(lblHuongDanDK, gbc);
        
        // Row 1: Đáp án đúng
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Đáp án đúng:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDapAnDienKhuyet = new JTextField(40);
        txtDapAnDienKhuyet.setToolTipText("Nếu có nhiều chỗ trống, phân cách đáp án bằng dấu | (ví dụ: từ1|từ2|từ3)");
        panel.add(txtDapAnDienKhuyet, gbc);
        
        // Row 2: Từ gợi ý
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Từ gợi ý:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTuGoiY = new JTextField(40);
        txtTuGoiY.setToolTipText("Danh sách từ gợi ý, phân cách bằng dấu | (có thể bao gồm cả đáp án sai để gây nhiễu)");
        panel.add(txtTuGoiY, gbc);
        
        // Hướng dẫn chi tiết
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        JLabel lblViDu = new JLabel("<html><b>Ví dụ:</b> Nội dung: \"Thủ đô của Việt Nam là _____\" | Đáp án: \"Hà Nội\" | Gợi ý: \"Hà Nội|Đà Nẵng|TP.HCM\"</html>");
        lblViDu.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblViDu.setForeground(Color.GRAY);
        panel.add(lblViDu, gbc);
        
        return panel;
    }
    
    /**
     * Chuyển đổi form theo loại câu hỏi
     */
    private void chuyenLoaiCauHoi() {
        String loai = (String) cboLoaiCauHoi.getSelectedItem();
        if ("Điền khuyết".equals(loai)) {
            cardLayoutForm.show(panelFormContainer, "DK");
        } else {
            cardLayoutForm.show(panelFormContainer, "TN");
        }
    }

    private void loadData() {
        loadHocPhan();
        loadCauHoi();
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

    private void loadCauHoi() {
        modelCauHoi.setRowCount(0);
        List<CauHoiDTO> danhSach = cauHoiBUS.getDanhSachCauHoi(giangVien.getMaGV());
        if (danhSach != null) {
            for (CauHoiDTO ch : danhSach) {
                String noiDung = ch.getNoiDungCauHoi();
                if (noiDung.length() > 50) {
                    noiDung = noiDung.substring(0, 50) + "...";
                }
                String tenMon = getTenMonByMa(ch.getMaMon());
                String loaiCH = CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) ? "Điền khuyết" : "Trắc nghiệm";
                String dapAn = ch.getDapAnDung();
                if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) && dapAn != null && dapAn.length() > 30) {
                    dapAn = dapAn.substring(0, 30) + "...";
                }
                modelCauHoi.addRow(new Object[] {
                        ch.getMaCauHoi(), loaiCH, noiDung, tenMon,
                        ch.getMucDo(), dapAn
                });
            }
        }
    }

    /**
     * Lấy tên môn học theo mã môn
     */
    private String getTenMonByMa(int maMon) {
        HocPhanDTO hp = hocPhanBUS.getById(maMon);
        return hp != null ? hp.getTenMon() : "";
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        modelCauHoi.setRowCount(0);

        // Sử dụng BUS để tìm kiếm
        List<CauHoiDTO> danhSach = cauHoiBUS.timKiem(
                giangVien.getMaGV(),
                keyword,
                loaiTimKiem,
                this::getTenMonByMa
        );
        
        if (danhSach != null) {
            for (CauHoiDTO ch : danhSach) {
                String noiDung = ch.getNoiDungCauHoi();
                if (noiDung.length() > 50) {
                    noiDung = noiDung.substring(0, 50) + "...";
                }
                String tenMon = getTenMonByMa(ch.getMaMon());
                String loaiCH = CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) ? "Điền khuyết" : "Trắc nghiệm";
                String dapAn = ch.getDapAnDung();
                if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) && dapAn != null && dapAn.length() > 30) {
                    dapAn = dapAn.substring(0, 30) + "...";
                }
                modelCauHoi.addRow(new Object[] {
                        ch.getMaCauHoi(), loaiCH, noiDung, tenMon,
                        ch.getMucDo(), dapAn
                });
            }
        }
    }

    private void moTimKiemNangCao() {
        String[] searchOptions = { "Tất cả", "Mã", "Nội dung", "Môn học", "Mức độ", "Loại", "Đáp án đúng" };
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tìm kiếm câu hỏi nâng cao",
                searchOptions
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            List<SearchCondition> conditions = dialog.getConditions();
            String logic = dialog.getLogic();
            timKiemNangCao(conditions, logic);
        }
    }

    private void timKiemNangCao(List<SearchCondition> conditions, String logic) {
        modelCauHoi.setRowCount(0);
        
        List<CauHoiDTO> danhSach = cauHoiBUS.timKiemNangCao(
                giangVien.getMaGV(),
                conditions,
                logic,
                this::getTenMonByMa
        );
        
        if (danhSach != null) {
            for (CauHoiDTO ch : danhSach) {
                String noiDung = ch.getNoiDungCauHoi();
                if (noiDung.length() > 50) {
                    noiDung = noiDung.substring(0, 50) + "...";
                }
                String tenMon = getTenMonByMa(ch.getMaMon());
                String loaiCH = CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) ? "Điền khuyết" : "Trắc nghiệm";
                String dapAn = ch.getDapAnDung();
                if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi()) && dapAn != null && dapAn.length() > 30) {
                    dapAn = dapAn.substring(0, 30) + "...";
                }
                modelCauHoi.addRow(new Object[] {
                        ch.getMaCauHoi(), loaiCH, noiDung, tenMon,
                        ch.getMucDo(), dapAn
                });
            }
        }
        
        JOptionPane.showMessageDialog(this, "Tìm thấy " + modelCauHoi.getRowCount() + " kết quả.");
    }

    private void hienThiThongTin() {
        int row = tblCauHoi.getSelectedRow();
        if (row >= 0) {
            int maCauHoi = (int) modelCauHoi.getValueAt(row, 0);
            CauHoiDTO cauHoi = cauHoiBUS.getById(maCauHoi);

            if (cauHoi != null) {
                txtMaCauHoi.setText(String.valueOf(cauHoi.getMaCauHoi()));
                txtNoiDung.setText(cauHoi.getNoiDungCauHoi());
                
                // Chọn loại câu hỏi
                if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(cauHoi.getLoaiCauHoi())) {
                    cboLoaiCauHoi.setSelectedItem("Điền khuyết");
                    cardLayoutForm.show(panelFormContainer, "DK");
                    
                    CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
                    txtDapAnDienKhuyet.setText(dk.getDapAnDung() != null ? dk.getDapAnDung() : "");
                    txtTuGoiY.setText(dk.getDanhSachTu() != null ? dk.getDanhSachTu() : "");
                    
                    // Xóa thông tin trắc nghiệm
                    txtDapAnA.setText("");
                    txtDapAnB.setText("");
                    txtDapAnC.setText("");
                    txtDapAnD.setText("");
                } else {
                    cboLoaiCauHoi.setSelectedItem("Trắc nghiệm");
                    cardLayoutForm.show(panelFormContainer, "TN");
                    
                    CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
                    txtDapAnA.setText(mc.getNoiDungA() != null ? mc.getNoiDungA() : "");
                    txtDapAnB.setText(mc.getNoiDungB() != null ? mc.getNoiDungB() : "");
                    txtDapAnC.setText(mc.getNoiDungC() != null ? mc.getNoiDungC() : "");
                    txtDapAnD.setText(mc.getNoiDungD() != null ? mc.getNoiDungD() : "");
                    cboDapAnDung.setSelectedItem(mc.getDapAnDung());
                    
                    // Xóa thông tin điền khuyết
                    txtDapAnDienKhuyet.setText("");
                    txtTuGoiY.setText("");
                }

                // Chọn mức độ
                String mucDo = cauHoi.getMucDo();
                for (int i = 0; i < cboMucDo.getItemCount(); i++) {
                    if (cboMucDo.getItemAt(i).equals(mucDo)) {
                        cboMucDo.setSelectedIndex(i);
                        break;
                    }
                }

                // Chọn học phần
                for (int i = 0; i < cboHocPhan.getItemCount(); i++) {
                    if (cboHocPhan.getItemAt(i).getMaHocPhan() == cauHoi.getMaMon()) {
                        cboHocPhan.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void themCauHoi() {
        if (!validateInput())
            return;

        String loai = (String) cboLoaiCauHoi.getSelectedItem();
        CauHoiDTO cauHoi;
        
        if ("Điền khuyết".equals(loai)) {
            CauHoiDKDTO dk = new CauHoiDKDTO();
            dk.setDapAnDung(txtDapAnDienKhuyet.getText().trim());
            dk.setDanhSachTu(txtTuGoiY.getText().trim());
            cauHoi = dk;
        } else {
            CauHoiMCDTO mc = new CauHoiMCDTO();
            mc.setNoiDungA(txtDapAnA.getText().trim());
            mc.setNoiDungB(txtDapAnB.getText().trim());
            mc.setNoiDungC(txtDapAnC.getText().trim());
            mc.setNoiDungD(txtDapAnD.getText().trim());
            mc.setDapAnDung((String) cboDapAnDung.getSelectedItem());
            cauHoi = mc;
        }
        
        cauHoi.setMaGV(giangVien.getMaGV());
        HocPhanDTO hocPhan = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hocPhan != null) {
            cauHoi.setMaMon(hocPhan.getMaHocPhan());
        }
        cauHoi.setNoiDungCauHoi(txtNoiDung.getText().trim());
        cauHoi.setMucDo((String) cboMucDo.getSelectedItem());

        if (cauHoiBUS.themCauHoi(cauHoi)) {
            JOptionPane.showMessageDialog(this, "Thêm câu hỏi thành công!");
            loadCauHoi();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm câu hỏi thất bại!");
        }
    }

    private void suaCauHoi() {
        if (txtMaCauHoi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần sửa!");
            return;
        }
        if (!validateInput())
            return;

        // Lấy câu hỏi cũ để kiểm tra loại
        int maCauHoi = Integer.parseInt(txtMaCauHoi.getText());
        CauHoiDTO cauHoiCu = cauHoiBUS.getById(maCauHoi);
        if (cauHoiCu == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy câu hỏi!");
            return;
        }
        
        String loaiMoi = (String) cboLoaiCauHoi.getSelectedItem();
        String loaiCu = CauHoiDTO.LOAI_DIEN_KHUYET.equals(cauHoiCu.getLoaiCauHoi()) ? "Điền khuyết" : "Trắc nghiệm";
        
        // Không cho đổi loại câu hỏi khi sửa
        if (!loaiMoi.equals(loaiCu)) {
            JOptionPane.showMessageDialog(this, 
                "Không thể thay đổi loại câu hỏi!\nNếu muốn đổi loại, hãy xóa và tạo câu hỏi mới.",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CauHoiDTO cauHoi;
        if ("Điền khuyết".equals(loaiMoi)) {
            CauHoiDKDTO dk = new CauHoiDKDTO();
            dk.setDapAnDung(txtDapAnDienKhuyet.getText().trim());
            dk.setDanhSachTu(txtTuGoiY.getText().trim());
            cauHoi = dk;
        } else {
            CauHoiMCDTO mc = new CauHoiMCDTO();
            mc.setNoiDungA(txtDapAnA.getText().trim());
            mc.setNoiDungB(txtDapAnB.getText().trim());
            mc.setNoiDungC(txtDapAnC.getText().trim());
            mc.setNoiDungD(txtDapAnD.getText().trim());
            mc.setDapAnDung((String) cboDapAnDung.getSelectedItem());
            cauHoi = mc;
        }
        
        cauHoi.setMaCauHoi(maCauHoi);
        cauHoi.setMaGV(giangVien.getMaGV());
        HocPhanDTO hocPhan = (HocPhanDTO) cboHocPhan.getSelectedItem();
        if (hocPhan != null) {
            cauHoi.setMaMon(hocPhan.getMaHocPhan());
        }
        cauHoi.setNoiDungCauHoi(txtNoiDung.getText().trim());
        cauHoi.setMucDo((String) cboMucDo.getSelectedItem());

        if (cauHoiBUS.capNhatCauHoi(cauHoi)) {
            JOptionPane.showMessageDialog(this, "Cập nhật câu hỏi thành công!");
            loadCauHoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật câu hỏi thất bại!");
        }
    }

    private void xoaCauHoi() {
        if (txtMaCauHoi.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn câu hỏi cần xóa!");
            return;
        }

        int maCauHoi = Integer.parseInt(txtMaCauHoi.getText());
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa câu hỏi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (cauHoiBUS.xoaCauHoi(maCauHoi)) {
                JOptionPane.showMessageDialog(this, "Xóa câu hỏi thành công!");
                loadCauHoi();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa câu hỏi này!\nCâu hỏi đang được sử dụng trong đề thi hoặc đã được thi.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void lamMoi() {
        txtMaCauHoi.setText("");
        txtNoiDung.setText("");
        txtDapAnA.setText("");
        txtDapAnB.setText("");
        txtDapAnC.setText("");
        txtDapAnD.setText("");
        txtDapAnDienKhuyet.setText("");
        txtTuGoiY.setText("");
        cboDapAnDung.setSelectedIndex(0);
        cboMucDo.setSelectedIndex(0);
        cboLoaiCauHoi.setSelectedIndex(0);
        cardLayoutForm.show(panelFormContainer, "TN");
        tblCauHoi.clearSelection();
    }

    private boolean validateInput() {
        if (txtNoiDung.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung câu hỏi!");
            txtNoiDung.requestFocus();
            return false;
        }
        
        String loai = (String) cboLoaiCauHoi.getSelectedItem();
        if ("Điền khuyết".equals(loai)) {
            if (txtDapAnDienKhuyet.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đáp án đúng cho câu hỏi điền khuyết!");
                txtDapAnDienKhuyet.requestFocus();
                return false;
            }
            // Kiểm tra số chỗ trống trong nội dung
            String noiDung = txtNoiDung.getText();
            int soChoTrong = 0;
            int index = 0;
            while ((index = noiDung.indexOf("_____", index)) != -1) {
                soChoTrong++;
                index += 5;
            }
            String[] dapAnArr = txtDapAnDienKhuyet.getText().split("\\|");
            if (soChoTrong > 0 && soChoTrong != dapAnArr.length) {
                JOptionPane.showMessageDialog(this, 
                    String.format("Số đáp án (%d) không khớp với số chỗ trống (%d) trong câu hỏi!", 
                        dapAnArr.length, soChoTrong),
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else {
            if (txtDapAnA.getText().trim().isEmpty() || txtDapAnB.getText().trim().isEmpty() ||
                    txtDapAnC.getText().trim().isEmpty() || txtDapAnD.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ 4 đáp án!");
                return false;
            }
        }
        return true;
    }

    private void moChonHocPhan() {
        List<HocPhanDTO> hocPhanList = hocPhanBUS.getDanhSachHocPhan();
        SelectEntityDialog.clearSelection();
        SelectEntityDialog<HocPhanDTO> dialog = new SelectEntityDialog<>(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chọn học phần",
                "HOCPHAN",
                hocPhanList,
                HocPhanDTO::getMaHocPhan,
                HocPhanDTO::getTenMon
        );
        dialog.setVisible(true);

        if ("HOCPHAN".equals(SelectEntityDialog.getSelectedType())) {
            int maHP = SelectEntityDialog.getSelectedId();
            if (maHP >= 0) {
                selectHocPhanById(maHP);
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
}
