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
 * Lớp Service chuyên cho nghiệp vụ nhập điểm của giáo viên.
 * SRP (Single Responsibility Principle): Lớp này chỉ chịu trách nhiệm cho các logic liên quan đến việc giáo viên nhập điểm.
 * OCP (Open/Closed Principle): Việc tính toán xếp loại (calculateGrade) được thiết kế để dễ dàng mở rộng (thêm/sửa thang điểm)
 * mà không cần sửa đổi mã nguồn của phương thức, nhờ vào việc sử dụng cấu trúc dữ liệu (GRADE_RULES) và Stream API.
 */
public class GradeEntryService {

    private final GradeEntryRepository resultRepo;
    private final TransactionManager tx;

    /**
     * OCP: Danh sách các quy tắc xếp loại (GradeRule).
     * Dữ liệu này được tách biệt khỏi logic, cho phép thêm/sửa các mức điểm mà KHÔNG cần sửa phương thức `calculateGrade`.
     * Các quy tắc phải được sắp xếp theo ngưỡng điểm (threshold) từ cao đến thấp.
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

    /**
     * Record để định nghĩa một quy tắc xếp loại, bao gồm ngưỡng điểm và nhãn xếp loại.
     * Tuân thủ nguyên tắc OCP.
     */
    private record GradeRule(double threshold, String grade) {
    }

    public GradeEntryService(GradeEntryRepository resultRepo, TransactionManager tx) {
        this.resultRepo = resultRepo;
        this.tx = tx;
    }

    /**
     * Lấy danh sách các lớp học đang diễn ra ('Ongoing') của một giáo viên.
     * Chỉ cho phép nhập điểm cho các lớp đang diễn ra.
     * @param teacherId ID của giáo viên.
     * @return Danh sách ClassEntity đã được lọc và sắp xếp.
     * @throws Exception nếu có lỗi giao dịch.
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
     * Lấy danh sách học viên đang ghi danh ('Enrolled') trong một lớp.
     * @param classId ID của lớp học.
     * @return Danh sách Enrollment đã được sắp xếp theo tên học viên.
     * @throws Exception nếu có lỗi giao dịch.
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
     * Lấy kết quả điểm đã nhập cho một lớp, trả về dưới dạng Map để tra cứu nhanh.
     * @param classId ID của lớp học.
     * @return Map với key là `studentId` và value là đối tượng `Result`.
     * @throws Exception nếu có lỗi giao dịch.
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
     * Lưu hoặc cập nhật điểm cho một danh sách học viên của một lớp.
     * Tự động tính toán xếp loại (grade) từ điểm số (score).
     * @param classId ID của lớp học.
     * @param records Danh sách các bản ghi điểm (dưới dạng `ResultRecord`).
     * @throws Exception nếu có lỗi giao dịch.
     * @throws IllegalStateException nếu lớp học không ở trạng thái 'Ongoing'.
     */
    public void saveResults(Long classId, List<ResultRecord> records) throws Exception {
        tx.runInTransaction(em -> {
            Map<Long, Result> existingMap = getResultMap(classId);

            ClassEntity classEntity = em.find(ClassEntity.class, classId);
            if (classEntity.getStatus() != ClassEntity.Status.Ongoing) {
                throw new IllegalStateException(
                        "Lớp '" + classEntity.getClassName() + "' không ở trạng thái Ongoing. Không thể nhập điểm.");
            }

            // Xử lý từng bản ghi điểm, chỉ xử lý những bản ghi có điểm số không null.
            records.stream()
                    .filter(record -> record.score() != null)
                    .forEach(record -> {
                        String grade = calculateGrade(record.score());
                        Result existing = existingMap.get(record.studentId());

                        if (existing != null) {
                            // Nếu đã có điểm -> Cập nhật
                            existing.setScore(record.score());
                            existing.setGrade(grade);
                            existing.setComment(record.comment());
                            em.merge(existing);
                        } else {
                            // Nếu chưa có điểm -> Tạo mới
                            Student student = em.getReference(Student.class, record.studentId());
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

    // ==================== OCP: Tính toán Xếp loại ====================

    /**
     * OCP: Tính toán xếp loại (grade) từ điểm số (score) theo thang điểm 100.
     * Sử dụng phương pháp data-driven và Stream API:
     * Tìm quy tắc đầu tiên trong `GRADE_RULES` có ngưỡng điểm nhỏ hơn hoặc bằng điểm số của học viên.
     * Nếu không tìm thấy quy tắc nào (điểm quá thấp), trả về "F".
     * @param score Điểm số cần xếp loại.
     * @return Chuỗi ký tự đại diện cho xếp loại (ví dụ: "A+", "B", "F").
     */
    public static String calculateGrade(BigDecimal score) {
        if (score == null)
            return "";
        double s = score.doubleValue();
        return GRADE_RULES.stream()
                .filter(rule -> s >= rule.threshold())
                .map(GradeRule::grade)
                .findFirst()
                .orElse("F"); // Mặc định là "F" nếu không đạt ngưỡng nào
    }

    /**
     * Record để đóng gói dữ liệu điểm nhận từ UI.
     */
    public record ResultRecord(Long studentId, BigDecimal score, String comment) {
    }
}
