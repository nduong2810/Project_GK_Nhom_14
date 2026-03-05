package vn.edu.ute.ui.schedule;

import vn.edu.ute.model.Schedule;
import vn.edu.ute.service.ScheduleService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Tab "Lịch hoạt động" - Hiển thị TẤT CẢ lịch học tại trung tâm.
 * Dành cho Admin và Staff.
 */
public class CenterSchedulePanel extends JPanel {

    private final ScheduleService scheduleService;
    private final ScheduleTableModel tableModel = new ScheduleTableModel(true); // Hiển thị cột Giáo viên
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = createPlaceholderField("Tìm theo lớp, khóa học, giáo viên, phòng...", 25);
    private final JTextField txtFromDate = createPlaceholderField("dd/MM/yyyy", 10);
    private final JTextField txtToDate = createPlaceholderField("dd/MM/yyyy", 10);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Dữ liệu gốc (chưa filter) để tái sử dụng khi filter ngày
    private List<Schedule> allSchedules;

    public CenterSchedulePanel(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildUI();
        loadData();
    }

    private void buildUI() {
        // ===== Thanh công cụ trên cùng =====
        JPanel topPanel = new JPanel(new BorderLayout());

        // Bên trái: nút Làm mới + bộ lọc ngày
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm Mới");
        btnRefresh.addActionListener(e -> loadData());
        leftPanel.add(btnRefresh);

        leftPanel.add(Box.createHorizontalStrut(15));
        leftPanel.add(new JLabel("Từ ngày:"));
        leftPanel.add(txtFromDate);
        leftPanel.add(new JLabel("Đến ngày:"));
        leftPanel.add(txtToDate);

        JButton btnFilter = new JButton("Lọc");
        btnFilter.addActionListener(e -> onFilterByDate());
        leftPanel.add(btnFilter);

        JButton btnClearFilter = new JButton("Xóa lọc");
        btnClearFilter.addActionListener(e -> {
            txtFromDate.setText("");
            txtToDate.setText("");
            if (allSchedules != null) {
                tableModel.setData(allSchedules);
            }
        });
        leftPanel.add(btnClearFilter);

        // Bên phải: ô tìm kiếm
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(new JLabel("🔍 Tìm kiếm:"));
        rightPanel.add(txtSearch);

        // Lắng nghe thay đổi text → lọc realtime
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

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // ===== Bảng dữ liệu =====
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);

        // ===== Ráp vào Panel =====
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            allSchedules = scheduleService.getAllSchedules();
            tableModel.setData(allSchedules);
            txtSearch.setText("");
            txtFromDate.setText("");
            txtToDate.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải lịch hoạt động: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSearchChanged() {
        tableModel.setFilter(txtSearch.getText());
    }

    /**
     * Lọc theo khoảng ngày sử dụng ScheduleService.filterByDateRange() (Stream
     * API).
     */
    private void onFilterByDate() {
        if (allSchedules == null)
            return;

        LocalDate from = parseDate(txtFromDate.getText());
        LocalDate to = parseDate(txtToDate.getText());

        List<Schedule> filtered = scheduleService.filterByDateRange(allSchedules, from, to);
        tableModel.setData(filtered);
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty())
            return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Tạo JTextField có placeholder text màu xám.
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
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.repaint();
            }
        });
        return field;
    }
}
