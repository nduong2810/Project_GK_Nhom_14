package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Invoice;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp `InvoiceTableModel` là mô hình dữ liệu cho JTable hiển thị thông tin hóa đơn.
 * Nó tính toán và hiển thị các thông tin phái sinh như "Còn Thiếu".
 */
public class InvoiceTableModel extends AbstractTableModel {
    private final String[] columns = { "ID", "Học Viên", "Tổng Tiền", "Còn Thiếu", "Khuyến Mãi", "Ngày Xuất",
            "Trạng Thái", "Ghi Chú" };
    private List<Invoice> data = new ArrayList<>();
    private List<Invoice> filteredData = new ArrayList<>();
    private Map<Long, BigDecimal> paidAmounts = new HashMap<>();
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    private String filterKeyword = "";

    /**
     * Cập nhật dữ liệu cho model.
     * @param data Danh sách hóa đơn.
     * @param paidAmounts Map chứa tổng số tiền đã thanh toán cho mỗi hóa đơn.
     */
    public void setData(List<Invoice> data, Map<Long, BigDecimal> paidAmounts) {
        this.data = data;
        this.paidAmounts = paidAmounts != null ? paidAmounts : new HashMap<>();
        applyFilter();
    }

    /**
     * Thiết lập từ khóa tìm kiếm.
     */
    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    /**
     * Áp dụng bộ lọc vào dữ liệu.
     */
    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            filteredData = new ArrayList<>(data);
        } else {
            filteredData = data.stream()
                    .filter(inv -> {
                        String studentName = inv.getStudent() != null
                                ? inv.getStudent().getFullName().toLowerCase()
                                : "";
                        String idStr = String.valueOf(inv.getInvoiceId());
                        return studentName.contains(filterKeyword) || idStr.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    /**
     * Lấy hóa đơn tại một hàng.
     */
    public Invoice getAt(int row) {
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
        Invoice inv = filteredData.get(rowIndex);
        BigDecimal paid = paidAmounts.getOrDefault(inv.getInvoiceId(), BigDecimal.ZERO);
        BigDecimal remaining = inv.getTotalAmount().subtract(paid);
        // Không hiển thị số tiền còn thiếu là số âm (trường hợp trả dư)
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        switch (columnIndex) {
            case 0: return inv.getInvoiceId();
            case 1: return inv.getStudent() != null ? inv.getStudent().getFullName() : "";
            case 2: return currencyFmt.format(inv.getTotalAmount());
            case 3: return currencyFmt.format(remaining);
            case 4: return inv.getPromotion() != null ? inv.getPromotion().getPromoName() : "—";
            case 5: return inv.getIssueDate() != null ? inv.getIssueDate().toString() : "";
            case 6: // Chuyển đổi Enum thành chuỗi tiếng Việt dễ hiểu
                return switch (inv.getStatus()) {
                    case Draft -> "Nháp";
                    case Issued -> "Đã xuất";
                    case Paid -> "Đã thanh toán";
                    case Cancelled -> "Đã hủy";
                    default -> inv.getStatus().name();
                };
            case 7: return inv.getNote() != null ? inv.getNote() : "";
            default: return "";
        }
    }
}
