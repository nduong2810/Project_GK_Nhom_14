package vn.edu.ute.ui.branch;

import vn.edu.ute.model.Branch;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;

public class BranchFormDialog extends JDialog {

    private final JTextField txtName = new JTextField(25);
    private final JTextField txtAddress = new JTextField(25);
    private final JTextField txtPhone = new JTextField(15);
    private final JComboBox<Branch.Status> cboStatus = new JComboBox<>(Branch.Status.values());

    private boolean saved = false;
    private Branch branch;

    public BranchFormDialog(Frame owner, String title, Branch existing) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();

        if (existing != null) {
            this.branch = existing;
            txtName.setText(existing.getBranchName());
            txtAddress.setText(existing.getAddress() != null ? existing.getAddress() : "");
            txtPhone.setText(existing.getPhone() != null ? existing.getPhone() : "");
            cboStatus.setSelectedItem(existing.getStatus());
        } else {
            this.branch = new Branch();
            cboStatus.setSelectedItem(Branch.Status.Active);
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

        // Tên chi nhánh
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Tên Chi Nhánh (*):"), g);
        g.gridx = 1;
        form.add(txtName, g);

        // Địa chỉ
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Địa Chỉ:"), g);
        g.gridx = 1;
        form.add(txtAddress, g);

        // SĐT
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Số Điện Thoại:"), g);
        g.gridx = 1;
        form.add(txtPhone, g);

        // Trạng thái
        r++;
        g.gridx = 0;
        g.gridy = r;
        form.add(UITheme.createFormLabel("Trạng Thái:"), g);
        g.gridx = 1;
        cboStatus.setFont(UITheme.FONT_BODY);
        form.add(cboStatus, g);

        // Buttons
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
            if (txtName.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập tên chi nhánh.");
            }

            branch.setBranchName(txtName.getText().trim());
            branch.setAddress(txtAddress.getText().trim().isEmpty() ? null : txtAddress.getText().trim());
            branch.setPhone(txtPhone.getText().trim().isEmpty() ? null : txtPhone.getText().trim());
            branch.setStatus((Branch.Status) cboStatus.getSelectedItem());

            saved = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Branch getBranch() {
        return branch;
    }
}
