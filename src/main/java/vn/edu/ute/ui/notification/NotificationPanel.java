package vn.edu.ute.ui.notification;

import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.NotificationService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Panel quản lý thông báo cho Admin/Staff.
 * Cho phép tạo mới, xem, xóa thông báo, tìm kiếm.
 */
public class NotificationPanel extends JPanel {

    private final NotificationService notificationService;
    private final UserAccount currentUser;
    private final NotificationTableModel tableModel = new NotificationTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập ID hoặc tiêu đề thông báo...", 25);
    private final JTextArea txtPreview = new JTextArea(4, 40);

    public NotificationPanel(NotificationService notificationService, UserAccount currentUser) {
        this.notificationService = notificationService;
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel toolbar = UITheme.createToolbar();
        JButton btnAdd = UITheme.createSuccessButton("Tạo Thông Báo", "");
        JButton btnDelete = UITheme.createDangerButton("Xóa", "");
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "");

        btnAdd.addActionListener(e -> onAdd());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        toolbar.add(btnAdd);
        toolbar.add(btnDelete);
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

        // Khi chọn row → hiện nội dung preview (dùng lambda)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    Notification n = tableModel.getAt(row);
                    txtPreview.setText(n != null ? n.getContent() : "");
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
        previewScroll.setBorder(BorderFactory.createTitledBorder("Nội dung thông báo"));
        previewScroll.setPreferredSize(new Dimension(0, 120));

        // Split layout: table trên, preview dưới
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                UITheme.createStyledScrollPane(table), previewScroll);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(5);
        add(splitPane, BorderLayout.CENTER);
    }

    private void loadData() {
        new SwingWorker<java.util.List<vn.edu.ute.model.Notification>, Void>() {
            @Override
            protected java.util.List<vn.edu.ute.model.Notification> doInBackground() throws Exception {
                return notificationService.getAllNotifications();
            }
            @Override
            protected void done() {
                try {
                    tableModel.setData(get());
                    txtPreview.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(NotificationPanel.this, "Lỗi tải dữ liệu thông báo: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void onAdd() {
        NotificationFormDialog dlg = new NotificationFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Tạo Thông Báo Mới");
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                notificationService.createNotification(dlg.getNotification(), currentUser);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tạo thông báo: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một thông báo trong bảng để xóa.");
            return;
        }
        Notification selected = tableModel.getAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa thông báo: \"" + selected.getTitle() + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                notificationService.deleteNotification(selected.getNotificationId());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
