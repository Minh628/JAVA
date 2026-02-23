/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: ChiTietDeThi - Data Transfer Object
 * Bảng trung gian N-N giữa DeThi và CauHoi
 */
package dto;

public class ChiTietDeThiDTO {
    private int maDeThi;   // ma_de_thi - Mã đề thi (PK, FK -> DeThi)
    private int maCauHoi;  // ma_cau_hoi - Mã câu hỏi (PK, FK -> CauHoi)
    private int thuTu;     // Thứ tự câu hỏi trong đề

    public ChiTietDeThiDTO() {
    }

    public ChiTietDeThiDTO(int maDeThi, int maCauHoi) {
        this.maDeThi = maDeThi;
        this.maCauHoi = maCauHoi;
    }

    public ChiTietDeThiDTO(int maDeThi, int maCauHoi, int thuTu) {
        this.maDeThi = maDeThi;
        this.maCauHoi = maCauHoi;
        this.thuTu = thuTu;
    }

    public int getMaDeThi() {
        return this.maDeThi;
    }

    public void setMaDeThi(int maDeThi) {
        this.maDeThi = maDeThi;
    }

    public int getMaCauHoi() {
        return this.maCauHoi;
    }

    public void setMaCauHoi(int maCauHoi) {
        this.maCauHoi = maCauHoi;
    }

    public int getThuTu() {
        return this.thuTu;
    }

    public void setThuTu(int thuTu) {
        this.thuTu = thuTu;
    }
}
