package vn.edu.ute.ui.certificate;

import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Student;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CertificateFormDialog extends JDialog {
    private final JComboBox<Student> cboStudent = new JComboBox<>();
    private final JComboBox<ClassEntity> cboClass = new JComboBox<>();
    private final JTextField txtCertName = new JTextField(20);
    private final JTextField txtIssueDate = new JTextField(15);
    private final JTextField txtSerialNo = new JTextField(15);

    private boolean saved = false;
    private Certificate certificate;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CertificateFormDialog(Frame owner, String title, Certificate existing, List<Student> students, List<ClassEntity> classes) {
        super(owner, title, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (Student s : students) cboStudent.addItem(s);

        cboClass.addItem(null);
        for (ClassEntity c : classes) cboClass.addItem(c);

        setupComboBoxRenderers();
        buildUI();

        if (existing != null) {
            if (existing.getStudent() != null) setComboSelection(cboStudent, existing.getStudent().getStudentId());
            if (existing.getClassEntity() != null) setComboSelection(cboClass, existing.getClassEntity().getClassId());
            else cboClass.setSelectedIndex(0);

            txtCertName.setText(existing.getCertName());
            txtIssueDate.setText(existing.getIssueDate() != null ? existing.getIssueDate().format(dateFormatter) : "");
            txtSerialNo.setText(existing.getSerialNo());
            this.certificate = existing;
        } else {
            this.certificate = new Certificate();
            txtIssueDate.setText(LocalDate.now().format(dateFormatter));
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void setupComboBoxRenderers() {
        cboStudent.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    Student s = (Student) value;
                    setText(s.getFullName() + " - " + (s.getPhone() != null ? s.getPhone() : "N/A"));
                }
                return this;
            }
        });

        cboClass.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("--- (Không thuộc lớp nào) ---");
                } else if (value instanceof ClassEntity) {
                    setText(((ClassEntity) value).getClassName());
                }
                return this;
            }
        });
    }

    private void setComboSelection(JComboBox<?> combo, Long idTarget) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Student && ((Student) item).getStudentId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
            if (item instanceof ClassEntity && ((ClassEntity) item).getClassId().equals(idTarget)) { combo.setSelectedIndex(i); return; }
        }
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
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Học Viên (*):"), g);
        g.gridx = 1; form.add(cboStudent, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Lớp Học (Tùy chọn):"), g);
        g.gridx = 1; form.add(cboClass, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Tên Chứng Chỉ (*):"), g);
        g.gridx = 1; form.add(txtCertName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Ngày Cấp (dd/MM/yyyy):"), g);
        g.gridx = 1; form.add(txtIssueDate, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(UITheme.createFormLabel("Số Serial:"), g);
        g.gridx = 1; form.add(txtSerialNo, g);

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
            Student selectedStudent = (Student) cboStudent.getSelectedItem();
            if (selectedStudent == null) throw new IllegalArgumentException("Vui lòng chọn học viên.");

            String certName = txtCertName.getText().trim();
            if (certName.isEmpty()) throw new IllegalArgumentException("Tên chứng chỉ không được để trống.");

            LocalDate iDate;
            try {
                iDate = LocalDate.parse(txtIssueDate.getText().trim(), dateFormatter);
            } catch (Exception ex) { throw new IllegalArgumentException("Ngày cấp không đúng định dạng dd/MM/yyyy"); }

            certificate.setStudent(selectedStudent);
            certificate.setClassEntity((ClassEntity) cboClass.getSelectedItem());
            certificate.setCertName(certName);
            certificate.setIssueDate(iDate);
            certificate.setSerialNo(txtSerialNo.getText().trim());

            saved = true; dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Certificate getCertificate() { return certificate; }
}