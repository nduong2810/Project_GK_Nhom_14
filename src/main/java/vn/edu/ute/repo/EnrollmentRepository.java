package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu ghi danh (Enrollment).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface EnrollmentRepository {

    /**
     * Lấy tất cả các bản ghi ghi danh từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các bản ghi ghi danh.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Enrollment> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một bản ghi ghi danh dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của bản ghi ghi danh cần tìm.
     * @return Đối tượng Enrollment nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Enrollment findById(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các bản ghi ghi danh của một học viên cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các bản ghi ghi danh của học viên đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Enrollment> findByStudentId(EntityManager em, Long studentId) throws Exception;

    /**
     * Tìm các bản ghi ghi danh có trạng thái 'Enrolled' nhưng chưa có hóa đơn (invoice) tương ứng.
     * Dùng để hiển thị danh sách các trường hợp cần tạo hóa đơn học phí.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách các bản ghi ghi danh chưa có hóa đơn.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Enrollment> findEnrolledWithoutInvoice(EntityManager em) throws Exception;

    /**
     * Lưu một bản ghi ghi danh mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param enrollment Đối tượng Enrollment cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Enrollment enrollment) throws Exception;

    /**
     * Cập nhật thông tin của một bản ghi ghi danh đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param enrollment Đối tượng Enrollment với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, Enrollment enrollment) throws Exception;

    /**
     * Xóa một bản ghi ghi danh khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của bản ghi ghi danh cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;
}
