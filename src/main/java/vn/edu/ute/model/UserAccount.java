package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp UserAccount đại diện cho một tài khoản người dùng trong hệ thống.
 * Tài khoản này có thể thuộc về một giáo viên, học viên, hoặc nhân viên.
 * Đây là một entity, được ánh xạ tới bảng 'user_accounts' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "user_accounts", uniqueConstraints = {
        // Đảm bảo tên người dùng (username) là duy nhất.
        @UniqueConstraint(name = "uq_user_accounts_username", columnNames = "username")
})
public class UserAccount {

    // ---- Enums ----

    /**
     * Enum định nghĩa các vai trò người dùng trong hệ thống.
     */
    public enum Role {
        Admin,   // Quản trị viên
        Teacher, // Giáo viên
        Student, // Học viên
        Staff    // Nhân viên
    }

    // ---- Fields ----

    /**
     * ID của tài khoản, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * Tên đăng nhập của người dùng.
     */
    @Column(name = "username", nullable = false, length = 80)
    private String username;

    /**
     * Mật khẩu đã được băm (hashed password) của người dùng.
     * Không bao giờ lưu mật khẩu ở dạng văn bản gốc.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * Vai trò của người dùng trong hệ thống.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Mối quan hệ nhiều-một với Teacher.
     * Nếu tài khoản này thuộc về một giáo viên, trường này sẽ liên kết đến hồ sơ giáo viên đó.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(name = "fk_user_accounts_teacher"))
    private Teacher teacher;

    /**
     * Mối quan hệ nhiều-một với Student.
     * Nếu tài khoản này thuộc về một học viên, trường này sẽ liên kết đến hồ sơ học viên đó.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_user_accounts_student"))
    private Student student;

    /**
     * Mối quan hệ nhiều-một với Staff.
     * Nếu tài khoản này thuộc về một nhân viên, trường này sẽ liên kết đến hồ sơ nhân viên đó.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", foreignKey = @ForeignKey(name = "fk_user_accounts_staff"))
    private Staff staff;

    /**
     * Cờ cho biết tài khoản có đang hoạt động hay không, mặc định là true (hoạt động).
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) NOT NULL DEFAULT 1")
    private Boolean isActive = true;

    /**
     * Thời gian tạo tài khoản, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật thông tin tài khoản lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships (inverse) ----

    /**
     * Mối quan hệ một-nhiều với Notification (một người dùng có thể tạo nhiều thông báo).
     * `mappedBy = "createdByUser"`: Mối quan hệ này được quản lý bởi thuộc tính `createdByUser` trong lớp Notification.
     */
    @OneToMany(mappedBy = "createdByUser")
    private List<Notification> notifications = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public UserAccount() {
    }

    // ---- Getters / Setters ----

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
