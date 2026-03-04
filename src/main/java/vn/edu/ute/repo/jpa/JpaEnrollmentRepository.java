package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.EnrollmentRepository;

import java.util.List;

public class JpaEnrollmentRepository implements EnrollmentRepository {

    @Override
    public List<Enrollment> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.classEntity ORDER BY e.enrollmentDate DESC",
                Enrollment.class)
                .getResultList();
    }

    @Override
    public Enrollment findById(EntityManager em, Long id) {
        return em.createQuery(
                "SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.classEntity WHERE e.enrollmentId = :id",
                Enrollment.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public List<Enrollment> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                "SELECT e FROM Enrollment e JOIN FETCH e.classEntity WHERE e.student.studentId = :sid ORDER BY e.enrollmentDate DESC",
                Enrollment.class)
                .setParameter("sid", studentId)
                .getResultList();
    }

    @Override
    public List<Enrollment> findEnrolledWithoutInvoice(EntityManager em) {
        // Tìm enrollment status = Enrolled mà chưa có invoice nào (LEFT JOIN rồi lọc
        // NULL)
        return em.createQuery(
                "SELECT e FROM Enrollment e " +
                        "JOIN FETCH e.student " +
                        "JOIN FETCH e.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "WHERE e.status = :status " +
                        "AND NOT EXISTS (" +
                        "  SELECT inv FROM Invoice inv " +
                        "  JOIN Payment p ON p.invoice = inv " +
                        "  WHERE p.enrollment = e AND inv.status <> :cancelled" +
                        ")",
                Enrollment.class)
                .setParameter("status", Enrollment.Status.Enrolled)
                .setParameter("cancelled", vn.edu.ute.model.Invoice.Status.Cancelled)
                .getResultList();
    }
}
