package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Invoice;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các hóa đơn (Invoice).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface InvoiceRepository {

    /**
     * Lấy tất cả các hóa đơn từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các hóa đơn.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Invoice> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một hóa đơn dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của hóa đơn cần tìm.
     * @return Đối tượng Invoice nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Invoice findById(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các hóa đơn của một học viên cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các hóa đơn của học viên đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Invoice> findByStudentId(EntityManager em, Long studentId) throws Exception;

    /**
     * Lưu một hóa đơn mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param invoice Đối tượng Invoice cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Invoice invoice) throws Exception;

    /**
     * Cập nhật thông tin của một hóa đơn đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param invoice Đối tượng Invoice với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, Invoice invoice) throws Exception;

    /**
     * Xóa một hóa đơn khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của hóa đơn cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;
}
