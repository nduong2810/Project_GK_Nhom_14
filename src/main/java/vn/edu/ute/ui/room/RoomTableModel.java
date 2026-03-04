package vn.edu.ute.ui.room;

import vn.edu.ute.model.Room;
import vn.edu.ute.model.Room.Status;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class RoomTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Phòng", "Sức Chứa", "Vị Trí", "Trạng Thái"};
    private List<Room> data = new ArrayList<>();

    public void setData(List<Room> data) { this.data = data; fireTableDataChanged(); }
    public Room getAt(int row) { return (row >= 0 && row < data.size()) ? data.get(row) : null; }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room r = data.get(rowIndex);
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