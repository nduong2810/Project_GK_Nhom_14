package vn.edu.ute.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public enum PersistenceManager {
    INSTANCE;

    private final EntityManagerFactory emFactory;

    PersistenceManager() {
        emFactory = Persistence.createEntityManagerFactory("language-center-pu");
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public void close() {
        emFactory.close();
    }
}