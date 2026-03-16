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
 * Lớp Service chuyên xử lý nghiệp vụ hoàn tiền (refund).
 * SRP (Single Responsibility Principle): Lớp này chỉ có một trách nhiệm duy nhất là xử lý logic liên quan đến hoàn tiền.
 */
public class RefundService {

    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;
    private final TransactionManager tx;

    /** Tỷ lệ hoàn tiền theo quy định của trung tâm: 70% */
    private static final BigDecimal REFUND_RATE = new BigDecimal("0.70");

    public RefundService(InvoiceRepository invoiceRepo,
            PaymentRepository paymentRepo,
            TransactionManager tx) {
        this.invoiceRepo = invoiceRepo;
        this.paymentRepo = paymentRepo;
        this.tx = tx;
    }

    /**
     * Thực hiện hoàn tiền cho một hóa đơn theo quy định: hoàn 70% tổng số tiền đã thanh toán.
     * <p>
     * Logic nghiệp vụ:
     * <ul>
     *     <li>Chỉ hoàn tiền khi có ít nhất một thanh toán (Payment) đã ở trạng thái 'Completed'.</li>
     *     <li>Chỉ hoàn tiền khi lớp học chưa bắt đầu (trạng thái 'Open' hoặc 'Planned').</li>
     *     <li>Tạo một bản ghi thanh toán mới (Payment) với số tiền âm (đại diện cho việc hoàn tiền) và trạng thái 'Refunded'.</li>
     *     <li>Cập nhật trạng thái của tất cả các thanh toán 'Completed' cũ thành 'Refunded'.</li>
     *     <li>Cập nhật trạng thái của hóa đơn (Invoice) về 'Issued'.</li>
     * </ul>
     * Phương thức này sử dụng rộng rãi Stream API để xử lý dữ liệu.
     *
     * @param invoiceId ID của hóa đơn cần hoàn tiền.
     * @return Số tiền đã hoàn lại cho học viên (bằng 70% tổng số tiền đã thanh toán).
     * @throws Exception Nếu có lỗi xảy ra trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu hóa đơn không hợp lệ để hoàn tiền (không tìm thấy, đã hủy, đã hoàn, lớp đã bắt đầu/kết thúc).
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

            // Kiểm tra trạng thái lớp học: chỉ hoàn tiền khi lớp chưa bắt đầu (Open/Planned)
            if (invoice.getEnrollment() != null && invoice.getEnrollment().getClassEntity() != null) {
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

            // Lấy danh sách các thanh toán đã hoàn thành (Completed) bằng Stream API
            List<Payment> payments = paymentRepo.findByInvoiceId(em, invoiceId);
            List<Payment> completedPayments = payments.stream()
                    .filter(p -> p.getStatus() == Payment.Status.Completed)
                    .collect(Collectors.toList());

            if (completedPayments.isEmpty()) {
                throw new IllegalArgumentException("Hóa đơn chưa có thanh toán nào đã hoàn thành. Không thể hoàn tiền.");
            }

            // Kiểm tra xem hóa đơn đã được hoàn tiền trước đó chưa (dùng Stream anyMatch)
            boolean alreadyRefunded = payments.stream()
                    .anyMatch(p -> p.getStatus() == Payment.Status.Refunded);
            if (alreadyRefunded) {
                throw new IllegalArgumentException("Hóa đơn này đã được hoàn tiền rồi.");
            }

            // Tính tổng số tiền đã thanh toán bằng Stream API (map và reduce)
            BigDecimal totalPaid = completedPayments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Tính số tiền hoàn lại: 70% của tổng đã thanh toán
            BigDecimal refundAmount = totalPaid.multiply(REFUND_RATE);

            // Đánh dấu tất cả các thanh toán cũ là 'Refunded' (dùng Stream forEach)
            completedPayments.forEach(p -> {
                p.setStatus(Payment.Status.Refunded);
                em.merge(p);
            });

            // Tạo một bản ghi thanh toán mới để ghi nhận việc hoàn tiền (với số tiền âm)
            Payment refundPayment = new Payment();
            refundPayment.setStudent(invoice.getStudent());
            refundPayment.setInvoice(invoice);
            refundPayment.setAmount(refundAmount.negate()); // Số tiền âm
            refundPayment.setPaymentDate(LocalDateTime.now());
            refundPayment.setPaymentMethod(Payment.PaymentMethod.Cash); // Giả định hoàn bằng tiền mặt
            refundPayment.setStatus(Payment.Status.Refunded);

            refundPayment.setReferenceCode("REFUND-" + invoiceId);

            if (invoice.getEnrollment() != null) {
                refundPayment.setEnrollment(invoice.getEnrollment());
            }

            paymentRepo.save(em, refundPayment);

            // Cập nhật trạng thái hóa đơn về 'Issued' sau khi hoàn tiền
            invoice.setStatus(Invoice.Status.Issued);
            invoiceRepo.update(em, invoice);

            return refundAmount; // Trả về số tiền đã hoàn
        });
    }
}
