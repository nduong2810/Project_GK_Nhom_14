package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.AttendanceRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Lớp triển khai của AttendanceRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho nghiệp vụ điểm danh.
 */
public class JpaAttendanceRepository implements AttendanceRepository {

    /**
     * {@inheritDoc}
     * Lấy danh sách lớp học mà giáo viên phụ trách, trừ các lớp đã hủy.
     */
    @Override
    public List<ClassEntity> findClassesByTeacherId(EntityManager em, Long teacherId) {
        return em.createQuery(
                "SELECT c FROM ClassEntity c " +
                        "JOIN FETCH c.course " +
                        "WHERE c.teacher.teacherId = :tid " +
                        "AND c.status <> :cancelled " +
                        "ORDER BY c.startDate DESC",
                ClassEntity.class)
                .setParameter("tid", teacherId)
                .setParameter("cancelled", ClassEntity.Status.Cancelled)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Lấy danh sách học viên đang theo học (Enrolled) trong một lớp.
     */
    @Override
    public List<Enrollment> findEnrolledStudentsByClassId(EntityManager em, Long classId) {
        return em.createQuery(
                "SELECT e FROM Enrollment e " +
                        "JOIN FETCH e.student " +
                        "WHERE e.classEntity.classId = :cid " +
                        "AND e.status = :enrolled " +
                        "ORDER BY e.student.fullName ASC",
                Enrollment.class)
                .setParameter("cid", classId)
                .setParameter("enrolled", Enrollment.Status.Enrolled)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Lấy thông tin điểm danh của một lớp vào một ngày cụ thể.
     */
    @Override
    public List<Attendance> findByClassAndDate(EntityManager em, Long classId, LocalDate date) {
        return em.createQuery(
                "SELECT a FROM Attendance a " +
                        "JOIN FETCH a.student " +
                        "JOIN FETCH a.classEntity " +
                        "WHERE a.classEntity.classId = :cid " +
                        "AND a.attendDate = :date " +
                        "ORDER BY a.student.fullName ASC",
                Attendance.class)
                .setParameter("cid", classId)
                .setParameter("date", date)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(EntityManager em, Attendance attendance) {
        em.persist(attendance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(EntityManager em, Attendance attendance) {
        em.merge(attendance);
    }
}
