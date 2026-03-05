package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import vn.edu.ute.service.RoomService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RoomPanel extends JPanel {
    private final RoomService roomService;
    private final RoomTableModel tableModel = new RoomTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = createPlaceholderField("Nhập ID, tên phòng, vị trí...", 25);

    public RoomPanel(RoomService roomService) {
        this.roomService = roomService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel topToolbar = new JPanel(new BorderLayout());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm Mới");
        JButton btnEdit = new JButton("Sửa");
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
        JScrollPane scrollPane = new JScrollPane(table);

        add(topToolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            tableModel.setData(roomService.getAllRooms());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        RoomFormDialog dlg = new RoomFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Phòng Học", null);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            try {
                roomService.createRoom(dlg.getRoom());
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

        Room selectedRoom = tableModel.getAt(selectedRow);

        RoomFormDialog dlg = new RoomFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa Phòng Học", selectedRoom);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            try {
                roomService.updateRoom(dlg.getRoom());
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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phòng: " + selectedRoom.getRoomName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomService.deleteRoom(selectedRoom.getRoomId());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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