package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

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

    private void buildUI() {
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
        new SwingWorker<java.util.List<Room>, Void>() {
            @Override
            protected java.util.List<Room> doInBackground() throws Exception {
                return roomService.getAllRooms();
            }

            @Override
            protected void done() {
                try {
                    tableModel.setData(get());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RoomPanel.this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void onAdd() {
        try {
            RoomFormDialog dlg = new RoomFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Thêm Phòng Học", null, branchService.getActiveBranches());
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                roomService.createRoom(dlg.getRoom());
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

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
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

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
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + ex.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}