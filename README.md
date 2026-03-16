
# Project_GK_Nhom_14
# Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ
Nhóm sinh viên thực hiện (Nhóm 14):

Bùi Phúc Nhân - 23110278

Bùi Nhật Dương - 23110198

Huỳnh Ngọc Tài - 23110305
## Giới thiệu dự án
Đây là một dự án ứng dụng Desktop được xây dựng bằng Java Swing, mô phỏng hệ thống quản lý cho một trung tâm ngoại ngữ. Ứng dụng cho phép quản lý đa vai trò bao gồm Quản trị viên (Admin), Nhân viên (Staff), Giáo viên (Teacher), và Học viên (Student), mỗi vai trò có những chức năng và quyền hạn riêng biệt.

## 🌟 Tính Năng Chính

Hệ thống được chia thành nhiều module chức năng, bao gồm:

-   **👤 Quản lý Người dùng:**
    -   Quản lý hồ sơ Nhân viên, Giáo viên, Học viên (CRUD).
    -   Quản lý Tài khoản đăng nhập và phân quyền.

-   **🏛️ Quản lý Cơ sở vật chất:**
    -   Quản lý các chi nhánh của trung tâm.
    -   Quản lý phòng học theo từng chi nhánh.

-   **📚 Quản lý Đào tạo:**
    -   Quản lý danh sách các khóa học.
    -   Mở và quản lý các lớp học cụ thể cho từng khóa học.
    -   Ghi danh học viên vào các lớp.
    -   Quản lý kết quả thi xếp lớp đầu vào.
    -   Quản lý và cấp phát chứng chỉ.

-   **💰 Quản lý Tài chính:**
    -   Tạo và quản lý hóa đơn học phí.
    -   Ghi nhận các giao dịch thanh toán.
    -   Xử lý hoàn tiền cho học viên.
    -   Quản lý các chương trình khuyến mãi.

-   **📅 Quản lý Lịch & Điểm danh:**
    -   Xem lịch hoạt động chung của toàn trung tâm.
    -   Giáo viên xem lịch dạy cá nhân.
    -   Học viên xem lịch học cá nhân.
    -   Giáo viên thực hiện điểm danh cho lớp học.

-   **🎓 Quản lý Điểm số:**
    -   Giáo viên nhập và quản lý điểm cho học viên.
    -   Học viên xem kết quả học tập của mình.

-   **📢 Hệ thống Thông báo:**
    -   Admin/Staff tạo và gửi thông báo đến các nhóm người dùng khác nhau.
    -   Người dùng xem các thông báo liên quan đến mình.

## 🛠️ Công Nghệ Sử Dụng

