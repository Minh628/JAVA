/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: ChiTietBaiThi - Data Transfer Object
 * Tương ứng với bảng ChiTietBaiThi trong database
 * Bảng trung gian N-N giữa BaiThi và CauHoi
 */
package dto;

public class ChiTietBaiThiDTO {
    private int maBaiThi;   // ma_bai_thi - Mã bài thi (PK, FK -> BaiThi)
    private int maCauHoi;   // ma_cau_hoi - Mã câu hỏi (PK, FK -> CauHoi)
    private String dapAnSV; // dap_an_sv - Đáp án sinh viên chọn/điền

    public ChiTietBaiThiDTO() {
    }

    public ChiTietBaiThiDTO(int maBaiThi, int maCauHoi, String dapAnSV) {
        this.maBaiThi = maBaiThi;
        this.maCauHoi = maCauHoi;
        this.dapAnSV = dapAnSV;
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

    @Override
    public String toString() {
        return "ChiTiet[BaiThi=" + this.maBaiThi + ", CauHoi=" + this.maCauHoi + "]";
    }
}
