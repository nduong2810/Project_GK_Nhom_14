package vn.edu.ute.controller;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.UserAccountService;
import vn.edu.ute.ui.LoginView;
import vn.edu.ute.ui.MainFrame;

import javax.swing.*;

/**
 * Lớp LoginController quản lý luồng đăng nhập của người dùng.
 *
 * SRP (Single Responsibility Principle): LoginController chỉ chịu trách nhiệm điều phối giao diện đăng nhập.
 *      Logic xác thực thông tin đăng nhập được ủy quyền cho UserAccountService.authenticate().
 *
 * DIP (Dependency Inversion Principle): LoginController phụ thuộc vào Service (tầng nghiệp vụ)
 *      thay vì phụ thuộc trực tiếp vào Repository (tầng truy xuất dữ liệu). Điều này giúp giảm sự phụ thuộc
 *      và làm cho hệ thống dễ bảo trì hơn.
 */
public class LoginController {
    private final LoginView loginView; // Giao diện đăng nhập
    private final UserAccountService userAccountService; // Dịch vụ quản lý tài khoản người dùng
    private final MainFrame mainFrame; // Cửa sổ chính của ứng dụng

    /**
     * Hàm khởi tạo cho LoginController.
     * @param loginView Giao diện đăng nhập.
     * @param userAccountService Dịch vụ tài khoản người dùng.
     * @param mainFrame Cửa sổ chính của ứng dụng.
     */
    public LoginController(LoginView loginView, UserAccountService userAccountService, MainFrame mainFrame) {
        this.loginView = loginView;
        this.userAccountService = userAccountService;
        this.mainFrame = mainFrame;

        // Thêm một trình lắng nghe sự kiện cho nút đăng nhập.
        // Khi người dùng nhấn nút, phương thức login() sẽ được gọi.
        loginView.getLoginButton().addActionListener(e -> login());
    }

    /**
     * Xử lý logic đăng nhập khi người dùng nhấn nút "Đăng nhập".
     */
    private void login() {
        // Lấy tên người dùng và mật khẩu từ các trường nhập liệu trên giao diện.
        String username = loginView.getUsernameField().getText();
        String password = new String(loginView.getPasswordField().getPassword());

        // SRP: Ủy quyền việc xác thực cho UserAccountService.
        // Controller không cần biết chi tiết về cách mật khẩu được lưu trữ hay xác thực (ví dụ: password hashing).
        UserAccount user = userAccountService.authenticate(username, password);

        // Kiểm tra kết quả xác thực.
        if (user != null) {
            // Nếu xác thực thành công:
            loginView.setVisible(false); // Ẩn cửa sổ đăng nhập.
            mainFrame.setUser(user); // Thiết lập thông tin người dùng cho cửa sổ chính.
            mainFrame.setVisible(true); // Hiển thị cửa sổ chính.
        } else {
            // Nếu xác thực thất bại:
            // Hiển thị một thông báo lỗi cho người dùng.
            JOptionPane.showMessageDialog(loginView, "Tên đăng nhập hoặc mật khẩu không đúng.", "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
