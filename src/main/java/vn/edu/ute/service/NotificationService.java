package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.NotificationRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final TransactionManager tx;

    /**
     * OCP: Mapping UserAccount.Role → Notification.TargetRole.
     * Thêm role mới: chỉ cần thêm 1 entry vào Map này, KHÔNG sửa logic bên dưới.
     */
    private static final Map<UserAccount.Role, Notification.TargetRole> ROLE_TARGET_MAP;

    static {
        ROLE_TARGET_MAP = new EnumMap<>(UserAccount.Role.class);
        ROLE_TARGET_MAP.put(UserAccount.Role.Student, Notification.TargetRole.Student);
        ROLE_TARGET_MAP.put(UserAccount.Role.Teacher, Notification.TargetRole.Teacher);
        ROLE_TARGET_MAP.put(UserAccount.Role.Staff,   Notification.TargetRole.Staff);
        ROLE_TARGET_MAP.put(UserAccount.Role.Admin,   Notification.TargetRole.Staff);
    }

    public NotificationService(NotificationRepository notificationRepo, TransactionManager tx) {
        this.notificationRepo = notificationRepo;
        this.tx = tx;
    }

    // ==================== CRUD ====================

    /**
     * Tạo thông báo mới, gắn người tạo (dùng lambda).
     */
    public void createNotification(Notification notification, UserAccount creator) throws Exception {
        tx.runInTransaction(em -> {
            // Gắn người tạo nếu có
            if (creator != null) {
                UserAccount managedUser = em.find(UserAccount.class, creator.getUserId());
                notification.setCreatedByUser(managedUser);
            }
            notificationRepo.save(em, notification);
            return null;
        });
    }

    /**
     * Xóa thông báo theo ID.
     */
    public void deleteNotification(Long notificationId) throws Exception {
        tx.runInTransaction(em -> {
            Notification existing = notificationRepo.findById(em, notificationId);
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy thông báo với ID: " + notificationId);
            }
            notificationRepo.delete(em, notificationId);
            return null;
        });
    }

    // ==================== QUERY ====================

    /**
     * Lấy tất cả thông báo (cho Admin/Staff quản lý).
     */
    public List<Notification> getAllNotifications() throws Exception {
        return tx.runInTransaction(notificationRepo::findAll);
    }

    /**
     * Lấy thông báo dành cho một role cụ thể (dùng repo query).
     */
    public List<Notification> getNotificationsForRole(Notification.TargetRole role) throws Exception {
        return tx.runInTransaction(em -> notificationRepo.findByTargetRole(em, role));
    }

    /**
     * Lấy thông báo phù hợp với user hiện tại (dùng Stream API + lambda).
     * OCP: Map lookup thay vì switch hardcoded — thêm role mới chỉ cần thêm entry vào ROLE_TARGET_MAP.
     */
    public List<Notification> getNotificationsForUser(UserAccount user) throws Exception {
        Notification.TargetRole userTargetRole = ROLE_TARGET_MAP.getOrDefault(
                user.getRole(), Notification.TargetRole.Staff);

        List<Notification> all = tx.runInTransaction(notificationRepo::findAll);

        return all.stream()
                .filter(n -> n.getTargetRole() == Notification.TargetRole.All
                        || n.getTargetRole() == userTargetRole)
                .collect(Collectors.toList());
    }
}
