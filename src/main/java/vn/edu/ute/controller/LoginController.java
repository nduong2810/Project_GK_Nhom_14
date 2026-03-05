package vn.edu.ute.controller;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.UserAccountRepository;
import vn.edu.ute.ui.LoginView;
import vn.edu.ute.ui.MainFrame;

import javax.swing.*;

public class LoginController {
    private LoginView loginView;
    private UserAccountRepository userAccountRepository;
    private MainFrame mainFrame;

    public LoginController(LoginView loginView, UserAccountRepository userAccountRepository, MainFrame mainFrame) {
        this.loginView = loginView;
        this.userAccountRepository = userAccountRepository;
        this.mainFrame = mainFrame;

        loginView.getLoginButton().addActionListener(e -> login());
    }

    private void login() {
        String username = loginView.getUsernameField().getText();
        String password = new String(loginView.getPasswordField().getPassword());

        UserAccount user = userAccountRepository.findByUsername(username);

        if (user != null && user.getPasswordHash().equals(password)) { // Note: This is not secure!
            loginView.setVisible(false);
            mainFrame.setUser(user);
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(loginView, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}