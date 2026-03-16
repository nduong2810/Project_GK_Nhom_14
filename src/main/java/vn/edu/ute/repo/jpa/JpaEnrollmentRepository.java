package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.EnrollmentRepository;

import java.util.List;

/**
 * Lớp triển khai của EnrollmentRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Enrollment.
 */
public class JpaEnrollmentRepository implements EnrollmentRepository {

    /**
     * {@inheritDoc}
     * Tải sẵn thông tin học viên, lớp học và khóa học để tối ưu hóa.
     */
    @Override
    public List<Enrollment> findAll(EntityManager em) {
        return em.createQuery(
                        "SELECT e FROM Enrollment e " +
                                "JOIN FETCH e.student " +
                                "JOIN FETCH e.classEntity ce " +
                                "JOIN FETCH ce.course " +
                                "ORDER BY e.enrollmentDate DESC",
                        Enrollment.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Tải sẵn thông tin liên quan khi tìm theo ID.
     */
    @Override
    public Enrollment findById(EntityManager em, Long id) {
        try {
            return em.createQuery(
                            "SELECT e FROM Enrollment e " +
                                    "JOIN FETCH e.student " +
                                    "JOIN FETCH e.classEntity ce " +
                                    "JOIN FETCH ce.course " +
                                    "WHERE e.enrollmentId = :id",
                            Enrollment.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null; // Trả về null nếu không tìm thấy
        }
    }

    /**
     * {@inheritDoc}
     * Lọc theo `studentId` và tải sẵn thông tin lớp, khóa học.
     */
    @Override
    public List<Enrollment> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                        "SELECT e FROM Enrollment e " +
                                "JOIN FETCH e.classEntity ce " +
                                "JOIN FETCH ce.course " +
                                "WHERE e.student.studentId = :sid " +
                                "ORDER BY e.enrollmentDate DESC",
                        Enrollment.class)
                .setParameter("sid", studentId)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Tìm các bản ghi ghi danh đang ở trạng thái 'Enrolled' và chưa có hóa đơn nào đang hoạt động
     * (Issued, Paid, Draft) được liên kết với nó.
     * Câu truy vấn con `NOT EXISTS` được sử dụng để kiểm tra sự tồn tại của hóa đơn.
     */
    @Override
    public List<Enrollment> findEnrolledWithoutInvoice(EntityManager em) {
        return em.createQuery(
                "SELECT e FROM Enrollment e " +
                        "JOIN FETCH e.student " +
                        "JOIN FETCH e.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "WHERE e.status = :status " +
                        "AND NOT EXISTS (" +
                        "  SELECT inv FROM Invoice inv " +
                        "  WHERE inv.enrollment = e AND inv.status <> :cancelled" +
                        ")",
                Enrollment.class)
                .setParameter("status", Enrollment.Status.Enrolled)
                .setParameter("cancelled", vn.edu.ute.model.Invoice.Status.Cancelled)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(EntityManager em, Enrollment enrollment) {
        em.persist(enrollment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(EntityManager em, Enrollment enrollment) {
        em.merge(enrollment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Enrollment e = em.find(Enrollment.class, id);
        if (e != null) {
            em.remove(e);
        }
    }
}
