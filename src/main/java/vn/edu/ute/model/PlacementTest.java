package vn.edu.ute.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "placement_tests")
public class PlacementTest {

    // ---- Enums ----

    public enum SuggestedLevel {
        Beginner, Intermediate, Advanced
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_placement_student"))
    private Student student;

    @Column(name = "test_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate testDate;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(name = "suggested_level")
    private SuggestedLevel suggestedLevel;

    @Column(name = "note", length = 255)
    private String note;

    // ---- Constructors ----

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
