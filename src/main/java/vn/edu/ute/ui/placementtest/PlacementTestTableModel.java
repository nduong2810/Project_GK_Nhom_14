package vn.edu.ute.ui.placementtest;

import vn.edu.ute.model.PlacementTest;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlacementTestTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Học Viên", "Ngày Thi", "Điểm Số", "Trình Độ Gợi Ý", "Ghi Chú"};
    private List<PlacementTest> data = new ArrayList<>();
    private List<PlacementTest> filteredData = new ArrayList<>();
    private String filterKeyword = "";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setData(List<PlacementTest> data) {
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
                    .filter(p -> p.getStudent() != null && p.getStudent().getFullName().toLowerCase().contains(filterKeyword))
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public PlacementTest getAt(int row) {
        if (row < 0 || row >= filteredData.size()) return null;
        return filteredData.get(row);
    }

    @Override public int getRowCount() { return filteredData.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PlacementTest p = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getTestId();
            case 1: return p.getStudent() != null ? p.getStudent().getFullName() : "";
            case 2: return p.getTestDate() != null ? p.getTestDate().format(formatter) : "";
            case 3: return p.getScore();
            case 4: return p.getSuggestedLevel();
            case 5: return p.getNote();
            default: return "";
        }
    }
}