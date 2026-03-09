package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CreateInvoiceDialog extends JDialog {

    private final FinanceService financeService;
    private JComboBox<Enrollment> cboEnrollment;
    private JLabel lblCourse;
    private JLabel lblFee;
    private JTextField txtNote;
    private boolean saved = false;

    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public CreateInvoiceDialog(Frame owner, FinanceService financeService) {
        super(owner, "Tạo Hóa Đơn Mới", true);
        this.financeService = financeService;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
        loadEnrollments();
        pack();
        setLocationRelativeTo(owner);
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
        form.add(UITheme.createFormLabel("Học viên - Khóa học (*):"), g);
        g.gridx = 1;
        cboEnrollment = new JComboBox<>();
        cboEnrollment.setPreferredSize(new Dimension(350, UITheme.FIELD_HEIGHT));
        cboEnrollment.setFont(UITheme.FONT_BODY);
        cboEnrollment.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setFont(UITheme.FONT_BODY);
            label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            if (value instanceof Enrollment en) {
                String studentName = en.getStudent() != null ? en.getStudent().getFullName() : "N/A";
                String className = en.getClassEntity() != null ? en.getClassEntity().getClassName() : "N/A";
                String courseName = en.getClassEntity() != null && en.getClassEntity().getCourse() != null
                        ? en.getClassEntity().getCourse().getCourseName()
                        : "";
                label.setText(studentName + " — " + className + " (" + courseName + ")");
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        cboEnrollment.addActionListener(e -> onEnrollmentSelected());
        form.add(cboEnrollment, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Khóa học:"), g);
        g.gridx = 1;
        lblCourse = new JLabel("—");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);
        form.add(lblCourse, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Học phí:"), g);
        g.gridx = 1;
        lblFee = new JLabel("—");
        lblFee.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFee.setForeground(UITheme.PRIMARY);
        form.add(lblFee, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Ghi chú:"), g);
        g.gridx = 1;
        txtNote = new JTextField(25);
        form.add(txtNote, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton btnCreate = UITheme.createSuccessButton("Tạo Hóa Đơn", "📄");
        JButton btnCancel = UITheme.createOutlineButton("Hủy");
        btnCreate.addActionListener(e -> onCreate());
        btnCancel.addActionListener(e -> dispose());
        actions.add(btnCreate);
        actions.add(btnCancel);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void loadEnrollments() {
        try {
            List<Enrollment> enrollments = financeService.getEnrolledWithoutInvoice();
            cboEnrollment.removeAllItems();
            for (Enrollment e : enrollments)
                cboEnrollment.addItem(e);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách ghi danh: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEnrollmentSelected() {
        Enrollment en = (Enrollment) cboEnrollment.getSelectedItem();
        if (en != null && en.getClassEntity() != null && en.getClassEntity().getCourse() != null) {
            lblCourse.setText(en.getClassEntity().getCourse().getCourseName());
            lblFee.setText(currencyFmt.format(en.getClassEntity().getCourse().getFee()));
        } else {
            lblCourse.setText("—");
            lblFee.setText("—");
        }
    }

    private void onCreate() {
        Enrollment selected = (Enrollment) cboEnrollment.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên - lớp học.");
            return;
        }
        try {
            financeService.createInvoice(selected, txtNote.getText().trim());
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!");
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
