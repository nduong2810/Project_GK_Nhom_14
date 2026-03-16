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
 * Lớp Service cho nghiệp vụ quản lý hóa đơn (Invoice).
 * SRP (Single Responsibility Principle): Lớp này chỉ chịu trách nhiệm quản lý hóa đơn (tạo, hủy, truy vấn).
 * Logic tính toán giảm giá được ủy quyền cho PromotionService để tuân thủ nguyên tắc DRY (Don't Repeat Yourself) và SRP.
 */
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PromotionRepository promotionRepo;
    private final PaymentRepository paymentRepo;
    private final PromotionService promotionService; // DIP: Ủy quyền việc tính toán giảm giá
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

    // ==================== NGHIỆP VỤ TẠO HÓA ĐƠN ====================

    /**
     * Tạo hóa đơn từ một bản ghi ghi danh (enrollment), có thể áp dụng khuyến mãi.
     * Tổng tiền hóa đơn = Học phí của khóa học - Giảm giá (nếu có).
     * @param enrollment Bản ghi ghi danh làm cơ sở tạo hóa đơn.
     * @param note Ghi chú cho hóa đơn.
     * @param promotionId ID của chương trình khuyến mãi (có thể là null).
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public void createInvoice(Enrollment enrollment, String note, Long promotionId) throws Exception {
        tx.runInTransaction(em -> {
            Invoice invoice = buildInvoice(em, enrollment, note, promotionId);
            invoiceRepo.save(em, invoice);
            return null;
        });
    }

    /**
     * Tạo hóa đơn và trả về đối tượng Invoice vừa được tạo.
     * Hữu ích khi cần ID của hóa đơn ngay sau khi tạo.
     * @return Đối tượng Invoice vừa được lưu vào CSDL.
     */
    public Invoice createInvoiceAndReturn(Enrollment enrollment, String note, Long promotionId) throws Exception {
        return tx.runInTransaction(em -> {
            Invoice invoice = buildInvoice(em, enrollment, note, promotionId);
            invoiceRepo.save(em, invoice);
            em.flush(); // Đẩy các thay đổi vào CSDL để nhận được ID
            return invoice;
        });
    }

    /**
     * Phương thức private để xây dựng đối tượng Invoice.
     * SRP + DRY: Tái sử dụng logic tạo hóa đơn, tránh lặp code giữa `createInvoice` và `createInvoiceAndReturn`.
     * @return Đối tượng Invoice đã được cấu hình nhưng chưa được lưu.
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

        // Ưu tiên ghi chú do người dùng nhập, nếu trống thì tự động tạo ghi chú.
        if (note != null && !note.trim().isEmpty()) {
            invoice.setNote(note);
        } else {
            invoice.setNote("Học phí lớp " + e.getClassEntity().getClassName());
        }

        // Áp dụng khuyến mãi nếu có - ủy quyền cho PromotionService để tính toán
        if (promotionId != null) {
            Promotion promo = promotionRepo.findById(em, promotionId);
            if (promo != null) {
                BigDecimal discount = promotionService.calculateDiscount(courseFee, promo);
                invoice.setTotalAmount(courseFee.subtract(discount));
                invoice.setPromotion(promo);
            } else {
                invoice.setTotalAmount(courseFee); // Nếu promotionId không hợp lệ, dùng giá gốc
            }
        } else {
            invoice.setTotalAmount(courseFee); // Không có khuyến mãi
        }

        return invoice;
    }

    // ==================== NGHIỆP VỤ HỦY HÓA ĐƠN ====================

    /**
     * Hủy một hóa đơn.
     * Logic nghiệp vụ: Chỉ cho phép hủy nếu hóa đơn chưa được thanh toán.
     * @param invoiceId ID của hóa đơn cần hủy.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu hóa đơn không hợp lệ để hủy.
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

            // Kiểm tra xem đã có thanh toán nào được hoàn thành chưa (dùng Stream API)
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

    // ==================== CÁC TRUY VẤN ====================

    /**
     * Lấy tất cả các hóa đơn.
     */
    public List<Invoice> getAllInvoices() throws Exception {
        return tx.runInTransaction(em -> invoiceRepo.findAll(em));
    }

    /**
     * Lấy tất cả hóa đơn của một học viên.
     */
    public List<Invoice> getInvoicesByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> invoiceRepo.findByStudentId(em, studentId));
    }

    /**
     * Lấy danh sách các bản ghi ghi danh (trạng thái 'Enrolled') chưa có hóa đơn.
     * Hữu ích cho việc hiển thị danh sách cần tạo hóa đơn.
     */
    public List<Enrollment> getEnrolledWithoutInvoice() throws Exception {
        return tx.runInTransaction(em -> enrollmentRepo.findEnrolledWithoutInvoice(em));
    }
}
