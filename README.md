# Hệ Thống Thi Trắc Nghiệm Trực Tuyến

## 1. Giới thiệu
Hệ thống thi trắc nghiệm trực tuyến được xây dựng bằng Java Swing theo kiến trúc 3 tầng (3-tier):
- **GUI (Presentation Layer)**: Giao diện người dùng
- **BUS (Business Logic Layer)**: Xử lý nghiệp vụ
- **DAO (Data Access Layer)**: Truy cập cơ sở dữ liệu

## 2. Công nghệ sử dụng
- **Ngôn ngữ**: Java 8+
- **GUI Framework**: Java Swing
- **Database**: MySQL Server 5.7+
- **Build Tool**: Maven
- **Thư viện**:
  - MySQL Connector/J (JDBC Driver)
  - Apache POI (Xuất Excel)
  - iText 7 (Xuất PDF)
  - JCalendar (Date Picker)
  - Ikonli (Icons)

## 3. Cấu trúc thư mục
```
java/
├── pom.xml                    # Maven configuration
├── database/
│   └── create_database.sql    # Script tạo CSDL MySQL
└── src/main/java/
    ├── Main.java              # Entry point
    ├── bus/                   # Business Logic Layer
    │   ├── BaiThiBUS.java     # Quản lý bài thi
    │   ├── CauHoiBUS.java     # Quản lý câu hỏi
    │   ├── DangNhapBUS.java   # Xác thực đăng nhập
    │   ├── DeThiBUS.java      # Quản lý đề thi
    │   ├── GiangVienBUS.java  # Quản lý giảng viên
    │   ├── HocPhanBUS.java    # Quản lý học phần
    │   ├── KhoaBUS.java       # Quản lý khoa
    │   ├── KyThiBUS.java      # Quản lý kỳ thi
    │   ├── NganhBUS.java      # Quản lý ngành
    │   ├── SinhVienBUS.java   # Quản lý sinh viên
    │   ├── ThongKeBUS.java    # Thống kê báo cáo
    │   └── VaiTroBUS.java     # Quản lý vai trò
    ├── config/
    │   ├── Constants.java     # Hằng số hệ thống
    │   └── DatabaseHelper.java# Kết nối database
    ├── dao/                   # Data Access Layer
    │   ├── BaiThiDAO.java
    │   ├── CauHoiDAO.java
    │   ├── ChiTietBaiThiDAO.java
    │   ├── ChiTietDeThiDAO.java
    │   ├── DangNhapDAO.java
    │   ├── DeThiDAO.java
    │   ├── GiangVienDAO.java
    │   ├── HocPhanDAO.java
    │   ├── KhoaDAO.java
    │   ├── KyThiDAO.java
    │   ├── NganhDAO.java
    │   ├── SinhVienDAO.java
    │   ├── ThongKeDAO.java
    │   └── VaiTroDAO.java
    ├── dto/                   # Data Transfer Objects
    │   ├── BaiThiDTO.java
    │   ├── CauHoiDTO.java     # Base class (abstract)
    │   ├── CauHoiMCDTO.java   # Câu hỏi trắc nghiệm
    │   ├── CauHoiDKDTO.java   # Câu hỏi điền khuyết
    │   ├── ChiTietBaiThiDTO.java
    │   ├── ChiTietDeThiDTO.java
    │   ├── DeThiDTO.java
    │   ├── GiangVienDTO.java
    │   ├── HocPhanDTO.java
    │   ├── KhoaDTO.java
    │   ├── KyThiDTO.java
    │   ├── NganhDTO.java
    │   ├── SinhVienDTO.java
    │   └── VaiTroDTO.java
    ├── gui/                   # Presentation Layer
    │   ├── login/             # Màn hình đăng nhập
    │   ├── admin/             # Giao diện Admin
    │   │   ├── AdminDashboard.java
    │   │   ├── QuanLyKhoaPanel.java
    │   │   ├── QuanLyNganhPanel.java
    │   │   ├── QuanLyHocPhanPanel.java
    │   │   ├── QuanLyKyThiPanel.java
    │   │   ├── QuanLyGiangVienPanel.java
    │   │   ├── QuanLySinhVienPanel.java
    │   │   ├── ThongKePanel.java
    │   │   └── ThongTinAdminPanel.java
    │   ├── teacher/           # Giao diện Giảng viên
    │   │   ├── TeacherDashboard.java
    │   │   ├── SoanCauHoiPanel.java
    │   │   ├── QuanLyDeThiPanel.java
    │   │   ├── XemDiemSinhVienPanel.java
    │   │   └── ThongTinGiangVienPanel.java
    │   ├── student/           # Giao diện Sinh viên
    │   │   ├── StudentDashboard.java
    │   │   ├── ThiTracNghiemPanel.java
    │   │   ├── LamBaiThiFrame.java
    │   │   ├── LichSuThiPanel.java
    │   │   ├── ChiTietDiemDialog.java
    │   │   └── ThongTinSinhVienPanel.java
    │   └── components/        # UI Components tái sử dụng
    │       ├── BaseCrudPanel.java
    │       ├── BaseDashboardFrame.java
    │       ├── CustomButton.java
    │       ├── CustomTable.java
    │       ├── SimpleBarChart.java
    │       ├── SimplePieChart.java
    │       └── ...
    └── util/                  # Tiện ích
        ├── BangDiemExcelExporter.java
        ├── CauHoiExcelImporter.java
        ├── GiangVienExcelImporter.java
        ├── SinhVienExcelImporter.java
        ├── PDFExporter.java
        ├── IconHelper.java
        └── SearchCondition.java
```

