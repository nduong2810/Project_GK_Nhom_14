package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Invoice;
import vn.edu.ute.repo.InvoiceRepository;

import java.util.List;

/**
 * Lớp triển khai của InvoiceRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Invoice.
 */
public class JpaInvoiceRepository implements InvoiceRepository {

    /**
     * {@inheritDoc}
     * Sử dụng JOIN FETCH để tải sẵn thông tin học viên và khuyến mãi (nếu có).
     * Sắp xếp theo ngày phát hành mới nhất.
     */
    @Override
    public List<Invoice> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT i FROM Invoice i JOIN FETCH i.student LEFT JOIN FETCH i.promotion ORDER BY i.issueDate DESC",
                Invoice.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `find` của EntityManager.
     */
    @Override
    public Invoice findById(EntityManager em, Long id) {
        return em.find(Invoice.class, id);
    }

    /**
     * {@inheritDoc}
     * Lọc hóa đơn theo `studentId` và tải sẵn thông tin học viên.
     */
    @Override
    public List<Invoice> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                "SELECT i FROM Invoice i JOIN FETCH i.student WHERE i.student.studentId = :sid ORDER BY i.issueDate DESC",
                Invoice.class)
                .setParameter("sid", studentId)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `persist` của EntityManager.
     */
    @Override
    public void save(EntityManager em, Invoice invoice) {
        em.persist(invoice);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `merge` của EntityManager.
     */
    @Override
    public void update(EntityManager em, Invoice invoice) {
        em.merge(invoice);
    }

    /**
     * {@inheritDoc}
     * Tìm đối tượng trước khi xóa bằng phương thức `remove` của EntityManager.
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Invoice inv = em.find(Invoice.class, id);
        if (inv != null) {
            em.remove(inv);
        }
    }
}
