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
        // Loại enrollment nếu đã có invoice active (Issued/Paid/Draft) cho enrollment đó.
        // Kiểm tra 2 cách để cover cả hóa đơn cũ (không có enrollment_id) và mới:
        //   C1: inv.enrollment = e  (hóa đơn có enrollment_id trực tiếp)
        //   C2: inv.student = e.student AND có payment liên kết enrollment này
        //       (hóa đơn cũ tạo trước khi có cột enrollment_id)
        return em.createQuery(
                "SELECT e FROM Enrollment e " +
                        "JOIN FETCH e.student " +
                        "JOIN FETCH e.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "WHERE e.status = :status " +
                        "AND NOT EXISTS (" +
                        "  SELECT inv FROM Invoice inv " +
                        "  WHERE inv.status <> :cancelled " +
                        "  AND (" +
                        "    inv.enrollment = e " +
                        "    OR (inv.student = e.student AND EXISTS (" +
                        "          SELECT p FROM Payment p " +
                        "          WHERE p.invoice = inv AND p.enrollment = e" +
                        "        ))" +
                        "  )" +
                        ")",
                Enrollment.class)
                .setParameter("status", Enrollment.Status.Enrolled)
                .setParameter("cancelled", vn.edu.ute.model.Invoice.Status.Cancelled)
                .getResultList();
    }
}
