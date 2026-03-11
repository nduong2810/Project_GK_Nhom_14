package vn.edu.ute.db;

import jakarta.persistence.EntityManager;

/**
 * SRP: PersistenceManager giờ delegate hoàn toàn sang Jpa.getInstance()
 * thay vì tự tạo EntityManagerFactory riêng (tránh duplicate logic).
 *
 * @deprecated Dùng Jpa.getInstance() hoặc EntityManagerProvider trực tiếp.
 *             Giữ lại class này để backward-compatible với code cũ nếu có.
 */
@Deprecated
public enum PersistenceManager {
    INSTANCE;

    public EntityManager getEntityManager() {
        return Jpa.getInstance().createEntityManager();
    }

    public void close() {
        Jpa.shutdown();
    }
}