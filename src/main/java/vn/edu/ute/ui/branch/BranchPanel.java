package vn.edu.ute.ui.branch;

import vn.edu.ute.model.Branch;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Lớp `BranchPanel` tạo giao diện quản lý các chi nhánh của trung tâm.
 */
public class BranchPanel extends JPanel {

    private final BranchService branchService;
    private final BranchTableModel tableModel = new BranchTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập ID, tên chi nhánh, địa chỉ...", 25);

    public BranchPanel(BranchService branchService) {
        this.branchService = branchService;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    /**
     * Xây dựng giao diện người dùng.
     */
    private void buildUI() {
        // Thanh công cụ
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

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);

        // Listener cho ô tìm kiếm
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

        // Bảng dữ liệu
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Tải dữ liệu chi nhánh từ service và cập nhật vào table model.
     */
    private void loadData() {
        try {
            tableModel.setData(branchService.getAllBranches());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu chi nhánh: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý sự kiện thêm mới chi nhánh.
     */
    private void onAdd() {
        BranchFormDialog dlg = new BranchFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Thêm Chi Nhánh", null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                branchService.createBranch(dlg.getBranch());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu chi nhánh: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý sự kiện sửa thông tin chi nhánh.
     */
    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chi nhánh trong bảng để sửa.");
            return;
        }
        Branch selected = tableModel.getAt(selectedRow);
        BranchFormDialog dlg = new BranchFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Sửa Chi Nhánh", selected);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                branchService.updateBranch(dlg.getBranch());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật chi nhánh: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xử lý sự kiện xóa chi nhánh.
     */
    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chi nhánh trong bảng để xóa.");
            return;
        }
        Branch selected = tableModel.getAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chi nhánh: " + selected.getBranchName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                branchService.deleteBranch(selected.getBranchId());
                loadData();
            } catch (Exception ex) {
                // Hiển thị thông báo lỗi từ service (ví dụ: không thể xóa vì còn phòng học)
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
