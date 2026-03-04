package vn.edu.ute.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "certificates", uniqueConstraints = {
        @UniqueConstraint(name = "uq_cert_serial", columnNames = "serial_no")
})
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cert_student"))
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", foreignKey = @ForeignKey(name = "fk_cert_class"))
    private ClassEntity classEntity;

    @Column(name = "cert_name", nullable = false, length = 150)
    private String certName;

    @Column(name = "issue_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate issueDate;

    @Column(name = "serial_no", length = 80)
    private String serialNo;

    // ---- Constructors ----

    public Certificate() {
    }

    // ---- Getters / Setters ----

    public Long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
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

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
