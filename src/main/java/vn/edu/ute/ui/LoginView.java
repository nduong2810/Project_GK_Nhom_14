package vn.edu.ute.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Màn hình đăng nhập hiện đại với gradient background và card trung tâm bo góc.
 */
public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordButton;

    public LoginView() {
        setTitle("Đăng Nhập - Trung Tâm Ngoại Ngữ");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(30, 58, 138),
                        getWidth(), getHeight(), new Color(59, 130, 246));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Card (white box in center)
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(380, 340));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 25, 5, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // ===== Logo / Title =====
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 25, 2, 25);
        JLabel iconLabel = new JLabel("🌐", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        card.add(iconLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 25, 3, 25);
        JLabel titleLabel = new JLabel("TRUNG TÂM NGOẠI NGỮ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UITheme.BG_HEADER);
        card.add(titleLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 25, 12, 25);
        JLabel subtitleLabel = new JLabel("Đăng nhập để tiếp tục", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.NEUTRAL_400);
        card.add(subtitleLabel, gbc);

        // ===== Username =====
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(3, 25, 2, 25);
        JLabel userLabel = UITheme.createFormLabel("Tài khoản");
        card.add(userLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 25, 8, 25);
        usernameField = new JTextField(20);
        usernameField.setFont(UITheme.FONT_BODY);
        usernameField.setPreferredSize(new Dimension(0, UITheme.FIELD_HEIGHT));
        usernameField.putClientProperty("JTextField.placeholderText", "Nhập tên đăng nhập");
        card.add(usernameField, gbc);

        // ===== Password =====
        gbc.gridy = 5;
        gbc.insets = new Insets(3, 25, 2, 25);
        JLabel passLabel = UITheme.createFormLabel("Mật khẩu");
        card.add(passLabel, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 25, 5, 25);
        passwordField = new JPasswordField(20);
        passwordField.setFont(UITheme.FONT_BODY);
        passwordField.setPreferredSize(new Dimension(0, UITheme.FIELD_HEIGHT));
        passwordField.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu");
        card.add(passwordField, gbc);

        // ===== Forgot Password =====
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 25, 5, 25);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        forgotPasswordButton = new JButton("Quên mật khẩu?");
        forgotPasswordButton.setFont(UITheme.FONT_SMALL);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(UITheme.PRIMARY);
        forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Chức năng này đang được phát triển.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE));
        card.add(forgotPasswordButton, gbc);

        // ===== Login Button =====
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 25, 15, 25);
        loginButton = new JButton("ĐĂNG NHẬP");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(UITheme.PRIMARY);
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginButton.setBackground(UITheme.PRIMARY_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginButton.setBackground(UITheme.PRIMARY);
            }
        });
        card.add(loginButton, gbc);

        // Add card to background
        backgroundPanel.add(card);
        setContentPane(backgroundPanel);
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getForgotPasswordButton() {
        return forgotPasswordButton;
    }
}