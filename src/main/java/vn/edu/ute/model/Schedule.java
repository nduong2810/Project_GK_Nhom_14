package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Lớp Schedule đại diện cho một buổi học cụ thể trong lịch học của một lớp.
 * Đây là một entity, được ánh xạ tới bảng 'schedules' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "schedules", uniqueConstraints = {
        // Đảm bảo không có hai lịch học trùng nhau cho cùng một lớp vào cùng một thời điểm.
        @UniqueConstraint(name = "uq_schedules_class_time", columnNames = { "class_id", "study_date", "start_time",
                "end_time" })
}, indexes = {
        // Tạo chỉ mục để tăng tốc độ truy vấn lịch học theo lớp và ngày học.
        @Index(name = "idx_schedules_class_date", columnList = "class_id,study_date")
})
public class Schedule {

    // ---- Fields ----

    /**
     * ID của lịch học, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    /**
     * Mối quan hệ nhiều-một với ClassEntity. Mỗi lịch học phải thuộc về một lớp học.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedules_class"))
    private ClassEntity classEntity;

    /**
     * Ngày học.
     */
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    /**
     * Thời gian bắt đầu buổi học.
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * Thời gian kết thúc buổi học.
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * Mối quan hệ nhiều-một với Room. Buổi học diễn ra tại một phòng học cụ thể.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_schedules_room"))
    private Room room;

    /**
     * Thời gian tạo bản ghi lịch học, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Schedule() {
    }

    // ---- Getters / Setters ----

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public LocalDate getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(LocalDate studyDate) {
        this.studyDate = studyDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
