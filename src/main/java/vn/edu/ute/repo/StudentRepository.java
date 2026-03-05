package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    List<Student> findAll(EntityManager em);
    Optional<Student> findById(EntityManager em, Long id);
    List<Student> findByNameContaining(EntityManager em, String name);
    Student save(EntityManager em, Student student);
    void deleteById(EntityManager em, Long id);
}