package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Certificate;
import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các chứng chỉ (Certificate).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface CertificateRepository {

    /**
     * Lưu một chứng chỉ mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param certificate Đối tượng Certificate cần lưu.
     */
    void save(EntityManager em, Certificate certificate);

    /**
     * Cập nhật thông tin của một chứng chỉ đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param certificate Đối tượng Certificate với thông tin đã được cập nhật.
     */
    void update(EntityManager em, Certificate certificate);

    /**
     * Xóa một chứng chỉ khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của chứng chỉ cần xóa.
     */
    void delete(EntityManager em, Long id);

    /**
     * Tìm một chứng chỉ dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của chứng chỉ cần tìm.
     * @return Đối tượng Certificate nếu tìm thấy, ngược lại trả về null.
     */
    Certificate findById(EntityManager em, Long id);

    /**
     * Lấy tất cả các chứng chỉ từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các chứng chỉ.
     */
    List<Certificate> findAll(EntityManager em);

    /**
     * Tìm tất cả các chứng chỉ của một học viên cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các chứng chỉ của học viên đó.
     */
    List<Certificate> findByStudentId(EntityManager em, Long studentId);
}
