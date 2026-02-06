-- =====================================================
-- DATABASE: HỆ THỐNG THI TRẮC NGHIỆM TRỰC TUYẾN
-- Tác giả: Online Examination System
-- Ngày tạo: 2024
-- Mô tả: Script tạo CSDL theo ERD chuẩn
-- =====================================================

-- Xóa database cũ nếu tồn tại và tạo mới
DROP DATABASE IF EXISTS thi_trac_nghiem;
CREATE DATABASE thi_trac_nghiem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE thi_trac_nghiem;

-- =====================================================
-- KHỐI CẤU TRÚC TỔ CHỨC & NGƯỜI DÙNG
-- =====================================================

-- Bảng Khoa: Quản lý thông tin các khoa trong trường
CREATE TABLE Khoa (
    ma_khoa INT PRIMARY KEY AUTO_INCREMENT,  -- Mã khoa (PK, tự tăng)
    ten_khoa VARCHAR(100) NOT NULL           -- Tên khoa
) ENGINE=InnoDB COMMENT='Bảng lưu trữ thông tin các khoa';

-- Bảng Ngành: Quản lý các ngành học thuộc khoa
CREATE TABLE Nganh (
    ma_nganh INT PRIMARY KEY AUTO_INCREMENT, -- Mã ngành (PK, tự tăng)
    ma_khoa INT NOT NULL,                    -- Mã khoa (FK -> Khoa)
    ten_nganh VARCHAR(100) NOT NULL,         -- Tên ngành
    FOREIGN KEY (ma_khoa) REFERENCES Khoa(ma_khoa) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng lưu trữ thông tin các ngành học';

-- Bảng VaiTro: Phân quyền người dùng trong hệ thống
CREATE TABLE VaiTro (
    ma_vai_tro INT PRIMARY KEY AUTO_INCREMENT, -- Mã vai trò (PK, tự tăng)
    ten_vai_tro VARCHAR(50) NOT NULL           -- Tên vai trò (Trưởng Khoa, Giảng Viên, Sinh Viên)
) ENGINE=InnoDB COMMENT='Bảng lưu trữ các vai trò trong hệ thống';

-- Bảng GiangVien: Quản lý thông tin giảng viên (bao gồm Trưởng Khoa)
CREATE TABLE GiangVien (
    ma_gv INT PRIMARY KEY AUTO_INCREMENT,     -- Mã giảng viên (PK, tự tăng)
    ma_khoa INT,                              -- Mã khoa trực thuộc (FK -> Khoa)
    ma_vai_tro INT NOT NULL,                  -- Mã vai trò (FK -> VaiTro)
    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,-- Tên đăng nhập (unique)
    mat_khau VARCHAR(255) NOT NULL,           -- Mật khẩu (đã mã hóa MD5)
    ho VARCHAR(50) NOT NULL,                  -- Họ và tên đệm
    ten VARCHAR(50) NOT NULL,                 -- Tên
    email VARCHAR(100),                       -- Email liên hệ
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP, -- Ngày tạo tài khoản
    trang_thai BOOLEAN DEFAULT TRUE,          -- Trạng thái: TRUE=hoạt động, FALSE=bị khóa
    FOREIGN KEY (ma_khoa) REFERENCES Khoa(ma_khoa) ON DELETE SET NULL,
    FOREIGN KEY (ma_vai_tro) REFERENCES VaiTro(ma_vai_tro)
) ENGINE=InnoDB COMMENT='Bảng lưu trữ thông tin giảng viên và trưởng khoa';

-- Bảng SinhVien: Quản lý thông tin sinh viên
CREATE TABLE SinhVien (
    ma_sv INT PRIMARY KEY AUTO_INCREMENT,     -- Mã sinh viên (PK, tự tăng)
    ma_vai_tro INT NOT NULL,                  -- Mã vai trò (FK -> VaiTro, luôn = 3)
    ma_nganh INT,                             -- Mã ngành học (FK -> Nganh)
    ten_dang_nhap VARCHAR(50) UNIQUE NOT NULL,-- Mã số sinh viên (unique)
    mat_khau VARCHAR(255) NOT NULL,           -- Mật khẩu (đã mã hóa MD5)
    ho VARCHAR(50) NOT NULL,                  -- Họ và tên đệm
    ten VARCHAR(50) NOT NULL,                 -- Tên
    email VARCHAR(100),                       -- Email liên hệ
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP, -- Ngày tạo tài khoản
    trang_thai BOOLEAN DEFAULT TRUE,          -- Trạng thái: TRUE=hoạt động, FALSE=bị khóa
    FOREIGN KEY (ma_vai_tro) REFERENCES VaiTro(ma_vai_tro),
    FOREIGN KEY (ma_nganh) REFERENCES Nganh(ma_nganh) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='Bảng lưu trữ thông tin sinh viên';

-- =====================================================
-- KHỐI HỌC PHẦN & NGÂN HÀNG CÂU HỎI
-- =====================================================

-- Bảng HocPhan: Quản lý các môn học/học phần
CREATE TABLE HocPhan (
    ma_hoc_phan INT PRIMARY KEY AUTO_INCREMENT, -- Mã học phần (PK, tự tăng)
    ma_khoa INT NOT NULL,                       -- Mã khoa (FK -> Khoa)
    ten_mon VARCHAR(100) NOT NULL,              -- Tên môn học
    so_tin INT DEFAULT 3,                       -- Số tín chỉ
    FOREIGN KEY (ma_khoa) REFERENCES Khoa(ma_khoa) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng lưu trữ thông tin các học phần';

-- Bảng CauHoi: Ngân hàng câu hỏi chung (bảng cha)
CREATE TABLE CauHoi (
    ma_cau_hoi INT PRIMARY KEY AUTO_INCREMENT,  -- Mã câu hỏi (PK, tự tăng)
    ma_mon INT NOT NULL,                        -- Mã học phần (FK -> HocPhan)
    ma_gv INT NOT NULL,                         -- Mã GV soạn thảo (FK -> GiangVien)
    noi_dung_cau_hoi TEXT NOT NULL,             -- Nội dung câu hỏi
    muc_do ENUM('De', 'TrungBinh', 'Kho') DEFAULT 'TrungBinh', -- Mức độ khó
    loai_cau_hoi ENUM('MC', 'DK') DEFAULT 'MC', -- Loại: MC=Trắc nghiệm, DK=Điền khuyết
    FOREIGN KEY (ma_mon) REFERENCES HocPhan(ma_hoc_phan) ON DELETE CASCADE,
    FOREIGN KEY (ma_gv) REFERENCES GiangVien(ma_gv) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng lưu trữ ngân hàng câu hỏi';

-- Bảng CauHoiMC: Chi tiết câu hỏi trắc nghiệm (Multiple Choice)
-- Quan hệ 1-1 với CauHoi
CREATE TABLE CauHoiMC (
    ma_cau_hoi_MC INT PRIMARY KEY,              -- Mã câu hỏi MC (PK, FK -> CauHoi)
    noi_dung_A TEXT NOT NULL,                   -- Nội dung đáp án A
    noi_dung_B TEXT NOT NULL,                   -- Nội dung đáp án B
    noi_dung_C TEXT NOT NULL,                   -- Nội dung đáp án C
    noi_dung_D TEXT NOT NULL,                   -- Nội dung đáp án D
    noi_dung_dung TEXT NOT NULL,                -- Nội dung đáp án đúng (có thể là A/B/C/D hoặc text)
    FOREIGN KEY (ma_cau_hoi_MC) REFERENCES CauHoi(ma_cau_hoi) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng chi tiết câu hỏi trắc nghiệm';

-- Bảng CauHoiDK: Chi tiết câu hỏi điền khuyết
-- Quan hệ 1-1 với CauHoi
CREATE TABLE CauHoiDK (
    ma_cau_hoi_DK INT PRIMARY KEY,              -- Mã câu hỏi DK (PK, FK -> CauHoi)
    danh_sach_tu TEXT,                          -- Danh sách các từ gợi ý (phân tách bởi dấu |)
    noi_dung_dung TEXT NOT NULL,                -- Nội dung đáp án đúng
    FOREIGN KEY (ma_cau_hoi_DK) REFERENCES CauHoi(ma_cau_hoi) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng chi tiết câu hỏi điền khuyết';

-- =====================================================
-- KHỐI ĐỀ THI & KỲ THI
-- =====================================================

-- Bảng KyThi: Quản lý các kỳ thi
CREATE TABLE KyThi (
    ma_ky_thi INT PRIMARY KEY AUTO_INCREMENT,   -- Mã kỳ thi (PK, tự tăng)
    ten_ky_thi VARCHAR(100) NOT NULL,           -- Tên kỳ thi
    thoi_gian_bat_dau DATETIME,                 -- Thời gian bắt đầu kỳ thi
    thoi_gian_ket_thuc DATETIME                 -- Thời gian kết thúc kỳ thi
) ENGINE=InnoDB COMMENT='Bảng lưu trữ thông tin các kỳ thi';

-- Bảng DeThi: Quản lý các đề thi
CREATE TABLE DeThi (
    ma_de_thi INT PRIMARY KEY AUTO_INCREMENT,   -- Mã đề thi (PK, tự tăng)
    ma_hoc_phan INT NOT NULL,                   -- Mã học phần (FK -> HocPhan)
    ma_ky_thi INT NOT NULL,                     -- Mã kỳ thi (FK -> KyThi)
    ma_gv INT NOT NULL,                         -- Mã GV ra đề (FK -> GiangVien)
    ten_de_thi VARCHAR(200) NOT NULL,           -- Tên đề thi
    thoi_gian_lam INT DEFAULT 45,               -- Thời gian làm bài (phút)
    ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP,-- Ngày tạo đề
    so_cau_hoi INT DEFAULT 30,                  -- Tổng số câu hỏi
    FOREIGN KEY (ma_hoc_phan) REFERENCES HocPhan(ma_hoc_phan) ON DELETE CASCADE,
    FOREIGN KEY (ma_ky_thi) REFERENCES KyThi(ma_ky_thi) ON DELETE CASCADE,
    FOREIGN KEY (ma_gv) REFERENCES GiangVien(ma_gv) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng lưu trữ thông tin đề thi';

-- Bảng ChiTietDeThi: Danh sách câu hỏi trong đề thi
-- Quan hệ N-N giữa DeThi và CauHoi
CREATE TABLE ChiTietDeThi (
    ma_de_thi INT NOT NULL,                     -- Mã đề thi (PK, FK -> DeThi)
    ma_cau_hoi INT NOT NULL,                    -- Mã câu hỏi (PK, FK -> CauHoi)
    thu_tu INT DEFAULT 1,                       -- Thứ tự câu hỏi trong đề
    PRIMARY KEY (ma_de_thi, ma_cau_hoi),        -- Khóa chính kép
    FOREIGN KEY (ma_de_thi) REFERENCES DeThi(ma_de_thi) ON DELETE CASCADE,
    FOREIGN KEY (ma_cau_hoi) REFERENCES CauHoi(ma_cau_hoi) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng chi tiết các câu hỏi trong đề thi';

-- =====================================================
-- KHỐI BÀI THI & KẾT QUẢ
-- =====================================================

-- Bảng BaiThi: Quản lý bài thi của sinh viên
CREATE TABLE BaiThi (
    ma_bai_thi INT PRIMARY KEY AUTO_INCREMENT,  -- Mã bài thi (PK, tự tăng)
    ma_de_thi INT NOT NULL,                     -- Mã đề thi (FK -> DeThi)
    ma_sv INT NOT NULL,                         -- Mã sinh viên (FK -> SinhVien)
    thoi_gian_bat_dau DATETIME DEFAULT CURRENT_TIMESTAMP, -- Thời điểm bắt đầu làm bài
    thoi_gian_nop DATETIME NULL,                -- Thời điểm nộp bài
    ngay_thi DATE,                              -- Ngày thi (chỉ lưu ngày)
    so_cau_dung INT DEFAULT 0,                  -- Số câu trả lời đúng
    so_cau_sai INT DEFAULT 0,                   -- Số câu trả lời sai
    diem_so FLOAT DEFAULT 0,                    -- Điểm số (thang 10)
    FOREIGN KEY (ma_de_thi) REFERENCES DeThi(ma_de_thi) ON DELETE CASCADE,
    FOREIGN KEY (ma_sv) REFERENCES SinhVien(ma_sv) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng lưu trữ bài thi của sinh viên';

-- Bảng ChiTietBaiThi: Chi tiết đáp án của sinh viên
-- Quan hệ N-N giữa BaiThi và CauHoi
CREATE TABLE ChiTietBaiThi (
    ma_bai_thi INT NOT NULL,                    -- Mã bài thi (PK, FK -> BaiThi)
    ma_cau_hoi INT NOT NULL,                    -- Mã câu hỏi (PK, FK -> CauHoi)
    dap_an_sv TEXT,                             -- Đáp án sinh viên chọn/điền
    PRIMARY KEY (ma_bai_thi, ma_cau_hoi),       -- Khóa chính kép
    FOREIGN KEY (ma_bai_thi) REFERENCES BaiThi(ma_bai_thi) ON DELETE CASCADE,
    FOREIGN KEY (ma_cau_hoi) REFERENCES CauHoi(ma_cau_hoi) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Bảng chi tiết đáp án của sinh viên';

-- =====================================================
-- DỮ LIỆU MẪU (INSERT DATA)
-- =====================================================

-- ========== 1. INSERT VaiTro ==========
-- Các vai trò trong hệ thống: 1-Trưởng Khoa, 2-Giảng Viên, 3-Sinh Viên
INSERT INTO VaiTro (ma_vai_tro, ten_vai_tro) VALUES 
(1, 'Admin'),   -- Quyền cao nhất: quản lý GV, SV, Ngành, HP
(2, 'Giảng Viên'),    -- Quyền: quản lý câu hỏi, đề thi, xem kết quả
(3, 'Sinh Viên');     -- Quyền: làm bài thi, xem kết quả cá nhân

-- ========== 2. INSERT Khoa ==========
-- Danh sách các khoa trong trường
INSERT INTO Khoa (ma_khoa, ten_khoa) VALUES 
(1, 'Khoa Công nghệ Thông tin'),
(2, 'Khoa Kinh tế'),
(3, 'Khoa Ngoại ngữ'),
(4, 'Khoa Điện - Điện tử');

-- ========== 3. INSERT Nganh ==========
-- Các ngành thuộc từng khoa (mở rộng ngành hot)
INSERT INTO Nganh (ma_nganh, ma_khoa, ten_nganh) VALUES 
-- Ngành thuộc Khoa CNTT (ma_khoa = 1)
(1, 1, 'Công nghệ Thông tin'),
(2, 1, 'Hệ thống Thông tin'),
(3, 1, 'Kỹ thuật Phần mềm'),
(4, 1, 'Khoa học Máy tính'),
(5, 1, 'Khoa học dữ liệu'),
(6, 1, 'Tự động hóa'),
(7, 1, 'An toàn Thông tin'),
-- Ngành thuộc Khoa Kinh tế (ma_khoa = 2)
(8, 2, 'Quản trị Kinh doanh'),
(9, 2, 'Kế toán'),
(10, 2, 'Marketing'),
-- Ngành thuộc Khoa Ngoại ngữ (ma_khoa = 3)
(11, 3, 'Ngôn ngữ Anh'),
(12, 3, 'Ngôn ngữ Nhật'),
-- Ngành thuộc Khoa Điện (ma_khoa = 4)
(13, 4, 'Kỹ thuật Điện'),
(14, 4, 'Điện tử Viễn thông');

-- ========== 4. INSERT HocPhan ==========
-- Danh sách các học phần/môn học (ma_hoc_phan, ma_khoa, ten_mon, so_tin)
INSERT INTO HocPhan (ma_hoc_phan, ma_khoa, ten_mon, so_tin) VALUES 
-- Môn học cho Khoa CNTT (ma_khoa = 1)
(1, 1, 'Lập trình Java', 3),
(2, 1, 'Cơ sở dữ liệu', 3),
(3, 1, 'Cấu trúc dữ liệu và Giải thuật', 3),
(4, 1, 'Mạng máy tính', 3),
(5, 1, 'Hệ điều hành', 3),
(6, 1, 'Lập trình Web', 3),
(7, 1, 'Trí tuệ nhân tạo', 3),
(8, 1, 'An toàn và Bảo mật Thông tin', 3),
(21, 1, 'Điện toán Đám mây', 3),
(22, 1, 'IoT và Cảm biến', 3),
-- Môn học cho Khoa Kinh tế (ma_khoa = 2)
(9, 2, 'Nguyên lý Kinh tế', 3),
(10, 2, 'Quản lý Dự án', 3),
(11, 2, 'Tài chính Doanh nghiệp', 3),
(12, 2, 'Thương mại Điện tử', 3),
-- Môn học cho Khoa Ngoại ngữ (ma_khoa = 3)
(13, 3, 'Kỹ năng Giao tiếp Tiếng Anh', 3),
(14, 3, 'Ngữ pháp Tiếng Anh Nâng cao', 3),
(15, 3, 'Dịch thuật Anh - Việt', 3),
(16, 3, 'Văn hóa Quốc tế', 3),
-- Môn học cho Khoa Điện (ma_khoa = 4)
(17, 4, 'Lý thuyết Điện', 3),
(18, 4, 'Điện tử Kỹ thuật', 3),
(19, 4, 'Hệ thống Điện', 3),
(20, 4, 'Công suất Điện', 3);

-- ========== 5. INSERT GiangVien ==========
-- Password mặc định: 123456 (plain text - code hỗ trợ cả plain text và SHA-256)
-- Admin cho tất cả các khoa (ma_vai_tro = 1)
INSERT INTO GiangVien (ma_gv,ma_vai_tro, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(1, 1, 'admin', '123456', 'Lương Kiện', 'Minh', 'admin@edu.vn', NOW());

-- Giảng viên thuộc Khoa CNTT (ma_vai_tro = 2)
INSERT INTO GiangVien (ma_gv, ma_khoa, ma_vai_tro, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(2, 1, 2, 'giangvien1', '123456', 'Lê Văn', 'Đông', 'giangvien1@edu.vn', NOW()),
(3, 1, 2, 'giangvien2', '123456', 'Võ Thị', 'Minh', 'giangvien2@edu.vn', NOW()),
(4, 1, 2, 'giangvien3', '123456', 'Dương Văn', 'Hà', 'giangvien3@edu.vn', NOW()),
(5, 1, 2, 'giangvien4', '123456', 'Bùi Thị', 'Thu', 'giangvien4@edu.vn', NOW()),
(6, 1, 2, 'giangvien5', '123456', 'Trần Văn', 'Tiến', 'giangvien5@edu.vn', NOW()),
(7, 1, 2, 'giangvien6', '123456', 'Đinh Thị', 'Hương', 'giangvien6@edu.vn', NOW()),
(8, 1, 2, 'giangvien7', '123456', 'Tạ Văn', 'Lâm', 'giangvien7@edu.vn', NOW());

-- Giảng viên thuộc Khoa Kinh tế
INSERT INTO GiangVien (ma_gv, ma_khoa, ma_vai_tro, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(9, 2, 2, 'gv_ke_01', '123456', 'Bùi Thanh', 'Tùng', 'bt_tung@edu.vn', NOW()),
(10, 2, 2, 'gv_ke_02', '123456', 'Cô Thị', 'Yến', 'ct_yen@edu.vn', NOW()),
(11, 2, 2, 'gv_mk_01', '123456', 'Phạm Văn', 'Sáng', 'pv_sang@edu.vn', NOW());

-- Giảng viên thuộc Khoa Ngoại ngữ
INSERT INTO GiangVien (ma_gv, ma_khoa, ma_vai_tro, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(12, 3, 2, 'gv_anh_01', '123456', 'Ngô Minh', 'Anh', 'ngominh@edu.vn', NOW()),
(13, 3, 2, 'gv_anh_02', '123456', 'Lý Thị', 'Hồng', 'lt_hong@edu.vn', NOW());

-- Giảng viên thuộc Khoa Điện
INSERT INTO GiangVien (ma_gv, ma_khoa, ma_vai_tro, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(14, 4, 2, 'gv_dien_01', '123456', 'Vương Tuấn', 'Anh', 'vta_anh@edu.vn', NOW()),
(15, 4, 2, 'gv_dien_02', '123456', 'Trịnh Văn', 'Hoàng', 'tv_hoang@edu.vn', NOW());

-- ========== 6. INSERT SinhVien ==========
-- Sinh viên các ngành (ma_vai_tro = 3)
INSERT INTO SinhVien (ma_sv, ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
-- Sinh viên ngành CNTT
(1, 3, 1, '20110001', '123456', 'Nguyễn Văn', 'An', 'sv1@edu.vn', NOW()),
(2, 3, 1, '20110002', '123456', 'Trần Thị', 'Bình', 'sv2@edu.vn', NOW()),
(3, 3, 1, '20110003', '123456', 'Lê Văn', 'Cường', 'sv3@edu.vn', NOW()),
-- Sinh viên ngành HTTT
(4, 3, 2, '20110004', '123456', 'Phạm Thị', 'Dung', 'sv4@edu.vn', NOW()),
(5, 3, 2, '20110005', '123456', 'Hoàng Văn', 'Em', 'sv5@edu.vn', NOW()),
-- Sinh viên ngành KTPM
(6, 3, 3, '20110006', '123456', 'Vũ Thị', 'Phương', 'sv6@edu.vn', NOW()),
(7, 3, 3, '20110007', '123456', 'Đặng Văn', 'Giang', 'sv7@edu.vn', NOW()),
-- Sinh viên ngành KHMT
(8, 3, 4, '20110008', '123456', 'Bùi Thị', 'Hương', 'sv8@edu.vn', NOW()),
(9, 3, 4, '20110009', '123456', 'Lý Văn', 'Inh', 'sv9@edu.vn', NOW()),
(10, 3, 5, '20110010', '123456', 'Ngô Thị', 'Kim', 'sv10@edu.vn', NOW());

-- Sinh viên ngành Quản trị Kinh doanh (Khoa Kinh tế)
INSERT INTO SinhVien (ma_sv, ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(11, 3, 8, '20110011', '123456', 'Phan Văn', 'Long', 'sv11@edu.vn', NOW()),
(12, 3, 8, '20110012', '123456', 'Bùi Thị', 'Mộc', 'sv12@edu.vn', NOW()),
(13, 3, 8, '20110013', '123456', 'Lê Văn', 'Năm', 'sv13@edu.vn', NOW()),
(14, 3, 8, '20110014', '123456', 'Phạm Thị', 'Oanh', 'sv14@edu.vn', NOW());

-- Sinh viên ngành Kế toán (Khoa Kinh tế)
INSERT INTO SinhVien (ma_sv, ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(15, 3, 9, '20110015', '123456', 'Võ Văn', 'Phát', 'sv15@edu.vn', NOW()),
(16, 3, 9, '20110016', '123456', 'Quỳnh Thị', 'Như', 'sv16@edu.vn', NOW());

-- Sinh viên ngành Ngôn ngữ Anh (Khoa Ngoại ngữ)
INSERT INTO SinhVien (ma_sv, ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(17, 3, 11, '20110017', '123456', 'Tạ Thị', 'Quỳnh', 'sv17@edu.vn', NOW()),
(18, 3, 11, '20110018', '123456', 'Tô Văn', 'Rý', 'sv18@edu.vn', NOW()),
(19, 3, 11, '20110019', '123456', 'Ứng Thị', 'San', 'sv19@edu.vn', NOW()),
(20, 3, 11, '20110020', '123456', 'Vương Văn', 'Tú', 'sv20@edu.vn', NOW());

-- Sinh viên ngành Ngôn ngữ Nhật (Khoa Ngoại ngữ)
INSERT INTO SinhVien (ma_sv, ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(21, 3, 12, '20110021', '123456', 'Vy Thị', 'Vân', 'sv21@edu.vn', NOW()),
(22, 3, 12, '20110022', '123456', 'Xấu Văn', 'Việt', 'sv22@edu.vn', NOW());

-- Sinh viên ngành Kỹ thuật Điện (Khoa Điện)
INSERT INTO SinhVien (ma_sv, ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(23, 3, 13, '20110023', '123456', 'Yêu Thị', 'Xuyên', 'sv23@edu.vn', NOW()),
(24, 3, 13, '20110024', '123456', 'Zieler Văn', 'Yang', 'sv24@edu.vn', NOW()),
(25, 3, 13, '20110025', '123456', 'Âu Văn', 'Zó', 'sv25@edu.vn', NOW()),
(26, 3, 13, '20110026', '123456', 'Bạch Thị', 'Cách', 'sv26@edu.vn', NOW());

-- Sinh viên ngành Điện tử Viễn thông (Khoa Điện)
INSERT INTO SinhVien (ma_sv, ma_vai_tro, ma_nganh, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(27, 3, 14, '20110027', '123456', 'Chế Văn', 'Đạo', 'sv27@edu.vn', NOW()),
(28, 3, 14, '20110028', '123456', 'Dương Thị', 'Ê', 'sv28@edu.vn', NOW());

-- ========== 7. INSERT KyThi ==========
-- Các kỳ thi trong năm học 2025-2026 (vừa thi xong HK1)
INSERT INTO KyThi (ma_ky_thi, ten_ky_thi, thoi_gian_bat_dau, thoi_gian_ket_thuc) VALUES 
(1, 'Kỳ thi Giữa kỳ HK1 2025-2026', '2025-10-01 00:00:00', '2025-10-15 23:59:59'),
(2, 'Kỳ thi Cuối kỳ HK1 2025-2026 (Đã diễn ra tháng 12/2025 - 01/2026)', '2025-12-15 00:00:00', '2026-01-10 23:59:59'),
(3, 'Kỳ thi Phụ HK1 2025-2026', '2026-01-15 00:00:00', '2026-01-31 23:59:59'),
(4, 'Kỳ thi Giữa kỳ HK2 2025-2026', '2026-03-01 00:00:00', '2026-03-15 23:59:59'),
(5, 'Kỳ thi Cuối kỳ HK2 2025-2026', '2026-05-15 00:00:00', '2026-05-30 23:59:59');

-- ========== 8. INSERT CauHoi - Môn Lập trình Java ==========
-- Câu hỏi trắc nghiệm môn Java (ma_mon = 1, ma_gv = 2,3,4)
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
-- Câu hỏi DỄ
(1, 1, 2, 'Java là ngôn ngữ lập trình có đặc điểm nào sau đây?', 'De', 'MC'),
(2, 1, 2, 'Từ khóa nào dùng để khai báo một lớp trong Java?', 'De', 'MC'),
(3, 1, 2, 'Phương thức main() trong Java có cú pháp đúng là?', 'De', 'MC'),
(4, 1, 2, 'Kiểu dữ liệu nào dùng để lưu số nguyên trong Java?', 'De', 'MC'),
(5, 1, 2, 'JVM là viết tắt của?', 'De', 'MC'),
-- Câu hỏi TRUNG BÌNH
(6, 1, 2, 'Vòng lặp nào sẽ thực hiện ít nhất một lần?', 'TrungBinh', 'MC'),
(7, 1, 2, 'Tính đa hình (Polymorphism) trong OOP là gì?', 'TrungBinh', 'MC'),
(8, 1, 3, 'Interface trong Java có thể chứa những gì?', 'TrungBinh', 'MC'),
(9, 1, 3, 'Sự khác biệt giữa == và equals() trong Java?', 'TrungBinh', 'MC'),
(10, 1, 3, 'ArrayList và LinkedList khác nhau ở điểm nào?', 'TrungBinh', 'MC'),
-- Câu hỏi KHÓ
(11, 1, 4, 'Abstract class khác với Interface ở điểm nào chính?', 'Kho', 'MC'),
(12, 1, 4, 'Design Pattern nào đảm bảo chỉ có một instance duy nhất?', 'Kho', 'MC'),
(13, 1, 5, 'Garbage Collection trong Java hoạt động như thế nào?', 'Kho', 'MC'),
(14, 1, 5, 'Thread-safe là gì? Làm sao để đảm bảo thread-safe?', 'Kho', 'MC'),
(15, 1, 5, 'Giải thích cơ chế Reflection trong Java?', 'Kho', 'MC');

-- ========== 9. INSERT CauHoiMC - Đáp án trắc nghiệm Java ==========
INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(1, 'Hướng đối tượng', 'Độc lập nền tảng', 'Đa luồng', 'Tất cả các đáp án trên', 'Tất cả các đáp án trên'),
(2, 'class', 'struct', 'object', 'type', 'class'),
(3, 'public static void main(String[] args)', 'public void main(String args)', 'static void main()', 'void main(String[] args)', 'public static void main(String[] args)'),
(4, 'int', 'float', 'double', 'boolean', 'int'),
(5, 'Java Virtual Machine', 'Java Variable Method', 'Java Visual Memory', 'Java Verified Module', 'Java Virtual Machine'),
(6, 'for', 'while', 'do-while', 'foreach', 'do-while'),
(7, 'Khả năng tạo nhiều đối tượng', 'Một phương thức có thể có nhiều hành vi khác nhau', 'Ẩn giấu dữ liệu', 'Kế thừa từ nhiều lớp', 'Một phương thức có thể có nhiều hành vi khác nhau'),
(8, 'Chỉ hằng số và phương thức abstract', 'Constructor và biến instance', 'Phương thức static và default (từ Java 8)', 'Tất cả các loại biến', 'Phương thức static và default (từ Java 8)'),
(9, '== so sánh tham chiếu, equals() so sánh nội dung', '== và equals() giống nhau', '== so sánh nội dung, equals() so sánh tham chiếu', 'Không có sự khác biệt', '== so sánh tham chiếu, equals() so sánh nội dung'),
(10, 'ArrayList dùng mảng, LinkedList dùng danh sách liên kết', 'ArrayList nhanh hơn khi thêm/xóa', 'LinkedList tốn ít bộ nhớ hơn', 'Không có sự khác biệt', 'ArrayList dùng mảng, LinkedList dùng danh sách liên kết'),
(11, 'Abstract class có thể có constructor, Interface thì không', 'Interface có thể có biến instance', 'Abstract class không thể có phương thức cụ thể', 'Không có sự khác biệt', 'Abstract class có thể có constructor, Interface thì không'),
(12, 'Factory Pattern', 'Singleton Pattern', 'Observer Pattern', 'Builder Pattern', 'Singleton Pattern'),
(13, 'Tự động giải phóng bộ nhớ không còn được tham chiếu', 'Lập trình viên phải gọi thủ công', 'Chỉ chạy khi JVM tắt', 'Không tồn tại trong Java', 'Tự động giải phóng bộ nhớ không còn được tham chiếu'),
(14, 'Code có thể chạy an toàn từ nhiều thread', 'Code chỉ chạy trên một thread', 'Code không cần đồng bộ hóa', 'Thread không thể truy cập đối tượng', 'Code có thể chạy an toàn từ nhiều thread'),
(15, 'Cho phép kiểm tra và thao tác class/method/field tại runtime', 'Chỉ dùng cho debug', 'Là cơ chế kế thừa', 'Không thể thay đổi behavior', 'Cho phép kiểm tra và thao tác class/method/field tại runtime');

-- ========== 10. INSERT CauHoi - Môn CSDL ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
-- Câu hỏi DỄ
(16, 2, 6, 'SQL là viết tắt của?', 'De', 'MC'),
(17, 2, 6, 'Lệnh nào dùng để lấy dữ liệu từ bảng?', 'De', 'MC'),
(18, 2, 6, 'PRIMARY KEY có đặc điểm gì?', 'De', 'MC'),
(19, 2, 6, 'Lệnh nào dùng để thêm dữ liệu vào bảng?', 'De', 'MC'),
(20, 2, 6, 'Kiểu dữ liệu VARCHAR dùng để lưu gì?', 'De', 'MC'),
-- Câu hỏi TRUNG BÌNH
(21, 2, 6, 'FOREIGN KEY dùng để làm gì?', 'TrungBinh', 'MC'),
(22, 2, 7, 'Lệnh JOIN nào trả về tất cả bản ghi từ cả hai bảng?', 'TrungBinh', 'MC'),
(23, 2, 7, 'INDEX trong database có tác dụng gì?', 'TrungBinh', 'MC'),
(24, 2, 7, 'Sự khác biệt giữa WHERE và HAVING?', 'TrungBinh', 'MC'),
(25, 2, 7, 'DISTINCT trong SQL dùng để làm gì?', 'TrungBinh', 'MC'),
-- Câu hỏi KHÓ
(26, 2, 8, 'Giải thích các cấp độ cô lập transaction (Isolation Level)?', 'Kho', 'MC'),
(27, 2, 8, 'Normalization là gì? Các dạng chuẩn hóa?', 'Kho', 'MC'),
(28, 2, 8, 'Deadlock là gì? Cách phòng tránh?', 'Kho', 'MC'),
(29, 2, 8, 'Stored Procedure và Function khác nhau như thế nào?', 'Kho', 'MC'),
(30, 2, 8, 'Sharding và Partitioning khác nhau ở điểm nào?', 'Kho', 'MC');

-- ========== 10. INSERT CauHoi - Môn Nguyên lý Kinh tế (Khoa Kinh tế, ma_mon = 9, ma_gv = 9,10,11) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
-- Câu hỏi DỄ
(36, 9, 9, 'Kinh tế học là khoa học nghiên cứu?', 'De', 'MC'),
(37, 9, 9, 'Cung và cầu ảnh hưởng đến giá như thế nào?', 'De', 'MC'),
(38, 9, 9, 'GDP là viết tắt của?', 'De', 'MC'),
(39, 9, 9, 'Lạm phát là gì?', 'De', 'MC'),
(40, 9, 9, 'Thị trường cạnh tranh hoàn toàn có bao nhiêu người bán?', 'De', 'MC'),
-- Câu hỏi TRUNG BÌNH
(41, 9, 10, 'Lợi suất cân bằng thị trường được xác định bởi?', 'TrungBinh', 'MC'),
(42, 9, 10, 'Độ co giãn của cầu theo giá là gì?', 'TrungBinh', 'MC'),
(43, 9, 10, 'Chi phí cơ hội là gì?', 'TrungBinh', 'MC'),
(44, 9, 10, 'Tỷ lệ thất nghiệp tự nhiên là gì?', 'TrungBinh', 'MC'),
(45, 9, 10, 'Tiền lương thực tế và danh nghĩa khác nhau?', 'TrungBinh', 'MC'),
-- Câu hỏi KHÓ
(46, 9, 11, 'Chính sách tiền tệ có tác dụng gì?', 'Kho', 'MC'),
(47, 9, 11, 'Đường IS-LM biểu diễn mối quan hệ gì?', 'Kho', 'MC'),
(48, 9, 11, 'Tăng trưởng kinh tế ngoài khung sản xuất có nghĩa là gì?', 'Kho', 'MC'),
(49, 9, 11, 'Tỷ giá hối đoái ảnh hưởng đến xuất khẩu như thế nào?', 'Kho', 'MC'),
(50, 9, 11, 'Chỉ số Gini đo lường cái gì?', 'Kho', 'MC');

-- ========== 11. INSERT CauHoi - Môn Kỹ năng Giao tiếp Tiếng Anh (Khoa Ngoại ngữ, ma_mon = 13, ma_gv = 12,13) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
-- Câu hỏi DỄ
(51, 13, 12, 'Câu chào hỏi tiếng Anh chuẩn là gì?', 'De', 'MC'),
(52, 13, 12, 'Phát âm của từ "pronunciation" là gì?', 'De', 'MC'),
(53, 13, 12, 'Present Simple Tense dùng để?', 'De', 'MC'),
(54, 13, 12, 'Tenses nào được sử dụng phổ biến nhất?', 'De', 'MC'),
(55, 13, 12, 'Cách đặt câu hỏi "Have you been there?" là gì?', 'De', 'MC'),
-- Câu hỏi TRUNG BÌNH
(56, 13, 13, 'Past Perfect dùng để diễn tả?', 'TrungBinh', 'MC'),
(57, 13, 13, 'Conditional Clause Type 1 dùng khi?', 'TrungBinh', 'MC'),
(58, 13, 13, 'Phrasal verb "come up with" có nghĩa là?', 'TrungBinh', 'MC'),
(59, 13, 13, 'Collocation là gì?', 'TrungBinh', 'MC'),
(60, 13, 13, 'Discourse marker "nevertheless" được dùng để?', 'TrungBinh', 'MC'),
-- Câu hỏi KHÓ
(61, 13, 12, 'Reported Speech dùng để làm gì?', 'Kho', 'MC'),
(62, 13, 12, 'Mixed Conditional dùng trong tình huống nào?', 'Kho', 'MC'),
(63, 13, 13, 'Idiomatic expression nào có ý nghĩa "rất khó"?', 'Kho', 'MC'),
(64, 13, 13, 'Register shift có tác dụng gì trong giao tiếp?', 'Kho', 'MC'),
(65, 13, 13, 'Tone of voice ảnh hưởng đến sự hiểu lầm như thế nào?', 'Kho', 'MC');

-- ========== 12. INSERT CauHoi - Môn Lý thuyết Điện (Khoa Điện, ma_mon = 17, ma_gv = 14,15) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
-- Câu hỏi DỄ
(66, 17, 14, 'Định luật Ohm phát biểu rằng?', 'De', 'MC'),
(67, 17, 14, 'Dòng điện tính bằng?', 'De', 'MC'),
(68, 17, 14, 'Điện trở được ký hiệu bằng chữ nào?', 'De', 'MC'),
(69, 17, 14, 'Định luật Kirchhoff nói về?', 'De', 'MC'),
(70, 17, 14, 'AC là viết tắt của?', 'De', 'MC'),
-- Câu hỏi TRUNG BÌNH
(71, 17, 15, 'Điện cảm là gì?', 'TrungBinh', 'MC'),
(72, 17, 15, 'Công suất điện tính bằng công thức nào?', 'TrungBinh', 'MC'),
(73, 17, 15, 'Hệ số công suất ảnh hưởng đến?', 'TrungBinh', 'MC'),
(74, 17, 15, 'Transformator là thiết bị dùng để?', 'TrungBinh', 'MC'),
(75, 17, 15, 'Sự cộng hưởng trong mạch LC xảy ra khi?', 'TrungBinh', 'MC'),
-- Câu hỏi KHÓ
(76, 17, 14, 'Phương trình Maxwell đôi mục?', 'Kho', 'MC'),
(77, 17, 14, 'Phương pháp phân tích mạch phức tạp là gì?', 'Kho', 'MC'),
(78, 17, 15, 'Tổn hao tĩnh điện là gì?', 'Kho', 'MC'),
(79, 17, 15, 'Độ nhạy từ của vật liệu ảnh hưởng đến?', 'Kho', 'MC'),
(80, 17, 15, 'Lực điện từ trong dây dẫn mang dòng điện tính như thế nào?', 'Kho', 'MC');

-- ========== 11. INSERT CauHoiMC - Đáp án trắc nghiệm CSDL ==========
INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(16, 'Structured Query Language', 'Simple Query Language', 'Standard Query Language', 'System Query Language', 'Structured Query Language'),
(17, 'INSERT', 'SELECT', 'UPDATE', 'DELETE', 'SELECT'),
(18, 'Duy nhất và không NULL', 'Có thể trùng lặp', 'Có thể NULL', 'Chỉ là số', 'Duy nhất và không NULL'),
(19, 'INSERT INTO', 'ADD INTO', 'CREATE INTO', 'PUT INTO', 'INSERT INTO'),
(20, 'Số nguyên', 'Số thực', 'Chuỗi ký tự', 'Ngày tháng', 'Chuỗi ký tự'),
(21, 'Liên kết giữa các bảng', 'Xóa dữ liệu', 'Thêm dữ liệu', 'Cập nhật dữ liệu', 'Liên kết giữa các bảng'),
(22, 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'FULL OUTER JOIN', 'FULL OUTER JOIN'),
(23, 'Tăng tốc độ truy vấn', 'Bảo mật dữ liệu', 'Sao lưu dữ liệu', 'Nén dữ liệu', 'Tăng tốc độ truy vấn'),
(24, 'WHERE lọc trước GROUP BY, HAVING lọc sau', 'WHERE và HAVING giống nhau', 'HAVING lọc trước GROUP BY', 'WHERE dùng với hàm tổng hợp', 'WHERE lọc trước GROUP BY, HAVING lọc sau'),
(25, 'Loại bỏ các bản ghi trùng lặp', 'Sắp xếp dữ liệu', 'Đếm số bản ghi', 'Giới hạn kết quả', 'Loại bỏ các bản ghi trùng lặp'),
(26, 'READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE', 'Chỉ có một cấp độ', 'LOW, MEDIUM, HIGH', 'NONE, PARTIAL, FULL', 'READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE'),
(27, 'Giảm dư thừa dữ liệu: 1NF, 2NF, 3NF, BCNF', 'Tăng dư thừa dữ liệu', 'Mã hóa dữ liệu', 'Nén dữ liệu', 'Giảm dư thừa dữ liệu: 1NF, 2NF, 3NF, BCNF'),
(28, 'Hai transaction chờ đợi lẫn nhau vô hạn', 'Lỗi kết nối database', 'Mất dữ liệu', 'Lỗi cú pháp SQL', 'Hai transaction chờ đợi lẫn nhau vô hạn'),
(29, 'Procedure không trả về giá trị qua RETURN, Function có', 'Procedure và Function giống nhau', 'Function không thể có tham số', 'Procedure không thể gọi Function', 'Procedure không trả về giá trị qua RETURN, Function có'),
(30, 'Sharding phân tán qua nhiều server, Partitioning trong cùng server', 'Giống nhau hoàn toàn', 'Sharding chỉ cho MySQL', 'Partitioning chỉ cho NoSQL', 'Sharding phân tán qua nhiều server, Partitioning trong cùng server');

-- ========== 12. INSERT CauHoiMC - Đáp án Kinh tế ==========
INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(36, 'Sản xuất hàng hóa', 'Sử dụng tài nguyên hiệu quả', 'Quản lý kinh doanh', 'Lập kế hoạch tài chính', 'Sử dụng tài nguyên hiệu quả'),
(37, 'Giảm khi cung tăng', 'Tăng khi cầu tăng', 'Không liên quan', 'Chỉ phụ thuộc vào tỷ giá', 'Tăng khi cầu tăng'),
(38, 'Gross Domestic Product', 'General Development Program', 'Global Data Process', 'Government Debt Percentage', 'Gross Domestic Product'),
(39, 'Tăng mức giá chung', 'Giảm giá trị đồng tiền', 'Mất sức mua', 'Tất cả các đáp án trên', 'Tất cả các đáp án trên'),
(40, 'Một', 'Hai', 'Nhiều', 'Rất nhiều', 'Nhiều'),
(41, 'Cung và cầu', 'Chỉ giá sản phẩm', 'Chi phí sản xuất', 'Tính cạnh tranh', 'Cung và cầu'),
(42, 'Mức độ thay đổi lượng cầu khi giá thay đổi', 'Tổng lượng cầu', 'Giá sản phẩm', 'Lợi nhuận', 'Mức độ thay đổi lượng cầu khi giá thay đổi'),
(43, 'Giá trị một lựa chọn bị mất vì chọn khác', 'Tổng chi phí sản xuất', 'Lợi nhuận doanh nghiệp', 'Giá thị trường', 'Giá trị một lựa chọn bị mất vì chọn khác'),
(44, 'Tỷ lệ người không có việc làm', 'Tỷ lệ lạm phát', 'Tỷ lệ tăng trưởng', 'Tỷ lệ tiết kiệm', 'Tỷ lệ người không có việc làm'),
(45, 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có', 'Giống nhau', 'Thực tế cao hơn', 'Không liên quan', 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có'),
(46, 'Kiểm soát cung tiền và lãi suất', 'Kiểm soát cung hàng hóa', 'Kiểm soát mức giá', 'Tất cả các đáp án trên', 'Kiểm soát cung tiền và lãi suất'),
(47, 'Mối quan hệ giữa đầu tư, tiết kiệm, lãi suất', 'Mối quan hệ giữa cung cầu', 'Mối quan hệ giữa giá và lượng', 'Không liên quan gì', 'Mối quan hệ giữa đầu tư, tiết kiệm, lãi suất'),
(48, 'Vượt quá khả năng sản xuất hiện tại', 'Giảm sản lượng', 'Phát triển bền vững', 'Chỉ về tính sản xuất', 'Vượt quá khả năng sản xuất hiện tại'),
(49, 'Tỷ giá cao làm giảm xuất khẩu', 'Tỷ giá thấp tốt cho xuất khẩu', 'Tỷ giá không ảnh hưởng', 'Cả A và B đúng', 'Cả A và B đúng'),
(50, 'Bất đẳng thức thu nhập', 'Tỷ lệ lạm phát', 'Tỷ lệ thất nghiệp', 'Tỷ lệ tăng trưởng', 'Bất đẳng thức thu nhập');

-- ========== 13. INSERT CauHoiMC - Đáp án Tiếng Anh ==========
INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(51, 'Hello, how are you?', 'Hey there!', 'What\'s up?', 'Hi mate!', 'Hello, how are you?'),
(52, 'pro-nun-see-ay-shun', 'pro-nounce-ment', 'pro-noun-ci-ay-shun', 'pro-nun-cia-tion', 'pro-nun-see-ay-shun'),
(53, 'Diễn tả hành động lặp lại', 'Sự thật hiển nhiên', 'Thói quen', 'Tất cả các đáp án trên', 'Tất cả các đáp án trên'),
(54, 'Present Simple', 'Past Tense', 'Future Tense', 'Present Perfect', 'Present Simple'),
(55, 'Câu hỏi nghi vấn với kinh nghiệm', 'Câu hỏi với DO/DOES', 'Câu hỏi với TO BE', 'Câu hỏi với WILL', 'Câu hỏi nghi vấn với kinh nghiệm'),
(56, 'Hành động hoàn thành trước hành động quá khứ khác', 'Hành động đang tiến hành', 'Hành động tương lai', 'Hành động hiện tại', 'Hành động hoàn thành trước hành động quá khứ khác'),
(57, 'Tình huống khả thi trong tương lai', 'Tình huống không thể xảy ra', 'Tình huống quá khứ', 'Tình huống hiện tại', 'Tình huống khả thi trong tương lai'),
(58, 'Đề ra, gợi ý', 'Từ bỏ', 'Đến muộn', 'Tuyên bố', 'Đề ra, gợi ý'),
(59, 'Hai hoặc nhiều từ thường đi cùng nhau', 'Từ đồng nghĩa', 'Từ trái nghĩa', 'Từ phức hợp', 'Hai hoặc nhiều từ thường đi cùng nhau'),
(60, 'Chỉ ra thêm thông tin', 'Đưa ra kết luận', 'Chỉ ra sự tương phản', 'Chỉ ra nguyên nhân', 'Chỉ ra sự tương phản'),
(61, 'Chuyển từ trực tiếp sang lời nói gián tiếp', 'Tóm tắt câu chuyện', 'Dịch sang ngôn ngữ khác', 'Giải thích ý nghĩa', 'Chuyển từ trực tiếp sang lời nói gián tiếp'),
(62, 'Diễn tả điều kiện quá khứ với kết quả hiện tại', 'Diễn tả tương lai', 'Diễn tả hiện tại', 'Không được dùng', 'Diễn tả điều kiện quá khứ với kết quả hiện tại'),
(63, 'Between a rock and a hard place', 'Break a leg', 'Call it a day', 'Hit the books', 'Between a rock and a hard place'),
(64, 'Thể hiện mức độ trang trọng hoặc bình thường', 'Thể hiện cảm xúc', 'Thể hiện tốc độ nói', 'Không quan trọng', 'Thể hiện mức độ trang trọng hoặc bình thường'),
(65, 'Thể hiện cảm xúc, ý định, thái độ của người nói', 'Kiểm soát kích thước', 'Kiểm soát tốc độ', 'Không ảnh hưởng', 'Thể hiện cảm xúc, ý định, thái độ của người nói');

-- ========== 14. INSERT CauHoiMC - Đáp án Điện ==========
INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(66, 'V = R + I', 'V = R / I', 'V = R * I', 'V = I / R', 'V = R * I'),
(67, 'Q = V * I', 'I = V / R', 'P = V * I', 'Tất cả các đáp án', 'I = V / R'),
(68, 'R', 'Z', 'X', 'W', 'R'),
(69, 'Tổng điện áp bằng tổng điện áp từng nhánh', 'Tổng dòng điện vào bằng tổng dòng ra', 'Cộng hưởng mạch', 'Quay vòng', 'Tổng dòng điện vào bằng tổng dòng ra'),
(70, 'Alternating Current', 'Amplitude Control', 'Automated Circuit', 'Analog Circuit', 'Alternating Current'),
(71, 'Khả năng tạo từ trường', 'Khả năng chống điện áp', 'Khả năng tạo điện', 'Khả năng cản dòng', 'Khả năng tạo từ trường'),
(72, 'P = V * I * cos(φ)', 'P = V * I', 'P = I² * R', 'Cả A và C', 'Cả A và C'),
(73, 'Mất mát điện năng', 'Hiệu suất hệ thống', 'Tổn hao lực', 'Điều chỉnh dòng', 'Hiệu suất hệ thống'),
(74, 'Biến đổi điện áp AC', 'Chuyển DC thành AC', 'Tinh chỉnh dòng', 'Kiểm soát tần số', 'Biến đổi điện áp AC'),
(75, 'Khi tần số bằng tần số cộng hưởng', 'Khi tần số cao', 'Khi tần số thấp', 'Khi có tải', 'Khi tần số bằng tần số cộng hưởng'),
(76, 'Bốn phương trình cơ bản', 'Hai phương trình', 'Sáu phương trình', 'Một phương trình', 'Bốn phương trình cơ bản'),
(77, 'Phương pháp nút hoặc vòng', 'Phương pháp đơn giản', 'Phương pháp ước lượng', 'Phương pháp so sánh', 'Phương pháp nút hoặc vòng'),
(78, 'Mất điện năng trong điện môi', 'Tạo dòng điện', 'Gia tăng dòng', 'Kiểm soát tần số', 'Mất điện năng trong điện môi'),
(79, 'Từ thông trong vật liệu', 'Dòng điện', 'Điện áp', 'Tần số', 'Từ thông trong vật liệu'),
(80, 'F = I * l * B * sin(θ)', 'F = V * I', 'F = P / I', 'F = R * I²', 'F = I * l * B * sin(θ)');

-- ========== 15. INSERT CauHoi - Bổ sung câu hỏi MC cho các môn còn thiếu ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
-- Cấu trúc dữ liệu và Giải thuật (ma_mon = 3)
(81, 3, 2, 'Cấu trúc dữ liệu nào hoạt động theo nguyên tắc LIFO?', 'De', 'MC'),
(82, 3, 2, 'Độ phức tạp thời gian trung bình của tìm kiếm nhị phân là?', 'TrungBinh', 'MC'),
-- Mạng máy tính (ma_mon = 4)
(83, 4, 3, 'Giao thức nào dùng để truyền trang web?', 'De', 'MC'),
(84, 4, 3, 'Địa chỉ IPv4 có bao nhiêu bit?', 'TrungBinh', 'MC'),
-- Hệ điều hành (ma_mon = 5)
(85, 5, 4, 'Tiến trình (process) là gì?', 'De', 'MC'),
(86, 5, 4, 'Thuật toán lập lịch nào ưu tiên tiến trình có thời gian chạy ngắn nhất?', 'TrungBinh', 'MC'),
-- Lập trình Web (ma_mon = 6)
(87, 6, 5, 'HTML dùng để làm gì?', 'De', 'MC'),
(88, 6, 5, 'HTTP method nào thường dùng để tạo mới dữ liệu?', 'TrungBinh', 'MC'),
-- Trí tuệ nhân tạo (ma_mon = 7)
(89, 7, 6, 'Học có giám sát cần loại dữ liệu nào?', 'De', 'MC'),
(90, 7, 6, 'Thuật toán tìm kiếm theo chiều sâu là?', 'TrungBinh', 'MC'),
-- An toàn và Bảo mật Thông tin (ma_mon = 8)
(91, 8, 7, 'Mã hóa đối xứng sử dụng?', 'De', 'MC'),
(92, 8, 7, 'Xác thực hai lớp (2FA) là?', 'TrungBinh', 'MC'),
-- Tài chính Doanh nghiệp (ma_mon = 11)
(93, 11, 9, 'Giá trị hiện tại (PV) là gì?', 'De', 'MC'),
(94, 11, 9, 'Chỉ số ROE đo lường?', 'TrungBinh', 'MC'),
-- Thương mại Điện tử (ma_mon = 12)
(95, 12, 10, 'Mô hình B2C là gì?', 'De', 'MC'),
(96, 12, 10, 'Giỏ hàng (shopping cart) dùng để?', 'TrungBinh', 'MC'),
-- Dịch thuật Anh - Việt (ma_mon = 15)
(97, 15, 12, 'Từ "economy" dịch là?', 'De', 'MC'),
(98, 15, 12, 'Kỹ thuật "literal translation" là?', 'TrungBinh', 'MC'),
-- Văn hóa Quốc tế (ma_mon = 16)
(99, 16, 13, 'Khái niệm "cultural shock" là?', 'De', 'MC'),
(100, 16, 13, 'Giao tiếp phi ngôn ngữ bao gồm?', 'TrungBinh', 'MC'),
-- Hệ thống Điện (ma_mon = 19)
(101, 19, 14, 'Lưới điện truyền tải thường dùng điện áp?', 'De', 'MC'),
(102, 19, 14, 'Chức năng của trạm biến áp là?', 'TrungBinh', 'MC'),
-- Công suất Điện (ma_mon = 20)
(103, 20, 15, 'Công suất tác dụng ký hiệu là?', 'De', 'MC'),
(104, 20, 15, 'Công thức công suất biểu kiến là?', 'TrungBinh', 'MC'),
-- Điện toán Đám mây (ma_mon = 21)
(105, 21, 8, 'Dịch vụ IaaS cung cấp?', 'De', 'MC'),
(106, 21, 8, 'Ưu điểm chính của điện toán đám mây là?', 'TrungBinh', 'MC'),
-- IoT và Cảm biến (ma_mon = 22)
(107, 22, 8, 'Cảm biến dùng để?', 'De', 'MC'),
(108, 22, 8, 'Giao thức nhẹ phổ biến trong IoT là?', 'TrungBinh', 'MC');

-- ========== 16. INSERT CauHoiMC - Đáp án cho các môn bổ sung ==========
INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(81, 'Stack', 'Queue', 'Tree', 'Graph', 'Stack'),
(82, 'O(n)', 'O(log n)', 'O(n log n)', 'O(1)', 'O(log n)'),
(83, 'FTP', 'SMTP', 'HTTP', 'DNS', 'HTTP'),
(84, '16', '32', '48', '64', '32'),
(85, 'Một chương trình đã biên dịch', 'Một chương trình đang thực thi', 'Một file dữ liệu', 'Một driver thiết bị', 'Một chương trình đang thực thi'),
(86, 'Round Robin', 'FCFS', 'SJF', 'Priority', 'SJF'),
(87, 'Tạo kiểu giao diện', 'Xử lý logic server', 'Cấu trúc nội dung trang', 'Lưu trữ dữ liệu', 'Cấu trúc nội dung trang'),
(88, 'GET', 'POST', 'PUT', 'DELETE', 'POST'),
(89, 'Dữ liệu đã gán nhãn', 'Dữ liệu không gán nhãn', 'Dữ liệu ngẫu nhiên', 'Dữ liệu thời gian thực', 'Dữ liệu đã gán nhãn'),
(90, 'BFS', 'DFS', 'A*', 'Greedy', 'DFS'),
(91, 'Một khóa dùng chung', 'Hai khóa công khai', 'Chỉ khóa công khai', 'Không cần khóa', 'Một khóa dùng chung'),
(92, 'Xác thực bằng mật khẩu', 'Xác thực bằng 2 yếu tố khác nhau', 'Xác thực bằng vân tay', 'Xác thực bằng OTP', 'Xác thực bằng 2 yếu tố khác nhau'),
(93, 'Giá trị tiền tệ trong tương lai', 'Giá trị tiền tệ hiện tại của dòng tiền tương lai', 'Tổng doanh thu', 'Tổng chi phí', 'Giá trị tiền tệ hiện tại của dòng tiền tương lai'),
(94, 'Lợi nhuận trên tổng tài sản', 'Lợi nhuận trên vốn chủ sở hữu', 'Lợi nhuận trên doanh thu', 'Tỷ lệ nợ', 'Lợi nhuận trên vốn chủ sở hữu'),
(95, 'Doanh nghiệp bán cho doanh nghiệp', 'Doanh nghiệp bán cho người tiêu dùng', 'Người tiêu dùng bán cho người tiêu dùng', 'Chính phủ bán cho doanh nghiệp', 'Doanh nghiệp bán cho người tiêu dùng'),
(96, 'Lưu sản phẩm trước khi thanh toán', 'So sánh giá', 'Tạo tài khoản', 'Tính thuế', 'Lưu sản phẩm trước khi thanh toán'),
(97, 'Nền kinh tế', 'Kinh doanh', 'Thương mại', 'Ngân hàng', 'Nền kinh tế'),
(98, 'Dịch ý', 'Dịch sát nghĩa', 'Dịch tự do', 'Dịch sáng tạo', 'Dịch sát nghĩa'),
(99, 'Sốc văn hóa', 'Tôn trọng văn hóa', 'Đa văn hóa', 'Giao lưu văn hóa', 'Sốc văn hóa'),
(100, 'Cử chỉ, nét mặt, ánh mắt', 'Chỉ ngôn ngữ nói', 'Chỉ văn bản', 'Chỉ âm thanh', 'Cử chỉ, nét mặt, ánh mắt'),
(101, 'Điện áp thấp', 'Điện áp trung bình', 'Điện áp cao', 'Điện áp một chiều', 'Điện áp cao'),
(102, 'Tăng công suất tải', 'Biến đổi điện áp', 'Đo tần số', 'Ổn định nhiệt', 'Biến đổi điện áp'),
(103, 'P', 'Q', 'S', 'U', 'P'),
(104, 'S = U * I', 'S = P / Q', 'S = U / I', 'S = I / U', 'S = U * I'),
(105, 'Hạ tầng ảo: máy chủ, lưu trữ, mạng', 'Nền tảng phát triển ứng dụng', 'Phần mềm hoàn chỉnh', 'Dịch vụ email', 'Hạ tầng ảo: máy chủ, lưu trữ, mạng'),
(106, 'Mua thiết bị phần cứng cố định', 'Co giãn tài nguyên theo nhu cầu', 'Giảm bảo mật', 'Chỉ dùng nội bộ', 'Co giãn tài nguyên theo nhu cầu'),
(107, 'Thu thập dữ liệu môi trường', 'Lưu trữ dữ liệu', 'Phân tích dữ liệu', 'Hiển thị giao diện', 'Thu thập dữ liệu môi trường'),
(108, 'MQTT', 'FTP', 'SNMP', 'IMAP', 'MQTT');

-- ========== 15. INSERT CauHoi - Câu hỏi điền khuyết ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(31, 1, 2, 'Trong Java, từ khóa _____ được dùng để kế thừa một lớp.', 'De', 'DK'),
(32, 1, 2, 'Phương thức _____ được gọi tự động khi tạo đối tượng mới.', 'De', 'DK'),
(33, 1, 3, 'Từ khóa _____ dùng để khai báo một hằng số trong Java.', 'TrungBinh', 'DK'),
(34, 2, 4, 'Lệnh _____ dùng để xóa dữ liệu trong bảng SQL.', 'De', 'DK'),
(35, 2, 4, 'Mệnh đề _____ dùng để sắp xếp kết quả truy vấn SQL.', 'De', 'DK');

-- ========== 17. INSERT CauHoi - Bổ sung câu hỏi điền khuyết cho các môn còn thiếu ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(109, 3, 2, 'Cấu trúc dữ liệu _____ hoạt động theo FIFO.', 'De', 'DK'),
(110, 4, 3, 'Giao thức _____ dùng để phân giải tên miền.', 'De', 'DK'),
(111, 5, 4, 'Bộ phận _____ quản lý bộ nhớ trong hệ điều hành.', 'TrungBinh', 'DK'),
(112, 6, 5, 'Ngôn ngữ _____ dùng để tạo kiểu giao diện web.', 'De', 'DK'),
(113, 7, 6, 'Thuật toán _____ là mô hình học máy tuyến tính cho phân loại.', 'TrungBinh', 'DK'),
(114, 8, 7, 'Tấn công _____ là hình thức giả mạo trang web.', 'De', 'DK'),
(115, 11, 9, 'Chỉ số _____ đo khả năng thanh toán ngắn hạn.', 'TrungBinh', 'DK'),
(116, 12, 10, 'Cổng _____ hỗ trợ thanh toán trực tuyến.', 'De', 'DK'),
(117, 15, 12, 'Dịch _____ là dịch theo ngữ cảnh.', 'TrungBinh', 'DK'),
(118, 16, 13, 'Khái niệm _____ mô tả chuẩn mực ứng xử trong văn hóa.', 'TrungBinh', 'DK'),
(119, 19, 14, 'Thiết bị _____ dùng để bảo vệ quá tải.', 'De', 'DK'),
(120, 20, 15, 'Công suất phản kháng ký hiệu là _____.', 'De', 'DK'),
(121, 21, 8, 'Mô hình _____ cho phép chạy ứng dụng mà không quản lý máy chủ.', 'TrungBinh', 'DK'),
(122, 22, 8, 'Thiết bị _____ kết nối cảm biến với mạng.', 'De', 'DK');

-- ========== 13. INSERT CauHoiDK - Đáp án điền khuyết ==========
INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(31, 'extends|implements|inherits|derives', 'extends'),
(32, 'constructor|destructor|initializer|main', 'constructor'),
(33, 'final|const|static|constant', 'final'),
(34, 'DELETE|REMOVE|DROP|TRUNCATE', 'DELETE'),
(35, 'ORDER BY|SORT BY|ARRANGE BY|GROUP BY', 'ORDER BY');

-- ========== 18. INSERT CauHoiDK - Đáp án điền khuyết cho các môn bổ sung ==========
INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(109, 'Queue|Stack|Tree|Graph', 'Queue'),
(110, 'DNS|HTTP|FTP|SMTP', 'DNS'),
(111, 'Memory Manager|Scheduler|Shell|Driver', 'Memory Manager'),
(112, 'CSS|HTML|SQL|XML', 'CSS'),
(113, 'Logistic Regression|KNN|Naive Bayes|SVM', 'Logistic Regression'),
(114, 'phishing|spoofing|sniffing|spam', 'phishing'),
(115, 'Current Ratio|ROE|ROI|EPS', 'Current Ratio'),
(116, 'Payment Gateway|Checkout|Invoice|Receipt', 'Payment Gateway'),
(117, 'tự do|máy|từng chữ|âm vị', 'tự do'),
(118, 'nghi thức|xung đột|đa văn hóa|định kiến', 'nghi thức'),
(119, 'CB|Biến áp|Tụ điện|Rơle thời gian', 'CB'),
(120, 'Q|P|S|U', 'Q'),
(121, 'Serverless|IaaS|PaaS|On-premises', 'Serverless'),
(122, 'Gateway|Router|Switch|Repeater', 'Gateway');

-- ========== 14. INSERT DeThi ==========
INSERT INTO DeThi (ma_de_thi, ma_hoc_phan, ma_ky_thi, ma_gv, ten_de_thi, thoi_gian_lam, ngay_tao, so_cau_hoi) VALUES 
(1, 1, 1, 2, 'Đề thi Giữa kỳ - Lập trình Java - Đề 01', 45, NOW(), 10),
(2, 1, 1, 3, 'Đề thi Giữa kỳ - Lập trình Java - Đề 02', 45, NOW(), 10),
(3, 2, 1, 6, 'Đề thi Giữa kỳ - Cơ sở dữ liệu - Đề 01', 60, NOW(), 10),
(4, 2, 2, 6, 'Đề thi Cuối kỳ - Cơ sở dữ liệu - Đề 01', 90, NOW(), 15),
(5, 1, 2, 2, 'Đề thi Cuối kỳ - Lập trình Java - Đề 01', 90, NOW(), 15),
-- Đề thi Khoa Kinh tế
(6, 9, 1, 9, 'Đề thi Giữa kỳ - Nguyên lý Kinh tế - Đề 01', 60, NOW(), 10),
(7, 9, 2, 9, 'Đề thi Cuối kỳ - Nguyên lý Kinh tế - Đề 01', 90, NOW(), 15),
(8, 10, 1, 10, 'Đề thi Giữa kỳ - Quản lý Dự án - Đề 01', 60, NOW(), 10),
(9, 10, 2, 10, 'Đề thi Cuối kỳ - Quản lý Dự án - Đề 01', 90, NOW(), 15),
-- Đề thi Khoa Ngoại ngữ
(10, 13, 1, 12, 'Đề thi Giữa kỳ - Kỹ năng Giao tiếp Tiếng Anh - Đề 01', 60, NOW(), 10),
(11, 13, 2, 12, 'Đề thi Cuối kỳ - Kỹ năng Giao tiếp Tiếng Anh - Đề 01', 90, NOW(), 15),
(12, 14, 1, 13, 'Đề thi Giữa kỳ - Ngữ pháp Tiếng Anh Nâng cao - Đề 01', 60, NOW(), 10),
(13, 14, 2, 13, 'Đề thi Cuối kỳ - Ngữ pháp Tiếng Anh Nâng cao - Đề 01', 90, NOW(), 15),
-- Đề thi Khoa Điện
(14, 17, 1, 14, 'Đề thi Giữa kỳ - Lý thuyết Điện - Đề 01', 60, NOW(), 10),
(15, 17, 2, 14, 'Đề thi Cuối kỳ - Lý thuyết Điện - Đề 01', 90, NOW(), 15),
(16, 18, 1, 15, 'Đề thi Giữa kỳ - Điện tử Kỹ thuật - Đề 01', 60, NOW(), 10),
(17, 18, 2, 15, 'Đề thi Cuối kỳ - Điện tử Kỹ thuật - Đề 01', 90, NOW(), 15),
-- Đề thi Phụ (Kỳ thi Phụ HK1 - ma_ky_thi = 3)
(18, 1, 3, 2, 'Đề thi Phụ HK1 - Lập trình Java - Đề 01', 60, NOW(), 10),
(19, 2, 3, 6, 'Đề thi Phụ HK1 - Cơ sở dữ liệu - Đề 01', 60, NOW(), 10),
(20, 9, 3, 9, 'Đề thi Phụ HK1 - Nguyên lý Kinh tế - Đề 01', 60, NOW(), 10),
(21, 10, 3, 10, 'Đề thi Phụ HK1 - Quản lý Dự án - Đề 01', 60, NOW(), 10),
(22, 13, 3, 12, 'Đề thi Phụ HK1 - Kỹ năng Giao tiếp Tiếng Anh - Đề 01', 60, NOW(), 10),
(23, 14, 3, 13, 'Đề thi Phụ HK1 - Ngữ pháp Tiếng Anh Nâng cao - Đề 01', 60, NOW(), 10),
(24, 17, 3, 14, 'Đề thi Phụ HK1 - Lý thuyết Điện - Đề 01', 60, NOW(), 10),
(25, 18, 3, 15, 'Đề thi Phụ HK1 - Điện tử Kỹ thuật - Đề 01', 60, NOW(), 10);

-- ========== 15. INSERT ChiTietDeThi ==========
-- Đề thi Java 1: 10 câu (1-10)
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(1, 6), (1, 7), (1, 8), (1, 11), (1, 12);

-- Đề thi Java 2: 10 câu 
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(2, 1), (2, 3), (2, 5), (2, 6), (2, 7),
(2, 9), (2, 10), (2, 13), (2, 14), (2, 15);

-- Đề thi CSDL 1: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(3, 16), (3, 17), (3, 18), (3, 19), (3, 20),
(3, 21), (3, 22), (3, 23), (3, 34), (3, 35);

-- Đề thi CSDL Cuối kỳ: 15 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(4, 16), (4, 17), (4, 18), (4, 19), (4, 20),
(4, 21), (4, 22), (4, 23), (4, 24), (4, 25),
(4, 26), (4, 27), (4, 28), (4, 29), (4, 30);

-- Đề thi Java Cuối kỳ: 15 câu (bao gồm điền khuyết)
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5),
(5, 6), (5, 7), (5, 8), (5, 9), (5, 10),
(5, 11), (5, 12), (5, 31), (5, 32), (5, 33);

-- Đề thi Kinh tế 1: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(6, 36), (6, 37), (6, 38), (6, 39), (6, 40),
(6, 41), (6, 42), (6, 43), (6, 44), (6, 45);

-- Đề thi Kinh tế Cuối kỳ: 15 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(7, 36), (7, 37), (7, 38), (7, 39), (7, 40),
(7, 41), (7, 42), (7, 43), (7, 44), (7, 45),
(7, 46), (7, 47), (7, 48), (7, 49), (7, 50);

-- Đề thi Quản lý Dự án: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(8, 36), (8, 37), (8, 38), (8, 39), (8, 40),
(8, 41), (8, 42), (8, 43), (8, 44), (8, 45);

-- Đề thi Quản lý Dự án Cuối kỳ: 15 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(9, 36), (9, 37), (9, 38), (9, 39), (9, 40),
(9, 41), (9, 42), (9, 43), (9, 44), (9, 45),
(9, 46), (9, 47), (9, 48), (9, 49), (9, 50);

-- Đề thi Tiếng Anh 1: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(10, 51), (10, 52), (10, 53), (10, 54), (10, 55),
(10, 56), (10, 57), (10, 58), (10, 59), (10, 60);

-- Đề thi Tiếng Anh Cuối kỳ: 15 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(11, 51), (11, 52), (11, 53), (11, 54), (11, 55),
(11, 56), (11, 57), (11, 58), (11, 59), (11, 60),
(11, 61), (11, 62), (11, 63), (11, 64), (11, 65);

-- Đề thi Ngữ pháp Tiếng Anh: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(12, 51), (12, 52), (12, 53), (12, 54), (12, 55),
(12, 56), (12, 57), (12, 58), (12, 59), (12, 60);

-- Đề thi Ngữ pháp Tiếng Anh Cuối kỳ: 15 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(13, 51), (13, 52), (13, 53), (13, 54), (13, 55),
(13, 56), (13, 57), (13, 58), (13, 59), (13, 60),
(13, 61), (13, 62), (13, 63), (13, 64), (13, 65);

-- Đề thi Điện 1: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(14, 66), (14, 67), (14, 68), (14, 69), (14, 70),
(14, 71), (14, 72), (14, 73), (14, 74), (14, 75);

-- Đề thi Điện Cuối kỳ: 15 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(15, 66), (15, 67), (15, 68), (15, 69), (15, 70),
(15, 71), (15, 72), (15, 73), (15, 74), (15, 75),
(15, 76), (15, 77), (15, 78), (15, 79), (15, 80);

-- Đề thi Điện tử Kỹ thuật: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(16, 66), (16, 67), (16, 68), (16, 69), (16, 70),
(16, 71), (16, 72), (16, 73), (16, 74), (16, 75);

-- Đề thi Điện tử Kỹ thuật Cuối kỳ: 15 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(17, 66), (17, 67), (17, 68), (17, 69), (17, 70),
(17, 71), (17, 72), (17, 73), (17, 74), (17, 75),
(17, 76), (17, 77), (17, 78), (17, 79), (17, 80);

-- Đề thi Phụ Java: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(18, 1), (18, 2), (18, 3), (18, 4), (18, 5),
(18, 6), (18, 7), (18, 8), (18, 9), (18, 10);

-- Đề thi Phụ CSDL: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(19, 16), (19, 17), (19, 18), (19, 19), (19, 20),
(19, 21), (19, 22), (19, 23), (19, 24), (19, 25);

-- Đề thi Phụ Kinh tế: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(20, 36), (20, 37), (20, 38), (20, 39), (20, 40),
(20, 41), (20, 42), (20, 43), (20, 44), (20, 45);

-- Đề thi Phụ Quản lý Dự án: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(21, 36), (21, 37), (21, 38), (21, 39), (21, 40),
(21, 41), (21, 42), (21, 43), (21, 44), (21, 45);

-- Đề thi Phụ Tiếng Anh: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(22, 51), (22, 52), (22, 53), (22, 54), (22, 55),
(22, 56), (22, 57), (22, 58), (22, 59), (22, 60);

-- Đề thi Phụ Ngữ pháp Tiếng Anh: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(23, 51), (23, 52), (23, 53), (23, 54), (23, 55),
(23, 56), (23, 57), (23, 58), (23, 59), (23, 60);

-- Đề thi Phụ Điện: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(24, 66), (24, 67), (24, 68), (24, 69), (24, 70),
(24, 71), (24, 72), (24, 73), (24, 74), (24, 75);

-- Đề thi Phụ Điện tử Kỹ thuật: 10 câu
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(25, 66), (25, 67), (25, 68), (25, 69), (25, 70),
(25, 71), (25, 72), (25, 73), (25, 74), (25, 75);

-- ========== 16. INSERT BaiThi mẫu ==========
-- MỖI SINH VIÊN PHẢI CÓ ÍT NHẤT 1 BÀI THI
INSERT INTO BaiThi (ma_bai_thi, ma_de_thi, ma_sv, thoi_gian_bat_dau, thoi_gian_nop, ngay_thi, so_cau_dung, so_cau_sai, diem_so) VALUES 
-- Sinh viên 1: 2 bài thi
(1, 1, 1, '2024-10-05 08:00:00', '2024-10-05 08:40:00', '2024-10-05', 8, 2, 8.0),
(2, 3, 1, '2024-10-06 09:00:00', '2024-10-06 09:45:00', '2024-10-06', 9, 1, 9.0),
-- Sinh viên 2: 2 bài thi
(3, 1, 2, '2024-10-05 08:00:00', '2024-10-05 08:35:00', '2024-10-05', 7, 3, 7.0),
(4, 3, 2, '2024-10-06 09:00:00', '2024-10-06 09:50:00', '2024-10-06', 7, 3, 7.0),
-- Sinh viên 3: 2 bài thi
(5, 1, 3, '2024-10-05 08:00:00', '2024-10-05 08:42:00', '2024-10-05', 9, 1, 9.0),
(6, 2, 3, '2024-10-05 14:00:00', '2024-10-05 14:40:00', '2024-10-05', 8, 2, 8.0),
-- Sinh viên 4: 1 bài thi
(7, 3, 4, '2024-10-06 09:00:00', '2024-10-06 09:55:00', '2024-10-06', 8, 2, 8.0),
-- Sinh viên 5: 1 bài thi
(8, 3, 5, '2024-10-06 09:00:00', '2024-10-06 09:50:00', '2024-10-06', 6, 4, 6.0),
-- Sinh viên 6: 1 bài thi
(9, 1, 6, '2024-10-05 08:00:00', '2024-10-05 08:38:00', '2024-10-05', 7, 3, 7.0),
-- Sinh viên 7: 1 bài thi
(10, 2, 7, '2024-10-05 14:00:00', '2024-10-05 14:35:00', '2024-10-05', 9, 1, 9.0),
-- Sinh viên 8: 1 bài thi
(11, 4, 8, '2024-12-20 08:00:00', '2024-12-20 09:25:00', '2024-12-20', 12, 3, 8.0),
-- Sinh viên 9: 1 bài thi
(12, 5, 9, '2024-12-20 08:00:00', '2024-12-20 09:20:00', '2024-12-20', 13, 2, 8.67),
-- Sinh viên 10: 1 bài thi
(13, 4, 10, '2024-12-20 08:00:00', '2024-12-20 09:30:00', '2024-12-20', 11, 4, 7.33),
-- Sinh viên 11 (Quản trị Kinh doanh): 2 bài thi
(14, 6, 11, '2024-10-08 08:00:00', '2024-10-08 09:00:00', '2024-10-08', 8, 2, 8.0),
(15, 7, 11, '2024-12-22 08:00:00', '2024-12-22 09:30:00', '2024-12-22', 12, 3, 8.0),
-- Sinh viên 12 (Quản trị Kinh doanh): 1 bài thi
(16, 6, 12, '2024-10-08 08:00:00', '2024-10-08 09:00:00', '2024-10-08', 7, 3, 7.0),
-- Sinh viên 13 (Quản trị Kinh doanh): 1 bài thi
(17, 7, 13, '2024-12-22 08:00:00', '2024-12-22 09:30:00', '2024-12-22', 11, 4, 7.33),
-- Sinh viên 14 (Quản trị Kinh doanh): 1 bài thi
(18, 8, 14, '2024-10-09 09:00:00', '2024-10-09 10:00:00', '2024-10-09', 9, 1, 9.0),
-- Sinh viên 15 (Kế toán): 1 bài thi
(19, 8, 15, '2024-10-09 09:00:00', '2024-10-09 10:00:00', '2024-10-09', 8, 2, 8.0),
-- Sinh viên 16 (Kế toán): 1 bài thi
(20, 9, 16, '2024-12-23 09:00:00', '2024-12-23 10:30:00', '2024-12-23', 13, 2, 8.67),
-- Sinh viên 17 (Tiếng Anh): 2 bài thi
(21, 10, 17, '2024-10-10 10:00:00', '2024-10-10 11:00:00', '2024-10-10', 8, 2, 8.0),
(22, 11, 17, '2024-12-24 10:00:00', '2024-12-24 11:30:00', '2024-12-24', 12, 3, 8.0),
-- Sinh viên 18 (Tiếng Anh): 1 bài thi
(23, 10, 18, '2024-10-10 10:00:00', '2024-10-10 11:00:00', '2024-10-10', 7, 3, 7.0),
-- Sinh viên 19 (Tiếng Anh): 1 bài thi
(24, 12, 19, '2024-10-11 10:00:00', '2024-10-11 11:00:00', '2024-10-11', 9, 1, 9.0),
-- Sinh viên 20 (Tiếng Anh): 1 bài thi
(25, 11, 20, '2024-12-24 10:00:00', '2024-12-24 11:30:00', '2024-12-24', 10, 5, 6.67),
-- Sinh viên 21 (Tiếng Nhật): 1 bài thi
(26, 13, 21, '2024-10-11 10:00:00', '2024-10-11 11:00:00', '2024-10-11', 8, 2, 8.0),
-- Sinh viên 22 (Tiếng Nhật): 1 bài thi
(27, 14, 22, '2024-12-25 10:00:00', '2024-12-25 11:30:00', '2024-12-25', 11, 4, 7.33),
-- Sinh viên 23 (Kỹ thuật Điện): 2 bài thi
(28, 14, 23, '2024-10-12 14:00:00', '2024-10-12 15:00:00', '2024-10-12', 8, 2, 8.0),
(29, 15, 23, '2024-12-26 14:00:00', '2024-12-26 15:30:00', '2024-12-26', 12, 3, 8.0),
-- Sinh viên 24 (Kỹ thuật Điện): 1 bài thi
(30, 14, 24, '2024-10-12 14:00:00', '2024-10-12 15:00:00', '2024-10-12', 7, 3, 7.0),
-- Sinh viên 25 (Kỹ thuật Điện): 1 bài thi
(31, 15, 25, '2024-12-26 14:00:00', '2024-12-26 15:30:00', '2024-12-26', 13, 2, 8.67),
-- Sinh viên 26 (Kỹ thuật Điện): 1 bài thi
(32, 16, 26, '2024-10-13 14:00:00', '2024-10-13 15:00:00', '2024-10-13', 9, 1, 9.0),
-- Sinh viên 27 (Điện tử Viễn thông): 1 bài thi
(33, 16, 27, '2024-10-13 14:00:00', '2024-10-13 15:00:00', '2024-10-13', 8, 2, 8.0),
-- Sinh viên 28 (Điện tử Viễn thông): 1 bài thi
(34, 17, 28, '2024-12-27 14:00:00', '2024-12-27 15:30:00', '2024-12-27', 11, 4, 7.33);

-- ========== 17. INSERT ChiTietBaiThi mẫu ==========
-- Chi tiết bài thi của sinh viên 1 - Bài thi 1 (ma_bai_thi = 1, Đề Java 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(1, 1, 'Tất cả các đáp án trên'),
(1, 2, 'class'),
(1, 3, 'public static void main(String[] args)'),
(1, 4, 'int'),
(1, 5, 'Java Virtual Machine'),
(1, 6, 'do-while'),
(1, 7, 'Một phương thức có thể có nhiều hành vi khác nhau'),
(1, 8, 'Phương thức static và default (từ Java 8)'),
(1, 11, 'Interface có thể có biến instance'),  -- Sai
(1, 12, 'Factory Pattern');                    -- Sai

-- Chi tiết bài thi của sinh viên 1 - Bài thi 2 (ma_bai_thi = 2, Đề CSDL 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(2, 16, 'Structured Query Language'),
(2, 17, 'SELECT'),
(2, 18, 'Duy nhất và không NULL'),
(2, 19, 'INSERT INTO'),
(2, 20, 'Chuỗi ký tự'),
(2, 21, 'Liên kết giữa các bảng'),
(2, 22, 'FULL OUTER JOIN'),
(2, 23, 'Tăng tốc độ truy vấn'),
(2, 34, 'DELETE'),
(2, 35, 'ORDER BY');

-- Chi tiết bài thi của sinh viên 2 - Bài thi 3 (ma_bai_thi = 3, Đề Java 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(3, 1, 'Tất cả các đáp án trên'),
(3, 2, 'class'),
(3, 3, 'public static void main(String[] args)'),
(3, 4, 'float'),  -- Sai
(3, 5, 'Java Virtual Machine'),
(3, 6, 'for'),    -- Sai
(3, 7, 'Một phương thức có thể có nhiều hành vi khác nhau'),
(3, 8, 'Phương thức static và default (từ Java 8)'),
(3, 11, 'Abstract class có thể có constructor, Interface thì không'),
(3, 12, 'Builder Pattern');  -- Sai

-- Chi tiết bài thi của sinh viên 2 - Bài thi 4 (ma_bai_thi = 4, Đề CSDL 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(4, 16, 'Structured Query Language'),
(4, 17, 'SELECT'),
(4, 18, 'Duy nhất và không NULL'),
(4, 19, 'ADD INTO'),  -- Sai
(4, 20, 'Chuỗi ký tự'),
(4, 21, 'Xóa dữ liệu'),  -- Sai
(4, 22, 'INNER JOIN'),   -- Sai
(4, 23, 'Tăng tốc độ truy vấn'),
(4, 34, 'DELETE'),
(4, 35, 'ORDER BY');

-- Chi tiết bài thi của sinh viên 3 - Bài thi 5 (ma_bai_thi = 5, Đề Java 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(5, 1, 'Tất cả các đáp án trên'),
(5, 2, 'class'),
(5, 3, 'public static void main(String[] args)'),
(5, 4, 'int'),
(5, 5, 'Java Virtual Machine'),
(5, 6, 'do-while'),
(5, 7, 'Một phương thức có thể có nhiều hành vi khác nhau'),
(5, 8, 'Phương thức static và default (từ Java 8)'),
(5, 11, 'Abstract class có thể có constructor, Interface thì không'),
(5, 12, 'Factory Pattern');  -- Sai

-- Chi tiết bài thi của sinh viên 3 - Bài thi 6 (ma_bai_thi = 6, Đề Java 2)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(6, 1, 'Tất cả các đáp án trên'),
(6, 3, 'public static void main(String[] args)'),
(6, 5, 'Java Virtual Machine'),
(6, 6, 'do-while'),
(6, 7, 'Một phương thức có thể có nhiều hành vi khác nhau'),
(6, 9, '== so sánh tham chiếu, equals() so sánh nội dung'),
(6, 10, 'ArrayList dùng mảng, LinkedList dùng danh sách liên kết'),
(6, 13, 'Tự động giải phóng bộ nhớ không còn được tham chiếu'),
(6, 14, 'Code không cần đồng bộ hóa'),  -- Sai
(6, 15, 'Cho phép kiểm tra và thao tác class/method/field tại runtime');

-- Chi tiết bài thi của sinh viên 4 - Bài thi 7 (ma_bai_thi = 7, Đề CSDL 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(7, 16, 'Structured Query Language'),
(7, 17, 'SELECT'),
(7, 18, 'Duy nhất và không NULL'),
(7, 19, 'INSERT INTO'),
(7, 20, 'Chuỗi ký tự'),
(7, 21, 'Liên kết giữa các bảng'),
(7, 22, 'LEFT JOIN'),  -- Sai
(7, 23, 'Tăng tốc độ truy vấn'),
(7, 34, 'TRUNCATE'),  -- Sai
(7, 35, 'ORDER BY');

-- Chi tiết bài thi của sinh viên 5 - Bài thi 8 (ma_bai_thi = 8, Đề CSDL 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(8, 16, 'Structured Query Language'),
(8, 17, 'INSERT'),  -- Sai
(8, 18, 'Duy nhất và không NULL'),
(8, 19, 'INSERT INTO'),
(8, 20, 'Số nguyên'),  -- Sai
(8, 21, 'Liên kết giữa các bảng'),
(8, 22, 'INNER JOIN'),  -- Sai
(8, 23, 'Bảo mật dữ liệu'),  -- Sai
(8, 34, 'DELETE'),
(8, 35, 'ORDER BY');

-- Chi tiết bài thi của sinh viên 6 - Bài thi 9 (ma_bai_thi = 9, Đề Java 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(9, 1, 'Tất cả các đáp án trên'),
(9, 2, 'class'),
(9, 3, 'static void main()'),  -- Sai
(9, 4, 'int'),
(9, 5, 'Java Virtual Machine'),
(9, 6, 'do-while'),
(9, 7, 'Khả năng tạo nhiều đối tượng'),  -- Sai
(9, 8, 'Phương thức static và default (từ Java 8)'),
(9, 11, 'Abstract class có thể có constructor, Interface thì không'),
(9, 12, 'Observer Pattern');  -- Sai

-- Chi tiết bài thi của sinh viên 7 - Bài thi 10 (ma_bai_thi = 10, Đề Java 2)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(10, 1, 'Tất cả các đáp án trên'),
(10, 3, 'public static void main(String[] args)'),
(10, 5, 'Java Virtual Machine'),
(10, 6, 'do-while'),
(10, 7, 'Một phương thức có thể có nhiều hành vi khác nhau'),
(10, 9, '== so sánh tham chiếu, equals() so sánh nội dung'),
(10, 10, 'ArrayList dùng mảng, LinkedList dùng danh sách liên kết'),
(10, 13, 'Tự động giải phóng bộ nhớ không còn được tham chiếu'),
(10, 14, 'Code có thể chạy an toàn từ nhiều thread'),
(10, 15, 'Chỉ dùng cho debug');  -- Sai

-- Chi tiết bài thi của sinh viên 8 - Bài thi 11 (ma_bai_thi = 11, Đề CSDL Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(11, 16, 'Structured Query Language'),
(11, 17, 'SELECT'),
(11, 18, 'Duy nhất và không NULL'),
(11, 19, 'INSERT INTO'),
(11, 20, 'Chuỗi ký tự'),
(11, 21, 'Liên kết giữa các bảng'),
(11, 22, 'FULL OUTER JOIN'),
(11, 23, 'Tăng tốc độ truy vấn'),
(11, 24, 'WHERE lọc trước GROUP BY, HAVING lọc sau'),
(11, 25, 'Loại bỏ các bản ghi trùng lặp'),
(11, 26, 'LOW, MEDIUM, HIGH'),  -- Sai
(11, 27, 'Giảm dư thừa dữ liệu: 1NF, 2NF, 3NF, BCNF'),
(11, 28, 'Hai transaction chờ đợi lẫn nhau vô hạn'),
(11, 29, 'Procedure và Function giống nhau'),  -- Sai
(11, 30, 'Sharding phân tán qua nhiều server, Partitioning trong cùng server');

-- Chi tiết bài thi của sinh viên 9 - Bài thi 12 (ma_bai_thi = 12, Đề Java Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(12, 1, 'Tất cả các đáp án trên'),
(12, 2, 'class'),
(12, 3, 'public static void main(String[] args)'),
(12, 4, 'int'),
(12, 5, 'Java Virtual Machine'),
(12, 6, 'do-while'),
(12, 7, 'Một phương thức có thể có nhiều hành vi khác nhau'),
(12, 8, 'Phương thức static và default (từ Java 8)'),
(12, 9, '== so sánh tham chiếu, equals() so sánh nội dung'),
(12, 10, 'ArrayList dùng mảng, LinkedList dùng danh sách liên kết'),
(12, 11, 'Abstract class có thể có constructor, Interface thì không'),
(12, 12, 'Singleton Pattern'),
(12, 31, 'extends'),
(12, 32, 'destructor'),  -- Sai
(12, 33, 'final');

-- Chi tiết bài thi của sinh viên 10 - Bài thi 13 (ma_bai_thi = 13, Đề CSDL Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(13, 16, 'Structured Query Language'),
(13, 17, 'SELECT'),
(13, 18, 'Duy nhất và không NULL'),
(13, 19, 'INSERT INTO'),
(13, 20, 'Chuỗi ký tự'),
(13, 21, 'Liên kết giữa các bảng'),
(13, 22, 'INNER JOIN'),  -- Sai
(13, 23, 'Tăng tốc độ truy vấn'),
(13, 24, 'WHERE lọc trước GROUP BY, HAVING lọc sau'),
(13, 25, 'Sắp xếp dữ liệu'),  -- Sai
(13, 26, 'READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE'),
(13, 27, 'Giảm dư thừa dữ liệu: 1NF, 2NF, 3NF, BCNF'),
(13, 28, 'Lỗi kết nối database'),  -- Sai
(13, 29, 'Procedure không trả về giá trị qua RETURN, Function có'),
(13, 30, 'Giống nhau hoàn toàn');  -- Sai

-- Chi tiết bài thi sinh viên 11 - Bài thi 14 (Đề Kinh tế 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(14, 36, 'Sử dụng tài nguyên hiệu quả'),
(14, 37, 'Tăng khi cầu tăng'),
(14, 38, 'Gross Domestic Product'),
(14, 39, 'Tất cả các đáp án trên'),
(14, 40, 'Nhiều'),
(14, 41, 'Cung và cầu'),
(14, 42, 'Mức độ thay đổi lượng cầu khi giá thay đổi'),
(14, 43, 'Giá trị một lựa chọn bị mất vì chọn khác'),
(14, 44, 'Tỷ lệ người không có việc làm'),
(14, 45, 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có');

-- Chi tiết bài thi sinh viên 11 - Bài thi 15 (Đề Kinh tế Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(15, 36, 'Sử dụng tài nguyên hiệu quả'),
(15, 37, 'Tăng khi cầu tăng'),
(15, 38, 'Gross Domestic Product'),
(15, 39, 'Tất cả các đáp án trên'),
(15, 40, 'Nhiều'),
(15, 41, 'Cung và cầu'),
(15, 42, 'Mức độ thay đổi lượng cầu khi giá thay đổi'),
(15, 43, 'Chi phí sản xuất'),  -- Sai
(15, 44, 'Tỷ lệ lạm phát'),  -- Sai
(15, 45, 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có'),
(15, 46, 'Kiểm soát cung tiền và lãi suất'),
(15, 47, 'Mối quan hệ giữa đầu tư, tiết kiệm, lãi suất'),
(15, 48, 'Vượt quá khả năng sản xuất hiện tại'),
(15, 49, 'Cả A và B đúng'),
(15, 50, 'Bất đẳng thức thu nhập');

-- Chi tiết bài thi sinh viên 12 - Bài thi 16 (Đề Kinh tế 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(16, 36, 'Sử dụng tài nguyên hiệu quả'),
(16, 37, 'Giảm khi cung tăng'),  -- Sai
(16, 38, 'Gross Domestic Product'),
(16, 39, 'Mất sức mua'),
(16, 40, 'Một'),  -- Sai
(16, 41, 'Cung và cầu'),
(16, 42, 'Tổng lượng cầu'),  -- Sai
(16, 43, 'Giá trị một lựa chọn bị mất vì chọn khác'),
(16, 44, 'Tỷ lệ người không có việc làm'),
(16, 45, 'Giống nhau');  -- Sai

-- Chi tiết bài thi sinh viên 13 - Bài thi 17 (Đề Kinh tế Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(17, 36, 'Sử dụng tài nguyên hiệu quả'),
(17, 37, 'Tăng khi cầu tăng'),
(17, 38, 'Gross Domestic Product'),
(17, 39, 'Tất cả các đáp án trên'),
(17, 40, 'Nhiều'),
(17, 41, 'Cung và cầu'),
(17, 42, 'Mức độ thay đổi lượng cầu khi giá thay đổi'),
(17, 43, 'Giá trị một lựa chọn bị mất vì chọn khác'),
(17, 44, 'Tỷ lệ người không có việc làm'),
(17, 45, 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có'),
(17, 46, 'Kiểm soát cung tiền và lãi suất'),
(17, 47, 'Mối quan hệ giữa cung cầu'),  -- Sai
(17, 48, 'Vượt quá khả năng sản xuất hiện tại'),
(17, 49, 'Tỷ giá không ảnh hưởng'),  -- Sai
(17, 50, 'Bất đẳng thức thu nhập');

-- Chi tiết bài thi sinh viên 14 - Bài thi 18 (Đề Quản lý Dự án)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(18, 36, 'Sử dụng tài nguyên hiệu quả'),
(18, 37, 'Tăng khi cầu tăng'),
(18, 38, 'Gross Domestic Product'),
(18, 39, 'Tất cả các đáp án trên'),
(18, 40, 'Nhiều'),
(18, 41, 'Cung và cầu'),
(18, 42, 'Mức độ thay đổi lượng cầu khi giá thay đổi'),
(18, 43, 'Giá trị một lựa chọn bị mất vì chọn khác'),
(18, 44, 'Tỷ lệ người không có việc làm'),
(18, 45, 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có');

-- Chi tiết bài thi sinh viên 15 - Bài thi 19 (Đề Quản lý Dự án)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(19, 36, 'Sử dụng tài nguyên hiệu quả'),
(19, 37, 'Tăng khi cầu tăng'),
(19, 38, 'Gross Domestic Product'),
(19, 39, 'Tất cả các đáp án trên'),
(19, 40, 'Nhiều'),
(19, 41, 'Cung và cầu'),
(19, 42, 'Mức độ thay đổi lượng cầu khi giá thay đổi'),
(19, 43, 'Giá trị một lựa chọn bị mất vì chọn khác'),
(19, 44, 'Tỷ lệ lạm phát'),  -- Sai
(19, 45, 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có');

-- Chi tiết bài thi sinh viên 16 - Bài thi 20 (Đề Quản lý Dự án Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(20, 36, 'Sử dụng tài nguyên hiệu quả'),
(20, 37, 'Tăng khi cầu tăng'),
(20, 38, 'Gross Domestic Product'),
(20, 39, 'Tất cả các đáp án trên'),
(20, 40, 'Nhiều'),
(20, 41, 'Cung và cầu'),
(20, 42, 'Mức độ thay đổi lượng cầu khi giá thay đổi'),
(20, 43, 'Giá trị một lựa chọn bị mất vì chọn khác'),
(20, 44, 'Tỷ lệ người không có việc làm'),
(20, 45, 'Tiền lương danh nghĩa không điều chỉnh lạm phát, thực tế có'),
(20, 46, 'Kiểm soát cung tiền và lãi suất'),
(20, 47, 'Mối quan hệ giữa đầu tư, tiết kiệm, lãi suất'),
(20, 48, 'Vượt quá khả năng sản xuất hiện tại'),
(20, 49, 'Cả A và B đúng'),
(20, 50, 'Bất đẳng thức thu nhập');

-- Chi tiết bài thi sinh viên 17 - Bài thi 21 (Đề Tiếng Anh 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(21, 51, 'Hello, how are you?'),
(21, 52, 'pro-nun-see-ay-shun'),
(21, 53, 'Tất cả các đáp án trên'),
(21, 54, 'Present Simple'),
(21, 55, 'Câu hỏi nghi vấn với kinh nghiệm'),
(21, 56, 'Hành động hoàn thành trước hành động quá khứ khác'),
(21, 57, 'Tình huống khả thi trong tương lai'),
(21, 58, 'Đề ra, gợi ý'),
(21, 59, 'Hai hoặc nhiều từ thường đi cùng nhau'),
(21, 60, 'Chỉ ra sự tương phản');

-- Chi tiết bài thi sinh viên 17 - Bài thi 22 (Đề Tiếng Anh Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(22, 51, 'Hello, how are you?'),
(22, 52, 'pro-nun-see-ay-shun'),
(22, 53, 'Tất cả các đáp án trên'),
(22, 54, 'Present Simple'),
(22, 55, 'Câu hỏi nghi vấn với kinh nghiệm'),
(22, 56, 'Hành động hoàn thành trước hành động quá khứ khác'),
(22, 57, 'Tình huống khả thi trong tương lai'),
(22, 58, 'Từ bỏ'),  -- Sai
(22, 59, 'Hai hoặc nhiều từ thường đi cùng nhau'),
(22, 60, 'Chỉ ra sự tương phản'),
(22, 61, 'Chuyển từ trực tiếp sang lời nói gián tiếp'),
(22, 62, 'Diễn tả điều kiện quá khứ với kết quả hiện tại'),
(22, 63, 'Between a rock and a hard place'),
(22, 64, 'Thể hiện mức độ trang trọng hoặc bình thường'),
(22, 65, 'Thể hiện cảm xúc, ý định, thái độ của người nói');

-- Chi tiết bài thi sinh viên 18 - Bài thi 23 (Đề Tiếng Anh 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(23, 51, 'Hello, how are you?'),
(23, 52, 'pro-nun-see-ay-shun'),
(23, 53, 'Diễn tả hành động lặp lại'),  -- Sai
(23, 54, 'Present Simple'),
(23, 55, 'Câu hỏi nghi vấn với kinh nghiệm'),
(23, 56, 'Hành động hoàn thành trước hành động quá khứ khác'),
(23, 57, 'Tình huống không thể xảy ra'),  -- Sai
(23, 58, 'Đề ra, gợi ý'),
(23, 59, 'Hai hoặc nhiều từ thường đi cùng nhau'),
(23, 60, 'Chỉ ra sự tương phản');

-- Chi tiết bài thi sinh viên 19 - Bài thi 24 (Đề Ngữ pháp Tiếng Anh)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(24, 51, 'Hello, how are you?'),
(24, 52, 'pro-nun-see-ay-shun'),
(24, 53, 'Tất cả các đáp án trên'),
(24, 54, 'Present Simple'),
(24, 55, 'Câu hỏi nghi vấn với kinh nghiệm'),
(24, 56, 'Hành động hoàn thành trước hành động quá khứ khác'),
(24, 57, 'Tình huống khả thi trong tương lai'),
(24, 58, 'Đề ra, gợi ý'),
(24, 59, 'Hai hoặc nhiều từ thường đi cùng nhau'),
(24, 60, 'Chỉ ra sự tương phản');

-- Chi tiết bài thi sinh viên 20 - Bài thi 25 (Đề Tiếng Anh Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(25, 51, 'Hello, how are you?'),
(25, 52, 'pro-nun-see-ay-shun'),
(25, 53, 'Tất cả các đáp án trên'),
(25, 54, 'Past Tense'),  -- Sai
(25, 55, 'Câu hỏi nghi vấn với kinh nghiệm'),
(25, 56, 'Hành động hoàn thành trước hành động quá khứ khác'),
(25, 57, 'Tình huống không thể xảy ra'),  -- Sai
(25, 58, 'Đề ra, gợi ý'),
(25, 59, 'Từ đồng nghĩa'),  -- Sai
(25, 60, 'Chỉ ra sự tương phản'),
(25, 61, 'Chuyển từ trực tiếp sang lời nói gián tiếp'),
(25, 62, 'Diễn tả tương lai'),  -- Sai
(25, 63, 'Between a rock and a hard place'),
(25, 64, 'Thể hiện cảm xúc'),  -- Sai
(25, 65, 'Thể hiện cảm xúc, ý định, thái độ của người nói');

-- Chi tiết bài thi sinh viên 21 - Bài thi 26 (Đề Ngữ pháp Tiếng Anh)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(26, 51, 'Hello, how are you?'),
(26, 52, 'pro-nun-see-ay-shun'),
(26, 53, 'Tất cả các đáp án trên'),
(26, 54, 'Present Simple'),
(26, 55, 'Câu hỏi nghi vấn với kinh nghiệm'),
(26, 56, 'Hành động hoàn thành trước hành động quá khứ khác'),
(26, 57, 'Tình huống khả thi trong tương lai'),
(26, 58, 'Đề ra, gợi ý'),
(26, 59, 'Hai hoặc nhiều từ thường đi cùng nhau'),
(26, 60, 'Chỉ ra sự tương phản');

-- Chi tiết bài thi sinh viên 22 - Bài thi 27 (Đề Ngữ pháp Tiếng Anh Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(27, 51, 'Hello, how are you?'),
(27, 52, 'pro-nun-see-ay-shun'),
(27, 53, 'Tất cả các đáp án trên'),
(27, 54, 'Present Simple'),
(27, 55, 'Câu hỏi nghi vấn với kinh nghiệm'),
(27, 56, 'Hành động hoàn thành trước hành động quá khứ khác'),
(27, 57, 'Tình huống khả thi trong tương lai'),
(27, 58, 'Đề ra, gợi ý'),
(27, 59, 'Hai hoặc nhiều từ thường đi cùng nhau'),
(27, 60, 'Chỉ ra sự tương phản'),
(27, 61, 'Chuyển từ trực tiếp sang lời nói gián tiếp'),
(27, 62, 'Không được dùng'),  -- Sai
(27, 63, 'Break a leg'),  -- Sai
(27, 64, 'Thể hiện cảm xúc'),  -- Sai
(27, 65, 'Thể hiện cảm xúc, ý định, thái độ của người nói');

-- Chi tiết bài thi sinh viên 23 - Bài thi 28 (Đề Điện 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(28, 66, 'V = R * I'),
(28, 67, 'I = V / R'),
(28, 68, 'R'),
(28, 69, 'Tổng dòng điện vào bằng tổng dòng ra'),
(28, 70, 'Alternating Current'),
(28, 71, 'Khả năng tạo từ trường'),
(28, 72, 'Cả A và C'),
(28, 73, 'Hiệu suất hệ thống'),
(28, 74, 'Biến đổi điện áp AC'),
(28, 75, 'Khi tần số bằng tần số cộng hưởng');

-- Chi tiết bài thi sinh viên 23 - Bài thi 29 (Đề Điện Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(29, 66, 'V = R * I'),
(29, 67, 'I = V / R'),
(29, 68, 'R'),
(29, 69, 'Tổng dòng điện vào bằng tổng dòng ra'),
(29, 70, 'Alternating Current'),
(29, 71, 'Khả năng tạo từ trường'),
(29, 72, 'Cả A và C'),
(29, 73, 'Hiệu suất hệ thống'),
(29, 74, 'Biến đổi điện áp AC'),
(29, 75, 'Khi tần số bằng tần số cộng hưởng'),
(29, 76, 'Bốn phương trình cơ bản'),
(29, 77, 'Phương pháp nút hoặc vòng'),
(29, 78, 'Mất điện năng trong điện môi'),
(29, 79, 'Từ thông trong vật liệu'),
(29, 80, 'F = I * l * B * sin(θ)');

-- Chi tiết bài thi sinh viên 24 - Bài thi 30 (Đề Điện 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(30, 66, 'V = R * I'),
(30, 67, 'P = V * I'),  -- Sai
(30, 68, 'R'),
(30, 69, 'Tổng dòng điện vào bằng tổng dòng ra'),
(30, 70, 'Alternating Current'),
(30, 71, 'Khả năng tạo từ trường'),
(30, 72, 'Cả A và C'),
(30, 73, 'Hiệu suất hệ thống'),
(30, 74, 'Biến đổi điện áp AC'),
(30, 75, 'Khi tần số bằng tần số cộng hưởng');

-- Chi tiết bài thi sinh viên 25 - Bài thi 31 (Đề Điện Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(31, 66, 'V = R * I'),
(31, 67, 'I = V / R'),
(31, 68, 'R'),
(31, 69, 'Tổng dòng điện vào bằng tổng dòng ra'),
(31, 70, 'Alternating Current'),
(31, 71, 'Khả năng tạo từ trường'),
(31, 72, 'Cả A và C'),
(31, 73, 'Hiệu suất hệ thống'),
(31, 74, 'Biến đổi điện áp AC'),
(31, 75, 'Khi tần số bằng tần số cộng hưởng'),
(31, 76, 'Bốn phương trình cơ bản'),
(31, 77, 'Phương pháp nút hoặc vòng'),
(31, 78, 'Mất điện năng trong điện môi'),
(31, 79, 'Từ thông trong vật liệu'),
(31, 80, 'F = I * l * B * sin(θ)');

-- Chi tiết bài thi sinh viên 26 - Bài thi 32 (Đề Điện tử 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(32, 66, 'V = R * I'),
(32, 67, 'I = V / R'),
(32, 68, 'R'),
(32, 69, 'Tổng dòng điện vào bằng tổng dòng ra'),
(32, 70, 'Alternating Current'),
(32, 71, 'Khả năng tạo từ trường'),
(32, 72, 'Cả A và C'),
(32, 73, 'Hiệu suất hệ thống'),
(32, 74, 'Biến đổi điện áp AC'),
(32, 75, 'Khi tần số bằng tần số cộng hưởng');

-- Chi tiết bài thi sinh viên 27 - Bài thi 33 (Đề Điện tử 1)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(33, 66, 'V = R * I'),
(33, 67, 'I = V / R'),
(33, 68, 'Z'),  -- Sai
(33, 69, 'Tổng dòng điện vào bằng tổng dòng ra'),
(33, 70, 'Alternating Current'),
(33, 71, 'Khả năng tạo từ trường'),
(33, 72, 'P = V * I'),  -- Sai
(33, 73, 'Hiệu suất hệ thống'),
(33, 74, 'Chuyển DC thành AC'),  -- Sai
(33, 75, 'Khi tần số bằng tần số cộng hưởng');

-- Chi tiết bài thi sinh viên 28 - Bài thi 34 (Đề Điện tử Cuối kỳ)
INSERT INTO ChiTietBaiThi (ma_bai_thi, ma_cau_hoi, dap_an_sv) VALUES 
(34, 66, 'V = R * I'),
(34, 67, 'I = V / R'),
(34, 68, 'R'),
(34, 69, 'Tổng dòng điện vào bằng tổng dòng ra'),
(34, 70, 'Alternating Current'),
(34, 71, 'Khả năng tạo từ trường'),
(34, 72, 'Cả A và C'),
(34, 73, 'Hiệu suất hệ thống'),
(34, 74, 'Biến đổi điện áp AC'),
(34, 75, 'Khi tần số bằng tần số cộng hưởng'),
(34, 76, 'Bốn phương trình cơ bản'),
(34, 77, 'Phương pháp nút hoặc vòng'),
(34, 78, 'Mất điện năng trong điện môi'),   
(34, 79, 'Từ thông trong vật liệu'),
(34, 80, 'F = I * l * B * sin(θ)');

-- ========== 18. THÊM CÂU HỎI MỚI CHO TẤT CẢ CÁC MÔN ==========

-- ========== 18.1 Câu hỏi trắc nghiệm mới cho môn Java (ma_mon=1) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(81, 1, 2, 'Trong Java, từ khóa nào được sử dụng để tạo một giao diện?', 'De', 'MC'),
(82, 1, 2, 'Giá trị mặc định của biến boolean trong Java là gì?', 'De', 'MC'),
(83, 1, 3, 'Phương thức nào được dùng để so sánh hai chuỗi trong Java?', 'TrungBinh', 'MC'),
(84, 1, 3, 'Exception nào xảy ra khi chia một số cho 0?', 'TrungBinh', 'MC'),
(85, 1, 2, 'Từ khóa nào ngăn chặn việc ghi đè phương thức trong Java?', 'Kho', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(81, 'interface', 'class', 'abstract', 'implements', 'interface'),
(82, 'true', 'false', 'null', '0', 'false'),
(83, 'equals()', 'compare()', 'match()', 'isEqual()', 'equals()'),
(84, 'NullPointerException', 'ArithmeticException', 'NumberFormatException', 'IOException', 'ArithmeticException'),
(85, 'static', 'final', 'private', 'abstract', 'final');

-- ========== 18.2 Câu hỏi điền khuyết mới cho môn Java (ma_mon=1) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(86, 1, 2, 'Để xử lý ngoại lệ trong Java, ta dùng khối _____ - _____ - finally.', 'De', 'DK'),
(87, 1, 3, 'Để khai báo một mảng số nguyên trong Java, ta dùng: _____ arr = new int[10];', 'De', 'DK'),
(88, 1, 2, 'Từ khóa _____ được dùng để gọi constructor của lớp cha.', 'TrungBinh', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(86, 'try|catch|throw|throws', 'try|catch'),
(87, 'int[]|int|Integer[]|array', 'int[]'),
(88, 'super|this|parent|base', 'super');

-- ========== 18.3 Câu hỏi trắc nghiệm mới cho môn CSDL (ma_mon=2) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(89, 2, 4, 'Lệnh nào dùng để thay đổi cấu trúc bảng trong SQL?', 'De', 'MC'),
(90, 2, 4, 'Hàm nào dùng để đếm số bản ghi trong SQL?', 'De', 'MC'),
(91, 2, 6, 'Ràng buộc nào đảm bảo giá trị trong cột không được trùng lặp?', 'TrungBinh', 'MC'),
(92, 2, 6, 'Kiểu dữ liệu nào lưu trữ ngày tháng trong MySQL?', 'De', 'MC'),
(93, 2, 4, 'Trigger trong SQL được kích hoạt khi nào?', 'Kho', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(89, 'ALTER TABLE', 'MODIFY TABLE', 'CHANGE TABLE', 'UPDATE TABLE', 'ALTER TABLE'),
(90, 'COUNT()', 'SUM()', 'TOTAL()', 'NUMBER()', 'COUNT()'),
(91, 'PRIMARY KEY', 'UNIQUE', 'NOT NULL', 'CHECK', 'UNIQUE'),
(92, 'DATE', 'TIME', 'DATETIME', 'Tất cả các đáp án trên', 'Tất cả các đáp án trên'),
(93, 'Khi có sự kiện INSERT, UPDATE, DELETE', 'Khi chạy SELECT', 'Khi tạo bảng', 'Khi kết nối database', 'Khi có sự kiện INSERT, UPDATE, DELETE');

-- ========== 18.4 Câu hỏi điền khuyết mới cho môn CSDL (ma_mon=2) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(94, 2, 4, 'Lệnh _____ dùng để thêm dữ liệu vào bảng.', 'De', 'DK'),
(95, 2, 6, 'Mệnh đề _____ dùng để lọc dữ liệu sau khi GROUP BY.', 'TrungBinh', 'DK'),
(96, 2, 4, 'Khóa _____ được dùng để tham chiếu đến khóa chính của bảng khác.', 'TrungBinh', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(94, 'INSERT|ADD|PUT|CREATE', 'INSERT'),
(95, 'HAVING|WHERE|FILTER|CONDITION', 'HAVING'),
(96, 'ngoại|chính|phụ|tham chiếu', 'ngoại');

-- ========== 18.5 Câu hỏi trắc nghiệm mới cho môn Kinh tế (ma_mon=9) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(97, 9, 9, 'Lạm phát là gì?', 'De', 'MC'),
(98, 9, 9, 'Ngân hàng Trung ương điều tiết kinh tế bằng công cụ nào?', 'TrungBinh', 'MC'),
(99, 9, 10, 'Chỉ số CPI đo lường điều gì?', 'TrungBinh', 'MC'),
(100, 9, 10, 'Thị trường độc quyền có đặc điểm gì?', 'Kho', 'MC'),
(101, 9, 9, 'Đường cung dốc lên vì lý do gì?', 'TrungBinh', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(97, 'Mức giá chung tăng liên tục', 'Giảm giá hàng hóa', 'Tăng sản lượng', 'Giảm thất nghiệp', 'Mức giá chung tăng liên tục'),
(98, 'Lãi suất và cung tiền', 'Thuế và chi tiêu', 'Nhập khẩu và xuất khẩu', 'Giá và lượng', 'Lãi suất và cung tiền'),
(99, 'Mức giá tiêu dùng', 'Tỷ lệ thất nghiệp', 'Tổng sản phẩm quốc nội', 'Tỷ giá hối đoái', 'Mức giá tiêu dùng'),
(100, 'Chỉ có một người bán', 'Nhiều người bán', 'Sản phẩm đồng nhất', 'Dễ gia nhập thị trường', 'Chỉ có một người bán'),
(101, 'Giá cao hơn khuyến khích sản xuất nhiều hơn', 'Giá thấp hơn tăng lợi nhuận', 'Chi phí giảm khi sản xuất nhiều', 'Cầu tăng khi giá tăng', 'Giá cao hơn khuyến khích sản xuất nhiều hơn');

-- ========== 18.6 Câu hỏi điền khuyết mới cho môn Kinh tế (ma_mon=9) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(102, 9, 9, 'Khi cung vượt quá cầu, giá sẽ có xu hướng _____.', 'De', 'DK'),
(103, 9, 10, 'GDP là viết tắt của Gross _____ Product.', 'De', 'DK'),
(104, 9, 9, 'Ngân hàng Trung ương có thể giảm lạm phát bằng cách _____ lãi suất.', 'TrungBinh', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(102, 'giảm|tăng|ổn định|biến động', 'giảm'),
(103, 'Domestic|National|Internal|Local', 'Domestic'),
(104, 'tăng|giảm|giữ nguyên|điều chỉnh', 'tăng');

-- ========== 18.7 Câu hỏi trắc nghiệm mới cho môn Tiếng Anh (ma_mon=13) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(105, 13, 12, 'Choose the correct form: She _____ to school every day.', 'De', 'MC'),
(106, 13, 12, 'Which word is a noun?', 'De', 'MC'),
(107, 13, 13, 'What is the past tense of "go"?', 'De', 'MC'),
(108, 13, 13, 'Choose the correct sentence:', 'TrungBinh', 'MC'),
(109, 13, 12, 'Which preposition is correct: "I am interested _____ music"?', 'TrungBinh', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(105, 'goes', 'go', 'going', 'went', 'goes'),
(106, 'beautiful', 'quickly', 'happiness', 'run', 'happiness'),
(107, 'goed', 'went', 'gone', 'going', 'went'),
(108, 'He don''t like coffee', 'He doesn''t like coffee', 'He not like coffee', 'He no like coffee', 'He doesn''t like coffee'),
(109, 'in', 'on', 'at', 'for', 'in');

-- ========== 18.8 Câu hỏi điền khuyết mới cho môn Tiếng Anh (ma_mon=13) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(110, 13, 12, 'Complete: I _____ reading a book now.', 'De', 'DK'),
(111, 13, 13, 'Fill in: She has _____ to Paris twice.', 'TrungBinh', 'DK'),
(112, 13, 12, 'Complete: If I _____ rich, I would travel the world.', 'Kho', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(110, 'am|is|are|be', 'am'),
(111, 'been|gone|went|go', 'been'),
(112, 'were|was|am|be', 'were');

-- ========== 18.9 Câu hỏi trắc nghiệm mới cho môn Điện (ma_mon=17) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(113, 17, 14, 'Đơn vị của điện trở là gì?', 'De', 'MC'),
(114, 17, 14, 'Công thức tính công suất điện?', 'De', 'MC'),
(115, 17, 15, 'Dòng điện một chiều ký hiệu là gì?', 'De', 'MC'),
(116, 17, 15, 'Tụ điện có chức năng gì?', 'TrungBinh', 'MC'),
(117, 17, 14, 'Đơn vị của điện dung là gì?', 'TrungBinh', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(113, 'Volt', 'Ampere', 'Ohm', 'Watt', 'Ohm'),
(114, 'P = V x I', 'P = V / I', 'P = V + I', 'P = V - I', 'P = V x I'),
(115, 'AC', 'DC', 'SC', 'CC', 'DC'),
(116, 'Lưu trữ năng lượng điện trường', 'Tạo từ trường', 'Khuếch đại tín hiệu', 'Biến đổi điện áp', 'Lưu trữ năng lượng điện trường'),
(117, 'Henry', 'Farad', 'Ohm', 'Weber', 'Farad');

-- ========== 18.10 Câu hỏi điền khuyết mới cho môn Điện (ma_mon=17) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(118, 17, 14, 'Đơn vị của cường độ dòng điện là _____.', 'De', 'DK'),
(119, 17, 15, 'Định luật _____ phát biểu rằng V = I x R.', 'De', 'DK'),
(120, 17, 14, 'Trong mạch nối tiếp, dòng điện qua các phần tử là _____.', 'TrungBinh', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(118, 'Ampere|Volt|Ohm|Watt', 'Ampere'),
(119, 'Ohm|Kirchhoff|Faraday|Coulomb', 'Ohm'),
(120, 'bằng nhau|khác nhau|tăng dần|giảm dần', 'bằng nhau');

-- ========== 18.11 Câu hỏi mới cho môn Quản lý Dự án (ma_mon=10) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(121, 10, 10, 'Phương pháp Agile tập trung vào điều gì?', 'De', 'MC'),
(122, 10, 10, 'Gantt Chart dùng để làm gì?', 'TrungBinh', 'MC'),
(123, 10, 9, 'Stakeholder là gì?', 'De', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(121, 'Linh hoạt và phản hồi nhanh', 'Kế hoạch cứng nhắc', 'Tài liệu chi tiết', 'Quy trình phức tạp', 'Linh hoạt và phản hồi nhanh'),
(122, 'Hiển thị tiến độ dự án theo thời gian', 'Tính toán chi phí', 'Quản lý nhân sự', 'Phân tích rủi ro', 'Hiển thị tiến độ dự án theo thời gian'),
(123, 'Các bên liên quan của dự án', 'Nhân viên IT', 'Khách hàng mua hàng', 'Nhà cung cấp', 'Các bên liên quan của dự án');

INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(124, 10, 10, 'Sprint trong Scrum thường kéo dài _____ tuần.', 'De', 'DK'),
(125, 10, 9, 'WBS là viết tắt của Work _____ Structure.', 'TrungBinh', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(124, '1-4|5-6|7-8|9-10', '1-4'),
(125, 'Breakdown|Building|Business|Budget', 'Breakdown');

-- ========== 18.12 Câu hỏi mới cho môn Ngữ pháp Tiếng Anh Nâng cao (ma_mon=14) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(126, 14, 13, 'Which sentence uses the subjunctive mood?', 'Kho', 'MC'),
(127, 14, 13, 'Identify the correct use of "whom":', 'TrungBinh', 'MC'),
(128, 14, 12, 'Which is a complex sentence?', 'TrungBinh', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(126, 'I suggest that he be promoted', 'He is being promoted', 'He will be promoted', 'He was promoted', 'I suggest that he be promoted'),
(127, 'Whom did you see?', 'Whom is calling?', 'Whom went to the party?', 'Whom likes pizza?', 'Whom did you see?'),
(128, 'I like coffee and tea', 'I went home', 'When it rains, I stay inside', 'She is happy', 'When it rains, I stay inside');

INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(129, 14, 13, 'Complete: Had I known, I _____ have come earlier.', 'Kho', 'DK'),
(130, 14, 12, 'The passive form of "write": The letter was _____ by her.', 'TrungBinh', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(129, 'would|could|should|might', 'would'),
(130, 'written|wrote|write|writing', 'written');

-- ========== 18.13 Câu hỏi mới cho môn Điện tử Kỹ thuật (ma_mon=18) ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(131, 18, 15, 'Diode bán dẫn có chức năng gì?', 'De', 'MC'),
(132, 18, 15, 'Transistor loại NPN có bao nhiêu lớp bán dẫn?', 'De', 'MC'),
(133, 18, 14, 'Op-Amp là viết tắt của gì?', 'TrungBinh', 'MC');

INSERT INTO CauHoiMC (ma_cau_hoi_MC, noi_dung_A, noi_dung_B, noi_dung_C, noi_dung_D, noi_dung_dung) VALUES 
(131, 'Chỉ cho dòng điện qua một chiều', 'Khuếch đại tín hiệu', 'Lưu trữ năng lượng', 'Tạo dao động', 'Chỉ cho dòng điện qua một chiều'),
(132, 'Hai lớp', 'Ba lớp', 'Bốn lớp', 'Năm lớp', 'Ba lớp'),
(133, 'Operational Amplifier', 'Optional Amplifier', 'Output Amplifier', 'Oscillating Amplifier', 'Operational Amplifier');

INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(134, 18, 15, 'LED là viết tắt của Light _____ Diode.', 'De', 'DK'),
(135, 18, 14, 'Trong cổng logic AND, đầu ra bằng 1 khi tất cả đầu vào bằng _____.', 'TrungBinh', 'DK');

INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(134, 'Emitting|Electric|Energy|Efficient', 'Emitting'),
(135, '1|0|2|null', '1');

