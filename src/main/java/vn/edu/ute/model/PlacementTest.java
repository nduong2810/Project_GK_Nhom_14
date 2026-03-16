package vn.edu.ute.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lớp PlacementTest đại diện cho một bài kiểm tra đầu vào của học viên.
 * Kết quả bài kiểm tra này giúp tư vấn khóa học phù hợp cho học viên.
 * Đây là một entity, được ánh xạ tới bảng 'placement_tests' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "placement_tests")
public class PlacementTest {

    // ---- Enums ----

    /**
     * Enum định nghĩa các cấp độ được đề xuất sau bài kiểm tra.
     */
    public enum SuggestedLevel {
        Beginner,     // Sơ cấp
        Intermediate, // Trung cấp
        Advanced      // Cao cấp
    }

    // ---- Fields ----

    /**
     * ID của bài kiểm tra, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    /**
     * Mối quan hệ nhiều-một với Student. Mỗi bài kiểm tra phải thuộc về một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_placement_student"))
    private Student student;

    /**
     * Ngày học viên làm bài kiểm tra, mặc định là ngày hiện tại.
     */
    @Column(name = "test_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate testDate;

    /**
     * Điểm số của bài kiểm tra.
     * `precision = 5, scale = 2`: Tổng cộng 5 chữ số, với 2 chữ số sau dấu thập phân (ví dụ: 100.00).
     */
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    /**
     * Cấp độ được đề xuất dựa trên điểm số của bài kiểm tra.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "suggested_level")
    private SuggestedLevel suggestedLevel;

    /**
     * Ghi chú của người chấm bài hoặc nhân viên tư vấn.
     */
    @Column(name = "note", length = 255)
    private String note;

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public PlacementTest() {
    }

    // ---- Getters / Setters ----

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public SuggestedLevel getSuggestedLevel() {
        return suggestedLevel;
    }

    public void setSuggestedLevel(SuggestedLevel suggestedLevel) {
        this.suggestedLevel = suggestedLevel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
