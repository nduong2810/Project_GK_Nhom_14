package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.repo.PlacementTestRepository;
import java.util.List;

public class JpaPlacementTestRepository implements PlacementTestRepository {

    @Override
    public void save(EntityManager em, PlacementTest test) {
        em.persist(test);
    }

    @Override
    public void update(EntityManager em, PlacementTest test) {
        em.merge(test);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        PlacementTest t = em.find(PlacementTest.class, id);
        if (t != null) {
            em.remove(t);
        }
    }

    @Override
    public PlacementTest findById(EntityManager em, Long id) {
        return em.find(PlacementTest.class, id);
    }

    @Override
    public List<PlacementTest> findAll(EntityManager em) {
        // Dùng JOIN FETCH ngay từ đầu để tránh lỗi Lazy Loading khi vẽ lên giao diện
        return em.createQuery(
                "SELECT p FROM PlacementTest p JOIN FETCH p.student ORDER BY p.testDate DESC",
                PlacementTest.class).getResultList();
    }

    @Override
    public List<PlacementTest> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                        "SELECT p FROM PlacementTest p JOIN FETCH p.student WHERE p.student.studentId = :sid ORDER BY p.testDate DESC",
                        PlacementTest.class)
                .setParameter("sid", studentId)
                .getResultList();
    }
}