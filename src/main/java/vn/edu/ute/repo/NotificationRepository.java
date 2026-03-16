package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Notification;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các thông báo (Notification).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface NotificationRepository {

    /**
     * Lấy tất cả các thông báo từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các thông báo.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Notification> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một thông báo dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của thông báo cần tìm.
     * @return Đối tượng Notification nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Notification findById(EntityManager em, Long id) throws Exception;

    /**
     * Lưu một thông báo mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param notification Đối tượng Notification cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Notification notification) throws Exception;

    /**
     * Xóa một thông báo khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của thông báo cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các thông báo dành cho một vai trò (role) cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param role Vai trò của đối tượng nhận thông báo (ví dụ: Student, Teacher).
     * @return Danh sách các thông báo cho vai trò đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Notification> findByTargetRole(EntityManager em, Notification.TargetRole role) throws Exception;
}
