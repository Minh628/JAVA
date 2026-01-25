/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: DeThi - Data Transfer Object
 */
package dto;

import java.sql.Timestamp;

public class DeThiDTO {
    private int maDeThi;         // ma_de_thi - Mã đề thi (PK)
    private int maHocPhan;       // ma_hoc_phan - Mã học phần (FK -> HocPhan)
    private int maKyThi;         // ma_ky_thi - Mã kỳ thi (FK -> KyThi)
    private int maGV;            // ma_gv - Mã GV ra đề (FK -> GiangVien)
    private String tenDeThi;     // ten_de_thi - Tên đề thi
    private int thoiGianLam;     // thoi_gian_lam - Thời gian làm bài (phút)
    private Timestamp ngayTao;   // ngay_tao - Ngày tạo đề
    private int soCauHoi;        // so_cau_hoi - Tổng số câu hỏi
    
    // Các trường JOIN
    private String tenHocPhan;   // Tên học phần
    private String tenKyThi;     // Tên kỳ thi
    private String tenGV;        // Tên giảng viên

    public DeThiDTO() {
    }

    public DeThiDTO(int maDeThi, int maHocPhan, int maKyThi, int maGV, 
                    String tenDeThi, int thoiGianLam, Timestamp ngayTao, int soCauHoi) {
        this.maDeThi = maDeThi;
        this.maHocPhan = maHocPhan;
        this.maKyThi = maKyThi;
        this.maGV = maGV;
        this.tenDeThi = tenDeThi;
        this.thoiGianLam = thoiGianLam;
        this.ngayTao = ngayTao;
        this.soCauHoi = soCauHoi;
    }

    public int getMaDeThi() {
        return this.maDeThi;
    }

    public void setMaDeThi(int maDeThi) {
        this.maDeThi = maDeThi;
    }

    public int getMaHocPhan() {
        return this.maHocPhan;
    }

    public void setMaHocPhan(int maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public int getMaKyThi() {
        return this.maKyThi;
    }

    public void setMaKyThi(int maKyThi) {
        this.maKyThi = maKyThi;
    }

    public int getMaGV() {
        return this.maGV;
    }

    public void setMaGV(int maGV) {
        this.maGV = maGV;
    }

    public String getTenDeThi() {
        return this.tenDeThi;
    }

    public void setTenDeThi(String tenDeThi) {
        this.tenDeThi = tenDeThi;
    }

    public int getThoiGianLam() {
        return this.thoiGianLam;
    }

    public void setThoiGianLam(int thoiGianLam) {
        this.thoiGianLam = thoiGianLam;
    }

    public Timestamp getNgayTao() {
        return this.ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public int getSoCauHoi() {
        return this.soCauHoi;
    }

    public void setSoCauHoi(int soCauHoi) {
        this.soCauHoi = soCauHoi;
    }

    public String getTenHocPhan() {
        return this.tenHocPhan;
    }

    public void setTenHocPhan(String tenHocPhan) {
        this.tenHocPhan = tenHocPhan;
    }

    public String getTenKyThi() {
        return this.tenKyThi;
    }

    public void setTenKyThi(String tenKyThi) {
        this.tenKyThi = tenKyThi;
    }

    public String getTenGV() {
        return this.tenGV;
    }

    public void setTenGV(String tenGV) {
        this.tenGV = tenGV;
    }

    @Override
    public String toString() {
        return this.tenDeThi;
    }
}
