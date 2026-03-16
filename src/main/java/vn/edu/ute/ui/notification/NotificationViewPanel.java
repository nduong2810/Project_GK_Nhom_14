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
 * Lớp `NotificationViewPanel` tạo giao diện để xem thông báo,
 * dành cho các vai trò như Student và Teacher.
 * Nó chỉ hiển thị các thông báo phù hợp với vai trò của người dùng.
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

    /**
     * Xây dựng giao diện người dùng.
     */
    private void buildUI() {
        JPanel toolbar = UITheme.createToolbar();
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "🔄");
        btnRefresh.addActionListener(e -> loadData());
        toolbar.add(btnRefresh);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(toolbar, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Bảng thông báo
        UITheme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    Notification n = tableModel.getAt(row);
                    if (n != null) {
                        // Hiển thị chi tiết thông báo trong JTextArea
                        String detail = String.format(
                                "Tiêu đề: %s\nNgày tạo: %s\nNgười gửi: %s\n\n%s",
                                n.getTitle(),
                                n.getCreatedAt() != null ? n.getCreatedAt().format(dtFormatter) : "—",
                                n.getCreatedByUser() != null ? n.getCreatedByUser().getUsername() : "Hệ thống",
                                n.getContent());
                        txtPreview.setText(detail);
                        txtPreview.setCaretPosition(0); // Cuộn lên đầu
                    }
                } else {
                    txtPreview.setText("");
                }
            }
        });

        // Panel xem trước
        txtPreview.setEditable(false);
        txtPreview.setLineWrap(true);
        txtPreview.setWrapStyleWord(true);
        JScrollPane previewScroll = new JScrollPane(txtPreview);
        previewScroll.setBorder(BorderFactory.createTitledBorder("Chi tiết thông báo"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                UITheme.createStyledScrollPane(table), previewScroll);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Tải dữ liệu thông báo phù hợp với vai trò của người dùng hiện tại.
     */
    private void loadData() {
        try {
            List<Notification> notifications = notificationService.getNotificationsForUser(currentUser);
            tableModel.setData(notifications);
            txtPreview.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu thông báo: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
