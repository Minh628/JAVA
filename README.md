# Hệ Thống Thi Trắc Nghiệm Trực Tuyến

## Giới thiệu
Hệ thống thi trắc nghiệm trực tuyến được xây dựng bằng Java Swing theo kiến trúc 3 tầng (3-tier):
- **GUI (Presentation Layer)**: Giao diện người dùng
- **BUS (Business Logic Layer)**: Xử lý nghiệp vụ
- **DAO (Data Access Layer)**: Truy cập cơ sở dữ liệu

## Cấu trúc thư mục
```
java/
├── database/
│   └── create_database.sql    # Script tạo CSDL MySQL
├── lib/                       # Thư viện (JDBC Driver)
│   └── mysql-connector-j-x.x.x.jar
└── src/
    ├── Main.java              # Entry point
    ├── bus/                   # Business Logic Layer
    │   ├── AuthBUS.java
    │   ├── TruongKhoaBUS.java
    │   ├── GiangVienBUS.java
    │   └── SinhVienThiBUS.java
    ├── config/
    │   └── DatabaseHelper.java
    ├── dao/                   # Data Access Layer
    │   ├── NganhDAO.java
    │   ├── HocPhanDAO.java
    │   ├── GiangVienDAO.java
    │   ├── SinhVienDAO.java
    │   ├── CauHoiDAO.java
    │   ├── KyThiDAO.java
    │   ├── DeThiDAO.java
    │   └── BaiThiDAO.java
    ├── dto/                   # Data Transfer Objects
    │   ├── VaiTroDTO.java
    │   ├── NganhDTO.java
    │   ├── HocPhanDTO.java
    │   ├── GiangVienDTO.java
    │   ├── SinhVienDTO.java
    │   ├── CauHoiDTO.java
    │   ├── KyThiDTO.java
    │   ├── DeThiDTO.java
    │   ├── BaiThiDTO.java
    │   ├── ChiTietDeThiDTO.java
    │   └── ChiTietBaiThiDTO.java
    ├── gui/                   # Presentation Layer
    │   ├── LoginFrame.java
    │   ├── AdminDashboard.java
    │   ├── TeacherDashboard.java
    │   ├── StudentDashboard.java
    │   └── ExamWindow.java
    └── util/
        └── PasswordEncoder.java
```

## Yêu cầu hệ thống
- JDK 8 trở lên
- MySQL Server 5.7 trở lên
- MySQL Connector/J (JDBC Driver)

