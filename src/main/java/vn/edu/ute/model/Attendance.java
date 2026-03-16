package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lớp Attendance đại diện cho một bản ghi điểm danh của học viên trong một buổi học.
 * Đây là một entity, được ánh xạ tới bảng 'attendances' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "attendances", uniqueConstraints = {
        // Đảm bảo mỗi học viên chỉ có một bản ghi điểm danh duy nhất cho một lớp vào một ngày cụ thể.
        @UniqueConstraint(name = "uq_attendances", columnNames = { "student_id", "class_id", "attend_date" })
}, indexes = {
        // Tạo chỉ mục để tăng tốc độ truy vấn điểm danh theo lớp và ngày, hoặc theo học viên và ngày.
        @Index(name = "idx_attendances_class_date", columnList = "class_id,attend_date"),
        @Index(name = "idx_attendances_student_date", columnList = "student_id,attend_date")
})
public class Attendance {

    // ---- Enums ----

    /**
     * Enum định nghĩa các trạng thái điểm danh.
     */
    public enum Status {
        Present, // Có mặt
        Absent,  // Vắng mặt
        Late     // Đi trễ
    }

    // ---- Fields ----

    /**
     * ID của bản ghi điểm danh, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    /**
     * Mối quan hệ nhiều-một với Student. Mỗi bản ghi điểm danh phải thuộc về một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendances_student"))
    private Student student;

    /**
     * Mối quan hệ nhiều-một với ClassEntity. Mỗi bản ghi điểm danh phải thuộc về một lớp học.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendances_class"))
    private ClassEntity classEntity;

    /**
     * Ngày điểm danh.
     */
    @Column(name = "attend_date", nullable = false)
    private LocalDate attendDate;

    /**
     * Trạng thái điểm danh, mặc định là 'Present' (Có mặt).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Present','Absent','Late') NOT NULL DEFAULT 'Present'")
    private Status status = Status.Present;

    /**
     * Ghi chú thêm về việc điểm danh (ví dụ: lý do vắng mặt).
     */
    @Column(name = "note", length = 255)
    private String note;

    /**
     * Thời gian tạo bản ghi điểm danh, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Attendance() {
    }

    // ---- Getters / Setters ----

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public LocalDate getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(LocalDate attendDate) {
        this.attendDate = attendDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
