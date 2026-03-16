package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepository;

import java.util.List;

/**
 * Lớp triển khai (implementation) của RoomRepository sử dụng JPA (Java Persistence API).
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Room.
 */
public class JpaRoomRepository implements RoomRepository {

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL, lấy tất cả phòng học và thông tin chi nhánh liên quan.
     */
    @Override
    public List<Room> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT r FROM Room r " +
                "LEFT JOIN FETCH r.branch " + // Sử dụng FETCH để tránh vấn đề N+1 query
                "ORDER BY r.roomName",
                Room.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `find` của EntityManager.
     */
    @Override
    public Room findById(EntityManager em, Long id) {
        return em.find(Room.class, id);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `persist` của EntityManager để lưu đối tượng mới.
     */
    @Override
    public void save(EntityManager em, Room room) {
        em.persist(room);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `merge` của EntityManager để cập nhật đối tượng đã có.
     */
    @Override
    public void update(EntityManager em, Room room) {
        em.merge(room);
    }

    /**
     * {@inheritDoc}
     * Tìm đối tượng trước khi xóa bằng phương thức `remove` của EntityManager.
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Room r = em.find(Room.class, id);
        if (r != null) {
            em.remove(r);
        }
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL, lọc các phòng có trạng thái 'Active'.
     */
    @Override
    public List<Room> findActiveRooms(EntityManager em) {
        return em.createQuery(
                "SELECT r FROM Room r " +
                "LEFT JOIN FETCH r.branch " +
                "WHERE r.status = :status " +
                "ORDER BY r.roomName",
                Room.class)
                .setParameter("status", Room.Status.Active)
                .getResultList();
    }
}
