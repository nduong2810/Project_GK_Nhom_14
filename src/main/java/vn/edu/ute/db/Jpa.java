package vn.edu.ute.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * DIP: Jpa implement EntityManagerProvider để TransactionManager không phụ thuộc
 * trực tiếp vào class cụ thể này.
 * SRP: Chỉ chịu trách nhiệm cung cấp EntityManager từ persistence unit.
 */
public final class Jpa implements EntityManagerProvider {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("language-center-pu");

    private static final Jpa INSTANCE = new Jpa();

    private Jpa() {}

    public static Jpa getInstance() {
        return INSTANCE;
    }

    @Override
    public EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }

    /** Backward-compatible static method — giữ lại để không phá vỡ code hiện tại. */
    public static EntityManager em() {
        return EMF.createEntityManager();
    }

    public static void shutdown() {
        EMF.close();
    }
}
