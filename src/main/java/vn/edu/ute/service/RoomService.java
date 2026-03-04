package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepository;

import java.util.List;

public class RoomService {

    private final RoomRepository roomRepo;
    private final TransactionManager tx;

    public RoomService(RoomRepository roomRepo, TransactionManager tx) {
        this.roomRepo = roomRepo;
        this.tx = tx;
    }

    public void createRoom(Room room) throws Exception {
        tx.runInTransaction(em -> {
            roomRepo.save(em, room);
            // Nếu có logic phức tạp (như tăng giảm số lượng), bạn sẽ viết tiếp ở đây
            return null;
        });
    }

    public void updateRoom(Room room) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra xem phòng có tồn tại không trước khi update (
            Room existingRoom = roomRepo.findById(em, room.getRoomId());
            if (existingRoom == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng học với ID: " + room.getRoomId());
            }

            roomRepo.update(em, room);
            return null;
        });
    }

    public void deleteRoom(Long roomId) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra tồn tại trước khi xóa
            Room existingRoom = roomRepo.findById(em, roomId);
            if (existingRoom == null) {
                throw new IllegalArgumentException("Không tìm thấy phòng học với ID: " + roomId);
            }

            roomRepo.delete(em, roomId);
            return null;
        });
    }

    public List<Room> getAllRooms() throws Exception {
        return tx.runInTransaction(em -> roomRepo.findAll(em));
    }

    public Room getRoomById(Long id) throws Exception {
        return tx.runInTransaction(em -> roomRepo.findById(em, id));
    }

    public List<Room> getActiveRooms() throws Exception {
        return tx.runInTransaction(em -> roomRepo.findActiveRooms(em));
    }
}