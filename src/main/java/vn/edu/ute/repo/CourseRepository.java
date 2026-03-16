package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Course;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các khóa học (Course).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface CourseRepository {

    /**
     * Lấy tất cả các khóa học từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các khóa học.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Course> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một khóa học dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của khóa học cần tìm.
     * @return Đối tượng Course nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Course findById(EntityManager em, Long id) throws Exception;

    /**
     * Lưu một khóa học mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param course Đối tượng Course cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Course course) throws Exception;

    /**
     * Cập nhật thông tin của một khóa học đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param course Đối tượng Course với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, Course course) throws Exception;

    /**
     * Xóa một khóa học khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của khóa học cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các khóa học đang ở trạng thái hoạt động (Active).
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách các khóa học đang hoạt động.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Course> findActiveCourses(EntityManager em) throws Exception;
}
