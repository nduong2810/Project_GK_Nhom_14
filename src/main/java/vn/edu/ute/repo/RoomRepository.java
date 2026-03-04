package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Room;

import java.util.List;

public interface RoomRepository {
    List<Room> findAll(EntityManager em) throws Exception;
    Room findById(EntityManager em, Long id) throws Exception;
    void save(EntityManager em, Room room) throws Exception;
    void update(EntityManager em, Room room) throws Exception;
    void delete(EntityManager em, Long id) throws Exception;
    List<Room> findActiveRooms(EntityManager em) throws Exception;
}