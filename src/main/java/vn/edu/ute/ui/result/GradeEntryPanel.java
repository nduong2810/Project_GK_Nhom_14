package vn.edu.ute.ui.result;

import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Result;
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
 * Tab "Nhập điểm" — Giáo viên chọn lớp Ongoing mình dạy, nhập điểm cho học viên.
 */
public class GradeEntryPanel extends JPanel {

    private final ResultService resultService;
    private final Long teacherId;

    private JComboBox<ClassItem> cmbClass;
    private final GradeTableModel tableModel = new GradeTableModel();
    private final JTable table = new JTable(tableModel);

    public GradeEntryPanel(ResultService resultService, Long teacherId) {
        this.resultService = resultService;
        this.teacherId = teacherId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        loadClasses();
    }

    private void buildUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        topPanel.add(new JLabel("Lớp (Ongoing):"));
        cmbClass = new JComboBox<>();
        cmbClass.setPreferredSize(new Dimension(300, 28));
        topPanel.add(cmbClass);
        topPanel.add(Box.createHorizontalStrut(10));
        JButton btnLoad = new JButton("Tải danh sách");
        btnLoad.addActionListener(e -> loadResults());
        topPanel.add(btnLoad);
        JButton btnSave = new JButton("Lưu điểm");
        btnSave.addActionListener(e -> saveResults());
        topPanel.add(btnSave);
        topPanel.add(Box.createHorizontalStrut(15));
        JButton btnClearAll = new JButton("Xóa tất cả điểm");
        btnClearAll.addActionListener(e -> clearAllScores());
        topPanel.add(btnClearAll);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(3).setCellRenderer(new GradeColorRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Hướng dẫn"));
        JLabel lblGuide = new JLabel(
                "<html>• Nhập điểm từ 0.00 đến 100.00 → Xếp loại sẽ được tính tự động<br/>"
                        + "• A+ (≥90) | A (≥85) | B+ (≥80) | B (≥70) | C+ (≥65) | C (≥55) | D+ (≥50) | D (≥40) | F (<40)<br/>"
                        + "• Chỉ lớp Ongoing mới được nhập điểm. Để trống ô điểm nếu chưa muốn chấm.</html>");
        bottomPanel.add(lblGuide);
        JButton btnStats = new JButton("Thống kê");
        btnStats.addActionListener(e -> showStats());
        bottomPanel.add(btnStats);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadClasses() {
        try {
            List<ClassEntity> classes = resultService.getOngoingClassesByTeacher(teacherId);
            cmbClass.removeAllItems();
            classes.stream()
                    .map(c -> new ClassItem(c.getClassId(),
                            c.getClassName() + " (" + c.getCourse().getCourseName() + ")"))
                    .forEach(cmbClass::addItem);
            if (classes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Bạn hiện không có lớp nào đang ở trạng thái Ongoing.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách lớp: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadResults() {
        ClassItem selected = (ClassItem) cmbClass.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp."); return; }
        try {
            List<Enrollment> enrollments = resultService.getEnrolledStudents(selected.classId());
            Map<Long, Result> existingMap = resultService.getResultMap(selected.classId());
            List<Object[]> rows = enrollments.stream()
                    .map(e -> {
                        Long sid = e.getStudent().getStudentId();
                        Result existing = existingMap.get(sid);
                        return new Object[]{
                                sid,
                                e.getStudent().getFullName(),
                                existing != null ? existing.getScore() : null,
                                existing != null ? existing.getGrade() : "",
                                existing != null && existing.getComment() != null ? existing.getComment() : ""};
                    })
                    .collect(Collectors.toList());
            tableModel.setData(rows);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveResults() {
        ClassItem selected = (ClassItem) cmbClass.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp."); return; }
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        List<Object[]> rows = tableModel.getData();
        if (rows.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để lưu."); return; }

        boolean hasInvalid = rows.stream()
                .filter(r -> r[2] != null)
                .anyMatch(r -> ((BigDecimal) r[2]).doubleValue() < 0 || ((BigDecimal) r[2]).doubleValue() > 100);
        if (hasInvalid) {
            JOptionPane.showMessageDialog(this, "Điểm phải nằm trong khoảng 0.00 đến 100.00!",
                    "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<ResultService.ResultRecord> records = rows.stream()
                    .map(row -> new ResultService.ResultRecord((Long) row[0], (BigDecimal) row[2], (String) row[4]))
                    .collect(Collectors.toList());
            resultService.saveResults(selected.classId(), records);
            long savedCount = rows.stream().filter(r -> r[2] != null).count();
            JOptionPane.showMessageDialog(this,
                    "Lưu điểm thành công!\nĐã lưu: " + savedCount + "/" + rows.size() + " học viên.");
            loadResults();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu điểm: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAllScores() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tất cả điểm trên bảng?\n(Chưa ảnh hưởng DB cho đến khi bấm Lưu)",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.getData().forEach(row -> { row[2] = null; row[3] = ""; row[4] = ""; });
            tableModel.fireTableDataChanged();
        }
    }

    private void showStats() {
        List<Object[]> rows = tableModel.getData();
        if (rows.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để thống kê."); return; }

        long total = rows.size();
        long graded = rows.stream().filter(r -> r[2] != null).count();
        long notGraded = total - graded;
        double avg = rows.stream().filter(r -> r[2] != null)
                .mapToDouble(r -> ((BigDecimal) r[2]).doubleValue()).average().orElse(0.0);
        Map<Boolean, Long> passFailMap = rows.stream().filter(r -> r[2] != null)
                .collect(Collectors.partitioningBy(r -> ((BigDecimal) r[2]).doubleValue() >= 40, Collectors.counting()));
        long passed = passFailMap.getOrDefault(true, 0L);
        long failed = passFailMap.getOrDefault(false, 0L);
        Map<String, Long> gradeDistribution = rows.stream()
                .filter(r -> r[2] != null && r[3] != null && !((String) r[3]).isEmpty())
                .collect(Collectors.groupingBy(r -> (String) r[3], Collectors.counting()));
        StringBuilder distStr = new StringBuilder();
        gradeDistribution.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(e -> distStr.append(e.getKey()).append(": ").append(e.getValue()).append("  "));

        String message = String.format(
                """
                Tổng số học viên: %d
                Đã chấm điểm: %d | Chưa chấm: %d
                Điểm trung bình: %.2f
                Đạt (≥40): %d | Không đạt: %d
                Phân bố xếp loại: %s""",
                total, graded, notGraded, avg, passed, failed, distStr);
        JOptionPane.showMessageDialog(this, message, "Thống kê điểm", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== INNER CLASSES ====================

    private record ClassItem(Long classId, String displayName) {
        @Override public String toString() { return displayName; }
    }


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
                    case "F"       -> new Color(255, 199, 206);
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

    static class GradeTableModel extends AbstractTableModel {

        // Indexes for Object[] data rows:
        // 0=studentId(Long), 1=fullName(String), 2=score(BigDecimal), 3=grade(String), 4=comment(String)
        private final String[] columns = { "STT", "Họ tên", "Điểm", "Xếp loại", "Nhận xét" };
        private List<Object[]> data = new ArrayList<>();

        void setData(List<Object[]> rows) { this.data = rows; fireTableDataChanged(); }
        List<Object[]> getData()           { return data; }

        @Override public int     getRowCount()              { return data.size(); }
        @Override public int     getColumnCount()           { return columns.length; }
        @Override public String  getColumnName(int col)     { return columns[col]; }
        @Override public boolean isCellEditable(int r, int c) { return c == 2 || c == 4; }

        @Override
        public Object getValueAt(int row, int col) {
            Object[] r = data.get(row);
            return switch (col) {
                case 0 -> row + 1;
                case 1 -> r[1];                                            // fullName
                case 2 -> r[2] != null ? r[2].toString() : "";             // score
                case 3 -> r[3];                                            // grade
                case 4 -> r[4];                                            // comment
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            Object[] r = data.get(row);
            switch (col) {
                case 2 -> {
                    String text = value != null ? value.toString().trim() : "";
                    if (text.isEmpty()) {
                        r[2] = null;
                        r[3] = "";
                    } else {
                        try {
                            BigDecimal score = new BigDecimal(text);
                            if (score.doubleValue() < 0 || score.doubleValue() > 100) {
                                JOptionPane.showMessageDialog(null,
                                        "Điểm phải từ 0.00 đến 100.00!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            r[2] = score;
                            r[3] = ResultService.calculateGrade(score);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    fireTableRowsUpdated(row, row);
                }
                case 4 -> { r[4] = value != null ? value.toString() : ""; fireTableCellUpdated(row, col); }
            }
        }
    }
}
