package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các lớp học (ClassEntity).
 * Định nghĩa các phương thức CRUD (Create, Read, Update, Delete) cơ bản.
 */
public interface ClassRepository {

    /**
     * Lấy tất cả các lớp học từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các lớp học.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<ClassEntity> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một lớp học dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của lớp học cần tìm.
     * @return Đối tượng ClassEntity nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    ClassEntity findById(EntityManager em, Long id) throws Exception;

    /**
     * Lưu một lớp học mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param classEntity Đối tượng ClassEntity cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, ClassEntity classEntity) throws Exception;

    /**
     * Cập nhật thông tin của một lớp học đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param classEntity Đối tượng ClassEntity với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, ClassEntity classEntity) throws Exception;

    /**
     * Xóa một lớp học khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của lớp học cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;
}
