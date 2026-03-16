package vn.edu.ute.ui.schedule;

import vn.edu.ute.model.Schedule;
import vn.edu.ute.service.ScheduleService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Lớp `CenterSchedulePanel` tạo giao diện cho chức năng "Lịch hoạt động".
 * Giao diện này hiển thị TẤT CẢ lịch học của toàn bộ trung tâm, dành cho Admin và Staff.
 */
public class CenterSchedulePanel extends JPanel {

    private final ScheduleService scheduleService;
    // Sử dụng ScheduleTableModel với `showTeacher = true` để hiển thị cột giáo viên
    private final ScheduleTableModel tableModel = new ScheduleTableModel(true);
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Tìm theo lớp, khóa học, giáo viên, phòng...", 25);
    private final JTextField txtFromDate = UITheme.createSearchField("dd/MM/yyyy", 10);
    private final JTextField txtToDate = UITheme.createSearchField("dd/MM/yyyy", 10);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<Schedule> allSchedules; // Lưu trữ danh sách lịch học gốc để lọc

    public CenterSchedulePanel(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    /**
     * Xây dựng giao diện người dùng.
     */
    private void buildUI() {
        // Panel bên trái chứa các bộ lọc ngày và nút làm mới
        JPanel leftPanel = UITheme.createToolbar();
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "🔄");
        btnRefresh.addActionListener(e -> loadData());
        leftPanel.add(btnRefresh);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(UITheme.createFormLabel("Từ ngày:"));
        leftPanel.add(txtFromDate);
        leftPanel.add(UITheme.createFormLabel("Đến ngày:"));
        leftPanel.add(txtToDate);
        JButton btnFilter = UITheme.createPrimaryButton("Lọc", "🔍");
        btnFilter.addActionListener(e -> onFilterByDate());
        leftPanel.add(btnFilter);
        JButton btnClearFilter = UITheme.createOutlineButton("Xóa lọc");
        btnClearFilter.addActionListener(e -> {
            txtFromDate.setText("");
            txtToDate.setText("");
            if (allSchedules != null)
                tableModel.setData(allSchedules); // Hiển thị lại toàn bộ dữ liệu
        });
        leftPanel.add(btnClearFilter);

        // Panel bên phải chứa ô tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);

        // Listener cho ô tìm kiếm để lọc realtime
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Cấu hình bảng
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(7).setPreferredWidth(220); // Tăng độ rộng cột địa chỉ
        table.getColumnModel().getColumn(7).setMinWidth(150);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Tải dữ liệu lịch học từ service một cách bất đồng bộ.
     */
    private void loadData() {
        tableModel.setData(java.util.Collections.emptyList());
        new SwingWorker<List<Schedule>, Void>() {
            @Override
            protected List<Schedule> doInBackground() throws Exception {
                return scheduleService.getAllSchedules();
            }

            @Override
            protected void done() {
                try {
                    allSchedules = get(); // Lưu lại danh sách gốc
                    tableModel.setData(allSchedules);
                    // Xóa các bộ lọc cũ
                    txtSearch.setText("");
                    txtFromDate.setText("");
                    txtToDate.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CenterSchedulePanel.this,
                            "Lỗi tải lịch hoạt động: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Xử lý sự kiện lọc theo khoảng ngày.
     */
    private void onFilterByDate() {
        if (allSchedules == null) return;
        LocalDate from = parseDate(txtFromDate.getText());
        LocalDate to = parseDate(txtToDate.getText());
        // Gọi phương thức lọc của service và cập nhật lại bảng
        tableModel.setData(scheduleService.filterByDateRange(allSchedules, from, to));
    }

    /**
     * Chuyển đổi chuỗi văn bản thành đối tượng LocalDate.
     * @param text Chuỗi ngày tháng (định dạng dd/MM/yyyy).
     * @return Đối tượng LocalDate hoặc null nếu chuỗi không hợp lệ.
     */
    private LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null; // Trả về null nếu không thể phân tích cú pháp
        }
    }
}
