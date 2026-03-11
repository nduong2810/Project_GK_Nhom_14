package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.*;
import vn.edu.ute.repo.InvoiceRepository;
import vn.edu.ute.repo.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ chịu trách nhiệm xử lý hoàn tiền (refund).
 */
public class RefundService {

    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;
    private final TransactionManager tx;

    /** Tỷ lệ hoàn tiền theo quy định: 70% */
    private static final BigDecimal REFUND_RATE = new BigDecimal("0.70");

    public RefundService(InvoiceRepository invoiceRepo,
            PaymentRepository paymentRepo,
            TransactionManager tx) {
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
        this.tx = tx;
    }

    /**
     * Hoàn tiền cho hóa đơn theo quy định: hoàn 70% số tiền đã thanh toán.
     * - Chỉ hoàn khi có payment đã Completed
     * - Tạo 1 bản ghi Payment âm (amount < 0) với status = Refunded
     * - Đánh dấu các payment cũ là Refunded
     * Sử dụng Stream API xuyên suốt.
     *
     * @return Số tiền hoàn lại (70% tổng đã thanh toán)
     */
    public BigDecimal refundInvoice(Long invoiceId) throws Exception {
        return tx.runInTransaction(em -> {
            Invoice invoice = invoiceRepo.findById(em, invoiceId);
            if (invoice == null) {
                throw new IllegalArgumentException("Không tìm thấy hóa đơn với ID: " + invoiceId);
            }
            if (invoice.getStatus() == Invoice.Status.Cancelled) {
                throw new IllegalArgumentException("Hóa đơn này đã bị hủy.");
            }

            // Kiểm tra lớp học: chỉ hoàn tiền khi lớp chưa bắt đầu (Open/Planned)
            if (invoice.getEnrollment() != null
                    && invoice.getEnrollment().getClassEntity() != null) {
                ClassEntity.Status classStatus = invoice.getEnrollment().getClassEntity().getStatus();
                if (classStatus == ClassEntity.Status.Ongoing) {
                    throw new IllegalArgumentException(
                            "Lớp '" + invoice.getEnrollment().getClassEntity().getClassName()
                                    + "' đang diễn ra (Ongoing). Không thể hoàn tiền khi đã bắt đầu học.");
                }
                if (classStatus == ClassEntity.Status.Completed) {
                    throw new IllegalArgumentException(
                            "Lớp '" + invoice.getEnrollment().getClassEntity().getClassName()
                                    + "' đã kết thúc (Completed). Không thể hoàn tiền.");
                }
            }

            // Lấy danh sách payment đã Completed (dùng Stream filter + collect)
            List<Payment> payments = paymentRepo.findByInvoiceId(em, invoiceId);
            List<Payment> completedPayments = payments.stream()
                    .filter(p -> p.getStatus() == Payment.Status.Completed)
                    .collect(Collectors.toList());

            if (completedPayments.isEmpty()) {
                throw new IllegalArgumentException(
                        "Hóa đơn chưa có thanh toán nào. Không cần hoàn tiền.");
            }

            // Kiểm tra đã hoàn tiền chưa (dùng Stream anyMatch)
            boolean alreadyRefunded = payments.stream()
                    .anyMatch(p -> p.getStatus() == Payment.Status.Refunded);
            if (alreadyRefunded) {
                throw new IllegalArgumentException("Hóa đơn này đã được hoàn tiền rồi.");
            }

            // Tính tổng đã thanh toán bằng Stream API
            BigDecimal totalPaid = completedPayments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Tính số tiền hoàn: 70%
            BigDecimal refundAmount = totalPaid.multiply(REFUND_RATE);

            // Đánh dấu các payment cũ là Refunded (dùng Stream forEach)
            completedPayments.forEach(p -> {
                p.setStatus(Payment.Status.Refunded);
                em.merge(p);
            });

            // Tạo bản ghi Payment hoàn tiền (số tiền âm)
            Payment refundPayment = new Payment();
            refundPayment.setStudent(invoice.getStudent());
            refundPayment.setInvoice(invoice);
            refundPayment.setAmount(refundAmount.negate());
            refundPayment.setPaymentDate(LocalDateTime.now());
            refundPayment.setPaymentMethod(Payment.PaymentMethod.Cash);
            refundPayment.setStatus(Payment.Status.Refunded);
            refundPayment.setReferenceCode("REFUND-" + invoiceId);

            if (invoice.getEnrollment() != null) {
                refundPayment.setEnrollment(invoice.getEnrollment());
            }

            paymentRepo.save(em, refundPayment);

            // Đổi trạng thái hóa đơn về "Đã xuất" (Issued) sau khi hoàn tiền
            invoice.setStatus(Invoice.Status.Issued);
            invoiceRepo.update(em, invoice);

            return refundAmount;
        });
    }
}
