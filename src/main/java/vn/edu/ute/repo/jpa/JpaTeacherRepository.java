package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepository;
import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai của TeacherRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Teacher.
 */
public class JpaTeacherRepository implements TeacherRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Teacher> findAll(EntityManager em) {
        return em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Teacher> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Teacher.class, id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Teacher> findByNameContaining(EntityManager em, String name) {
        return em.createQuery("SELECT t FROM Teacher t WHERE t.fullName LIKE :name", Teacher.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Teacher> findActiveTeachers(EntityManager em) {
        return em.createQuery("SELECT t FROM Teacher t WHERE t.status = :status ORDER BY t.fullName", Teacher.class)
                .setParameter("status", Teacher.Status.Active)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Teacher save(EntityManager em, Teacher teacher) {
        if (teacher.getTeacherId() == null) {
            em.persist(teacher);
            return teacher;
        } else {
            return em.merge(teacher);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }
}
