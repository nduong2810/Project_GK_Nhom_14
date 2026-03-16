package vn.edu.ute.ui.role;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.notification.NotificationViewPanel;
import vn.edu.ute.ui.result.StudentGradePanel;
import vn.edu.ute.ui.schedule.StudentSchedulePanel;

import javax.swing.*;

/**
 * Lớp `StudentMenuBuilder` triển khai `MenuBuilder` để xây dựng menu
 * dành riêng cho người dùng có vai trò là Học viên (Student).
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

    /**
     * {@inheritDoc}
     * Builder này chỉ hỗ trợ vai trò `Student`.
     */
    @Override
    public boolean supports(UserAccount.Role role) {
        return role == UserAccount.Role.Student;
    }

    /**
     * {@inheritDoc}
     * Xây dựng menu cho học viên, bao gồm các chức năng chính:
     * - Xem lịch học.
     * - Xem điểm.
     * - Xem thông báo.
     */
    @Override
    public void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser) {
        // Chỉ thêm các tab liên quan đến học viên nếu tài khoản người dùng được liên kết với một hồ sơ học viên
        if (currentUser.getStudent() != null) {
            Long studentId = currentUser.getStudent().getStudentId();
            addTab(tabbedPane, "  Lịch học  ", new StudentSchedulePanel(scheduleService, studentId), "Xem lịch học của bạn");
            addTab(tabbedPane, "  Xem điểm  ", new StudentGradePanel(studentGradeService, studentId),
                    "Xem kết quả học tập của bạn");
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