## 4. Yêu cầu hệ thống
- JDK 8 trở lên
- MySQL Server 5.7 trở lên
- Maven 3.6+

## 5. Cài đặt và chạy
```bash
# 1. Clone repository
git clone <repository-url>

# 2. Import database
mysql -u root -p < database/create_database.sql

# 3. Cấu hình kết nối database trong config/DatabaseHelper.java

# 4. Build và chạy
mvn clean compile exec:java
```

## 6. Đăng nhập mặc định
| Vai trò | Tên đăng nhập | Mật khẩu |
|---------|---------------|----------|
| Admin | admin | admin |
| Giảng viên | giangvien1 | 123456 |
| Sinh viên | 20110001 | 123456 |

## 7. Cấu trúc Database (ERD)
### Các bảng chính:
- **VaiTro**: Quản lý vai trò (Admin, Giảng viên, Sinh viên)
- **Khoa**: Quản lý các khoa
- **Nganh**: Quản lý ngành học (thuộc khoa)
- **HocPhan**: Quản lý học phần/môn học
- **GiangVien**: Quản lý giảng viên
- **SinhVien**: Quản lý sinh viên
- **CauHoi**: Ngân hàng câu hỏi
- **CauHoiMC**: Chi tiết câu hỏi trắc nghiệm (4 đáp án A/B/C/D)
- **CauHoiDK**: Chi tiết câu hỏi điền khuyết
- **KyThi**: Quản lý kỳ thi
- **DeThi**: Quản lý đề thi
- **ChiTietDeThi**: Danh sách câu hỏi trong đề thi
- **BaiThi**: Bài thi của sinh viên
- **ChiTietBaiThi**: Chi tiết đáp án của sinh viên

## 8. Phân quyền người dùng
| Chức năng            | Admin | Giảng viên | Sinh viên |
|----------------------|:-----:|:----------:|:---------:|
| Quản lý Khoa         |   ✓   |     -      |     -     |
| Quản lý Ngành        |   ✓   |     -      |     -     |
| Quản lý Học phần     |   ✓   |     -      |     -     |
| Quản lý Kỳ thi       |   ✓   |     -      |     -     |
| Quản lý Giảng viên   |   ✓   |     -      |     -     |
| Quản lý Sinh viên    |   ✓   |     -      |     -     |
| Thống kê tổng quan   |   ✓   |     -      |     -     |
| Soạn câu hỏi         |   -   |     ✓      |     -     |
| Tạo đề thi           |   -   |     ✓      |     -     |
| Xem điểm sinh viên   |   -   |     ✓      |     -     |
| Làm bài thi          |   -   |     -      |     ✓     |
| Xem lịch sử thi      |   -   |     -      |     ✓     |
| Đổi mật khẩu         |   ✓   |     ✓      |     ✓     |
## 9. Các loại câu hỏi hỗ trợ
1. **Trắc nghiệm (MC - Multiple Choice)**: 4 đáp án A, B, C, D - chọn 1 đáp án đúng
2. **Điền khuyết (DK)**: Điền từ/cụm từ vào chỗ trống

