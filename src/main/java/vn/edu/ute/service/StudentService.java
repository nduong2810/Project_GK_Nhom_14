package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.StudentRepository;
import java.util.List;
import java.util.Optional;

/**
 * Lớp Service cho nghiệp vụ quản lý học viên (Student).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class StudentService {
    private final StudentRepository studentRepository;
    private final TransactionManager transactionManager;

    public StudentService(StudentRepository studentRepository, TransactionManager transactionManager) {
        this.studentRepository = studentRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Lấy danh sách tất cả học viên.
     * @return Danh sách các đối tượng Student.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public List<Student> getAllStudents() {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.findAll(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tìm một học viên theo ID.
     * @param id ID của học viên.
     * @return Optional chứa học viên nếu tìm thấy.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public Optional<Student> findStudentById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tìm kiếm học viên theo tên (chứa một chuỗi con).
     * @param name Tên hoặc một phần tên cần tìm.
     * @return Danh sách các học viên khớp với tiêu chí.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public List<Student> findStudentByName(String name) {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.findByNameContaining(em, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lưu hoặc cập nhật thông tin một học viên.
     * @param student Đối tượng Student cần lưu.
     * @return Đối tượng Student đã được lưu.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public Student saveStudent(Student student) {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.save(em, student));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xóa một học viên theo ID.
     * @param id ID của học viên cần xóa.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public void deleteStudent(Long id) {
        try {
            transactionManager.runInTransaction(em -> {
                studentRepository.deleteById(em, id);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy danh sách các học viên đang hoạt động.
     * @return Danh sách các học viên có trạng thái 'Active'.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Student> getActiveStudents() throws Exception {
        return transactionManager.runInTransaction(em -> studentRepository.findActiveStudents(em));
    }
}
