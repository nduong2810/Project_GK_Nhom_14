package vn.edu.ute.ui.finance;

import vn.edu.ute.model.Payment;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Lớp `PaymentTableModel` là mô hình dữ liệu cho JTable hiển thị lịch sử thanh toán.
 */
public class PaymentTableModel extends AbstractTableModel {
    private final String[] columns = { "ID", "Số Tiền", "Ngày Thanh Toán", "Hình Thức", "Trạng Thái", "Mã Tham Chiếu" };
    private List<Payment> data = new ArrayList<>();
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Cập nhật dữ liệu cho model.
     */
    public void setData(List<Payment> data) {
        this.data = data;
        fireTableDataChanged();
    }

    /**
     * Lấy thanh toán tại một hàng.
     */
    public Payment getAt(int row) {
        return (row >= 0 && row < data.size()) ? data.get(row) : null;
    }

    @Override
    public int getRowCount() {
        return data.size();
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
        Payment p = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getPaymentId();
            case 1: return currencyFmt.format(p.getAmount());
            case 2: return p.getPaymentDate() != null ? p.getPaymentDate().format(dtFmt) : "";
            case 3: // Chuyển đổi Enum thành chuỗi tiếng Việt
                return switch (p.getPaymentMethod()) {
                    case Cash -> "Tiền mặt";
                    case Bank -> "Chuyển khoản";
                    case Momo -> "Momo";
                    case ZaloPay -> "ZaloPay";
                    case Card -> "Thẻ";
                    case Other -> "Khác";
                    default -> p.getPaymentMethod().name();
                };
            case 4: // Chuyển đổi Enum thành chuỗi tiếng Việt
                return switch (p.getStatus()) {
                    case Pending -> "Đang chờ";
                    case Completed -> "Hoàn tất";
                    case Failed -> "Thất bại";
                    case Refunded -> "Hoàn tiền";
                    default -> p.getStatus().name();
                };
            case 5: return p.getReferenceCode() != null ? p.getReferenceCode() : "";
            default: return "";
        }
    }
}
