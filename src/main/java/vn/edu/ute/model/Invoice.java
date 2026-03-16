package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Invoice đại diện cho một hóa đơn tài chính được tạo cho học viên.
 * Đây là một entity, được ánh xạ tới bảng 'invoices' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "invoices", indexes = {
        // Tạo chỉ mục để tăng tốc độ truy vấn hóa đơn theo học viên và ngày phát hành.
        @Index(name = "idx_invoices_student", columnList = "student_id"),
        @Index(name = "idx_invoices_issue", columnList = "issue_date")
})
public class Invoice {

    // ---- Enums ----

    /**
     * Enum định nghĩa các trạng thái có thể có của một hóa đơn.
     */
    public enum Status {
        Draft,      // Hóa đơn nháp, chưa chính thức
        Issued,     // Hóa đơn đã được phát hành
        Paid,       // Hóa đơn đã được thanh toán đầy đủ
        Cancelled   // Hóa đơn đã bị hủy
    }

    // ---- Fields ----

    /**
     * ID của hóa đơn, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    /**
     * Mối quan hệ nhiều-một với Student. Mỗi hóa đơn phải thuộc về một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invoices_student"))
    private Student student;

    /**
     * Mối quan hệ nhiều-một với Enrollment. Hóa đơn có thể được liên kết với một lần ghi danh cụ thể.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", foreignKey = @ForeignKey(name = "fk_invoices_enrollment"))
    private Enrollment enrollment;

    /**
     * Tổng số tiền của hóa đơn, mặc định là 0.
     */
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) NOT NULL DEFAULT 0.00")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * Ngày phát hành hóa đơn, mặc định là ngày hiện tại.
     */
    @Column(name = "issue_date", nullable = false, columnDefinition = "DATE NOT NULL DEFAULT (CURRENT_DATE)")
    private LocalDate issueDate;

    /**
     * Trạng thái của hóa đơn, mặc định là 'Issued'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Draft','Issued','Paid','Cancelled') NOT NULL DEFAULT 'Issued'")
    private Status status = Status.Issued;

    /**
     * Ghi chú thêm cho hóa đơn.
     */
    @Column(name = "note", length = 255)
    private String note;

    /**
     * Mối quan hệ nhiều-một với Promotion. Một chương trình khuyến mãi có thể được áp dụng cho hóa đơn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", foreignKey = @ForeignKey(name = "fk_invoices_promotion"))
    private Promotion promotion;

    /**
     * Thời gian tạo hóa đơn, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật hóa đơn lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships (inverse) ----

    /**
     * Mối quan hệ một-nhiều với Payment (một hóa đơn có thể có nhiều lần thanh toán).
     * `mappedBy = "invoice"`: Mối quan hệ này được quản lý bởi thuộc tính `invoice` trong lớp Payment.
     */
    @OneToMany(mappedBy = "invoice")
    private List<Payment> payments = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Invoice() {
    }

    // ---- Getters / Setters ----

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
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

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
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
