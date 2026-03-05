package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import vn.edu.ute.db.PersistenceManager;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.UserAccountRepository;

public class JpaUserAccountRepository implements UserAccountRepository {
    @Override
    public UserAccount findByUsername(String username) {
        EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
        try {
            TypedQuery<UserAccount> query = em.createQuery("SELECT u FROM UserAccount u WHERE u.username = :username", UserAccount.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}