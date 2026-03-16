package vn.edu.ute.ui.attendance;

import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.AttendanceService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp `AttendancePanel` tạo giao diện cho chức năng "Điểm danh".
 * Giáo viên chọn lớp, chọn ngày, sau đó điểm danh cho từng học viên.
 */
public class AttendancePanel extends JPanel {

    private final AttendanceService attendanceService;
    private final Long teacherId;

    private JComboBox<ClassItem> cmbClass;
    private JTextField txtDate;
    private final AttendanceTableModel tableModel = new AttendanceTableModel();
    private final JTable table = new JTable(tableModel);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AttendancePanel(AttendanceService attendanceService, Long teacherId) {
        this.attendanceService = attendanceService;
        this.teacherId = teacherId;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadClasses();
    }

    /**
     * Xây dựng giao diện người dùng.
     */
    private void buildUI() {
        // Thanh công cụ
        JPanel topPanel = UITheme.createToolbar();
        topPanel.add(UITheme.createFormLabel("Lớp:"));
        cmbClass = new JComboBox<>();
        cmbClass.setPreferredSize(new Dimension(250, UITheme.FIELD_HEIGHT));
        topPanel.add(cmbClass);

        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(UITheme.createFormLabel("Ngày:"));
        txtDate = UITheme.createSearchField("dd/MM/yyyy", 10);
        txtDate.setText(LocalDate.now().format(DATE_FMT));
        topPanel.add(txtDate);

        topPanel.add(Box.createHorizontalStrut(10));
        JButton btnLoad = UITheme.createPrimaryButton("Tải danh sách", "📋");
        btnLoad.addActionListener(e -> loadAttendance());
        topPanel.add(btnLoad);

        JButton btnSave = UITheme.createSuccessButton("Lưu điểm danh", "💾");
        btnSave.addActionListener(e -> saveAttendance());
        topPanel.add(btnSave);

        topPanel.add(Box.createHorizontalStrut(10));
        JButton btnMarkAllPresent = UITheme.createSuccessButton("Tất cả Có mặt", "✔");
        btnMarkAllPresent.addActionListener(e -> markAll(Attendance.Status.Present));
        topPanel.add(btnMarkAllPresent);

        JButton btnMarkAllAbsent = UITheme.createDangerButton("Tất cả Vắng", "✘");
        btnMarkAllAbsent.addActionListener(e -> markAll(Attendance.Status.Absent));
        topPanel.add(btnMarkAllAbsent);

        // Bảng điểm danh
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<>(Attendance.Status.values())));
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.NEUTRAL_200, 1, true));

        // Panel thống kê
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(UITheme.createTitledBorder("Thống kê"));
        bottomPanel.setBackground(UITheme.BG_CARD);
        JButton btnShowStats = UITheme.createOutlineButton("Xem thống kê");
        btnShowStats.addActionListener(e -> showStats());
        bottomPanel.add(btnShowStats);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Tải danh sách lớp học của giáo viên.
     */
    private void loadClasses() {
        try {
            List<ClassEntity> classes = attendanceService.getClassesByTeacher(teacherId);
            cmbClass.removeAllItems();
            classes.stream()
                    .map(c -> new ClassItem(c.getClassId(), c.getClassName() + " [" + c.getStatus() + "]", c.getStatus().toString()))
                    .forEach(cmbClass::addItem);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách lớp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tải danh sách điểm danh cho lớp và ngày đã chọn.
     */
    private void loadAttendance() {
        ClassItem selected = (ClassItem) cmbClass.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp."); return; }
        LocalDate date = parseDate(txtDate.getText());
        if (date == null) { JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Định dạng: dd/MM/yyyy"); return; }
        try {
            List<Enrollment> enrollments = attendanceService.getEnrolledStudents(selected.classId);
            Map<Long, Attendance> existingMap = attendanceService.getAttendanceMap(selected.classId, date);
            List<AttendanceRow> rows = enrollments.stream()
                    .map(e -> {
                        Attendance existing = existingMap.get(e.getStudent().getStudentId());
                        return new AttendanceRow(e.getStudent().getStudentId(), e.getStudent().getFullName(),
                                e.getStudent().getPhone(),
                                existing != null ? existing.getStatus() : Attendance.Status.Present,
                                existing != null ? existing.getNote() : "");
                    })
                    .collect(Collectors.toList());
            tableModel.setData(rows);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải điểm danh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lưu thông tin điểm danh.
     */
    private void saveAttendance() {
        ClassItem selected = (ClassItem) cmbClass.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp."); return; }
        if (!"Ongoing".equalsIgnoreCase(selected.classStatus())) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể điểm danh cho lớp đang diễn ra (Ongoing).", "Không thể lưu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate date = parseDate(txtDate.getText());
        if (date == null) { JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Định dạng: dd/MM/yyyy"); return; }
        if (tableModel.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để lưu."); return; }
        try {
            List<AttendanceService.AttendanceRecord> records = tableModel.getData().stream()
                    .map(row -> new AttendanceService.AttendanceRecord(row.studentId, row.status, row.note))
                    .collect(Collectors.toList());
            attendanceService.saveAttendance(selected.classId, date, records);
            JOptionPane.showMessageDialog(this, "Lưu điểm danh thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu điểm danh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Đánh dấu tất cả học viên với một trạng thái nhất định.
     */
    private void markAll(Attendance.Status status) {
        tableModel.getData().forEach(row -> row.status = status);
        tableModel.fireTableDataChanged();
    }

    /**
     * Hiển thị thống kê điểm danh.
     */
    private void showStats() {
        if (tableModel.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để thống kê."); return; }
        Map<Attendance.Status, Long> stats = tableModel.getData().stream()
                .collect(Collectors.groupingBy(r -> r.status, Collectors.counting()));
        String message = String.format("Tổng số: %d | Có mặt: %d | Vắng: %d | Trễ: %d",
                tableModel.getRowCount(),
                stats.getOrDefault(Attendance.Status.Present, 0L),
                stats.getOrDefault(Attendance.Status.Absent, 0L),
                stats.getOrDefault(Attendance.Status.Late, 0L));
        JOptionPane.showMessageDialog(this, message, "Thống kê điểm danh", JOptionPane.INFORMATION_MESSAGE);
    }

    private LocalDate parseDate(String text) {
        try { return LocalDate.parse(text.trim(), DATE_FMT); }
        catch (Exception e) { return null; }
    }

    // Lớp nội để lưu dữ liệu trong ComboBox và bảng
    private record ClassItem(Long classId, String displayName, String classStatus) {
        @Override public String toString() { return displayName; }
    }

    private static class AttendanceRow {
        Long studentId; String fullName; String phone; Attendance.Status status; String note;
        AttendanceRow(Long id, String name, String ph, Attendance.Status st, String n) {
            studentId = id; fullName = name; phone = ph; status = st; note = n;
        }
    }

    // Renderer để tô màu cho ô trạng thái
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
            if (!isS && v instanceof Attendance.Status s) {
                comp.setBackground(switch (s) {
                    case Present -> UITheme.SUCCESS_LIGHT;
                    case Absent -> UITheme.DANGER_LIGHT;
                    case Late -> UITheme.WARNING_LIGHT;
                });
            } else if (!isS) {
                comp.setBackground(Color.WHITE);
            }
            return comp;
        }
    }

    // TableModel cho bảng điểm danh
    private static class AttendanceTableModel extends AbstractTableModel {
        private final String[] columns = { "STT", "Họ tên", "Số ĐT", "Trạng thái", "Ghi chú" };
        private List<AttendanceRow> data = new ArrayList<>();

        void setData(List<AttendanceRow> data) { this.data = data; fireTableDataChanged(); }
        List<AttendanceRow> getData() { return data; }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public boolean isCellEditable(int r, int c) { return c == 3 || c == 4; }

        @Override
        public Object getValueAt(int row, int col) {
            AttendanceRow r = data.get(row);
            return switch (col) {
                case 0 -> row + 1;
                case 1 -> r.fullName;
                case 2 -> r.phone;
                case 3 -> r.status;
                case 4 -> r.note;
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            AttendanceRow r = data.get(row);
            if (col == 3) r.status = (Attendance.Status) value;
            else if (col == 4) r.note = value.toString();
            fireTableCellUpdated(row, col);
        }
    }
}
