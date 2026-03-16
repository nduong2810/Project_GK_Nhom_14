package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các chi nhánh (Branch).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface BranchRepository {

    /**
     * Lấy tất cả các chi nhánh từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các chi nhánh.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Branch> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một chi nhánh dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của chi nhánh cần tìm.
     * @return Đối tượng Branch nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Branch findById(EntityManager em, Long id) throws Exception;

    /**
     * Lưu một chi nhánh mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param branch Đối tượng Branch cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Branch branch) throws Exception;

    /**
     * Cập nhật thông tin của một chi nhánh đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param branch Đối tượng Branch với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, Branch branch) throws Exception;

    /**
     * Xóa một chi nhánh khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của chi nhánh cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các chi nhánh đang ở trạng thái hoạt động (Active).
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách các chi nhánh đang hoạt động.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Branch> findActiveBranches(EntityManager em) throws Exception;
}
