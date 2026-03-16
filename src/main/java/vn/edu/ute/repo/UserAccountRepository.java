package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.UserAccount;

import java.util.List;
import java.util.Optional;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các tài khoản người dùng (UserAccount).
 * Định nghĩa các phương thức cần thiết để truy xuất và quản lý tài khoản.
 */
public interface UserAccountRepository {

    /**
     * Tìm một tài khoản người dùng dựa trên tên đăng nhập (username).
     * @param em EntityManager để thực hiện truy vấn.
     * @param username Tên đăng nhập cần tìm.
     * @return Đối tượng UserAccount nếu tìm thấy, ngược lại trả về null.
     */
    UserAccount findByUsername(EntityManager em, String username);

    /**
     * Lấy tất cả các tài khoản người dùng từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các tài khoản người dùng.
     */
    List<UserAccount> findAll(EntityManager em);

    /**
     * Tìm một tài khoản người dùng dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của tài khoản cần tìm.
     * @return Một đối tượng Optional chứa tài khoản nếu tìm thấy, ngược lại là Optional rỗng.
     */
    Optional<UserAccount> findById(EntityManager em, Long id);

    /**
     * Lưu hoặc cập nhật một tài khoản người dùng vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param userAccount Đối tượng UserAccount cần lưu hoặc cập nhật.
     * @return Đối tượng UserAccount đã được lưu.
     */
    UserAccount save(EntityManager em, UserAccount userAccount);

    /**
     * Xóa một tài khoản người dùng khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của tài khoản cần xóa.
     */
    void deleteById(EntityManager em, Long id);
}
