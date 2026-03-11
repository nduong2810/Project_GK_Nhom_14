package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.UserAccountRepository;

import java.util.List;
import java.util.Optional;

public class JpaUserAccountRepository implements UserAccountRepository {

    // ISP: nhất quán dùng EntityManager param giống các method khác
    @Override
    public UserAccount findByUsername(EntityManager em, String username) {
        try {
            TypedQuery<UserAccount> query = em.createQuery(
                    "SELECT u FROM UserAccount u " +
                            "LEFT JOIN FETCH u.staff " +
                            "LEFT JOIN FETCH u.teacher " +
                            "LEFT JOIN FETCH u.student " +
                            "WHERE u.username = :username",
                    UserAccount.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<UserAccount> findAll(EntityManager em) {
        // Use JOIN FETCH to eagerly load related entities and avoid
        // LazyInitializationException
        return em.createQuery(
                "SELECT DISTINCT u FROM UserAccount u " +
                        "LEFT JOIN FETCH u.staff " +
                        "LEFT JOIN FETCH u.teacher " +
                        "LEFT JOIN FETCH u.student",
                UserAccount.class).getResultList();
    }

    @Override
    public Optional<UserAccount> findById(EntityManager em, Long id) {
        // Also use JOIN FETCH here for consistency when fetching a single account
        TypedQuery<UserAccount> query = em.createQuery(
                "SELECT u FROM UserAccount u " +
                        "LEFT JOIN FETCH u.staff " +
                        "LEFT JOIN FETCH u.teacher " +
                        "LEFT JOIN FETCH u.student " +
                        "WHERE u.userId = :id",
                UserAccount.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Override
    public UserAccount save(EntityManager em, UserAccount userAccount) {
        if (userAccount.getUserId() == null) {
            em.persist(userAccount);
            return userAccount;
        } else {
            return em.merge(userAccount);
        }
    }

    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }
}