package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.*;
import vn.edu.ute.repo.EnrollmentRepository;
import vn.edu.ute.repo.InvoiceRepository;
import vn.edu.ute.repo.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ chịu trách nhiệm quản lý Payment (ghi nhận thanh toán, truy vấn).
 */
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final InvoiceRepository invoiceRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final TransactionManager tx;

    public PaymentService(PaymentRepository paymentRepo,
            InvoiceRepository invoiceRepo,
            EnrollmentRepository enrollmentRepo,
            TransactionManager tx) {
        this.paymentRepo = paymentRepo;
        this.invoiceRepo = invoiceRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.tx = tx;
    }

    /**
     * Ghi nhận thanh toán cho một hóa đơn.
     * Nếu tổng tiền đã thanh toán >= totalAmount → cập nhật Invoice status = Paid.
     * Sử dụng Stream API để tính tổng paid.
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

    // ==================== QUERY ====================

    public List<Payment> getPaymentsByInvoice(Long invoiceId) throws Exception {
        return tx.runInTransaction(em -> paymentRepo.findByInvoiceId(em, invoiceId));
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
    public Map<Long, BigDecimal> getAllPaidAmounts() throws Exception {
        return tx.runInTransaction(em -> {
            List<Payment> allPayments = paymentRepo.findAll(em);
            return allPayments.stream()
                    .filter(p -> p.getStatus() == Payment.Status.Completed && p.getInvoice() != null)
                    .collect(Collectors.groupingBy(
                            p -> p.getInvoice().getInvoiceId(),
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    Payment::getAmount,
                                    BigDecimal::add)));
        });
    }
}
