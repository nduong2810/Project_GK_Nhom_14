package vn.edu.ute.ui.placementtest;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.model.Student;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Lớp `PlacementTestFormDialog` tạo hộp thoại để thêm hoặc sửa kết quả thi xếp lớp.
 */
public class PlacementTestFormDialog extends JDialog {
    private final JComboBox<Student> cboStudent = new JComboBox<>();
    private final JTextField txtTestDate = new JTextField(15);
    private final JTextField txtScore = new JTextField(15);
    private final JComboBox<PlacementTest.SuggestedLevel> cboLevel = new JComboBox<>(PlacementTest.SuggestedLevel.values());
    private final JTextField txtNote = new JTextField(20);

    private boolean saved = false;
    private PlacementTest placementTest;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PlacementTestFormDialog(Frame owner, String title, PlacementTest existing, List<Student> students) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        students.forEach(cboStudent::addItem);
        setupComboBoxRenderer();
        buildUI();

        if (existing != null) {
            // Chế độ sửa
            this.placementTest = existing;
            if (existing.getStudent() != null) setStudentSelection(existing.getStudent().getStudentId());
            txtTestDate.setText(existing.getTestDate() != null ? existing.getTestDate().format(dateFormatter) : "");
            txtScore.setText(existing.getScore() != null ? existing.getScore().toString() : "");
            cboLevel.setSelectedItem(existing.getSuggestedLevel());
            txtNote.setText(existing.getNote());
        } else {
            // Chế độ thêm mới
            this.placementTest = new PlacementTest();
            txtTestDate.setText(LocalDate.now().format(dateFormatter));
            cboLevel.setSelectedItem(PlacementTest.SuggestedLevel.Beginner);
        }

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Tùy chỉnh hiển thị cho ComboBox học viên.
     */
    private void setupComboBoxRenderer() {
        cboStudent.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Student st) setText(st.getFullName());
                return this;
            }
        });
    }

    /**
     * Tiện ích chọn học viên trong ComboBox theo ID.
     */
    private void setStudentSelection(Long studentId) {
        for (int i = 0; i < cboStudent.getItemCount(); i++) {
            if (cboStudent.getItemAt(i).getStudentId().equals(studentId)) {
                cboStudent.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Xây dựng giao diện người dùng.
     */
    private void buildUI() {
        UITheme.styleDialog(this);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Học Viên (*):"), g);
        g.gridx = 1; form.add(cboStudent, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ngày Thi (dd/MM/yyyy):"), g);
        g.gridx = 1; form.add(txtTestDate, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Điểm Số:"), g);
        g.gridx = 1; form.add(txtScore, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Trình Độ Gợi Ý:"), g);
        g.gridx = 1; form.add(cboLevel, g);
        r++; g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ghi Chú:"), g);
        g.gridx = 1; form.add(txtNote, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JButton btnSave = UITheme.createPrimaryButton("Lưu", "💾");
        JButton btnCancel = UITheme.createOutlineButton("Hủy");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        actions.add(btnSave);
        actions.add(btnCancel);

        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Lưu".
     */
    private void onSave() {
        try {
            Student selectedStudent = (Student) cboStudent.getSelectedItem();
            if (selectedStudent == null) throw new IllegalArgumentException("Vui lòng chọn học viên.");

            LocalDate tDate;
            try {
                tDate = LocalDate.parse(txtTestDate.getText().trim(), dateFormatter);
            } catch (Exception ex) { throw new IllegalArgumentException("Ngày thi không đúng định dạng dd/MM/yyyy"); }

            BigDecimal scoreValue = null;
            if (!txtScore.getText().trim().isEmpty()) {
                try {
                    scoreValue = new BigDecimal(txtScore.getText().trim());
                } catch (Exception ex) { throw new IllegalArgumentException("Điểm số phải là một số hợp lệ."); }
            }

            placementTest.setStudent(selectedStudent);
            placementTest.setTestDate(tDate);
            placementTest.setScore(scoreValue);
            placementTest.setSuggestedLevel((PlacementTest.SuggestedLevel) cboLevel.getSelectedItem());
            placementTest.setNote(txtNote.getText().trim());

            saved = true; dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public PlacementTest getPlacementTest() { return placementTest; }
}
