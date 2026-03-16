package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Promotion;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các chương trình khuyến mãi (Promotion).
 * Định nghĩa các phương thức CRUD và các truy vấn tùy chỉnh.
 */
public interface PromotionRepository {

    /**
     * Lấy tất cả các chương trình khuyến mãi từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các khuyến mãi.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Promotion> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một chương trình khuyến mãi dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của khuyến mãi cần tìm.
     * @return Đối tượng Promotion nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Promotion findById(EntityManager em, Long id) throws Exception;

    /**
     * Lưu một chương trình khuyến mãi mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param promotion Đối tượng Promotion cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Promotion promotion) throws Exception;

    /**
     * Cập nhật thông tin của một chương trình khuyến mãi đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param promotion Đối tượng Promotion với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, Promotion promotion) throws Exception;

    /**
     * Xóa một chương trình khuyến mãi khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của khuyến mãi cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các chương trình khuyến mãi đang ở trạng thái hoạt động (Active).
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách các khuyến mãi đang hoạt động.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Promotion> findActivePromotions(EntityManager em) throws Exception;
}
