package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Invoice;

import java.util.List;

public interface InvoiceRepository {
    List<Invoice> findAll(EntityManager em) throws Exception;

    Invoice findById(EntityManager em, Long id) throws Exception;

    List<Invoice> findByStudentId(EntityManager em, Long studentId) throws Exception;

    void save(EntityManager em, Invoice invoice) throws Exception;

    void update(EntityManager em, Invoice invoice) throws Exception;

    void delete(EntityManager em, Long id) throws Exception;
}
