package vn.edu.ute.ui.course;

import vn.edu.ute.model.Course;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp `CourseTableModel` là mô hình dữ liệu cho JTable hiển thị thông tin khóa học.
 */
public class CourseTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Khóa Học", "Cấp Độ", "Thời Lượng", "Học Phí", "Trạng Thái"};
    private List<Course> data = new ArrayList<>();
    private List<Course> filteredData = new ArrayList<>(); // Danh sách đã lọc
    private String filterKeyword = "";

    /**
     * Cập nhật dữ liệu cho model.
     */
    public void setData(List<Course> data) {
        this.data = data;
        applyFilter();
    }

    /**
     * Thiết lập từ khóa lọc.
     */
    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    /**
     * Áp dụng bộ lọc.
     */
    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            filteredData = new ArrayList<>(data);
        } else {
            filteredData = data.stream()
                    .filter(c -> {
                        String idStr = String.valueOf(c.getCourseId());
                        String nameStr = c.getCourseName().toLowerCase();
                        return idStr.contains(filterKeyword) || nameStr.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    /**
     * Lấy đối tượng Course tại một hàng.
     */
    public Course getAt(int row) {
        if (row < 0 || row >= filteredData.size()) return null;
        return filteredData.get(row);
    }

    @Override public int getRowCount() { return filteredData.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Course c = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getCourseId();
            case 1: return c.getCourseName();
            case 2: return c.getLevel() != null ? c.getLevel().name() : "";
            case 3: return c.getDuration() + " " + (c.getDurationUnit() != null ? c.getDurationUnit().name() : "");
            case 4: return String.format("%,.0f VNĐ", c.getFee()); // Định dạng tiền tệ
            case 5: return c.getStatus() == Course.Status.Active ? "Đang mở" : "Đã đóng";
            default: return "";
        }
    }
}
