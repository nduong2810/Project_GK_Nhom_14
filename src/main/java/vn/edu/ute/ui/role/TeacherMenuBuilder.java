package vn.edu.ute.ui.role;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.attendance.AttendancePanel;
import vn.edu.ute.ui.notification.NotificationViewPanel;
import vn.edu.ute.ui.result.GradeEntryPanel;
import vn.edu.ute.ui.schedule.TeacherSchedulePanel;

import javax.swing.*;

/**
 * Lớp `TeacherMenuBuilder` triển khai `MenuBuilder` để xây dựng menu
 * dành riêng cho người dùng có vai trò là Giáo viên (Teacher).
 */
public class TeacherMenuBuilder implements MenuBuilder {

    private final CourseService courseService;
    private final ScheduleService scheduleService;
    private final AttendanceService attendanceService;
    private final GradeEntryService gradeEntryService;
    private final NotificationService notificationService;

    public TeacherMenuBuilder(CourseService courseService, ScheduleService scheduleService,
            AttendanceService attendanceService, GradeEntryService gradeEntryService,
            NotificationService notificationService) {
        this.courseService = courseService;
        this.scheduleService = scheduleService;
        this.attendanceService = attendanceService;
        this.gradeEntryService = gradeEntryService;
        this.notificationService = notificationService;
    }

    /**
     * {@inheritDoc}
     * Builder này chỉ hỗ trợ vai trò `Teacher`.
     */
    @Override
    public boolean supports(UserAccount.Role role) {
        return role == UserAccount.Role.Teacher;
    }

    /**
     * {@inheritDoc}
     * Xây dựng menu cho giáo viên, bao gồm các chức năng chính:
     * - Xem thông tin khóa học.
     * - Xem lịch dạy cá nhân.
     * - Điểm danh.
     * - Nhập điểm.
     * - Xem thông báo.
     */
    @Override
    public void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser) {
        addTab(tabbedPane, "  Khóa học  ", new CoursePanel(courseService), "Xem thông tin khóa học");

        // Chỉ thêm các tab yêu cầu teacherId nếu tài khoản được liên kết với một giáo viên
        if (currentUser.getTeacher() != null) {
            Long teacherId = currentUser.getTeacher().getTeacherId();
            addTab(tabbedPane, "  Lịch dạy  ", new TeacherSchedulePanel(scheduleService, teacherId), "Xem lịch dạy của bạn");
            addTab(tabbedPane, "  Điểm danh  ", new AttendancePanel(attendanceService, teacherId),
                    "Điểm danh học viên cho lớp bạn dạy");
            addTab(tabbedPane, "  Nhập điểm  ", new GradeEntryPanel(gradeEntryService, teacherId),
                    "Nhập điểm cho học viên các lớp bạn dạy");
        }

        addTab(tabbedPane, "  Thông Báo  ", new NotificationViewPanel(notificationService, currentUser),
                "Xem thông báo từ trung tâm");
    }

    /**
     * Phương thức tiện ích để thêm tab vào `JTabbedPane`.
     */
    private void addTab(JTabbedPane tabbedPane, String title, java.awt.Component component, String tooltip) {
        tabbedPane.addTab(title, component);
        tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, tooltip);
    }
}
