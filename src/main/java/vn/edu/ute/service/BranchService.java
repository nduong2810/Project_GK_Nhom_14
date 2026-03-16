package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.BranchRepository;

import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý chi nhánh (Branch).
 * Chứa các logic nghiệp vụ, bao gồm cả các quy tắc kiểm tra trước khi thực hiện thao tác dữ liệu.
 */
public class BranchService {

    private final BranchRepository branchRepo;
    private final TransactionManager tx;

    /**
     * Constructor để inject các dependency.
     * @param branchRepo Repository để truy xuất dữ liệu chi nhánh.
     * @param tx Manager để quản lý giao dịch.
     */
    public BranchService(BranchRepository branchRepo, TransactionManager tx) {
        this.branchRepo = branchRepo;
        this.tx = tx;
    }

    // ==================== CRUD Operations ====================

    /**
     * Tạo một chi nhánh mới.
     * @param branch Đối tượng Branch cần tạo.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public void createBranch(Branch branch) throws Exception {
        tx.runInTransaction(em -> {
            branchRepo.save(em, branch);
            return null;
        });
    }

    /**
     * Cập nhật thông tin một chi nhánh.
     * @param branch Đối tượng Branch chứa thông tin cập nhật.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy chi nhánh.
     */
    public void updateBranch(Branch branch) throws Exception {
        tx.runInTransaction(em -> {
            Branch existing = branchRepo.findById(em, branch.getBranchId());
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy chi nhánh với ID: " + branch.getBranchId());
            }
            branchRepo.update(em, branch);
            return null;
        });
    }

    /**
     * Xóa một chi nhánh.
     * Phương thức này chứa logic nghiệp vụ quan trọng: chỉ cho phép xóa nếu chi nhánh không có
     * phòng học hoặc lớp học nào đang hoạt động.
     * @param branchId ID của chi nhánh cần xóa.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy chi nhánh hoặc nếu chi nhánh vẫn còn phòng/lớp hoạt động.
     */
    public void deleteBranch(Long branchId) throws Exception {
        tx.runInTransaction(em -> {
            // Lấy thông tin chi nhánh và các mối quan hệ (phòng, lớp)
            Branch existing = branchRepo.findById(em, branchId);
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy chi nhánh với ID: " + branchId);
            }

            // Kiểm tra xem chi nhánh có phòng nào đang ở trạng thái 'Active' không.
            boolean hasActiveRooms = existing.getRooms().stream()
                    .anyMatch(r -> r.getStatus() == Room.Status.Active);
            if (hasActiveRooms) {
                throw new IllegalArgumentException(
                        "Không thể xóa chi nhánh đang có phòng học hoạt động. Hãy chuyển trạng thái sang 'Inactive'.");
            }

            // Kiểm tra xem chi nhánh có lớp nào đang hoạt động (Planned, Open, Ongoing) không.
            boolean hasActiveClasses = existing.getClasses().stream()
                    .anyMatch(c -> c.getStatus() == ClassEntity.Status.Ongoing
                            || c.getStatus() == ClassEntity.Status.Open
                            || c.getStatus() == ClassEntity.Status.Planned);
            if (hasActiveClasses) {
                throw new IllegalArgumentException(
                        "Không thể xóa chi nhánh đang có lớp học hoạt động. Hãy chuyển trạng thái sang 'Inactive'.");
            }

            // Nếu tất cả các điều kiện được thỏa mãn, tiến hành xóa.
            branchRepo.delete(em, branchId);
            return null;
        });
    }

    // ==================== Query Operations ====================

    /**
     * Lấy danh sách tất cả các chi nhánh.
     * @return Danh sách các đối tượng Branch.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public List<Branch> getAllBranches() throws Exception {
        return tx.runInTransaction(em -> branchRepo.findAll(em));
    }

    /**
     * Lấy thông tin một chi nhánh theo ID.
     * @param id ID của chi nhánh cần tìm.
     * @return Đối tượng Branch nếu tìm thấy.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public Branch getBranchById(Long id) throws Exception {
        return tx.runInTransaction(em -> branchRepo.findById(em, id));
    }

    /**
     * Lấy danh sách các chi nhánh đang hoạt động.
     * @return Danh sách các chi nhánh có trạng thái 'Active'.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public List<Branch> getActiveBranches() throws Exception {
        return tx.runInTransaction(em -> branchRepo.findActiveBranches(em));
    }
}