---

## 10. TÓM TẮT TÀI LIỆU YÊU CẦU (REQUIREMENT)

### 10.a. DANH SÁCH CHỨC NĂNG

#### **1. Chức năng Đăng nhập / Xác thực**
   - a. Đăng nhập hệ thống (Admin/Giảng viên/Sinh viên)
   - b. Đổi mật khẩu
   - c. Đăng xuất

#### **2. Chức năng Quản lý Khoa (Admin)**
   - a. Xem danh sách khoa
   - b. Thêm khoa mới
   - c. Sửa thông tin khoa
   - d. Xóa khoa (nếu không có ngành thuộc khoa)
   - e. Tìm kiếm khoa (theo mã, tên)

#### **3. Chức năng Quản lý Ngành (Admin)**
   - a. Xem danh sách ngành
   - b. Thêm ngành mới (gán vào khoa)
   - c. Sửa thông tin ngành
   - d. Xóa ngành (nếu không có sinh viên thuộc ngành)
   - e. Tìm kiếm ngành (theo mã, tên, khoa)

#### **4. Chức năng Quản lý Học phần (Admin)**
   - a. Xem danh sách học phần
   - b. Thêm học phần mới (gán vào khoa)
   - c. Sửa thông tin học phần
   - d. Xóa học phần
   - e. Tìm kiếm học phần (theo mã, tên, số tín chỉ, khoa)

#### **5. Chức năng Quản lý Kỳ thi (Admin)**
   - a. Xem danh sách kỳ thi
   - b. Thêm kỳ thi mới (thời gian bắt đầu, kết thúc)
   - c. Sửa thông tin kỳ thi
   - d. Xóa kỳ thi
   - e. Tìm kiếm kỳ thi (theo mã, tên, trạng thái)
   - f. Xem trạng thái kỳ thi (Chưa bắt đầu/Đang diễn ra/Đã kết thúc)

#### **6. Chức năng Quản lý Giảng viên (Admin)**
   - a. Xem danh sách giảng viên
   - b. Thêm giảng viên mới
   - c. Sửa thông tin giảng viên
   - d. Xóa giảng viên
   - e. Tìm kiếm giảng viên (theo mã, họ tên, email, khoa)
   - f. Import giảng viên từ Excel
   - g. Tìm kiếm nâng cao (nhiều điều kiện)

#### **7. Chức năng Quản lý Sinh viên (Admin)**
   - a. Xem danh sách sinh viên
   - b. Thêm sinh viên mới
   - c. Sửa thông tin sinh viên
   - d. Xóa sinh viên
   - e. Tìm kiếm sinh viên (theo MSSV, họ tên, email, ngành)
   - f. Import sinh viên từ Excel
   - g. Tìm kiếm nâng cao (nhiều điều kiện)

#### **8. Chức năng Soạn câu hỏi (Giảng viên)**
   - a. Xem danh sách câu hỏi (của mình)
   - b. Thêm câu hỏi trắc nghiệm (MC) - 4 đáp án A/B/C/D
   - c. Thêm câu hỏi điền khuyết (DK)
   - d. Sửa câu hỏi
   - e. Xóa câu hỏi (nếu chưa được sử dụng trong đề thi)
   - f. Tìm kiếm câu hỏi (theo nội dung, học phần, mức độ)
   - g. Import câu hỏi từ Excel
   - h. Phân loại theo mức độ (Dễ/Trung bình/Khó)

