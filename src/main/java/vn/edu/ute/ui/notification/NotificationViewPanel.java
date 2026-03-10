package vn.edu.ute.ui.notification;

import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.NotificationService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel xem thông báo cho Student/Teacher.
 * Hiển thị danh sách thông báo dành cho role tương ứng + thông báo "All".
 * Cho phép tìm kiếm và xem chi tiết.
 */
public class NotificationViewPanel extends JPanel {

    private final NotificationService notificationService;
    private final UserAccount currentUser;
    private final NotificationTableModel tableModel = new NotificationTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Tìm kiếm thông báo...", 25);
    private final JTextArea txtPreview = new JTextArea(5, 40);
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotificationViewPanel(NotificationService notificationService, UserAccount currentUser) {
        this.notificationService = notificationService;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel toolbar = UITheme.createToolbar();
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "");
        btnRefresh.addActionListener(e -> loadData());
        toolbar.add(btnRefresh);

        JPanel searchPanel = UITheme.createSearchPanel(txtSearch);

        // Search listener dùng lambda
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }
        });

        add(UITheme.createTopPanel(toolbar, searchPanel), BorderLayout.NORTH);

        // Bảng thông báo
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(table);

        // Khi chọn row → hiện nội dung chi tiết (dùng lambda)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    Notification n = tableModel.getAt(row);
                    if (n != null) {
                        // Hiển thị nội dung chi tiết dùng String.format (functional style)
                        String detail = String.format(
                                "Tiêu đề: %s\nNgày tạo: %s\nNgười gửi: %s\n\n%s",
                                n.getTitle(),
                                n.getCreatedAt() != null ? n.getCreatedAt().format(dtFormatter) : "—",
                                n.getCreatedByUser() != null ? n.getCreatedByUser().getUsername() : "Hệ thống",
                                n.getContent());
                        txtPreview.setText(detail);
                        txtPreview.setCaretPosition(0);
                    }
                } else {
                    txtPreview.setText("");
                }
            }
        });

        // Panel preview nội dung
        txtPreview.setEditable(false);
        txtPreview.setLineWrap(true);
        txtPreview.setWrapStyleWord(true);
        txtPreview.setFont(UITheme.FONT_BODY);
        JScrollPane previewScroll = new JScrollPane(txtPreview);
        previewScroll.setBorder(BorderFactory.createTitledBorder("Chi tiết thông báo"));
        previewScroll.setPreferredSize(new Dimension(0, 150));

        // Split layout: table trên, preview dưới
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                UITheme.createStyledScrollPane(table), previewScroll);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(5);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Tải thông báo phù hợp với role user hiện tại (dùng Stream API).
     * - Lấy tất cả thông báo, lọc theo role bằng Stream.
     */
    private void loadData() {
        try {
            // Dùng getNotificationsForUser — bên trong đã lọc bằng Stream
            List<Notification> notifications = notificationService.getNotificationsForUser(currentUser);
            tableModel.setData(notifications);
            txtPreview.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu thông báo: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
