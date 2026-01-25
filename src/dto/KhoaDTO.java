/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: Khoa - Data Transfer Object
 */
package dto;

public class KhoaDTO {
    private int maKhoa;      // ma_khoa - Mã khoa (PK)
    private String tenKhoa;  // ten_khoa - Tên khoa

    public KhoaDTO() {
    }

    public KhoaDTO(int maKhoa, String tenKhoa) {
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public KhoaDTO(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public int getMaKhoa() {
        return this.maKhoa;
    }

    public void setMaKhoa(int maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return this.tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    @Override
    public String toString() {
        return this.tenKhoa;
    }
}
