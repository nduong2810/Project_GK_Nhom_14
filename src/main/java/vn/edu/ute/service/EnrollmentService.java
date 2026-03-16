package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.EnrollmentRepository;
import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý ghi danh (Enrollment).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepo;
    private final TransactionManager tx;

    public EnrollmentService(EnrollmentRepository enrollmentRepo, TransactionManager tx) {
        this.enrollmentRepo = enrollmentRepo;
        this.tx = tx;
    }

    /**
     * Tạo một bản ghi ghi danh mới.
     * @param enrollment Đối tượng Enrollment cần tạo.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void createEnrollment(Enrollment enrollment) throws Exception {
        tx.runInTransaction(em -> {
            enrollmentRepo.save(em, enrollment);
            return null;
        });
    }

    /**
     * Cập nhật thông tin một bản ghi ghi danh.
     * @param enrollment Đối tượng Enrollment chứa thông tin cập nhật.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void updateEnrollment(Enrollment enrollment) throws Exception {
        tx.runInTransaction(em -> {
            enrollmentRepo.update(em, enrollment);
            return null;
        });
    }

    /**
     * Xóa một bản ghi ghi danh.
     * @param id ID của bản ghi cần xóa.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void deleteEnrollment(Long id) throws Exception {
        tx.runInTransaction(em -> {
            enrollmentRepo.delete(em, id);
            return null;
        });
    }

    /**
     * Lấy danh sách tất cả các bản ghi ghi danh.
     * @return Danh sách các đối tượng Enrollment.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Enrollment> getAllEnrollments() throws Exception {
        return tx.runInTransaction(em -> enrollmentRepo.findAll(em));
    }
}
