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
 * Lớp Service cho nghiệp vụ quản lý thanh toán (Payment).
 * SRP (Single Responsibility Principle): Lớp này chỉ chịu trách nhiệm ghi nhận và truy vấn các giao dịch thanh toán.
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
     * Ghi nhận một giao dịch thanh toán cho một hóa đơn.
     * <p>
     * Logic nghiệp vụ:
     * <ul>
     *     <li>Kiểm tra tính hợp lệ của hóa đơn (tồn tại, chưa thanh toán xong, chưa bị hủy).</li>
     *     <li>Tạo một bản ghi Payment mới với trạng thái 'Completed'.</li>
     *     <li>Sau khi lưu thanh toán mới, tính lại tổng số tiền đã thanh toán cho hóa đơn đó.</li>
     *     <li>Nếu tổng đã thanh toán lớn hơn hoặc bằng tổng tiền hóa đơn, cập nhật trạng thái hóa đơn thành 'Paid'.</li>
     * </ul>
     *
     * @param invoiceId ID của hóa đơn được thanh toán.
     * @param enrollmentId ID của lần ghi danh liên quan (có thể null).
     * @param amount Số tiền thanh toán.
     * @param method Phương thức thanh toán.
     * @param referenceCode Mã tham chiếu của giao dịch (nếu có).
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu hóa đơn không hợp lệ để thanh toán.
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

            // Tạo đối tượng Payment mới
            Payment payment = new Payment();
            payment.setStudent(invoice.getStudent());
            payment.setInvoice(invoice);
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMethod(method);
            payment.setStatus(Payment.Status.Completed);
            payment.setReferenceCode(referenceCode);

            // Gắn enrollment vào payment nếu có
            if (enrollmentId != null) {
                Enrollment enrollment = enrollmentRepo.findById(em, enrollmentId);
                payment.setEnrollment(enrollment);
            }

            paymentRepo.save(em, payment);

            // Tính lại tổng số tiền đã thanh toán cho hóa đơn này (sử dụng Stream API)
            List<Payment> existingPayments = paymentRepo.findByInvoiceId(em, invoiceId);
            BigDecimal totalPaid = existingPayments.stream()
                    .filter(p -> p.getStatus() == Payment.Status.Completed)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Nếu đã trả đủ, cập nhật trạng thái hóa đơn thành 'Paid'
            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus(Invoice.Status.Paid);
                invoiceRepo.update(em, invoice);
            }

            return null;
        });
    }

    // ==================== CÁC TRUY VẤN ====================

    /**
     * Lấy danh sách các thanh toán của một hóa đơn.
     */
    public List<Payment> getPaymentsByInvoice(Long invoiceId) throws Exception {
        return tx.runInTransaction(em -> paymentRepo.findByInvoiceId(em, invoiceId));
    }

    /**
     * Tính tổng số tiền đã thanh toán cho một hóa đơn cụ thể (sử dụng Stream API).
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
     * Lấy tổng số tiền đã thanh toán cho TẤT CẢ các hóa đơn.
     * Sử dụng Stream API với `groupingBy` và `reducing` để nhóm các thanh toán theo `invoiceId` và tính tổng.
     * Cách này hiệu quả hơn việc lặp qua từng hóa đơn và truy vấn thanh toán riêng lẻ (tránh vấn đề N+1 query).
     * @return Một Map với key là `invoiceId` và value là tổng số tiền đã thanh toán cho hóa đơn đó.
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
