package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Staff;
import java.util.List;
import java.util.Optional;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của nhân viên (Staff).
 * Định nghĩa các phương thức CRUD cơ bản.
 */
public interface StaffRepository {

    /**
     * Lấy tất cả các nhân viên từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các nhân viên.
     */
    List<Staff> findAll(EntityManager em);

    /**
     * Tìm một nhân viên dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của nhân viên cần tìm.
     * @return Một đối tượng Optional chứa nhân viên nếu tìm thấy, ngược lại là Optional rỗng.
     */
    Optional<Staff> findById(EntityManager em, Long id);

    /**
     * Lưu hoặc cập nhật một nhân viên vào cơ sở dữ liệu.
     * Nếu nhân viên đã có ID, nó sẽ được cập nhật. Nếu không, một nhân viên mới sẽ được tạo.
     * @param em EntityManager để thực hiện thao tác.
     * @param staff Đối tượng Staff cần lưu hoặc cập nhật.
     * @return Đối tượng Staff đã được lưu (có thể có ID được gán).
     */
    Staff save(EntityManager em, Staff staff);

    /**
     * Xóa một nhân viên khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của nhân viên cần xóa.
     */
    void deleteById(EntityManager em, Long id);
}
