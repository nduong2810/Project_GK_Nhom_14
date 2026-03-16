package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.repo.ScheduleRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Service cho nghiệp vụ quản lý và truy vấn lịch học (Schedule).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class ScheduleService {

    private final ScheduleRepository scheduleRepo;
    private final TransactionManager tx;

    public ScheduleService(ScheduleRepository scheduleRepo, TransactionManager tx) {
        this.scheduleRepo = scheduleRepo;
        this.tx = tx;
    }

    /**
     * Lấy tất cả lịch hoạt động của trung tâm (dành cho Admin/Staff).
     * Dữ liệu được sắp xếp theo ngày học, sau đó là giờ bắt đầu.
     * @return Danh sách các đối tượng Schedule đã được sắp xếp.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Schedule> getAllSchedules() throws Exception {
        return tx.runInTransaction(em -> {
            List<Schedule> schedules = scheduleRepo.findAll(em);
            // Sắp xếp lại danh sách bằng Stream API sau khi lấy từ CSDL.
            return schedules.stream()
                    .sorted(Comparator.comparing(Schedule::getStudyDate)
                            .thenComparing(Schedule::getStartTime))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy lịch dạy của một giáo viên cụ thể.
     * @param teacherId ID của giáo viên.
     * @return Danh sách lịch dạy của giáo viên đó, đã được sắp xếp.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Schedule> getSchedulesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Schedule> schedules = scheduleRepo.findByTeacherId(em, teacherId);
            return schedules.stream()
                    .sorted(Comparator.comparing(Schedule::getStudyDate)
                            .thenComparing(Schedule::getStartTime))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy lịch học của một học viên cụ thể (chỉ các lớp đang 'Enrolled').
     * @param studentId ID của học viên.
     * @return Danh sách lịch học của học viên đó, đã được sắp xếp.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Schedule> getSchedulesByStudent(Long studentId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Schedule> schedules = scheduleRepo.findByStudentId(em, studentId);
            return schedules.stream()
                    .sorted(Comparator.comparing(Schedule::getStudyDate)
                            .thenComparing(Schedule::getStartTime))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lọc một danh sách lịch học (đã có) theo một khoảng thời gian.
     * Phương thức này thực hiện lọc ở phía client-side (sau khi dữ liệu đã được tải về từ CSDL).
     * @param schedules Danh sách lịch học cần lọc.
     * @param from Ngày bắt đầu khoảng thời gian (bao gồm).
     * @param to Ngày kết thúc khoảng thời gian (bao gồm).
     * @return Danh sách lịch học mới đã được lọc và sắp xếp.
     */
    public List<Schedule> filterByDateRange(List<Schedule> schedules, LocalDate from, LocalDate to) {
        return schedules.stream()
                .filter(s -> {
                    LocalDate date = s.getStudyDate();
                    // Kiểm tra xem ngày học có nằm sau hoặc bằng ngày 'from' không.
                    boolean afterFrom = (from == null) || !date.isBefore(from);
                    // Kiểm tra xem ngày học có nằm trước hoặc bằng ngày 'to' không.
                    boolean beforeTo = (to == null) || !date.isAfter(to);
                    return afterFrom && beforeTo;
                })
                .sorted(Comparator.comparing(Schedule::getStudyDate)
                        .thenComparing(Schedule::getStartTime))
                .collect(Collectors.toList());
    }
}
