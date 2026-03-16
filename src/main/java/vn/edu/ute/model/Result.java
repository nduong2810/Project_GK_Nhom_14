package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lớp Result đại diện cho kết quả học tập của một học viên trong một lớp học cụ thể.
 * Đây là một entity, được ánh xạ tới bảng 'results' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "results", uniqueConstraints = {
        // Đảm bảo rằng mỗi học viên chỉ có một kết quả duy nhất cho một lớp học.
        @UniqueConstraint(name = "uq_results", columnNames = { "student_id", "class_id" })
}, indexes = {
        // Tạo chỉ mục trên cột class_id để tăng tốc độ truy vấn theo lớp học.
        @Index(name = "idx_results_class", columnList = "class_id")
})
public class Result {

    // ---- Fields ----

    /**
     * ID của kết quả, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    /**
     * Mối quan hệ nhiều-một với Student.
     * Mỗi kết quả thuộc về một học viên.
     * `optional = false`: Một kết quả phải luôn được liên kết với một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_results_student"))
    private Student student;

    /**
     * Mối quan hệ nhiều-một với ClassEntity.
     * Mỗi kết quả thuộc về một lớp học.
     * `optional = false`: Một kết quả phải luôn được liên kết với một lớp học.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_results_class"))
    private ClassEntity classEntity;

    /**
     * Điểm số của học viên.
     * `precision = 5, scale = 2`: Tổng cộng 5 chữ số, với 2 chữ số sau dấu thập phân (ví dụ: 100.00).
     */
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    /**
     * Xếp loại dựa trên điểm số (ví dụ: A, B, C, "Giỏi", "Khá").
     */
    @Column(name = "grade", length = 10)
    private String grade;

    /**
     * Nhận xét của giáo viên về kết quả học tập.
     */
    @Column(name = "comment", length = 255)
    private String comment;

    /**
     * Thời gian tạo bản ghi kết quả, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật bản ghi kết quả lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Result() {
    }

    // ---- Getters / Setters ----

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
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

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
