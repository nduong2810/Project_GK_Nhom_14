package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Notification;
import vn.edu.ute.repo.NotificationRepository;

import java.util.List;

public class JpaNotificationRepository implements NotificationRepository {

    @Override
    public List<Notification> findAll(EntityManager em) {
        return em.createQuery("SELECT n FROM Notification n LEFT JOIN FETCH n.createdByUser ORDER BY n.createdAt DESC",
                Notification.class)
                .getResultList();
    }

    @Override
    public Notification findById(EntityManager em, Long id) {
        return em.find(Notification.class, id);
    }

    @Override
    public void save(EntityManager em, Notification notification) {
        em.persist(notification);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Notification n = em.find(Notification.class, id);
        if (n != null) {
            em.remove(n);
        }
    }

    /**
     * Lấy thông báo theo role: bao gồm targetRole = 'All' HOẶC targetRole = role
     * truyền vào.
     * Sắp xếp mới nhất trước.
     */
    @Override
    public List<Notification> findByTargetRole(EntityManager em, Notification.TargetRole role) {
        return em.createQuery(
                "SELECT n FROM Notification n LEFT JOIN FETCH n.createdByUser " +
                        "WHERE n.targetRole = :all OR n.targetRole = :role " +
                        "ORDER BY n.createdAt DESC",
                Notification.class)
                .setParameter("all", Notification.TargetRole.All)
                .setParameter("role", role)
                .getResultList();
    }
}
