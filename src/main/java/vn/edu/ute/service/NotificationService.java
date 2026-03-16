package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.NotificationRepository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Service cho nghiệp vụ quản lý thông báo (Notification).
 * Chứa các logic nghiệp vụ CRUD và lọc thông báo theo vai trò người dùng.
 */
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final TransactionManager tx;

    /**
     * OCP (Open/Closed Principle): Sử dụng một Map để ánh xạ từ vai trò người dùng (UserAccount.Role)
     * sang vai trò mục tiêu của thông báo (Notification.TargetRole).
     * Khi có một vai trò người dùng mới, chúng ta chỉ cần thêm một entry vào Map này
     * mà không cần phải sửa đổi logic của các phương thức bên dưới.
     */
    private static final Map<UserAccount.Role, Notification.TargetRole> ROLE_TARGET_MAP;

    static {
        // Sử dụng EnumMap để tối ưu hiệu suất khi làm việc với key là Enum.
        ROLE_TARGET_MAP = new EnumMap<>(UserAccount.Role.class);
        ROLE_TARGET_MAP.put(UserAccount.Role.Student, Notification.TargetRole.Student);
        ROLE_TARGET_MAP.put(UserAccount.Role.Teacher, Notification.TargetRole.Teacher);
        ROLE_TARGET_MAP.put(UserAccount.Role.Staff,   Notification.TargetRole.Staff);
        ROLE_TARGET_MAP.put(UserAccount.Role.Admin,   Notification.TargetRole.Staff); // Admin cũng xem thông báo của Staff
    }

    public NotificationService(NotificationRepository notificationRepo, TransactionManager tx) {
        this.notificationRepo = notificationRepo;
        this.tx = tx;
    }

    // ==================== CRUD ====================

    /**
     * Tạo một thông báo mới và gán người tạo thông báo.
     * @param notification Đối tượng Notification cần tạo.
     * @param creator Tài khoản người dùng tạo thông báo.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void createNotification(Notification notification, UserAccount creator) throws Exception {
        tx.runInTransaction(em -> {
            // Gắn người tạo vào thông báo (nếu có)
            if (creator != null) {
                // Đảm bảo creator là một managed entity trong phiên làm việc hiện tại
                UserAccount managedUser = em.find(UserAccount.class, creator.getUserId());
                notification.setCreatedByUser(managedUser);
            }
            notificationRepo.save(em, notification);
            return null;
        });
    }

    /**
     * Xóa một thông báo theo ID.
     * @param notificationId ID của thông báo cần xóa.
     * @throws Exception nếu có lỗi giao dịch.
     * @throws IllegalArgumentException nếu không tìm thấy thông báo.
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

    // ==================== TRUY VẤN ====================

    /**
     * Lấy tất cả các thông báo (dành cho Admin/Staff quản lý).
     * @return Danh sách tất cả thông báo.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Notification> getAllNotifications() throws Exception {
        return tx.runInTransaction(notificationRepo::findAll);
    }

    /**
     * Lấy các thông báo dành cho một vai trò mục tiêu cụ thể.
     * @param role Vai trò mục tiêu (Student, Teacher, Staff).
     * @return Danh sách các thông báo phù hợp.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Notification> getNotificationsForRole(Notification.TargetRole role) throws Exception {
        return tx.runInTransaction(em -> notificationRepo.findByTargetRole(em, role));
    }

    /**
     * Lấy các thông báo phù hợp cho một người dùng cụ thể.
     * Một người dùng sẽ thấy các thông báo "All" và các thông báo dành riêng cho vai trò của họ.
     * OCP: Logic này sử dụng `ROLE_TARGET_MAP` để tra cứu, giúp dễ dàng mở rộng.
     * @param user Người dùng cần lấy thông báo.
     * @return Danh sách các thông báo phù hợp.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Notification> getNotificationsForUser(UserAccount user) throws Exception {
        // Xác định vai trò mục tiêu tương ứng với vai trò của người dùng
        Notification.TargetRole userTargetRole = ROLE_TARGET_MAP.getOrDefault(
                user.getRole(), Notification.TargetRole.Staff); // Mặc định là Staff nếu không có trong map

        // Lấy tất cả thông báo
        List<Notification> all = tx.runInTransaction(notificationRepo::findAll);

        // Lọc client-side: giữ lại những thông báo có targetRole là 'All' hoặc khớp với vai trò của người dùng.
        return all.stream()
                .filter(n -> n.getTargetRole() == Notification.TargetRole.All
                        || n.getTargetRole() == userTargetRole)
                .collect(Collectors.toList());
    }
}
