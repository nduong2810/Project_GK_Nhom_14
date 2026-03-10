package vn.edu.ute.ui.notification;

import vn.edu.ute.model.Notification;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;

public class NotificationFormDialog extends JDialog {

    private final JTextField txtTitle = new JTextField(30);
    private final JTextArea txtContent = new JTextArea(5, 30);
    private final JComboBox<Notification.TargetRole> cboTargetRole = new JComboBox<>(Notification.TargetRole.values());

    private boolean saved = false;
    private Notification notification;

    public NotificationFormDialog(Frame owner, String title) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.notification = new Notification();
        buildUI();
        setSize(560, 420);
        setMinimumSize(new Dimension(480, 360));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        UITheme.styleDialog(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        // Tiêu đề
        g.gridx = 0; g.gridy = r;
        g.weightx = 0; g.weighty = 0;
        form.add(UITheme.createFormLabel("Tiêu Đề (*):"), g);
        g.gridx = 1;
        g.weightx = 1.0;
        form.add(txtTitle, g);

        // Nội dung
        r++;
        g.gridx = 0; g.gridy = r;
        g.weightx = 0; g.weighty = 1.0;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill = GridBagConstraints.NONE;
        form.add(UITheme.createFormLabel("Nội Dung (*):"), g);
        g.gridx = 1;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.BOTH;
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setFont(UITheme.FONT_BODY);
        txtContent.setRows(7);
        JScrollPane scrollContent = new JScrollPane(txtContent);
        scrollContent.setPreferredSize(new Dimension(0, 160));
        form.add(scrollContent, g);

        // Đối tượng
        r++;
        g.gridx = 0; g.gridy = r;
        g.weightx = 0; g.weighty = 0;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(UITheme.createFormLabel("Đối Tượng (*):"), g);
        g.gridx = 1;
        g.weightx = 1.0;
        cboTargetRole.setFont(UITheme.FONT_BODY);
        // Renderer hiển thị tiếng Việt (dùng lambda)
        cboTargetRole.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            label.setFont(UITheme.FONT_BODY);
            label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            if (value != null) {
                label.setText(switch (value) {
                    case All -> "Tất cả";
                    case Student -> "Học viên";
                    case Teacher -> "Giáo viên";
                    case Staff -> "Nhân viên";
                });
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
                label.setOpaque(true);
            }
            return label;
        });
        form.add(cboTargetRole, g);

        // Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(8, 24, 16, 24));
        JButton btnSave = UITheme.createPrimaryButton("Gửi Thông Báo", "");
        JButton btnCancel = UITheme.createOutlineButton("Hủy");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        actions.add(btnSave);
        actions.add(btnCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void onSave() {
        try {
            if (txtTitle.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập tiêu đề thông báo.");
            }
            if (txtContent.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập nội dung thông báo.");
            }

            notification.setTitle(txtTitle.getText().trim());
            notification.setContent(txtContent.getText().trim());
            notification.setTargetRole((Notification.TargetRole) cboTargetRole.getSelectedItem());

            saved = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Notification getNotification() {
        return notification;
    }
}
