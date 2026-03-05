package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherRepository {
    List<Teacher> findAll(EntityManager em);
    Optional<Teacher> findById(EntityManager em, Long id);
    List<Teacher> findByNameContaining(EntityManager em, String name);
    List<Teacher> findActiveTeachers(EntityManager em);
    Teacher save(EntityManager em, Teacher teacher);
    void deleteById(EntityManager em, Long id);
}