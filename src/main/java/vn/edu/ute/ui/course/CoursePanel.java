package vn.edu.ute.ui.course;

import vn.edu.ute.model.Course;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CoursePanel extends JPanel {
    private final CourseService courseService;
    private final CourseTableModel tableModel = new CourseTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập ID hoặc tên khóa học...", 25);

    public CoursePanel(CourseService courseService) {
        this.courseService = courseService;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    private void buildUI() {
        // ===== Toolbar =====
        JPanel toolbar = UITheme.createToolbar();
        JButton btnAdd = UITheme.createSuccessButton("Thêm Mới", "➕");
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

        JPanel searchPanel = UITheme.createSearchPanel(txtSearch);

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

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            tableModel.setData(courseService.getAllCourses());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void onAdd() {
        CourseFormDialog dlg = new CourseFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Khóa Học",
                null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                courseService.createCourse(dlg.getCourse());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khóa học để sửa.");
            return;
        }
        CourseFormDialog dlg = new CourseFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Khóa Học",
                tableModel.getAt(row));
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                courseService.updateCourse(dlg.getCourse());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khóa học để xóa.");
            return;
        }
        Course c = tableModel.getAt(row);
        if (JOptionPane.showConfirmDialog(this, "Xóa khóa học: " + c.getCourseName() + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                courseService.deleteCourse(c.getCourseId());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }
}