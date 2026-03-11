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
            return transactionManager.runInTransaction(userAccountRepository::findAll);
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

    /**
     * DIP: Tìm UserAccount theo username — dùng TransactionManager nhất quán.
     * LoginController sẽ gọi method này thay vì dùng trực tiếp Repository.
     */
    public UserAccount findByUsername(String username) {
        try {
            return transactionManager.runInTransaction(em -> userAccountRepository.findByUsername(em, username));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SRP: Logic xác thực credentials nằm trong Service, không nằm trong Controller.
     * Controller chỉ cần gọi method này và nhận kết quả true/false.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu thô nhập từ UI
     * @return UserAccount nếu xác thực thành công, null nếu thất bại
     */
    public UserAccount authenticate(String username, String password) {
        UserAccount user = findByUsername(username);
        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }
        return null;
    }
}