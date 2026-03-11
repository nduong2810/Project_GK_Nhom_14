package vn.edu.ute.controller;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.UserAccountService;
import vn.edu.ute.ui.LoginView;
import vn.edu.ute.ui.MainFrame;

import javax.swing.*;

/**
 * SRP: LoginController chỉ chịu trách nhiệm điều phối UI đăng nhập.
 *      Logic xác thực credentials được delegate sang UserAccountService.authenticate().
 * DIP: LoginController phụ thuộc vào Service (tầng nghiệp vụ)
 *      thay vì phụ thuộc trực tiếp vào Repository (tầng truy xuất dữ liệu).
 */
public class LoginController {
    private final LoginView loginView;
    private final UserAccountService userAccountService;
    private final MainFrame mainFrame;

    public LoginController(LoginView loginView, UserAccountService userAccountService, MainFrame mainFrame) {
        this.loginView = loginView;
        this.userAccountService = userAccountService;
        this.mainFrame = mainFrame;

        loginView.getLoginButton().addActionListener(e -> login());
    }

    private void login() {
        String username = loginView.getUsernameField().getText();
        String password = new String(loginView.getPasswordField().getPassword());

        // SRP: Delegate xác thực sang Service — Controller không biết gì về passwordHash
        UserAccount user = userAccountService.authenticate(username, password);

        if (user != null) {
            loginView.setVisible(false);
            mainFrame.setUser(user);
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(loginView, "Tên đăng nhập hoặc mật khẩu không đúng.", "Đăng nhập thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}