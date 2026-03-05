package vn.edu.ute.ui;

import vn.edu.ute.model.Student;
import vn.edu.ute.service.StudentService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class StudentPanel extends JPanel {
    private final StudentService studentService;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentPanel(StudentService studentService) {
        this.studentService = studentService;
        setLayout(new BorderLayout());
        initializeUI();
        loadStudentData();
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
        String[] columnNames = {"ID", "Họ và Tên", "Ngày sinh", "Giới tính", "Điện thoại", "Email", "Địa chỉ", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Button Actions
        addButton.addActionListener(e -> openStudentDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
                studentService.findStudentById(studentId).ifPresent(this::openStudentDialog);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một học viên để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa học viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
                    studentService.deleteStudent(studentId);
                    loadStudentData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một học viên để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> loadStudentData());
    }

    private void loadStudentData() {
        tableModel.setRowCount(0);
        List<Student> studentList = studentService.getAllStudents();
        for (Student student : studentList) {
            tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getPhone(),
                    student.getEmail(),
                    student.getAddress(),
                    student.getStatus()
            });
        }
    }

    private void openStudentDialog(Student student) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin Học viên", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        
        JTextField fullNameField = new JTextField(student != null ? student.getFullName() : "");
        JDateChooser dateOfBirthChooser = new JDateChooser();
        if (student != null && student.getDateOfBirth() != null) {
            dateOfBirthChooser.setDate(Date.from(student.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        JComboBox<Student.Gender> genderComboBox = new JComboBox<>(Student.Gender.values());
        if (student != null) genderComboBox.setSelectedItem(student.getGender());
        JTextField phoneField = new JTextField(student != null ? student.getPhone() : "");
        JTextField emailField = new JTextField(student != null ? student.getEmail() : "");
        JTextField addressField = new JTextField(student != null ? student.getAddress() : "");
        JComboBox<Student.Status> statusComboBox = new JComboBox<>(Student.Status.values());
        if (student != null) statusComboBox.setSelectedItem(student.getStatus());

        dialog.add(new JLabel("Họ và Tên:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Ngày sinh:"));
        dialog.add(dateOfBirthChooser);
        dialog.add(new JLabel("Giới tính:"));
        dialog.add(genderComboBox);
        dialog.add(new JLabel("Điện thoại:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Địa chỉ:"));
        dialog.add(addressField);
        dialog.add(new JLabel("Trạng thái:"));
        dialog.add(statusComboBox);

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(e -> {
            Student newStudent = (student != null) ? student : new Student();
            newStudent.setFullName(fullNameField.getText());
            if (dateOfBirthChooser.getDate() != null) {
                newStudent.setDateOfBirth(dateOfBirthChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            newStudent.setGender((Student.Gender) genderComboBox.getSelectedItem());
            newStudent.setPhone(phoneField.getText());
            newStudent.setEmail(emailField.getText());
            newStudent.setAddress(addressField.getText());
            newStudent.setStatus((Student.Status) statusComboBox.getSelectedItem());
            if (newStudent.getRegistrationDate() == null) {
                newStudent.setRegistrationDate(LocalDate.now());
            }

            studentService.saveStudent(newStudent);
            loadStudentData();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}