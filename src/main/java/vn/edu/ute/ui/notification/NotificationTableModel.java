package vn.edu.ute.ui.notification;

import vn.edu.ute.model.Notification;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationTableModel extends AbstractTableModel {
    private final String[] columns = { "ID", "Tiêu Đề", "Đối Tượng", "Người Tạo", "Ngày Tạo" };
    private List<Notification> data = new ArrayList<>();
    private List<Notification> filteredData = new ArrayList<>();
    private String filterKeyword = "";
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setData(List<Notification> data) {
        this.data = data;
        applyFilter();
    }

    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    /**
     * Lọc danh sách thông báo theo từ khóa (dùng Stream API + lambda).
     */
    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            filteredData = new ArrayList<>(data);
        } else {
            filteredData = data.stream()
                    .filter(n -> {
                        String idStr = String.valueOf(n.getNotificationId());
                        String title = n.getTitle() != null ? n.getTitle().toLowerCase() : "";
                        String targetRole = n.getTargetRole() != null
                                ? mapTargetRoleDisplay(n.getTargetRole()).toLowerCase()
                                : "";
                        return idStr.contains(filterKeyword)
                                || title.contains(filterKeyword)
                                || targetRole.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public Notification getAt(int row) {
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
        Notification n = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return n.getNotificationId();
            case 1:
                return n.getTitle();
            case 2:
                return mapTargetRoleDisplay(n.getTargetRole());
            case 3:
                if (n.getCreatedByUser() != null) {
                    return n.getCreatedByUser().getUsername();
                }
                return "—";
            case 4:
                return n.getCreatedAt() != null ? n.getCreatedAt().format(dtFormatter) : "";
            default:
                return "";
        }
    }

    /**
     * Map TargetRole sang tên hiển thị tiếng Việt (dùng switch expression).
     */
    private String mapTargetRoleDisplay(Notification.TargetRole role) {
        return switch (role) {
            case All -> "Tất cả";
            case Student -> "Học viên";
            case Teacher -> "Giáo viên";
            case Staff -> "Nhân viên";
        };
    }
}
