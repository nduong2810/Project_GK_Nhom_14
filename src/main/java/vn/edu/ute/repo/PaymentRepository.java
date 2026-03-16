package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Payment;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các giao dịch thanh toán (Payment).
 * Định nghĩa các phương thức cần thiết để truy xuất và lưu trữ thông tin thanh toán.
 */
public interface PaymentRepository {

    /**
     * Lấy tất cả các giao dịch thanh toán từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các thanh toán.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Payment> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một giao dịch thanh toán dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của thanh toán cần tìm.
     * @return Đối tượng Payment nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Payment findById(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các giao dịch thanh toán cho một hóa đơn cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param invoiceId ID của hóa đơn.
     * @return Danh sách các thanh toán liên quan đến hóa đơn đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Payment> findByInvoiceId(EntityManager em, Long invoiceId) throws Exception;

    /**
     * Tìm tất cả các giao dịch thanh toán của một học viên cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các thanh toán của học viên đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Payment> findByStudentId(EntityManager em, Long studentId) throws Exception;

    /**
     * Lưu một giao dịch thanh toán mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param payment Đối tượng Payment cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Payment payment) throws Exception;
}
