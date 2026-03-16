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
 * Lớp Service chuyên cho nghiệp vụ xem điểm của học viên.
 * SRP (Single Responsibility Principle): Lớp này chỉ chịu trách nhiệm cung cấp dữ liệu điểm cho học viên xem.
 */
public class StudentGradeService {

    private final StudentGradeRepository resultRepo;
    private final TransactionManager tx;

    public StudentGradeService(StudentGradeRepository resultRepo, TransactionManager tx) {
        this.resultRepo = resultRepo;
        this.tx = tx;
    }

    /**
     * Lấy thông tin điểm của một học viên cho tất cả các lớp đã và đang tham gia.
     * <p>
     * Logic:
     * <ol>
     *     <li>Lấy tất cả các lần ghi danh (enrollment) của học viên.</li>
     *     <li>Lấy tất cả các kết quả (result) của học viên và đưa vào một Map để tra cứu nhanh theo `classId`.</li>
     *     <li>Duyệt qua danh sách enrollment, với mỗi enrollment, tìm kết quả tương ứng trong Map.</li>
     *     <li>Tạo một đối tượng `StudentGradeRow` để tổng hợp thông tin từ enrollment và result.</li>
     *     <li>Sắp xếp kết quả: ưu tiên các lớp đang diễn ra ('Ongoing') lên đầu, sau đó sắp xếp theo tên lớp.</li>
     * </ol>
     * @param studentId ID của học viên.
     * @return Danh sách các đối tượng `StudentGradeRow` chứa thông tin điểm để hiển thị.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<StudentGradeRow> getStudentGrades(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            // 1. Lấy tất cả enrollment của học viên (bao gồm mọi trạng thái)
            List<Enrollment> enrollments = resultRepo.findEnrollmentsByStudentId(em, studentId);

            // 2. Lấy tất cả kết quả của học viên và chuyển thành Map<classId, Result> để tra cứu nhanh
            Map<Long, Result> resultMap = resultRepo.findResultsByStudentId(em, studentId)
                    .stream()
                    .collect(Collectors.toMap(
                            r -> r.getClassEntity().getClassId(), // Key là classId
                            r -> r                             // Value là đối tượng Result
                    ));

            // 3. Sử dụng Stream API để xử lý:
            //    - Lọc bỏ các lớp đã bị hủy ('Cancelled').
            //    - Kết hợp thông tin từ enrollment và result (nếu có) để tạo StudentGradeRow.
            //    - Sắp xếp kết quả.
            return enrollments.stream()
                    .filter(e -> e.getClassEntity().getStatus() != ClassEntity.Status.Cancelled)
                    .map(e -> {
                        ClassEntity cls = e.getClassEntity();
                        Result result = resultMap.get(cls.getClassId()); // Tra cứu kết quả trong Map
                        // Tạo đối tượng DTO (Data Transfer Object) để hiển thị
                        return new StudentGradeRow(
                                cls.getClassName(),
                                cls.getCourse().getCourseName(),
                                result != null ? result.getScore() : null, // Lấy điểm nếu có
                                result != null && result.getGrade() != null ? result.getGrade() : "",
                                result != null && result.getComment() != null ? result.getComment() : "",
                                cls.getStatus().toString());
                    })
                    .sorted(Comparator.comparing(
                            // Ưu tiên lớp 'Ongoing' lên đầu (0), các lớp khác sau (1)
                            (StudentGradeRow r) -> r.classStatus().equals("Ongoing") ? 0 : 1)
                            // Nếu cùng nhóm ưu tiên, sắp xếp theo tên lớp
                            .thenComparing(StudentGradeRow::className))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Record (lớp dữ liệu bất biến) để đóng gói thông tin điểm của học viên cho một lớp học.
     * Dùng làm DTO (Data Transfer Object) để truyền dữ liệu lên tầng giao diện.
     */
    public record StudentGradeRow(
            String className, String courseName,
            BigDecimal score, String grade,
            String comment, String classStatus) {
    }
}
