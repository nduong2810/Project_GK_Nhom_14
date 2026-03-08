package vn.edu.ute.ui.enrollment;

import vn.edu.ute.model.Enrollment;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Học Viên", "Lớp Học", "Khóa Học", "Ngày Ghi Danh", "Trạng Thái"};
    private List<Enrollment> data = new ArrayList<>();
    private List<Enrollment> filteredData = new ArrayList<>();
    private String filterKeyword = "";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setData(List<Enrollment> data) {
        this.data = data;
        applyFilter();
    }

    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            filteredData = new ArrayList<>(data);
        } else {
            filteredData = data.stream()
                    .filter(e -> {
                        String studentName = e.getStudent() != null ? e.getStudent().getFullName().toLowerCase() : "";
                        String className = e.getClassEntity() != null ? e.getClassEntity().getClassName().toLowerCase() : "";
                        return studentName.contains(filterKeyword) || className.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public Enrollment getAt(int row) {
        if (row < 0 || row >= filteredData.size()) return null;
        return filteredData.get(row);
    }

    @Override public int getRowCount() { return filteredData.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Enrollment e = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return e.getEnrollmentId();
            case 1: return e.getStudent() != null ? e.getStudent().getFullName() : "";
            case 2: return e.getClassEntity() != null ? e.getClassEntity().getClassName() : "";
            case 3: return (e.getClassEntity() != null && e.getClassEntity().getCourse() != null)
                    ? e.getClassEntity().getCourse().getCourseName() : "";
            case 4: return e.getEnrollmentDate() != null ? e.getEnrollmentDate().format(formatter) : "";
            case 5: return e.getStatus();
            default: return "";
        }
    }
}