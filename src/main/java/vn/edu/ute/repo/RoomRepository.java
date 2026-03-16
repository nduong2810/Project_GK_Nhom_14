package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;

import java.util.List;

/**
 * Giao diện Repository cho việc quản lý dữ liệu của các phòng học (Room).
 * Định nghĩa các phương thức CRUD (Create, Read, Update, Delete) và các truy vấn tùy chỉnh.
 *
 * Nguyên tắc: Giao diện này không phụ thuộc vào bất kỳ công nghệ lưu trữ cụ thể nào.
 * Nó chỉ định nghĩa "cái gì" cần làm, không phải "làm như thế nào".
 * Các lớp triển khai (ví dụ: JpaRoomRepository) sẽ cung cấp cách thực hiện cụ thể.
 */
public interface RoomRepository {

    /**
     * Lấy tất cả các phòng học từ cơ sở dữ liệu.
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách tất cả các phòng học.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Room> findAll(EntityManager em) throws Exception;

    /**
     * Tìm một phòng học dựa trên ID.
     * @param em EntityManager để thực hiện truy vấn.
     * @param id ID của phòng học cần tìm.
     * @return Đối tượng Room nếu tìm thấy, ngược lại trả về null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    Room findById(EntityManager em, Long id) throws Exception;

    /**
     * Lưu một phòng học mới vào cơ sở dữ liệu.
     * @param em EntityManager để thực hiện thao tác.
     * @param room Đối tượng Room cần lưu.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình lưu.
     */
    void save(EntityManager em, Room room) throws Exception;

    /**
     * Cập nhật thông tin của một phòng học đã có.
     * @param em EntityManager để thực hiện thao tác.
     * @param room Đối tượng Room với thông tin đã được cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình cập nhật.
     */
    void update(EntityManager em, Room room) throws Exception;

    /**
     * Xóa một phòng học khỏi cơ sở dữ liệu dựa trên ID.
     * @param em EntityManager để thực hiện thao tác.
     * @param id ID của phòng học cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xóa.
     */
    void delete(EntityManager em, Long id) throws Exception;

    /**
     * Tìm tất cả các phòng học đang ở trạng thái hoạt động (Active).
     * @param em EntityManager để thực hiện truy vấn.
     * @return Danh sách các phòng học đang hoạt động.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình truy vấn.
     */
    List<Room> findActiveRooms(EntityManager em) throws Exception;
}
