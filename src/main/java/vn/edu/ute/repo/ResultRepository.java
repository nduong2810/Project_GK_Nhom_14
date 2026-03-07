package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;

import java.util.List;

public interface ResultRepository {

    /**
     * Lấy danh sách lớp học mà giáo viên đang phụ trách (không bao gồm lớp đã hủy).
     */
    List<ClassEntity> findClassesByTeacherId(EntityManager em, Long teacherId);

    /**
     * Lấy danh sách enrollment đang Enrolled của một lớp.
     */
    List<Enrollment> findEnrolledStudentsByClassId(EntityManager em, Long classId);

    /**
     * Lấy danh sách kết quả (Result) theo classId.
     */
    List<Result> findResultsByClassId(EntityManager em, Long classId);

    /**
     * Lấy danh sách kết quả (Result) theo studentId (kèm thông tin lớp + khóa học).
     */
    List<Result> findResultsByStudentId(EntityManager em, Long studentId);

    /**
     * Lấy tất cả enrollment của học viên (kèm fetch class + course) — mọi trạng
     * thái.
     */
    List<Enrollment> findEnrollmentsByStudentId(EntityManager em, Long studentId);
}
