package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.repo.ScheduleRepository;

import java.util.List;

/**
 * Lớp triển khai của ScheduleRepository sử dụng JPA.
 * Cung cấp logic cụ thể để truy vấn dữ liệu lịch học.
 */
public class JpaScheduleRepository implements ScheduleRepository {

    /**
     * {@inheritDoc}
     * Sử dụng JOIN FETCH để tải sẵn tất cả các thông tin liên quan (lớp, khóa học, giáo viên, phòng, chi nhánh)
     * để tránh lỗi LazyInitializationException và vấn đề N+1 query.
     * `DISTINCT` được dùng để tránh các bản ghi trùng lặp có thể phát sinh từ các join.
     */
    @Override
    public List<Schedule> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT DISTINCT s FROM Schedule s " +
                        "JOIN FETCH s.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "LEFT JOIN FETCH ce.teacher " +
                        "LEFT JOIN FETCH s.room r " +
                        "LEFT JOIN FETCH r.branch " +
                        "LEFT JOIN FETCH ce.branch " +
                        "ORDER BY s.studyDate ASC, s.startTime ASC",
                Schedule.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Lọc lịch học theo `teacherId` và tải sẵn các thông tin liên quan.
     */
    @Override
    public List<Schedule> findByTeacherId(EntityManager em, Long teacherId) {
        return em.createQuery(
                "SELECT s FROM Schedule s " +
                        "JOIN FETCH s.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "JOIN FETCH ce.teacher t " +
                        "LEFT JOIN FETCH s.room r " +
                        "LEFT JOIN FETCH r.branch " +
                        "LEFT JOIN FETCH ce.branch " +
                        "WHERE t.teacherId = :tid " +
                        "ORDER BY s.studyDate ASC, s.startTime ASC",
                Schedule.class)
                .setParameter("tid", teacherId)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Lọc lịch học theo `studentId` bằng cách join qua bảng `enrollments`.
     * Chỉ lấy lịch của các lớp mà học viên đang có trạng thái `Enrolled`.
     * `DISTINCT` rất quan trọng ở đây để đảm bảo mỗi buổi học chỉ xuất hiện một lần.
     */
    @Override
    public List<Schedule> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                "SELECT DISTINCT s FROM Schedule s " +
                        "JOIN FETCH s.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "LEFT JOIN FETCH ce.teacher " +
                        "LEFT JOIN FETCH s.room r " +
                        "LEFT JOIN FETCH r.branch " +
                        "LEFT JOIN FETCH ce.branch " +
                        "JOIN ce.enrollments e " +
                        "WHERE e.student.studentId = :sid " +
                        "AND e.status = :enrolledStatus " +
                        "ORDER BY s.studyDate ASC, s.startTime ASC",
                Schedule.class)
                .setParameter("sid", studentId)
                .setParameter("enrolledStatus", vn.edu.ute.model.Enrollment.Status.Enrolled)
                .getResultList();
    }
}
