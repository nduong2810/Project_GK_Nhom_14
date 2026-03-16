package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Enrollment đại diện cho việc một học viên ghi danh vào một lớp học.
 * Đây là một entity, được ánh xạ tới bảng 'enrollments' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        // Đảm bảo một học viên chỉ có thể ghi danh vào một lớp học một lần duy nhất.
        @UniqueConstraint(name = "uq_enrollments_student_class", columnNames = { "student_id", "class_id" })
}, indexes = {
        // Tạo chỉ mục để tăng tốc độ truy vấn theo học viên và lớp học.
        @Index(name = "idx_enrollments_student", columnList = "student_id"),
        @Index(name = "idx_enrollments_class", columnList = "class_id")
})
public class Enrollment {

    // ---- Enums ----

    /**
     * Enum định nghĩa trạng thái của việc ghi danh.
     */
    public enum Status {
        Enrolled,  // Đã ghi danh và đang học
        Dropped,   // Đã bỏ học
        Completed  // Đã hoàn thành khóa học
    }

    /**
     * Enum định nghĩa kết quả cuối cùng của việc ghi danh.
     */
    public enum EnrollmentResult {
        Pass, // Đậu
        Fail, // Rớt
        NA    // Chưa có kết quả (Not Applicable)
    }

    // ---- Fields ----

    /**
     * ID của bản ghi ghi danh, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    /**
     * Mối quan hệ nhiều-một với Student. Mỗi bản ghi ghi danh phải thuộc về một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollments_student"))
    private Student student;

    /**
     * Mối quan hệ nhiều-một với ClassEntity. Mỗi bản ghi ghi danh phải thuộc về một lớp học.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollments_class"))
    private ClassEntity classEntity;

    /**
     * Ngày học viên ghi danh vào lớp, mặc định là ngày hiện tại.
     */
    @Column(name = "enrollment_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate enrollmentDate;

    /**
     * Trạng thái của việc ghi danh, mặc định là 'Enrolled'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Enrolled','Dropped','Completed') NOT NULL DEFAULT 'Enrolled'")
    private Status status = Status.Enrolled;

    /**
     * Kết quả của việc ghi danh, mặc định là 'NA' (Chưa có).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, columnDefinition = "ENUM('Pass','Fail','NA') NOT NULL DEFAULT 'NA'")
    private EnrollmentResult result = EnrollmentResult.NA;

    /**
     * Thời gian tạo bản ghi ghi danh, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật bản ghi ghi danh lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships (inverse) ----

    /**
     * Mối quan hệ một-nhiều với Payment (một lần ghi danh có thể liên quan đến nhiều lần thanh toán).
     * `mappedBy = "enrollment"`: Mối quan hệ này được quản lý bởi thuộc tính `enrollment` trong lớp Payment.
     */
    @OneToMany(mappedBy = "enrollment")
    private List<Payment> payments = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Enrollment() {
    }

    // ---- Getters / Setters ----

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
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

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public EnrollmentResult getResult() {
        return result;
    }

    public void setResult(EnrollmentResult result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
