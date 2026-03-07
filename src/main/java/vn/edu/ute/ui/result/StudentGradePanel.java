package vn.edu.ute.ui.result;

import vn.edu.ute.service.ResultService;

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
 * Tab "Xem điểm" — Học sinh xem điểm các lớp mình đã/đang học.
 * Read-only, tự động load khi mở tab.
 * Hiển thị TẤT CẢ lớp tham gia (có hoặc chưa có điểm).
 * Thống kê phía dưới: GPA trung bình, số lớp, tỷ lệ pass.
 */
public class StudentGradePanel extends JPanel {

    private final ResultService resultService;
    private final Long studentId;

    private final StudentGradeTableModel tableModel = new StudentGradeTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSummary = new JLabel(" ");

    public StudentGradePanel(ResultService resultService, Long studentId) {
        this.resultService = resultService;
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildUI();
        loadResults();
    }

    private void buildUI() {
        // ===== Thanh công cụ trên cùng =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        topPanel.add(new JLabel("Kết quả học tập của bạn:"));

        topPanel.add(Box.createHorizontalStrut(15));
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadResults());
        topPanel.add(btnRefresh);

        // ===== Bảng điểm (read-only) =====
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setEnabled(true);

        // Cột Xếp loại (4): renderer tô màu
        table.getColumnModel().getColumn(4).setCellRenderer(new GradeColorRenderer());

        // Cột Điểm (3): căn giữa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Đặt độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(250);

        JScrollPane scrollPane = new JScrollPane(table);

        // ===== Summary panel phía dưới =====
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Tổng kết"));
        lblSummary.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomPanel.add(lblSummary, BorderLayout.CENTER);

        // ===== Ráp vào =====
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==================== DATA LOADING ====================

    private void loadResults() {
        try {
            // Lấy tất cả lớp đã tham gia (có hoặc chưa có điểm) bằng Stream merge
            List<ResultService.StudentGradeRow> rows = resultService.getStudentGrades(studentId);
            tableModel.setData(rows);
            updateSummary(rows);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi tải kết quả: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật thống kê tổng kết bằng Stream API.
     */
    private void updateSummary(List<ResultService.StudentGradeRow> rows) {
        if (rows.isEmpty()) {
            lblSummary.setText("<html><i>Bạn chưa tham gia khóa học nào.</i></html>");
            return;
        }

        // Tổng số lớp đã có điểm (Stream count)
        long totalGraded = rows.stream()
                .filter(r -> r.score() != null)
                .count();

        if (totalGraded == 0) {
            lblSummary.setText("<html><b>Tổng số lớp tham gia:</b> " + rows.size()
                    + " | <i>Chưa có điểm nào được nhập.</i></html>");
            return;
        }

        // Tính GPA trung bình (Stream mapToDouble + average)
        double avg = rows.stream()
                .filter(r -> r.score() != null)
                .mapToDouble(r -> r.score().doubleValue())
                .average()
                .orElse(0.0);

        // Điểm cao nhất / thấp nhất (Stream max / min)
        double maxScore = rows.stream()
                .filter(r -> r.score() != null)
                .mapToDouble(r -> r.score().doubleValue())
                .max()
                .orElse(0.0);

        double minScore = rows.stream()
                .filter(r -> r.score() != null)
                .mapToDouble(r -> r.score().doubleValue())
                .min()
                .orElse(0.0);

        // Tỷ lệ đạt (≥ 40) bằng Stream partitioningBy
        Map<Boolean, Long> passFailMap = rows.stream()
                .filter(r -> r.score() != null)
                .collect(Collectors.partitioningBy(
                        r -> r.score().doubleValue() >= 40,
                        Collectors.counting()));

        long passed = passFailMap.getOrDefault(true, 0L);
        double passRate = passed * 100.0 / totalGraded;

        // Xếp loại trung bình
        String avgGrade = ResultService.calculateGrade(BigDecimal.valueOf(avg));

        lblSummary.setText(String.format(
                "<html><b>Tổng số lớp:</b> %d &nbsp;|&nbsp; <b>Đã có điểm:</b> %d &nbsp;|&nbsp; "
                        + "<b>Điểm TB:</b> %.2f (%s) &nbsp;|&nbsp; "
                        + "<b>Cao nhất:</b> %.2f &nbsp;|&nbsp; <b>Thấp nhất:</b> %.2f &nbsp;|&nbsp; "
                        + "<b>Tỷ lệ đạt:</b> %.1f%%</html>",
                rows.size(), totalGraded, avg, avgGrade, maxScore, minScore, passRate));
    }

    // ==================== INNER CLASSES ====================

    /**
     * Renderer tô màu cho cột Xếp loại.
     */
    static class GradeColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            if (!isSelected && value instanceof String grade && !grade.isEmpty()) {
                c.setBackground(switch (grade) {
                    case "A+", "A" -> new Color(198, 239, 206);
                    case "B+", "B" -> new Color(221, 235, 247);
                    case "C+", "C" -> new Color(255, 235, 156);
                    case "D+", "D" -> new Color(255, 213, 160);
                    case "F" -> new Color(255, 199, 206);
                    default -> Color.WHITE;
                });
                c.setForeground(Color.BLACK);
            } else if (!isSelected) {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }

    /**
     * TableModel cho bảng xem điểm (read-only).
     * Dùng trực tiếp ResultService.StudentGradeRow record.
     * Cột: STT | Tên lớp | Khóa học | Điểm | Xếp loại | Nhận xét
     */
    static class StudentGradeTableModel extends AbstractTableModel {
        private final String[] columns = {
                "STT", "Tên lớp", "Khóa học", "Điểm", "Xếp loại", "Nhận xét"
        };
        private List<ResultService.StudentGradeRow> data = new ArrayList<>();

        void setData(List<ResultService.StudentGradeRow> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int col) {
            return columns[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false; // Read-only
        }

        @Override
        public Object getValueAt(int row, int col) {
            ResultService.StudentGradeRow r = data.get(row);
            return switch (col) {
                case 0 -> row + 1;
                case 1 -> r.className();
                case 2 -> r.courseName();
                case 3 -> r.score() != null ? r.score().toString() : "—";
                case 4 -> r.grade();
                case 5 -> r.comment();
                default -> "";
            };
        }
    }
}
