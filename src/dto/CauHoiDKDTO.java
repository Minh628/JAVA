/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * DTO: CauHoiDK - Câu hỏi điền khuyết (Fill in the blank)
 * Kế thừa từ CauHoiDTO
 */
package dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CauHoiDKDTO extends CauHoiDTO {
    
    private String danhSachTu;    // danh_sach_tu - Danh sách từ gợi ý (phân cách bởi |)
    private String dapAnDung;     // Đáp án đúng cho các chỗ trống (phân cách bởi |)

    public CauHoiDKDTO() {
        super();
        setLoaiCauHoi(LOAI_DIEN_KHUYET);
    }

    public CauHoiDKDTO(int maCauHoi, int maMon, int maGV, String noiDungCauHoi, String mucDo,
                       String danhSachTu, String dapAnDung) {
        super(maCauHoi, maMon, maGV, noiDungCauHoi, mucDo);
        setLoaiCauHoi(LOAI_DIEN_KHUYET);
        this.danhSachTu = danhSachTu;
        this.dapAnDung = dapAnDung;
    }

    // Implement abstract methods - CauHoiDK không có A/B/C/D
    @Override
    public String getNoiDungA() {
        return null;
    }

    @Override
    public void setNoiDungA(String noiDungA) {
        // Không làm gì - DK không có đáp án A/B/C/D
    }

    @Override
    public String getNoiDungB() {
        return null;
    }

    @Override
    public void setNoiDungB(String noiDungB) {
        // Không làm gì
    }

    @Override
    public String getNoiDungC() {
        return null;
    }

    @Override
    public void setNoiDungC(String noiDungC) {
        // Không làm gì
    }

    @Override
    public String getNoiDungD() {
        return null;
    }

    @Override
    public void setNoiDungD(String noiDungD) {
        // Không làm gì
    }

    @Override
    public String getDanhSachTu() {
        return this.danhSachTu;
    }

    @Override
    public void setDanhSachTu(String danhSachTu) {
        this.danhSachTu = danhSachTu;
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

    @Override
    public boolean isTracNghiem() {
        return false;
    }

    @Override
    public boolean isDienKhuyet() {
        return true;
    }

    /**
     * Lấy danh sách đáp án đúng dưới dạng List
     */
    public List<String> getDapAnDungList() {
        if (dapAnDung == null || dapAnDung.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(dapAnDung.split("\\|"));
    }


    /**
     * Đếm số chỗ trống trong câu hỏi (đánh dấu bằng _____)
     */
    public int demSoChoTrong() {
        String noiDung = getNoiDungCauHoi();
        if (noiDung == null) return 0;
        
        int count = 0;
        int index = 0;
        while ((index = noiDung.indexOf("_____", index)) != -1) {
            count++;
            index += 5;
        }
        return count;
    }

    /**
     * Kiểm tra đáp án có đúng không
     * @param dapAnNhap Danh sách đáp án người dùng nhập (phân cách bởi |)
     */
    public boolean kiemTraDapAn(String dapAnNhap) {
        if (dapAnDung == null || dapAnNhap == null) return false;
        
        List<String> dapAnDungList = getDapAnDungList();
        List<String> dapAnNhapList = Arrays.asList(dapAnNhap.split("\\|"));
        
        if (dapAnDungList.size() != dapAnNhapList.size()) return false;
        
        for (int i = 0; i < dapAnDungList.size(); i++) {
            if (!dapAnDungList.get(i).trim().equalsIgnoreCase(dapAnNhapList.get(i).trim())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Câu " + getMaCauHoi() + " [DK]: " + getNoiDungCauHoi();
    }
}
