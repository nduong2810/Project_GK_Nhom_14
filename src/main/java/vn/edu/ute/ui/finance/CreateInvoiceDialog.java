package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.service.InvoiceService;
import vn.edu.ute.service.PromotionService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Lớp `CreateInvoiceDialog` tạo hộp thoại để tạo hóa đơn mới cho một lần ghi danh.
 * SRP: Dialog này sử dụng `InvoiceService` và `PromotionService` thay vì một `FinanceService` lớn.
 */
public class CreateInvoiceDialog extends JDialog {

    private final InvoiceService invoiceService;
    private final PromotionService promotionService;
    private JComboBox<Enrollment> cboEnrollment;
    private JComboBox<Object> cboPromotion;
    private JLabel lblCourse;
    private JLabel lblFee;
    private JLabel lblDiscount;
    private JLabel lblFinalAmount;
    private JTextField txtNote;
    private boolean saved = false;

    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public CreateInvoiceDialog(Frame owner, InvoiceService invoiceService, PromotionService promotionService) {
        super(owner, "Tạo Hóa Đơn Mới", true);
        this.invoiceService = invoiceService;
        this.promotionService = promotionService;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
        loadEnrollments();
        loadPromotions();
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

        // ComboBox chọn Ghi danh
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Học viên - Lớp học (*):"), g);
        g.gridx = 1;
        cboEnrollment = new JComboBox<>();
        cboEnrollment.setPreferredSize(new Dimension(350, UITheme.FIELD_HEIGHT));
        cboEnrollment.setFont(UITheme.FONT_BODY);
        cboEnrollment.setRenderer(new DefaultListCellRenderer() { // Tùy chỉnh hiển thị
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Enrollment en) {
                    String studentName = en.getStudent() != null ? en.getStudent().getFullName() : "N/A";
                    String className = en.getClassEntity() != null ? en.getClassEntity().getClassName() : "N/A";
                    setText(studentName + " — " + className);
                }
                return this;
            }
        });
        cboEnrollment.addActionListener(e -> recalculate());
        form.add(cboEnrollment, g);

        // Các nhãn hiển thị thông tin
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Khóa học:"), g);
        g.gridx = 1; lblCourse = new JLabel("—"); lblCourse.setFont(UITheme.FONT_BODY_BOLD); form.add(lblCourse, g);

        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Học phí gốc:"), g);
        g.gridx = 1; lblFee = new JLabel("—"); lblFee.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblFee.setForeground(UITheme.PRIMARY); form.add(lblFee, g);

        // ComboBox chọn Khuyến mãi
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Khuyến mãi:"), g);
        g.gridx = 1;
        cboPromotion = new JComboBox<>();
        cboPromotion.setPreferredSize(new Dimension(350, UITheme.FIELD_HEIGHT));
        cboPromotion.setFont(UITheme.FONT_BODY);
        cboPromotion.setRenderer(new DefaultListCellRenderer() { // Tùy chỉnh hiển thị
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Promotion promo) {
                    String typeStr = promo.getDiscountType() == Promotion.DiscountType.Percent
                            ? promo.getDiscountValue() + "%"
                            : currencyFmt.format(promo.getDiscountValue());
                    setText(promo.getPromoName() + " (Giảm " + typeStr + ")");
                } else if (value instanceof String) {
                    setText((String) value);
                }
                return this;
            }
        });
        cboPromotion.addActionListener(e -> recalculate());
        form.add(cboPromotion, g);

        // Các nhãn hiển thị tính toán
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Giảm giá:"), g);
        g.gridx = 1; lblDiscount = new JLabel("—"); lblDiscount.setFont(new Font("Segoe UI", Font.BOLD, 13)); lblDiscount.setForeground(UITheme.DANGER); form.add(lblDiscount, g);

        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Thành tiền:"), g);
        g.gridx = 1; lblFinalAmount = new JLabel("—"); lblFinalAmount.setFont(new Font("Segoe UI", Font.BOLD, 16)); lblFinalAmount.setForeground(UITheme.SUCCESS); form.add(lblFinalAmount, g);

        // Ghi chú
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ghi chú:"), g);
        g.gridx = 1; txtNote = new JTextField(25); form.add(txtNote, g);

        // Các nút hành động
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

    /**
     * Tải danh sách các lần ghi danh chưa có hóa đơn.
     */
    private void loadEnrollments() {
        try {
            List<Enrollment> enrollments = invoiceService.getEnrolledWithoutInvoice();
            cboEnrollment.removeAllItems();
            enrollments.forEach(cboEnrollment::addItem);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách ghi danh: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tải danh sách các khuyến mãi đang hoạt động.
     */
    private void loadPromotions() {
        try {
            cboPromotion.removeAllItems();
            cboPromotion.addItem("-- Không áp dụng --");
            List<Promotion> activePromos = promotionService.getActivePromotions();
            activePromos.forEach(cboPromotion::addItem);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khuyến mãi: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tính toán lại các giá trị khi lựa chọn thay đổi.
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

        BigDecimal courseFee = en.getClassEntity().getCourse().getFee();
        lblCourse.setText(en.getClassEntity().getCourse().getCourseName());
        lblFee.setText(currencyFmt.format(courseFee));

        Object selectedPromo = cboPromotion.getSelectedItem();
        BigDecimal discount = BigDecimal.ZERO;
        if (selectedPromo instanceof Promotion promo) {
            discount = promotionService.calculateDiscount(courseFee, promo);
        }

        BigDecimal finalAmount = courseFee.subtract(discount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        lblDiscount.setText(discount.compareTo(BigDecimal.ZERO) > 0 ? "- " + currencyFmt.format(discount) : "Không giảm");
        lblFinalAmount.setText(currencyFmt.format(finalAmount));
    }

    /**
     * Xử lý sự kiện tạo hóa đơn.
     */
    private void onCreate() {
        Enrollment selected = (Enrollment) cboEnrollment.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên - lớp học.");
            return;
        }

        Long promotionId = null;
        if (cboPromotion.getSelectedItem() instanceof Promotion promo) {
            promotionId = promo.getPromotionId();
        }

        try {
            invoiceService.createInvoice(selected, txtNote.getText().trim(), promotionId);
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
