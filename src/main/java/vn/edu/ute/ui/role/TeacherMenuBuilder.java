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
 * OCP: MenuBuilder cho Teacher — thêm/sửa tab chỉ cần sửa class này.
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

    @Override
    public boolean supports(UserAccount.Role role) {
        return role == UserAccount.Role.Teacher;
    }

    @Override
    public void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser) {
        addTab(tabbedPane, "  Khóa học  ", new CoursePanel(courseService), "Xem thông tin khóa học");

        if (currentUser.getTeacher() != null) {
            Long tid = currentUser.getTeacher().getTeacherId();
            addTab(tabbedPane, "  Lịch dạy  ", new TeacherSchedulePanel(scheduleService, tid), "Xem lịch dạy của bạn");
            addTab(tabbedPane, "  Điểm danh  ", new AttendancePanel(attendanceService, tid),
                    "Điểm danh học viên cho lớp bạn dạy");
            addTab(tabbedPane, "  Nhập điểm  ", new GradeEntryPanel(gradeEntryService, tid),
                    "Nhập điểm cho học viên các lớp bạn dạy");
        }

        addTab(tabbedPane, "  Thông Báo  ", new NotificationViewPanel(notificationService, currentUser),
                "Xem thông báo từ trung tâm");
    }

    private void addTab(JTabbedPane tabbedPane, String title, java.awt.Component component, String tooltip) {
        tabbedPane.addTab(title, component);
        tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, tooltip);
    }
}