#### **9. Chức năng Quản lý Đề thi (Giảng viên)**
   - a. Xem danh sách đề thi (của mình)
   - b. Tạo đề thi mới (chọn học phần, kỳ thi, thời gian làm bài)
   - c. Sửa thông tin đề thi
   - d. Xóa đề thi (nếu chưa có sinh viên làm)
   - e. Quản lý câu hỏi trong đề thi (thêm/xóa câu hỏi)
   - f. Tìm kiếm đề thi (theo tên, học phần, kỳ thi)
   - g. Tìm kiếm nâng cao (nhiều điều kiện)

#### **10. Chức năng Xem điểm sinh viên (Giảng viên)**
   - a. Xem danh sách bài thi của sinh viên (trong khoa)
   - b. Tìm kiếm theo MSSV, họ tên, đề thi, môn học
   - c. Xuất bảng điểm ra Excel

#### **11. Chức năng Làm bài thi (Sinh viên)**
   - a. Xem danh sách kỳ thi đang diễn ra
   - b. Xem danh sách đề thi trong kỳ thi
   - c. Vào thi (kiểm tra chưa thi trước đó)
   - d. Làm bài (trả lời câu hỏi, đếm ngược thời gian)
   - e. Chuyển câu hỏi (tới/lui)
   - f. Đánh dấu câu hỏi cần xem lại
   - g. Nộp bài (thủ công hoặc tự động khi hết giờ)
   - h. Xem kết quả ngay sau khi nộp

#### **12. Chức năng Xem lịch sử thi (Sinh viên)**
   - a. Xem danh sách bài thi đã làm
   - b. Xem chi tiết kết quả bài thi
   - c. Tìm kiếm lịch sử (theo mã bài thi, đề thi, môn học)

#### **13. Chức năng Thống kê (Admin)**
   - a. Thống kê tổng quan (tổng bài thi, đề thi, điểm TB, tỷ lệ đạt)
   - b. Thống kê theo Khoa
   - c. Thống kê theo Ngành
   - d. Thống kê theo Học phần
   - e. Thống kê theo Kỳ thi
   - f. Thống kê theo Quý (cross-tabulation Q1/Q2/Q3/Q4)
   - g. Thống kê Giảng viên theo Quý
   - h. Thống kê Sinh viên theo Quý
   - i. Thống kê Sinh viên & Học phần
   - j. Thống kê Giảng viên & Học phần theo Năm
   - k. Lọc theo: Khoảng ngày / Tháng / Quý / Năm
   - l. Xuất báo cáo PDF
   - m. Hiển thị biểu đồ (Bar Chart, Pie Chart)

---

### 10.b. THUYẾT MINH CÔNG THỨC TÍNH TOÁN

#### **1. Công thức tính điểm bài thi**
```
Điểm số = (Số câu đúng / Tổng số câu) × 10
```
- **Đầu vào**: 
  - Tổng số câu hỏi trong đề thi
  - Số câu trả lời đúng
- **Đầu ra**: Điểm số (thang điểm 10)
- **Ví dụ**: Đề có 30 câu, sinh viên đúng 24 câu → Điểm = 24/30 × 10 = 8.0 điểm

#### **2. Công thức tính tỷ lệ đạt**
```
Tỷ lệ đạt (%) = (Số sinh viên đạt / Tổng số bài thi) × 100
```
- **Điều kiện đạt**: Điểm >= 5.0
- **Đầu vào**:
  - Tổng số bài thi trong khoảng thời gian
  - Số bài thi có điểm >= 5.0
- **Đầu ra**: Tỷ lệ phần trăm

#### **3. Công thức tính điểm trung bình**
```
Điểm trung bình = Tổng điểm tất cả bài thi / Tổng số bài thi
```
- **Đầu vào**: Danh sách điểm các bài thi
- **Đầu ra**: Điểm trung bình (làm tròn 2 chữ số thập phân)

#### **4. Công thức xác định trạng thái kỳ thi**
```
Nếu (Ngày hiện tại < Ngày bắt đầu) → "Chưa bắt đầu"
Nếu (Ngày bắt đầu <= Ngày hiện tại <= Ngày kết thúc) → "Đang diễn ra"
Nếu (Ngày hiện tại > Ngày kết thúc) → "Đã kết thúc"
```

