/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: HocPhan - Data Transfer Object
 */
package dto;

public class HocPhanDTO {
    private int maHocPhan;   // ma_hoc_phan - Mã học phần (PK)
    private String tenMon;   // ten_mon - Tên môn học
    private int soTin;       // so_tin - Số tín chỉ

    public HocPhanDTO() {
    }

    public HocPhanDTO(int maHocPhan, String tenMon, int soTin) {
        this.maHocPhan = maHocPhan;
        this.tenMon = tenMon;
        this.soTin = soTin;
    }

    public int getMaHocPhan() {
        return this.maHocPhan;
    }

    public void setMaHocPhan(int maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public String getTenMon() {
        return this.tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoTin() {
        return this.soTin;
    }

    public void setSoTin(int soTin) {
        this.soTin = soTin;
    }

    @Override
    public String toString() {
        return this.tenMon + " (" + this.soTin + " tín chỉ)";
    }
}
