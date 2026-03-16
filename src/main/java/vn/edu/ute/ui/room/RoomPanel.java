package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Lớp `RoomPanel` tạo ra giao diện chính để quản lý phòng học.
 * Nó bao gồm một bảng hiển thị danh sách phòng học, các nút chức năng (thêm, sửa, xóa)
 * và một ô tìm kiếm để lọc dữ liệu.
 */
public class RoomPanel extends JPanel {
    private final RoomService roomService;
    private final BranchService branchService;
    private final RoomTableModel tableModel = new RoomTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập ID, tên phòng, vị trí...", 25);

    public RoomPanel(RoomService roomService, BranchService branchService) {
        this.roomService = roomService;
        this.branchService = branchService;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    /**
     * Xây dựng các thành phần giao diện người dùng.
     */
    private void buildUI() {
        // Tạo thanh công cụ với các nút chức năng
        JPanel toolbar = UITheme.createToolbar();
        JButton btnAdd = UITheme.createSuccessButton("Thêm Mới", "➕");
        JButton btnEdit = UITheme.createPrimaryButton("Sửa", "✏️");
        JButton btnDelete = UITheme.createDangerButton("Xóa", "🗑");
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "🔄");

        // Gán hành động cho các nút
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);

        // Tạo panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);

        // Thêm listener cho ô tìm kiếm để tự động lọc bảng khi người dùng nhập
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
        });

        // Sắp xếp layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(toolbar, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Cấu hình và thêm bảng vào panel
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Tải dữ liệu phòng học từ service một cách bất đồng bộ bằng SwingWorker.
     * Điều này giúp giao diện không bị "đơ" khi đang tải dữ liệu.
     */
    private void loadData() {
        new SwingWorker<java.util.List<Room>, Void>() {
            @Override
            protected java.util.List<Room> doInBackground() throws Exception {
                // Tải dữ liệu trong một luồng nền
                return roomService.getAllRooms();
            }

            @Override
            protected void done() {
                try {
                    // Cập nhật model của bảng trên Event Dispatch Thread sau khi tải xong
                    tableModel.setData(get());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RoomPanel.this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Thêm mới".
     * Mở hộp thoại `RoomFormDialog` để người dùng nhập thông tin.
     */
    private void onAdd() {
        try {
            RoomFormDialog dlg = new RoomFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Thêm Phòng Học", null, branchService.getActiveBranches());
            dlg.setVisible(true);
            // Nếu người dùng nhấn "Lưu"
            if (dlg.isSaved()) {
                roomService.createRoom(dlg.getRoom());
                loadData(); // Tải lại dữ liệu để hiển thị phòng mới
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Sửa".
     * Lấy phòng được chọn từ bảng và mở `RoomFormDialog` với thông tin của phòng đó.
     */
    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng học trong bảng để sửa.");
            return;
        }
        try {
            Room selectedRoom = tableModel.getAt(selectedRow);
            RoomFormDialog dlg = new RoomFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Sửa Phòng Học", selectedRoom, branchService.getActiveBranches());
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                roomService.updateRoom(dlg.getRoom());
                loadData(); // Tải lại dữ liệu để cập nhật thay đổi
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Xóa".
     * Yêu cầu xác nhận từ người dùng trước khi xóa.
     */
    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng học trong bảng để xóa.");
            return;
        }
        Room selectedRoom = tableModel.getAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phòng: " + selectedRoom.getRoomName() + "?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomService.deleteRoom(selectedRoom.getRoomId());
                loadData(); // Tải lại dữ liệu sau khi xóa
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
