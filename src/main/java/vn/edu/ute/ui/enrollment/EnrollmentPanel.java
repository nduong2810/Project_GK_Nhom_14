package vn.edu.ute.ui.enrollment;

import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.ClassService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.StudentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class EnrollmentPanel extends JPanel {
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final ClassService classService;

    private final EnrollmentTableModel tableModel = new EnrollmentTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = createPlaceholderField("Nhập tên HV, Tên Lớp...", 25);

    public EnrollmentPanel(EnrollmentService enrollmentService, StudentService studentService, ClassService classService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.classService = classService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel topToolbar = new JPanel(new BorderLayout());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Ghi Danh Mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm Mới");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

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
        try { tableModel.setData(enrollmentService.getAllEnrollments()); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage()); }
    }

    private void onAdd() {
        try {
            EnrollmentFormDialog dlg = new EnrollmentFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ghi Danh Khóa Học", null,
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

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn một bản ghi để sửa."); return; }

        try {
            EnrollmentFormDialog dlg = new EnrollmentFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Ghi Danh", tableModel.getAt(row),
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

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn một bản ghi để xóa."); return; }
        Enrollment e = tableModel.getAt(row);
        if (JOptionPane.showConfirmDialog(this, "Xóa ghi danh của học viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { enrollmentService.deleteEnrollment(e.getEnrollmentId()); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
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