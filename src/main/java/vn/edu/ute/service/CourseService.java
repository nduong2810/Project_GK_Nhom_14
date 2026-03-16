package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.CourseRepository;

import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý khóa học (Course).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class CourseService {

    private final CourseRepository courseRepo;
    private final TransactionManager tx;

    /**
     * Constructor để inject các dependency.
     * @param courseRepo Repository để truy xuất dữ liệu khóa học.
     * @param tx Manager để quản lý giao dịch.
     */
    public CourseService(CourseRepository courseRepo, TransactionManager tx) {
        this.courseRepo = courseRepo;
        this.tx = tx;
    }

    /**
     * Tạo một khóa học mới.
     * @param course Đối tượng Course cần tạo.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public void createCourse(Course course) throws Exception {
        tx.runInTransaction(em -> {
            courseRepo.save(em, course);
            return null;
        });
    }

    /**
     * Cập nhật thông tin một khóa học.
     * @param course Đối tượng Course chứa thông tin cập nhật.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy khóa học.
     */
    public void updateCourse(Course course) throws Exception {
        tx.runInTransaction(em -> {
            Course existingCourse = courseRepo.findById(em, course.getCourseId());
            if (existingCourse == null) {
                throw new IllegalArgumentException("Không tìm thấy khóa học với ID: " + course.getCourseId());
            }
            courseRepo.update(em, course);
            return null;
        });
    }

    /**
     * Xóa một khóa học.
     * @param courseId ID của khóa học cần xóa.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy khóa học.
     */
    public void deleteCourse(Long courseId) throws Exception {
        tx.runInTransaction(em -> {
            Course existingCourse = courseRepo.findById(em, courseId);
            if (existingCourse == null) {
                throw new IllegalArgumentException("Không tìm thấy khóa học với ID: " + courseId);
            }
            courseRepo.delete(em, courseId);
            return null;
        });
    }

    /**
     * Lấy danh sách tất cả các khóa học.
     * @return Danh sách các đối tượng Course.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public List<Course> getAllCourses() throws Exception {
        return tx.runInTransaction(em -> courseRepo.findAll(em));
    }

    /**
     * Lấy thông tin một khóa học theo ID.
     * @param id ID của khóa học cần tìm.
     * @return Đối tượng Course nếu tìm thấy.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public Course getCourseById(Long id) throws Exception {
        return tx.runInTransaction(em -> courseRepo.findById(em, id));
    }

    /**
     * Lấy danh sách các khóa học đang hoạt động.
     * @return Danh sách các khóa học có trạng thái 'Active'.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public List<Course> getActiveCourses() throws Exception {
        return tx.runInTransaction(em -> courseRepo.findActiveCourses(em));
    }
}
