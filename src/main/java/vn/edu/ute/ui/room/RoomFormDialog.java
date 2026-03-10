package vn.edu.ute.ui.room;

import vn.edu.ute.model.Branch;
import vn.edu.ute.model.Room;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoomFormDialog extends JDialog {
    private final JTextField txtName = new JTextField(25);
    private final JTextField txtCapacity = new JTextField(10);
    private final JTextField txtLocation = new JTextField(25);
    private final JComboBox<Branch> cboBranch = new JComboBox<>();
    private final JComboBox<Room.Status> cboStatus = new JComboBox<>(Room.Status.values());

    private boolean saved = false;
    private Room room;

    public RoomFormDialog(Frame owner, String title, Room existing, List<Branch> branches) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Populate branch combo
        cboBranch.addItem(null);
        branches.forEach(cboBranch::addItem);

        // Renderer dùng lambda: hiển thị tên chi nhánh
        cboBranch.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setFont(UITheme.FONT_BODY);
            label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            if (value instanceof Branch b) {
                label.setText(b.getBranchName());
            } else {
                label.setText("-- Chưa chọn chi nhánh --");
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });

        buildUI();

        if (existing != null) {
            txtName.setText(existing.getRoomName());
            txtCapacity.setText(String.valueOf(existing.getCapacity()));
            txtLocation.setText(existing.getLocation() != null ? existing.getLocation() : "");
            cboStatus.setSelectedItem(existing.getStatus());
            // Set selected branch (tìm bằng Stream)
            if (existing.getBranch() != null) {
                branches.stream()
                        .filter(b -> b.getBranchId().equals(existing.getBranch().getBranchId()))
                        .findFirst()
                        .ifPresent(cboBranch::setSelectedItem);
            }
            this.room = existing;
        } else {
            this.room = new Room();
            cboStatus.setSelectedItem(Room.Status.Active);
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        UITheme.styleDialog(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Tên Phòng (*):"), g);
        g.gridx = 1;
        form.add(txtName, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Sức Chứa (*):"), g);
        g.gridx = 1;
        form.add(txtCapacity, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Vị Trí:"), g);
        g.gridx = 1;
        form.add(txtLocation, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Chi Nhánh:"), g);
        g.gridx = 1;
        cboBranch.setFont(UITheme.FONT_BODY);
        form.add(cboBranch, g);

        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Trạng Thái:"), g);
        g.gridx = 1;
        form.add(cboStatus, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton btnSave = UITheme.createPrimaryButton("Lưu", "");
        JButton btnCancel = UITheme.createOutlineButton("Hủy");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        actions.add(btnSave);
        actions.add(btnCancel);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void onSave() {
        try {
            if (txtName.getText().trim().isEmpty())
                throw new IllegalArgumentException("Vui lòng nhập Tên phòng.");
            int capacity;
            try {
                capacity = Integer.parseInt(txtCapacity.getText().trim());
                if (capacity <= 0)
                    throw new NumberFormatException();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Sức chứa phải là số nguyên > 0.");
            }

            room.setRoomName(txtName.getText().trim());
            room.setCapacity(capacity);
            room.setLocation(txtLocation.getText().trim());
            room.setBranch((Branch) cboBranch.getSelectedItem());
            room.setStatus((Room.Status) cboStatus.getSelectedItem());
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Room getRoom() {
        return room;
    }
}