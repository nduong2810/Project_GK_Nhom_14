package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepository;

import java.util.List;

public class JpaRoomRepository implements RoomRepository {

    @Override
    public List<Room> findAll(EntityManager em) {
        return em.createQuery("SELECT r FROM Room r ORDER BY r.roomName", Room.class)
                .getResultList();
    }

    @Override
    public Room findById(EntityManager em, Long id) {
        return em.find(Room.class, id);
    }

    @Override
    public void save(EntityManager em, Room room) {
        em.persist(room);
    }

    @Override
    public void update(EntityManager em, Room room) {
        em.merge(room);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Room r = em.find(Room.class, id);
        if (r != null) {
            em.remove(r);
        }
    }

    @Override
    public List<Room> findActiveRooms(EntityManager em) {
        return em.createQuery("SELECT r FROM Room r WHERE r.status = :status ORDER BY r.roomName", Room.class)
                .setParameter("status", Room.Status.Active)
                .getResultList();
    }
}