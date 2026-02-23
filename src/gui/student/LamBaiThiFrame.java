/*
 * ===========================================================================
 * Hệ thống thi trắc nghiệm trực tuyến
 * ===========================================================================
 * GUI: LamBaiThiFrame - Màn hình làm bài thi
 * 
 * MÔ TẢ:
 *   - Màn hình full-screen để làm bài thi
 *   - Hỗ trợ 2 loại câu hỏi: Trắc nghiệm và Điền khuyết
 *   - Đếm ngược thời gian
 *   - Tự động nộp bài khi hết giờ
 * 
 * CẤU TRÚC GIAO DIỆN:
 *   ┌─────────────────────────────────────────────────┐
 *   │ Câu 1/10        [Loại: Trắc nghiệm]        00:30 │
 *   ├─────────────────────────────────────────────────┤
 *   │                                                 │
 *   │              NỘI DUNG CÂU HỏI                    │
 *   │                                                 │
 *   ├─────────────────────────────────────────────────┤
 *   │ ○ A. Đáp án A                                    │
 *   │ ● B. Đáp án B  <- Đã chọn                       │
 *   │ ○ C. Đáp án C                                    │
 *   │ ○ D. Đáp án D                                    │
 *   ├─────────────────────────────────────────────────┤
 *   │  [1][2][3]...[10]   Bảng số câu hỏi            │
 *   ├─────────────────────────────────────────────────┤
 *   │ [<< Trước]           [Tiếp >>]        [NộP BÀI] │
 *   └─────────────────────────────────────────────────┘
 * 
 * TÍNH NĂNG:
 *   - Trộn ngẫu nhiên thứ tự câu hỏi
 *   - Trộn ngẫu nhiên đáp án (câu trắc nghiệm)
 *   - Lưu đáp án tạm thời khi chuyển câu
 *   - Bảng số câu hiện màu: Xanh = đã làm, Xám = chưa làm
 *   - Khóa cửa sổ khi đang làm bài (không cho đóng)
 * 
 * @see BaiThiBUS - Lưu kết quả bài thi
 * @see ChiTietBaiThiDTO - Lưu đáp án từng câu
 * ===========================================================================
 */
package gui.student;

import bus.BaiThiBUS;
import bus.CauHoiBUS;
import bus.DeThiBUS;
import config.Constants;
import dto.*;
import gui.components.CustomButton;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import util.IconHelper;

public class LamBaiThiFrame extends JFrame {
    private int maBaiThi;
    private List<CauHoiDTO> danhSachCauHoi;
    private List<String> dapAnChon;
    private int cauHienTai = 0;
    private int thoiGianConLai; // Tính bằng giây
    
    private JLabel lblCauHoi;
    private JLabel lblLoaiCauHoi;
    private JTextArea txtNoiDung;
    
    // Các component cho câu hỏi trắc nghiệm
    private JPanel panelTracNghiem;
    private JRadioButton rbA, rbB, rbC, rbD;
    private ButtonGroup buttonGroup;
    
    // Các component cho câu hỏi điền khuyết
    private JPanel panelDienKhuyet;
    private JPanel panelCacChoTrong;
    private List<JTextField> danhSachTxtDapAn;
    private JLabel lblTuGoiY;
    
    private CustomButton btnTruoc;
    private CustomButton btnSau;
    private CustomButton btnNop;
    private JLabel lblThoiGian;
    private JPanel panelDanhSachCau;
    private JButton[] btnCau;
    
    private Timer timer;
    private BaiThiBUS baiThiBUS;
    private DeThiBUS deThiBUS;
    private CauHoiBUS cauHoiBUS;
    private StudentDashboard parentDashboard;

