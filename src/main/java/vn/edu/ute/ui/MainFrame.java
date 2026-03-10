package vn.edu.ute.ui;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.AttendanceService;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.ClassService;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.NotificationService;
import vn.edu.ute.service.PromotionService;
import vn.edu.ute.service.ResultService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.ScheduleService;
import vn.edu.ute.service.StaffService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.TeacherService;
import vn.edu.ute.service.UserAccountService;
import vn.edu.ute.ui.attendance.AttendancePanel;
import vn.edu.ute.ui.branch.BranchPanel;
import vn.edu.ute.ui.classmgmt.ClassPanel;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.finance.FinancePanel;
import vn.edu.ute.ui.notification.NotificationPanel;
import vn.edu.ute.ui.notification.NotificationViewPanel;
import vn.edu.ute.ui.promotion.PromotionPanel;
import vn.edu.ute.ui.result.GradeEntryPanel;
import vn.edu.ute.ui.result.StudentGradePanel;
import vn.edu.ute.ui.room.RoomPanel;
import vn.edu.ute.ui.schedule.CenterSchedulePanel;
import vn.edu.ute.ui.schedule.StudentSchedulePanel;
import vn.edu.ute.ui.schedule.TeacherSchedulePanel;
import vn.edu.ute.ui.enrollment.EnrollmentPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private RoomService roomService;
    private CourseService courseService;
    private ClassService classService;
    private TeacherService teacherService;
    private StudentService studentService;
    private EnrollmentService enrollmentService;
    private FinanceService financeService;
    private ScheduleService scheduleService;
    private AttendanceService attendanceService;
    private StaffService staffService;
    private ResultService resultService;
    private UserAccountService userAccountService;
    private PromotionService promotionService;
    private BranchService branchService;
    private NotificationService notificationService;
    private LoginView loginView;

    private UserAccount currentUser;
    private JLabel userInfoLabel;
    private JButton logoutButton;

    public MainFrame(RoomService roomService, CourseService courseService, ClassService classService,
            TeacherService teacherService, StudentService studentService, EnrollmentService enrollmentService,
            FinanceService financeService, ScheduleService scheduleService, AttendanceService attendanceService,
            StaffService staffService, UserAccountService userAccountService, ResultService resultService,
            PromotionService promotionService, BranchService branchService, NotificationService notificationService,
            LoginView loginView) {
        super("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ");
        this.roomService = roomService;
        this.courseService = courseService;
        this.classService = classService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
        this.financeService = financeService;
        this.scheduleService = scheduleService;
        this.attendanceService = attendanceService;
        this.staffService = staffService;
        this.userAccountService = userAccountService;
        this.resultService = resultService;
        this.promotionService = promotionService;
        this.branchService = branchService;
        this.notificationService = notificationService;
        this.loginView = loginView;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // ===== Header Panel (Gradient) =====
        JPanel headerPanel = UITheme.createGradientHeader();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 52));

        // Left: App title
        JLabel appTitle = new JLabel("    Trung Tâm Ngoại Ngữ");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);
        headerPanel.add(appTitle, BorderLayout.WEST);

        // Right: User info + Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setOpaque(false);

        userInfoLabel = new JLabel();
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userInfoLabel.setForeground(new Color(219, 234, 254));

        logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(255, 255, 255, 30));
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logoutButton.setBackground(new Color(255, 255, 255, 60));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                logoutButton.setBackground(new Color(255, 255, 255, 30));
            }
        });

        userPanel.add(userInfoLabel);
        userPanel.add(logoutButton);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // ===== Tabbed Pane =====
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // ===== Main Panel =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_MAIN);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public void setUser(UserAccount user) {
        this.currentUser = user;
        String name = "Unknown";
        if (user.getStaff() != null) {
            name = user.getStaff().getFullName();
        } else if (user.getTeacher() != null) {
            name = user.getTeacher().getFullName();
        } else if (user.getStudent() != null) {
            name = user.getStudent().getFullName();
        }
        userInfoLabel.setText("Chào, " + name + "  (" + user.getRole() + ")");
        showMenuByUserRole(user.getRole().toString());
    }

    private void logout() {
        this.currentUser = null;
        this.setVisible(false);
        loginView.setVisible(true);
    }

    public void showMenuByUserRole(String role) {
        tabbedPane.removeAll();

        if ("Admin".equalsIgnoreCase(role)) {
            StaffPanel staffPanel = new StaffPanel(staffService);
            tabbedPane.addTab("  Nhân viên  ", staffPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Thêm/Sửa/Xóa Nhân viên");

            UserAccountPanel userAccountPanel = new UserAccountPanel(userAccountService);
            tabbedPane.addTab("  Tài khoản  ", userAccountPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Thêm/Sửa/Xóa Tài khoản");
        }

        if ("Staff".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
            StudentPanel studentPanel = new StudentPanel(studentService);
            tabbedPane.addTab("  Học viên  ", studentPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Thêm/Sửa/Xóa Học viên");

            TeacherPanel teacherPanel = new TeacherPanel(teacherService);
            tabbedPane.addTab("  Giáo viên  ", teacherPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Thêm/Sửa/Xóa Giáo viên");

            BranchPanel branchPanel = new BranchPanel(branchService);
            tabbedPane.addTab("  Chi Nhánh  ", branchPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Quản lý chi nhánh trung tâm");

            RoomPanel roomPanel = new RoomPanel(roomService, branchService);
            tabbedPane.addTab("  Phòng học  ", roomPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Thêm/Sửa/Xóa Phòng học");

            CoursePanel coursePanel = new CoursePanel(courseService);
            tabbedPane.addTab("  Khóa học  ", coursePanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Thêm/Sửa/Xóa Khóa học");

            ClassPanel classPanel = new ClassPanel(classService, courseService, teacherService, roomService,
                    branchService);
            tabbedPane.addTab("  Lớp học  ", classPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Mở/Sửa/Xóa Lớp học");

            EnrollmentPanel enrollmentPanel = new EnrollmentPanel(enrollmentService, studentService, classService);
            tabbedPane.addTab("  Ghi danh  ", enrollmentPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Ghi danh học viên vào lớp học");

            FinancePanel financePanel = new FinancePanel(financeService, promotionService);
            tabbedPane.addTab("  Tài chính  ", financePanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Hóa đơn / Thanh toán học phí");

            PromotionPanel promotionPanel = new PromotionPanel(promotionService);
            tabbedPane.addTab("  Khuyến mãi  ", promotionPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Quản lý chương trình khuyến mãi");

            CenterSchedulePanel schedulePanel = new CenterSchedulePanel(scheduleService);
            tabbedPane.addTab("  Lịch hoạt động  ", schedulePanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Xem lịch hoạt động chung của trung tâm");

            NotificationPanel notificationPanel = new NotificationPanel(notificationService, currentUser);
            tabbedPane.addTab("  Thông Báo  ", notificationPanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Quản lý thông báo cho học viên, giáo viên");

        } else if ("Teacher".equalsIgnoreCase(role)) {
            CoursePanel coursePanel = new CoursePanel(courseService);
            tabbedPane.addTab("  Khóa học  ", coursePanel);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Xem thông tin khóa học");

            if (currentUser != null && currentUser.getTeacher() != null) {
                Long tid = currentUser.getTeacher().getTeacherId();
                TeacherSchedulePanel teacherSchedulePanel = new TeacherSchedulePanel(scheduleService, tid);
                tabbedPane.addTab("  Lịch dạy  ", teacherSchedulePanel);
                tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Xem lịch dạy của bạn");

                AttendancePanel attendancePanel = new AttendancePanel(attendanceService, tid);
                tabbedPane.addTab("  Điểm danh  ", attendancePanel);
                tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Điểm danh học viên cho lớp bạn dạy");

                GradeEntryPanel gradeEntryPanel = new GradeEntryPanel(resultService, tid);
                tabbedPane.addTab("  Nhập điểm  ", gradeEntryPanel);
                tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Nhập điểm cho học viên các lớp bạn dạy");
            }

            // Tab xem thông báo cho Teacher
            NotificationViewPanel notifView = new NotificationViewPanel(notificationService, currentUser);
            tabbedPane.addTab("  Thông Báo  ", notifView);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Xem thông báo từ trung tâm");

        } else if ("Student".equalsIgnoreCase(role)) {
            if (currentUser != null && currentUser.getStudent() != null) {
                Long sid = currentUser.getStudent().getStudentId();
                StudentSchedulePanel studentSchedulePanel = new StudentSchedulePanel(scheduleService, sid);
                tabbedPane.addTab("  Lịch học  ", studentSchedulePanel);
                tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Xem lịch học của bạn");

                StudentGradePanel studentGradePanel = new StudentGradePanel(resultService, sid);
                tabbedPane.addTab("  Xem điểm  ", studentGradePanel);
                tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Xem kết quả học tập của bạn");
            }

            // Tab xem thông báo cho Student
            NotificationViewPanel notifView = new NotificationViewPanel(notificationService, currentUser);
            tabbedPane.addTab("  Thông Báo  ", notifView);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, "Xem thông báo từ trung tâm");
        }

        revalidate();
        repaint();
    }
}
