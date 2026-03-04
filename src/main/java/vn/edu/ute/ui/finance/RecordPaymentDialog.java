package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.service.FinanceService;

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
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        int r = 0;
        // Thông tin hóa đơn (chỉ đọc)
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Học viên:"), g);
        g.gridx = 1;
        lblStudent.setFont(lblStudent.getFont().deriveFont(Font.BOLD));
        form.add(lblStudent, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Tổng hóa đơn:"), g);
        g.gridx = 1;
        form.add(lblTotal, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Đã thanh toán:"), g);
        g.gridx = 1;
        form.add(lblPaid, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Còn lại:"), g);
        g.gridx = 1;
        lblRemaining.setFont(lblRemaining.getFont().deriveFont(Font.BOLD, 14f));
        lblRemaining.setForeground(Color.RED);
        form.add(lblRemaining, g);

        // Separator
        r++;
        g.gridx = 0;
        g.gridy = r;
        g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JSeparator(), g);
        g.gridwidth = 1;
        g.fill = GridBagConstraints.NONE;

        // Input thanh toán
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Số tiền TT (*):"), g);
        g.gridx = 1;
        form.add(txtAmount, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(new JLabel("Hình thức (*):"), g);
        g.gridx = 1;
        // Custom renderer cho PaymentMethod
        cboMethod.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value != null) {
                switch (value) {
                    case Cash:
                        label.setText("Tiền mặt");
                        break;
                    case Bank:
                        label.setText("Chuyển khoản ngân hàng");
                        break;
                    case Momo:
                        label.setText("Ví Momo");
                        break;
                    case ZaloPay:
                        label.setText("ZaloPay");
                        break;
                    case Card:
                        label.setText("Thẻ tín dụng/ghi nợ");
                        break;
                    case Other:
                        label.setText("Khác");
                        break;
                }
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
        form.add(new JLabel("Mã tham chiếu:"), g);
        g.gridx = 1;
        form.add(txtRefCode, g);

        // Nút bấm
        JButton btnPay = new JButton("Thanh Toán");
        JButton btnCancel = new JButton("Hủy");
        btnPay.addActionListener(e -> onPay());
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnPay);
        actions.add(btnCancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
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
            // Tự động điền số tiền còn lại vào ô nhập
            txtAmount.setText(remaining.toPlainString());
        } catch (Exception ex) {
            lblPaid.setText("Lỗi");
            lblRemaining.setText("Lỗi");
        }
    }

    private void onPay() {
        try {
            // Validate số tiền
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

            // Kiểm tra không vượt quá số tiền còn thiếu
            BigDecimal totalPaid = financeService.getTotalPaidForInvoice(invoice.getInvoiceId());
            BigDecimal remaining = invoice.getTotalAmount().subtract(totalPaid);
            if (remaining.compareTo(BigDecimal.ZERO) < 0) remaining = BigDecimal.ZERO;

            if (amount.compareTo(remaining) > 0) {
                JOptionPane.showMessageDialog(this,
                        "Số tiền thanh toán (" + currencyFmt.format(amount) + ") vượt quá số tiền còn thiếu ("
                                + currencyFmt.format(remaining) + ").\n"
                                + "Vui lòng thanh toán số tiền nhỏ hơn hoặc bằng " + currencyFmt.format(remaining) + ".",
                        "Số tiền không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Payment.PaymentMethod method = (Payment.PaymentMethod) cboMethod.getSelectedItem();
            String refCode = txtRefCode.getText().trim().isEmpty() ? null : txtRefCode.getText().trim();

            // Gọi service để ghi nhận thanh toán
            financeService.recordPayment(
                    invoice.getInvoiceId(),
                    null, // enrollmentId - có thể mở rộng sau
                    amount,
                    method,
                    refCode);

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
