package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import vn.edu.ute.model.Room.Status;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Phòng", "Sức Chứa", "Vị Trí", "Trạng Thái"};
    private List<Room> data = new ArrayList<>();
    private List<Room> filteredData = new ArrayList<>();
    private String filterKeyword = "";

    public void setData(List<Room> data) {
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
                    .filter(r -> {
                        String idStr = String.valueOf(r.getRoomId());
                        String nameStr = r.getRoomName().toLowerCase();
                        String locStr = r.getLocation() != null ? r.getLocation().toLowerCase() : "";
                        return idStr.contains(filterKeyword) || nameStr.contains(filterKeyword) || locStr.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public Room getAt(int row) {
        if (row < 0 || row >= filteredData.size()) return null;
        return filteredData.get(row);
    }

    @Override public int getRowCount() { return filteredData.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room r = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return r.getRoomId();
            case 1: return r.getRoomName();
            case 2: return r.getCapacity();
            case 3: return r.getLocation() != null ? r.getLocation() : "";
            case 4: return r.getStatus() == Status.Active ? "Hoạt động" : "Ngưng hoạt động";
            default: return "";
        }
    }
}