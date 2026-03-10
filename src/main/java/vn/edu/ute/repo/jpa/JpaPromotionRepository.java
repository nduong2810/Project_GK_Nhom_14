package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.repo.PromotionRepository;

import java.util.List;

public class JpaPromotionRepository implements PromotionRepository {

    @Override
    public List<Promotion> findAll(EntityManager em) {
        return em.createQuery("SELECT p FROM Promotion p ORDER BY p.promotionId DESC", Promotion.class)
                .getResultList();
    }

    @Override
    public Promotion findById(EntityManager em, Long id) {
        return em.find(Promotion.class, id);
    }

    @Override
    public void save(EntityManager em, Promotion promotion) {
        em.persist(promotion);
    }

    @Override
    public void update(EntityManager em, Promotion promotion) {
        em.merge(promotion);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Promotion p = em.find(Promotion.class, id);
        if (p != null) {
            em.remove(p);
        }
    }

    /**
     * Tìm các promotion đang Active và trong thời hạn hiệu lực.
     * - status = Active
     * - startDate <= today (hoặc startDate IS NULL)
     * - endDate >= today (hoặc endDate IS NULL)
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
