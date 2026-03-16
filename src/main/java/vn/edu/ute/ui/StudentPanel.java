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

/**
 * Lớp StudentPanel tạo ra một giao diện người dùng để quản lý thông tin học viên.
 * Giao diện này bao gồm một bảng hiển thị danh sách học viên và các nút để thêm, sửa, xóa và làm mới dữ liệu.
 */
public class StudentPanel extends JPanel {
    private final StudentService studentService; // Dịch vụ để tương tác với dữ liệu học viên
    private JTable studentTable; // Bảng hiển thị danh sách học viên
    private DefaultTableModel tableModel; // Mô hình dữ liệu cho bảng

    /**
     * Hàm khởi tạo cho StudentPanel.
     * @param studentService Dịch vụ học viên được truyền vào để quản lý dữ liệu.
     */
    public StudentPanel(StudentService studentService) {
        this.studentService = studentService;
        setLayout(new BorderLayout()); // Sử dụng BorderLayout cho panel chính
        UITheme.applyPanelStyle(this); // Áp dụng phong cách giao diện tùy chỉnh
        initializeUI(); // Khởi tạo các thành phần giao diện người dùng
        loadStudentData(); // Tải dữ liệu học viên vào bảng
    }

    /**
     * Khởi tạo các thành phần giao diện người dùng như thanh công cụ, bảng và các nút hành động.
     */
    private void initializeUI() {
        // ===== Thanh công cụ =====
        JPanel toolbar = UITheme.createToolbar(); // Tạo thanh công cụ với phong cách tùy chỉnh
        JButton addButton = UITheme.createSuccessButton("Thêm", "➕"); // Nút thêm học viên
        JButton editButton = UITheme.createPrimaryButton("Sửa", "✏️"); // Nút sửa thông tin học viên
        JButton deleteButton = UITheme.createDangerButton("Xóa", "🗑"); // Nút xóa học viên
        JButton refreshButton = UITheme.createNeutralButton("Làm mới", "🔄"); // Nút làm mới danh sách
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(refreshButton);
        add(toolbar, BorderLayout.NORTH); // Thêm thanh công cụ vào phía trên của panel

        // ===== Bảng =====
        String[] columnNames = { "ID", "Họ và Tên", "Ngày sinh", "Giới tính", "Điện thoại", "Email", "Địa chỉ",
                "Trạng thái" }; // Tên các cột của bảng
        tableModel = new DefaultTableModel(columnNames, 0); // Tạo mô hình bảng với các cột đã định nghĩa
        studentTable = new JTable(tableModel); // Tạo bảng từ mô hình
        add(UITheme.createStyledScrollPane(studentTable), BorderLayout.CENTER); // Thêm bảng vào một JScrollPane có phong cách và đặt ở trung tâm

        // ===== Hành động =====
        // Mở hộp thoại để thêm học viên mới khi nhấn nút "Thêm"
        addButton.addActionListener(e -> openStudentDialog(null));
        // Mở hộp thoại để sửa thông tin học viên đã chọn
        editButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow(); // Lấy hàng đang được chọn
            if (selectedRow >= 0) {
                Long studentId = (Long) tableModel.getValueAt(selectedRow, 0); // Lấy ID của học viên từ hàng đã chọn
                studentService.findStudentById(studentId).ifPresent(this::openStudentDialog); // Tìm học viên và mở hộp thoại nếu tồn tại
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một học viên để sửa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE); // Hiển thị cảnh báo nếu chưa chọn học viên
            }
        });
        // Xóa học viên đã chọn
        deleteButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa học viên này?", "Xác nhận",
                        JOptionPane.YES_NO_OPTION); // Yêu cầu xác nhận từ người dùng
                if (confirm == JOptionPane.YES_OPTION) {
                    Long studentId = (Long) tableModel.getValueAt(selectedRow, 0);
                    studentService.deleteStudent(studentId); // Xóa học viên
                    loadStudentData(); // Tải lại dữ liệu
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một học viên để xóa.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        // Tải lại dữ liệu học viên khi nhấn nút "Làm mới"
        refreshButton.addActionListener(e -> loadStudentData());
    }

    /**
     * Tải dữ liệu học viên từ service và hiển thị lên bảng.
     */
    private void loadStudentData() {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có trong bảng
        List<Student> studentList = studentService.getAllStudents(); // Lấy danh sách tất cả học viên
        for (Student student : studentList) {
            // Thêm một hàng mới vào bảng cho mỗi học viên
            tableModel.addRow(new Object[] {
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

    /**
     * Mở một hộp thoại để thêm hoặc chỉnh sửa thông tin học viên.
     * @param student Học viên cần chỉnh sửa, hoặc null nếu là thêm mới.
     */
    private void openStudentDialog(Student student) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin Học viên", true);
        dialog.getContentPane().setLayout(new BorderLayout());
        UITheme.styleDialog(dialog); // Áp dụng phong cách cho hộp thoại

        JPanel form = new JPanel(new GridBagLayout()); // Panel chứa các trường nhập liệu
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tạo các trường nhập liệu và điền dữ liệu nếu là chỉnh sửa
        JTextField fullNameField = new JTextField(student != null ? student.getFullName() : "", 22);
        JDateChooser dateOfBirthChooser = new JDateChooser();
        if (student != null && student.getDateOfBirth() != null) {
            dateOfBirthChooser
                    .setDate(Date.from(student.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        JComboBox<Student.Gender> genderComboBox = new JComboBox<>(Student.Gender.values());
        if (student != null)
            genderComboBox.setSelectedItem(student.getGender());
        JTextField phoneField = new JTextField(student != null ? student.getPhone() : "", 22);
        JTextField emailField = new JTextField(student != null ? student.getEmail() : "", 22);
        JTextField addressField = new JTextField(student != null ? student.getAddress() : "", 22);
        JComboBox<Student.Status> statusComboBox = new JComboBox<>(Student.Status.values());
        if (student != null)
            statusComboBox.setSelectedItem(student.getStatus());

        // Thêm các nhãn và trường nhập liệu vào form
        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Họ và Tên:"), gbc);
        gbc.gridx = 1;
        form.add(fullNameField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        form.add(dateOfBirthChooser, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        form.add(genderComboBox, gbc);
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
        form.add(UITheme.createFormLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        form.add(addressField, gbc);
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(UITheme.createFormLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        form.add(statusComboBox, gbc);

        // Panel chứa các nút hành động (Lưu, Hủy)
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton saveButton = UITheme.createPrimaryButton("Lưu", "💾");
        JButton cancelButton = UITheme.createOutlineButton("Hủy");
        actions.add(saveButton);
        actions.add(cancelButton);

        // Xử lý sự kiện khi nhấn nút "Lưu"
        saveButton.addActionListener(e -> {
            Student newStudent = (student != null) ? student : new Student(); // Tạo học viên mới hoặc sử dụng học viên hiện tại
            // Cập nhật thông tin cho học viên
            newStudent.setFullName(fullNameField.getText());
            if (dateOfBirthChooser.getDate() != null) {
                newStudent.setDateOfBirth(
                        dateOfBirthChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            newStudent.setGender((Student.Gender) genderComboBox.getSelectedItem());
            newStudent.setPhone(phoneField.getText());
            newStudent.setEmail(emailField.getText());
            newStudent.setAddress(addressField.getText());
            newStudent.setStatus((Student.Status) statusComboBox.getSelectedItem());
            if (newStudent.getRegistrationDate() == null) {
                newStudent.setRegistrationDate(LocalDate.now()); // Đặt ngày đăng ký nếu là học viên mới
            }

            studentService.saveStudent(newStudent); // Lưu thông tin học viên
            loadStudentData(); // Tải lại dữ liệu
            dialog.dispose(); // Đóng hộp thoại
        });
        // Đóng hộp thoại khi nhấn nút "Hủy"
        cancelButton.addActionListener(e -> dialog.dispose());

        // Thêm các panel vào hộp thoại
        dialog.getContentPane().add(form, BorderLayout.CENTER);
        dialog.getContentPane().add(actions, BorderLayout.SOUTH);
        dialog.setSize(500, 420); // Đặt kích thước hộp thoại
        dialog.setLocationRelativeTo(this); // Hiển thị hộp thoại ở trung tâm của cửa sổ cha
        dialog.setVisible(true); // Hiển thị hộp thoại
    }
}
