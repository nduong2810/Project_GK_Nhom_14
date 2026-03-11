package vn.edu.ute.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * DIP: TransactionManager phụ thuộc vào EntityManagerProvider (abstraction)
 * thay vì Jpa (concrete class). Giúp dễ dàng inject mock trong testing.
 * SRP: Chỉ chịu trách nhiệm quản lý vòng đời transaction.
 */
public class TransactionManager {

    private final EntityManagerProvider emProvider;

    /**
     * DIP: Nhận EntityManagerProvider qua constructor injection.
     */
    public TransactionManager(EntityManagerProvider emProvider) {
        this.emProvider = emProvider;
    }

    /**
     * Backward-compatible: dùng Jpa.getInstance() mặc định.
     */
    public TransactionManager() {
        this(Jpa.getInstance());
    }

    public <T> T runInTransaction(JpaWork<T> work) throws Exception {
        try (EntityManager em = emProvider.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                T result = work.execute(em);
                tx.commit();
                return result;
            } catch (Exception ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
}
