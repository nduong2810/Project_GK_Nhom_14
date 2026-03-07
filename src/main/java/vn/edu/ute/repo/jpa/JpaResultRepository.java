package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.repo.ResultRepository;

import java.util.List;

public class JpaResultRepository implements ResultRepository {

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

        @Override
        public List<Result> findResultsByClassId(EntityManager em, Long classId) {
                return em.createQuery(
                                "SELECT r FROM Result r " +
                                                "JOIN FETCH r.student " +
                                                "JOIN FETCH r.classEntity " +
                                                "WHERE r.classEntity.classId = :cid " +
                                                "ORDER BY r.student.fullName ASC",
                                Result.class)
                                .setParameter("cid", classId)
                                .getResultList();
        }

        @Override
        public List<Result> findResultsByStudentId(EntityManager em, Long studentId) {
                return em.createQuery(
                                "SELECT r FROM Result r " +
                                                "JOIN FETCH r.classEntity c " +
                                                "JOIN FETCH c.course " +
                                                "WHERE r.student.studentId = :sid " +
                                                "ORDER BY c.startDate DESC",
                                Result.class)
                                .setParameter("sid", studentId)
                                .getResultList();
        }

        @Override
        public List<Enrollment> findEnrollmentsByStudentId(EntityManager em, Long studentId) {
                return em.createQuery(
                                "SELECT e FROM Enrollment e " +
                                                "JOIN FETCH e.classEntity c " +
                                                "JOIN FETCH c.course " +
                                                "WHERE e.student.studentId = :sid " +
                                                "ORDER BY c.startDate DESC",
                                Enrollment.class)
                                .setParameter("sid", studentId)
                                .getResultList();
        }
}
