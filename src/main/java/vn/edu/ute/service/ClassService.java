package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.repo.ClassRepository;
import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý lớp học (ClassEntity).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class ClassService {
    private final ClassRepository classRepo;
    private final TransactionManager tx;

    /**
     * Constructor để inject các dependency.
     * @param classRepo Repository để truy xuất dữ liệu lớp học.
     * @param tx Manager để quản lý giao dịch.
     */
    public ClassService(ClassRepository classRepo, TransactionManager tx) {
        this.classRepo = classRepo;
        this.tx = tx;
    }

    /**
     * Tạo một lớp học mới.
     * @param cls Đối tượng ClassEntity cần tạo.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public void createClass(ClassEntity cls) throws Exception {
        tx.runInTransaction(em -> {
            classRepo.save(em, cls);
            return null;
        });
    }

    /**
     * Cập nhật thông tin một lớp học.
     * @param cls Đối tượng ClassEntity chứa thông tin cập nhật.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy lớp học.
     */
    public void updateClass(ClassEntity cls) throws Exception {
        tx.runInTransaction(em -> {
            ClassEntity existing = classRepo.findById(em, cls.getClassId());
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với ID: " + cls.getClassId());
            }
            classRepo.update(em, cls);
            return null;
        });
    }

    /**
     * Xóa một lớp học.
     * @param classId ID của lớp học cần xóa.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy lớp học.
     */
    public void deleteClass(Long classId) throws Exception {
        tx.runInTransaction(em -> {
            ClassEntity existing = classRepo.findById(em, classId);
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với ID: " + classId);
            }
            classRepo.delete(em, classId);
            return null;
        });
    }

    /**
     * Lấy danh sách tất cả các lớp học.
     * @return Danh sách các đối tượng ClassEntity.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public List<ClassEntity> getAllClasses() throws Exception {
        return tx.runInTransaction(em -> classRepo.findAll(em));
    }

    /**
     * Lấy thông tin một lớp học theo ID.
     * @param id ID của lớp học cần tìm.
     * @return Đối tượng ClassEntity nếu tìm thấy.
     * @throws Exception Nếu có lỗi trong quá trình giao dịch.
     */
    public ClassEntity getClassById(Long id) throws Exception {
        return tx.runInTransaction(em -> classRepo.findById(em, id));
    }
}
