package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Room đại diện cho một phòng học trong trung tâm.
 * Đây là một entity, được ánh xạ tới bảng 'rooms' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "rooms", uniqueConstraints = {
        // Đảm bảo tên phòng học là duy nhất trong toàn bộ hệ thống.
        @UniqueConstraint(name = "uq_rooms_name", columnNames = "room_name")
})
public class Room {

    // ---- Enums ----

    /**
     * Enum định nghĩa các trạng thái có thể có của một phòng học.
     */
    public enum Status {
        Active, // Phòng đang được sử dụng
        Inactive // Phòng không được sử dụng
    }

    // ---- Fields ----

    /**
     * ID của phòng học, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    /**
     * Tên của phòng học, không được để trống.
     */
    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    /**
     * Sức chứa của phòng học, mặc định là 0.
     */
    @Column(name = "capacity", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer capacity = 0;

    /**
     * Vị trí của phòng học.
     */
    @Column(name = "location", length = 150)
    private String location;

    /**
     * Trạng thái của phòng học (Active hoặc Inactive), mặc định là Active.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') NOT NULL DEFAULT 'Active'")
    private Status status = Status.Active;

    /**
     * Thời gian tạo phòng học, tự động được gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật thông tin phòng học lần cuối, tự động được gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships ----

    /**
     * Mối quan hệ nhiều-một với Branch (nhiều phòng học thuộc về một chi nhánh).
     * FetchType.LAZY: Dữ liệu của Branch chỉ được tải khi có yêu cầu truy cập.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "fk_rooms_branch"))
    private Branch branch;

    /**
     * Mối quan hệ một-nhiều với ClassEntity (một phòng học có thể có nhiều lớp học).
     * `mappedBy = "room"`: Mối quan hệ này được quản lý bởi thuộc tính `room` trong lớp ClassEntity.
     */
    @OneToMany(mappedBy = "room")
    private List<ClassEntity> classes = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Schedule (một phòng học có thể có nhiều lịch học).
     * `mappedBy = "room"`: Mối quan hệ này được quản lý bởi thuộc tính `room` trong lớp Schedule.
     */
    @OneToMany(mappedBy = "room")
    private List<Schedule> schedules = new ArrayList<>();

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Room() {
    }

    // ---- Getters / Setters ----

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public List<ClassEntity> getClasses() { return classes; }
    public void setClasses(List<ClassEntity> classes) { this.classes = classes; }

    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }
}
