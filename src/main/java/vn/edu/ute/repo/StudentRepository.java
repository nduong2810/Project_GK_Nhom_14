package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của học viên (Student).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface StudentRepository {

    /**
     * Lấy tất cả các học viên từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các học viên.
     */
    List<Student> findAll(EntityManager em);

    /**
     * Tìm một học viên dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của học viên cần tìm.
     * @return Một đối tượng Optional chứa học viên nếu tìm thấy, ngược lại là Optional rỗng.
     */
    Optional<Student> findById(EntityManager em, Long id);

    /**
     * Tìm kiếm học viên theo tên.
     * @param em EntityManager để thực hiện truy vấn.
     * @param name Chuỗi ký tự chứa trong tên học viên.
     * @return Danh sách các học viên có tên chứa chuỗi ký tự đã cho.
     */
    List<Student> findByNameContaining(EntityManager em, String name);

    /**
     * Lưu hoặc cập nhật một học viên vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param student Đối tượng Student cần lưu hoặc cập nhật.
     * @return Đối tượng Student đã được lưu.
     */
    Student save(EntityManager em, Student student);

    /**
     * Xóa một học viên khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của học viên cần xóa.
     */
    void deleteById(EntityManager em, Long id);

    /**
     * Tìm tất cả các học viên đang ở trạng thái hoạt động (Active).
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách các học viên đang hoạt động.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Student> findActiveStudents(EntityManager em) throws Exception;
}
