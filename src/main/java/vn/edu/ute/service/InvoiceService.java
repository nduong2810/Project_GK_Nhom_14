package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.*;
import vn.edu.ute.repo.EnrollmentRepository;
import vn.edu.ute.repo.InvoiceRepository;
import vn.edu.ute.repo.PaymentRepository;
import vn.edu.ute.repo.PromotionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * SRP: Chỉ chịu trách nhiệm quản lý Invoice (tạo, hủy, truy vấn).
 * Delegate logic tính discount cho PromotionService (DRY + SRP).
 */
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PromotionRepository promotionRepo;
    private final PaymentRepository paymentRepo;
    private final PromotionService promotionService; // DIP: delegate discount calculation
    private final TransactionManager tx;

    public InvoiceService(InvoiceRepository invoiceRepo,
            EnrollmentRepository enrollmentRepo,
            PromotionRepository promotionRepo,
            PaymentRepository paymentRepo,
            PromotionService promotionService,
            TransactionManager tx) {
        this.invoiceRepo = invoiceRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.promotionRepo = promotionRepo;
        this.paymentRepo = paymentRepo;
        this.promotionService = promotionService;
        this.tx = tx;
    }

    // ==================== CREATE ====================

    /**
     * Tạo hóa đơn từ một enrollment, có thể áp dụng khuyến mãi.
     * Học phí = Course.fee - discount (nếu có promotion).
     */
    public void createInvoice(Enrollment enrollment, String note, Long promotionId) throws Exception {
        tx.runInTransaction(em -> {
            Invoice invoice = buildInvoice(em, enrollment, note, promotionId);
            invoiceRepo.save(em, invoice);
            return null;
        });
    }

    /**
     * Tạo hóa đơn và trả về invoice vừa tạo.
     */
    public Invoice createInvoiceAndReturn(Enrollment enrollment, String note, Long promotionId) throws Exception {
        return tx.runInTransaction(em -> {
            Invoice invoice = buildInvoice(em, enrollment, note, promotionId);
            invoiceRepo.save(em, invoice);
            em.flush();
            return invoice;
        });
    }

    /**
     * SRP + DRY: Logic tạo Invoice được gộp vào 1 method duy nhất,
     * tránh duplicate code giữa createInvoice và createInvoiceAndReturn.
     */
    private Invoice buildInvoice(jakarta.persistence.EntityManager em,
            Enrollment enrollment, String note, Long promotionId) throws Exception {
        Enrollment e = enrollmentRepo.findById(em, enrollment.getEnrollmentId());
        BigDecimal courseFee = e.getClassEntity().getCourse().getFee();

        Invoice invoice = new Invoice();
        invoice.setStudent(e.getStudent());
        invoice.setEnrollment(e);
        invoice.setIssueDate(LocalDate.now());
        invoice.setStatus(Invoice.Status.Issued);

        // Ghi chú: ưu tiên ghi chú user nhập, nếu trống thì tự sinh
        if (note != null && !note.isEmpty()) {
            invoice.setNote(note);
        } else {
            invoice.setNote("Học phí lớp " + e.getClassEntity().getClassName());
        }

        // Áp dụng khuyến mãi nếu có — delegate cho PromotionService (DRY)
        if (promotionId != null) {
            Promotion promo = promotionRepo.findById(em, promotionId);
            if (promo != null) {
                BigDecimal discount = promotionService.calculateDiscount(courseFee, promo);
                invoice.setTotalAmount(courseFee.subtract(discount));
                invoice.setPromotion(promo);
            } else {
                invoice.setTotalAmount(courseFee);
            }
        } else {
            invoice.setTotalAmount(courseFee);
        }

        return invoice;
    }

    // ==================== CANCEL ====================

    /**
     * Hủy hóa đơn (chỉ hủy được nếu chưa thanh toán).
     */
    public void cancelInvoice(Long invoiceId) throws Exception {
        tx.runInTransaction(em -> {
            Invoice invoice = invoiceRepo.findById(em, invoiceId);
            if (invoice == null) {
                throw new IllegalArgumentException("Không tìm thấy hóa đơn với ID: " + invoiceId);
            }
            if (invoice.getStatus() == Invoice.Status.Paid) {
                throw new IllegalArgumentException("Không thể hủy hóa đơn đã thanh toán.");
            }

            // Kiểm tra xem đã có payment nào chưa (dùng Stream API)
            List<Payment> payments = paymentRepo.findByInvoiceId(em, invoiceId);
            boolean hasCompletedPayment = payments.stream()
                    .anyMatch(p -> p.getStatus() == Payment.Status.Completed);
            if (hasCompletedPayment) {
                throw new IllegalArgumentException("Không thể hủy hóa đơn đã có thanh toán. Vui lòng hoàn tiền trước.");
            }

            invoice.setStatus(Invoice.Status.Cancelled);
            invoiceRepo.update(em, invoice);
            return null;
        });
    }

    // ==================== QUERY ====================

    public List<Invoice> getAllInvoices() throws Exception {
        return tx.runInTransaction(em -> invoiceRepo.findAll(em));
    }

    public List<Invoice> getInvoicesByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> invoiceRepo.findByStudentId(em, studentId));
    }

    /**
     * Lấy danh sách enrollment (status = Enrolled) chưa có hóa đơn.
     */
    public List<Enrollment> getEnrolledWithoutInvoice() throws Exception {
        return tx.runInTransaction(em -> enrollmentRepo.findEnrolledWithoutInvoice(em));
    }
}
