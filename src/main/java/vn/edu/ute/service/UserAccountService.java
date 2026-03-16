package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.UserAccountRepository;

import java.util.List;
import java.util.Optional;

/**
 * Lớp Service cho nghiệp vụ quản lý tài khoản người dùng (UserAccount).
 * Chứa các logic nghiệp vụ CRUD và xác thực người dùng.
 */
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final TransactionManager transactionManager;

    public UserAccountService(UserAccountRepository userAccountRepository, TransactionManager transactionManager) {
        this.userAccountRepository = userAccountRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Lấy danh sách tất cả các tài khoản người dùng.
     * @return Danh sách UserAccount.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public List<UserAccount> getAllUserAccounts() {
        try {
            // Sử dụng method reference cho ngắn gọn
            return transactionManager.runInTransaction(userAccountRepository::findAll);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tìm một tài khoản người dùng theo ID.
     * @param id ID của tài khoản.
     * @return Optional chứa tài khoản nếu tìm thấy.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public Optional<UserAccount> findUserAccountById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> userAccountRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lưu hoặc cập nhật một tài khoản người dùng.
     * @param userAccount Đối tượng UserAccount cần lưu.
     * @return Đối tượng UserAccount đã được lưu.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public UserAccount saveUserAccount(UserAccount userAccount) {
        try {
            return transactionManager.runInTransaction(em -> userAccountRepository.save(em, userAccount));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xóa một tài khoản người dùng theo ID.
     * @param id ID của tài khoản cần xóa.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
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
     * Tìm một tài khoản người dùng theo tên đăng nhập.
     * DIP (Dependency Inversion Principle): LoginController sẽ gọi phương thức này của Service
     * thay vì gọi trực tiếp Repository, giúp giảm sự phụ thuộc vào tầng dữ liệu.
     * @param username Tên đăng nhập cần tìm.
     * @return Đối tượng UserAccount nếu tìm thấy, ngược lại là null.
     * @throws RuntimeException nếu có lỗi giao dịch.
     */
    public UserAccount findByUsername(String username) {
        try {
            return transactionManager.runInTransaction(em -> userAccountRepository.findByUsername(em, username));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Xác thực thông tin đăng nhập của người dùng.
     * SRP (Single Responsibility Principle): Logic xác thực được đặt trong Service, không phải trong Controller.
     * Controller chỉ cần gọi phương thức này và nhận kết quả.
     *
     * @param username Tên đăng nhập do người dùng nhập.
     * @param password Mật khẩu thô (chưa băm) do người dùng nhập.
     * @return Đối tượng UserAccount nếu xác thực thành công, ngược lại trả về null.
     */
    public UserAccount authenticate(String username, String password) {
        UserAccount user = findByUsername(username);
        // Lưu ý: Trong một ứng dụng thực tế, bạn phải băm mật khẩu (password) người dùng nhập
        // và so sánh với mật khẩu đã băm (passwordHash) trong cơ sở dữ liệu.
        // Ở đây, để đơn giản, chúng ta đang so sánh mật khẩu thô.
        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }
        return null;
    }
}
