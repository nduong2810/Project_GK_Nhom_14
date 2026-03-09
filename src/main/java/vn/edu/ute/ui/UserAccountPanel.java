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
        UITheme.applyPanelStyle(this);
        initializeUI();
        loadUserAccountData();
    }

    private void initializeUI() {
        // ===== Toolbar =====
        JPanel toolbar = UITheme.createToolbar();
        JButton addButton = UITheme.createSuccessButton("Thêm", "➕");
        JButton editButton = UITheme.createPrimaryButton("Sửa", "✏️");
        JButton deleteButton = UITheme.createDangerButton("Xóa", "🗑");
        JButton refreshButton = UITheme.createNeutralButton("Làm mới", "🔄");
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);
        add(toolbar, BorderLayout.NORTH);

        // ===== Table =====
        String[] columnNames = { "ID", "Username", "Vai trò", "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0);
        userAccountTable = new JTable(tableModel);
        add(UITheme.createStyledScrollPane(userAccountTable), BorderLayout.CENTER);

        // ===== Actions =====
        addButton.addActionListener(e -> openUserAccountDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = userAccountTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
                userAccountService.findUserAccountById(userId).ifPresent(this::openUserAccountDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để sửa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = userAccountTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản này?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
                    userAccountService.deleteUserAccount(userId);
                    loadUserAccountData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để xóa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> loadUserAccountData());
    }

    private void loadUserAccountData() {
        tableModel.setRowCount(0);
        List<UserAccount> userAccountList = userAccountService.getAllUserAccounts();
        for (UserAccount user : userAccountList) {
            tableModel.addRow(new Object[] {
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getIsActive() ? "Active" : "Inactive"
            });
        }
    }

    private void openUserAccountDialog(UserAccount userAccount) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin Tài khoản", true);
        dialog.getContentPane().setLayout(new BorderLayout());
        UITheme.styleDialog(dialog);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(userAccount != null ? userAccount.getUsername() : "", 22);
        JPasswordField passwordField = new JPasswordField(22);
        JComboBox<UserAccount.Role> roleComboBox = new JComboBox<>(UserAccount.Role.values());
        if (userAccount != null)
            roleComboBox.setSelectedItem(userAccount.getRole());
        JCheckBox activeCheckBox = new JCheckBox("Active", userAccount == null || userAccount.getIsActive());

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Username:"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel(userAccount != null ? "Mật khẩu (để trống nếu không đổi):" : "Mật khẩu:"),
                gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        form.add(roleComboBox, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        form.add(activeCheckBox, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton saveButton = UITheme.createPrimaryButton("Lưu", "💾");
        JButton cancelButton = UITheme.createOutlineButton("Hủy");
        actions.add(saveButton);
        actions.add(cancelButton);

        saveButton.addActionListener(e -> {
            UserAccount newUserAccount = (userAccount != null) ? userAccount : new UserAccount();
            newUserAccount.setUsername(usernameField.getText());

            String password = new String(passwordField.getPassword());
            if (!password.isEmpty()) {
                newUserAccount.setPasswordHash(password);
            }

            newUserAccount.setRole((UserAccount.Role) roleComboBox.getSelectedItem());
            newUserAccount.setIsActive(activeCheckBox.isSelected());

            newUserAccount.setStaff(null);
            newUserAccount.setTeacher(null);
            newUserAccount.setStudent(null);

            userAccountService.saveUserAccount(newUserAccount);
            loadUserAccountData();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.getContentPane().add(form, BorderLayout.CENTER);
        dialog.getContentPane().add(actions, BorderLayout.SOUTH);
        dialog.setSize(520, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}