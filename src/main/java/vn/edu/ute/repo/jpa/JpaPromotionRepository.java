package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.repo.PromotionRepository;

import java.util.List;

/**
 * Lớp triển khai của PromotionRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Promotion.
 */
public class JpaPromotionRepository implements PromotionRepository {

    /**
     * {@inheritDoc}
     * Sắp xếp theo ID giảm dần để khuyến mãi mới nhất lên đầu.
     */
    @Override
    public List<Promotion> findAll(EntityManager em) {
        return em.createQuery("SELECT p FROM Promotion p ORDER BY p.promotionId DESC", Promotion.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promotion findById(EntityManager em, Long id) {
        return em.find(Promotion.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(EntityManager em, Promotion promotion) {
        em.persist(promotion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(EntityManager em, Promotion promotion) {
        em.merge(promotion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Promotion p = em.find(Promotion.class, id);
        if (p != null) {
            em.remove(p);
        }
    }

    /**
     * {@inheritDoc}
     * Tìm các khuyến mãi đang hoạt động và còn trong thời hạn hiệu lực.
     * Điều kiện lọc bao gồm:
     * - Trạng thái (status) là 'Active'.
     * - Ngày bắt đầu (startDate) phải nhỏ hơn hoặc bằng ngày hiện tại (hoặc null).
     * - Ngày kết thúc (endDate) phải lớn hơn hoặc bằng ngày hiện tại (hoặc null).
     */
    @Override
    public List<Promotion> findActivePromotions(EntityManager em) {
        return em.createQuery(
                "SELECT p FROM Promotion p WHERE p.status = :status " +
                        "AND (p.startDate IS NULL OR p.startDate <= CURRENT_DATE) " +
                        "AND (p.endDate IS NULL OR p.endDate >= CURRENT_DATE) " +
                        "ORDER BY p.promoName",
                Promotion.class)
                .setParameter("status", Promotion.Status.Active)
                .getResultList();
    }
}
