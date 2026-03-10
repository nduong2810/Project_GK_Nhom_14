package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Notification;

import java.util.List;

public interface NotificationRepository {
    List<Notification> findAll(EntityManager em) throws Exception;

    Notification findById(EntityManager em, Long id) throws Exception;

    void save(EntityManager em, Notification notification) throws Exception;

    void delete(EntityManager em, Long id) throws Exception;

    List<Notification> findByTargetRole(EntityManager em, Notification.TargetRole role) throws Exception;
}
