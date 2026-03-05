package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import java.util.List;

public interface TeacherRepository {
    List<Teacher> findActiveTeachers(EntityManager em) throws Exception;
}