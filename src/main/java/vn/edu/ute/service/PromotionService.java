package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.repo.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý khuyến mãi (Promotion).
 * Chứa các logic nghiệp vụ CRUD và tính toán giảm giá.
 */
public class PromotionService {

    private final PromotionRepository promotionRepo;
    private final TransactionManager tx;

    public PromotionService(PromotionRepository promotionRepo, TransactionManager tx) {
        this.promotionRepo = promotionRepo;
        this.tx = tx;
    }

    // ==================== CRUD ====================

    public void createPromotion(Promotion promotion) throws Exception {
        tx.runInTransaction(em -> {
            promotionRepo.save(em, promotion);
            return null;
        });
    }

    public void updatePromotion(Promotion promotion) throws Exception {
        tx.runInTransaction(em -> {
            Promotion existing = promotionRepo.findById(em, promotion.getPromotionId());
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy khuyến mãi với ID: " + promotion.getPromotionId());
            }
            promotionRepo.update(em, promotion);
            return null;
        });
    }

    /**
     * Xóa một chương trình khuyến mãi.
     * Logic nghiệp vụ: Chỉ cho phép xóa nếu khuyến mãi chưa được áp dụng cho bất kỳ hóa đơn nào
     * (trừ các hóa đơn đã bị hủy).
     * @param promotionId ID của khuyến mãi cần xóa.
     * @throws Exception Nếu có lỗi giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy hoặc khuyến mãi đang được sử dụng.
     */
    public void deletePromotion(Long promotionId) throws Exception {
        tx.runInTransaction(em -> {
            Promotion existing = promotionRepo.findById(em, promotionId);
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy khuyến mãi với ID: " + promotionId);
            }
            // Kiểm tra xem khuyến mãi đã được dùng bởi hóa đơn nào chưa (trừ hóa đơn đã hủy)
            boolean hasInvoices = existing.getInvoices().stream()
                    .anyMatch(inv -> inv.getStatus() != vn.edu.ute.model.Invoice.Status.Cancelled);
            if (hasInvoices) {
                throw new IllegalArgumentException(
                        "Không thể xóa khuyến mãi đang được sử dụng trong hóa đơn. Hãy chuyển trạng thái sang 'Inactive'.");
            }
            promotionRepo.delete(em, promotionId);
            return null;
        });
    }

    // ==================== TRUY VẤN ====================

    public List<Promotion> getAllPromotions() throws Exception {
        return tx.runInTransaction(em -> promotionRepo.findAll(em));
    }

    public Promotion getPromotionById(Long id) throws Exception {
        return tx.runInTransaction(em -> promotionRepo.findById(em, id));
    }

    /**
     * Lấy danh sách các khuyến mãi đang hoạt động và còn trong thời hạn hiệu lực.
     */
    public List<Promotion> getActivePromotions() throws Exception {
        return tx.runInTransaction(em -> promotionRepo.findActivePromotions(em));
    }

    // ==================== LOGIC NGHIỆP VỤ ====================

    /**
     * Tính toán số tiền được giảm giá dựa trên loại khuyến mãi.
     *
     * @param originalFee Học phí gốc.
     * @param promotion   Đối tượng khuyến mãi để áp dụng (có thể là null).
     * @return Số tiền được giảm giá. Luôn đảm bảo giá trị trả về >= 0 và <= học phí gốc.
     */
    public BigDecimal calculateDiscount(BigDecimal originalFee, Promotion promotion) {
        if (promotion == null || originalFee == null || originalFee.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (promotion.getDiscountType() == Promotion.DiscountType.Percent) {
            // Trường hợp giảm theo phần trăm: discount = fee * value / 100
            discount = originalFee.multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP); // Làm tròn đến 2 chữ số thập phân
        } else {
            // Trường hợp giảm theo số tiền cố định
            discount = promotion.getDiscountValue();
        }

        // Đảm bảo số tiền giảm giá không lớn hơn học phí gốc.
        if (discount.compareTo(originalFee) > 0) {
            discount = originalFee;
        }
        // Đảm bảo số tiền giảm giá không phải là số âm.
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        return discount;
    }
}
