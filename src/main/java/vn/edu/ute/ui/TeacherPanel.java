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
        initializeUI();
        loadTeacherData();
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
        String[] columnNames = {"ID", "Họ và Tên", "Chuyên môn", "Ngày thuê", "Điện thoại", "Email", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        teacherTable = new JTable(tableModel);
        add(new JScrollPane(teacherTable), BorderLayout.CENTER);

        // Button Actions
        addButton.addActionListener(e -> openTeacherDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long teacherId = (Long) tableModel.getValueAt(selectedRow, 0);
                teacherService.findTeacherById(teacherId).ifPresent(this::openTeacherDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một giáo viên để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = teacherTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa giáo viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long teacherId = (Long) tableModel.getValueAt(selectedRow, 0);
                    teacherService.deleteTeacher(teacherId);
                    loadTeacherData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một giáo viên để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> loadTeacherData());
    }

    private void loadTeacherData() {
        tableModel.setRowCount(0);
        List<Teacher> teacherList = teacherService.getAllTeachers();
        for (Teacher teacher : teacherList) {
            tableModel.addRow(new Object[]{
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
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField fullNameField = new JTextField(teacher != null ? teacher.getFullName() : "");
        JTextField specialtyField = new JTextField(teacher != null ? teacher.getSpecialty() : "");
        JDateChooser hireDateChooser = new JDateChooser();
        if (teacher != null && teacher.getHireDate() != null) {
            hireDateChooser.setDate(Date.from(teacher.getHireDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        JTextField phoneField = new JTextField(teacher != null ? teacher.getPhone() : "");
        JTextField emailField = new JTextField(teacher != null ? teacher.getEmail() : "");
        JComboBox<Teacher.Status> statusComboBox = new JComboBox<>(Teacher.Status.values());
        if (teacher != null) statusComboBox.setSelectedItem(teacher.getStatus());

        dialog.add(new JLabel("Họ và Tên:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Chuyên môn:"));
        dialog.add(specialtyField);
        dialog.add(new JLabel("Ngày thuê:"));
        dialog.add(hireDateChooser);
        dialog.add(new JLabel("Điện thoại:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Trạng thái:"));
        dialog.add(statusComboBox);

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(e -> {
            Teacher newTeacher = (teacher != null) ? teacher : new Teacher();
            newTeacher.setFullName(fullNameField.getText());
            newTeacher.setSpecialty(specialtyField.getText());
            if (hireDateChooser.getDate() != null) {
                newTeacher.setHireDate(hireDateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            newTeacher.setPhone(phoneField.getText());
            newTeacher.setEmail(emailField.getText());
            newTeacher.setStatus((Teacher.Status) statusComboBox.getSelectedItem());

            teacherService.saveTeacher(newTeacher);
            loadTeacherData();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);

        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}