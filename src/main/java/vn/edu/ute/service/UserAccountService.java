package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.UserAccountRepository;

import java.util.List;
import java.util.Optional;

public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final TransactionManager transactionManager;

    public UserAccountService(UserAccountRepository userAccountRepository, TransactionManager transactionManager) {
        this.userAccountRepository = userAccountRepository;
        this.transactionManager = transactionManager;
    }

    public List<UserAccount> getAllUserAccounts() {
        try {
            return transactionManager.runInTransaction(em -> userAccountRepository.findAll(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserAccount> findUserAccountById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> userAccountRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserAccount saveUserAccount(UserAccount userAccount) {
        try {
            return transactionManager.runInTransaction(em -> userAccountRepository.save(em, userAccount));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUserAccount(Long id) {
        try {
            transactionManager.runInTransaction(em -> {
                userAccountRepository.deleteById(em, id);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}