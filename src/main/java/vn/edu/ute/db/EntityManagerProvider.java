package vn.edu.ute.db;

import jakarta.persistence.EntityManager;

/**
 * DIP: Abstraction cho việc cung cấp EntityManager.
 * TransactionManager phụ thuộc vào interface này thay vì class Jpa cụ thể,
 * giúp dễ dàng thay thế / mock trong testing.
 */
public interface EntityManagerProvider {
    EntityManager createEntityManager();
}
