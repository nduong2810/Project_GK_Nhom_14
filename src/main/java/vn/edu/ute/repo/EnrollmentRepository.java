package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;

import java.util.List;

public interface EnrollmentRepository {
    List<Enrollment> findAll(EntityManager em) throws Exception;

    Enrollment findById(EntityManager em, Long id) throws Exception;

    List<Enrollment> findByStudentId(EntityManager em, Long studentId) throws Exception;

    /**
     * Tìm các enrollment có status = 'Enrolled' mà chưa có invoice nào tương ứng.
     * Dùng để hiển thị danh sách enrollment chờ tạo hóa đơn.
     */
    List<Enrollment> findEnrolledWithoutInvoice(EntityManager em) throws Exception;
    void save(EntityManager em, Enrollment enrollment) throws Exception;
    void update(EntityManager em, Enrollment enrollment) throws Exception;
    void delete(EntityManager em, Long id) throws Exception;
}
