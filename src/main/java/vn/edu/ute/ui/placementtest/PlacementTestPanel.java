package vn.edu.ute.ui.placementtest;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.service.PlacementTestService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Lớp `PlacementTestPanel` tạo giao diện quản lý kết quả thi xếp lớp.
 */
public class PlacementTestPanel extends JPanel {
    private final PlacementTestService testService;
    private final StudentService studentService;

    private final PlacementTestTableModel tableModel = new PlacementTestTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập tên Học viên...", 25);

    public PlacementTestPanel(PlacementTestService testService, StudentService studentService) {
        this.testService = testService;
        this.studentService = studentService;

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
        JButton btnAdd = UITheme.createSuccessButton("Thêm Kết Quả Thi", "➕");
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
     * Tải dữ liệu bất đồng bộ.
     */
    private void loadData() {
        new SwingWorker<List<PlacementTest>, Void>() {
            @Override
            protected List<PlacementTest> doInBackground() throws Exception {
                return testService.getAllTests();
            }
            @Override
            protected void done() {
                try { tableModel.setData(get()); }
                catch (Exception ex) { JOptionPane.showMessageDialog(PlacementTestPanel.this, "Lỗi tải dữ liệu: " + ex.getMessage()); }
            }
        }.execute();
    }

    /**
     * Xử lý sự kiện thêm mới.
     */
    private void onAdd() {
        try {
            PlacementTestFormDialog dlg = new PlacementTestFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Thêm Kết Quả Thi Xếp Lớp", null, studentService.getActiveStudents());
            dlg.setVisible(true);
            if (dlg.isSaved()) { testService.createTest(dlg.getPlacementTest()); loadData(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    /**
     * Xử lý sự kiện sửa.
     */
    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn một bản ghi để sửa."); return; }
        try {
            PlacementTestFormDialog dlg = new PlacementTestFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Sửa Kết Quả Thi", tableModel.getAt(row), studentService.getActiveStudents());
            dlg.setVisible(true);
            if (dlg.isSaved()) { testService.updateTest(dlg.getPlacementTest()); loadData(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    /**
     * Xử lý sự kiện xóa.
     */
    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn một bản ghi để xóa."); return; }
        PlacementTest p = tableModel.getAt(row);
        if (JOptionPane.showConfirmDialog(this, "Xóa kết quả thi này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { testService.deleteTest(p.getTestId()); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }
}
