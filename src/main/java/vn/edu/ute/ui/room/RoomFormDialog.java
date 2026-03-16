package vn.edu.ute.ui.room;

import vn.edu.ute.model.Branch;
import vn.edu.ute.model.Room;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Lớp `RoomFormDialog` tạo ra một hộp thoại để người dùng nhập hoặc chỉnh sửa thông tin
 * của một phòng học.
 */
public class RoomFormDialog extends JDialog {
    // Các thành phần UI cho form
    private final JTextField txtName = new JTextField(25);
    private final JTextField txtCapacity = new JTextField(10);
    private final JTextField txtLocation = new JTextField(25);
    private final JComboBox<Branch> cboBranch = new JComboBox<>();
    private final JComboBox<Room.Status> cboStatus = new JComboBox<>(Room.Status.values());

    private boolean saved = false; // Cờ để kiểm tra xem người dùng đã nhấn "Lưu" chưa
    private Room room; // Đối tượng Room đang được thêm hoặc sửa

    /**
     * Constructor của hộp thoại.
     * @param owner Frame cha của hộp thoại.
     * @param title Tiêu đề của hộp thoại.
     * @param existing Đối tượng Room hiện có để sửa (nếu là null thì là chế độ thêm mới).
     * @param branches Danh sách các chi nhánh để hiển thị trong ComboBox.
     */
    public RoomFormDialog(Frame owner, String title, Room existing, List<Branch> branches) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Thêm các chi nhánh vào ComboBox
        cboBranch.addItem(null); // Thêm một item null để đại diện cho "Chưa chọn"
        branches.forEach(cboBranch::addItem);

        // Tùy chỉnh cách hiển thị các item trong ComboBox chi nhánh
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

        // Nếu là chế độ sửa, điền thông tin của phòng học hiện có vào form
        if (existing != null) {
            this.room = existing;
            txtName.setText(existing.getRoomName());
            txtCapacity.setText(String.valueOf(existing.getCapacity()));
            txtLocation.setText(existing.getLocation() != null ? existing.getLocation() : "");
            cboStatus.setSelectedItem(existing.getStatus());
            // Tìm và chọn chi nhánh tương ứng trong ComboBox
            if (existing.getBranch() != null) {
                branches.stream()
                        .filter(b -> b.getBranchId().equals(existing.getBranch().getBranchId()))
                        .findFirst()
                        .ifPresent(cboBranch::setSelectedItem);
            }
        } else {
            // Nếu là chế độ thêm mới, tạo một đối tượng Room mới
            this.room = new Room();
            cboStatus.setSelectedItem(Room.Status.Active); // Mặc định là Active
        }
        pack(); // Tự động điều chỉnh kích thước hộp thoại cho vừa với nội dung
        setLocationRelativeTo(owner); // Hiển thị hộp thoại ở giữa frame cha
    }

    /**
     * Xây dựng giao diện người dùng của hộp thoại.
     */
    private void buildUI() {
        UITheme.styleDialog(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        // Thêm các trường nhập liệu vào form
        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Tên Phòng (*):"), g);
        g.gridx = 1; form.add(txtName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Sức Chứa (*):"), g);
        g.gridx = 1; form.add(txtCapacity, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Vị Trí:"), g);
        g.gridx = 1; form.add(txtLocation, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Chi Nhánh:"), g);
        g.gridx = 1; cboBranch.setFont(UITheme.FONT_BODY); form.add(cboBranch, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Trạng Thái:"), g);
        g.gridx = 1; form.add(cboStatus, g);

        // Tạo các nút hành động (Lưu, Hủy)
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

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Lưu".
     * Kiểm tra dữ liệu nhập, cập nhật đối tượng `room` và đóng hộp thoại.
     */
    private void onSave() {
        try {
            // Kiểm tra các trường bắt buộc
            if (txtName.getText().trim().isEmpty())
                throw new IllegalArgumentException("Vui lòng nhập Tên phòng.");
            int capacity;
            try {
                capacity = Integer.parseInt(txtCapacity.getText().trim());
                if (capacity <= 0)
                    throw new NumberFormatException();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Sức chứa phải là một số nguyên dương.");
            }

            // Cập nhật thông tin vào đối tượng `room`
            room.setRoomName(txtName.getText().trim());
            room.setCapacity(capacity);
            room.setLocation(txtLocation.getText().trim());
            room.setBranch((Branch) cboBranch.getSelectedItem());
            room.setStatus((Room.Status) cboStatus.getSelectedItem());

            saved = true; // Đánh dấu là đã lưu thành công
            dispose(); // Đóng hộp thoại
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Trả về `true` nếu người dùng đã nhấn "Lưu".
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Trả về đối tượng `Room` với thông tin đã được cập nhật từ form.
     */
    public Room getRoom() {
        return room;
    }
}
