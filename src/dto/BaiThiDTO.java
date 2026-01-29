/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: BaiThi - Data Transfer Object
 * Tương ứng với bảng BaiThi trong database
 */
package dto;

import java.sql.Date;
import java.sql.Timestamp;

public class BaiThiDTO {
    private int maBaiThi;            // ma_bai_thi - Mã bài thi (PK)
    private int maDeThi;             // ma_de_thi - Mã đề thi (FK -> DeThi)
    private int maSV;                // ma_sv - Mã sinh viên (FK -> SinhVien)
    private Timestamp thoiGianBatDau;// thoi_gian_bat_dau - Thời điểm bắt đầu làm bài
    private Timestamp thoiGianNop;   // thoi_gian_nop - Thời điểm nộp bài
    private Date ngayThi;            // ngay_thi - Ngày thi
    private int soCauDung;           // so_cau_dung - Số câu trả lời đúng
    private int soCauSai;            // so_cau_sai - Số câu trả lời sai
    private float diemSo;            // diem_so - Điểm số (thang 10)

    public BaiThiDTO() {
    }

    public BaiThiDTO(int maBaiThi, int maDeThi, int maSV, Timestamp thoiGianBatDau, 
                     Timestamp thoiGianNop, Date ngayThi, int soCauDung, int soCauSai, float diemSo) {
        this.maBaiThi = maBaiThi;
        this.maDeThi = maDeThi;
        this.maSV = maSV;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianNop = thoiGianNop;
        this.ngayThi = ngayThi;
        this.soCauDung = soCauDung;
        this.soCauSai = soCauSai;
        this.diemSo = diemSo;
    }

    public BaiThiDTO(int maDeThi, int maSV) {
        this.maDeThi = maDeThi;
        this.maSV = maSV;
        this.thoiGianBatDau = new Timestamp(System.currentTimeMillis());
        this.ngayThi = new Date(System.currentTimeMillis());
        this.soCauDung = 0;
        this.soCauSai = 0;
        this.diemSo = 0.0f;
    }

    public int getMaBaiThi() {
        return this.maBaiThi;
    }

    public void setMaBaiThi(int maBaiThi) {
        this.maBaiThi = maBaiThi;
    }

    public int getMaDeThi() {
        return this.maDeThi;
    }

    public void setMaDeThi(int maDeThi) {
        this.maDeThi = maDeThi;
    }

    public int getMaSV() {
        return this.maSV;
    }

    public void setMaSV(int maSV) {
        this.maSV = maSV;
    }

    public Timestamp getThoiGianBatDau() {
        return this.thoiGianBatDau;
    }

    public void setThoiGianBatDau(Timestamp thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public Timestamp getThoiGianNop() {
        return this.thoiGianNop;
    }

    public void setThoiGianNop(Timestamp thoiGianNop) {
        this.thoiGianNop = thoiGianNop;
    }

    public Date getNgayThi() {
        return this.ngayThi;
    }

    public void setNgayThi(Date ngayThi) {
        this.ngayThi = ngayThi;
    }

    public int getSoCauDung() {
        return this.soCauDung;
    }

    public void setSoCauDung(int soCauDung) {
        this.soCauDung = soCauDung;
    }

    public int getSoCauSai() {
        return this.soCauSai;
    }

    public void setSoCauSai(int soCauSai) {
        this.soCauSai = soCauSai;
    }

    public float getDiemSo() {
        return this.diemSo;
    }

    public void setDiemSo(float diemSo) {
        this.diemSo = diemSo;
    }

    @Override
    public String toString() {
        return "Bài thi " + this.maBaiThi + " - Điểm: " + this.diemSo;
    }
}
