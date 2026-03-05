package vn.edu.ute.ui.classmgmt;

import vn.edu.ute.model.ClassEntity;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClassTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Lớp", "Khóa Học", "Giáo Viên", "Phòng", "Ngày Bắt Đầu", "Trạng Thái"};
    private List<ClassEntity> data = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setData(List<ClassEntity> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public ClassEntity getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClassEntity c = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getClassId();
            case 1: return c.getClassName();
            case 2: return c.getCourse() != null ? c.getCourse().getCourseName() : "";
            case 3: return c.getTeacher() != null ? c.getTeacher().getFullName() : "Chưa phân công";
            case 4: return c.getRoom() != null ? c.getRoom().getRoomName() : "Chưa xếp phòng";
            case 5: return c.getStartDate() != null ? c.getStartDate().format(formatter) : "";
            case 6: return c.getStatus();
            default: return "";
        }
    }
}