package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepository;
import java.util.List;
import java.util.Optional;

public class JpaTeacherRepository implements TeacherRepository {
    @Override
    public List<Teacher> findAll(EntityManager em) {
        return em.createQuery("SELECT t FROM Teacher t", Teacher.class).getResultList();
    }

    @Override
    public Optional<Teacher> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Teacher.class, id));
    }

    @Override
    public List<Teacher> findByNameContaining(EntityManager em, String name) {
        return em.createQuery("SELECT t FROM Teacher t WHERE t.fullName LIKE :name", Teacher.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public List<Teacher> findActiveTeachers(EntityManager em) {
        return em.createQuery("SELECT t FROM Teacher t WHERE t.status = :status ORDER BY t.fullName", Teacher.class)
                .setParameter("status", Teacher.Status.Active)
                .getResultList();
    }

    @Override
    public Teacher save(EntityManager em, Teacher teacher) {
        if (teacher.getTeacherId() == null) {
            em.persist(teacher);
            return teacher;
        } else {
            return em.merge(teacher);
        }
    }

    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }
}