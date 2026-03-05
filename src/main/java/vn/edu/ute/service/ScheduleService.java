package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.repo.ScheduleRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleService {

    private final ScheduleRepository scheduleRepo;
    private final TransactionManager tx;

    // Dependency Injection qua Constructor
    public ScheduleService(ScheduleRepository scheduleRepo, TransactionManager tx) {
        this.scheduleRepo = scheduleRepo;
        this.tx = tx;
    }

    /**
     * Lấy tất cả lịch hoạt động của trung tâm (Admin/Staff).
     * Sắp xếp theo ngày học → giờ bắt đầu bằng Stream API.
     */
    public List<Schedule> getAllSchedules() throws Exception {
        return tx.runInTransaction(em -> {
            List<Schedule> schedules = scheduleRepo.findAll(em);
            return schedules.stream()
                    .sorted(Comparator.comparing(Schedule::getStudyDate)
                            .thenComparing(Schedule::getStartTime))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy lịch dạy của một giáo viên.
     * Sắp xếp theo ngày học → giờ bắt đầu bằng Stream API.
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
     * Lấy lịch học của một học viên (chỉ enrollment đang Enrolled).
     * Sắp xếp theo ngày học → giờ bắt đầu bằng Stream API.
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
     * Lọc danh sách schedule theo khoảng ngày bằng Stream API filter().
     * Dùng để filter client-side sau khi đã load dữ liệu.
     */
    public List<Schedule> filterByDateRange(List<Schedule> schedules, LocalDate from, LocalDate to) {
        return schedules.stream()
                .filter(s -> {
                    LocalDate date = s.getStudyDate();
                    boolean afterFrom = (from == null) || !date.isBefore(from);
                    boolean beforeTo = (to == null) || !date.isAfter(to);
                    return afterFrom && beforeTo;
                })
                .sorted(Comparator.comparing(Schedule::getStudyDate)
                        .thenComparing(Schedule::getStartTime))
                .collect(Collectors.toList());
    }
}
