package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.repo.PlacementTestRepository;
import java.util.List;

/**
 * Lớp triển khai của PlacementTestRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng PlacementTest.
 */
public class JpaPlacementTestRepository implements PlacementTestRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(EntityManager em, PlacementTest test) {
        em.persist(test);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(EntityManager em, PlacementTest test) {
        em.merge(test);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(EntityManager em, Long id) {
        PlacementTest t = em.find(PlacementTest.class, id);
        if (t != null) {
            em.remove(t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlacementTest findById(EntityManager em, Long id) {
        return em.find(PlacementTest.class, id);
    }

    /**
     * {@inheritDoc}
     * Sử dụng `JOIN FETCH` để tải sẵn thông tin học viên, tránh lỗi Lazy Loading
     * khi hiển thị dữ liệu lên giao diện.
     */
    @Override
    public List<PlacementTest> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT p FROM PlacementTest p JOIN FETCH p.student ORDER BY p.testDate DESC",
                PlacementTest.class).getResultList();
    }

    /**
     * {@inheritDoc}
     * Lọc theo `studentId` và tải sẵn thông tin học viên.
     */
    @Override
    public List<PlacementTest> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                        "SELECT p FROM PlacementTest p JOIN FETCH p.student WHERE p.student.studentId = :sid ORDER BY p.testDate DESC",
                        PlacementTest.class)
                .setParameter("sid", studentId)
                .getResultList();
    }
}
