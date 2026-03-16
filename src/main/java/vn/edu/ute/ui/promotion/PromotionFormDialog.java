package vn.edu.ute.ui.promotion;

import com.toedter.calendar.JDateChooser;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Lớp `PromotionFormDialog` tạo hộp thoại để thêm hoặc sửa thông tin khuyến mãi.
 */
public class PromotionFormDialog extends JDialog {

    private final JTextField txtName = new JTextField(25);
    private final JComboBox<Promotion.DiscountType> cboDiscountType = new JComboBox<>(Promotion.DiscountType.values());
    private final JTextField txtDiscountValue = new JTextField(15);
    private final JDateChooser dateStart = new JDateChooser();
    private final JDateChooser dateEnd = new JDateChooser();
    private final JComboBox<Promotion.Status> cboStatus = new JComboBox<>(Promotion.Status.values());

    private boolean saved = false;
    private Promotion promotion;

    public PromotionFormDialog(Frame owner, String title, Promotion existing) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();

        if (existing != null) {
            // Chế độ sửa
            this.promotion = existing;
            txtName.setText(existing.getPromoName());
            cboDiscountType.setSelectedItem(existing.getDiscountType());
            txtDiscountValue.setText(existing.getDiscountValue().toPlainString());
            if (existing.getStartDate() != null) {
                dateStart.setDate(Date.from(existing.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            if (existing.getEndDate() != null) {
                dateEnd.setDate(Date.from(existing.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            cboStatus.setSelectedItem(existing.getStatus());
        } else {
            // Chế độ thêm mới
            this.promotion = new Promotion();
            cboStatus.setSelectedItem(Promotion.Status.Active);
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
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Tên KM (*):"), g);
        g.gridx = 1; form.add(txtName, g);

        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Loại giảm giá (*):"), g);
        g.gridx = 1;
        cboDiscountType.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v == Promotion.DiscountType.Percent) setText("Phần trăm (%)");
                else if (v == Promotion.DiscountType.Amount) setText("Số tiền cố định (VNĐ)");
                return this;
            }
        });
        form.add(cboDiscountType, g);

        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Giá trị giảm (*):"), g);
        g.gridx = 1; form.add(txtDiscountValue, g);

        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ngày bắt đầu:"), g);
        g.gridx = 1; dateStart.setDateFormatString("yyyy-MM-dd"); form.add(dateStart, g);

        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ngày kết thúc:"), g);
        g.gridx = 1; dateEnd.setDateFormatString("yyyy-MM-dd"); form.add(dateEnd, g);

        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Trạng thái:"), g);
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
            if (txtName.getText().trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập tên khuyến mãi.");

            BigDecimal discountValue;
            try {
                discountValue = new BigDecimal(txtDiscountValue.getText().trim());
                if (discountValue.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Giá trị giảm phải là số dương.");
            }

            Promotion.DiscountType selectedType = (Promotion.DiscountType) cboDiscountType.getSelectedItem();
            if (selectedType == Promotion.DiscountType.Percent && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Giảm theo phần trăm không được vượt quá 100%.");
            }

            LocalDate startDate = (dateStart.getDate() != null) ? dateStart.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            LocalDate endDate = (dateEnd.getDate() != null) ? dateEnd.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            }

            promotion.setPromoName(txtName.getText().trim());
            promotion.setDiscountType(selectedType);
            promotion.setDiscountValue(discountValue);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setStatus((Promotion.Status) cboStatus.getSelectedItem());

            saved = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Promotion getPromotion() { return promotion; }
}
