package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.CourseRepository;

import java.util.List;

public class CourseService {

    private final CourseRepository courseRepo;
    private final TransactionManager tx;

    // Dependency Injection qua Constructor
    public CourseService(CourseRepository courseRepo, TransactionManager tx) {
        this.courseRepo = courseRepo;
        this.tx = tx;
    }

    public void createCourse(Course course) throws Exception {
        tx.runInTransaction(em -> {
            courseRepo.save(em, course);
            return null;
        });
    }

    public void updateCourse(Course course) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra khóa học có tồn tại không trước khi update
            Course existingCourse = courseRepo.findById(em, course.getCourseId());
            if (existingCourse == null) {
                throw new IllegalArgumentException("Không tìm thấy khóa học với ID: " + course.getCourseId());
            }

            courseRepo.update(em, course);
            return null;
        });
    }

    public void deleteCourse(Long courseId) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra tồn tại trước khi xóa
            Course existingCourse = courseRepo.findById(em, courseId);
            if (existingCourse == null) {
                throw new IllegalArgumentException("Không tìm thấy khóa học với ID: " + courseId);
            }

            courseRepo.delete(em, courseId);
            return null;
        });
    }

    public List<Course> getAllCourses() throws Exception {
        return tx.runInTransaction(em -> courseRepo.findAll(em));
    }

    public Course getCourseById(Long id) throws Exception {
        return tx.runInTransaction(em -> courseRepo.findById(em, id));
    }

    public List<Course> getActiveCourses() throws Exception {
        return tx.runInTransaction(em -> courseRepo.findActiveCourses(em));
    }
}