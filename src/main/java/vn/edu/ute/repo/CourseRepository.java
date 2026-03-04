package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Course;

import java.util.List;

public interface CourseRepository {
    List<Course> findAll(EntityManager em) throws Exception;
    Course findById(EntityManager em, Long id) throws Exception;
    void save(EntityManager em, Course course) throws Exception;
    void update(EntityManager em, Course course) throws Exception;
    void delete(EntityManager em, Long id) throws Exception; // Bổ sung hàm xóa cho đủ bộ CRUD
    List<Course> findActiveCourses(EntityManager em) throws Exception;
}