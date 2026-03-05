package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.StudentRepository;
import java.util.List;
import java.util.Optional;

public class JpaStudentRepository implements StudentRepository {
    @Override
    public List<Student> findAll(EntityManager em) {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    @Override
    public Optional<Student> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Student.class, id));
    }

    @Override
    public List<Student> findByNameContaining(EntityManager em, String name) {
        return em.createQuery("SELECT s FROM Student s WHERE s.fullName LIKE :name", Student.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public Student save(EntityManager em, Student student) {
        if (student.getStudentId() == null) {
            em.persist(student);
            return student;
        } else {
            return em.merge(student);
        }
    }

    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }
}