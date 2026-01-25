/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: CauHoi - Data Transfer Object (Base class)
 * Các class con: CauHoiMCDTO, CauHoiDKDTO
 */
package dto;

public abstract class CauHoiDTO {
    // Hằng số mức độ câu hỏi
    public static final String MUC_DO_DE = "De";
    public static final String MUC_DO_TRUNG_BINH = "TrungBinh";
    public static final String MUC_DO_KHO = "Kho";
    
    // Hằng số loại câu hỏi
    public static final String LOAI_TRAC_NGHIEM = "MC";  // Multiple Choice
    public static final String LOAI_DIEN_KHUYET = "DK";  // Điền khuyết
    
    // Các trường chung cho tất cả câu hỏi
    private int maCauHoi;         // ma_cau_hoi - Mã câu hỏi (PK)
    private int maMon;            // ma_mon - Mã học phần (FK -> HocPhan)
    private int maGV;             // ma_gv - Mã GV soạn thảo (FK -> GiangVien)
    private String noiDungCauHoi; // noi_dung_cau_hoi - Nội dung câu hỏi
    private String mucDo;         // muc_do - Mức độ (De, TrungBinh, Kho)
    private String loaiCauHoi;    // loai_cau_hoi - Loại câu hỏi (MC, DK)
    
    // Các trường JOIN
    private String tenMon;        // Tên môn học
    private String tenGV;         // Tên giảng viên

    public CauHoiDTO() {
        this.mucDo = MUC_DO_TRUNG_BINH;
    }

    public CauHoiDTO(int maCauHoi, int maMon, int maGV, String noiDungCauHoi, String mucDo) {
        this.maCauHoi = maCauHoi;
        this.maMon = maMon;
        this.maGV = maGV;
        this.noiDungCauHoi = noiDungCauHoi;
        this.mucDo = mucDo;
    }

    public int getMaCauHoi() {
        return this.maCauHoi;
    }

    public void setMaCauHoi(int maCauHoi) {
        this.maCauHoi = maCauHoi;
    }

    public int getMaMon() {
        return this.maMon;
    }

    public void setMaMon(int maMon) {
        this.maMon = maMon;
    }

    public int getMaGV() {
        return this.maGV;
    }

    public void setMaGV(int maGV) {
        this.maGV = maGV;
    }

    public String getNoiDungCauHoi() {
        return this.noiDungCauHoi;
    }

    public void setNoiDungCauHoi(String noiDungCauHoi) {
        this.noiDungCauHoi = noiDungCauHoi;
    }

    public String getMucDo() {
        return this.mucDo;
    }

    public void setMucDo(String mucDo) {
        this.mucDo = mucDo;
    }

    public String getLoaiCauHoi() {
        return this.loaiCauHoi;
    }

    protected void setLoaiCauHoi(String loaiCauHoi) {
        this.loaiCauHoi = loaiCauHoi;
    }

    // Abstract methods - được implement bởi class con
    public abstract String getNoiDungA();
    public abstract void setNoiDungA(String noiDungA);
    public abstract String getNoiDungB();
    public abstract void setNoiDungB(String noiDungB);
    public abstract String getNoiDungC();
    public abstract void setNoiDungC(String noiDungC);
    public abstract String getNoiDungD();
    public abstract void setNoiDungD(String noiDungD);
    public abstract String getNoiDungDung();
    public abstract void setNoiDungDung(String noiDungDung);
    public abstract String getDapAnDung();
    public abstract void setDapAnDung(String dapAnDung);
    public abstract String getDanhSachTu();
    public abstract void setDanhSachTu(String danhSachTu);

    public String getTenMon() {
        return this.tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getTenGV() {
        return this.tenGV;
    }

    public void setTenGV(String tenGV) {
        this.tenGV = tenGV;
    }

    public boolean isTracNghiem() {
        return LOAI_TRAC_NGHIEM.equals(this.loaiCauHoi);
    }

    public boolean isDienKhuyet() {
        return LOAI_DIEN_KHUYET.equals(this.loaiCauHoi);
    }

    @Override
    public String toString() {
        return "Câu " + this.maCauHoi + ": " + this.noiDungCauHoi;
    }
}
