package vn.edu.ute.ui.result;

import vn.edu.ute.service.GradeEntryService;
import vn.edu.ute.service.StudentGradeService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp `StudentGradePanel` tạo giao diện cho chức năng "Xem điểm" của học viên.
 * Hiển thị điểm của tất cả các lớp mà học viên đã và đang tham gia.
 */
public class StudentGradePanel extends JPanel {

    private final StudentGradeService studentGradeService;
    private final Long studentId;

    private final StudentGradeTableModel tableModel = new StudentGradeTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSummary = new JLabel(" "); // Nhãn để hiển thị tóm tắt kết quả

    public StudentGradePanel(StudentGradeService studentGradeService, Long studentId) {
        this.studentGradeService = studentGradeService;
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadResults();
    }

    /**
     * Xây dựng các thành phần giao diện người dùng.
     */
    private void buildUI() {
        JPanel topPanel = UITheme.createToolbar();
        topPanel.add(UITheme.createSectionTitle("📊 Kết quả học tập của bạn"));
        topPanel.add(Box.createHorizontalStrut(15));
        JButton btnRefresh = UITheme.createNeutralButton("Làm mới", "🔄");
        btnRefresh.addActionListener(e -> loadResults());
        topPanel.add(btnRefresh);

        // Cấu hình bảng
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setEnabled(false); // Không cho phép chỉnh sửa trực tiếp trên bảng này

        // Renderer để tô màu cho cột "Xếp loại"
        table.getColumnModel().getColumn(4).setCellRenderer(new GradeColorRenderer());

        // Renderer để căn giữa cho cột STT và Điểm
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Thiết lập độ rộng các cột
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(250);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.NEUTRAL_200, 1, true));

        // Panel dưới cùng hiển thị tóm tắt
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(UITheme.createTitledBorder("Tổng kết"));
        bottomPanel.setBackground(UITheme.BG_CARD);
        lblSummary.setFont(UITheme.FONT_BODY);
        lblSummary.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        bottomPanel.add(lblSummary, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Tải kết quả học tập từ service và cập nhật giao diện.
     */
    private void loadResults() {
        try {
            List<StudentGradeService.StudentGradeRow> rows = studentGradeService.getStudentGrades(studentId);
            tableModel.setData(rows);
            updateSummary(rows); // Cập nhật phần tóm tắt
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải kết quả: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật nhãn tóm tắt dựa trên dữ liệu điểm.
     * @param rows Danh sách các hàng dữ liệu điểm.
     */
    private void updateSummary(List<StudentGradeService.StudentGradeRow> rows) {
        if (rows.isEmpty()) {
            lblSummary.setText("<html><i>Bạn chưa tham gia khóa học nào.</i></html>");
            return;
        }
        long totalGraded = rows.stream().filter(r -> r.score() != null).count();
        if (totalGraded == 0) {
            lblSummary.setText("<html><b>Tổng số lớp tham gia:</b> " + rows.size()
                    + " | <i>Chưa có điểm nào được nhập.</i></html>");
            return;
        }
        // Tính toán các chỉ số thống kê
        double avg = rows.stream().filter(r -> r.score() != null).mapToDouble(r -> r.score().doubleValue()).average().orElse(0.0);
        double maxScore = rows.stream().filter(r -> r.score() != null).mapToDouble(r -> r.score().doubleValue()).max().orElse(0.0);
        double minScore = rows.stream().filter(r -> r.score() != null).mapToDouble(r -> r.score().doubleValue()).min().orElse(0.0);
        Map<Boolean, Long> passFailMap = rows.stream().filter(r -> r.score() != null)
                .collect(Collectors.partitioningBy(r -> r.score().doubleValue() >= 40, Collectors.counting()));
        long passed = passFailMap.getOrDefault(true, 0L);
        double passRate = passed * 100.0 / totalGraded;
        String avgGrade = GradeEntryService.calculateGrade(BigDecimal.valueOf(avg));

        // Hiển thị thông tin tóm tắt
        lblSummary.setText(String.format(
                "<html><b>Tổng số lớp:</b> %d &nbsp;|&nbsp; <b>Đã có điểm:</b> %d &nbsp;|&nbsp; "
                        + "<b>Điểm TB:</b> %.2f (%s) &nbsp;|&nbsp; "
                        + "<b>Cao nhất:</b> %.2f &nbsp;|&nbsp; <b>Thấp nhất:</b> %.2f &nbsp;|&nbsp; "
                        + "<b>Tỷ lệ đạt:</b> %.1f%%</html>",
                rows.size(), totalGraded, avg, avgGrade, maxScore, minScore, passRate));
    }

    // ==================== CÁC LỚP NỘI (INNER CLASSES) ====================

    /**
     * Renderer tùy chỉnh để tô màu cho ô xếp loại.
     */
    static class GradeColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            if (!isSelected && value instanceof String grade && !grade.isEmpty()) {
                c.setBackground(switch (grade) {
                    case "A+", "A" -> UITheme.SUCCESS_LIGHT;
                    case "B+", "B" -> UITheme.PRIMARY_LIGHT;
                    case "C+", "C" -> UITheme.WARNING_LIGHT;
                    case "D+", "D" -> new Color(255, 213, 160);
                    case "F" -> UITheme.DANGER_LIGHT;
                    default -> Color.WHITE;
                });
                c.setForeground(Color.BLACK);
            } else if (!isSelected) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return c;
        }
    }

    /**
     * TableModel cho bảng xem điểm của học viên.
     */
    static class StudentGradeTableModel extends AbstractTableModel {
        private final String[] columns = { "STT", "Tên lớp", "Khóa học", "Điểm", "Xếp loại", "Nhận xét" };
        private List<StudentGradeService.StudentGradeRow> data = new ArrayList<>();

        void setData(List<StudentGradeService.StudentGradeRow> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public boolean isCellEditable(int row, int col) { return false; } // Không cho phép sửa

        @Override
        public Object getValueAt(int row, int col) {
            StudentGradeService.StudentGradeRow r = data.get(row);
            return switch (col) {
                case 0 -> row + 1;
                case 1 -> r.className();
                case 2 -> r.courseName();
                case 3 -> r.score() != null ? r.score().toString() : "—"; // Hiển thị "—" nếu chưa có điểm
                case 4 -> r.grade();
                case 5 -> r.comment();
                default -> "";
            };
        }
    }
}
