package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.repo.StudentGradeRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ chịu trách nhiệm cho Student xem điểm.
 */
public class StudentGradeService {

    private final StudentGradeRepository resultRepo;
    private final TransactionManager tx;

    public StudentGradeService(StudentGradeRepository resultRepo, TransactionManager tx) {
        this.resultRepo = resultRepo;
        this.tx = tx;
    }

    /**
     * Lấy toàn bộ lớp mà học viên tham gia (có hoặc chưa có điểm).
     * Dùng Stream: lấy enrollments → merge với results (nếu có) → tạo
     * StudentGradeRow.
     * Sắp xếp: lớp đang Ongoing lên trước, rồi theo tên lớp.
     */
    public List<StudentGradeRow> getStudentGrades(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            // 1. Lấy tất cả enrollment của học viên (mọi trạng thái)
            List<Enrollment> enrollments = resultRepo.findEnrollmentsByStudentId(em, studentId);

            // 2. Lấy tất cả result của học viên → Map<classId, Result> để lookup nhanh
            Map<Long, Result> resultMap = resultRepo.findResultsByStudentId(em, studentId)
                    .stream()
                    .collect(Collectors.toMap(
                            r -> r.getClassEntity().getClassId(),
                            r -> r));

            // 3. Stream: lọc bỏ lớp Cancelled, merge enrollment + result → StudentGradeRow
            return enrollments.stream()
                    .filter(e -> e.getClassEntity().getStatus() != ClassEntity.Status.Cancelled)
                    .map(e -> {
                        ClassEntity cls = e.getClassEntity();
                        Result result = resultMap.get(cls.getClassId());
                        return new StudentGradeRow(
                                cls.getClassName(),
                                cls.getCourse().getCourseName(),
                                result != null ? result.getScore() : null,
                                result != null && result.getGrade() != null ? result.getGrade() : "",
                                result != null && result.getComment() != null ? result.getComment() : "",
                                cls.getStatus().toString());
                    })
                    .sorted(Comparator.comparing(
                            (StudentGradeRow r) -> r.classStatus().equals("Ongoing") ? 0 : 1)
                            .thenComparing(StudentGradeRow::className))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Record chứa dữ liệu điểm cho học viên xem (bao gồm cả lớp chưa có điểm).
     */
    public record StudentGradeRow(
            String className, String courseName,
            BigDecimal score, String grade,
            String comment, String classStatus) {
    }
}
