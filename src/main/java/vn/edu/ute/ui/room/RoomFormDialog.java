package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import javax.swing.*;
import java.awt.*;

public class RoomFormDialog extends JDialog {
    private final JTextField txtName = new JTextField(25);
    private final JTextField txtCapacity = new JTextField(10);
    private final JTextField txtLocation = new JTextField(25);
    private final JComboBox<Room.Status> cboStatus = new JComboBox<>(Room.Status.values());

    private boolean saved = false;
    private Room room;

    public RoomFormDialog(Frame owner, String title, Room existing) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();

        if (existing != null) {
            txtName.setText(existing.getRoomName());
            txtCapacity.setText(String.valueOf(existing.getCapacity()));
            txtLocation.setText(existing.getLocation() != null ? existing.getLocation() : "");
            cboStatus.setSelectedItem(existing.getStatus());
            this.room = existing;
        } else {
            this.room = new Room();
            cboStatus.setSelectedItem(Room.Status.Active);
        }
        pack(); setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Tên Phòng (*):"), g);
        g.gridx = 1; form.add(txtName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Sức Chứa (*):"), g);
        g.gridx = 1; form.add(txtCapacity, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Vị Trí:"), g);
        g.gridx = 1; form.add(txtLocation, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Trạng Thái:"), g);
        g.gridx = 1; form.add(cboStatus, g);

        JButton btnSave = new JButton("Lưu"); JButton btnCancel = new JButton("Hủy");
        btnSave.addActionListener(e -> onSave()); btnCancel.addActionListener(e -> dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnSave); actions.add(btnCancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void onSave() {
        try {
            if (txtName.getText().trim().isEmpty()) throw new IllegalArgumentException("Vui lòng nhập Tên phòng.");
            int capacity;
            try {
                capacity = Integer.parseInt(txtCapacity.getText().trim());
                if (capacity <= 0) throw new NumberFormatException();
            } catch (Exception ex) { throw new IllegalArgumentException("Sức chứa phải là số nguyên > 0."); }

            room.setRoomName(txtName.getText().trim());
            room.setCapacity(capacity);
            room.setLocation(txtLocation.getText().trim());
            room.setStatus((Room.Status) cboStatus.getSelectedItem());

            saved = true; dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Room getRoom() { return room; }
}