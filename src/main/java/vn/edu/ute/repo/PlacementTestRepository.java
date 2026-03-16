package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.PlacementTest;
import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các bài kiểm tra đầu vào (PlacementTest).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface PlacementTestRepository {

    /**
     * Lưu một bài kiểm tra mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param test Đối tượng PlacementTest cần lưu.
     */
    void save(EntityManager em, PlacementTest test);

    /**
     * Cập nhật thông tin của một bài kiểm tra đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param test Đối tượng PlacementTest với thông tin đã được cập nhật.
     */
    void update(EntityManager em, PlacementTest test);

    /**
     * Xóa một bài kiểm tra khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của bài kiểm tra cần xóa.
     */
    void delete(EntityManager em, Long id);

    /**
     * Tìm một bài kiểm tra dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của bài kiểm tra cần tìm.
     * @return Đối tượng PlacementTest nếu tìm thấy, ngược lại trả về null.
     */
    PlacementTest findById(EntityManager em, Long id);

    /**
     * Lấy tất cả các bài kiểm tra từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các bài kiểm tra.
     */
    List<PlacementTest> findAll(EntityManager em);

    /**
     * Tìm tất cả các bài kiểm tra của một học viên cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các bài kiểm tra của học viên đó.
     */
    List<PlacementTest> findByStudentId(EntityManager em, Long studentId);
}
