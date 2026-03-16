package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.repo.CertificateRepository;
import java.util.List;

/**
 * Lớp triển khai của CertificateRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Certificate.
 */
public class JpaCertificateRepository implements CertificateRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(EntityManager em, Certificate certificate) {
        em.persist(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(EntityManager em, Certificate certificate) {
        em.merge(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Certificate c = em.find(Certificate.class, id);
        if (c != null) {
            em.remove(c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Certificate findById(EntityManager em, Long id) {
        return em.find(Certificate.class, id);
    }

    /**
     * {@inheritDoc}
     * Tải sẵn thông tin học viên, lớp học và khóa học.
     * Sử dụng `LEFT JOIN FETCH` cho `classEntity` và `course` vì một chứng chỉ có thể không liên quan đến một lớp học cụ thể.
     */
    @Override
    public List<Certificate> findAll(EntityManager em) {
        String jpql = "SELECT c FROM Certificate c " +
                "JOIN FETCH c.student " +
                "LEFT JOIN FETCH c.classEntity ce " +
                "LEFT JOIN FETCH ce.course " +
                "ORDER BY c.issueDate DESC";
        return em.createQuery(jpql, Certificate.class).getResultList();
    }

    /**
     * {@inheritDoc}
     * Lọc theo `studentId` và tải sẵn các thông tin liên quan.
     */
    @Override
    public List<Certificate> findByStudentId(EntityManager em, Long studentId) {
        String jpql = "SELECT c FROM Certificate c " +
                "JOIN FETCH c.student " +
                "LEFT JOIN FETCH c.classEntity ce " +
                "LEFT JOIN FETCH ce.course " +
                "WHERE c.student.studentId = :sid " +
                "ORDER BY c.issueDate DESC";
        return em.createQuery(jpql, Certificate.class)
                .setParameter("sid", studentId)
                .getResultList();
    }
}
