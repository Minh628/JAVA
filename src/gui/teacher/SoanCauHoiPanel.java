


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
import bus.HocPhanBUS;
import config.Constants;
import dto.CauHoiDKDTO;
import dto.CauHoiDTO;
import dto.CauHoiMCDTO;
import dto.GiangVienDTO;
import dto.HocPhanDTO;
import gui.components.AdvancedSearchDialog;
import gui.components.BaseCrudPanel;
import gui.components.CustomButton;
import gui.components.SelectEntityDialog;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import util.CauHoiExcelImporter;
import util.SearchCondition;

public class SoanCauHoiPanel extends BaseCrudPanel {
    private GiangVienDTO giangVien;
    private CauHoiBUS cauHoiBUS;
    private HocPhanBUS hocPhanBUS;

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


    public SoanCauHoiPanel(GiangVienDTO giangVien) {
        super(
                "SOẠN CÂU HỎI",
                new String[] { "Mã", "Loại", "Nội dung câu hỏi", "Môn học", "Mức độ", "Đáp án đúng" },
                new String[] { "Tất cả", "Mã", "Nội dung", "Môn học", "Mức độ", "Loại" }
        );
        this.giangVien = giangVien;
        this.cauHoiBUS = new CauHoiBUS();
        this.hocPhanBUS = new HocPhanBUS();
    }

