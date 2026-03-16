package vn.edu.ute.ui.classmgmt;

import vn.edu.ute.model.ClassEntity;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp `ClassTableModel` là mô hình dữ liệu cho JTable hiển thị thông tin lớp học.
 */
public class ClassTableModel extends AbstractTableModel {
    private final String[] columns = { "ID", "Tên Lớp", "Khóa Học", "Giáo Viên", "Phòng", "Chi Nhánh", "Ngày Bắt Đầu",
            "Trạng Thái" };
    private List<ClassEntity> data = new ArrayList<>();
    private List<ClassEntity> filteredData = new ArrayList<>();
    private String filterKeyword = "";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Cập nhật dữ liệu cho model.
     */
    public void setData(List<ClassEntity> data) {
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
                        String className = c.getClassName().toLowerCase();
                        String courseName = c.getCourse() != null ? c.getCourse().getCourseName().toLowerCase() : "";
                        String teacherName = c.getTeacher() != null ? c.getTeacher().getFullName().toLowerCase() : "";
                        String branchName = c.getBranch() != null ? c.getBranch().getBranchName().toLowerCase() : "";

                        return className.contains(filterKeyword) ||
                                courseName.contains(filterKeyword) ||
                                teacherName.contains(filterKeyword) ||
                                branchName.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    /**
     * Lấy đối tượng ClassEntity tại một hàng.
     */
    public ClassEntity getAt(int row) {
        if (row < 0 || row >= filteredData.size())
            return null;
        return filteredData.get(row);
    }

    @Override public int getRowCount() { return filteredData.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClassEntity c = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getClassId();
            case 1: return c.getClassName();
            case 2: return c.getCourse() != null ? c.getCourse().getCourseName() : "";
            case 3: return c.getTeacher() != null ? c.getTeacher().getFullName() : "Chưa phân công";
            case 4: return c.getRoom() != null ? c.getRoom().getRoomName() : "Chưa xếp phòng";
            case 5: return c.getBranch() != null ? c.getBranch().getBranchName() : "—";
            case 6: return c.getStartDate() != null ? c.getStartDate().format(formatter) : "";
            case 7: return c.getStatus(); // Trả về enum, renderer của bảng sẽ xử lý hiển thị
            default: return "";
        }
    }
}
