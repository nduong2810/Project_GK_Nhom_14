package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Payment;
import vn.edu.ute.repo.PaymentRepository;

import java.util.List;

/**
 * Lớp triển khai của PaymentRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Payment.
 */
public class JpaPaymentRepository implements PaymentRepository {

    /**
     * {@inheritDoc}
     * Tải sẵn thông tin học viên và sắp xếp theo ngày thanh toán mới nhất.
     */
    @Override
    public List<Payment> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT p FROM Payment p JOIN FETCH p.student ORDER BY p.paymentDate DESC", Payment.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `find` của EntityManager.
     */
    @Override
    public Payment findById(EntityManager em, Long id) {
        return em.find(Payment.class, id);
    }

    /**
     * {@inheritDoc}
     * Lọc các thanh toán theo `invoiceId`.
     */
    @Override
    public List<Payment> findByInvoiceId(EntityManager em, Long invoiceId) {
        return em.createQuery(
                "SELECT p FROM Payment p WHERE p.invoice.invoiceId = :iid ORDER BY p.paymentDate DESC", Payment.class)
                .setParameter("iid", invoiceId)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Lọc các thanh toán theo `studentId`.
     */
    @Override
    public List<Payment> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                "SELECT p FROM Payment p WHERE p.student.studentId = :sid ORDER BY p.paymentDate DESC", Payment.class)
                .setParameter("sid", studentId)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `persist` của EntityManager.
     */
    @Override
    public void save(EntityManager em, Payment payment) {
        em.persist(payment);
    }
}
