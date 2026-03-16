package vn.edu.ute.ui.enrollment;

import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.ClassService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Lớp `EnrollmentPanel` tạo giao diện quản lý việc ghi danh của học viên.
 */
public class EnrollmentPanel extends JPanel {
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final ClassService classService;

    private final EnrollmentTableModel tableModel = new EnrollmentTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập tên HV, Tên Lớp...", 25);

    public EnrollmentPanel(EnrollmentService enrollmentService, StudentService studentService,
            ClassService classService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.classService = classService;

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
        JButton btnAdd = UITheme.createSuccessButton("Ghi Danh Mới", "➕");
        JButton btnEdit = UITheme.createPrimaryButton("Sửa", "✏️");
        JButton btnDelete = UITheme.createDangerButton("Xóa", "🗑");
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "🔄");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
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

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Tải dữ liệu ghi danh bất đồng bộ.
     */
    private void loadData() {
        new SwingWorker<java.util.List<Enrollment>, Void>() {
            @Override
            protected java.util.List<Enrollment> doInBackground() throws Exception {
                return enrollmentService.getAllEnrollments();
            }
            @Override
            protected void done() {
                try {
                    tableModel.setData(get());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EnrollmentPanel.this, "Lỗi tải dữ liệu: " + ex.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Xử lý sự kiện thêm mới ghi danh.
     */
    private void onAdd() {
        try {
            EnrollmentFormDialog dlg = new EnrollmentFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Ghi Danh Khóa Học", null,
                    studentService.getActiveStudents(), classService.getAllClasses());
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                enrollmentService.createEnrollment(dlg.getEnrollment());
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    /**
     * Xử lý sự kiện sửa ghi danh.
     */
    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một bản ghi để sửa.");
            return;
        }
        try {
            EnrollmentFormDialog dlg = new EnrollmentFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Sửa Ghi Danh", tableModel.getAt(row),
                    studentService.getActiveStudents(), classService.getAllClasses());
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                enrollmentService.updateEnrollment(dlg.getEnrollment());
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    /**
     * Xử lý sự kiện xóa ghi danh.
     */
    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một bản ghi để xóa.");
            return;
        }
        Enrollment e = tableModel.getAt(row);
        if (JOptionPane.showConfirmDialog(this, "Xóa ghi danh của học viên này?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                enrollmentService.deleteEnrollment(e.getEnrollmentId());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }
}
