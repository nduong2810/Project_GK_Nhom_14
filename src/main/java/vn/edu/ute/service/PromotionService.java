package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.repo.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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

    public void deletePromotion(Long promotionId) throws Exception {
        tx.runInTransaction(em -> {
            Promotion existing = promotionRepo.findById(em, promotionId);
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy khuyến mãi với ID: " + promotionId);
            }
            // Kiểm tra nếu promotion đã được dùng bởi invoice nào
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

    // ==================== QUERY ====================

    public List<Promotion> getAllPromotions() throws Exception {
        return tx.runInTransaction(em -> promotionRepo.findAll(em));
    }

    public Promotion getPromotionById(Long id) throws Exception {
        return tx.runInTransaction(em -> promotionRepo.findById(em, id));
    }

    /**
     * Lấy danh sách khuyến mãi đang Active & trong thời hạn hiệu lực.
     */
    public List<Promotion> getActivePromotions() throws Exception {
        return tx.runInTransaction(em -> promotionRepo.findActivePromotions(em));
    }

    // ==================== BUSINESS LOGIC ====================

    /**
     * Tính số tiền giảm giá dựa trên loại khuyến mãi (dùng Stream-compatible
     * functional style).
     *
     * @param originalFee Học phí gốc
     * @param promotion   Khuyến mãi áp dụng (nullable)
     * @return Số tiền được giảm (luôn >= 0 và <= originalFee)
     */
    public BigDecimal calculateDiscount(BigDecimal originalFee, Promotion promotion) {
        if (promotion == null || originalFee == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (promotion.getDiscountType() == Promotion.DiscountType.Percent) {
            // Giảm theo phần trăm: fee * value / 100
            discount = originalFee.multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // Giảm theo số tiền cố định
            discount = promotion.getDiscountValue();
        }

        // Đảm bảo discount không vượt quá originalFee và không âm
        if (discount.compareTo(originalFee) > 0) {
            discount = originalFee;
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        return discount;
    }
}
