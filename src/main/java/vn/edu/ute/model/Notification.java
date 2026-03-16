package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Lớp Notification đại diện cho một thông báo được gửi trong hệ thống.
 * Đây là một entity, được ánh xạ tới bảng 'notifications' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    // ---- Enums ----

    /**
     * Enum định nghĩa đối tượng nhận thông báo.
     */
    public enum TargetRole {
        All,     // Tất cả mọi người
        Student, // Chỉ học viên
        Teacher, // Chỉ giáo viên
        Staff    // Chỉ nhân viên
    }

    // ---- Fields ----

    /**
     * ID của thông báo, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    /**
     * Tiêu đề của thông báo.
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * Nội dung chi tiết của thông báo.
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Vai trò của đối tượng nhận thông báo, mặc định là 'All' (tất cả).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", nullable = false, columnDefinition = "ENUM('All','Student','Teacher','Staff') NOT NULL DEFAULT 'All'")
    private TargetRole targetRole = TargetRole.All;

    /**
     * Mối quan hệ nhiều-một với UserAccount.
     * Liên kết đến tài khoản người dùng đã tạo thông báo này.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user", foreignKey = @ForeignKey(name = "fk_notifications_user"))
    private UserAccount createdByUser;

    /**
     * Thời gian tạo thông báo, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Notification() {
    }

    // ---- Getters / Setters ----

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TargetRole getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(TargetRole targetRole) {
        this.targetRole = targetRole;
    }

    public UserAccount getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(UserAccount createdByUser) {
        this.createdByUser = createdByUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
