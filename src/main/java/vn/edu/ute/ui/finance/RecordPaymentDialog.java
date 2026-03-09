package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class RecordPaymentDialog extends JDialog {

    private final FinanceService financeService;
    private final Invoice invoice;

    private final JLabel lblStudent = new JLabel();
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblPaid = new JLabel();
    private final JLabel lblRemaining = new JLabel();
    private final JTextField txtAmount = new JTextField(15);
    private final JComboBox<Payment.PaymentMethod> cboMethod = new JComboBox<>(Payment.PaymentMethod.values());
    private final JTextField txtRefCode = new JTextField(20);

    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private boolean saved = false;

    public RecordPaymentDialog(Frame owner, FinanceService financeService, Invoice invoice) {
        super(owner, "Ghi Nhận Thanh Toán", true);
        this.financeService = financeService;
        this.invoice = invoice;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
        loadInvoiceInfo();
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
        // Thông tin hóa đơn (chỉ đọc)
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Học viên:"), g);
        g.gridx = 1;
        lblStudent.setFont(UITheme.FONT_BODY_BOLD);
        form.add(lblStudent, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Tổng hóa đơn:"), g);
        g.gridx = 1;
        form.add(lblTotal, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Đã thanh toán:"), g);
        g.gridx = 1;
        form.add(lblPaid, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Còn lại:"), g);
        g.gridx = 1;
        lblRemaining.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRemaining.setForeground(UITheme.DANGER);
        form.add(lblRemaining, g);

        // Separator
        r++;
        g.gridx = 0;
        g.gridy = r;
        g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(UITheme.createSeparator(), g);
        g.gridwidth = 1;
        g.fill = GridBagConstraints.HORIZONTAL;

        // Input thanh toán
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Số tiền TT (*):"), g);
        g.gridx = 1;
        form.add(txtAmount, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Hình thức (*):"), g);
        g.gridx = 1;
        cboMethod.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setFont(UITheme.FONT_BODY);
            label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            if (value != null) {
                label.setText(switch (value) {
                    case Cash -> "💵 Tiền mặt";
                    case Bank -> "🏦 Chuyển khoản ngân hàng";
                    case Momo -> "📱 Ví Momo";
                    case ZaloPay -> "📱 ZaloPay";
                    case Card -> "💳 Thẻ tín dụng/ghi nợ";
                    case Other -> "📋 Khác";
                });
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        form.add(cboMethod, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Mã tham chiếu:"), g);
        g.gridx = 1;
        form.add(txtRefCode, g);

        // Nút bấm
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton btnPay = UITheme.createSuccessButton("Thanh Toán", "💳");
        JButton btnCancel = UITheme.createOutlineButton("Hủy");
        btnPay.addActionListener(e -> onPay());
        btnCancel.addActionListener(e -> dispose());
        actions.add(btnPay);
        actions.add(btnCancel);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void loadInvoiceInfo() {
        lblStudent.setText(invoice.getStudent() != null ? invoice.getStudent().getFullName() : "N/A");
        lblTotal.setText(currencyFmt.format(invoice.getTotalAmount()));
        try {
            BigDecimal totalPaid = financeService.getTotalPaidForInvoice(invoice.getInvoiceId());
            lblPaid.setText(currencyFmt.format(totalPaid));
            BigDecimal remaining = invoice.getTotalAmount().subtract(totalPaid);
            lblRemaining.setText(currencyFmt.format(remaining));
            txtAmount.setText(remaining.toPlainString());
        } catch (Exception ex) {
            lblPaid.setText("Lỗi");
            lblRemaining.setText("Lỗi");
        }
    }

    private void onPay() {
        try {
            String amountStr = txtAmount.getText().trim();
            if (amountStr.isEmpty())
                throw new IllegalArgumentException("Vui lòng nhập số tiền thanh toán.");
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Số tiền phải là số dương hợp lệ.");
            }

            BigDecimal totalPaid = financeService.getTotalPaidForInvoice(invoice.getInvoiceId());
            BigDecimal remaining = invoice.getTotalAmount().subtract(totalPaid);
            if (remaining.compareTo(BigDecimal.ZERO) < 0)
                remaining = BigDecimal.ZERO;

            if (amount.compareTo(remaining) > 0) {
                JOptionPane.showMessageDialog(this,
                        "Số tiền thanh toán (" + currencyFmt.format(amount) + ") vượt quá số tiền còn thiếu ("
                                + currencyFmt.format(remaining) + ").\n"
                                + "Vui lòng thanh toán số tiền nhỏ hơn hoặc bằng " + currencyFmt.format(remaining)
                                + ".",
                        "Số tiền không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Payment.PaymentMethod method = (Payment.PaymentMethod) cboMethod.getSelectedItem();
            String refCode = txtRefCode.getText().trim().isEmpty() ? null : txtRefCode.getText().trim();

            financeService.recordPayment(invoice.getInvoiceId(), null, amount, method, refCode);
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
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
