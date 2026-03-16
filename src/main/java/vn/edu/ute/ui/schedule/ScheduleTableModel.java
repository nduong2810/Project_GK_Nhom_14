package vn.edu.ute.ui.schedule;

import vn.edu.ute.model.Schedule;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp `ScheduleTableModel` là mô hình dữ liệu cho JTable hiển thị thông tin lịch học.
 * Nó có khả năng hiển thị hoặc ẩn cột "Giáo viên" và hỗ trợ lọc dữ liệu.
 */
public class ScheduleTableModel extends AbstractTableModel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final String[] columns;
    private final boolean showTeacher;
    private List<Schedule> allData = new ArrayList<>();
    private List<Schedule> filteredData = new ArrayList<>();

    /**
     * Constructor.
     * @param showTeacher `true` nếu muốn hiển thị cột "Giáo viên" (dành cho Admin/Staff),
     *                    `false` nếu muốn ẩn (dành cho Học viên).
     */
    public ScheduleTableModel(boolean showTeacher) {
        this.showTeacher = showTeacher;
        if (showTeacher) {
            columns = new String[] { "Ngày học", "Bắt đầu", "Kết thúc", "Lớp", "Khóa học", "Giáo viên",
                    "Phòng", "Địa Chỉ Chi Nhánh" };
        } else {
            columns = new String[] { "Ngày học", "Bắt đầu", "Kết thúc", "Lớp", "Khóa học", "Phòng",
                    "Địa Chỉ Chi Nhánh" };
        }
    }

    /**
     * Cập nhật dữ liệu cho model.
     */
    public void setData(List<Schedule> data) {
        this.allData = data;
        this.filteredData = new ArrayList<>(data);
        fireTableDataChanged();
    }

    /**
     * Lọc dữ liệu dựa trên từ khóa tìm kiếm.
     * Tìm kiếm trên các trường: tên lớp, khóa học, giáo viên, phòng, ngày, địa chỉ.
     */
    public void setFilter(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            filteredData = new ArrayList<>(allData);
        } else {
            String lowerKeyword = keyword.trim().toLowerCase();
            filteredData = allData.stream()
                    .filter(s -> {
                        String className = s.getClassEntity() != null ? s.getClassEntity().getClassName().toLowerCase() : "";
                        String courseName = (s.getClassEntity() != null && s.getClassEntity().getCourse() != null)
                                ? s.getClassEntity().getCourse().getCourseName().toLowerCase()
                                : "";
                        String teacherName = (s.getClassEntity() != null && s.getClassEntity().getTeacher() != null)
                                ? s.getClassEntity().getTeacher().getFullName().toLowerCase()
                                : "";
                        String roomName = s.getRoom() != null ? s.getRoom().getRoomName().toLowerCase() : "";
                        String dateStr = s.getStudyDate() != null ? s.getStudyDate().format(DATE_FMT) : "";
                        String branchAddress = "";
                        if (s.getRoom() != null && s.getRoom().getBranch() != null)
                            branchAddress = s.getRoom().getBranch().getAddress() != null
                                    ? s.getRoom().getBranch().getAddress().toLowerCase() : "";
                        else if (s.getClassEntity() != null && s.getClassEntity().getBranch() != null)
                            branchAddress = s.getClassEntity().getBranch().getAddress() != null
                                    ? s.getClassEntity().getBranch().getAddress().toLowerCase() : "";

                        return className.contains(lowerKeyword)
                                || courseName.contains(lowerKeyword)
                                || teacherName.contains(lowerKeyword)
                                || roomName.contains(lowerKeyword)
                                || dateStr.contains(lowerKeyword)
                                || branchAddress.contains(lowerKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    public Schedule getAt(int row) {
        return (row >= 0 && row < filteredData.size()) ? filteredData.get(row) : null;
    }

    @Override public int getRowCount() { return filteredData.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Schedule s = filteredData.get(rowIndex);

        if (showTeacher) {
            return getValueForAdmin(s, columnIndex);
        } else {
            return getValueForStudent(s, columnIndex);
        }
    }

    /**
     * Lấy giá trị cho các cột khi hiển thị cho Admin/Staff (có cột giáo viên).
     */
    private Object getValueForAdmin(Schedule s, int columnIndex) {
        return switch (columnIndex) {
            case 0 -> s.getStudyDate() != null ? s.getStudyDate().format(DATE_FMT) : "";
            case 1 -> s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : "";
            case 2 -> s.getEndTime() != null ? s.getEndTime().format(TIME_FMT) : "";
            case 3 -> s.getClassEntity() != null ? s.getClassEntity().getClassName() : "";
            case 4 -> (s.getClassEntity() != null && s.getClassEntity().getCourse() != null)
                    ? s.getClassEntity().getCourse().getCourseName() : "";
            case 5 -> (s.getClassEntity() != null && s.getClassEntity().getTeacher() != null)
                    ? s.getClassEntity().getTeacher().getFullName() : "(Chưa phân công)";
            case 6 -> s.getRoom() != null ? s.getRoom().getRoomName() : "(Chưa xếp phòng)";
            case 7 -> getBranchAddress(s);
            default -> "";
        };
    }

    /**
     * Lấy giá trị cho các cột khi hiển thị cho Học viên (không có cột giáo viên).
     */
    private Object getValueForStudent(Schedule s, int columnIndex) {
        return switch (columnIndex) {
            case 0 -> s.getStudyDate() != null ? s.getStudyDate().format(DATE_FMT) : "";
            case 1 -> s.getStartTime() != null ? s.getStartTime().format(TIME_FMT) : "";
            case 2 -> s.getEndTime() != null ? s.getEndTime().format(TIME_FMT) : "";
            case 3 -> s.getClassEntity() != null ? s.getClassEntity().getClassName() : "";
            case 4 -> (s.getClassEntity() != null && s.getClassEntity().getCourse() != null)
                    ? s.getClassEntity().getCourse().getCourseName() : "";
            case 5 -> s.getRoom() != null ? s.getRoom().getRoomName() : "(Chưa xếp phòng)";
            case 6 -> getBranchAddress(s);
            default -> "";
        };
    }

    /**
     * Lấy địa chỉ chi nhánh. Ưu tiên địa chỉ từ phòng học, nếu không có thì lấy từ lớp học.
     */
    private String getBranchAddress(Schedule s) {
        if (s.getRoom() != null && s.getRoom().getBranch() != null && s.getRoom().getBranch().getAddress() != null) {
            return s.getRoom().getBranch().getAddress();
        }
        if (s.getClassEntity() != null && s.getClassEntity().getBranch() != null && s.getClassEntity().getBranch().getAddress() != null) {
            return s.getClassEntity().getBranch().getAddress();
        }
        return "(Chưa có địa chỉ)";
    }
}
