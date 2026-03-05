package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.repo.ScheduleRepository;

import java.util.List;

public class JpaScheduleRepository implements ScheduleRepository {

    @Override
    public List<Schedule> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT s FROM Schedule s " +
                        "JOIN FETCH s.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "LEFT JOIN FETCH ce.teacher " +
                        "LEFT JOIN FETCH s.room " +
                        "ORDER BY s.studyDate ASC, s.startTime ASC",
                Schedule.class)
                .getResultList();
    }

    @Override
    public List<Schedule> findByTeacherId(EntityManager em, Long teacherId) {
        return em.createQuery(
                "SELECT s FROM Schedule s " +
                        "JOIN FETCH s.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "JOIN FETCH ce.teacher t " +
                        "LEFT JOIN FETCH s.room " +
                        "WHERE t.teacherId = :tid " +
                        "ORDER BY s.studyDate ASC, s.startTime ASC",
                Schedule.class)
                .setParameter("tid", teacherId)
                .getResultList();
    }

    @Override
    public List<Schedule> findByStudentId(EntityManager em, Long studentId) {
        return em.createQuery(
                "SELECT DISTINCT s FROM Schedule s " +
                        "JOIN FETCH s.classEntity ce " +
                        "JOIN FETCH ce.course " +
                        "LEFT JOIN FETCH ce.teacher " +
                        "LEFT JOIN FETCH s.room " +
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
