/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: VaiTro - Data Transfer Object
 */
package dto;

public class VaiTroDTO {
    // Hằng số vai trò
    public static final int ADMIN = 1;
    public static final int GIANG_VIEN = 2;
    public static final int SINH_VIEN = 3;
    
    private int maVaiTro;      // ma_vai_tro - Mã vai trò (PK)
    private String tenVaiTro;  // ten_vai_tro - Tên vai trò

    public VaiTroDTO() {
    }

    public VaiTroDTO(int maVaiTro, String tenVaiTro) {
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
    }

    public int getMaVaiTro() {
        return this.maVaiTro;
    }

    public void setMaVaiTro(int maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public String getTenVaiTro() {
        return this.tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }

    @Override
    public String toString() {
        return this.tenVaiTro;
    }
}
