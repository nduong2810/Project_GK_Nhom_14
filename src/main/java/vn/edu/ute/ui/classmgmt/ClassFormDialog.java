package vn.edu.ute.ui.classmgmt;

import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Course;
import vn.edu.ute.model.Room;
import vn.edu.ute.model.Teacher;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ClassFormDialog extends JDialog {
    private final JTextField txtName = new JTextField(20);
    private final JComboBox<Course> cboCourse = new JComboBox<>();
    private final JComboBox<Teacher> cboTeacher = new JComboBox<>();
    private final JComboBox<Room> cboRoom = new JComboBox<>();
    private final JTextField txtStartDate = new JTextField(10); // Format: dd/MM/yyyy
    private final JTextField txtEndDate = new JTextField(10);   // Format: dd/MM/yyyy (Có thể để trống)
    private final JTextField txtMaxStudent = new JTextField(5);

    // Sử dụng trực tiếp Enum Status từ ClassEntity
    private final JComboBox<ClassEntity.Status> cboStatus = new JComboBox<>(ClassEntity.Status.values());

    private boolean saved = false;
    private ClassEntity classEntity;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClassFormDialog(Frame owner, String title, ClassEntity existing, List<Course> courses, List<Teacher> teachers, List<Room> rooms) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Đổ dữ liệu Khóa học
        for (Course c : courses) cboCourse.addItem(c);

        // Đổ dữ liệu Giáo viên (Cho phép giá trị null nếu chưa phân công)
        cboTeacher.addItem(null);
        for (Teacher t : teachers) cboTeacher.addItem(t);

        // Đổ dữ liệu Phòng học (Cho phép giá trị null nếu chưa xếp phòng)
        cboRoom.addItem(null);
        for (Room r : rooms) cboRoom.addItem(r);

        setupComboBoxRenderers();
        buildUI();

        if (existing != null) {
            txtName.setText(existing.getClassName());
            txtStartDate.setText(existing.getStartDate() != null ? existing.getStartDate().format(dateFormatter) : "");
            txtEndDate.setText(existing.getEndDate() != null ? existing.getEndDate().format(dateFormatter) : "");
            txtMaxStudent.setText(String.valueOf(existing.getMaxStudent()));

            // Set giá trị cho Enum Status
            cboStatus.setSelectedItem(existing.getStatus());

            // Set giá trị cho các ComboBox khóa ngoại
            if (existing.getCourse() != null) setComboSelection(cboCourse, existing.getCourse().getCourseId());
            if (existing.getTeacher() != null) setComboSelection(cboTeacher, existing.getTeacher().getTeacherId());
            if (existing.getRoom() != null) setComboSelection(cboRoom, existing.getRoom().getRoomId());

            this.classEntity = existing;
        } else {
            this.classEntity = new ClassEntity();
            txtStartDate.setText(LocalDate.now().format(dateFormatter));
            cboStatus.setSelectedItem(ClassEntity.Status.Planned); // Mặc định là Planned
            txtMaxStudent.setText("20"); // Mặc định sĩ số 20
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void setupComboBoxRenderers() {
        cboCourse.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) setText(((Course) value).getCourseName());
                return this;
            }
        });

        cboTeacher.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Teacher) {
                    Teacher t = (Teacher) value;
                    setText(t.getFullName() + (t.getSpecialty() != null ? " - " + t.getSpecialty() : ""));
                } else if (value == null) {
                    setText("-- Chưa phân công --");
                }
                return this;
            }
        });

        cboRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    Room r = (Room) value;
                    setText(r.getRoomName() + " (Sức chứa: " + r.getCapacity() + ")");
                } else if (value == null) {
                    setText("-- Chưa xếp phòng --");
                }
                return this;
            }
        });
    }

    private void setComboSelection(JComboBox<?> combo, Long idTarget) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Course && ((Course) item).getCourseId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
            if (item instanceof Teacher && ((Teacher) item).getTeacherId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
            if (item instanceof Room && ((Room) item).getRoomId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
        }
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Tên Lớp (*):"), g);
        g.gridx = 1; form.add(txtName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Khóa Học (*):"), g);
        g.gridx = 1; form.add(cboCourse, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Giáo Viên:"), g);
        g.gridx = 1; form.add(cboTeacher, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Phòng Học:"), g);
        g.gridx = 1; form.add(cboRoom, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ngày Khai Giảng (*):"), g);
        g.gridx = 1; form.add(txtStartDate, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ngày Kết Thúc:"), g);
        g.gridx = 1; form.add(txtEndDate, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Sĩ số tối đa:"), g);
        g.gridx = 1; form.add(txtMaxStudent, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Trạng Thái:"), g);
        g.gridx = 1; form.add(cboStatus, g);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnSave); actions.add(btnCancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void onSave() {
        try {
            if (txtName.getText().trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập tên lớp.");

            Course selectedCourse = (Course) cboCourse.getSelectedItem();
            if (selectedCourse == null) throw new IllegalArgumentException("Vui lòng chọn khóa học.");

            // Xử lý Ngày khai giảng (Bắt buộc)
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(txtStartDate.getText().trim(), dateFormatter);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Ngày khai giảng không đúng định dạng dd/MM/yyyy");
            }

            // Xử lý Ngày kết thúc (Không bắt buộc)
            LocalDate endDate = null;
            if (!txtEndDate.getText().trim().isEmpty()) {
                try {
                    endDate = LocalDate.parse(txtEndDate.getText().trim(), dateFormatter);
                    if (endDate.isBefore(startDate)) {
                        throw new IllegalArgumentException("Ngày kết thúc phải sau ngày khai giảng.");
                    }
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("Ngày kết thúc không đúng định dạng dd/MM/yyyy");
                }
            }

            int maxStd;
            try {
                maxStd = Integer.parseInt(txtMaxStudent.getText().trim());
                if (maxStd < 0) throw new NumberFormatException();
            } catch (Exception e) { throw new IllegalArgumentException("Sĩ số phải là số nguyên dương"); }

            // Gán dữ liệu vào Entity
            classEntity.setClassName(txtName.getText().trim());
            classEntity.setCourse(selectedCourse);
            classEntity.setTeacher((Teacher) cboTeacher.getSelectedItem());
            classEntity.setRoom((Room) cboRoom.getSelectedItem());
            classEntity.setStartDate(startDate);
            classEntity.setEndDate(endDate); // Thêm dòng gán endDate
            classEntity.setMaxStudent(maxStd);
            classEntity.setStatus((ClassEntity.Status) cboStatus.getSelectedItem()); // Ép kiểu chuẩn Enum

            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public ClassEntity getClassEntity() { return classEntity; }
}