    // Constructor đơn giản - tự động load câu hỏi
    public LamBaiThiFrame(StudentDashboard parent, int maBaiThi, int maDeThi, int thoiGianPhut) {
        this.parentDashboard = parent;
        this.maBaiThi = maBaiThi;
        this.thoiGianConLai = thoiGianPhut * 60;
        this.baiThiBUS = new BaiThiBUS();
        this.deThiBUS = new DeThiBUS();
        this.cauHoiBUS = new CauHoiBUS();
        
        // GUI tự load câu hỏi: Lấy ID từ ChiTietDeThi -> Lấy nội dung từ CauHoi
        this.danhSachCauHoi = new ArrayList<>();
        List<Integer> listMaCauHoi = deThiBUS.getMaCauHoiByDeThi(maDeThi);
        for (int maCH : listMaCauHoi) {
            CauHoiDTO ch = cauHoiBUS.getById(maCH);
            if (ch != null) this.danhSachCauHoi.add(ch);
        }
        
        if (this.danhSachCauHoi == null) {
            this.danhSachCauHoi = new ArrayList<>();
        }
        
        // Khởi tạo danh sách đáp án
        this.dapAnChon = new ArrayList<>();
        for (int i = 0; i < danhSachCauHoi.size(); i++) {
            dapAnChon.add("");
        }
        
        initComponents();
        hienThiCauHoi(0);
        batDauDemGio();
    }


