package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Course đại diện cho một khóa học được cung cấp bởi trung tâm.
 * Đây là một entity, được ánh xạ tới bảng 'courses' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "courses")
public class Course {

    // ---- Enums ----

    /**
     * Enum định nghĩa các cấp độ của khóa học.
     */
    public enum Level {
        Beginner,     // Sơ cấp
        Intermediate, // Trung cấp
        Advanced      // Cao cấp
    }

    /**
     * Enum định nghĩa đơn vị thời gian cho thời lượng khóa học.
     */
    public enum DurationUnit {
        Hour, // Giờ
        Week  // Tuần
    }

    /**
     * Enum định nghĩa trạng thái của khóa học.
     */
    public enum Status {
        Active,   // Đang mở
        Inactive  // Đã đóng
    }

    // ---- Fields ----

    /**
     * ID của khóa học, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    /**
     * Tên của khóa học.
     */
    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    /**
     * Mô tả chi tiết về khóa học.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Cấp độ của khóa học (Beginner, Intermediate, Advanced).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private Level level;

    /**
     * Thời lượng của khóa học (một con số).
     */
    @Column(name = "duration")
    private Integer duration;

    /**
     * Đơn vị thời gian cho thời lượng (Hour hoặc Week), mặc định là Week.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", columnDefinition = "ENUM('Hour','Week') DEFAULT 'Week'")
    private DurationUnit durationUnit = DurationUnit.Week;

    /**
     * Học phí của khóa học, mặc định là 0.
     */
    @Column(name = "fee", nullable = false, precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) NOT NULL DEFAULT 0.00")
    private BigDecimal fee = BigDecimal.ZERO;

    /**
     * Trạng thái của khóa học, mặc định là 'Active'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') NOT NULL DEFAULT 'Active'")
    private Status status = Status.Active;

    /**
     * Thời gian tạo khóa học, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật thông tin khóa học lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships ----

    /**
     * Mối quan hệ một-nhiều với ClassEntity (một khóa học có thể có nhiều lớp học).
     * `mappedBy = "course"`: Mối quan hệ này được quản lý bởi thuộc tính `course` trong lớp ClassEntity.
     */
    @OneToMany(mappedBy = "course")
    private List<ClassEntity> classes = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Course() {
    }

    // ---- Getters / Setters ----

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
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
}
