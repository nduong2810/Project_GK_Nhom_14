package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.PromotionService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CreateInvoiceDialog extends JDialog {

    private final FinanceService financeService;
    private final PromotionService promotionService;
    private JComboBox<Enrollment> cboEnrollment;
    private JComboBox<Object> cboPromotion; // Object để hỗ trợ item "Không áp dụng"
    private JLabel lblCourse;
    private JLabel lblFee;
    private JLabel lblDiscount;
    private JLabel lblFinalAmount;
    private JTextField txtNote;
    private boolean saved = false;

    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public CreateInvoiceDialog(Frame owner, FinanceService financeService, PromotionService promotionService) {
        super(owner, "Tạo Hóa Đơn Mới", true);
        this.financeService = financeService;
        this.promotionService = promotionService;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
        loadEnrollments();
        loadPromotions();
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

        // Row: Học viên - Khóa học
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
        cboEnrollment.addActionListener(e -> recalculate());
        form.add(cboEnrollment, g);

        // Row: Khóa học
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Khóa học:"), g);
        g.gridx = 1;
        lblCourse = new JLabel("—");
        lblCourse.setFont(UITheme.FONT_BODY_BOLD);
        form.add(lblCourse, g);

        // Row: Học phí gốc
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Học phí gốc:"), g);
        g.gridx = 1;
        lblFee = new JLabel("—");
        lblFee.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFee.setForeground(UITheme.PRIMARY);
        form.add(lblFee, g);

        // Row: Khuyến mãi
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Khuyến mãi:"), g);
        g.gridx = 1;
        cboPromotion = new JComboBox<>();
        cboPromotion.setPreferredSize(new Dimension(350, UITheme.FIELD_HEIGHT));
        cboPromotion.setFont(UITheme.FONT_BODY);
        cboPromotion.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setFont(UITheme.FONT_BODY);
            label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            if (value instanceof Promotion promo) {
                String typeStr = promo.getDiscountType() == Promotion.DiscountType.Percent
                        ? promo.getDiscountValue() + "%"
                        : currencyFmt.format(promo.getDiscountValue());
                label.setText(promo.getPromoName() + " (Giảm " + typeStr + ")");
            } else if (value instanceof String) {
                label.setText((String) value);
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        cboPromotion.addActionListener(e -> recalculate());
        form.add(cboPromotion, g);

        // Row: Số tiền giảm
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Giảm giá:"), g);
        g.gridx = 1;
        lblDiscount = new JLabel("—");
        lblDiscount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDiscount.setForeground(new Color(220, 53, 69)); // Đỏ
        form.add(lblDiscount, g);

        // Row: Thành tiền
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Thành tiền:"), g);
        g.gridx = 1;
        lblFinalAmount = new JLabel("—");
        lblFinalAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFinalAmount.setForeground(new Color(25, 135, 84)); // Xanh lá
        form.add(lblFinalAmount, g);

        // Row: Ghi chú
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Ghi chú:"), g);
        g.gridx = 1;
        txtNote = new JTextField(25);
        form.add(txtNote, g);

        // Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton btnCreate = UITheme.createSuccessButton("Tạo Hóa Đơn", "");
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
            enrollments.forEach(e -> cboEnrollment.addItem(e));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách ghi danh: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load danh sách khuyến mãi đang Active vào combo (dùng Stream forEach).
     */
    private void loadPromotions() {
        try {
            cboPromotion.removeAllItems();
            cboPromotion.addItem("-- Không áp dụng --");
            List<Promotion> activePromos = promotionService.getActivePromotions();
            activePromos.forEach(p -> cboPromotion.addItem(p));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khuyến mãi: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tính lại giá hiển thị khi chọn enrollment hoặc promotion thay đổi.
     */
    private void recalculate() {
        Enrollment en = (Enrollment) cboEnrollment.getSelectedItem();
        if (en == null || en.getClassEntity() == null || en.getClassEntity().getCourse() == null) {
            lblCourse.setText("—");
            lblFee.setText("—");
            lblDiscount.setText("—");
            lblFinalAmount.setText("—");
            return;
        }

        String courseName = en.getClassEntity().getCourse().getCourseName();
        BigDecimal courseFee = en.getClassEntity().getCourse().getFee();
        lblCourse.setText(courseName);
        lblFee.setText(currencyFmt.format(courseFee));

        // Lấy promotion đang chọn
        Object selectedPromo = cboPromotion.getSelectedItem();
        BigDecimal discount = BigDecimal.ZERO;
        if (selectedPromo instanceof Promotion promo) {
            discount = promotionService.calculateDiscount(courseFee, promo);
        }

        BigDecimal finalAmount = courseFee.subtract(discount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            lblDiscount.setText("- " + currencyFmt.format(discount));
        } else {
            lblDiscount.setText("Không giảm");
        }
        lblFinalAmount.setText(currencyFmt.format(finalAmount));
    }

    private void onCreate() {
        Enrollment selected = (Enrollment) cboEnrollment.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên - lớp học.");
            return;
        }

        // Lấy promotionId nếu có chọn
        Long promotionId = null;
        Object selectedPromo = cboPromotion.getSelectedItem();
        if (selectedPromo instanceof Promotion promo) {
            promotionId = promo.getPromotionId();
        }

        try {
            financeService.createInvoice(selected, txtNote.getText().trim(), promotionId);
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