#### **5. Công thức tính thời gian làm bài còn lại**
```
Thời gian còn lại = Thời gian cho phép - (Thời điểm hiện tại - Thời điểm bắt đầu)
```
- **Đầu vào**:
  - Thời gian làm bài cho phép (phút)
  - Thời điểm bắt đầu làm bài
- **Đầu ra**: Số phút/giây còn lại
- **Xử lý**: Tự động nộp bài khi thời gian còn lại = 0

#### **6. Công thức chuyển đổi khoảng thời gian theo Tháng/Quý/Năm**
```
# Theo Tháng:
Ngày đầu tháng = Ngày 1 của tháng
Ngày cuối tháng = Ngày cuối cùng của tháng

# Theo Quý:
Quý 1: Tháng 1-3   → Ngày 01/01 đến 31/03
Quý 2: Tháng 4-6   → Ngày 01/04 đến 30/06
Quý 3: Tháng 7-9   → Ngày 01/07 đến 30/09
Quý 4: Tháng 10-12 → Ngày 01/10 đến 31/12

# Theo Năm:
Ngày đầu năm = 01/01/YYYY
Ngày cuối năm = 31/12/YYYY
```

---

### 10.c. ĐẶC TẢ CHỨC NĂNG CHI TIẾT (ĐẦU VÀO / ĐẦU RA / XỬ LÝ)

#### **1. Chức năng: Đăng nhập hệ thống**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Tên đăng nhập (String), Mật khẩu (String) |
| **Đầu ra** | Đối tượng người dùng (GiangVienDTO hoặc SinhVienDTO), hoặc null nếu thất bại |
| **Xử lý** | 1. Tìm trong bảng GiangVien theo tên đăng nhập<br>2. Nếu tìm thấy và mật khẩu khớp → Trả về GiangVienDTO<br>3. Nếu không, tìm trong bảng SinhVien<br>4. Nếu tìm thấy và mật khẩu khớp → Trả về SinhVienDTO<br>5. Nếu không có → Trả về null |

#### **2. Chức năng: Thêm Khoa mới**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Tên khoa (String, không rỗng) |
| **Đầu ra** | true nếu thêm thành công, false nếu thất bại |
| **Xử lý** | 1. Kiểm tra tên khoa không rỗng<br>2. Tạo KhoaDTO mới<br>3. Gọi KhoaDAO.insert()<br>4. Cập nhật cache danh sách khoa<br>5. Trả về kết quả |

#### **3. Chức năng: Xóa Khoa**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Mã khoa (int) |
| **Đầu ra** | true nếu xóa thành công, false nếu có ràng buộc |
| **Xử lý** | 1. Kiểm tra còn ngành thuộc khoa không (NganhDAO.countByKhoa)<br>2. Nếu còn ngành → Trả về false<br>3. Gọi KhoaDAO.delete()<br>4. Cập nhật cache<br>5. Trả về kết quả |

#### **4. Chức năng: Thêm Sinh viên**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | SinhVienDTO (MSSV, họ, tên, email, mã ngành, mật khẩu) |
| **Đầu ra** | true nếu thêm thành công, false nếu MSSV đã tồn tại |
| **Xử lý** | 1. Kiểm tra MSSV đã tồn tại chưa (checkTenDangNhapExists)<br>2. Nếu tồn tại → Trả về false<br>3. Thiết lập mã vai trò = SINH_VIEN (3)<br>4. Gọi SinhVienDAO.insert()<br>5. Cập nhật cache<br>6. Trả về kết quả |

#### **5. Chức năng: Thêm Câu hỏi trắc nghiệm**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | CauHoiMCDTO (nội dung câu hỏi, mã học phần, mã GV, mức độ, 4 đáp án A/B/C/D, đáp án đúng) |
| **Đầu ra** | true nếu thêm thành công |
| **Xử lý** | 1. Thiết lập loại câu hỏi = "MC"<br>2. Insert vào bảng CauHoi → Lấy mã câu hỏi tự tăng<br>3. Insert vào bảng CauHoiMC với mã câu hỏi đã lấy<br>4. Cập nhật cache<br>5. Trả về kết quả |

