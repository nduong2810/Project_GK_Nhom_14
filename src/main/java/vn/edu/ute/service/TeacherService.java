package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepository;
import java.util.List;
import java.util.Optional;

/**
 * Lớp Service cho nghiệp vụ quản lý giáo viên (Teacher).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final TransactionManager transactionManager;

    public TeacherService(TeacherRepository teacherRepository, TransactionManager transactionManager) {
        this.teacherRepository = teacherRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Lấy danh sách tất cả giáo viên.
     * @return Danh sách các đối tượng Teacher.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public List<Teacher> getAllTeachers() {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findAll(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tìm một giáo viên theo ID.
     * @param id ID của giáo viên.
     * @return Optional chứa giáo viên nếu tìm thấy.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public Optional<Teacher> findTeacherById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tìm kiếm giáo viên theo tên.
     * @param name Tên hoặc một phần tên cần tìm.
     * @return Danh sách các giáo viên khớp với tiêu chí.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public List<Teacher> findTeacherByName(String name) {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findByNameContaining(em, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy danh sách các giáo viên đang hoạt động.
     * @return Danh sách các giáo viên có trạng thái 'Active'.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public List<Teacher> getActiveTeachers() {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findActiveTeachers(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lưu hoặc cập nhật thông tin một giáo viên.
     * @param teacher Đối tượng Teacher cần lưu.
     * @return Đối tượng Teacher đã được lưu.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public Teacher saveTeacher(Teacher teacher) {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.save(em, teacher));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xóa một giáo viên theo ID.
     * @param id ID của giáo viên cần xóa.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public void deleteTeacher(Long id) {
        try {
            transactionManager.runInTransaction(em -> {
                teacherRepository.deleteById(em, id);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
