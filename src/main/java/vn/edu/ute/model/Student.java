package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Student đại diện cho một học viên của trung tâm.
 * Đây là một entity, được ánh xạ tới bảng 'students' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "students", uniqueConstraints = {
        // Đảm bảo email và số điện thoại của học viên là duy nhất.
        @UniqueConstraint(name = "uq_students_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_students_phone", columnNames = "phone")
})
public class Student {

    // ---- Enums ----

    /**
     * Enum định nghĩa giới tính của học viên.
     */
    public enum Gender {
        Male,   // Nam
        Female, // Nữ
        Other   // Khác
    }

    /**
     * Enum định nghĩa trạng thái của học viên.
     */
    public enum Status {
        Active,   // Đang theo học
        Inactive  // Đã nghỉ học
    }

    // ---- Fields ----

    /**
     * ID của học viên, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    /**
     * Họ và tên đầy đủ của học viên.
     */
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * Ngày sinh của học viên.
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Giới tính của học viên.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    /**
     * Số điện thoại của học viên.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Địa chỉ email của học viên.
     */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Địa chỉ liên lạc của học viên.
     */
    @Column(name = "address", length = 255)
    private String address;

    /**
     * Ngày đăng ký học lần đầu, mặc định là ngày hiện tại.
     */
    @Column(name = "registration_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate registrationDate;

    /**
     * Trạng thái của học viên, mặc định là 'Active'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') NOT NULL DEFAULT 'Active'")
    private Status status = Status.Active;

    /**
     * Thời gian tạo hồ sơ học viên, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật thông tin học viên lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships ----

    /**
     * Mối quan hệ một-nhiều với Enrollment (một học viên có thể có nhiều lần ghi danh).
     */
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Invoice (một học viên có thể có nhiều hóa đơn).
     */
    @OneToMany(mappedBy = "student")
    private List<Invoice> invoices = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Payment (một học viên có thể có nhiều lần thanh toán).
     */
    @OneToMany(mappedBy = "student")
    private List<Payment> payments = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Attendance (một học viên có nhiều bản ghi điểm danh).
     */
    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Result (một học viên có nhiều kết quả học tập).
     */
    @OneToMany(mappedBy = "student")
    private List<Result> results = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với PlacementTest (một học viên có thể làm nhiều bài kiểm tra đầu vào).
     * `cascade = CascadeType.ALL, orphanRemoval = true`: Khi xóa học viên, các bài kiểm tra liên quan cũng sẽ bị xóa.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacementTest> placementTests = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Certificate (một học viên có thể nhận nhiều chứng chỉ).
     * `cascade = CascadeType.ALL, orphanRemoval = true`: Khi xóa học viên, các chứng chỉ liên quan cũng sẽ bị xóa.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với UserAccount (một học viên có thể có nhiều tài khoản người dùng).
     */
    @OneToMany(mappedBy = "student")
    private List<UserAccount> userAccounts = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Student() {
    }

    // ---- Getters / Setters ----

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<PlacementTest> getPlacementTests() {
        return placementTests;
    }

    public void setPlacementTests(List<PlacementTest> placementTests) {
        this.placementTests = placementTests;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }
}
