/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: Nganh - Data Transfer Object
 * Tương ứng với bảng Nganh trong database
 */
package dto;

public class NganhDTO {
    private int maNganh;     // ma_nganh - Mã ngành (PK)
    private int maKhoa;      // ma_khoa - Mã khoa (FK -> Khoa)
    private String tenNganh; // ten_nganh - Tên ngành

    public NganhDTO() {
    }

    public NganhDTO(int maNganh, int maKhoa, String tenNganh) {
        this.maNganh = maNganh;
        this.maKhoa = maKhoa;
        this.tenNganh = tenNganh;
    }

    public NganhDTO(int maKhoa, String tenNganh) {
        this.maKhoa = maKhoa;
        this.tenNganh = tenNganh;
    }

    public int getMaNganh() {
        return this.maNganh;
    }

    public void setMaNganh(int maNganh) {
        this.maNganh = maNganh;
    }

    public int getMaKhoa() {
        return this.maKhoa;
    }

    public void setMaKhoa(int maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenNganh() {
        return this.tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }

    @Override
    public String toString() {
        return this.tenNganh;
    }
}
