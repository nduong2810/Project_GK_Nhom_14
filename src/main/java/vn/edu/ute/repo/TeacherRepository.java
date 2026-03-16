package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import java.util.List;
import java.util.Optional;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của giáo viên (Teacher).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface TeacherRepository {

    /**
     * Lấy tất cả các giáo viên từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các giáo viên.
     */
    List<Teacher> findAll(EntityManager em);

    /**
     * Tìm một giáo viên dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của giáo viên cần tìm.
     * @return Một đối tượng Optional chứa giáo viên nếu tìm thấy, ngược lại là Optional rỗng.
     */
    Optional<Teacher> findById(EntityManager em, Long id);

    /**
     * Tìm kiếm giáo viên theo tên.
     * @param em EntityManager để thực hiện truy vấn.
     * @param name Chuỗi ký tự chứa trong tên giáo viên.
     * @return Danh sách các giáo viên có tên chứa chuỗi ký tự đã cho.
     */
    List<Teacher> findByNameContaining(EntityManager em, String name);

    /**
     * Tìm tất cả các giáo viên đang ở trạng thái hoạt động (Active).
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách các giáo viên đang hoạt động.
     */
    List<Teacher> findActiveTeachers(EntityManager em);

    /**
     * Lưu hoặc cập nhật một giáo viên vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param teacher Đối tượng Teacher cần lưu hoặc cập nhật.
     * @return Đối tượng Teacher đã được lưu.
     */
    Teacher save(EntityManager em, Teacher teacher);

    /**
     * Xóa một giáo viên khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của giáo viên cần xóa.
     */
    void deleteById(EntityManager em, Long id);
}
