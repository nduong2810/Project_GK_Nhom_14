package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(name = "uq_enrollments_student_class", columnNames = { "student_id", "class_id" })
}, indexes = {
        @Index(name = "idx_enrollments_student", columnList = "student_id"),
        @Index(name = "idx_enrollments_class", columnList = "class_id")
})
public class Enrollment {

    // ---- Enums ----

    public enum Status {
        Enrolled, Dropped, Completed
    }

    public enum EnrollmentResult {
        Pass, Fail, NA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollments_student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollments_class"))
    private ClassEntity classEntity;

    @Column(name = "enrollment_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Enrolled','Dropped','Completed') NOT NULL DEFAULT 'Enrolled'")
    private Status status = Status.Enrolled;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, columnDefinition = "ENUM('Pass','Fail','NA') NOT NULL DEFAULT 'NA'")
    private EnrollmentResult result = EnrollmentResult.NA;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships (inverse) ----

    @OneToMany(mappedBy = "enrollment")
    private List<Payment> payments = new ArrayList<>();


    // ---- Constructors ----

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
