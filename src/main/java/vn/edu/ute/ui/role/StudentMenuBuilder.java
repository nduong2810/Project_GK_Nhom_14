package vn.edu.ute.ui.role;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.notification.NotificationViewPanel;
import vn.edu.ute.ui.result.StudentGradePanel;
import vn.edu.ute.ui.schedule.StudentSchedulePanel;

import javax.swing.*;

/**
 * OCP: MenuBuilder cho Student — thêm/sửa tab chỉ cần sửa class này.
 */
public class StudentMenuBuilder implements MenuBuilder {

    private final ScheduleService scheduleService;
    private final StudentGradeService studentGradeService;
    private final NotificationService notificationService;

    public StudentMenuBuilder(ScheduleService scheduleService, StudentGradeService studentGradeService,
            NotificationService notificationService) {
        this.scheduleService = scheduleService;
        this.studentGradeService = studentGradeService;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(UserAccount.Role role) {
        return role == UserAccount.Role.Student;
    }

    @Override
    public void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser) {
        if (currentUser.getStudent() != null) {
            Long sid = currentUser.getStudent().getStudentId();
            addTab(tabbedPane, "  Lịch học  ", new StudentSchedulePanel(scheduleService, sid), "Xem lịch học của bạn");
            addTab(tabbedPane, "  Xem điểm  ", new StudentGradePanel(studentGradeService, sid),
                    "Xem kết quả học tập của bạn");
        }

        addTab(tabbedPane, "  Thông Báo  ", new NotificationViewPanel(notificationService, currentUser),
                "Xem thông báo từ trung tâm");
    }

    private void addTab(JTabbedPane tabbedPane, String title, java.awt.Component component, String tooltip) {
        tabbedPane.addTab(title, component);
        tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, tooltip);
    }
}
