package vn.edu.ute.ui;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.UserAccountService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserAccountPanel extends JPanel {
    private final UserAccountService userAccountService;
    private JTable userAccountTable;
    private DefaultTableModel tableModel;

    public UserAccountPanel(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        setLayout(new BorderLayout());
        initializeUI();
        loadUserAccountData();
    }

    private void initializeUI() {
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Thêm");
        JButton editButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Username", "Vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userAccountTable = new JTable(tableModel);
        add(new JScrollPane(userAccountTable), BorderLayout.CENTER);

        // Button Actions
        addButton.addActionListener(e -> openUserAccountDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = userAccountTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
                userAccountService.findUserAccountById(userId).ifPresent(this::openUserAccountDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = userAccountTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
                    userAccountService.deleteUserAccount(userId);
                    loadUserAccountData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> loadUserAccountData());
    }

    private void loadUserAccountData() {
        tableModel.setRowCount(0);
        List<UserAccount> userAccountList = userAccountService.getAllUserAccounts();
        for (UserAccount user : userAccountList) {
            tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getIsActive() ? "Active" : "Inactive"
            });
        }
    }

    private void openUserAccountDialog(UserAccount userAccount) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin Tài khoản", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(450, 200);

        JTextField usernameField = new JTextField(userAccount != null ? userAccount.getUsername() : "");
        JPasswordField passwordField = new JPasswordField();
        JLabel passwordLabel = new JLabel(userAccount != null ? "Mật khẩu (để trống nếu không đổi):" : "Mật khẩu:");
        JComboBox<UserAccount.Role> roleComboBox = new JComboBox<>(UserAccount.Role.values());
        if (userAccount != null) roleComboBox.setSelectedItem(userAccount.getRole());
        JCheckBox activeCheckBox = new JCheckBox("Active", userAccount == null || userAccount.getIsActive());

        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(passwordLabel);
        dialog.add(passwordField);
        dialog.add(new JLabel("Vai trò:"));
        dialog.add(roleComboBox);
        dialog.add(new JLabel("Trạng thái:"));
        dialog.add(activeCheckBox);

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(e -> {
            UserAccount newUserAccount = (userAccount != null) ? userAccount : new UserAccount();
            newUserAccount.setUsername(usernameField.getText());
            
            String password = new String(passwordField.getPassword());
            if (!password.isEmpty()) {
                // In a real app, hash this password!
                newUserAccount.setPasswordHash(password);
            }

            newUserAccount.setRole((UserAccount.Role) roleComboBox.getSelectedItem());
            newUserAccount.setIsActive(activeCheckBox.isSelected());
            
            // Clear owner fields
            newUserAccount.setStaff(null);
            newUserAccount.setTeacher(null);
            newUserAccount.setStudent(null);

            userAccountService.saveUserAccount(newUserAccount);
            loadUserAccountData();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}