package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository {

    /**
     * Lấy danh sách lớp học mà giáo viên đang phụ trách (status không phải
     * Cancelled).
     */
    List<ClassEntity> findClassesByTeacherId(EntityManager em, Long teacherId) throws Exception;

    /**
     * Lấy danh sách enrollment đang Enrolled của một lớp (để biết danh sách học
     * viên).
     */
    List<Enrollment> findEnrolledStudentsByClassId(EntityManager em, Long classId) throws Exception;

    /**
     * Lấy danh sách điểm danh theo lớp và ngày.
     */
    List<Attendance> findByClassAndDate(EntityManager em, Long classId, LocalDate date) throws Exception;

    /**
     * Lưu mới một bản ghi điểm danh.
     */
    void save(EntityManager em, Attendance attendance) throws Exception;

    /**
     * Cập nhật bản ghi điểm danh.
     */
    void update(EntityManager em, Attendance attendance) throws Exception;
}
