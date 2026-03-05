package vn.edu.ute.ui.schedule;

import vn.edu.ute.model.Schedule;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleTableModel extends AbstractTableModel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final String[] columns;
    private final boolean showTeacher;
    private List<Schedule> allData = new ArrayList<>();
    private List<Schedule> filteredData = new ArrayList<>();

    /**
     * @param showTeacher true nếu muốn hiển thị cột Giáo viên (Admin/Staff panel).
     */
    public ScheduleTableModel(boolean showTeacher) {
        this.showTeacher = showTeacher;
        if (showTeacher) {
            columns = new String[] { "Ngày học", "Giờ bắt đầu", "Giờ kết thúc", "Lớp", "Khóa học", "Giáo viên",
                    "Phòng" };
        } else {
            columns = new String[] { "Ngày học", "Giờ bắt đầu", "Giờ kết thúc", "Lớp", "Khóa học", "Phòng" };
        }
    }

    public void setData(List<Schedule> data) {
        this.allData = data;
        this.filteredData = new ArrayList<>(data);
        fireTableDataChanged();
    }

    /**
     * Lọc realtime theo keyword sử dụng Stream API.
     * Tìm kiếm trong tên lớp, khóa học, giáo viên, phòng.
     */
    public void setFilter(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredData = new ArrayList<>(allData);
        } else {
            String lowerKeyword = keyword.trim().toLowerCase();
            filteredData = allData.stream()
                    .filter(s -> {
                        String className = s.getClassEntity() != null ? s.getClassEntity().getClassName() : "";
                        String courseName = (s.getClassEntity() != null && s.getClassEntity().getCourse() != null)
                                ? s.getClassEntity().getCourse().getCourseName()
                                : "";
                        String teacherName = (s.getClassEntity() != null && s.getClassEntity().getTeacher() != null)
                                ? s.getClassEntity().getTeacher().getFullName()
                                : "";
                        String roomName = s.getRoom() != null ? s.getRoom().getRoomName() : "";
                        String dateStr = s.getStudyDate() != null ? s.getStudyDate().format(DATE_FMT) : "";

                        return className.toLowerCase().contains(lowerKeyword)
                                || courseName.toLowerCase().contains(lowerKeyword)
                                || teacherName.toLowerCase().contains(lowerKeyword)
                                || roomName.toLowerCase().contains(lowerKeyword)
                                || dateStr.contains(lowerKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public Schedule getAt(int row) {
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
        Schedule s = filteredData.get(rowIndex);

        if (showTeacher) {
            // Cột: Ngày học | Giờ bắt đầu | Giờ kết thúc | Lớp | Khóa học | Giáo viên |
            // Phòng
            switch (columnIndex) {
                case 0:
                    return s.getStudyDate() != null ? s.getStudyDate().format(DATE_FMT) : "";
                case 1:
                    return s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : "";
                case 2:
                    return s.getEndTime() != null ? s.getEndTime().format(TIME_FMT) : "";
                case 3:
                    return s.getClassEntity() != null ? s.getClassEntity().getClassName() : "";
                case 4:
                    return (s.getClassEntity() != null && s.getClassEntity().getCourse() != null)
                            ? s.getClassEntity().getCourse().getCourseName()
                            : "";
                case 5:
                    return (s.getClassEntity() != null && s.getClassEntity().getTeacher() != null)
                            ? s.getClassEntity().getTeacher().getFullName()
                            : "(Chưa phân công)";
                case 6:
                    return s.getRoom() != null ? s.getRoom().getRoomName() : "(Chưa xếp phòng)";
                default:
                    return "";
            }
        } else {
            // Cột: Ngày học | Giờ bắt đầu | Giờ kết thúc | Lớp | Khóa học | Phòng
            switch (columnIndex) {
                case 0:
                    return s.getStudyDate() != null ? s.getStudyDate().format(DATE_FMT) : "";
                case 1:
                    return s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : "";
                case 2:
                    return s.getEndTime() != null ? s.getEndTime().format(TIME_FMT) : "";
                case 3:
                    return s.getClassEntity() != null ? s.getClassEntity().getClassName() : "";
                case 4:
                    return (s.getClassEntity() != null && s.getClassEntity().getCourse() != null)
                            ? s.getClassEntity().getCourse().getCourseName()
                            : "";
                case 5:
                    return s.getRoom() != null ? s.getRoom().getRoomName() : "(Chưa xếp phòng)";
                default:
                    return "";
            }
        }
    }
}
