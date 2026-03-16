package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.repo.StaffRepository;

import java.util.List;
import java.util.Optional;

/**
 * Lớp Service cho nghiệp vụ quản lý nhân viên (Staff).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class StaffService {
    private final StaffRepository staffRepository;
    private final TransactionManager transactionManager;

    /**
     * Constructor để inject các dependency.
     * @param staffRepository Repository để truy xuất dữ liệu nhân viên.
     * @param transactionManager Manager để quản lý giao dịch.
     */
    public StaffService(StaffRepository staffRepository, TransactionManager transactionManager) {
        this.staffRepository = staffRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Lấy danh sách tất cả nhân viên.
     * @return Danh sách các đối tượng Staff.
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public List<Staff> getAllStaff() {
        try {
            return transactionManager.runInTransaction(em -> staffRepository.findAll(em));
        } catch (Exception e) {
            // Bọc ngoại lệ đã kiểm tra (checked exception) thành ngoại lệ không kiểm tra (unchecked exception)
            // để đơn giản hóa việc xử lý lỗi ở các tầng cao hơn.
            throw new RuntimeException(e);
        }
    }

    /**
     * Tìm một nhân viên theo ID.
     * @param id ID của nhân viên cần tìm.
     * @return Optional chứa nhân viên nếu tìm thấy, ngược lại là Optional rỗng.
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public Optional<Staff> findStaffById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> staffRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lưu hoặc cập nhật thông tin một nhân viên.
     * @param staff Đối tượng Staff cần lưu.
     * @return Đối tượng Staff đã được lưu.
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public Staff saveStaff(Staff staff) {
        try {
            return transactionManager.runInTransaction(em -> staffRepository.save(em, staff));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xóa một nhân viên theo ID.
     * @param id ID của nhân viên cần xóa.
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public void deleteStaff(Long id) {
        try {
            transactionManager.runInTransaction(em -> {
                staffRepository.deleteById(em, id);
                return null; // Không cần trả về gì
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
