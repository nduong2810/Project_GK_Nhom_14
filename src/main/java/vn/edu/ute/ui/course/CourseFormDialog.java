package vn.edu.ute.ui.course;

import vn.edu.ute.model.Course;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CourseFormDialog extends JDialog {
    private final JTextField txtName = new JTextField(25);
    private final JTextField txtDescription = new JTextField(25);
    private final JComboBox<Course.Level> cboLevel = new JComboBox<>(Course.Level.values());
    private final JTextField txtDuration = new JTextField(10);
    private final JComboBox<Course.DurationUnit> cboUnit = new JComboBox<>(Course.DurationUnit.values());
    private final JTextField txtFee = new JTextField(15);
    private final JComboBox<Course.Status> cboStatus = new JComboBox<>(Course.Status.values());

    private boolean saved = false;
    private Course course;

    public CourseFormDialog(Frame owner, String title, Course existing) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();

        if (existing != null) {
            txtName.setText(existing.getCourseName());
            txtDescription.setText(existing.getDescription() != null ? existing.getDescription() : "");
            cboLevel.setSelectedItem(existing.getLevel());
            txtDuration.setText(String.valueOf(existing.getDuration()));
            cboUnit.setSelectedItem(existing.getDurationUnit());
            txtFee.setText(existing.getFee().toPlainString());
            cboStatus.setSelectedItem(existing.getStatus());
            this.course = existing;
        } else {
            this.course = new Course();
            cboStatus.setSelectedItem(Course.Status.Active);
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Tên khóa học (*):"), g);
        g.gridx = 1; form.add(txtName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Mô tả:"), g);
        g.gridx = 1; form.add(txtDescription, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Cấp độ:"), g);
        g.gridx = 1; form.add(cboLevel, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Thời lượng (*):"), g);
        JPanel pnlDuration = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlDuration.add(txtDuration); pnlDuration.add(Box.createHorizontalStrut(5)); pnlDuration.add(cboUnit);
        g.gridx = 1; form.add(pnlDuration, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Học phí (VNĐ) (*):"), g);
        g.gridx = 1; form.add(txtFee, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Trạng thái:"), g);
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
            if (txtName.getText().trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập tên khóa học.");

            int duration;
            try {
                duration = Integer.parseInt(txtDuration.getText().trim());
                if (duration <= 0) throw new NumberFormatException();
            } catch (Exception ex) { throw new IllegalArgumentException("Thời lượng phải là số nguyên > 0."); }

            BigDecimal fee;
            try {
                fee = new BigDecimal(txtFee.getText().trim());
                if (fee.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
            } catch (Exception ex) { throw new IllegalArgumentException("Học phí phải là số hợp lệ >= 0."); }

            course.setCourseName(txtName.getText().trim());
            course.setDescription(txtDescription.getText().trim());
            course.setLevel((Course.Level) cboLevel.getSelectedItem());
            course.setDuration(duration);
            course.setDurationUnit((Course.DurationUnit) cboUnit.getSelectedItem());
            course.setFee(fee);
            course.setStatus((Course.Status) cboStatus.getSelectedItem());

            saved = true; dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Course getCourse() { return course; }
}