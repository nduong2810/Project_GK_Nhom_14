package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Payment;
import vn.edu.ute.repo.PaymentRepository;

import java.util.List;

public class JpaPaymentRepository implements PaymentRepository {

    @Override
    public List<Payment> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT p FROM Payment p JOIN FETCH p.student ORDER BY p.paymentDate DESC", Payment.class)
                .getResultList();
    }

    @Override
    public Payment findById(EntityManager em, Long id) {
        return em.find(Payment.class, id);
    }

    @Override
    public List<Payment> findByInvoiceId(EntityManager em, Long invoiceId) {
        return em.createQuery(
                "SELECT p FROM Payment p WHERE p.invoice.invoiceId = :iid ORDER BY p.paymentDate DESC", Payment.class)
                .setParameter("iid", invoiceId)
                .getResultList();
    }

    @Override
    public List<Payment> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                "SELECT p FROM Payment p WHERE p.student.studentId = :sid ORDER BY p.paymentDate DESC", Payment.class)
                .setParameter("sid", studentId)
                .getResultList();
    }

    @Override
    public void save(EntityManager em, Payment payment) {
        em.persist(payment);
    }
}
