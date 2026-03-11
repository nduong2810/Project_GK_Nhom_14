package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;

import java.util.List;

/**
 * ISP: Interface chuyên biệt cho use-case HocVien xem điểm.
 * Tách khỏi GradeEntryRepository để mỗi interface chỉ phục vụ 1 client.
 */
public interface StudentGradeRepository {

    /**
     * Lấy danh sách kết quả (Result) theo studentId (kèm thông tin lớp + khóa học).
     */
    List<Result> findResultsByStudentId(EntityManager em, Long studentId);

    /**
     * Lấy tất cả enrollment của học viên (kèm fetch class + course) — mọi trạng thái.
     */
    List<Enrollment> findEnrollmentsByStudentId(EntityManager em, Long studentId);
}
