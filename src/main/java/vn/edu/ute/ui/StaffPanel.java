package vn.edu.ute.ui;

import vn.edu.ute.model.Staff;
import vn.edu.ute.service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Lớp StaffPanel tạo ra giao diện người dùng để quản lý thông tin nhân viên.
 */
public class StaffPanel extends JPanel {
    private final StaffService staffService;
    private JTable staffTable;
    private DefaultTableModel tableModel;

    public StaffPanel(StaffService staffService) {
        this.staffService = staffService;
        setLayout(new BorderLayout());
        UITheme.applyPanelStyle(this);
        initializeUI();
        loadStaffData();
    }

    /**
     * Khởi tạo các thành phần giao diện người dùng.
     */
    private void initializeUI() {
        // ===== Thanh công cụ =====
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

        // ===== Bảng hiển thị nhân viên =====
        String[] columnNames = { "ID", "Họ và Tên", "Chức vụ", "Điện thoại", "Email", "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0);
        staffTable = new JTable(tableModel);
        add(UITheme.createStyledScrollPane(staffTable), BorderLayout.CENTER);

        // ===== Các hành động (Actions) =====
        addButton.addActionListener(e -> openStaffDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long staffId = (Long) tableModel.getValueAt(selectedRow, 0);
                staffService.findStaffById(staffId).ifPresent(this::openStaffDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để sửa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long staffId = (Long) tableModel.getValueAt(selectedRow, 0);
                    staffService.deleteStaff(staffId);
                    loadStaffData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xóa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> loadStaffData());
    }

    /**
     * Tải dữ liệu nhân viên từ service và hiển thị lên bảng.
     */
    private void loadStaffData() {
        tableModel.setRowCount(0);
        List<Staff> staffList = staffService.getAllStaff();
        for (Staff staff : staffList) {
            tableModel.addRow(new Object[] {
                    staff.getStaffId(),
                    staff.getFullName(),
                    staff.getRole(),
                    staff.getPhone(),
                    staff.getEmail(),
                    staff.getStatus()
            });
        }
    }

    /**
     * Mở hộp thoại để thêm hoặc chỉnh sửa thông tin nhân viên.
     * @param staff Nhân viên cần chỉnh sửa, hoặc null nếu là thêm mới.
     */
    private void openStaffDialog(Staff staff) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin Nhân viên", true);
        dialog.getContentPane().setLayout(new BorderLayout());
        UITheme.styleDialog(dialog);

        // Panel form nhập liệu
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fullNameField = new JTextField(staff != null ? staff.getFullName() : "", 22);
        JComboBox<Staff.Role> roleComboBox = new JComboBox<>(Staff.Role.values());
        if (staff != null)
            roleComboBox.setSelectedItem(staff.getRole());
        JTextField phoneField = new JTextField(staff != null ? staff.getPhone() : "", 22);
        JTextField emailField = new JTextField(staff != null ? staff.getEmail() : "", 22);
        JComboBox<Staff.Status> statusComboBox = new JComboBox<>(Staff.Status.values());
        if (staff != null)
            statusComboBox.setSelectedItem(staff.getStatus());

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Họ và Tên:"), gbc);
        gbc.gridx = 1;
        form.add(fullNameField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Chức vụ:"), gbc);
        gbc.gridx = 1;
        form.add(roleComboBox, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Điện thoại:"), gbc);
        gbc.gridx = 1;
        form.add(phoneField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Email:"), gbc);
        gbc.gridx = 1;
        form.add(emailField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        form.add(statusComboBox, gbc);

        // Các nút hành động
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton saveButton = UITheme.createPrimaryButton("Lưu", "💾");
        JButton cancelButton = UITheme.createOutlineButton("Hủy");
        actions.add(saveButton);
        actions.add(cancelButton);

        saveButton.addActionListener(e -> {
            Staff newStaff = (staff != null) ? staff : new Staff();
            newStaff.setFullName(fullNameField.getText());
            newStaff.setRole((Staff.Role) roleComboBox.getSelectedItem());
            newStaff.setPhone(phoneField.getText());
            newStaff.setEmail(emailField.getText());
            newStaff.setStatus((Staff.Status) statusComboBox.getSelectedItem());

            staffService.saveStaff(newStaff);
            loadStaffData();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.getContentPane().add(form, BorderLayout.CENTER);
        dialog.getContentPane().add(actions, BorderLayout.SOUTH);
        dialog.setSize(480, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
