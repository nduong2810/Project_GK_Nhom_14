package vn.edu.ute.ui.branch;

import vn.edu.ute.model.Branch;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp `BranchTableModel` là mô hình dữ liệu cho JTable hiển thị thông tin chi nhánh.
 * Nó hỗ trợ việc lọc dữ liệu dựa trên từ khóa tìm kiếm.
 */
public class BranchTableModel extends AbstractTableModel {
    private final String[] columns = { "ID", "Tên Chi Nhánh", "Địa Chỉ", "SĐT", "Trạng Thái" };
    private List<Branch> data = new ArrayList<>(); // Dữ liệu gốc
    private List<Branch> filteredData = new ArrayList<>(); // Dữ liệu đã lọc để hiển thị
    private String filterKeyword = "";

    /**
     * Cập nhật dữ liệu cho model.
     * @param data Danh sách chi nhánh mới.
     */
    public void setData(List<Branch> data) {
        this.data = data;
        applyFilter();
    }

    /**
     * Thiết lập từ khóa lọc.
     * @param keyword Từ khóa tìm kiếm.
     */
    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    /**
     * Áp dụng bộ lọc vào dữ liệu gốc và cập nhật danh sách hiển thị.
     * Sử dụng Stream API để lọc hiệu quả.
     */
    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            filteredData = new ArrayList<>(data);
        } else {
            filteredData = data.stream()
                    .filter(b -> {
                        String idStr = String.valueOf(b.getBranchId());
                        String name = b.getBranchName() != null ? b.getBranchName().toLowerCase() : "";
                        String address = b.getAddress() != null ? b.getAddress().toLowerCase() : "";
                        String phone = b.getPhone() != null ? b.getPhone().toLowerCase() : "";
                        // Kiểm tra từ khóa trên nhiều trường
                        return idStr.contains(filterKeyword)
                                || name.contains(filterKeyword)
                                || address.contains(filterKeyword)
                                || phone.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        // Thông báo cho JTable rằng dữ liệu đã thay đổi
        fireTableDataChanged();
    }

    /**
     * Lấy đối tượng Branch tại một hàng cụ thể.
     * @param row Chỉ số hàng.
     * @return Đối tượng Branch hoặc null nếu hàng không hợp lệ.
     */
    public Branch getAt(int row) {
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
        Branch b = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return b.getBranchId();
            case 1: return b.getBranchName();
            case 2: return b.getAddress() != null ? b.getAddress() : "";
            case 3: return b.getPhone() != null ? b.getPhone() : "";
            case 4: return b.getStatus() == Branch.Status.Active ? "Hoạt động" : "Ngưng hoạt động";
            default: return "";
        }
    }
}
