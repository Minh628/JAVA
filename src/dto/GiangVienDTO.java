/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: GiangVien - Data Transfer Object
 * Tương ứng với bảng GiangVien trong database
 */
package dto;

import java.sql.Timestamp;

public class GiangVienDTO {
    private int maGV;            // ma_gv - Mã giảng viên (PK)
    private int maKhoa;          // ma_khoa - Mã khoa (FK -> Khoa)
    private int maVaiTro;        // ma_vai_tro - Mã vai trò (FK -> VaiTro)
    private String tenDangNhap;  // ten_dang_nhap - Tên đăng nhập
    private String matKhau;      // mat_khau - Mật khẩu (MD5)
    private String ho;           // ho - Họ và tên đệm
    private String ten;          // ten - Tên
    private String email;        // email - Email liên hệ
    private Timestamp ngayTao;   // ngay_tao - Ngày tạo tài khoản
    private boolean trangThai;   // trang_thai - Trạng thái (true=hoạt động)

    public GiangVienDTO() {
        this.maVaiTro = 2;  // Mặc định là Giảng viên
        this.trangThai = true;
    }

    public GiangVienDTO(int maGV, int maKhoa, int maVaiTro, String tenDangNhap, 
                        String matKhau, String ho, String ten, String email) {
        this.maGV = maGV;
        this.maKhoa = maKhoa;
        this.maVaiTro = maVaiTro;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.ho = ho;
        this.ten = ten;
        this.email = email;
        this.trangThai = true;
    }

    public int getMaGV() {
        return this.maGV;
    }

    public void setMaGV(int maGV) {
        this.maGV = maGV;
    }

    public int getMaKhoa() {
        return this.maKhoa;
    }

    public void setMaKhoa(int maKhoa) {
        this.maKhoa = maKhoa;
    }

    public int getMaVaiTro() {
        return this.maVaiTro;
    }

    public void setMaVaiTro(int maVaiTro) {
        this.maVaiTro = maVaiTro;
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
        return this.maGV + " - " + this.getHoTen();
    }
}
