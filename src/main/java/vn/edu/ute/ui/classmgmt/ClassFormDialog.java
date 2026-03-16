package vn.edu.ute.ui.classmgmt;

import vn.edu.ute.model.Branch;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Course;
import vn.edu.ute.model.Room;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Lớp `ClassFormDialog` tạo hộp thoại để thêm hoặc sửa thông tin lớp học.
 */
public class ClassFormDialog extends JDialog {
    // Các thành phần UI
    private final JTextField txtName = new JTextField(20);
    private final JComboBox<Course> cboCourse = new JComboBox<>();
    private final JComboBox<Teacher> cboTeacher = new JComboBox<>();
    private final JComboBox<Room> cboRoom = new JComboBox<>();
    private final JComboBox<Branch> cboBranch = new JComboBox<>();
    private final JTextField txtStartDate = new JTextField(10);
    private final JTextField txtEndDate = new JTextField(10);
    private final JTextField txtMaxStudent = new JTextField(5);
    private final JComboBox<ClassEntity.Status> cboStatus = new JComboBox<>(ClassEntity.Status.values());

    private boolean saved = false;
    private ClassEntity classEntity;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClassFormDialog(Frame owner, String title, ClassEntity existing, List<Course> courses,
            List<Teacher> teachers, List<Room> rooms, List<Branch> branches) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Nạp dữ liệu vào các ComboBox
        courses.forEach(cboCourse::addItem);
        cboTeacher.addItem(null); // Cho phép không chọn giáo viên
        teachers.forEach(cboTeacher::addItem);
        cboRoom.addItem(null); // Cho phép không chọn phòng
        rooms.forEach(cboRoom::addItem);
        cboBranch.addItem(null); // Cho phép không chọn chi nhánh
        branches.forEach(cboBranch::addItem);

        setupComboBoxRenderers();
        buildUI();

        // Nếu là chế độ sửa, điền dữ liệu
        if (existing != null) {
            this.classEntity = existing;
            txtName.setText(existing.getClassName());
            txtStartDate.setText(existing.getStartDate() != null ? existing.getStartDate().format(dateFormatter) : "");
            txtEndDate.setText(existing.getEndDate() != null ? existing.getEndDate().format(dateFormatter) : "");
            txtMaxStudent.setText(String.valueOf(existing.getMaxStudent()));
            cboStatus.setSelectedItem(existing.getStatus());
            if (existing.getCourse() != null) setComboSelection(cboCourse, existing.getCourse().getCourseId());
            if (existing.getTeacher() != null) setComboSelection(cboTeacher, existing.getTeacher().getTeacherId());
            if (existing.getRoom() != null) setComboSelection(cboRoom, existing.getRoom().getRoomId());
            if (existing.getBranch() != null) {
                branches.stream()
                        .filter(b -> b.getBranchId().equals(existing.getBranch().getBranchId()))
                        .findFirst()
                        .ifPresent(cboBranch::setSelectedItem);
            }
        } else {
            // Chế độ thêm mới
            this.classEntity = new ClassEntity();
            txtStartDate.setText(LocalDate.now().format(dateFormatter));
            cboStatus.setSelectedItem(ClassEntity.Status.Planned);
            txtMaxStudent.setText("20");
        }
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Tùy chỉnh cách hiển thị cho các ComboBox.
     */
    private void setupComboBoxRenderers() {
        cboCourse.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Course c) setText(c.getCourseName());
                return this;
            }
        });
        cboTeacher.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Teacher t) setText(t.getFullName());
                else if (v == null) setText("-- Chưa phân công --");
                return this;
            }
        });
        cboRoom.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Room r) setText(r.getRoomName() + " (Sức chứa: " + r.getCapacity() + ")");
                else if (v == null) setText("-- Chưa xếp phòng --");
                return this;
            }
        });
        cboBranch.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value instanceof Branch b) label.setText(b.getBranchName());
            else label.setText("-- Chưa chọn chi nhánh --");
            // ... (phần style có thể thêm vào)
            return label;
        });
    }

    /**
     * Tiện ích để chọn một item trong ComboBox dựa trên ID.
     */
    private void setComboSelection(JComboBox<?> combo, Long idTarget) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Course c && c.getCourseId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
            if (item instanceof Teacher t && t.getTeacherId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
            if (item instanceof Room r && r.getRoomId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
        }
    }

    /**
     * Xây dựng giao diện người dùng.
     */
    private void buildUI() {
        UITheme.styleDialog(this);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Tên Lớp (*):"), g);
        g.gridx = 1; form.add(txtName, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Khóa Học (*):"), g);
        g.gridx = 1; form.add(cboCourse, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Giáo Viên:"), g);
        g.gridx = 1; form.add(cboTeacher, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Phòng Học:"), g);
        g.gridx = 1; form.add(cboRoom, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Chi Nhánh:"), g);
        g.gridx = 1; form.add(cboBranch, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ngày Khai Giảng (*):"), g);
        g.gridx = 1; form.add(txtStartDate, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ngày Kết Thúc:"), g);
        g.gridx = 1; form.add(txtEndDate, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Sĩ số tối đa:"), g);
        g.gridx = 1; form.add(txtMaxStudent, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Trạng Thái:"), g);
        g.gridx = 1; form.add(cboStatus, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton btnSave = UITheme.createPrimaryButton("Lưu", "💾");
        JButton btnCancel = UITheme.createOutlineButton("Hủy");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        actions.add(btnSave);
        actions.add(btnCancel);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Lưu".
     */
    private void onSave() {
        try {
            // Kiểm tra dữ liệu
            if (txtName.getText().trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập tên lớp.");
            Course selectedCourse = (Course) cboCourse.getSelectedItem();
            if (selectedCourse == null) throw new IllegalArgumentException("Vui lòng chọn khóa học.");
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(txtStartDate.getText().trim(), dateFormatter);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Ngày khai giảng không đúng định dạng dd/MM/yyyy");
            }
            LocalDate endDate = null;
            if (!txtEndDate.getText().trim().isEmpty()) {
                try {
                    endDate = LocalDate.parse(txtEndDate.getText().trim(), dateFormatter);
                    if (endDate.isBefore(startDate)) throw new IllegalArgumentException("Ngày kết thúc phải sau ngày khai giảng.");
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("Ngày kết thúc không đúng định dạng dd/MM/yyyy");
                }
            }
            int maxStd;
            try {
                maxStd = Integer.parseInt(txtMaxStudent.getText().trim());
                if (maxStd < 0) throw new NumberFormatException();
            } catch (Exception e) {
                throw new IllegalArgumentException("Sĩ số phải là số nguyên dương");
            }

            // Cập nhật đối tượng `classEntity`
            classEntity.setClassName(txtName.getText().trim());
            classEntity.setCourse(selectedCourse);
            classEntity.setTeacher((Teacher) cboTeacher.getSelectedItem());
            classEntity.setRoom((Room) cboRoom.getSelectedItem());
            classEntity.setBranch((Branch) cboBranch.getSelectedItem());
            classEntity.setStartDate(startDate);
            classEntity.setEndDate(endDate);
            classEntity.setMaxStudent(maxStd);
            classEntity.setStatus((ClassEntity.Status) cboStatus.getSelectedItem());
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public ClassEntity getClassEntity() { return classEntity; }
}
