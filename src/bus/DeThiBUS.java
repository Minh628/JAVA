/*
 * Hệ thống thi trắc nghiệm trực tuyến
 * BUS: DeThiBUS - Xử lý logic nghiệp vụ Đề thi
 * Gọi DeThiDAO, ChiTietDeThiDAO và BaiThiDAO để quản lý đề thi, chi tiết đề thi và ràng buộc bài thi
 */
package bus;

import dao.BaiThiDAO;
import dao.ChiTietDeThiDAO;
import dao.DeThiDAO;
import dto.ChiTietDeThiDTO;
import dto.DeThiDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.SearchCondition;

public class DeThiBUS {
    private DeThiDAO deThiDAO;
    private ChiTietDeThiDAO chiTietDeThiDAO;
    private BaiThiDAO baiThiDAO;

    // Cache theo giảng viên
    private static ArrayList<DeThiDTO> danhSachDeThi = null;
    private static int lastMaGV = -1;

    public DeThiBUS() {
        this.deThiDAO = new DeThiDAO();
        this.chiTietDeThiDAO = new ChiTietDeThiDAO();
        this.baiThiDAO = new BaiThiDAO();
    }

    /**
     * Lấy danh sách đề thi theo giảng viên
     */
    public List<DeThiDTO> getDanhSachDeThi(int maGV) {
        if (danhSachDeThi == null || lastMaGV != maGV) {
            try {
                danhSachDeThi = new ArrayList<>(deThiDAO.getByGiangVien(maGV));
                lastMaGV = maGV;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return danhSachDeThi;
    }

    /**
     * Lấy tất cả đề thi
     */
    public List<DeThiDTO> getAllDeThi() {
        try {
            return deThiDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy đề thi theo kỳ thi
     */
    public List<DeThiDTO> getDeThiTheoKyThi(int maKyThi) {
        try {
            return deThiDAO.getByKyThi(maKyThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy đề thi theo kỳ thi và khoa (cho sinh viên)
     */
    public List<DeThiDTO> getDeThiTheoKyThiVaKhoa(int maKyThi, int maKhoa) {
        try {
            return deThiDAO.getByKyThiAndKhoa(maKyThi, maKhoa);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy đề thi theo mã
     */
    public DeThiDTO getById(int maDeThi) {
        try {
            return deThiDAO.getById(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Thêm đề thi mới
     */
    public boolean themDeThi(DeThiDTO deThi) {
        try {
            if (deThiDAO.insert(deThi)) {
                if (lastMaGV == deThi.getMaGV()) {
                    danhSachDeThi = new ArrayList<>(deThiDAO.getByGiangVien(deThi.getMaGV()));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật đề thi
     */
    public boolean capNhatDeThi(DeThiDTO deThi) {
        try {
            if (deThiDAO.update(deThi)) {
                DeThiDTO updated = deThiDAO.getById(deThi.getMaDeThi());
                if (danhSachDeThi != null) {
                    for (int i = 0; i < danhSachDeThi.size(); i++) {
                        if (danhSachDeThi.get(i).getMaDeThi() == deThi.getMaDeThi()) {
                            danhSachDeThi.set(i, updated);
                            break;
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa đề thi
     */
    public boolean xoaDeThi(int maDeThi) {
        try {
            // Không cho xóa nếu đã có bài thi sử dụng đề thi này
            if (baiThiDAO.countByDeThi(maDeThi) > 0) {
                return false;
            }
            if (deThiDAO.delete(maDeThi)) {
                if (danhSachDeThi != null) {
                    danhSachDeThi.removeIf(dt -> dt.getMaDeThi() == maDeThi);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra có thể xóa đề thi không
     */
    public boolean coTheXoaDeThi(int maDeThi) {
        try {
            return baiThiDAO.countByDeThi(maDeThi) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật số câu hỏi trong đề thi
     */
    public boolean capNhatSoCauHoi(int maDeThi, int soCauHoi) {
        try {
            DeThiDTO deThi = deThiDAO.getById(maDeThi);
            if (deThi != null) {
                deThi.setSoCauHoi(soCauHoi);
                return deThiDAO.update(deThi);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void reloadCache() {
        danhSachDeThi = null;
        lastMaGV = -1;
    }

    /**
     * Tìm kiếm đề thi theo keyword và loại tìm kiếm
     */
    public List<DeThiDTO> timKiem(int maGV, String keyword, String loai, 
            java.util.function.Function<Integer, String> getTenHocPhan,
            java.util.function.Function<Integer, String> getTenKyThi) {
        List<DeThiDTO> result = new ArrayList<>();
        try {
            keyword = keyword.toLowerCase();
            getDanhSachDeThi(maGV);

            for (DeThiDTO dt : danhSachDeThi) {
                if (matchFilter(dt, keyword, loai, getTenHocPhan, getTenKyThi)) {
                    result.add(dt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Tìm kiếm nâng cao với nhiều điều kiện
     */
    public List<DeThiDTO> timKiemNangCao(int maGV, List<SearchCondition> conditions, String logic,
            java.util.function.Function<Integer, String> getTenHocPhan,
            java.util.function.Function<Integer, String> getTenKyThi) {
        List<DeThiDTO> result = new ArrayList<>();
        try {
            getDanhSachDeThi(maGV);
            
            for (DeThiDTO dt : danhSachDeThi) {
                boolean match = evaluateConditions(dt, conditions, logic, getTenHocPhan, getTenKyThi);
                if (match) {
                    result.add(dt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean matchFilter(DeThiDTO dt, String keyword, String loai,
            java.util.function.Function<Integer, String> getTenHocPhan,
            java.util.function.Function<Integer, String> getTenKyThi) {
        if (keyword.isEmpty()) return true;
        
        String tenHocPhan = getTenHocPhan.apply(dt.getMaHocPhan());
        String tenKyThi = getTenKyThi.apply(dt.getMaKyThi());
        
        return switch (loai) {
            case "Mã đề" -> String.valueOf(dt.getMaDeThi()).contains(keyword);
            case "Tên đề thi" -> dt.getTenDeThi() != null && 
                    dt.getTenDeThi().toLowerCase().contains(keyword);
            case "Học phần" -> tenHocPhan != null && 
                    tenHocPhan.toLowerCase().contains(keyword);
            case "Kỳ thi" -> tenKyThi != null && 
                    tenKyThi.toLowerCase().contains(keyword);
            case "Tất cả" -> {
                if (String.valueOf(dt.getMaDeThi()).contains(keyword)) yield true;
                if (dt.getTenDeThi() != null && 
                        dt.getTenDeThi().toLowerCase().contains(keyword)) yield true;
                if (tenHocPhan != null && 
                        tenHocPhan.toLowerCase().contains(keyword)) yield true;
                if (tenKyThi != null && 
                        tenKyThi.toLowerCase().contains(keyword)) yield true;
                yield false;
            }
            default -> true;
        };
    }

    private boolean evaluateConditions(DeThiDTO dt, List<SearchCondition> conditions, String logic,
            java.util.function.Function<Integer, String> getTenHocPhan,
            java.util.function.Function<Integer, String> getTenKyThi) {
        if (conditions.isEmpty()) return true;
        if ("AND".equals(logic)) {
            for (SearchCondition cond : conditions) {
                if (!evaluateSingleCondition(dt, cond, getTenHocPhan, getTenKyThi)) {
                    return false;
                }
            }
            return true;
        }

        if ("OR".equals(logic)) {
            for (SearchCondition cond : conditions) {
                if (evaluateSingleCondition(dt, cond, getTenHocPhan, getTenKyThi)) {
                    return true;
                }
            }
            return false;
        }

        if ("NOT".equals(logic)) {
            for (SearchCondition cond : conditions) {
                if (evaluateSingleCondition(dt, cond, getTenHocPhan, getTenKyThi)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean evaluateSingleCondition(DeThiDTO dt, SearchCondition cond,
            java.util.function.Function<Integer, String> getTenHocPhan,
            java.util.function.Function<Integer, String> getTenKyThi) {
        String fieldValue = switch (cond.getField()) {
            case "Mã đề" -> String.valueOf(dt.getMaDeThi());
            case "Tên đề thi" -> dt.getTenDeThi();
            case "Học phần" -> getTenHocPhan.apply(dt.getMaHocPhan());
            case "Kỳ thi" -> getTenKyThi.apply(dt.getMaKyThi());
            case "Số câu" -> String.valueOf(dt.getSoCauHoi());
            case "Thời gian" -> String.valueOf(dt.getThoiGianLam());
            default -> "";
        };
        
        if (fieldValue == null) fieldValue = "";
        return cond.evaluate(fieldValue);
    }

    // ============== Quản lý Chi tiết đề thi ==============

    /**
     * Lấy danh sách chi tiết đề thi theo mã đề thi
     */
    public List<ChiTietDeThiDTO> getChiTietByDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.getByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách mã câu hỏi trong đề thi
     */
    public List<Integer> getMaCauHoiByDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.getMaCauHoiByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Thêm một câu hỏi vào đề thi
     */
    public boolean themCauHoiVaoDeThi(ChiTietDeThiDTO chiTiet) {
        try {
            return chiTietDeThiDAO.insert(chiTiet);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm nhiều câu hỏi vào đề thi
     */
    public boolean themNhieuCauHoiVaoDeThi(int maDeThi, List<Integer> danhSachMaCauHoi) {
        try {
            List<ChiTietDeThiDTO> chiTietList = new ArrayList<>();
            int thuTu = chiTietDeThiDAO.getMaxThuTu(maDeThi) + 1;
            for (int maCauHoi : danhSachMaCauHoi) {
                ChiTietDeThiDTO ct = new ChiTietDeThiDTO();
                ct.setMaDeThi(maDeThi);
                ct.setMaCauHoi(maCauHoi);
                ct.setThuTu(thuTu++);
                chiTietList.add(ct);
            }
            return chiTietDeThiDAO.insertBatch(chiTietList);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm batch chi tiết đề thi
     */
    public boolean themChiTietBatch(List<ChiTietDeThiDTO> danhSach) {
        try {
            return chiTietDeThiDAO.insertBatch(danhSach);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa một câu hỏi khỏi đề thi
     */
    public boolean xoaCauHoiKhoiDeThi(int maDeThi, int maCauHoi) {
        try {
            return chiTietDeThiDAO.delete(maDeThi, maCauHoi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả câu hỏi trong đề thi
     */
    public boolean xoaTatCaCauHoiTrongDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.deleteByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đếm số câu hỏi trong đề thi
     */
    public int demCauHoiTrongDeThi(int maDeThi) {
        try {
            return chiTietDeThiDAO.countByDeThi(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Lấy thứ tự lớn nhất trong đề thi
     */
    public int getMaxThuTuChiTiet(int maDeThi) {
        try {
            return chiTietDeThiDAO.getMaxThuTu(maDeThi);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


}
