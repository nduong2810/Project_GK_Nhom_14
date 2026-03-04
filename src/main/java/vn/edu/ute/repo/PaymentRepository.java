package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Payment;

import java.util.List;

public interface PaymentRepository {
    List<Payment> findAll(EntityManager em) throws Exception;

    Payment findById(EntityManager em, Long id) throws Exception;

    List<Payment> findByInvoiceId(EntityManager em, Long invoiceId) throws Exception;

    List<Payment> findByStudentId(EntityManager em, Long studentId) throws Exception;

    void save(EntityManager em, Payment payment) throws Exception;
}
