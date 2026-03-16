package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import vn.edu.ute.model.Room.Status;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp `RoomTableModel` là một mô hình dữ liệu (TableModel) cho JTable,
 * được thiết kế để hiển thị và quản lý dữ liệu phòng học.
 * Nó kế thừa từ `AbstractTableModel` và cung cấp logic để lọc dữ liệu.
 */
public class RoomTableModel extends AbstractTableModel {
    // Tên các cột của bảng
    private final String[] columns = { "ID", "Tên Phòng", "Sức Chứa", "Vị Trí", "Chi Nhánh", "Trạng Thái" };
    // Danh sách dữ liệu gốc từ cơ sở dữ liệu
    private List<Room> data = new ArrayList<>();
    // Danh sách dữ liệu đã được lọc để hiển thị trên bảng
    private List<Room> filteredData = new ArrayList<>();
    // Từ khóa tìm kiếm hiện tại
    private String filterKeyword = "";

    /**
     * Cung cấp dữ liệu mới cho model và áp dụng bộ lọc.
     * @param data Danh sách phòng học mới.
     */
    public void setData(List<Room> data) {
        this.data = data;
        applyFilter();
    }

    /**
     * Thiết lập từ khóa tìm kiếm và áp dụng lại bộ lọc.
     * @param keyword Từ khóa do người dùng nhập.
     */
    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    /**
     * Lọc danh sách `data` dựa trên `filterKeyword` và cập nhật `filteredData`.
     * Sau đó, thông báo cho JTable rằng dữ liệu đã thay đổi để nó tự vẽ lại.
     */
    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            // Nếu không có từ khóa, hiển thị tất cả dữ liệu
            filteredData = new ArrayList<>(data);
        } else {
            // Nếu có từ khóa, sử dụng Stream API để lọc
            filteredData = data.stream()
                    .filter(r -> {
                        String idStr = String.valueOf(r.getRoomId());
                        String nameStr = r.getRoomName().toLowerCase();
                        String locStr = r.getLocation() != null ? r.getLocation().toLowerCase() : "";
                        String branchStr = r.getBranch() != null ? r.getBranch().getBranchName().toLowerCase() : "";
                        // Trả về true nếu bất kỳ trường nào chứa từ khóa
                        return idStr.contains(filterKeyword)
                                || nameStr.contains(filterKeyword)
                                || locStr.contains(filterKeyword)
                                || branchStr.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        // Thông báo cho bảng rằng toàn bộ dữ liệu đã thay đổi
        fireTableDataChanged();
    }

    /**
     * Lấy đối tượng Room tại một hàng cụ thể trong bảng (dữ liệu đã lọc).
     * @param row Chỉ số của hàng.
     * @return Đối tượng Room hoặc null nếu chỉ số không hợp lệ.
     */
    public Room getAt(int row) {
        if (row < 0 || row >= filteredData.size())
            return null;
        return filteredData.get(row);
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

    /**
     * Lấy giá trị để hiển thị tại một ô cụ thể trong bảng.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room r = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return r.getRoomId();
            case 1: return r.getRoomName();
            case 2: return r.getCapacity();
            case 3: return r.getLocation() != null ? r.getLocation() : "";
            case 4: return r.getBranch() != null ? r.getBranch().getBranchName() : "—"; // Hiển thị tên chi nhánh
            case 5: return r.getStatus() == Status.Active ? "Hoạt động" : "Ngưng hoạt động"; // Chuyển enum thành chuỗi dễ hiểu
            default: return "";
        }
    }
}
