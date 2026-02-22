/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: BaiThiBUS - Xử lý logic nghiệp vụ Bài thi
 * Gọi BaiThiDAO và ChiTietBaiThiDAO để quản lý bài thi và chi tiết bài thi
 */
package bus;

import dao.BaiThiDAO;
import dao.CauHoiDAO;
import dao.ChiTietBaiThiDAO;
import dao.DeThiDAO;
import dao.HocPhanDAO;
import dto.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BaiThiBUS {
    private BaiThiDAO baiThiDAO;
    private ChiTietBaiThiDAO chiTietBaiThiDAO;
    private CauHoiDAO cauHoiDAO;
    private DeThiDAO deThiDAO;
    private HocPhanDAO hocPhanDAO;

    public BaiThiBUS() {
        this.baiThiDAO = new BaiThiDAO();
        this.chiTietBaiThiDAO = new ChiTietBaiThiDAO();
        this.cauHoiDAO = new CauHoiDAO();
        this.deThiDAO = new DeThiDAO();
        this.hocPhanDAO = new HocPhanDAO();
    }

    /**
     * Bắt đầu làm bài thi - tạo bài thi mới
     * @return Mã bài thi mới, -1 nếu đã thi hoặc thất bại
     */
    public int batDauLamBai(int maDeThi, int maSV) {
        try {
            // Kiểm tra đã thi chưa
            if (baiThiDAO.checkDaThi(maDeThi, maSV)) {
                return -1;
            }

            // Tạo bài thi mới
            BaiThiDTO baiThi = new BaiThiDTO();
            baiThi.setMaDeThi(maDeThi);
            baiThi.setMaSV(maSV);
            baiThi.setThoiGianBatDau(new Timestamp(System.currentTimeMillis()));
            baiThi.setNgayThi(new Date(System.currentTimeMillis()));
            baiThi.setSoCauDung(0);
            baiThi.setSoCauSai(0);
            baiThi.setDiemSo(0);

            if (baiThiDAO.insert(baiThi)) {
                return baiThi.getMaBaiThi();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Cập nhật kết quả sau khi chấm điểm
     */
    public boolean capNhatKetQua(int maBaiThi, int soCauDung, int soCauSai, float diemSo) {
        try {
            BaiThiDTO baiThi = baiThiDAO.getById(maBaiThi);
            if (baiThi != null) {
                baiThi.setThoiGianNop(new Timestamp(System.currentTimeMillis()));
                baiThi.setSoCauDung(soCauDung);
                baiThi.setSoCauSai(soCauSai);
                baiThi.setDiemSo(diemSo);
                return baiThiDAO.updateKetQua(baiThi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy lịch sử bài thi của sinh viên
     */
    public List<BaiThiDTO> getLichSuBaiThi(int maSV) {
        try {
            return baiThiDAO.getBySinhVien(maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm lịch sử bài thi theo từ khóa và loại tìm kiếm
     */
    public List<BaiThiDTO> timKiemLichSu(int maSV, String keyword, String loai) {
        List<BaiThiDTO> result = new ArrayList<>();
        try {
            String key = keyword == null ? "" : keyword.trim().toLowerCase();
            List<BaiThiDTO> danhSach = getLichSuBaiThi(maSV);

            for (BaiThiDTO bt : danhSach) {
                if (matchLichSuFilter(bt, key, loai)) {
                    result.add(bt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchLichSuFilter(BaiThiDTO bt, String keyword, String loai) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }

        DeThiDTO deThi = null;
        try {
            deThi = deThiDAO.getById(bt.getMaDeThi());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String tenDeThi = deThi != null && deThi.getTenDeThi() != null
                ? deThi.getTenDeThi().toLowerCase()
                : "";
        String tenHocPhan = "";
        if (deThi != null) {
            try {
                HocPhanDTO hocPhan = hocPhanDAO.getById(deThi.getMaHocPhan());
                if (hocPhan != null && hocPhan.getTenMon() != null) {
                    tenHocPhan = hocPhan.getTenMon().toLowerCase();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (loai == null || loai.trim().isEmpty()) {
            loai = "Tất cả";
        }

        return switch (loai) {
            case "Mã bài thi" -> String.valueOf(bt.getMaBaiThi()).contains(keyword);
            case "Đề thi" -> tenDeThi.contains(keyword);
            case "Môn học" -> tenHocPhan.contains(keyword);
            case "Tất cả" -> String.valueOf(bt.getMaBaiThi()).contains(keyword)
                    || tenDeThi.contains(keyword)
                    || tenHocPhan.contains(keyword);
            default -> true;
        };
    }


    /**
     * Lấy bài thi theo mã
     */
    public BaiThiDTO getById(int maBaiThi) {
        try {
            return baiThiDAO.getById(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Kiểm tra sinh viên đã thi đề này chưa
     */
    public boolean daDuThi(int maDeThi, int maSV) {
        try {
            return baiThiDAO.checkDaThi(maDeThi, maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Mặc định true để an toàn
        }
    }

    /**
     * Đếm số bài thi theo đề thi
     */
    public int demBaiThiTheoDeThi(int maDeThi) {
        try {
            return baiThiDAO.countByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Xóa tất cả bài thi và chi tiết bài thi theo sinh viên
     */
    public boolean xoaBaiThiTheoSinhVien(int maSV) {
        try {
            // Xóa chi tiết bài thi trước
            chiTietBaiThiDAO.deleteBySinhVien(maSV);
            // Xóa bài thi
            return baiThiDAO.deleteBySinhVien(maSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ============== Quản lý Chi tiết bài thi ==============

    /**
     * Lấy chi tiết bài thi theo mã bài thi
     */
    public List<ChiTietBaiThiDTO> getChiTietByBaiThi(int maBaiThi) {
        try {
            return chiTietBaiThiDAO.getByBaiThi(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Thêm một chi tiết bài thi
     */
    public boolean themChiTiet(ChiTietBaiThiDTO chiTiet) {
        try {
            return chiTietBaiThiDAO.insert(chiTiet);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm nhiều chi tiết bài thi
     */
    public boolean themChiTietBatch(List<ChiTietBaiThiDTO> danhSach) {
        try {
            return chiTietBaiThiDAO.insertBatch(danhSach);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật đáp án sinh viên
     */
    public boolean capNhatDapAnChiTiet(int maBaiThi, int maCauHoi, String dapAnSV) {
        try {
            return chiTietBaiThiDAO.updateDapAn(maBaiThi, maCauHoi, dapAnSV);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa chi tiết bài thi theo mã bài thi
     */
    public boolean xoaChiTietByBaiThi(int maBaiThi) {
        try {
            return chiTietBaiThiDAO.deleteByBaiThi(maBaiThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tính điểm bài thi từ chi tiết
     * @return mảng [soCauDung, soCauSai, diemSo]
     */
    public float[] tinhDiem(int maBaiThi) {
        List<ChiTietBaiThiDTO> chiTiet = getChiTietByBaiThi(maBaiThi);
        int soCauDung = 0;
        int soCauSai = 0;

        for (ChiTietBaiThiDTO ct : chiTiet) {
            try {
                CauHoiDTO cauHoi = cauHoiDAO.getById(ct.getMaCauHoi());
                if (cauHoi != null && kiemTraDapAn(cauHoi, ct.getDapAnSV())) {
                    soCauDung++;
                } else {
                    soCauSai++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                soCauSai++;
            }
        }

        int tongSoCau = soCauDung + soCauSai;
        float diemSo = tongSoCau > 0 ? (float) soCauDung / tongSoCau * 10 : 0;

        return new float[]{soCauDung, soCauSai, diemSo};
    }

    /**
     * Kiểm tra đáp án sinh viên so với đáp án đúng
     */
    private boolean kiemTraDapAn(CauHoiDTO cauHoi, String dapAnSV) {
        if (dapAnSV == null || dapAnSV.trim().isEmpty()) {
            return false;
        }

        if (cauHoi instanceof CauHoiMCDTO) {
            CauHoiMCDTO mc = (CauHoiMCDTO) cauHoi;
            String dapAnDung = mc.getNoiDungDung();
            String noiDungDapAnSV = null;
            
            // Nếu đáp án là ký hiệu A/B/C/D, chuyển đổi sang nội dung
            String dapAnUpper = dapAnSV.trim().toUpperCase();
            if (dapAnUpper.equals("A") || dapAnUpper.equals("B") || 
                dapAnUpper.equals("C") || dapAnUpper.equals("D")) {
                switch (dapAnUpper) {
                    case "A": noiDungDapAnSV = mc.getNoiDungA(); break;
                    case "B": noiDungDapAnSV = mc.getNoiDungB(); break;
                    case "C": noiDungDapAnSV = mc.getNoiDungC(); break;
                    case "D": noiDungDapAnSV = mc.getNoiDungD(); break;
                }
            } else {
                // Nếu đáp án là nội dung trực tiếp, so sánh trực tiếp
                noiDungDapAnSV = dapAnSV.trim();
            }
            
            return noiDungDapAnSV != null && dapAnDung != null && 
                   noiDungDapAnSV.trim().equalsIgnoreCase(dapAnDung.trim());
        } else if (cauHoi instanceof CauHoiDKDTO) {
            CauHoiDKDTO dk = (CauHoiDKDTO) cauHoi;
            // Sử dụng phương thức kiemTraDapAn của CauHoiDKDTO
            return dk.kiemTraDapAn(dapAnSV);
        }
        return false;
    }

    // ============== Xem điểm sinh viên theo Khoa ==============

    /**
     * Lấy tất cả bài thi của sinh viên thuộc khoa
     * Dùng cho giảng viên xem điểm sinh viên trong khoa mình
     */
    public List<BaiThiDTO> getDanhSachBaiThiTheoKhoa(int maKhoa) {
        try {
            return baiThiDAO.getByKhoa(maKhoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm kiếm bài thi theo khoa
     * @param maKhoa Mã khoa
     * @param keyword Từ khóa tìm kiếm
     * @param loai Loại tìm kiếm: "Tất cả", "MSSV", "Họ tên SV", "Đề thi", "Môn học"
     * @return Danh sách bài thi thỏa điều kiện
     */
    public List<BaiThiDTO> timKiemDiemTheoKhoa(int maKhoa, String keyword, String loai) {
        List<BaiThiDTO> result = new ArrayList<>();
        try {
            keyword = keyword == null ? "" : keyword.trim().toLowerCase();
            List<BaiThiDTO> danhSach = getDanhSachBaiThiTheoKhoa(maKhoa);

            for (BaiThiDTO bt : danhSach) {
                if (matchDiemFilter(bt, keyword, loai)) {
                    result.add(bt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Kiểm tra bài thi có khớp với bộ lọc không
     */
    private boolean matchDiemFilter(BaiThiDTO bt, String keyword, String loai) {
        if (keyword.isEmpty()) {
            return true;
        }

        // Lấy thông tin sinh viên
        String hoTenSV = "";
        String mssv = "";
        try {
            dao.SinhVienDAO sinhVienDAO = new dao.SinhVienDAO();
            dto.SinhVienDTO sv = sinhVienDAO.getById(bt.getMaSV());
            if (sv != null) {
                hoTenSV = sv.getHoTen() != null ? sv.getHoTen().toLowerCase() : "";
                mssv = sv.getTenDangNhap() != null ? sv.getTenDangNhap().toLowerCase() : "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Lấy thông tin đề thi và học phần
        String tenDeThi = "";
        String tenHocPhan = "";
        try {
            DeThiDTO deThi = deThiDAO.getById(bt.getMaDeThi());
            if (deThi != null) {
                tenDeThi = deThi.getTenDeThi() != null ? deThi.getTenDeThi().toLowerCase() : "";
                HocPhanDTO hocPhan = hocPhanDAO.getById(deThi.getMaHocPhan());
                if (hocPhan != null) {
                    tenHocPhan = hocPhan.getTenMon() != null ? hocPhan.getTenMon().toLowerCase() : "";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return switch (loai) {
            case "MSSV" -> mssv.contains(keyword);
            case "Họ tên SV" -> hoTenSV.contains(keyword);
            case "Đề thi" -> tenDeThi.contains(keyword);
            case "Môn học" -> tenHocPhan.contains(keyword);
            case "Tất cả" -> mssv.contains(keyword) 
                          || hoTenSV.contains(keyword) 
                          || tenDeThi.contains(keyword) 
                          || tenHocPhan.contains(keyword);
            default -> true;
        };
    }


    /**
     * Lấy tổng số câu hỏi của đề thi
     */
    public int getTongSoCauHoi(int maDeThi) {
        try {
            DeThiDTO deThi = deThiDAO.getById(maDeThi);
            return deThi != null ? deThi.getSoCauHoi() : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
