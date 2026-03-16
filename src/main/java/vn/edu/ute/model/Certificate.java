package vn.edu.ute.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Lớp Certificate đại diện cho một chứng chỉ được cấp cho học viên.
 * Đây là một entity, được ánh xạ tới bảng 'certificates' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "certificates", uniqueConstraints = {
        // Đảm bảo số serial của chứng chỉ là duy nhất.
        @UniqueConstraint(name = "uq_cert_serial", columnNames = "serial_no")
})
public class Certificate {

    // ---- Fields ----

    /**
     * ID của chứng chỉ, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    /**
     * Mối quan hệ nhiều-một với Student. Mỗi chứng chỉ phải được cấp cho một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cert_student"))
    private Student student;

    /**
     * Mối quan hệ nhiều-một với ClassEntity. Chứng chỉ có thể được liên kết với lớp học mà học viên đã hoàn thành.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", foreignKey = @ForeignKey(name = "fk_cert_class"))
    private ClassEntity classEntity;

    /**
     * Tên của chứng chỉ (ví dụ: "Chứng chỉ hoàn thành khóa học Tiếng Anh A2").
     */
    @Column(name = "cert_name", nullable = false, length = 150)
    private String certName;

    /**
     * Ngày cấp chứng chỉ, mặc định là ngày hiện tại.
     */
    @Column(name = "issue_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate issueDate;

    /**
     * Số serial hoặc mã số của chứng chỉ.
     */
    @Column(name = "serial_no", length = 80)
    private String serialNo;

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
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
