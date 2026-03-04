package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.FinanceService;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CreateInvoiceDialog extends JDialog {

    private final FinanceService financeService;
    private final JComboBox<Enrollment> cboEnrollment = new JComboBox<>();
    private final JLabel lblCourseName = new JLabel("—");
    private final JLabel lblFee = new JLabel("—");
    private final JTextField txtNote = new JTextField(30);
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private boolean saved = false;

    public CreateInvoiceDialog(Frame owner, FinanceService financeService) {
        super(owner, "Tạo Hóa Đơn Học Phí", true);
        this.financeService = financeService;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
        loadEnrollments();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        int r = 0;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Đăng ký (*):"), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(cboEnrollment, g);
        g.fill = GridBagConstraints.NONE;

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Khóa học:"), g);
        g.gridx = 1;
        form.add(lblCourseName, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Học phí:"), g);
        g.gridx = 1;
        lblFee.setFont(lblFee.getFont().deriveFont(Font.BOLD, 14f));
        lblFee.setForeground(new Color(0, 128, 0));
        form.add(lblFee, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Ghi chú:"), g);
        g.gridx = 1;
        form.add(txtNote, g);

        // Sự kiện: khi chọn enrollment khác → cập nhật thông tin khóa học & học phí
        cboEnrollment.addActionListener(e -> onEnrollmentSelected());

        // Nút bấm
        JButton btnCreate = new JButton("Tạo Hóa Đơn");
        JButton btnCancel = new JButton("Hủy");
        btnCreate.addActionListener(e -> onCreateInvoice());
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCreate);
        actions.add(btnCancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void loadEnrollments() {
        try {
            List<Enrollment> enrollments = financeService.getEnrolledWithoutInvoice();
            cboEnrollment.removeAllItems();
            enrollments.forEach(cboEnrollment::addItem);

            // Custom renderer: hiển thị "Tên HV — Tên Lớp"
            cboEnrollment.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                JLabel label = new JLabel();
                if (value != null) {
                    String text = value.getStudent().getFullName()
                            + " — " + value.getClassEntity().getClassName();
                    label.setText(text);
                }
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                    label.setOpaque(true);
                }
                return label;
            });

            if (cboEnrollment.getItemCount() > 0) {
                cboEnrollment.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách đăng ký: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEnrollmentSelected() {
        Enrollment e = (Enrollment) cboEnrollment.getSelectedItem();
        if (e != null) {
            String courseName = e.getClassEntity().getCourse().getCourseName();
            lblCourseName.setText(courseName);
            lblFee.setText(currencyFmt.format(e.getClassEntity().getCourse().getFee()));
        } else {
            lblCourseName.setText("—");
            lblFee.setText("—");
        }
    }

    private void onCreateInvoice() {
        Enrollment enrollment = (Enrollment) cboEnrollment.getSelectedItem();
        if (enrollment == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đăng ký học.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            financeService.createInvoiceAndReturn(enrollment, txtNote.getText().trim());
            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!");
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo hóa đơn: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
