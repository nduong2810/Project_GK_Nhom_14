package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.*;
import vn.edu.ute.repo.EnrollmentRepository;
import vn.edu.ute.repo.InvoiceRepository;
import vn.edu.ute.repo.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FinanceService {

    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final TransactionManager tx;

    // Dependency Injection qua Constructor
    public FinanceService(InvoiceRepository invoiceRepo,
            PaymentRepository paymentRepo,
            EnrollmentRepository enrollmentRepo,
            TransactionManager tx) {
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.tx = tx;
    }

    // ==================== INVOICE ====================

    /**
     * Tạo hóa đơn từ một enrollment.
     * Học phí = Course.fee của lớp mà học viên đăng ký.
     */
    public void createInvoice(Enrollment enrollment, String note) throws Exception {
        tx.runInTransaction(em -> {
            // Load lại enrollment trong session hiện tại để tránh detached entity
            Enrollment e = enrollmentRepo.findById(em, enrollment.getEnrollmentId());
            BigDecimal courseFee = e.getClassEntity().getCourse().getFee();

            Invoice invoice = new Invoice();
            invoice.setStudent(e.getStudent());
            invoice.setEnrollment(e);
            invoice.setTotalAmount(courseFee);
            invoice.setIssueDate(LocalDate.now());
            invoice.setStatus(Invoice.Status.Issued);
            invoice.setNote(note);

            invoiceRepo.save(em, invoice);
            return null;
        });
    }

    /**
     * Tạo hóa đơn và liên kết với enrollment (lưu enrollment_id trong note để truy
     * vết).
     */
    public Invoice createInvoiceAndReturn(Enrollment enrollment, String note) throws Exception {
        return tx.runInTransaction(em -> {
            Enrollment e = enrollmentRepo.findById(em, enrollment.getEnrollmentId());
            BigDecimal courseFee = e.getClassEntity().getCourse().getFee();

            Invoice invoice = new Invoice();
            invoice.setStudent(e.getStudent());
            invoice.setEnrollment(e);
            invoice.setTotalAmount(courseFee);
            invoice.setIssueDate(LocalDate.now());
            invoice.setStatus(Invoice.Status.Issued);
            // Ghi chú: ưu tiên ghi chú user nhập, nếu trống thì tự sinh
            if (note != null && !note.isEmpty()) {
                invoice.setNote(note);
            } else {
                invoice.setNote("Học phí lớp " + e.getClassEntity().getClassName());
            }

            invoiceRepo.save(em, invoice);
            em.flush(); // Đảm bảo ID được sinh ra
            return invoice;
        });
    }

    /**
     * Ghi nhận thanh toán cho một hóa đơn.
     * Nếu tổng tiền đã thanh toán >= totalAmount → cập nhật Invoice status = Paid.
     */
    public void recordPayment(Long invoiceId, Long enrollmentId,
            BigDecimal amount, Payment.PaymentMethod method,
            String referenceCode) throws Exception {
        tx.runInTransaction(em -> {
            Invoice invoice = invoiceRepo.findById(em, invoiceId);
            if (invoice == null) {
                throw new IllegalArgumentException("Không tìm thấy hóa đơn với ID: " + invoiceId);
            }
            if (invoice.getStatus() == Invoice.Status.Paid) {
                throw new IllegalArgumentException("Hóa đơn này đã được thanh toán đầy đủ.");
            }
            if (invoice.getStatus() == Invoice.Status.Cancelled) {
                throw new IllegalArgumentException("Hóa đơn này đã bị hủy.");
            }

            // Tạo payment mới
            Payment payment = new Payment();
            payment.setStudent(invoice.getStudent());
            payment.setInvoice(invoice);
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(method);
            payment.setStatus(Payment.Status.Completed);
            payment.setReferenceCode(referenceCode);

            // Gắn enrollment nếu có
            if (enrollmentId != null) {
                Enrollment enrollment = enrollmentRepo.findById(em, enrollmentId);
                payment.setEnrollment(enrollment);
            }

            paymentRepo.save(em, payment);

            // Tính tổng đã thanh toán cho invoice này (dùng Stream API)
            // Lưu ý: payment vừa save ở trên đã nằm trong DB, nên findByInvoiceId đã bao
            // gồm nó
            List<Payment> existingPayments = paymentRepo.findByInvoiceId(em, invoiceId);
            BigDecimal totalPaid = existingPayments.stream()
                    .filter(p -> p.getStatus() == Payment.Status.Completed)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Nếu đã trả đủ → cập nhật trạng thái hóa đơn
            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus(Invoice.Status.Paid);
                invoiceRepo.update(em, invoice);
            }

            return null;
        });
    }

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

            // Kiểm tra xem đã có payment nào chưa
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

    public List<Payment> getPaymentsByInvoice(Long invoiceId) throws Exception {
        return tx.runInTransaction(em -> paymentRepo.findByInvoiceId(em, invoiceId));
    }

    /**
     * Lấy danh sách enrollment (status = Enrolled) chưa có hóa đơn.
     */
    public List<Enrollment> getEnrolledWithoutInvoice() throws Exception {
        return tx.runInTransaction(em -> enrollmentRepo.findEnrolledWithoutInvoice(em));
    }

    /**
     * Tính tổng đã thanh toán cho một invoice (dùng Stream API).
     */
    public BigDecimal getTotalPaidForInvoice(Long invoiceId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Payment> payments = paymentRepo.findByInvoiceId(em, invoiceId);
            return payments.stream()
                    .filter(p -> p.getStatus() == Payment.Status.Completed)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }

    /**
     * Lấy tổng đã thanh toán cho TẤT CẢ invoices (dùng Stream API groupingBy).
     * Trả về Map: invoiceId → totalPaid. Tránh vấn đề N+1 query.
     */
    public java.util.Map<Long, BigDecimal> getAllPaidAmounts() throws Exception {
        return tx.runInTransaction(em -> {
            List<Payment> allPayments = paymentRepo.findAll(em);
            return allPayments.stream()
                    .filter(p -> p.getStatus() == Payment.Status.Completed && p.getInvoice() != null)
                    .collect(java.util.stream.Collectors.groupingBy(
                            p -> p.getInvoice().getInvoiceId(),
                            java.util.stream.Collectors.reducing(
                                    BigDecimal.ZERO,
                                    Payment::getAmount,
                                    BigDecimal::add)));
        });
    }
}
