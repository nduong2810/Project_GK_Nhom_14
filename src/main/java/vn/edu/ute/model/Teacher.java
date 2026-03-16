package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Teacher đại diện cho một giáo viên của trung tâm.
 * Đây là một entity, được ánh xạ tới bảng 'teachers' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "teachers", uniqueConstraints = {
        // Đảm bảo email và số điện thoại của giáo viên là duy nhất.
        @UniqueConstraint(name = "uq_teachers_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_teachers_phone", columnNames = "phone")
})
public class Teacher {

    // ---- Enums ----

    /**
     * Enum định nghĩa trạng thái của giáo viên.
     */
    public enum Status {
        Active,   // Đang giảng dạy
        Inactive  // Đã nghỉ dạy
    }

    // ---- Fields ----

    /**
     * ID của giáo viên, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * Họ và tên đầy đủ của giáo viên.
     */
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * Số điện thoại của giáo viên.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Địa chỉ email của giáo viên.
     */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Chuyên môn của giáo viên (ví dụ: "Tiếng Anh giao tiếp", "Luyện thi IELTS").
     */
    @Column(name = "specialty", length = 100)
    private String specialty;

    /**
     * Ngày bắt đầu làm việc tại trung tâm.
     */
    @Column(name = "hire_date")
    private LocalDate hireDate;

    /**
     * Trạng thái của giáo viên, mặc định là 'Active'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') NOT NULL DEFAULT 'Active'")
    private Status status = Status.Active;

    /**
     * Thời gian tạo hồ sơ giáo viên, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật thông tin giáo viên lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships ----

    /**
     * Mối quan hệ một-nhiều với ClassEntity (một giáo viên có thể dạy nhiều lớp học).
     * `mappedBy = "teacher"`: Mối quan hệ này được quản lý bởi thuộc tính `teacher` trong lớp ClassEntity.
     */
    @OneToMany(mappedBy = "teacher")
    private List<ClassEntity> classes = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với UserAccount (một giáo viên có thể có nhiều tài khoản người dùng).
     * `mappedBy = "teacher"`: Mối quan hệ này được quản lý bởi thuộc tính `teacher` trong lớp UserAccount.
     */
    @OneToMany(mappedBy = "teacher")
    private List<UserAccount> userAccounts = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Teacher() {
    }

    // ---- Getters / Setters ----

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
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

    public List<ClassEntity> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassEntity> classes) {
        this.classes = classes;
    }

    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }
}
