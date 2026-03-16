package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;

import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu điểm danh (Attendance).
 * Cung cấp các phương thức cần thiết cho nghiệp vụ điểm danh của giáo viên.
 */
public interface AttendanceRepository {

    /**
     * Lấy danh sách các lớp học mà một giáo viên đang phụ trách.
     * Chỉ lấy các lớp có trạng thái khác 'Cancelled'.
     * @param em EntityManager để thực hiện truy vấn.
     * @param teacherId ID của giáo viên.
     * @return Danh sách các lớp học do giáo viên đó phụ trách.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<ClassEntity> findClassesByTeacherId(EntityManager em, Long teacherId) throws Exception;

    /**
     * Lấy danh sách các học viên đã ghi danh (trạng thái 'Enrolled') vào một lớp học cụ thể.
     * Dùng để hiển thị danh sách lớp cho việc điểm danh.
     * @param em EntityManager để thực hiện truy vấn.
     * @param classId ID của lớp học.
     * @return Danh sách các bản ghi ghi danh (Enrollment) của học viên trong lớp đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Enrollment> findEnrolledStudentsByClassId(EntityManager em, Long classId) throws Exception;

    /**
     * Lấy danh sách điểm danh của một lớp học vào một ngày cụ thể.
     * @param em EntityManager để thực hiện truy vấn.
     * @param classId ID của lớp học.
     * @param date Ngày cần lấy thông tin điểm danh.
     * @return Danh sách các bản ghi điểm danh (Attendance) của lớp đó trong ngày đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Attendance> findByClassAndDate(EntityManager em, Long classId, LocalDate date) throws Exception;

    /**
     * Lưu một bản ghi điểm danh mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param attendance Đối tượng Attendance cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Attendance attendance) throws Exception;

    /**
     * Cập nhật một bản ghi điểm danh đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param attendance Đối tượng Attendance với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, Attendance attendance) throws Exception;
}