#### **6. Chức năng: Tạo Đề thi mới**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | DeThiDTO (tên đề thi, mã học phần, mã kỳ thi, mã GV, thời gian làm bài, số câu hỏi) |
| **Đầu ra** | true nếu tạo thành công |
| **Xử lý** | 1. Kiểm tra các trường bắt buộc<br>2. Thiết lập ngày tạo = thời điểm hiện tại<br>3. Gọi DeThiDAO.insert()<br>4. Cập nhật cache<br>5. Trả về kết quả |

#### **7. Chức năng: Thêm câu hỏi vào đề thi**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Mã đề thi (int), Mã câu hỏi (int), Thứ tự (int) |
| **Đầu ra** | true nếu thêm thành công |
| **Xử lý** | 1. Kiểm tra câu hỏi chưa có trong đề<br>2. Tạo ChiTietDeThiDTO<br>3. Insert vào bảng ChiTietDeThi<br>4. Cập nhật số câu hỏi trong đề thi<br>5. Trả về kết quả |

#### **8. Chức năng: Bắt đầu làm bài thi**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Mã đề thi (int), Mã sinh viên (int) |
| **Đầu ra** | Mã bài thi (int), -1 nếu đã thi hoặc thất bại |
| **Xử lý** | 1. Kiểm tra sinh viên đã thi đề này chưa (checkDaThi)<br>2. Nếu đã thi → Trả về -1<br>3. Tạo BaiThiDTO mới với thời gian bắt đầu = hiện tại<br>4. Insert vào bảng BaiThi<br>5. Trả về mã bài thi tự tăng |

#### **9. Chức năng: Lưu đáp án sinh viên**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Mã bài thi (int), Mã câu hỏi (int), Đáp án sinh viên (String) |
| **Đầu ra** | true nếu lưu thành công |
| **Xử lý** | 1. Cập nhật hoặc insert vào bảng ChiTietBaiThi<br>2. Trả về kết quả |

#### **10. Chức năng: Tính điểm và nộp bài**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Mã bài thi (int) |
| **Đầu ra** | float[] {số câu đúng, số câu sai, điểm số} |
| **Xử lý** | 1. Lấy danh sách chi tiết bài thi (ChiTietBaiThi)<br>2. Với mỗi câu hỏi:<br>&nbsp;&nbsp;- Lấy đáp án đúng từ CauHoi/CauHoiMC/CauHoiDK<br>&nbsp;&nbsp;- So sánh với đáp án sinh viên<br>&nbsp;&nbsp;- Tăng số câu đúng hoặc sai<br>3. Tính điểm = (soCauDung / tongSoCau) × 10<br>4. Cập nhật BaiThi (thời gian nộp, số câu đúng/sai, điểm)<br>5. Trả về kết quả |

#### **11. Chức năng: Kiểm tra đáp án câu hỏi trắc nghiệm**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | CauHoiMCDTO, Đáp án sinh viên (String: "A"/"B"/"C"/"D" hoặc nội dung) |
| **Đầu ra** | true nếu đúng, false nếu sai |
| **Xử lý** | 1. Nếu đáp án là ký hiệu A/B/C/D → Chuyển thành nội dung tương ứng<br>2. So sánh với nội dung đáp án đúng (không phân biệt hoa thường)<br>3. Trả về kết quả |

#### **12. Chức năng: Thống kê tổng quan**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Ngày bắt đầu (Date), Ngày kết thúc (Date) |
| **Đầu ra** | Map<String, Object> {tongBaiThi, tongDeThi, diemTrungBinh, soDat, soRot, tyLeDat} |
| **Xử lý** | 1. Query COUNT bài thi trong khoảng thời gian<br>2. Query COUNT đề thi trong khoảng thời gian<br>3. Query AVG(diem_so) các bài thi<br>4. Query COUNT bài thi có điểm >= 5 (đạt)<br>5. Query COUNT bài thi có điểm < 5 (rớt)<br>6. Tính tỷ lệ đạt = soDat / (soDat + soRot) × 100<br>7. Trả về Map kết quả |

