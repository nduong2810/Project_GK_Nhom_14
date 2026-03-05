package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository {
    UserAccount findByUsername(String username); // Keep this for login
    List<UserAccount> findAll(EntityManager em);
    Optional<UserAccount> findById(EntityManager em, Long id);
    UserAccount save(EntityManager em, UserAccount userAccount);
    void deleteById(EntityManager em, Long id);
}