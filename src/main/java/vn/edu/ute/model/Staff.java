package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Staff đại diện cho một nhân viên trong trung tâm.
 * Đây là một entity, được ánh xạ tới bảng 'staffs' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "staffs", uniqueConstraints = {
        // Đảm bảo email và số điện thoại của nhân viên là duy nhất.
        @UniqueConstraint(name = "uq_staffs_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_staffs_phone", columnNames = "phone")
})
public class Staff {

    // ---- Enums ----

    /**
     * Enum định nghĩa các vai trò có thể có của một nhân viên.
     */
    public enum Role {
        Admin,      // Quản trị viên hệ thống
        Consultant, // Nhân viên tư vấn
        Accountant, // Kế toán
        Manager,    // Quản lý
        Other       // Vai trò khác
    }

    /**
     * Enum định nghĩa trạng thái của nhân viên.
     */
    public enum Status {
        Active,   // Đang làm việc
        Inactive  // Đã nghỉ việc
    }

    // ---- Fields ----

    /**
     * ID của nhân viên, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long staffId;

    /**
     * Họ và tên đầy đủ của nhân viên.
     */
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    /**
     * Vai trò của nhân viên, mặc định là 'Other'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('Admin','Consultant','Accountant','Manager','Other') NOT NULL DEFAULT 'Other'")
    private Role role = Role.Other;

    /**
     * Số điện thoại của nhân viên.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Địa chỉ email của nhân viên.
     */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Trạng thái của nhân viên, mặc định là 'Active'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') NOT NULL DEFAULT 'Active'")
    private Status status = Status.Active;

    /**
     * Thời gian tạo hồ sơ nhân viên, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật thông tin nhân viên lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships ----

    /**
     * Mối quan hệ một-nhiều với UserAccount (một nhân viên có thể có nhiều tài khoản người dùng).
     * `mappedBy = "staff"`: Mối quan hệ này được quản lý bởi thuộc tính `staff` trong lớp UserAccount.
     */
    @OneToMany(mappedBy = "staff")
    private List<UserAccount> userAccounts = new ArrayList<>();

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Staff() {
    }

    // ---- Getters / Setters ----

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }
}
