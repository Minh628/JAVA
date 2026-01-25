/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * GUI: LamBaiThiFrame - Frame làm bài thi (phiên bản thay thế cho ExamWindow)
 */
package gui.student;

import bus.SinhVienThiBUS;
import config.Constants;
import dto.*;
import gui.components.CustomButton;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class LamBaiThiFrame extends JFrame {
    private int maSV;
    private int maDeThi;
    private int maBaiThi;
    private List<CauHoiDTO> danhSachCauHoi;
    private List<String> dapAnChon;
    private int cauHienTai = 0;
    private int thoiGianConLai; // Tính bằng giây
    
    private JLabel lblCauHoi;
    private JTextArea txtNoiDung;
    private JRadioButton rbA, rbB, rbC, rbD;
    private ButtonGroup buttonGroup;
    private CustomButton btnTruoc;
    private CustomButton btnSau;
    private CustomButton btnNop;
    private JLabel lblThoiGian;
    private JPanel panelDanhSachCau;
    private JButton[] btnCau;
    
    private Timer timer;
    private SinhVienThiBUS sinhVienThiBUS;
    private StudentDashboard parentDashboard;

    // Constructor đơn giản - tự động load câu hỏi
    public LamBaiThiFrame(StudentDashboard parent, int maBaiThi, int maDeThi, int thoiGianPhut) {
        this.parentDashboard = parent;
        this.maBaiThi = maBaiThi;
        this.maDeThi = maDeThi;
        this.thoiGianConLai = thoiGianPhut * 60;
        this.sinhVienThiBUS = new SinhVienThiBUS();
        
        // Load câu hỏi từ database
        this.danhSachCauHoi = sinhVienThiBUS.getCauHoiDeThi(maDeThi);
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

    // Constructor đầy đủ - truyền câu hỏi từ bên ngoài
    public LamBaiThiFrame(StudentDashboard parent, int maSV, int maDeThi, 
                          int maBaiThi, List<CauHoiDTO> danhSachCauHoi, int thoiGianPhut) {
        this.parentDashboard = parent;
        this.maSV = maSV;
        this.maDeThi = maDeThi;
        this.maBaiThi = maBaiThi;
        this.danhSachCauHoi = danhSachCauHoi;
        this.thoiGianConLai = thoiGianPhut * 60;
        this.sinhVienThiBUS = new SinhVienThiBUS();
        
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
        setSize(1000, 700);
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
        
        lblCauHoi = new JLabel("Câu 1:");
        lblCauHoi.setFont(Constants.HEADER_FONT);
        lblCauHoi.setForeground(Constants.PRIMARY_COLOR);
        panelCauHoi.add(lblCauHoi, BorderLayout.NORTH);
        
        txtNoiDung = new JTextArea();
        txtNoiDung.setFont(Constants.NORMAL_FONT);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.setEditable(false);
        txtNoiDung.setBackground(Color.WHITE);
        JScrollPane scrollNoiDung = new JScrollPane(txtNoiDung);
        scrollNoiDung.setBorder(null);
        panelCauHoi.add(scrollNoiDung, BorderLayout.CENTER);
        
        // Đáp án
        JPanel panelDapAn = new JPanel(new GridLayout(4, 1, 10, 10));
        panelDapAn.setBackground(Color.WHITE);
        
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
            radios[i].addActionListener(e -> luuDapAn(da));
            buttonGroup.add(radios[i]);
            panelDapAn.add(radios[i]);
        }
        
        panelCauHoi.add(panelDapAn, BorderLayout.SOUTH);
        panelChinh.add(panelCauHoi, BorderLayout.CENTER);
        
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
            panelDanhSachCau.add(btnCau[i]);
        }
        
        JScrollPane scrollDanhSach = new JScrollPane(panelDanhSachCau);
        scrollDanhSach.setPreferredSize(new Dimension(250, 0));
        panelChinh.add(scrollDanhSach, BorderLayout.EAST);
        
        add(panelChinh);
    }
    
    private void hienThiCauHoi(int index) {
        if (index < 0 || index >= danhSachCauHoi.size()) return;
        
        CauHoiDTO cauHoi = danhSachCauHoi.get(index);
        
        lblCauHoi.setText("Câu " + (index + 1) + "/" + danhSachCauHoi.size() + ":");
        txtNoiDung.setText(cauHoi.getNoiDungCauHoi());
        
        rbA.setText("A. " + cauHoi.getNoiDungA());
        rbB.setText("B. " + cauHoi.getNoiDungB());
        rbC.setText("C. " + cauHoi.getNoiDungC());
        rbD.setText("D. " + cauHoi.getNoiDungD());
        
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
        
        capNhatTrangThaiNut();
    }
    
    private void luuDapAn(String dapAn) {
        dapAnChon.set(cauHienTai, dapAn);
        capNhatTrangThaiNut();
    }
    
    private void capNhatTrangThaiNut() {
        for (int i = 0; i < btnCau.length; i++) {
            if (!dapAnChon.get(i).isEmpty()) {
                btnCau[i].setBackground(Color.GREEN);
            } else {
                btnCau[i].setBackground(null);
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
        for (String da : dapAnChon) {
            if (da.isEmpty()) soChuaTra++;
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
        
        // Nộp bài
        BaiThiDTO ketQua = sinhVienThiBUS.nopBai(maBaiThi, chiTiet);
        
        if (ketQua != null) {
            String thongBao = String.format(
                "Nộp bài thành công!\n\n" +
                "Số câu đúng: %d/%d\n" +
                "Điểm số: %.2f/10",
                ketQua.getSoCauDung(),
                ketQua.getSoCauDung() + ketQua.getSoCauSai(),
                ketQua.getDiemSo()
            );
            JOptionPane.showMessageDialog(this, thongBao, "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi khi nộp bài!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        
        this.dispose();
        if (parentDashboard != null) {
            parentDashboard.hoanThanhThi();
        }
    }
}
