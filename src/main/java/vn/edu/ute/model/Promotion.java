package vn.edu.ute.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Promotion đại diện cho một chương trình khuyến mãi của trung tâm.
 * Đây là một entity, được ánh xạ tới bảng 'promotions' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "promotions")
public class Promotion {

    // ---- Enums ----

    /**
     * Enum định nghĩa loại giảm giá của khuyến mãi.
     */
    public enum DiscountType {
        Percent, // Giảm giá theo phần trăm
        Amount   // Giảm giá một số tiền cụ thể
    }

    /**
     * Enum định nghĩa trạng thái của chương trình khuyến mãi.
     */
    public enum Status {
        Active,   // Đang diễn ra
        Inactive  // Đã kết thúc hoặc bị hủy
    }

    // ---- Fields ----

    /**
     * ID của chương trình khuyến mãi, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;

    /**
     * Tên của chương trình khuyến mãi.
     */
    @Column(name = "promo_name", nullable = false, length = 150)
    private String promoName;

    /**
     * Loại giảm giá (phần trăm hoặc số tiền), mặc định là 'Percent'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, columnDefinition = "ENUM('Percent','Amount') NOT NULL DEFAULT 'Percent'")
    private DiscountType discountType = DiscountType.Percent;

    /**
     * Giá trị giảm giá, mặc định là 0.
     * Nếu `discountType` là 'Percent', giá trị này là phần trăm giảm.
     * Nếu `discountType` là 'Amount', giá trị này là số tiền giảm.
     */
    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) NOT NULL DEFAULT 0.00")
    private BigDecimal discountValue = BigDecimal.ZERO;

    /**
     * Ngày bắt đầu chương trình khuyến mãi.
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * Ngày kết thúc chương trình khuyến mãi.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Trạng thái của chương trình khuyến mãi, mặc định là 'Active'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Active','Inactive') NOT NULL DEFAULT 'Active'")
    private Status status = Status.Active;

    // ---- Relationships ----

    /**
     * Mối quan hệ một-nhiều với Invoice (một chương trình khuyến mãi có thể được áp dụng cho nhiều hóa đơn).
     * `mappedBy = "promotion"`: Mối quan hệ này được quản lý bởi thuộc tính `promotion` trong lớp Invoice.
     */
    @OneToMany(mappedBy = "promotion")
    private List<Invoice> invoices = new ArrayList<>();

    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public Promotion() {
    }

    // ---- Getters / Setters ----

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
