package vn.edu.ute.ui.classmgmt;

import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.service.ClassService;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.TeacherService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ClassPanel extends JPanel {
    private final ClassService classService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final RoomService roomService;

    private final ClassTableModel tableModel = new ClassTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = createPlaceholderField("Nhập tên lớp, khóa học, giáo viên...", 25);

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
        JPanel topToolbar = new JPanel(new BorderLayout());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Mở Lớp Mới");
        JButton btnEdit = new JButton("Sửa Lớp");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm Mới");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("🔍 Tìm kiếm:"));
        searchPanel.add(txtSearch);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
        });

        topToolbar.add(btnPanel, BorderLayout.WEST);
        topToolbar.add(searchPanel, BorderLayout.EAST);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(topToolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        try { tableModel.setData(classService.getAllClasses()); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu lớp: " + ex.getMessage()); }
    }

    private void onAdd() {
        try {
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

    private JTextField createPlaceholderField(String placeholder, int columns) {
        JTextField field = new JTextField(columns) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() - ins.bottom - 4);
                    g2.dispose();
                }
            }
        };
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.repaint(); }
            @Override public void focusLost(FocusEvent e) { field.repaint(); }
        });
        return field;
    }
}