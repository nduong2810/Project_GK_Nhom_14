package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepository;

import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý phòng học (Room).
 * Chứa các logic nghiệp vụ, điều phối các thao tác dữ liệu thông qua RoomRepository
 * và quản lý giao dịch (transaction) thông qua TransactionManager.
 */
public class RoomService {

    private final RoomRepository roomRepo;
    private final TransactionManager tx;

    /**
     * Constructor để inject (tiêm) các dependency cần thiết.
     * @param roomRepo Repository để truy xuất dữ liệu phòng học.
     * @param tx Manager để quản lý các giao dịch cơ sở dữ liệu.
     */
    public RoomService(RoomRepository roomRepo, TransactionManager tx) {
        this.roomRepo = roomRepo;
        this.tx = tx;
    }

    /**
     * Tạo một phòng học mới.
     * @param room Đối tượng Room cần tạo.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public void createRoom(Room room) throws Exception {
        tx.runInTransaction(em -> {
            roomRepo.save(em, room);
            // Nếu có logic nghiệp vụ phức tạp hơn (ví dụ: ghi log, gửi thông báo),
            // nó sẽ được thêm vào đây.
            return null; // Không cần trả về gì từ giao dịch này.
        });
    }

    /**
     * Cập nhật thông tin một phòng học đã có.
     * @param room Đối tượng Room chứa thông tin cập nhật.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy phòng học với ID tương ứng.
     */
    public void updateRoom(Room room) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra xem phòng có tồn tại không trước khi cập nhật.
            Room existingRoom = roomRepo.findById(em, room.getRoomId());
            if (existingRoom == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng học với ID: " + room.getRoomId());
            }
            roomRepo.update(em, room);
            return null;
        });
    }

    /**
     * Xóa một phòng học.
     * @param roomId ID của phòng học cần xóa.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình giao dịch.
     * @throws IllegalArgumentException Nếu không tìm thấy phòng học với ID tương ứng.
     */
    public void deleteRoom(Long roomId) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra sự tồn tại trước khi xóa để đảm bảo an toàn.
            Room existingRoom = roomRepo.findById(em, roomId);
            if (existingRoom == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng học với ID: " + roomId);
            }
            roomRepo.delete(em, roomId);
            return null;
        });
    }

    /**
     * Lấy danh sách tất cả các phòng học.
     * @return Danh sách các đối tượng Room.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public List<Room> getAllRooms() throws Exception {
        return tx.runInTransaction(em -> roomRepo.findAll(em));
    }

    /**
     * Lấy thông tin một phòng học theo ID.
     * @param id ID của phòng học cần tìm.
     * @return Đối tượng Room nếu tìm thấy, ngược lại là null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public Room getRoomById(Long id) throws Exception {
        return tx.runInTransaction(em -> roomRepo.findById(em, id));
    }

    /**
     * Lấy danh sách các phòng học đang hoạt động.
     * @return Danh sách các phòng học có trạng thái 'Active'.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình giao dịch.
     */
    public List<Room> getActiveRooms() throws Exception {
        return tx.runInTransaction(em -> roomRepo.findActiveRooms(em));
    }
}
