package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;

import java.util.List;

/**
 * Giao diện Repository chuyên biệt cho nghiệp vụ xem điểm của Học viên.
 *
 * ISP (Interface Segregation Principle): Giao diện này được tách ra để chỉ phục vụ cho "client"
 * là các chức năng liên quan đến việc học viên xem điểm (StudentGradeService).
 * Nó không chứa các phương thức mà chỉ giáo viên mới cần (như nhập điểm).
 */
public interface StudentGradeRepository {

    /**
     * Lấy danh sách tất cả kết quả học tập của một học viên.
     * Dữ liệu trả về nên bao gồm cả thông tin về lớp học và khóa học tương ứng để hiển thị.
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các đối tượng Result của học viên đó.
     */
    List<Result> findResultsByStudentId(EntityManager em, Long studentId);

    /**
     * Lấy tất cả các lần ghi danh của một học viên, bao gồm tất cả các trạng thái (đang học, đã nghỉ, đã hoàn thành).
     * Dữ liệu trả về nên được fetch cùng với thông tin lớp (class) và khóa học (course).
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các đối tượng Enrollment của học viên đó.
     */
    List<Enrollment> findEnrollmentsByStudentId(EntityManager em, Long studentId);
}
