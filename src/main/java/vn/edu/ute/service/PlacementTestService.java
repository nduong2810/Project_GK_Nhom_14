package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.repo.PlacementTestRepository;

import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý bài kiểm tra đầu vào (PlacementTest).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class PlacementTestService {
    private final PlacementTestRepository testRepo;
    private final TransactionManager tx;

    public PlacementTestService(PlacementTestRepository testRepo, TransactionManager tx) {
        this.testRepo = testRepo;
        this.tx = tx;
    }

    /**
     * Tạo một bản ghi bài kiểm tra mới.
     * @param test Đối tượng PlacementTest cần tạo.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void createTest(PlacementTest test) throws Exception {
        tx.runInTransaction(em -> {
            testRepo.save(em, test);
            return null;
        });
    }

    /**
     * Cập nhật thông tin một bài kiểm tra.
     * @param test Đối tượng PlacementTest chứa thông tin cập nhật.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void updateTest(PlacementTest test) throws Exception {
        tx.runInTransaction(em -> {
            testRepo.update(em, test);
            return null;
        });
    }

    /**
     * Xóa một bài kiểm tra.
     * @param id ID của bài kiểm tra cần xóa.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void deleteTest(Long id) throws Exception {
        tx.runInTransaction(em -> {
            testRepo.delete(em, id);
            return null;
        });
    }

    /**
     * Lấy danh sách tất cả các bài kiểm tra.
     * @return Danh sách các đối tượng PlacementTest.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<PlacementTest> getAllTests() throws Exception {
        return tx.runInTransaction(em -> testRepo.findAll(em));
    }
}
