package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Invoice;
import vn.edu.ute.service.FinanceService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class FinancePanel extends JPanel {

    private final FinanceService financeService;
    private final InvoiceTableModel invoiceTableModel = new InvoiceTableModel();
    private final PaymentTableModel paymentTableModel = new PaymentTableModel();
    private final JTable invoiceTable = new JTable(invoiceTableModel);
    private final JTable paymentTable = new JTable(paymentTableModel);
    private final JTextField txtSearch = createPlaceholderField("Nhập ID hoặc tên học viên...", 22);

    public FinancePanel(FinanceService financeService) {
        this.financeService = financeService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildUI();
        loadInvoices();
    }

    private void buildUI() {
        // ============ PHẦN TRÊN: Danh sách Hóa đơn ============
        JPanel invoiceSection = new JPanel(new BorderLayout(5, 5));
        invoiceSection.setBorder(BorderFactory.createTitledBorder("Danh Sách Hóa Đơn"));

        // Thanh công cụ (nút bấm + ô tìm kiếm)
        JPanel topToolbar = new JPanel(new BorderLayout());

        // Bên trái: các nút
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCreateInvoice = new JButton("Tạo Hóa Đơn");
        JButton btnRecordPayment = new JButton("Thanh Toán");
        JButton btnCancelInvoice = new JButton("Hủy Hóa Đơn");
        JButton btnRefresh = new JButton("Làm Mới");

        // Gắn sự kiện bằng lambda
        btnCreateInvoice.addActionListener(e -> onCreateInvoice());
        btnRecordPayment.addActionListener(e -> onRecordPayment());
        btnCancelInvoice.addActionListener(e -> onCancelInvoice());
        btnRefresh.addActionListener(e -> loadInvoices());

        btnPanel.add(btnCreateInvoice);
        btnPanel.add(btnRecordPayment);
        btnPanel.add(btnCancelInvoice);
        btnPanel.add(btnRefresh);

        // Bên phải: ô tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("🔍 Tìm kiếm:"));
        txtSearch.setToolTipText("Nhập ID hóa đơn hoặc tên học viên để lọc nhanh");
        searchPanel.add(txtSearch);

        // Lắng nghe thay đổi text → lọc realtime (dùng DocumentListener + lambda-style)
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

        topToolbar.add(btnPanel, BorderLayout.WEST);
        topToolbar.add(searchPanel, BorderLayout.EAST);

        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Khi chọn hóa đơn → load danh sách thanh toán tương ứng
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onInvoiceSelected();
            }
        });

        invoiceSection.add(topToolbar, BorderLayout.NORTH);
        invoiceSection.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        // ============ PHẦN DƯỚI: Lịch sử Thanh toán ============
        JPanel paymentSection = new JPanel(new BorderLayout(5, 5));
        paymentSection.setBorder(BorderFactory.createTitledBorder("Lịch Sử Thanh Toán (của hóa đơn được chọn)"));

        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentSection.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        // ============ Chia đôi bằng JSplitPane ============
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, invoiceSection, paymentSection);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.6);

        add(splitPane, BorderLayout.CENTER);
    }

    // ==================== SEARCH ====================

    private void onSearchChanged() {
        invoiceTableModel.setFilter(txtSearch.getText());
    }

    // ==================== DATA LOADING ====================

    private void loadInvoices() {
        try {
            List<Invoice> invoices = financeService.getAllInvoices();
            // Sắp xếp theo ID giảm dần (dùng Stream API + Comparator)
            invoices.sort(Comparator.comparing(Invoice::getInvoiceId).reversed());

            Map<Long, BigDecimal> paidMap = financeService.getAllPaidAmounts();
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
                paymentTableModel.setData(
                        financeService.getPaymentsByInvoice(selectedInvoice.getInvoiceId()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tải lịch sử thanh toán: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== ACTIONS ====================

    private void onCreateInvoice() {
        CreateInvoiceDialog dlg = new CreateInvoiceDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), financeService);
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
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán đầy đủ.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (selectedInvoice.getStatus() == Invoice.Status.Cancelled) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã bị hủy.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RecordPaymentDialog dlg = new RecordPaymentDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), financeService, selectedInvoice);
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
                financeService.cancelInvoice(selectedInvoice.getInvoiceId());
                JOptionPane.showMessageDialog(this, "Hủy hóa đơn thành công!");
                loadInvoices();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== HELPER ====================

    /**
     * Tạo JTextField có placeholder text màu xám, tự ẩn khi người dùng gõ.
     */
    private static JTextField createPlaceholderField(String placeholder, int columns) {
        JTextField field = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() - ins.bottom - 4);
                    g2.dispose();
                }
            }
        };
        // Repaint khi focus thay đổi để ẩn/hiện placeholder
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { field.repaint(); }
            @Override
            public void focusLost(FocusEvent e)   { field.repaint(); }
        });
        return field;
    }
}
