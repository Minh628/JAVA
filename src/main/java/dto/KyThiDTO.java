/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: KyThi - Data Transfer Object
 */
package dto;

import java.sql.Timestamp;

public class KyThiDTO {
    private int maKyThi;              // ma_ky_thi - Mã kỳ thi (PK)
    private String tenKyThi;          // ten_ky_thi - Tên kỳ thi
    private Timestamp thoiGianBatDau; // thoi_gian_bat_dau - Thời gian bắt đầu
    private Timestamp thoiGianKetThuc;// thoi_gian_ket_thuc - Thời gian kết thúc

    public KyThiDTO() {
    }

    public KyThiDTO(int maKyThi, String tenKyThi, Timestamp thoiGianBatDau, Timestamp thoiGianKetThuc) {
        this.maKyThi = maKyThi;
        this.tenKyThi = tenKyThi;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public int getMaKyThi() {
        return this.maKyThi;
    }

    public void setMaKyThi(int maKyThi) {
        this.maKyThi = maKyThi;
    }

    public String getTenKyThi() {
        return this.tenKyThi;
    }

    public void setTenKyThi(String tenKyThi) {
        this.tenKyThi = tenKyThi;
    }

    public Timestamp getThoiGianBatDau() {
        return this.thoiGianBatDau;
    }

    public void setThoiGianBatDau(Timestamp thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public Timestamp getThoiGianKetThuc() {
        return this.thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(Timestamp thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    @Override
    public String toString() {
        return this.tenKyThi;
    }
}
