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
    ten_mon VARCHAR(100) NOT NULL,              -- Tên môn học
    so_tin INT DEFAULT 3                        -- Số tín chỉ
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
(1, 'Trưởng Khoa'),   -- Quyền cao nhất: quản lý GV, SV, Ngành, HP
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
-- Các ngành thuộc từng khoa
INSERT INTO Nganh (ma_nganh, ma_khoa, ten_nganh) VALUES 
-- Ngành thuộc Khoa CNTT (ma_khoa = 1)
(1, 1, 'Công nghệ Thông tin'),
(2, 1, 'Hệ thống Thông tin'),
(3, 1, 'Kỹ thuật Phần mềm'),
(4, 1, 'Khoa học Máy tính'),
(5, 1, 'An toàn Thông tin'),
-- Ngành thuộc Khoa Kinh tế (ma_khoa = 2)
(6, 2, 'Quản trị Kinh doanh'),
(7, 2, 'Kế toán'),
-- Ngành thuộc Khoa Ngoại ngữ (ma_khoa = 3)
(8, 3, 'Ngôn ngữ Anh'),
(9, 3, 'Ngôn ngữ Nhật');

-- ========== 4. INSERT HocPhan ==========
-- Danh sách các học phần/môn học
INSERT INTO HocPhan (ma_hoc_phan, ten_mon, so_tin) VALUES 
(1, 'Lập trình Java', 3),
(2, 'Cơ sở dữ liệu', 3),
(3, 'Cấu trúc dữ liệu và Giải thuật', 3),
(4, 'Mạng máy tính', 3),
(5, 'Hệ điều hành', 3),
(6, 'Lập trình Web', 3),
(7, 'Trí tuệ nhân tạo', 3),
(8, 'An toàn và Bảo mật Thông tin', 3);

