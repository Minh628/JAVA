/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: HocPhan - Data Transfer Object
 * Tương ứng với bảng HocPhan trong database
 */
package dto;

public class HocPhanDTO {
    private int maHocPhan;  // ma_hoc_phan - Mã học phần (PK)
    private int maKhoa;     // ma_khoa - Mã khoa (FK)
    private String tenMon;  // ten_mon - Tên môn học
    private int soTin;      // so_tin - Số tín chỉ

    public HocPhanDTO() {
    }

    public HocPhanDTO(int maHocPhan, int maKhoa, String tenMon, int soTin) {
        this.maHocPhan = maHocPhan;
        this.maKhoa = maKhoa;
        this.tenMon = tenMon;
        this.soTin = soTin;
    }

    public int getMaHocPhan() {
        return this.maHocPhan;
    }

    public void setMaHocPhan(int maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public int getMaKhoa() {
        return this.maKhoa;
    }

    public void setMaKhoa(int maKhoa) {
        this.maKhoa = maKhoa;
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
