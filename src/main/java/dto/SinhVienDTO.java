/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: SinhVien - Data Transfer Object
 * Tương ứng với bảng SinhVien trong database
 */
package dto;

import java.sql.Timestamp;

public class SinhVienDTO {
    private int maSV;            // ma_sv - Mã sinh viên (PK)
    private int maVaiTro;        // ma_vai_tro - Mã vai trò (FK -> VaiTro)
    private int maNganh;         // ma_nganh - Mã ngành học (FK -> Nganh)
    private String tenDangNhap;  // ten_dang_nhap - Mã số sinh viên
    private String matKhau;      // mat_khau - Mật khẩu (MD5)
    private String ho;           // ho - Họ và tên đệm
    private String ten;          // ten - Tên
    private String email;        // email - Email liên hệ
    private Timestamp ngayTao;   // ngay_tao - Ngày tạo tài khoản
    private boolean trangThai;   // trang_thai - Trạng thái (true=hoạt động)

    public SinhVienDTO() {
        this.maVaiTro = 3; // Mặc định là Sinh viên
        this.trangThai = true;
    }

    public SinhVienDTO(int maSV, int maNganh, String tenDangNhap, String matKhau,
            String ho, String ten, String email) {
        this.maSV = maSV;
        this.maVaiTro = 3;
        this.maNganh = maNganh;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.ho = ho;
        this.ten = ten;
        this.email = email;
        this.trangThai = true;
    }

    public int getMaSV() {
        return this.maSV;
    }

    public void setMaSV(int maSV) {
        this.maSV = maSV;
    }

    public int getMaVaiTro() {
        return this.maVaiTro;
    }

    public void setMaVaiTro(int maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public int getMaNganh() {
        return this.maNganh;
    }

    public void setMaNganh(int maNganh) {
        this.maNganh = maNganh;
    }

    public String getTenDangNhap() {
        return this.tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return this.matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getHo() {
        return this.ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return this.ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getHoTen() {
        return this.ho + " " + this.ten;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getNgayTao() {
        return this.ngayTao;
    }

    public void setNgayTao(Timestamp ngayTao) {
        this.ngayTao = ngayTao;
    }

    public boolean isTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return this.maSV + " - " + this.getHoTen();
    }
}
