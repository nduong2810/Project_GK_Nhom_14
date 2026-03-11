package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;

import java.util.List;

/**
 * ISP: Interface chuyên biệt cho use-case GiaoVien nhập điểm.
 * Tách khỏi StudentGradeRepository để mỗi interface chỉ phục vụ 1 client.
 */
public interface GradeEntryRepository {

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
}
