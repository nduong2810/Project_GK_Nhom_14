package vn.edu.ute.ui.classmgmt;

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

public class ClassFormDialog extends JDialog {
    private final JTextField txtName = new JTextField(20);
    private final JComboBox<Course> cboCourse = new JComboBox<>();
    private final JComboBox<Teacher> cboTeacher = new JComboBox<>();
    private final JComboBox<Room> cboRoom = new JComboBox<>();
    private final JTextField txtStartDate = new JTextField(10);
    private final JTextField txtEndDate = new JTextField(10);
    private final JTextField txtMaxStudent = new JTextField(5);
    private final JComboBox<ClassEntity.Status> cboStatus = new JComboBox<>(ClassEntity.Status.values());

    private boolean saved = false;
    private ClassEntity classEntity;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClassFormDialog(Frame owner, String title, ClassEntity existing, List<Course> courses,
            List<Teacher> teachers, List<Room> rooms) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (Course c : courses)
            cboCourse.addItem(c);
        cboTeacher.addItem(null);
        for (Teacher t : teachers)
            cboTeacher.addItem(t);
        cboRoom.addItem(null);
        for (Room r : rooms)
            cboRoom.addItem(r);

        setupComboBoxRenderers();
        buildUI();

        if (existing != null) {
            txtName.setText(existing.getClassName());
            txtStartDate.setText(existing.getStartDate() != null ? existing.getStartDate().format(dateFormatter) : "");
            txtEndDate.setText(existing.getEndDate() != null ? existing.getEndDate().format(dateFormatter) : "");
            txtMaxStudent.setText(String.valueOf(existing.getMaxStudent()));
            cboStatus.setSelectedItem(existing.getStatus());
            if (existing.getCourse() != null)
                setComboSelection(cboCourse, existing.getCourse().getCourseId());
            if (existing.getTeacher() != null)
                setComboSelection(cboTeacher, existing.getTeacher().getTeacherId());
            if (existing.getRoom() != null)
                setComboSelection(cboRoom, existing.getRoom().getRoomId());
            this.classEntity = existing;
        } else {
            this.classEntity = new ClassEntity();
            txtStartDate.setText(LocalDate.now().format(dateFormatter));
            cboStatus.setSelectedItem(ClassEntity.Status.Planned);
            txtMaxStudent.setText("20");
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupComboBoxRenderers() {
        cboCourse.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course)
                    setText(((Course) value).getCourseName());
                return this;
            }
        });
        cboTeacher.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Teacher t)
                    setText(t.getFullName() + (t.getSpecialty() != null ? " - " + t.getSpecialty() : ""));
                else if (value == null)
                    setText("-- Chưa phân công --");
                return this;
            }
        });
        cboRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room r)
                    setText(r.getRoomName() + " (Sức chứa: " + r.getCapacity() + ")");
                else if (value == null)
                    setText("-- Chưa xếp phòng --");
                return this;
            }
        });
    }

    private void setComboSelection(JComboBox<?> combo, Long idTarget) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Course && ((Course) item).getCourseId().equals(idTarget)) {
                combo.setSelectedIndex(i);
                return;
            }
            if (item instanceof Teacher && ((Teacher) item).getTeacherId().equals(idTarget)) {
                combo.setSelectedIndex(i);
                return;
            }
            if (item instanceof Room && ((Room) item).getRoomId().equals(idTarget)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void buildUI() {
        UITheme.styleDialog(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Tên Lớp (*):"), g);
        g.gridx = 1;
        form.add(txtName, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Khóa Học (*):"), g);
        g.gridx = 1;
        form.add(cboCourse, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Giáo Viên:"), g);
        g.gridx = 1;
        form.add(cboTeacher, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Phòng Học:"), g);
        g.gridx = 1;
        form.add(cboRoom, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Ngày Khai Giảng (*):"), g);
        g.gridx = 1;
        form.add(txtStartDate, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Ngày Kết Thúc:"), g);
        g.gridx = 1;
        form.add(txtEndDate, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Sĩ số tối đa:"), g);
        g.gridx = 1;
        form.add(txtMaxStudent, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Trạng Thái:"), g);
        g.gridx = 1;
        form.add(cboStatus, g);

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

    private void onSave() {
        try {
            if (txtName.getText().trim().isEmpty())
                throw new IllegalArgumentException("Vui lòng nhập tên lớp.");
            Course selectedCourse = (Course) cboCourse.getSelectedItem();
            if (selectedCourse == null)
                throw new IllegalArgumentException("Vui lòng chọn khóa học.");
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
                    if (endDate.isBefore(startDate))
                        throw new IllegalArgumentException("Ngày kết thúc phải sau ngày khai giảng.");
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("Ngày kết thúc không đúng định dạng dd/MM/yyyy");
                }
            }
            int maxStd;
            try {
                maxStd = Integer.parseInt(txtMaxStudent.getText().trim());
                if (maxStd < 0)
                    throw new NumberFormatException();
            } catch (Exception e) {
                throw new IllegalArgumentException("Sĩ số phải là số nguyên dương");
            }

            classEntity.setClassName(txtName.getText().trim());
            classEntity.setCourse(selectedCourse);
            classEntity.setTeacher((Teacher) cboTeacher.getSelectedItem());
            classEntity.setRoom((Room) cboRoom.getSelectedItem());
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

    public boolean isSaved() {
        return saved;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }
}