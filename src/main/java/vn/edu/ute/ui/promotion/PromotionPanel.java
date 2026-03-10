package vn.edu.ute.ui.promotion;

import vn.edu.ute.model.Promotion;
import vn.edu.ute.service.PromotionService;
import vn.edu.ute.ui.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PromotionPanel extends JPanel {

    private final PromotionService promotionService;
    private final PromotionTableModel tableModel = new PromotionTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = UITheme.createSearchField("Nhập ID hoặc tên khuyến mãi...", 25);

    public PromotionPanel(PromotionService promotionService) {
        this.promotionService = promotionService;
        setLayout(new BorderLayout(10, 10));
        UITheme.applyPanelStyle(this);
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel toolbar = UITheme.createToolbar();
        JButton btnAdd = UITheme.createSuccessButton("Thêm Mới", "");
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

        // Search listener dùng lambda expression
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                tableModel.setFilter(txtSearch.getText());
            }
        });

        add(UITheme.createTopPanel(toolbar, searchPanel), BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UITheme.styleTable(table);
        add(UITheme.createStyledScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            tableModel.setData(promotionService.getAllPromotions());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khuyến mãi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        PromotionFormDialog dlg = new PromotionFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Thêm Khuyến Mãi", null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                promotionService.createPromotion(dlg.getPromotion());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu khuyến mãi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi trong bảng để sửa.");
            return;
        }
        Promotion selected = tableModel.getAt(selectedRow);
        PromotionFormDialog dlg = new PromotionFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), "Sửa Khuyến Mãi", selected);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            try {
                promotionService.updatePromotion(dlg.getPromotion());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật khuyến mãi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi trong bảng để xóa.");
            return;
        }
        Promotion selected = tableModel.getAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khuyến mãi: " + selected.getPromoName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                promotionService.deletePromotion(selected.getPromotionId());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
