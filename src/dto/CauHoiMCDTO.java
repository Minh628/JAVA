/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: CauHoiMC - Câu hỏi trắc nghiệm (Multiple Choice)
 * Kế thừa từ CauHoiDTO
 */
package dto;

public class CauHoiMCDTO extends CauHoiDTO {
    
    private String noiDungA;      // noi_dung_A - Đáp án A
    private String noiDungB;      // noi_dung_B - Đáp án B
    private String noiDungC;      // noi_dung_C - Đáp án C
    private String noiDungD;      // noi_dung_D - Đáp án D
    private String dapAnDung;     // noi_dung_dung - Đáp án đúng (A/B/C/D)

    public CauHoiMCDTO() {
        super();
        setLoaiCauHoi(LOAI_TRAC_NGHIEM);
    }

    public CauHoiMCDTO(int maCauHoi, int maMon, int maGV, String noiDungCauHoi, String mucDo,
                       String noiDungA, String noiDungB, String noiDungC, String noiDungD, String dapAnDung) {
        super(maCauHoi, maMon, maGV, noiDungCauHoi, mucDo);
        setLoaiCauHoi(LOAI_TRAC_NGHIEM);
        this.noiDungA = noiDungA;
        this.noiDungB = noiDungB;
        this.noiDungC = noiDungC;
        this.noiDungD = noiDungD;
        this.dapAnDung = dapAnDung;
    }

    // Implement abstract methods từ CauHoiDTO
    @Override
    public String getNoiDungA() {
        return this.noiDungA;
    }

    @Override
    public void setNoiDungA(String noiDungA) {
        this.noiDungA = noiDungA;
    }

    @Override
    public String getNoiDungB() {
        return this.noiDungB;
    }

    @Override
    public void setNoiDungB(String noiDungB) {
        this.noiDungB = noiDungB;
    }

    @Override
    public String getNoiDungC() {
        return this.noiDungC;
    }

    @Override
    public void setNoiDungC(String noiDungC) {
        this.noiDungC = noiDungC;
    }

    @Override
    public String getNoiDungD() {
        return this.noiDungD;
    }

    @Override
    public void setNoiDungD(String noiDungD) {
        this.noiDungD = noiDungD;
    }

    @Override
    public String getDapAnDung() {
        return this.dapAnDung;
    }

    @Override
    public void setDapAnDung(String dapAnDung) {
        this.dapAnDung = dapAnDung;
    }

    @Override
    public String getNoiDungDung() {
        return this.dapAnDung;
    }

    @Override
    public void setNoiDungDung(String noiDungDung) {
        this.dapAnDung = noiDungDung;
    }

    // CauHoiMC không có danh sách từ - trả về null
    @Override
    public String getDanhSachTu() {
        return null;
    }

    @Override
    public void setDanhSachTu(String danhSachTu) {
        // Không làm gì - MC không có danh sách từ
    }

    @Override
    public boolean isTracNghiem() {
        return true;
    }

    @Override
    public boolean isDienKhuyet() {
        return false;
    }

    /**
     * Lấy nội dung đáp án theo ký tự (A, B, C, D)
     */
    public String getNoiDungDapAn(String dapAn) {
        if (dapAn == null) return null;
        switch (dapAn.toUpperCase()) {
            case "A": return noiDungA;
            case "B": return noiDungB;
            case "C": return noiDungC;
            case "D": return noiDungD;
            default: return null;
        }
    }

    /**
     * Kiểm tra đáp án có đúng không
     */
    public boolean kiemTraDapAn(String dapAnChon) {
        if (dapAnDung == null || dapAnChon == null) return false;
        return dapAnDung.equalsIgnoreCase(dapAnChon);
    }

    @Override
    public String toString() {
        return "Câu " + getMaCauHoi() + " [MC]: " + getNoiDungCauHoi();
    }
}
