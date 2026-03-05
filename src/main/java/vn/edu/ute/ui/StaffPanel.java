package vn.edu.ute.ui;

import vn.edu.ute.model.Staff;
import vn.edu.ute.service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffPanel extends JPanel {
    private final StaffService staffService;
    private JTable staffTable;
    private DefaultTableModel tableModel;

    public StaffPanel(StaffService staffService) {
        this.staffService = staffService;
        setLayout(new BorderLayout());
        initializeUI();
        loadStaffData();
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
        String[] columnNames = {"ID", "Họ và Tên", "Chức vụ", "Điện thoại", "Email", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        staffTable = new JTable(tableModel);
        add(new JScrollPane(staffTable), BorderLayout.CENTER);

        // Button Actions
        addButton.addActionListener(e -> openStaffDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long staffId = (Long) tableModel.getValueAt(selectedRow, 0);
                staffService.findStaffById(staffId).ifPresent(this::openStaffDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = staffTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long staffId = (Long) tableModel.getValueAt(selectedRow, 0);
                    staffService.deleteStaff(staffId);
                    loadStaffData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> loadStaffData());
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        List<Staff> staffList = staffService.getAllStaff();
        for (Staff staff : staffList) {
            tableModel.addRow(new Object[]{
                    staff.getStaffId(),
                    staff.getFullName(),
                    staff.getRole(),
                    staff.getPhone(),
                    staff.getEmail(),
                    staff.getStatus()
            });
        }
    }

    private void openStaffDialog(Staff staff) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin Nhân viên", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(450, 250);

        JTextField fullNameField = new JTextField(staff != null ? staff.getFullName() : "");
        JComboBox<Staff.Role> roleComboBox = new JComboBox<>(Staff.Role.values());
        if (staff != null) roleComboBox.setSelectedItem(staff.getRole());
        JTextField phoneField = new JTextField(staff != null ? staff.getPhone() : "");
        JTextField emailField = new JTextField(staff != null ? staff.getEmail() : "");
        JComboBox<Staff.Status> statusComboBox = new JComboBox<>(Staff.Status.values());
        if (staff != null) statusComboBox.setSelectedItem(staff.getStatus());

        dialog.add(new JLabel("Họ và Tên:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Chức vụ:"));
        dialog.add(roleComboBox);
        dialog.add(new JLabel("Điện thoại:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Trạng thái:"));
        dialog.add(statusComboBox);

        JButton saveButton = new JButton("Lưu");
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

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}