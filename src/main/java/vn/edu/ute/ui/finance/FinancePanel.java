package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.service.InvoiceService;
import vn.edu.ute.service.PaymentService;
import vn.edu.ute.service.RefundService;
import vn.edu.ute.service.PromotionService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * SRP: FinancePanel nhận 3 service riêng biệt thay vì 1 FinanceService.
 */
public class FinancePanel extends JPanel {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final RefundService refundService;
    private final PromotionService promotionService;
    private final InvoiceTableModel invoiceTableModel = new InvoiceTableModel();
    private final PaymentTableModel paymentTableModel = new PaymentTableModel();
    private final JTable invoiceTable = new JTable(invoiceTableModel);
    private final JTable paymentTable = new JTable(paymentTableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập ID hoặc tên học viên...", 22);

    public FinancePanel(InvoiceService invoiceService, PaymentService paymentService,
            RefundService refundService, PromotionService promotionService) {
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.refundService = refundService;
        this.promotionService = promotionService;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadInvoices();
    }

    private void buildUI() {
        // ============ PHẦN TRÊN: Danh sách Hóa đơn ============
        JPanel invoiceSection = new JPanel(new BorderLayout(5, 5));
        invoiceSection.setBorder(UITheme.createTitledBorder("Danh Sách Hóa Đơn"));
        invoiceSection.setBackground(UITheme.BG_CARD);

        // Thanh công cụ
        JPanel toolbar = UITheme.createToolbar();
        JButton btnCreateInvoice = UITheme.createSuccessButton("Tạo Hóa Đơn", "📄");
        JButton btnRecordPayment = UITheme.createPrimaryButton("Thanh Toán", "💳");
        JButton btnCancelInvoice = UITheme.createWarningButton("Hủy Hóa Đơn", "❌");
        JButton btnRefund = UITheme.createDangerButton("Hoàn Tiền", "↩");
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "🔄");

        btnCreateInvoice.addActionListener(e -> onCreateInvoice());
        btnRecordPayment.addActionListener(e -> onRecordPayment());
        btnCancelInvoice.addActionListener(e -> onCancelInvoice());
        btnRefund.addActionListener(e -> onRefundInvoice());
        btnRefresh.addActionListener(e -> loadInvoices());

        toolbar.add(btnCreateInvoice);
        toolbar.add(btnRecordPayment);
        toolbar.add(btnCancelInvoice);
        toolbar.add(btnRefund);
        toolbar.add(btnRefresh);

        JPanel searchPanel = UITheme.createSearchPanel(txtSearch);
        txtSearch.setToolTipText("Nhập ID hóa đơn hoặc tên học viên để lọc nhanh");

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onSearchChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onSearchChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onSearchChanged();
            }
        });

        JPanel topToolbar = UITheme.createTopPanel(toolbar, searchPanel);

        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(invoiceTable);
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onInvoiceSelected();
            }
        });

        invoiceSection.add(topToolbar, BorderLayout.NORTH);
        invoiceSection.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        // ============ PHẦN DƯỚI: Lịch sử Thanh toán ============
        JPanel paymentSection = new JPanel(new BorderLayout(5, 5));
        paymentSection.setBorder(UITheme.createTitledBorder("Lịch Sử Thanh Toán (của hóa đơn được chọn)"));
        paymentSection.setBackground(UITheme.BG_CARD);

        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(paymentTable);
        paymentSection.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        // ============ Chia đôi bằng JSplitPane ============
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, invoiceSection, paymentSection);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    // ==================== SEARCH ====================

    private void onSearchChanged() {
        invoiceTableModel.setFilter(txtSearch.getText());
    }

    // ==================== DATA LOADING ====================

    private void loadInvoices() {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            invoices.sort(Comparator.comparing(Invoice::getInvoiceId).reversed());
            Map<Long, BigDecimal> paidMap = paymentService.getAllPaidAmounts();
            invoiceTableModel.setData(invoices, paidMap);
            paymentTableModel.setData(Collections.emptyList());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu hóa đơn: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onInvoiceSelected() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow < 0) {
            paymentTableModel.setData(Collections.emptyList());
            return;
        }
        Invoice selectedInvoice = invoiceTableModel.getAt(selectedRow);
        if (selectedInvoice != null) {
            try {
                paymentTableModel.setData(paymentService.getPaymentsByInvoice(selectedInvoice.getInvoiceId()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tải lịch sử thanh toán: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== ACTIONS ====================

    private void onCreateInvoice() {
        CreateInvoiceDialog dlg = new CreateInvoiceDialog((Frame) SwingUtilities.getWindowAncestor(this),
                invoiceService, promotionService);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadInvoices();
        }
    }

    private void onRecordPayment() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trong bảng để thanh toán.");
            return;
        }
        Invoice selectedInvoice = invoiceTableModel.getAt(selectedRow);
        if (selectedInvoice.getStatus() == Invoice.Status.Paid) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán đầy đủ.", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (selectedInvoice.getStatus() == Invoice.Status.Cancelled) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã bị hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RecordPaymentDialog dlg = new RecordPaymentDialog((Frame) SwingUtilities.getWindowAncestor(this),
                paymentService, selectedInvoice);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadInvoices();
        }
    }

    private void onCancelInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trong bảng để hủy.");
            return;
        }
        Invoice selectedInvoice = invoiceTableModel.getAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy hóa đơn #" + selectedInvoice.getInvoiceId() + "?",
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                invoiceService.cancelInvoice(selectedInvoice.getInvoiceId());
                JOptionPane.showMessageDialog(this, "Hủy hóa đơn thành công!");
                loadInvoices();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onRefundInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn trong bảng để hoàn tiền.");
            return;
        }
        Invoice selectedInvoice = invoiceTableModel.getAt(selectedRow);
        if (selectedInvoice.getStatus() == Invoice.Status.Cancelled) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã bị hủy.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Theo quy định, chỉ hoàn lại 70% số tiền đã thanh toán.\n"
                        + "Bạn có muốn tiến hành hoàn tiền cho hóa đơn #" + selectedInvoice.getInvoiceId() + "?",
                "Xác nhận hoàn tiền", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                java.math.BigDecimal refundAmount = refundService.refundInvoice(selectedInvoice.getInvoiceId());
                JOptionPane.showMessageDialog(this,
                        String.format("Hoàn tiền thành công!\nSố tiền hoàn: %,.0f đ (70%%)", refundAmount),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadInvoices();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
