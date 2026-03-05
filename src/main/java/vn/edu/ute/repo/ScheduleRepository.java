package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Schedule;

import java.util.List;

public interface ScheduleRepository {

    /**
     * Lấy tất cả lịch học tại trung tâm (dùng cho Admin/Staff).
     */
    List<Schedule> findAll(EntityManager em) throws Exception;

    /**
     * Lấy lịch dạy theo giáo viên (JOIN schedules → classes.teacher_id).
     */
    List<Schedule> findByTeacherId(EntityManager em, Long teacherId) throws Exception;

    /**
     * Lấy lịch học theo học viên (JOIN schedules → classes →
     * enrollments.student_id).
     * Chỉ lấy enrollment có status = 'Enrolled'.
     */
    List<Schedule> findByStudentId(EntityManager em, Long studentId) throws Exception;
}
