package vn.edu.ute.ui.promotion;

import vn.edu.ute.model.Promotion;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PromotionTableModel extends AbstractTableModel {
    private final String[] columns = { "ID", "Tên Khuyến Mãi", "Loại Giảm", "Giá Trị", "Ngày BĐ", "Ngày KT",
            "Trạng Thái" };
    private List<Promotion> data = new ArrayList<>();
    private List<Promotion> filteredData = new ArrayList<>();
    private String filterKeyword = "";
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public void setData(List<Promotion> data) {
        this.data = data;
        applyFilter();
    }

    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    /**
     * Lọc danh sách khuyến mãi theo từ khóa (dùng Stream API).
     */
    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            filteredData = new ArrayList<>(data);
        } else {
            filteredData = data.stream()
                    .filter(p -> {
                        String idStr = String.valueOf(p.getPromotionId());
                        String name = p.getPromoName() != null ? p.getPromoName().toLowerCase() : "";
                        return idStr.contains(filterKeyword) || name.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public Promotion getAt(int row) {
        return (row >= 0 && row < filteredData.size()) ? filteredData.get(row) : null;
    }

    @Override
    public int getRowCount() {
        return filteredData.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Promotion p = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return p.getPromotionId();
            case 1:
                return p.getPromoName();
            case 2:
                return p.getDiscountType() == Promotion.DiscountType.Percent
                        ? "Phần trăm (%)"
                        : "Số tiền cố định";
            case 3:
                if (p.getDiscountType() == Promotion.DiscountType.Percent) {
                    return p.getDiscountValue() + "%";
                } else {
                    return currencyFmt.format(p.getDiscountValue());
                }
            case 4:
                return p.getStartDate() != null ? p.getStartDate().toString() : "—";
            case 5:
                return p.getEndDate() != null ? p.getEndDate().toString() : "—";
            case 6:
                return p.getStatus() == Promotion.Status.Active ? "Active" : "Inactive";
            default:
                return "";
        }
    }
}
