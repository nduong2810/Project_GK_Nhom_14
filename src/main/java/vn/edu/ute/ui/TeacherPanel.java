package vn.edu.ute.ui;

import vn.edu.ute.model.Teacher;
import vn.edu.ute.service.TeacherService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TeacherPanel extends JPanel {
    private final TeacherService teacherService;
    private JTable teacherTable;
    private DefaultTableModel tableModel;

    public TeacherPanel(TeacherService teacherService) {
        this.teacherService = teacherService;
        setLayout(new BorderLayout());
        UITheme.applyPanelStyle(this);
        initializeUI();
        loadTeacherData();
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
        String[] columnNames = { "ID", "Họ và Tên", "Chuyên môn", "Ngày thuê", "Điện thoại", "Email", "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0);
        teacherTable = new JTable(tableModel);
        add(UITheme.createStyledScrollPane(teacherTable), BorderLayout.CENTER);

        // ===== Actions =====
        addButton.addActionListener(e -> openTeacherDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long teacherId = (Long) tableModel.getValueAt(selectedRow, 0);
                teacherService.findTeacherById(teacherId).ifPresent(this::openTeacherDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một giáo viên để sửa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa giáo viên này?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long teacherId = (Long) tableModel.getValueAt(selectedRow, 0);
                    teacherService.deleteTeacher(teacherId);
                    loadTeacherData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một giáo viên để xóa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> loadTeacherData());
    }

    private void loadTeacherData() {
        tableModel.setRowCount(0);
        List<Teacher> teacherList = teacherService.getAllTeachers();
        for (Teacher teacher : teacherList) {
            tableModel.addRow(new Object[] {
                    teacher.getTeacherId(),
                    teacher.getFullName(),
                    teacher.getSpecialty(),
                    teacher.getHireDate(),
                    teacher.getPhone(),
                    teacher.getEmail(),
                    teacher.getStatus()
            });
        }
    }

    private void openTeacherDialog(Teacher teacher) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin Giáo viên", true);
        dialog.getContentPane().setLayout(new BorderLayout());
        UITheme.styleDialog(dialog);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fullNameField = new JTextField(teacher != null ? teacher.getFullName() : "", 22);
        JTextField specialtyField = new JTextField(teacher != null ? teacher.getSpecialty() : "", 22);
        JDateChooser hireDateChooser = new JDateChooser();
        if (teacher != null && teacher.getHireDate() != null) {
            hireDateChooser.setDate(Date.from(teacher.getHireDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        JTextField phoneField = new JTextField(teacher != null ? teacher.getPhone() : "", 22);
        JTextField emailField = new JTextField(teacher != null ? teacher.getEmail() : "", 22);
        JComboBox<Teacher.Status> statusComboBox = new JComboBox<>(Teacher.Status.values());
        if (teacher != null)
            statusComboBox.setSelectedItem(teacher.getStatus());

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Họ và Tên:"), gbc);
        gbc.gridx = 1;
        form.add(fullNameField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Chuyên môn:"), gbc);
        gbc.gridx = 1;
        form.add(specialtyField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Ngày thuê:"), gbc);
        gbc.gridx = 1;
        form.add(hireDateChooser, gbc);
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

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton saveButton = UITheme.createPrimaryButton("Lưu", "💾");
        JButton cancelButton = UITheme.createOutlineButton("Hủy");
        actions.add(saveButton);
        actions.add(cancelButton);

        saveButton.addActionListener(e -> {
            Teacher newTeacher = (teacher != null) ? teacher : new Teacher();
            newTeacher.setFullName(fullNameField.getText());
            newTeacher.setSpecialty(specialtyField.getText());
            if (hireDateChooser.getDate() != null) {
                newTeacher.setHireDate(
                        hireDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            newTeacher.setPhone(phoneField.getText());
            newTeacher.setEmail(emailField.getText());
            newTeacher.setStatus((Teacher.Status) statusComboBox.getSelectedItem());

            teacherService.saveTeacher(newTeacher);
            loadTeacherData();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.getContentPane().add(form, BorderLayout.CENTER);
        dialog.getContentPane().add(actions, BorderLayout.SOUTH);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}