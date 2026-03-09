package vn.edu.ute.ui.enrollment;

import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Student;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EnrollmentFormDialog extends JDialog {
    private final JComboBox<Student> cboStudent = new JComboBox<>();
    private final JComboBox<ClassEntity> cboClass = new JComboBox<>();
    private final JTextField txtEnrollmentDate = new JTextField(15);
    private final JComboBox<Enrollment.Status> cboStatus = new JComboBox<>(Enrollment.Status.values());

    private boolean saved = false;
    private Enrollment enrollment;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EnrollmentFormDialog(Frame owner, String title, Enrollment existing, List<Student> students,
            List<ClassEntity> classes) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (Student s : students)
            cboStudent.addItem(s);
        for (ClassEntity c : classes)
            cboClass.addItem(c);

        setupComboBoxRenderers();
        buildUI();

        if (existing != null) {
            if (existing.getStudent() != null)
                setComboSelection(cboStudent, existing.getStudent().getStudentId());
            if (existing.getClassEntity() != null)
                setComboSelection(cboClass, existing.getClassEntity().getClassId());
            txtEnrollmentDate.setText(
                    existing.getEnrollmentDate() != null ? existing.getEnrollmentDate().format(dateFormatter) : "");
            cboStatus.setSelectedItem(existing.getStatus());
            this.enrollment = existing;
        } else {
            this.enrollment = new Enrollment();
            txtEnrollmentDate.setText(LocalDate.now().format(dateFormatter));
            cboStatus.setSelectedItem(Enrollment.Status.Enrolled);
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupComboBoxRenderers() {
        cboStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student s)
                    setText(s.getFullName() + " - " + (s.getPhone() != null ? s.getPhone() : "N/A"));
                return this;
            }
        });
        cboClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ClassEntity c)
                    setText(c.getClassName());
                return this;
            }
        });
    }

    private void setComboSelection(JComboBox<?> combo, Long idTarget) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Student && ((Student) item).getStudentId().equals(idTarget)) {
                combo.setSelectedIndex(i);
                return;
            }
            if (item instanceof ClassEntity && ((ClassEntity) item).getClassId().equals(idTarget)) {
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
        form.add(UITheme.createFormLabel("Học Viên (*):"), g);
        g.gridx = 1;
        form.add(cboStudent, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Lớp Học (*):"), g);
        g.gridx = 1;
        form.add(cboClass, g);
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Ngày Ghi Danh:"), g);
        g.gridx = 1;
        form.add(txtEnrollmentDate, g);
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
            Student selectedStudent = (Student) cboStudent.getSelectedItem();
            if (selectedStudent == null)
                throw new IllegalArgumentException("Vui lòng chọn học viên.");
            ClassEntity selectedClass = (ClassEntity) cboClass.getSelectedItem();
            if (selectedClass == null)
                throw new IllegalArgumentException("Vui lòng chọn lớp học.");
            LocalDate enDate;
            try {
                enDate = LocalDate.parse(txtEnrollmentDate.getText().trim(), dateFormatter);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Ngày ghi danh không đúng định dạng dd/MM/yyyy");
            }

            enrollment.setStudent(selectedStudent);
            enrollment.setClassEntity(selectedClass);
            enrollment.setEnrollmentDate(enDate);
            enrollment.setStatus((Enrollment.Status) cboStatus.getSelectedItem());
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }
}