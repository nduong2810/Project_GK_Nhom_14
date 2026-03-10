package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Promotion;

import java.util.List;

public interface PromotionRepository {
    List<Promotion> findAll(EntityManager em) throws Exception;

    Promotion findById(EntityManager em, Long id) throws Exception;

    void save(EntityManager em, Promotion promotion) throws Exception;

    void update(EntityManager em, Promotion promotion) throws Exception;

    void delete(EntityManager em, Long id) throws Exception;

    List<Promotion> findActivePromotions(EntityManager em) throws Exception;
}