#### **13. Chức năng: Xuất bảng điểm ra Excel**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | List<BaiThiDTO> danh sách bài thi, String tiêu đề |
| **Đầu ra** | File Excel (.xlsx) |
| **Xử lý** | 1. Tạo Workbook mới (Apache POI)<br>2. Tạo Sheet "BangDiem"<br>3. Tạo header: STT, MSSV, Họ tên, Đề thi, Môn học, Ngày thi, Số câu đúng, Điểm, Kết quả<br>4. Với mỗi bài thi → Thêm 1 row dữ liệu<br>5. Thêm thống kê tổng (điểm TB, tỷ lệ đạt)<br>6. Lưu file<br>7. Mở file sau khi xuất |

#### **14. Chức năng: Xuất báo cáo thống kê PDF**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | Dữ liệu thống kê (Map hoặc List), Khoảng thời gian, Tiêu đề báo cáo |
| **Đầu ra** | File PDF |
| **Xử lý** | 1. Tạo PdfDocument mới (iText 7)<br>2. Load font tiếng Việt (Arial hoặc Times New Roman)<br>3. Thêm tiêu đề báo cáo<br>4. Thêm thông tin thời gian<br>5. Tạo bảng số liệu với header và dữ liệu<br>6. Thêm footer (ngày xuất, người xuất)<br>7. Lưu file<br>8. Mở file sau khi xuất |

#### **15. Chức năng: Import Sinh viên từ Excel**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | File Excel (.xlsx) với các cột: MSSV, Họ, Tên, Email, Ngành |
| **Đầu ra** | Số sinh viên import thành công, danh sách lỗi |
| **Xử lý** | 1. Đọc file Excel (Apache POI)<br>2. Bỏ qua header row<br>3. Với mỗi row:<br>&nbsp;&nbsp;- Đọc các ô dữ liệu<br>&nbsp;&nbsp;- Validate: MSSV không rỗng, không trùng<br>&nbsp;&nbsp;- Tìm mã ngành theo tên ngành<br>&nbsp;&nbsp;- Tạo SinhVienDTO<br>&nbsp;&nbsp;- Mật khẩu mặc định = MSSV<br>&nbsp;&nbsp;- Insert vào database<br>4. Trả về kết quả (thành công/thất bại) |

#### **16. Chức năng: Tìm kiếm nâng cao**
| Mục | Chi tiết |
|-----|----------|
| **Đầu vào** | List<SearchCondition> (mỗi condition gồm: trường, toán tử, giá trị, AND/OR) |
| **Đầu ra** | Danh sách đối tượng thỏa điều kiện |
| **Xử lý** | 1. Xây dựng câu query SQL động từ các condition<br>2. Với mỗi condition:<br>&nbsp;&nbsp;- Thêm tên trường<br>&nbsp;&nbsp;- Thêm toán tử (=, LIKE, >, <, >=, <=)<br>&nbsp;&nbsp;- Thêm giá trị so sánh<br>&nbsp;&nbsp;- Nối với AND hoặc OR<br>3. Thực thi query<br>4. Trả về danh sách kết quả |

---

### BẢNG TỔNG HỢP CÔNG THỨC

| STT | Công thức | Mô tả | Áp dụng |
|-----|-----------|-------|---------|
| 1 | `Điểm = (SốCâuĐúng / TổngSốCâu) × 10` | Tính điểm bài thi | BaiThiBUS.tinhDiem() |
| 2 | `TỷLệĐạt = (SốĐạt / TổngBàiThi) × 100` | Thống kê tỷ lệ đạt | ThongKeBUS |
| 3 | `ĐiểmTB = SUM(Điểm) / COUNT(BàiThi)` | Điểm trung bình | ThongKeBUS |
| 4 | `ĐiềuKiệnĐạt: Điểm >= 5.0` | Xét đạt/rớt | ThongKeDAO |
| 5 | `ThoiGianConLai = ThoiGianLam - (Now - ThoiDiemBatDau)` | Đếm ngược thời gian thi | LamBaiThiFrame |

---

*Tài liệu được cập nhật: Tháng 02/2026*

