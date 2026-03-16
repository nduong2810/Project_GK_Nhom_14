package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
import vn.edu.ute.repo.ResultRepository;

import java.util.List;

/**
 * Lớp triển khai của ResultRepository sử dụng JPA.
 * Lớp này triển khai cả hai giao diện GradeEntryRepository và StudentGradeRepository,
 * cung cấp logic cụ thể để truy vấn dữ liệu liên quan đến kết quả học tập.
 */
public class JpaResultRepository implements ResultRepository {

        /**
         * {@inheritDoc}
         * Lấy danh sách lớp học mà giáo viên phụ trách, không bao gồm các lớp đã bị hủy.
         * Sử dụng JOIN FETCH để tải thông tin khóa học liên quan.
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
         * Lấy danh sách học viên đã ghi danh (Enrolled) vào một lớp.
         * Sử dụng JOIN FETCH để tải thông tin học viên liên quan.
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
         * Lấy danh sách kết quả của một lớp học.
         * Sử dụng JOIN FETCH để tải thông tin học viên và lớp học liên quan.
         */
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

        /**
         * {@inheritDoc}
         * Lấy danh sách kết quả học tập của một học viên.
         * Sử dụng JOIN FETCH để tải thông tin lớp học và khóa học liên quan.
         */
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

        /**
         * {@inheritDoc}
         * Lấy danh sách tất cả các lần ghi danh của một học viên.
         * Sử dụng JOIN FETCH để tải thông tin lớp học và khóa học liên quan.
         */
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
