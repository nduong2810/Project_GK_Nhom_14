package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepository;
import java.util.List;

public class JpaTeacherRepository implements TeacherRepository {
    @Override
    public List<Teacher> findActiveTeachers(EntityManager em) {
        // Chỉ lấy những giáo viên đang "Active" để phân công vào lớp
        return em.createQuery("SELECT t FROM Teacher t WHERE t.status = :status ORDER BY t.fullName", Teacher.class)
                .setParameter("status", Teacher.Status.Active)
                .getResultList();
    }
}