package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import vn.edu.ute.service.RoomService;

import javax.swing.*;
import java.awt.*;

public class RoomPanel extends JPanel {
    private final RoomService roomService;
    private final RoomTableModel tableModel = new RoomTableModel();
    private final JTable table = new JTable(tableModel);

    public RoomPanel(RoomService roomService) {
        this.roomService = roomService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Căn lề cho đẹp

        buildUI();
        loadData(); // Tải dữ liệu từ DB lên ngay khi mở tab
    }

    private void buildUI() {
        // 1. Thanh công cụ (Nút bấm)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm Mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm Mới");

        // Gắn sự kiện
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        topPanel.add(btnAdd);
        topPanel.add(btnEdit);
        topPanel.add(btnDelete);
        topPanel.add(btnRefresh);

        // 2. Bảng dữ liệu
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        // 3. Ráp vào Panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            // Gọi hàm getAllRooms từ Service
            tableModel.setData(roomService.getAllRooms());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        // Mở Dialog thêm mới (chuyền null vào existing)
        RoomFormDialog dlg = new RoomFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Phòng Học", null);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            try {
                roomService.createRoom(dlg.getRoom());
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng học trong bảng để sửa.");
            return;
        }

        // Lấy dữ liệu của dòng đang chọn
        Room selectedRoom = tableModel.getAt(selectedRow);

        // Mở Dialog sửa (truyền selectedRoom vào)
        RoomFormDialog dlg = new RoomFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Phòng Học", selectedRoom);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            try {
                roomService.updateRoom(dlg.getRoom());
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phòng học trong bảng để xóa.");
            return;
        }

        Room selectedRoom = tableModel.getAt(selectedRow);

        // Hỏi lại cho chắc chắn
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phòng: " + selectedRoom.getRoomName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomService.deleteRoom(selectedRoom.getRoomId());
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}