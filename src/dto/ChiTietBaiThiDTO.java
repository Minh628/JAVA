/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: ChiTietBaiThi - Data Transfer Object
 * Bảng trung gian N-N giữa BaiThi và CauHoi
 */
package dto;

public class ChiTietBaiThiDTO {
    private int maBaiThi;         // ma_bai_thi - Mã bài thi (PK, FK -> BaiThi)
    private int maCauHoi;         // ma_cau_hoi - Mã câu hỏi (PK, FK -> CauHoi)
    private String dapAnSV;       // dap_an_sv - Đáp án sinh viên chọn/điền
    
    // Các trường bổ sung
    private boolean laDung;       // Đáp án có đúng không
    private String noiDungCauHoi; // Nội dung câu hỏi
    private String dapAnDung;     // Đáp án đúng
    private String loaiCauHoi;    // Loại câu hỏi (MC/DK)

    public ChiTietBaiThiDTO() {
    }

    public ChiTietBaiThiDTO(int maBaiThi, int maCauHoi, String dapAnSV) {
        this.maBaiThi = maBaiThi;
        this.maCauHoi = maCauHoi;
        this.dapAnSV = dapAnSV;
    }

    public ChiTietBaiThiDTO(int maBaiThi, int maCauHoi, String dapAnSV, String dapAnDung) {
        this.maBaiThi = maBaiThi;
        this.maCauHoi = maCauHoi;
        this.dapAnSV = dapAnSV;
        this.dapAnDung = dapAnDung;
        this.laDung = this.kiemTraDapAn();
    }

    public int getMaBaiThi() {
        return this.maBaiThi;
    }

    public void setMaBaiThi(int maBaiThi) {
        this.maBaiThi = maBaiThi;
    }

    public int getMaCauHoi() {
        return this.maCauHoi;
    }

    public void setMaCauHoi(int maCauHoi) {
        this.maCauHoi = maCauHoi;
    }

    public String getDapAnSV() {
        return this.dapAnSV;
    }

    public void setDapAnSV(String dapAnSV) {
        this.dapAnSV = dapAnSV;
    }

    public boolean isLaDung() {
        return this.laDung;
    }

    public void setLaDung(boolean laDung) {
        this.laDung = laDung;
    }

    public String getNoiDungCauHoi() {
        return this.noiDungCauHoi;
    }

    public void setNoiDungCauHoi(String noiDungCauHoi) {
        this.noiDungCauHoi = noiDungCauHoi;
    }

    public String getDapAnDung() {
        return this.dapAnDung;
    }

    public void setDapAnDung(String dapAnDung) {
        this.dapAnDung = dapAnDung;
    }

    public String getLoaiCauHoi() {
        return this.loaiCauHoi;
    }

    public void setLoaiCauHoi(String loaiCauHoi) {
        this.loaiCauHoi = loaiCauHoi;
    }

    // Kiểm tra đáp án có đúng không
    public boolean kiemTraDapAn() {
        if (this.dapAnDung == null || this.dapAnDung.trim().isEmpty()) {
            return false;
        }
        if (this.dapAnSV == null || this.dapAnSV.trim().isEmpty()) {
            return false;
        }
        return this.dapAnSV.trim().equalsIgnoreCase(this.dapAnDung.trim());
    }

    // Chấm điểm câu hỏi
    public void chamDiem(String dapAnDung) {
        this.dapAnDung = dapAnDung;
        this.laDung = this.kiemTraDapAn();
    }
}
