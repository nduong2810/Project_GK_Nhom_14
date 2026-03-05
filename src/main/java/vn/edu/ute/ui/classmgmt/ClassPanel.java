package vn.edu.ute.ui.classmgmt;

import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.service.ClassService;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.TeacherService;

import javax.swing.*;
import java.awt.*;

public class ClassPanel extends JPanel {
    private final ClassService classService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final RoomService roomService;

    private final ClassTableModel tableModel = new ClassTableModel();
    private final JTable table = new JTable(tableModel);

    public ClassPanel(ClassService classService, CourseService courseService, TeacherService teacherService, RoomService roomService) {
        this.classService = classService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.roomService = roomService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Mở Lớp Mới");
        JButton btnEdit = new JButton("Sửa Lớp");
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
        try { tableModel.setData(classService.getAllClasses()); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu lớp: " + ex.getMessage()); }
    }

    private void onAdd() {
        try {
            // Lấy danh sách đang hoạt động để đưa lên Form
            ClassFormDialog dlg = new ClassFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Mở Lớp Mới", null,
                    courseService.getActiveCourses(), teacherService.getActiveTeachers(), roomService.getActiveRooms());
            dlg.setVisible(true);

            if (dlg.isSaved()) {
                classService.createClass(dlg.getClassEntity());
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi chuẩn bị dữ liệu: " + ex.getMessage());
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp học để sửa."); return; }

        try {
            ClassFormDialog dlg = new ClassFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Lớp Học", tableModel.getAt(row),
                    courseService.getActiveCourses(), teacherService.getActiveTeachers(), roomService.getActiveRooms());
            dlg.setVisible(true);

            if (dlg.isSaved()) {
                classService.updateClass(dlg.getClassEntity());
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp để xóa."); return; }
        ClassEntity c = tableModel.getAt(row);
        if (JOptionPane.showConfirmDialog(this, "Xóa lớp: " + c.getClassName() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { classService.deleteClass(c.getClassId()); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage()); }
        }
    }
}