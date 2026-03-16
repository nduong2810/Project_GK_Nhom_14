package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Schedule;

import java.util.List;

/**
 * Giao diện Repository cho việc truy xuất dữ liệu lịch học (Schedule).
 * Cung cấp các phương thức để lấy lịch học dựa trên các vai trò người dùng khác nhau.
 */
public interface ScheduleRepository {

    /**
     * Lấy tất cả lịch học tại trung tâm.
     * Phương thức này thường được sử dụng bởi Admin hoặc Nhân viên (Staff) để có cái nhìn tổng quan.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các lịch học.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Schedule> findAll(EntityManager em) throws Exception;

    /**
     * Lấy lịch dạy của một giáo viên cụ thể.
     * Truy vấn này sẽ kết (JOIN) từ bảng `schedules` qua `classes` để lọc theo `teacher_id`.
     * @param em EntityManager để thực hiện truy vấn.
     * @param teacherId ID của giáo viên.
     * @return Danh sách các lịch học mà giáo viên đó phụ trách.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Schedule> findByTeacherId(EntityManager em, Long teacherId) throws Exception;

    /**
     * Lấy lịch học của một học viên cụ thể.
     * Truy vấn này sẽ kết (JOIN) từ `schedules` qua `classes` đến `enrollments` để lọc theo `student_id`.
     * Chỉ những lớp học mà học viên đang có trạng thái ghi danh là 'Enrolled' mới được tính.
     * @param em EntityManager để thực hiện truy vấn.
     * @param studentId ID của học viên.
     * @return Danh sách các lịch học của học viên đó.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Schedule> findByStudentId(EntityManager em, Long studentId) throws Exception;
}
