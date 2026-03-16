package vn.edu.ute.ui.course;

import vn.edu.ute.model.Course;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Lớp `CourseFormDialog` tạo hộp thoại để thêm hoặc sửa thông tin khóa học.
 */
public class CourseFormDialog extends JDialog {
    // Các thành phần UI
    private final JTextField txtName = new JTextField(25);
    private final JTextField txtDescription = new JTextField(25);
    private final JComboBox<Course.Level> cboLevel = new JComboBox<>(Course.Level.values());
    private final JTextField txtDuration = new JTextField(10);
    private final JComboBox<Course.DurationUnit> cboUnit = new JComboBox<>(Course.DurationUnit.values());
    private final JTextField txtFee = new JTextField(15);
    private final JComboBox<Course.Status> cboStatus = new JComboBox<>(Course.Status.values());

    private boolean saved = false;
    private Course course;

    /**
     * Constructor.
     * @param owner Frame cha.
     * @param title Tiêu đề hộp thoại.
     * @param existing Khóa học hiện có để sửa (null nếu thêm mới).
     */
    public CourseFormDialog(Frame owner, String title, Course existing) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();

        if (existing != null) {
            // Chế độ sửa
            this.course = existing;
            txtName.setText(existing.getCourseName());
            txtDescription.setText(existing.getDescription() != null ? existing.getDescription() : "");
            cboLevel.setSelectedItem(existing.getLevel());
            txtDuration.setText(String.valueOf(existing.getDuration()));
            cboUnit.setSelectedItem(existing.getDurationUnit());
            txtFee.setText(existing.getFee().toPlainString());
            cboStatus.setSelectedItem(existing.getStatus());
        } else {
            // Chế độ thêm mới
            this.course = new Course();
            cboStatus.setSelectedItem(Course.Status.Active);
        }
        pack();
        setLocationRelativeTo(owner);
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
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Tên khóa học (*):"), g);
        g.gridx = 1; form.add(txtName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Mô tả:"), g);
        g.gridx = 1; form.add(txtDescription, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Cấp độ:"), g);
        g.gridx = 1; form.add(cboLevel, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Thời lượng (*):"), g);
        JPanel pnlDuration = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlDuration.setOpaque(false);
        pnlDuration.add(txtDuration);
        pnlDuration.add(Box.createHorizontalStrut(5));
        pnlDuration.add(cboUnit);
        g.gridx = 1; form.add(pnlDuration, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Học phí (VNĐ) (*):"), g);
        g.gridx = 1; form.add(txtFee, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Trạng thái:"), g);
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
            if (txtName.getText().trim().isEmpty())
                throw new IllegalArgumentException("Vui lòng nhập tên khóa học.");
            int duration;
            try {
                duration = Integer.parseInt(txtDuration.getText().trim());
                if (duration <= 0) throw new NumberFormatException();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Thời lượng phải là số nguyên > 0.");
            }
            BigDecimal fee;
            try {
                fee = new BigDecimal(txtFee.getText().trim());
                if (fee.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Học phí phải là số hợp lệ >= 0.");
            }

            // Cập nhật đối tượng `course`
            course.setCourseName(txtName.getText().trim());
            course.setDescription(txtDescription.getText().trim());
            course.setLevel((Course.Level) cboLevel.getSelectedItem());
            course.setDuration(duration);
            course.setDurationUnit((Course.DurationUnit) cboUnit.getSelectedItem());
            course.setFee(fee);
            course.setStatus((Course.Status) cboStatus.getSelectedItem());
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Course getCourse() {
        return course;
    }
}
