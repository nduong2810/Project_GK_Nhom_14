package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Notification;
import vn.edu.ute.repo.NotificationRepository;

import java.util.List;

/**
 * Lớp triển khai của NotificationRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Notification.
 */
public class JpaNotificationRepository implements NotificationRepository {

    /**
     * {@inheritDoc}
     * Tải sẵn thông tin người tạo và sắp xếp theo thời gian tạo mới nhất.
     */
    @Override
    public List<Notification> findAll(EntityManager em) {
        return em.createQuery("SELECT n FROM Notification n LEFT JOIN FETCH n.createdByUser ORDER BY n.createdAt DESC",
                Notification.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification findById(EntityManager em, Long id) {
        return em.find(Notification.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(EntityManager em, Notification notification) {
        em.persist(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Notification n = em.find(Notification.class, id);
        if (n != null) {
            em.remove(n);
        }
    }

    /**
     * {@inheritDoc}
     * Lấy các thông báo có `targetRole` là 'All' (dành cho tất cả mọi người)
     * hoặc có `targetRole` khớp với vai trò được chỉ định.
     * Sắp xếp theo thời gian tạo mới nhất để hiển thị các thông báo gần đây trước.
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
