package vn.edu.ute.db;

import jakarta.persistence.EntityManager;

/**
 * Lớp PersistenceManager cung cấp quyền truy cập vào EntityManager.
 * Đây là một lớp Singleton được triển khai bằng enum để đảm bảo chỉ có một thể hiện duy nhất.
 *
 * SRP (Single Responsibility Principle): PersistenceManager giờ đây ủy quyền hoàn toàn cho Jpa.getInstance()
 * thay vì tự tạo EntityManagerFactory riêng (tránh trùng lặp logic).
 *
 * @deprecated Nên sử dụng Jpa.getInstance() hoặc EntityManagerProvider trực tiếp.
 *             Lớp này được giữ lại để đảm bảo tương thích ngược (backward-compatibility) với code cũ nếu có.
 */
@Deprecated
public enum PersistenceManager {
    /**
     * Thể hiện duy nhất của PersistenceManager.
     */
    INSTANCE;

    /**
     * Lấy một đối tượng EntityManager mới.
     * @return Một EntityManager mới.
     */
    public EntityManager getEntityManager() {
        // Ủy quyền việc tạo EntityManager cho lớp Jpa
        return Jpa.getInstance().createEntityManager();
    }

    /**
     * Đóng kết nối đến cơ sở dữ liệu.
     */
    public void close() {
        // Ủy quyền việc đóng EntityManagerFactory cho lớp Jpa
        Jpa.shutdown();
    }
}
