package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.BranchRepository;

import java.util.List;

public class BranchService {

    private final BranchRepository branchRepo;
    private final TransactionManager tx;

    public BranchService(BranchRepository branchRepo, TransactionManager tx) {
        this.branchRepo = branchRepo;
        this.tx = tx;
    }

    // ==================== CRUD ====================

    public void createBranch(Branch branch) throws Exception {
        tx.runInTransaction(em -> {
            branchRepo.save(em, branch);
            return null;
        });
    }

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

    public void deleteBranch(Long branchId) throws Exception {
        tx.runInTransaction(em -> {
            Branch existing = branchRepo.findById(em, branchId);
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy chi nhánh với ID: " + branchId);
            }

            // Kiểm tra xem chi nhánh có phòng đang Active không (dùng Stream)
            boolean hasActiveRooms = existing.getRooms().stream()
                    .anyMatch(r -> r.getStatus() == Room.Status.Active);
            if (hasActiveRooms) {
                throw new IllegalArgumentException(
                        "Không thể xóa chi nhánh đang có phòng học hoạt động. Hãy chuyển trạng thái sang 'Inactive'.");
            }

            // Kiểm tra chi nhánh có lớp đang hoạt động không (dùng Stream)
            boolean hasActiveClasses = existing.getClasses().stream()
                    .anyMatch(c -> c.getStatus() == ClassEntity.Status.Ongoing
                            || c.getStatus() == ClassEntity.Status.Open
                            || c.getStatus() == ClassEntity.Status.Planned);
            if (hasActiveClasses) {
                throw new IllegalArgumentException(
                        "Không thể xóa chi nhánh đang có lớp học hoạt động. Hãy chuyển trạng thái sang 'Inactive'.");
            }

            branchRepo.delete(em, branchId);
            return null;
        });
    }

    // ==================== QUERY ====================

    public List<Branch> getAllBranches() throws Exception {
        return tx.runInTransaction(em -> branchRepo.findAll(em));
    }

    public Branch getBranchById(Long id) throws Exception {
        return tx.runInTransaction(em -> branchRepo.findById(em, id));
    }

    public List<Branch> getActiveBranches() throws Exception {
        return tx.runInTransaction(em -> branchRepo.findActiveBranches(em));
    }
}
