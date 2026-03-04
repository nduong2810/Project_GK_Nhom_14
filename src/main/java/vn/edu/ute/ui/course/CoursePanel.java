package vn.edu.ute.ui.course;

import vn.edu.ute.model.Course;
import vn.edu.ute.service.CourseService;
import javax.swing.*;
import java.awt.*;

public class CoursePanel extends JPanel {
    private final CourseService courseService;
    private final CourseTableModel tableModel = new CourseTableModel();
    private final JTable table = new JTable(tableModel);

    public CoursePanel(CourseService courseService) {
        this.courseService = courseService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm Mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm Mới");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        topPanel.add(btnAdd); topPanel.add(btnEdit); topPanel.add(btnDelete); topPanel.add(btnRefresh);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        try { tableModel.setData(courseService.getAllCourses()); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage()); }
    }

    private void onAdd() {
        CourseFormDialog dlg = new CourseFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Khóa Học", null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try { courseService.createCourse(dlg.getCourse()); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn khóa học để sửa."); return; }

        CourseFormDialog dlg = new CourseFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Khóa Học", tableModel.getAt(row));
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try { courseService.updateCourse(dlg.getCourse()); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn khóa học để xóa."); return; }
        Course c = tableModel.getAt(row);
        if (JOptionPane.showConfirmDialog(this, "Xóa khóa học: " + c.getCourseName() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { courseService.deleteCourse(c.getCourseId()); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }
}