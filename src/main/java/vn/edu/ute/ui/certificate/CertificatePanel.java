package vn.edu.ute.ui.certificate;

import vn.edu.ute.model.Certificate;
import vn.edu.ute.service.CertificateService;
import vn.edu.ute.service.ClassService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class CertificatePanel extends JPanel {
    private final CertificateService certificateService;
    private final StudentService studentService;
    private final ClassService classService;

    private final CertificateTableModel tableModel = new CertificateTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập tên, số Serial...", 25);

    public CertificatePanel(CertificateService certificateService, StudentService studentService, ClassService classService) {
        this.certificateService = certificateService;
        this.studentService = studentService;
        this.classService = classService;

        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel toolbar = UITheme.createToolbar();
        JButton btnAdd = UITheme.createSuccessButton("Cấp Chứng Chỉ", "");
        JButton btnEdit = UITheme.createPrimaryButton("Sửa", "");
        JButton btnDelete = UITheme.createDangerButton("Xóa", "");
        JButton btnRefresh = UITheme.createNeutralButton("Làm Mới", "");

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
            public void insertUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { tableModel.setFilter(txtSearch.getText()); }
        });

        add(UITheme.createTopPanel(toolbar, searchPanel), BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        new SwingWorker<List<Certificate>, Void>() {
            @Override
            protected List<Certificate> doInBackground() throws Exception {
                return certificateService.getAllCertificates();
            }
            @Override
            protected void done() {
                try { tableModel.setData(get()); }
                catch (Exception ex) { JOptionPane.showMessageDialog(CertificatePanel.this, "Lỗi tải dữ liệu: " + ex.getMessage()); }
            }
        }.execute();
    }

    private void onAdd() {
        try {
            CertificateFormDialog dlg = new CertificateFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Cấp Chứng Chỉ Mới", null, studentService.getActiveStudents(), classService.getAllClasses());
            dlg.setVisible(true);
            if (dlg.isSaved()) { certificateService.createCertificate(dlg.getCertificate()); loadData(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn một bản ghi để sửa."); return; }
        try {
            CertificateFormDialog dlg = new CertificateFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Sửa Chứng Chỉ", tableModel.getAt(row), studentService.getActiveStudents(), classService.getAllClasses());
            dlg.setVisible(true);
            if (dlg.isSaved()) { certificateService.updateCertificate(dlg.getCertificate()); loadData(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn một bản ghi để xóa."); return; }
        Certificate c = tableModel.getAt(row);
        if (JOptionPane.showConfirmDialog(this, "Xóa chứng chỉ này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { certificateService.deleteCertificate(c.getCertificateId()); loadData(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }
}