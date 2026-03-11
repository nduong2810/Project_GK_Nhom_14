package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.GradeEntryRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SRP: Chỉ chịu trách nhiệm cho Teacher nhập điểm.
 * OCP: calculateGrade dùng data-driven approach + Stream API,
 * dễ mở rộng thêm mức điểm mới mà không sửa logic.
 */
public class GradeEntryService {

    private final GradeEntryRepository resultRepo;
    private final TransactionManager tx;

    /**
     * OCP: Danh sách GradeRule — có thể thêm/sửa rule mà KHÔNG sửa logic
     * calculateGrade.
     * Sắp xếp từ cao → thấp (threshold giảm dần).
     */
    private static final List<GradeRule> GRADE_RULES = List.of(
            new GradeRule(90, "A+"),
            new GradeRule(85, "A"),
            new GradeRule(80, "B+"),
            new GradeRule(70, "B"),
            new GradeRule(65, "C+"),
            new GradeRule(55, "C"),
            new GradeRule(50, "D+"),
            new GradeRule(40, "D"));

    /** Record chứa threshold và grade label — OCP compliant */
    private record GradeRule(double threshold, String grade) {
    }

    public GradeEntryService(GradeEntryRepository resultRepo, TransactionManager tx) {
        this.resultRepo = resultRepo;
        this.tx = tx;
    }

    /**
     * Lấy danh sách lớp Ongoing của giáo viên (chỉ cho phép nhập điểm lớp Ongoing).
     * Dùng Stream filter + sorted.
     */
    public List<ClassEntity> getOngoingClassesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            List<ClassEntity> allClasses = resultRepo.findClassesByTeacherId(em, teacherId);
            return allClasses.stream()
                    .filter(c -> c.getStatus() == ClassEntity.Status.Ongoing)
                    .sorted(Comparator.comparing(ClassEntity::getClassName))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy danh sách học viên đang enrolled trong một lớp.
     * Sắp xếp theo tên bằng Stream API.
     */
    public List<Enrollment> getEnrolledStudents(Long classId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Enrollment> enrollments = resultRepo.findEnrolledStudentsByClassId(em, classId);
            return enrollments.stream()
                    .sorted(Comparator.comparing(e -> e.getStudent().getFullName()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy kết quả đã có cho một lớp.
     * Trả về Map: studentId → Result (dùng Stream Collectors.toMap).
     */
    public Map<Long, Result> getResultMap(Long classId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Result> results = resultRepo.findResultsByClassId(em, classId);
            return results.stream()
                    .collect(Collectors.toMap(
                            r -> r.getStudent().getStudentId(),
                            r -> r));
        });
    }

    /**
     * Lưu/cập nhật điểm cho toàn bộ danh sách học viên của một lớp.
     * Sử dụng Stream forEach để xử lý từng record.
     * Tự động tính grade từ score bằng calculateGrade().
     */
    public void saveResults(Long classId, List<ResultRecord> records) throws Exception {
        tx.runInTransaction(em -> {
            Map<Long, Result> existingMap = resultRepo.findResultsByClassId(em, classId)
                    .stream()
                    .collect(Collectors.toMap(r -> r.getStudent().getStudentId(), r -> r));

            ClassEntity classEntity = em.find(ClassEntity.class, classId);
            if (classEntity.getStatus() != ClassEntity.Status.Ongoing) {
                throw new IllegalStateException(
                        "Lớp '" + classEntity.getClassName() + "' không ở trạng thái Ongoing. Không thể nhập điểm.");
            }

            // Xử lý từng record bằng Stream forEach + lambda
            records.stream()
                    .filter(record -> record.score() != null)
                    .forEach(record -> {
                        String grade = calculateGrade(record.score());
                        Result existing = existingMap.get(record.studentId());

                        if (existing != null) {
                            existing.setScore(record.score());
                            existing.setGrade(grade);
                            existing.setComment(record.comment());
                            em.merge(existing);
                        } else {
                            Student student = em.find(Student.class, record.studentId());
                            Result result = new Result();
                            result.setStudent(student);
                            result.setClassEntity(classEntity);
                            result.setScore(record.score());
                            result.setGrade(grade);
                            result.setComment(record.comment());
                            em.persist(result);
                        }
                    });

            return null;
        });
    }

    // ==================== OCP: Grade Calculation ====================

    /**
     * OCP: Tính grade (xếp loại) từ điểm số theo thang điểm 100.
     * Dùng data-driven approach + Stream API:
     * - Thêm/sửa mức điểm mới chỉ cần thay đổi GRADE_RULES, KHÔNG sửa logic.
     */
    public static String calculateGrade(BigDecimal score) {
        if (score == null)
            return "";
        double s = score.doubleValue();
        return GRADE_RULES.stream()
                .filter(rule -> s >= rule.threshold())
                .map(GradeRule::grade)
                .findFirst()
                .orElse("F");
    }

    /**
     * Record class để chứa dữ liệu điểm từ UI.
     */
    public record ResultRecord(Long studentId, BigDecimal score, String comment) {
    }
}
