package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Branch đại diện cho một chi nhánh của trung tâm.
 * Đây là một entity, được ánh xạ tới bảng 'branches' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "branches", uniqueConstraints = {
        // Đảm bảo tên chi nhánh là duy nhất.
        @UniqueConstraint(name = "uq_branches_name", columnNames = "branch_name")
})
public class Branch {

    // ---- Enums ----

    /**
     * Enum định nghĩa các trạng thái có thể có của một chi nhánh.
     */
    public enum Status {
        Active,   // Chi nhánh đang hoạt động
        Inactive  // Chi nhánh đã đóng cửa
    }

    // ---- Fields ----

    /**
     * ID của chi nhánh, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long branchId;

    /**
     * Tên của chi nhánh, không được để trống.
     */
    @Column(name = "branch_name", nullable = false, length = 150)
    private String branchName;

    /**
     * Địa chỉ của chi nhánh.
     */
    @Column(name = "address", length = 255)
    private String address;

    /**
     * Số điện thoại liên hệ của chi nhánh.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Trạng thái của chi nhánh, mặc định là 'Active'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') NOT NULL DEFAULT 'Active'")
    private Status status = Status.Active;

    /**
     * Thời gian tạo chi nhánh, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ---- Relationships ----

    /**
     * Mối quan hệ một-nhiều với Room (một chi nhánh có nhiều phòng học).
     * `mappedBy = "branch"`: Mối quan hệ này được quản lý bởi thuộc tính `branch` trong lớp Room.
     */
    @OneToMany(mappedBy = "branch")
    private List<Room> rooms = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với ClassEntity (một chi nhánh có nhiều lớp học).
     * `mappedBy = "branch"`: Mối quan hệ này được quản lý bởi thuộc tính `branch` trong lớp ClassEntity.
     */
    @OneToMany(mappedBy = "branch")
    private List<ClassEntity> classes = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Branch() {
    }

    // ---- Getters / Setters ----

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<ClassEntity> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassEntity> classes) {
        this.classes = classes;
    }
}
