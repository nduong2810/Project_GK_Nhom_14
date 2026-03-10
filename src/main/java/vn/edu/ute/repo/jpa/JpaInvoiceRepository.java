package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Invoice;
import vn.edu.ute.repo.InvoiceRepository;

import java.util.List;

public class JpaInvoiceRepository implements InvoiceRepository {

    @Override
    public List<Invoice> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT i FROM Invoice i JOIN FETCH i.student LEFT JOIN FETCH i.promotion ORDER BY i.issueDate DESC",
                Invoice.class)
                .getResultList();
    }

    @Override
    public Invoice findById(EntityManager em, Long id) {
        return em.find(Invoice.class, id);
    }

    @Override
    public List<Invoice> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                "SELECT i FROM Invoice i JOIN FETCH i.student WHERE i.student.studentId = :sid ORDER BY i.issueDate DESC",
                Invoice.class)
                .setParameter("sid", studentId)
                .getResultList();
    }

    @Override
    public void save(EntityManager em, Invoice invoice) {
        em.persist(invoice);
    }

    @Override
    public void update(EntityManager em, Invoice invoice) {
        em.merge(invoice);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Invoice inv = em.find(Invoice.class, id);
        if (inv != null) {
            em.remove(inv);
        }
    }
}