-   **Ngôn ngữ:** Java 17
-   **Giao diện người dùng (UI):** Java Swing
-   **Thư viện UI:** [FlatLaf](https://www.formdev.com/flatlaf/) - Giao diện phẳng, hiện đại cho Swing.
-   **Database & ORM:** JPA (Hibernate)
-   **Cơ sở dữ liệu:** MySQL (hoặc các CSDL quan hệ khác được Hibernate hỗ trợ)
-   **Build Tool:** Apache Maven
-   **Thư viện khác:**
    -   `com.toedter:jcalendar`: Để chọn ngày tháng.
    -   `org.mindrot:jbcrypt`: (Đề xuất) Để băm mật khẩu an toàn.

## 🏗️ Cấu Trúc Dự Án

Dự án được tổ chức theo kiến trúc phân lớp (Layered Architecture) để đảm bảo sự tách biệt và dễ dàng bảo trì:

-   `vn.edu.ute.model`: Chứa các lớp Entity (POJO) được ánh xạ tới các bảng trong cơ sở dữ liệu bằng JPA.
-   `vn.edu.ute.db`: Quản lý kết nối cơ sở dữ liệu (`Jpa`), quản lý giao dịch (`TransactionManager`).
-   `vn.edu.ute.repo`: Chứa các interface của tầng Repository (Data Access Layer), định nghĩa các phương thức truy xuất dữ liệu.
-   `vn.edu.ute.repo.jpa`: Các lớp triển khai cụ thể của Repository sử dụng JPA và `EntityManager`.
-   `vn.edu.ute.service`: Tầng nghiệp vụ (Business Logic Layer), chứa logic xử lý chính của ứng dụng.
-   `vn.edu.ute.ui`: Tầng trình bày (Presentation Layer), chứa các `JPanel`, `JFrame` để xây dựng giao diện người dùng.
-   `vn.edu.ute.controller`: Tầng điều khiển, kết nối sự kiện từ UI với các xử lý ở tầng Service.
-   `vn.edu.ute.App.java`: Điểm khởi đầu của ứng dụng, nơi các thành phần được khởi tạo và liên kết với nhau (Dependency Injection thủ công).

## 📐 Nguyên Tắc Thiết Kế (Design Principles)

Dự án cố gắng tuân thủ các nguyên tắc thiết kế SOLID để mã nguồn trở nên linh hoạt, dễ mở rộng và bảo trì:

1.  **Single Responsibility Principle (SRP):**
    -   Mỗi lớp có một trách nhiệm duy nhất. Ví dụ: `InvoiceService`, `PaymentService`, và `RefundService` được tách ra từ một `FinanceService` lớn để mỗi lớp chỉ xử lý một nghiệp vụ tài chính cụ thể.
    -   Các lớp `Panel` trong `ui` chỉ chịu trách nhiệm hiển thị và nhận sự kiện, không chứa logic nghiệp vụ.

2.  **Open/Closed Principle (OCP):**
    -   **Dễ dàng mở rộng, hạn chế sửa đổi.** Ví dụ điển hình là hệ thống `MenuBuilder`. Để thêm một vai trò người dùng mới, ta chỉ cần tạo một lớp `...MenuBuilder` mới và implement interface `MenuBuilder` mà không cần sửa đổi `MainFrame`.

3.  **Dependency Inversion Principle (DIP):**
    -   Các module cấp cao không phụ thuộc vào các module cấp thấp. Cả hai nên phụ thuộc vào abstraction.
    -   `Controller` phụ thuộc vào interface của `Service` (ví dụ: `UserAccountService`).
    -   `Service` phụ thuộc vào interface của `Repository` (ví dụ: `StudentRepository`).
    -   `TransactionManager` phụ thuộc vào `EntityManagerProvider` thay vì lớp `Jpa` cụ thể.

4.  **Interface Segregation Principle (ISP):**
    -   Các client không nên bị buộc phải phụ thuộc vào các phương thức mà chúng không sử dụng.
    -   Ví dụ: `ResultRepository` được tách thành hai interface nhỏ hơn là `GradeEntryRepository` (cho giáo viên nhập điểm) và `StudentGradeRepository` (cho học viên xem điểm). `GradeEntryService` chỉ phụ thuộc vào `GradeEntryRepository`.

## 🚀 Cài Đặt và Chạy Dự Án

### Yêu Cầu

1.  **JDK 17** hoặc cao hơn.
2.  **Apache Maven** 3.6+
3.  **MySQL Server** 8.0+

### Các Bước Cài Đặt

1.  **Clone Repository:**
    ```bash
    git clone <URL_CUA_REPOSITORY>
    cd Project_GK_Nhom_14
    ```

2.  **Cấu hình Cơ sở dữ liệu:**
    -   Mở MySQL và tạo một database mới, ví dụ: `language_center_db`.
        ```sql
        CREATE DATABASE language_center_db;
        ```
    -   Mở file `src/main/resources/META-INF/persistence.xml`.
    -   Cập nhật các thuộc tính `jakarta.persistence.jdbc.url`, `jakarta.persistence.jdbc.user`, và `jakarta.persistence.jdbc.password` để khớp với cấu hình MySQL của bạn.

        ```xml
        <properties>
            <!-- ... các thuộc tính khác ... -->
            <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/language_center_db"/>
            <property name="jakarta.persistence.jdbc.user" value="your_username"/>
            <property name="jakarta.persistence.jdbc.password" value="your_password"/>
            <!-- ... -->
        </properties>
        ```
    -   Thuộc tính `hibernate.hbm2ddl.auto` đang được đặt là `update`, nghĩa là Hibernate sẽ tự động tạo/cập nhật các bảng khi ứng dụng khởi động.

3.  **Build Dự Án:**
    Mở Terminal hoặc Command Prompt tại thư mục gốc của dự án và chạy lệnh Maven:
    ```bash
    mvn clean install
    ```
    Lệnh này sẽ tải các dependency cần thiết và biên dịch mã nguồn.

4.  **Chạy Ứng Dụng:**
    Sau khi build thành công, chạy lệnh sau:
    ```bash
    mvn exec:java -Dexec.mainClass="vn.edu.ute.App"
    ```
    Màn hình đăng nhập sẽ xuất hiện.
