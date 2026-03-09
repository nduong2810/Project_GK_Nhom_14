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
 * Tab "Điểm danh" — Giáo viên chọn lớp, chọn ngày, điểm danh học viên.
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

    private void buildUI() {
        // ===== Thanh công cụ trên cùng =====
        JPanel topPanel = UITheme.createToolbar();

        topPanel.add(UITheme.createFormLabel("Lớp:"));
        cmbClass = new JComboBox<>();
        cmbClass.setPreferredSize(new Dimension(250, UITheme.FIELD_HEIGHT));
        cmbClass.setFont(UITheme.FONT_BODY);
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

        // ===== Bảng điểm danh =====
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Cột "Trạng thái" dùng JComboBox editor
        JComboBox<Attendance.Status> statusCombo = new JComboBox<>(Attendance.Status.values());
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusCombo));

        // Renderer cho cột Trạng thái: tô màu theo trạng thái
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected && value instanceof Attendance.Status status) {
                    switch (status) {
                        case Present -> c.setBackground(UITheme.SUCCESS_LIGHT);
                        case Absent -> c.setBackground(UITheme.DANGER_LIGHT);
                        case Late -> c.setBackground(UITheme.WARNING_LIGHT);
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.NEUTRAL_200, 1, true));

        // ===== Summary panel phía dưới =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(UITheme.createTitledBorder("Thống kê"));
        bottomPanel.setBackground(UITheme.BG_CARD);
        JButton btnShowStats = UITheme.createOutlineButton("Xem thống kê");
        btnShowStats.addActionListener(e -> showStats());
        bottomPanel.add(btnShowStats);

        // ===== Ráp vào =====
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==================== DATA LOADING ====================

    private void loadClasses() {
        try {
            List<ClassEntity> classes = attendanceService.getClassesByTeacher(teacherId);
            cmbClass.removeAllItems();
            classes.stream()
                    .map(c -> new ClassItem(c.getClassId(), c.getClassName()
                            + " (" + c.getCourse().getCourseName() + ")"
                            + " [" + c.getStatus() + "]",
                            c.getStatus().toString()))
                    .forEach(cmbClass::addItem);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách lớp: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttendance() {
        ClassItem selected = (ClassItem) cmbClass.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp.");
            return;
        }
        LocalDate date = parseDate(txtDate.getText());
        if (date == null) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Định dạng: dd/MM/yyyy");
            return;
        }
        try {
            List<Enrollment> enrollments = attendanceService.getEnrolledStudents(selected.classId);
            Map<Long, Attendance> existingMap = attendanceService.getAttendanceMap(selected.classId, date);
            List<AttendanceRow> rows = enrollments.stream()
                    .map(e -> {
                        Long sid = e.getStudent().getStudentId();
                        Attendance existing = existingMap.get(sid);
                        return new AttendanceRow(sid, e.getStudent().getFullName(),
                                e.getStudent().getPhone() != null ? e.getStudent().getPhone() : "",
                                existing != null ? existing.getStatus() : Attendance.Status.Present,
                                existing != null && existing.getNote() != null ? existing.getNote() : "");
                    })
                    .collect(Collectors.toList());
            tableModel.setData(rows);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải điểm danh: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== ACTIONS ====================

    private void saveAttendance() {
        ClassItem selected = (ClassItem) cmbClass.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp.");
            return;
        }
        if (!"Ongoing".equalsIgnoreCase(selected.classStatus())) {
            String reason = switch (selected.classStatus()) {
                case "Planned" -> "Lớp chưa được mở (Planned).";
                case "Open" -> "Lớp mới mở tuyển sinh, chưa bắt đầu học (Open).";
                case "Completed" -> "Lớp đã kết thúc (Completed).";
                default -> "Lớp không ở trạng thái cho phép điểm danh.";
            };
            JOptionPane.showMessageDialog(this, reason + " Chỉ có thể điểm danh cho lớp đang diễn ra (Ongoing).",
                    "Không thể lưu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate date = parseDate(txtDate.getText());
        if (date == null) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Định dạng: dd/MM/yyyy");
            return;
        }
        List<AttendanceRow> rows = tableModel.getData();
        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu điểm danh để lưu.");
            return;
        }
        try {
            List<AttendanceService.AttendanceRecord> records = rows.stream()
                    .map(row -> new AttendanceService.AttendanceRecord(row.studentId, row.status, row.note))
                    .collect(Collectors.toList());
            attendanceService.saveAttendance(selected.classId, date, records);
            JOptionPane.showMessageDialog(this, "Lưu điểm danh thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu điểm danh: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAll(Attendance.Status status) {
        tableModel.getData().forEach(row -> row.status = status);
        tableModel.fireTableDataChanged();
    }

    private void showStats() {
        List<AttendanceRow> rows = tableModel.getData();
        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để thống kê.");
            return;
        }
        Map<Attendance.Status, Long> stats = rows.stream()
                .collect(Collectors.groupingBy(r -> r.status, Collectors.counting()));
        long present = stats.getOrDefault(Attendance.Status.Present, 0L);
        long absent = stats.getOrDefault(Attendance.Status.Absent, 0L);
        long late = stats.getOrDefault(Attendance.Status.Late, 0L);
        String message = String.format("Tổng số học viên: %d\n✔ Có mặt: %d\n✘ Vắng: %d\n⏰ Trễ: %d",
                rows.size(), present, absent, late);
        JOptionPane.showMessageDialog(this, message, "Thống kê điểm danh", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== HELPERS ====================

    private LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty())
            return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ==================== INNER CLASSES ====================

    private record ClassItem(Long classId, String displayName, String classStatus) {
        @Override
        public String toString() {
            return displayName;
        }
    }

    static class AttendanceRow {
        Long studentId;
        String fullName;
        String phone;
        Attendance.Status status;
        String note;

        AttendanceRow(Long studentId, String fullName, String phone, Attendance.Status status, String note) {
            this.studentId = studentId;
            this.fullName = fullName;
            this.phone = phone;
            this.status = status;
            this.note = note;
        }
    }

    static class AttendanceTableModel extends AbstractTableModel {
        private final String[] columns = { "STT", "Họ tên", "Số ĐT", "Trạng thái", "Ghi chú" };
        private List<AttendanceRow> data = new ArrayList<>();

        void setData(List<AttendanceRow> data) {
            this.data = data;
            fireTableDataChanged();
        }

        List<AttendanceRow> getData() {
            return data;
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
            return col == 3 || col == 4;
        }

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
            switch (col) {
                case 3 -> r.status = (Attendance.Status) value;
                case 4 -> r.note = value != null ? value.toString() : "";
            }
            fireTableCellUpdated(row, col);
        }
    }
}