    @Override
    protected JPanel createFormPanel() {
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

        return panelForm;
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
        lblHuongDanDK = new JLabel(" Trong nội dung câu hỏi, dùng _____ (5 dấu gạch dưới) để đánh dấu chỗ trống");
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

    @Override
    protected void loadData() {
        loadHocPhan();
        loadCauHoi();
    }

    @Override
    protected void addExtraSearchComponents(JPanel searchPanel) {
        CustomButton btnTimNangCao = new CustomButton("Tìm nâng cao", new Color(128, 0, 128), Constants.TEXT_COLOR);
        btnTimNangCao.addActionListener(e -> moTimKiemNangCao());
        searchPanel.add(btnTimNangCao);
        
        // Nút Import từ Excel
        CustomButton btnImportExcel = new CustomButton("Import Excel", new Color(34, 139, 34), Constants.TEXT_COLOR);
        btnImportExcel.addActionListener(e -> importCauHoiTuExcel());
        searchPanel.add(btnImportExcel);
        
        // Nút tạo file mẫu
        CustomButton btnTaoMau = new CustomButton("Tải mẫu Excel", new Color(70, 130, 180), Constants.TEXT_COLOR);
        btnTaoMau.addActionListener(e -> CauHoiExcelImporter.createTemplateFile(this));
        searchPanel.add(btnTaoMau);
    }
    
    /**
     * Import câu hỏi từ file Excel
     */
    private void importCauHoiTuExcel() {
        // Lấy mã học phần mặc định từ combobox
        HocPhanDTO selectedHP = (HocPhanDTO) cboHocPhan.getSelectedItem();
        int defaultMaHocPhan = selectedHP != null ? selectedHP.getMaHocPhan() : 0;
        
        if (defaultMaHocPhan == 0) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn học phần mặc định trước khi import!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<CauHoiDTO> danhSach = CauHoiExcelImporter.importFromExcel(this, giangVien.getMaGV(), defaultMaHocPhan);
        
        if (danhSach == null || danhSach.isEmpty()) {
            return;
        }
        
        // Xác nhận import
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có muốn import " + danhSach.size() + " câu hỏi vào hệ thống?",
            "Xác nhận Import",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Thực hiện import
        int success = 0, fail = 0;
        StringBuilder errors = new StringBuilder();
        
        for (CauHoiDTO cauHoi : danhSach) {
            try {
                boolean result = cauHoiBUS.themCauHoi(cauHoi);
                
                if (result) {
                    success++;
                } else {
                    fail++;
                    String noiDung = cauHoi.getNoiDungCauHoi();
                    if (noiDung.length() > 30) noiDung = noiDung.substring(0, 30) + "...";
                    errors.append("• ").append(noiDung).append(": Thêm thất bại\n");
                }
            } catch (Exception e) {
                fail++;
                String noiDung = cauHoi.getNoiDungCauHoi();
                if (noiDung.length() > 30) noiDung = noiDung.substring(0, 30) + "...";
                errors.append("• ").append(noiDung).append(": ").append(e.getMessage()).append("\n");
            }
        }
        
        // Hiển thị kết quả
        StringBuilder result = new StringBuilder();
        result.append("Kết quả import:\n\n");
        result.append("• Thành công: ").append(success).append(" câu hỏi\n");
        result.append("• Thất bại: ").append(fail).append(" câu hỏi\n");
        
        if (fail > 0 && errors.length() > 0) {
            result.append("\nChi tiết lỗi:\n").append(errors.toString().substring(0, Math.min(errors.length(), 500)));
        }
        
        JOptionPane.showMessageDialog(this, result.toString(),
            fail > 0 ? "Import hoàn tất với lỗi" : "Import thành công",
            fail > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        
        // Refresh danh sách
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
        tableModel.setRowCount(0);
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
                tableModel.addRow(new Object[] {
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

    @Override
    protected void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        String loaiTimKiem = (String) cboLoaiTimKiem.getSelectedItem();
        tableModel.setRowCount(0);

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
                tableModel.addRow(new Object[] {
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
        tableModel.setRowCount(0);
        
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
                tableModel.addRow(new Object[] {
                        ch.getMaCauHoi(), loaiCH, noiDung, tenMon,
                        ch.getMucDo(), dapAn
                });
            }
        }

        JOptionPane.showMessageDialog(this, "Tìm thấy " + tableModel.getRowCount() + " kết quả.");
    }

    @Override
    protected void hienThiThongTin() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int maCauHoi = (int) tableModel.getValueAt(row, 0);
            CauHoiDTO cauHoi = cauHoiBUS.getById(maCauHoi);

            if (cauHoi != null) {
                txtMaCauHoi.setText(String.valueOf(cauHoi.getMaCauHoi()));
                txtNoiDung.setText(cauHoi.getNoiDungCauHoi());
                String mucDo = cauHoi.getMucDo();
                cboMucDo.setSelectedItem(mucDo);
                // Chọn loại câu hỏi
                if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(cauHoi.getLoaiCauHoi())) {
                    cboLoaiCauHoi.setSelectedItem("Điền khuyết");
                    cardLayoutForm.show(panelFormContainer, "DK");
                    
                    CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
                    txtDapAnDienKhuyet.setText(dk.getDapAnDung() != null ? dk.getDapAnDung() : "");
                    txtTuGoiY.setText(dk.getDanhSachTu() != null ? dk.getDanhSachTu() : "");
                } else {
                    cboLoaiCauHoi.setSelectedItem("Trắc nghiệm");
                    cardLayoutForm.show(panelFormContainer, "TN");
                    
                    CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
                    txtDapAnA.setText(mc.getNoiDungA() != null ? mc.getNoiDungA() : "");
                    txtDapAnB.setText(mc.getNoiDungB() != null ? mc.getNoiDungB() : "");
                    txtDapAnC.setText(mc.getNoiDungC() != null ? mc.getNoiDungC() : "");
                    txtDapAnD.setText(mc.getNoiDungD() != null ? mc.getNoiDungD() : "");
                    cboDapAnDung.setSelectedItem(mc.getDapAnDung());
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

    @Override
    protected void them() {
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

    @Override
    protected void sua() {
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
            JOptionPane.showMessageDialog(this, "Không thể cập nhật câu hỏi này!\nCâu hỏi đang được sử dụng trong đề thi hoặc đã được thi.",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    protected void xoa() {
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

    @Override
    protected void lamMoi() {
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
        table.clearSelection();
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
