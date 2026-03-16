package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lớp Payment đại diện cho một giao dịch thanh toán được thực hiện bởi học viên.
 * Đây là một entity, được ánh xạ tới bảng 'payments' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "payments", indexes = {
        // Tạo chỉ mục để tăng tốc độ truy vấn theo học viên, ghi danh, hóa đơn và ngày thanh toán.
        @Index(name = "idx_payments_student", columnList = "student_id"),
        @Index(name = "idx_payments_enrollment", columnList = "enrollment_id"),
        @Index(name = "idx_payments_invoice", columnList = "invoice_id"),
        @Index(name = "idx_payments_date", columnList = "payment_date")
})
public class Payment {

    // ---- Enums ----

    /**
     * Enum định nghĩa các phương thức thanh toán.
     */
    public enum PaymentMethod {
        Cash,     // Tiền mặt
        Bank,     // Chuyển khoản ngân hàng
        Momo,     // Ví điện tử Momo
        ZaloPay,  // Ví điện tử ZaloPay
        Card,     // Thẻ tín dụng/ghi nợ
        Other     // Phương thức khác
    }

    /**
     * Enum định nghĩa trạng thái của một giao dịch thanh toán.
     */
    public enum Status {
        Pending,   // Đang chờ xử lý
        Completed, // Đã hoàn thành
        Failed,    // Thất bại
        Refunded   // Đã hoàn tiền
    }

    // ---- Fields ----

    /**
     * ID của giao dịch thanh toán, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    /**
     * Mối quan hệ nhiều-một với Student. Mỗi thanh toán phải thuộc về một học viên.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payments_student"))
    private Student student;

    /**
     * Mối quan hệ nhiều-một với Enrollment. Thanh toán có thể được liên kết với một lần ghi danh.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", foreignKey = @ForeignKey(name = "fk_payments_enrollment"))
    private Enrollment enrollment;

    /**
     * Mối quan hệ nhiều-một với Invoice. Thanh toán có thể được liên kết với một hóa đơn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", foreignKey = @ForeignKey(name = "fk_payments_invoice"))
    private Invoice invoice;

    /**
     * Số tiền được thanh toán.
     */
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /**
     * Ngày và giờ thực hiện thanh toán, mặc định là thời điểm hiện tại.
     */
    @Column(name = "payment_date", nullable = false, columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paymentDate;

    /**
     * Phương thức thanh toán được sử dụng, mặc định là 'Cash'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, columnDefinition = "ENUM('Cash','Bank','Momo','ZaloPay','Card','Other') NOT NULL DEFAULT 'Cash'")
    private PaymentMethod paymentMethod = PaymentMethod.Cash;

    /**
     * Trạng thái của thanh toán, mặc định là 'Completed'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Pending','Completed','Failed','Refunded') NOT NULL DEFAULT 'Completed'")
    private Status status = Status.Completed;

    /**
     * Mã tham chiếu cho giao dịch (ví dụ: mã giao dịch ngân hàng).
     */
    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    /**
     * Thời gian tạo bản ghi thanh toán, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Payment() {
    }

    // ---- Getters / Setters ----

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