    private void initComponents() {
        setTitle("Làm bài thi trắc nghiệm");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                xacNhanNopBai();
            }
        });
        
        JPanel panelChinh = new JPanel(new BorderLayout(10, 10));
        panelChinh.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelChinh.setBackground(Constants.BACKGROUND_COLOR);
        
        // Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Constants.PRIMARY_COLOR);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel lblTieuDe = new JLabel("BÀI THI TRẮC NGHIỆM");
        lblTieuDe.setFont(Constants.HEADER_FONT);
        lblTieuDe.setForeground(Color.WHITE);
        panelHeader.add(lblTieuDe, BorderLayout.WEST);
        
        lblThoiGian = new JLabel(formatThoiGian(thoiGianConLai));
        lblThoiGian.setFont(Constants.HEADER_FONT);
        lblThoiGian.setForeground(Color.WHITE);
        panelHeader.add(lblThoiGian, BorderLayout.EAST);
        
        panelChinh.add(panelHeader, BorderLayout.NORTH);
        
        // Center: Câu hỏi và đáp án
        JPanel panelCauHoi = new JPanel(new BorderLayout(10, 10));
        panelCauHoi.setBackground(Color.WHITE);
        panelCauHoi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Panel header câu hỏi
        JPanel panelCauHoiHeader = new JPanel(new BorderLayout());
        panelCauHoiHeader.setBackground(Color.WHITE);
        
        lblCauHoi = new JLabel("Câu 1:");
        lblCauHoi.setFont(Constants.HEADER_FONT);
        lblCauHoi.setForeground(Constants.PRIMARY_COLOR);
        panelCauHoiHeader.add(lblCauHoi, BorderLayout.WEST);
        
        lblLoaiCauHoi = new JLabel();
        lblLoaiCauHoi.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblLoaiCauHoi.setForeground(Color.GRAY);
        panelCauHoiHeader.add(lblLoaiCauHoi, BorderLayout.EAST);
        
        panelCauHoi.add(panelCauHoiHeader, BorderLayout.NORTH);
        
        txtNoiDung = new JTextArea();
        txtNoiDung.setFont(Constants.NORMAL_FONT);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.setEditable(false);
        txtNoiDung.setBackground(Color.WHITE);
        JScrollPane scrollNoiDung = new JScrollPane(txtNoiDung);
        scrollNoiDung.setBorder(null);
        scrollNoiDung.setPreferredSize(new Dimension(0, 120));
        panelCauHoi.add(scrollNoiDung, BorderLayout.CENTER);
        
        // Panel chứa các loại đáp án (CardLayout)
        JPanel panelDapAn = new JPanel(new CardLayout());
        panelDapAn.setBackground(Color.WHITE);
        
        // Panel cho câu hỏi trắc nghiệm
        panelTracNghiem = new JPanel(new GridLayout(4, 1, 10, 10));
        panelTracNghiem.setBackground(Color.WHITE);
        panelTracNghiem.setBorder(BorderFactory.createTitledBorder("Chọn đáp án:"));
        
        buttonGroup = new ButtonGroup();
        rbA = new JRadioButton("A. ");
        rbB = new JRadioButton("B. ");
        rbC = new JRadioButton("C. ");
        rbD = new JRadioButton("D. ");
        
        JRadioButton[] radios = {rbA, rbB, rbC, rbD};
        String[] dapAn = {"A", "B", "C", "D"};
        
        for (int i = 0; i < radios.length; i++) {
            radios[i].setFont(Constants.NORMAL_FONT);
            radios[i].setBackground(Color.WHITE);
            final String da = dapAn[i];
            radios[i].addActionListener(e -> luuDapAnTracNghiem(da));
            buttonGroup.add(radios[i]);
            panelTracNghiem.add(radios[i]);
        }
        
        panelDapAn.add(panelTracNghiem, "TN");
        
        // Panel cho câu hỏi điền khuyết
        panelDienKhuyet = new JPanel(new BorderLayout(10, 10));
        panelDienKhuyet.setBackground(Color.WHITE);
        panelDienKhuyet.setBorder(BorderFactory.createTitledBorder("Điền vào chỗ trống:"));
        
        // Panel chứa các ô điền
        panelCacChoTrong = new JPanel();
        panelCacChoTrong.setLayout(new BoxLayout(panelCacChoTrong, BoxLayout.Y_AXIS));
        panelCacChoTrong.setBackground(Color.WHITE);
        
        JScrollPane scrollChoTrong = new JScrollPane(panelCacChoTrong);
        scrollChoTrong.setBorder(null);
        scrollChoTrong.setPreferredSize(new Dimension(0, 150));
        panelDienKhuyet.add(scrollChoTrong, BorderLayout.CENTER);
        
        // Label từ gợi ý
        lblTuGoiY = new JLabel();
        lblTuGoiY.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblTuGoiY.setForeground(new Color(100, 100, 100));
        JPanel panelGoiY = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelGoiY.setBackground(new Color(255, 255, 220));
        panelGoiY.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelGoiY.add(new JLabel("💡 Từ gợi ý: "));
        panelGoiY.add(lblTuGoiY);
        panelDienKhuyet.add(panelGoiY, BorderLayout.SOUTH);
        
        panelDapAn.add(panelDienKhuyet, "DK");
        
        panelCauHoi.add(panelDapAn, BorderLayout.SOUTH);
        panelChinh.add(panelCauHoi, BorderLayout.CENTER);
        
        // Khởi tạo danh sách text field cho điền khuyết
        danhSachTxtDapAn = new ArrayList<>();
        
        // Bottom: Điều hướng
        JPanel panelDieuHuong = new JPanel(new FlowLayout());
        panelDieuHuong.setBackground(Constants.BACKGROUND_COLOR);
        
        btnTruoc = new CustomButton("< Câu trước", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnSau = new CustomButton("Câu sau >", Constants.PRIMARY_COLOR, Constants.TEXT_COLOR);
        btnNop = new CustomButton("Nộp bài", Constants.DANGER_COLOR, Constants.TEXT_COLOR);
        
        btnTruoc.addActionListener(e -> cauTruoc());
        btnSau.addActionListener(e -> cauSau());
        btnNop.addActionListener(e -> xacNhanNopBai());
        
        panelDieuHuong.add(btnTruoc);
        panelDieuHuong.add(btnSau);
        panelDieuHuong.add(btnNop);
        
        panelChinh.add(panelDieuHuong, BorderLayout.SOUTH);
        
        // Right: Danh sách câu hỏi
        panelDanhSachCau = new JPanel(new GridLayout(0, 5, 5, 5));
        panelDanhSachCau.setBackground(Constants.BACKGROUND_COLOR);
        panelDanhSachCau.setBorder(BorderFactory.createTitledBorder("Danh sách câu hỏi"));
        
        btnCau = new JButton[danhSachCauHoi.size()];
        for (int i = 0; i < danhSachCauHoi.size(); i++) {
            final int index = i;
            btnCau[i] = new JButton(String.valueOf(i + 1));
            btnCau[i].setPreferredSize(new Dimension(40, 40));
            btnCau[i].addActionListener(e -> chuyenDenCau(index));
            
            // Đánh dấu loại câu hỏi
            CauHoiDTO ch = danhSachCauHoi.get(i);
            if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi())) {
                btnCau[i].setToolTipText("Câu điền khuyết");
            } else {
                btnCau[i].setToolTipText("Câu trắc nghiệm");
            }
            
            panelDanhSachCau.add(btnCau[i]);
        }
        
        JScrollPane scrollDanhSach = new JScrollPane(panelDanhSachCau);
        scrollDanhSach.setPreferredSize(new Dimension(280, 0));
        panelChinh.add(scrollDanhSach, BorderLayout.EAST);
        
        add(panelChinh);
    }
    
    private void hienThiCauHoi(int index) {
        if (index < 0 || index >= danhSachCauHoi.size()) return;
        
        CauHoiDTO cauHoi = danhSachCauHoi.get(index);
        
        lblCauHoi.setText("Câu " + (index + 1) + "/" + danhSachCauHoi.size() + ":");
        txtNoiDung.setText(cauHoi.getNoiDungCauHoi());
        
        // Xác định loại câu hỏi và hiển thị panel tương ứng
        Container parent = panelTracNghiem.getParent();
        CardLayout cardLayout = (CardLayout) parent.getLayout();
        
        if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(cauHoi.getLoaiCauHoi())) {
            // Câu hỏi điền khuyết
            CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
            lblLoaiCauHoi.setText("Câu hỏi điền khuyết");
            lblLoaiCauHoi.setIcon(IconHelper.createIcon(Constants.ICON_EDIT, Constants.ICON_SIZE_NORMAL, Constants.INFO_COLOR));
            
            // Hiển thị panel điền khuyết
            cardLayout.show(parent, "DK");
            
            // Chuẩn bị các ô điền
            hienThiCauDienKhuyet(dk, index);
        } else {
            // Câu hỏi trắc nghiệm
            CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
            lblLoaiCauHoi.setText("Câu hỏi trắc nghiệm");
            lblLoaiCauHoi.setIcon(IconHelper.createIcon(Constants.ICON_LIST, Constants.ICON_SIZE_NORMAL, Constants.INFO_COLOR));
            
            // Hiển thị panel trắc nghiệm
            cardLayout.show(parent, "TN");
            
            rbA.setText("A. " + (mc.getNoiDungA() != null ? mc.getNoiDungA() : ""));
            rbB.setText("B. " + (mc.getNoiDungB() != null ? mc.getNoiDungB() : ""));
            rbC.setText("C. " + (mc.getNoiDungC() != null ? mc.getNoiDungC() : ""));
            rbD.setText("D. " + (mc.getNoiDungD() != null ? mc.getNoiDungD() : ""));
            
            // Khôi phục đáp án đã chọn
            buttonGroup.clearSelection();
            String dapAnDaChon = dapAnChon.get(index);
            if (!dapAnDaChon.isEmpty()) {
                switch (dapAnDaChon) {
                    case "A": rbA.setSelected(true); break;
                    case "B": rbB.setSelected(true); break;
                    case "C": rbC.setSelected(true); break;
                    case "D": rbD.setSelected(true); break;
                }
            }
        }
        
        capNhatTrangThaiNut();
    }
    
    /**
     * Hiển thị câu hỏi điền khuyết
     */
    private void hienThiCauDienKhuyet(CauHoiDKDTO dk, int index) {
        // Xóa các ô cũ
        panelCacChoTrong.removeAll();
        danhSachTxtDapAn.clear();
        
        // Đếm số chỗ trống
        int soChoTrong = dk.demSoChoTrong();
        if (soChoTrong == 0) soChoTrong = 1;
        
        // Tạo các ô nhập liệu cho từng chỗ trống
        for (int i = 0; i < soChoTrong; i++) {
            JPanel panelMot = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            panelMot.setBackground(Color.WHITE);
            panelMot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            
            JLabel lbl = new JLabel("Chỗ trống " + (i + 1) + ":");
            lbl.setFont(Constants.NORMAL_FONT);
            lbl.setPreferredSize(new Dimension(100, 30));
            
            JTextField txt = new JTextField(30);
            txt.setFont(Constants.NORMAL_FONT);
            final int choTrong = i;
            txt.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    luuDapAnDienKhuyet();
                }
            });
            
            panelMot.add(lbl);
            panelMot.add(txt);
            panelCacChoTrong.add(panelMot);
            danhSachTxtDapAn.add(txt);
        }
        
        // Hiển thị từ gợi ý (xáo trộn)
        String danhSachTu = dk.getDanhSachTu();
        if (danhSachTu != null && !danhSachTu.isEmpty()) {
            List<String> tuList = new ArrayList<>(Arrays.asList(danhSachTu.split("\\|")));
            Collections.shuffle(tuList);
            lblTuGoiY.setText(String.join(" | ", tuList));
        } else {
            lblTuGoiY.setText("(Không có từ gợi ý)");
        }
        
        // Khôi phục đáp án đã nhập
        String dapAnDaChon = dapAnChon.get(index);
        if (dapAnDaChon != null && !dapAnDaChon.isEmpty()) {
            String[] cacDapAn = dapAnDaChon.split("\\|", -1);
            for (int i = 0; i < Math.min(cacDapAn.length, danhSachTxtDapAn.size()); i++) {
                danhSachTxtDapAn.get(i).setText(cacDapAn[i]);
            }
        }
        
        panelCacChoTrong.revalidate();
        panelCacChoTrong.repaint();
    }
    
    /**
     * Lưu đáp án trắc nghiệm
     */
    private void luuDapAnTracNghiem(String dapAn) {
        dapAnChon.set(cauHienTai, dapAn);
        capNhatTrangThaiNut();
    }
    
    /**
     * Lưu đáp án điền khuyết
     */
    private void luuDapAnDienKhuyet() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < danhSachTxtDapAn.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(danhSachTxtDapAn.get(i).getText().trim());
        }
        dapAnChon.set(cauHienTai, sb.toString());
        capNhatTrangThaiNut();
    }
    
    private void capNhatTrangThaiNut() {
        for (int i = 0; i < btnCau.length; i++) {
            String dapAn = dapAnChon.get(i);
            boolean daDien = dapAn != null && !dapAn.isEmpty() && !dapAn.equals("|");
            
            // Kiểm tra nếu là câu điền khuyết, xem có ô nào được điền không
            if (dapAn != null && dapAn.contains("|")) {
                String[] parts = dapAn.split("\\|", -1);
                daDien = false;
                for (String p : parts) {
                    if (!p.trim().isEmpty()) {
                        daDien = true;
                        break;
                    }
                }
            }
            
            CauHoiDTO ch = danhSachCauHoi.get(i);
            if (daDien) {
                btnCau[i].setBackground(Color.GREEN);
            } else {
                // Màu khác nhau cho loại câu hỏi
                if (CauHoiDTO.LOAI_DIEN_KHUYET.equals(ch.getLoaiCauHoi())) {
                    btnCau[i].setBackground(new Color(255, 255, 200)); // Vàng nhạt cho điền khuyết
                } else {
                    btnCau[i].setBackground(null);
                }
            }
            
            if (i == cauHienTai) {
                btnCau[i].setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            } else {
                btnCau[i].setBorder(null);
            }
        }
    }
    
    private void chuyenDenCau(int index) {
        cauHienTai = index;
        hienThiCauHoi(index);
    }
    
    private void cauTruoc() {
        if (cauHienTai > 0) {
            chuyenDenCau(cauHienTai - 1);
        }
    }
    
    private void cauSau() {
        if (cauHienTai < danhSachCauHoi.size() - 1) {
            chuyenDenCau(cauHienTai + 1);
        }
    }
    
    private void batDauDemGio() {
        timer = new Timer(1000, e -> {
            thoiGianConLai--;
            lblThoiGian.setText(formatThoiGian(thoiGianConLai));
            
            if (thoiGianConLai <= 300) { // 5 phút cuối
                lblThoiGian.setForeground(Color.RED);
            }
            
            if (thoiGianConLai <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Hết thời gian! Bài thi sẽ được nộp tự động.");
                nopBai();
            }
        });
        timer.start();
    }
    
    private String formatThoiGian(int giay) {
        int gio = giay / 3600;
        int phut = (giay % 3600) / 60;
        int s = giay % 60;
        return String.format("%02d:%02d:%02d", gio, phut, s);
    }
    
    private void xacNhanNopBai() {
        int soChuaTra = 0;
        for (int i = 0; i < dapAnChon.size(); i++) {
            String dapAn = dapAnChon.get(i);
            boolean chuaTra = (dapAn == null || dapAn.isEmpty());
            
            // Kiểm tra đặc biệt cho câu điền khuyết
            if (!chuaTra && dapAn.contains("|")) {
                String[] parts = dapAn.split("\\|", -1);
                chuaTra = true;
                for (String p : parts) {
                    if (!p.trim().isEmpty()) {
                        chuaTra = false;
                        break;
                    }
                }
            }
            
            if (chuaTra) soChuaTra++;
        }
        
        String message = "Bạn có chắc muốn nộp bài?";
        if (soChuaTra > 0) {
            message = String.format("Còn %d câu chưa trả lời. Bạn có chắc muốn nộp bài?", soChuaTra);
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận nộp bài", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            nopBai();
        }
    }
    
    private void nopBai() {
        timer.stop();
        
        // Tạo danh sách chi tiết bài thi
        List<ChiTietBaiThiDTO> chiTiet = new ArrayList<>();
        for (int i = 0; i < danhSachCauHoi.size(); i++) {
            ChiTietBaiThiDTO ct = new ChiTietBaiThiDTO();
            ct.setMaBaiThi(maBaiThi);
            ct.setMaCauHoi(danhSachCauHoi.get(i).getMaCauHoi());
            ct.setDapAnSV(dapAnChon.get(i));
            chiTiet.add(ct);
        }
        
        // Lưu chi tiết bài thi - gọi BaiThiBUS
        baiThiBUS.themChiTietBatch(chiTiet);
        
        // Tính điểm - gọi BaiThiBUS
        float[] ketQua = baiThiBUS.tinhDiem(maBaiThi);
        int soCauDung = (int) ketQua[0];
        int soCauSai = (int) ketQua[1];
        float diemSo = ketQua[2];
        
        // Cập nhật kết quả bài thi - gọi BaiThiBUS
        baiThiBUS.capNhatKetQua(maBaiThi, soCauDung, soCauSai, diemSo);
        
        String thongBao = String.format(
            "Nộp bài thành công!\n\n" +
            "Số câu đúng: %d/%d\n" +
            "Điểm số: %.2f/10",
            soCauDung,
            soCauDung + soCauSai,
            diemSo
        );
        JOptionPane.showMessageDialog(this, thongBao, "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        
        this.dispose();
        if (parentDashboard != null) {
            parentDashboard.hoanThanhThi();
        }
    }
}
