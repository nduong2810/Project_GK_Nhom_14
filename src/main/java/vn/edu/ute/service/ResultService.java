package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.ResultRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultService {

    private final ResultRepository resultRepo;
    private final TransactionManager tx;

    public ResultService(ResultRepository resultRepo, TransactionManager tx) {
        this.resultRepo = resultRepo;
        this.tx = tx;
    }

    // ==================== TEACHER: Nhập điểm ====================

    /**
     * Lấy danh sách lớp Ongoing của giáo viên (chỉ cho phép nhập điểm lớp Ongoing).
     * Dùng Stream filter + sorted.
     */
    public List<ClassEntity> getOngoingClassesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            List<ClassEntity> allClasses = resultRepo.findClassesByTeacherId(em, teacherId);
            // Stream: lọc chỉ lấy lớp Ongoing, sắp xếp theo tên lớp
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
            // Lấy result đã có cho lớp → Map để lookup nhanh
            Map<Long, Result> existingMap = resultRepo.findResultsByClassId(em, classId)
                    .stream()
                    .collect(Collectors.toMap(r -> r.getStudent().getStudentId(), r -> r));

            // Load ClassEntity và kiểm tra trạng thái
            ClassEntity classEntity = em.find(ClassEntity.class, classId);
            if (classEntity.getStatus() != ClassEntity.Status.Ongoing) {
                throw new IllegalStateException(
                        "Lớp '" + classEntity.getClassName() + "' không ở trạng thái Ongoing. Không thể nhập điểm.");
            }

            // Xử lý từng record bằng Stream forEach + lambda
            records.stream()
                    .filter(record -> record.score() != null) // Chỉ lưu khi có điểm
                    .forEach(record -> {
                        String grade = calculateGrade(record.score());
                        Result existing = existingMap.get(record.studentId());

                        if (existing != null) {
                            // Cập nhật record đã tồn tại
                            existing.setScore(record.score());
                            existing.setGrade(grade);
                            existing.setComment(record.comment());
                            em.merge(existing);
                        } else {
                            // Tạo mới
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

    // ==================== STUDENT: Xem điểm ====================

    /**
     * Lấy toàn bộ lớp mà học viên tham gia (có hoặc chưa có điểm).
     * Dùng Stream: lấy enrollments → merge với results (nếu có) → tạo
     * StudentGradeRow.
     * Sắp xếp: lớp mới nhất lên trước.
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

    // ==================== UTILITIES ====================

    /**
     * Tính grade (xếp loại) từ điểm số theo thang điểm 100.
     * Sử dụng pattern matching style với lambda-friendly approach.
     */
    public static String calculateGrade(BigDecimal score) {
        if (score == null)
            return "";
        double s = score.doubleValue();
        if (s >= 90)
            return "A+";
        if (s >= 85)
            return "A";
        if (s >= 80)
            return "B+";
        if (s >= 70)
            return "B";
        if (s >= 65)
            return "C+";
        if (s >= 55)
            return "C";
        if (s >= 50)
            return "D+";
        if (s >= 40)
            return "D";
        return "F";
    }

    /**
     * Record class để chứa dữ liệu điểm từ UI.
     */
    public record ResultRecord(Long studentId, BigDecimal score, String comment) {
    }
}