-- ========== 5. INSERT GiangVien ==========
-- Password mặc định: 123456 (plain text - code hỗ trợ cả plain text và SHA-256)
-- Trưởng Khoa CNTT (ma_vai_tro = 1)
INSERT INTO GiangVien (ma_gv, ma_khoa, ma_vai_tro, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(1, 1, 1, 'truongkhoa', '123456', 'Nguyễn Văn', 'An', 'truongkhoa@edu.vn', NOW());

-- Giảng viên thuộc Khoa CNTT (ma_vai_tro = 2)
INSERT INTO GiangVien (ma_gv, ma_khoa, ma_vai_tro, ten_dang_nhap, mat_khau, ho, ten, email, ngay_tao) VALUES 
(2, 1, 2, 'giangvien1', '123456', 'Trần Thị', 'Bình', 'giangvien1@edu.vn', NOW()),
(3, 1, 2, 'giangvien2', '123456', 'Lê Văn', 'Cường', 'giangvien2@edu.vn', NOW()),
(4, 1, 2, 'giangvien3', '123456', 'Phạm Thị', 'Dung', 'giangvien3@edu.vn', NOW()),
(5, 1, 2, 'giangvien4', '123456', 'Hoàng Minh', 'Em', 'giangvien4@edu.vn', NOW());

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

-- ========== 7. INSERT KyThi ==========
-- Các kỳ thi trong năm học
INSERT INTO KyThi (ma_ky_thi, ten_ky_thi, thoi_gian_bat_dau, thoi_gian_ket_thuc) VALUES 
(1, 'Kỳ thi Giữa kỳ HK1 2024-2025', '2024-10-01 00:00:00', '2024-10-15 23:59:59'),
(2, 'Kỳ thi Cuối kỳ HK1 2024-2025', '2024-12-15 00:00:00', '2024-12-30 23:59:59'),
(3, 'Kỳ thi Giữa kỳ HK2 2024-2025', '2025-03-01 00:00:00', '2025-03-15 23:59:59'),
(4, 'Kỳ thi Cuối kỳ HK2 2024-2025', '2025-05-15 00:00:00', '2025-05-30 23:59:59'),
(5, 'Kỳ thi Phụ HK2 2024-2025', '2025-06-15 00:00:00', '2025-06-30 23:59:59');

-- ========== 8. INSERT CauHoi - Môn Lập trình Java ==========
-- Câu hỏi trắc nghiệm môn Java (ma_mon = 1, ma_gv = 2)
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
(8, 1, 2, 'Interface trong Java có thể chứa những gì?', 'TrungBinh', 'MC'),
(9, 1, 3, 'Sự khác biệt giữa == và equals() trong Java?', 'TrungBinh', 'MC'),
(10, 1, 3, 'ArrayList và LinkedList khác nhau ở điểm nào?', 'TrungBinh', 'MC'),
-- Câu hỏi KHÓ
(11, 1, 2, 'Abstract class khác với Interface ở điểm nào chính?', 'Kho', 'MC'),
(12, 1, 2, 'Design Pattern nào đảm bảo chỉ có một instance duy nhất?', 'Kho', 'MC'),
(13, 1, 3, 'Garbage Collection trong Java hoạt động như thế nào?', 'Kho', 'MC'),
(14, 1, 3, 'Thread-safe là gì? Làm sao để đảm bảo thread-safe?', 'Kho', 'MC'),
(15, 1, 3, 'Giải thích cơ chế Reflection trong Java?', 'Kho', 'MC');

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
(16, 2, 4, 'SQL là viết tắt của?', 'De', 'MC'),
(17, 2, 4, 'Lệnh nào dùng để lấy dữ liệu từ bảng?', 'De', 'MC'),
(18, 2, 4, 'PRIMARY KEY có đặc điểm gì?', 'De', 'MC'),
(19, 2, 4, 'Lệnh nào dùng để thêm dữ liệu vào bảng?', 'De', 'MC'),
(20, 2, 4, 'Kiểu dữ liệu VARCHAR dùng để lưu gì?', 'De', 'MC'),
-- Câu hỏi TRUNG BÌNH
(21, 2, 4, 'FOREIGN KEY dùng để làm gì?', 'TrungBinh', 'MC'),
(22, 2, 4, 'Lệnh JOIN nào trả về tất cả bản ghi từ cả hai bảng?', 'TrungBinh', 'MC'),
(23, 2, 4, 'INDEX trong database có tác dụng gì?', 'TrungBinh', 'MC'),
(24, 2, 5, 'Sự khác biệt giữa WHERE và HAVING?', 'TrungBinh', 'MC'),
(25, 2, 5, 'DISTINCT trong SQL dùng để làm gì?', 'TrungBinh', 'MC'),
-- Câu hỏi KHÓ
(26, 2, 4, 'Giải thích các cấp độ cô lập transaction (Isolation Level)?', 'Kho', 'MC'),
(27, 2, 4, 'Normalization là gì? Các dạng chuẩn hóa?', 'Kho', 'MC'),
(28, 2, 5, 'Deadlock là gì? Cách phòng tránh?', 'Kho', 'MC'),
(29, 2, 5, 'Stored Procedure và Function khác nhau như thế nào?', 'Kho', 'MC'),
(30, 2, 5, 'Sharding và Partitioning khác nhau ở điểm nào?', 'Kho', 'MC');

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

-- ========== 12. INSERT CauHoi - Câu hỏi điền khuyết ==========
INSERT INTO CauHoi (ma_cau_hoi, ma_mon, ma_gv, noi_dung_cau_hoi, muc_do, loai_cau_hoi) VALUES 
(31, 1, 2, 'Trong Java, từ khóa _____ được dùng để kế thừa một lớp.', 'De', 'DK'),
(32, 1, 2, 'Phương thức _____ được gọi tự động khi tạo đối tượng mới.', 'De', 'DK'),
(33, 1, 3, 'Từ khóa _____ dùng để khai báo một hằng số trong Java.', 'TrungBinh', 'DK'),
(34, 2, 4, 'Lệnh _____ dùng để xóa dữ liệu trong bảng SQL.', 'De', 'DK'),
(35, 2, 4, 'Mệnh đề _____ dùng để sắp xếp kết quả truy vấn SQL.', 'De', 'DK');

-- ========== 13. INSERT CauHoiDK - Đáp án điền khuyết ==========
INSERT INTO CauHoiDK (ma_cau_hoi_DK, danh_sach_tu, noi_dung_dung) VALUES 
(31, 'extends|implements|inherits|derives', 'extends'),
(32, 'constructor|destructor|initializer|main', 'constructor'),
(33, 'final|const|static|constant', 'final'),
(34, 'DELETE|REMOVE|DROP|TRUNCATE', 'DELETE'),
(35, 'ORDER BY|SORT BY|ARRANGE BY|GROUP BY', 'ORDER BY');

-- ========== 14. INSERT DeThi ==========
INSERT INTO DeThi (ma_de_thi, ma_hoc_phan, ma_ky_thi, ma_gv, ten_de_thi, thoi_gian_lam, ngay_tao, so_cau_hoi) VALUES 
(1, 1, 1, 2, 'Đề thi Giữa kỳ - Lập trình Java - Đề 01', 45, NOW(), 10),
(2, 1, 1, 3, 'Đề thi Giữa kỳ - Lập trình Java - Đề 02', 45, NOW(), 10),
(3, 2, 1, 4, 'Đề thi Giữa kỳ - Cơ sở dữ liệu - Đề 01', 60, NOW(), 10),
(4, 2, 2, 4, 'Đề thi Cuối kỳ - Cơ sở dữ liệu - Đề 01', 90, NOW(), 15),
(5, 1, 2, 2, 'Đề thi Cuối kỳ - Lập trình Java - Đề 01', 90, NOW(), 15);

-- ========== 15. INSERT ChiTietDeThi ==========
-- Đề thi Java 1: 10 câu (1-10)
INSERT INTO ChiTietDeThi (ma_de_thi, ma_cau_hoi) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(1, 6), (1, 7), (1, 8), (1, 11), (1, 12);

-- Đề thi Java 2: 10 câu khác
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

-- ========== 16. INSERT BaiThi mẫu ==========
-- Một số bài thi mẫu đã hoàn thành
INSERT INTO BaiThi (ma_bai_thi, ma_de_thi, ma_sv, thoi_gian_bat_dau, thoi_gian_nop, ngay_thi, so_cau_dung, so_cau_sai, diem_so) VALUES 
(1, 1, 1, '2024-10-05 08:00:00', '2024-10-05 08:40:00', '2024-10-05', 8, 2, 8.0),
(2, 1, 2, '2024-10-05 08:00:00', '2024-10-05 08:35:00', '2024-10-05', 7, 3, 7.0),
(3, 1, 3, '2024-10-05 08:00:00', '2024-10-05 08:42:00', '2024-10-05', 9, 1, 9.0),
(4, 3, 4, '2024-10-06 09:00:00', '2024-10-06 09:55:00', '2024-10-06', 8, 2, 8.0),
(5, 3, 5, '2024-10-06 09:00:00', '2024-10-06 09:50:00', '2024-10-06', 6, 4, 6.0);

-- ========== 17. INSERT ChiTietBaiThi mẫu ==========
-- Chi tiết bài thi của sinh viên 1 (ma_bai_thi = 1)
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

-- =====================================================
-- VIEWS & INDEXES (Tối ưu hiệu suất)
-- =====================================================

-- View: Thông tin Sinh viên đầy đủ với Ngành và Khoa
CREATE VIEW v_SinhVien AS
SELECT 
    sv.ma_sv,
    sv.ten_dang_nhap AS ma_sinh_vien,
    sv.ho,
    sv.ten,
    CONCAT(sv.ho, ' ', sv.ten) AS ho_ten,
    sv.email,
    sv.ngay_tao,
    n.ma_nganh,
    n.ten_nganh,
    k.ma_khoa,
    k.ten_khoa
FROM SinhVien sv
LEFT JOIN Nganh n ON sv.ma_nganh = n.ma_nganh
LEFT JOIN Khoa k ON n.ma_khoa = k.ma_khoa;

-- View: Thông tin Giảng viên đầy đủ với Khoa
CREATE VIEW v_GiangVien AS
SELECT 
    gv.ma_gv,
    gv.ten_dang_nhap,
    gv.ho,
    gv.ten,
    CONCAT(gv.ho, ' ', gv.ten) AS ho_ten,
    gv.email,
    gv.ngay_tao,
    gv.ma_vai_tro,
    vt.ten_vai_tro,
    k.ma_khoa,
    k.ten_khoa
FROM GiangVien gv
LEFT JOIN VaiTro vt ON gv.ma_vai_tro = vt.ma_vai_tro
LEFT JOIN Khoa k ON gv.ma_khoa = k.ma_khoa;

-- View: Kết quả bài thi chi tiết
CREATE VIEW v_KetQuaBaiThi AS
SELECT 
    bt.ma_bai_thi,
    bt.ma_de_thi,
    dt.ten_de_thi,
    hp.ten_mon,
    bt.ma_sv,
    sv.ten_dang_nhap AS ma_sinh_vien,
    CONCAT(sv.ho, ' ', sv.ten) AS ho_ten_sv,
    bt.thoi_gian_bat_dau,
    bt.thoi_gian_nop,
    bt.ngay_thi,
    bt.so_cau_dung,
    bt.so_cau_sai,
    bt.diem_so,
    dt.so_cau_hoi AS tong_so_cau
FROM BaiThi bt
JOIN SinhVien sv ON bt.ma_sv = sv.ma_sv
JOIN DeThi dt ON bt.ma_de_thi = dt.ma_de_thi
JOIN HocPhan hp ON dt.ma_hoc_phan = hp.ma_hoc_phan;

-- View: Câu hỏi đầy đủ thông tin
CREATE VIEW v_CauHoi AS
SELECT 
    ch.ma_cau_hoi,
    ch.noi_dung_cau_hoi,
    ch.muc_do,
    ch.loai_cau_hoi,
    hp.ma_hoc_phan,
    hp.ten_mon,
    gv.ma_gv,
    CONCAT(gv.ho, ' ', gv.ten) AS nguoi_tao
FROM CauHoi ch
JOIN HocPhan hp ON ch.ma_mon = hp.ma_hoc_phan
JOIN GiangVien gv ON ch.ma_gv = gv.ma_gv;

-- Index để tối ưu truy vấn
CREATE INDEX idx_sinhvien_nganh ON SinhVien(ma_nganh);
CREATE INDEX idx_sinhvien_tendangnhap ON SinhVien(ten_dang_nhap);
CREATE INDEX idx_giangvien_khoa ON GiangVien(ma_khoa);
CREATE INDEX idx_giangvien_tendangnhap ON GiangVien(ten_dang_nhap);
CREATE INDEX idx_cauhoi_mon ON CauHoi(ma_mon);
CREATE INDEX idx_cauhoi_gv ON CauHoi(ma_gv);
CREATE INDEX idx_cauhoi_mucdo ON CauHoi(muc_do);
CREATE INDEX idx_dethi_hocphan ON DeThi(ma_hoc_phan);
CREATE INDEX idx_dethi_kythi ON DeThi(ma_ky_thi);
CREATE INDEX idx_baithi_sv ON BaiThi(ma_sv);
CREATE INDEX idx_baithi_dethi ON BaiThi(ma_de_thi);

-- =====================================================
-- END OF SCRIPT
-- =====================================================
