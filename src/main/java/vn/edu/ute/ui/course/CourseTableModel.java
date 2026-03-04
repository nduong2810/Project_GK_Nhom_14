package vn.edu.ute.ui.course;

import vn.edu.ute.model.Course;
import vn.edu.ute.model.Course.Status;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CourseTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Khóa Học", "Cấp Độ", "Thời Lượng", "Học Phí", "Trạng Thái"};
    private List<Course> data = new ArrayList<>();

    public void setData(List<Course> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public Course getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Course c = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getCourseId();
            case 1: return c.getCourseName();
            case 2: return c.getLevel() != null ? c.getLevel().name() : "";
            case 3: return c.getDuration() + " " + (c.getDurationUnit() != null ? c.getDurationUnit().name() : "");
            case 4: return String.format("%,.0f VNĐ", c.getFee()); // Định dạng tiền tệ
            case 5: return c.getStatus() == Status.Active ? "Đang mở" : "Đã đóng";
            default: return "";
        }
    }
}