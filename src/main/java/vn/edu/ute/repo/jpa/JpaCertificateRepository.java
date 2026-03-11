package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.repo.CertificateRepository;
import java.util.List;

public class JpaCertificateRepository implements CertificateRepository {

    @Override
    public void save(EntityManager em, Certificate certificate) {
        em.persist(certificate);
    }

    @Override
    public void update(EntityManager em, Certificate certificate) {
        em.merge(certificate);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Certificate c = em.find(Certificate.class, id);
        if (c != null) {
            em.remove(c);
        }
    }

    @Override
    public Certificate findById(EntityManager em, Long id) {
        return em.find(Certificate.class, id);
    }

    @Override
    public List<Certificate> findAll(EntityManager em) {
        // Dùng LEFT JOIN FETCH cho classEntity vì Học viên có thể có chứng chỉ mà không cần thuộc lớp cụ thể nào
        String jpql = "SELECT c FROM Certificate c " +
                "JOIN FETCH c.student " +
                "LEFT JOIN FETCH c.classEntity ce " +
                "LEFT JOIN FETCH ce.course " +
                "ORDER BY c.issueDate DESC";
        return em.createQuery(jpql, Certificate.class).getResultList();
    }

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