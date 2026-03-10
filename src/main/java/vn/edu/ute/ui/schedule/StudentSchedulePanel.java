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
 * Tab "Lịch học" - Hiển thị lịch học của học viên đang đăng nhập.
 */
public class StudentSchedulePanel extends JPanel {

    private final ScheduleService scheduleService;
    private final Long studentId;
    private final ScheduleTableModel tableModel = new ScheduleTableModel(false);
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Tìm theo lớp, khóa học, phòng...", 22);
    private final JTextField txtFromDate = UITheme.createSearchField("dd/MM/yyyy", 10);
    private final JTextField txtToDate = UITheme.createSearchField("dd/MM/yyyy", 10);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<Schedule> allSchedules;

    public StudentSchedulePanel(ScheduleService scheduleService, Long studentId) {
        this.scheduleService = scheduleService;
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    private void buildUI() {
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
                tableModel.setData(allSchedules);
        });
        leftPanel.add(btnClearFilter);

        JPanel searchPanel = UITheme.createSearchPanel(txtSearch);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }
        });

        add(UITheme.createTopPanel(leftPanel, searchPanel), BorderLayout.NORTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(6).setPreferredWidth(220);
        table.getColumnModel().getColumn(6).setMinWidth(150);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setData(java.util.Collections.emptyList());
        new SwingWorker<List<Schedule>, Void>() {
            @Override
            protected List<Schedule> doInBackground() throws Exception {
                return scheduleService.getSchedulesByStudent(studentId);
            }

            @Override
            protected void done() {
                try {
                    allSchedules = get();
                    tableModel.setData(allSchedules);
                    txtSearch.setText("");
                    txtFromDate.setText("");
                    txtToDate.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StudentSchedulePanel.this,
                            "Lỗi tải lịch học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void onFilterByDate() {
        if (allSchedules == null)
            return;
        LocalDate from = parseDate(txtFromDate.getText());
        LocalDate to = parseDate(txtToDate.getText());
        tableModel.setData(scheduleService.filterByDateRange(allSchedules, from, to));
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
}
