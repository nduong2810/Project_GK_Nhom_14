package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;

import java.util.List;

/**
 * Giao diện Repository chuyên biệt cho nghiệp vụ nhập điểm của Giáo viên.
 *
 * ISP (Interface Segregation Principle): Đây là một ví dụ về việc áp dụng Nguyên tắc Phân tách Giao diện.
 * Giao diện này được tách ra từ một giao diện lớn hơn (ResultRepository) để chỉ phục vụ cho một "client"
 * cụ thể là các chức năng liên quan đến việc giáo viên nhập điểm (GradeEntryService).
 * Nó không chứa các phương thức mà chỉ học viên mới cần (như xem điểm của riêng mình).
 */
public interface GradeEntryRepository {

    /**
     * Lấy danh sách các lớp học mà một giáo viên đang phụ trách.
     * Không bao gồm các lớp đã bị hủy (Cancelled).
     * @param em EntityManager để thực hiện truy vấn.
     * @param teacherId ID của giáo viên.
     * @return Danh sách các lớp học (ClassEntity) do giáo viên đó phụ trách.
     */
    List<ClassEntity> findClassesByTeacherId(EntityManager em, Long teacherId);

    /**
     * Lấy danh sách các học viên đã ghi danh (trạng thái 'Enrolled') vào một lớp học cụ thể.
     * Dùng để hiển thị danh sách sinh viên khi nhập điểm.
     * @param em EntityManager để thực hiện truy vấn.
     * @param classId ID của lớp học.
     * @return Danh sách các bản ghi ghi danh (Enrollment) của học viên trong lớp đó.
     */
    List<Enrollment> findEnrolledStudentsByClassId(EntityManager em, Long classId);

    /**
     * Lấy danh sách tất cả các kết quả (điểm số) đã có của một lớp học.
     * @param em EntityManager để thực hiện truy vấn.
     * @param classId ID của lớp học.
     * @return Danh sách các đối tượng Result của lớp học đó.
     */
    List<Result> findResultsByClassId(EntityManager em, Long classId);
}
