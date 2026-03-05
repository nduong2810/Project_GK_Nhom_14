package vn.edu.ute.ui;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.AttendanceService;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.ScheduleService;
import vn.edu.ute.ui.attendance.AttendancePanel;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.finance.FinancePanel;
import vn.edu.ute.ui.room.RoomPanel;
import vn.edu.ute.ui.schedule.CenterSchedulePanel;
import vn.edu.ute.ui.schedule.StudentSchedulePanel;
import vn.edu.ute.ui.schedule.TeacherSchedulePanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private RoomService roomService;
    private CourseService courseService;
    private FinanceService financeService;
    private ScheduleService scheduleService;
    private AttendanceService attendanceService;
    private LoginView loginView;

    private UserAccount currentUser; // Lưu user hiện tại để lấy ID khi tạo tab lịch
    private JLabel userInfoLabel;
    private JButton logoutButton;

    public MainFrame(RoomService roomService, CourseService courseService,
            FinanceService financeService, ScheduleService scheduleService,
            AttendanceService attendanceService, LoginView loginView) {
        super("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ");
        this.roomService = roomService;
        this.courseService = courseService;
        this.financeService = financeService;
        this.scheduleService = scheduleService;
        this.attendanceService = attendanceService;
        this.loginView = loginView;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        // User Info Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        userInfoLabel = new JLabel();
        userInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> logout());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(userInfoLabel);
        rightPanel.add(logoutButton);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
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
        userInfoLabel.setText("Chào, " + name + " (" + user.getRole() + ")");
        showMenuByUserRole(user.getRole().toString());
    }

    private void logout() {
        this.currentUser = null;
        this.setVisible(false);
        loginView.setVisible(true);
    }

    public void showMenuByUserRole(String role) {
        tabbedPane.removeAll();

        if ("Staff".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
            // --- Các tab quản lý ---
            RoomPanel roomPanel = new RoomPanel(roomService);
            tabbedPane.addTab("Quản lý Phòng học", new ImageIcon(), roomPanel, "Thêm/Sửa/Xóa Phòng học");

            CoursePanel coursePanel = new CoursePanel(courseService);
            tabbedPane.addTab("Quản lý Khóa học", new ImageIcon(), coursePanel, "Thêm/Sửa/Xóa Khóa học");

            FinancePanel financePanel = new FinancePanel(financeService);
            tabbedPane.addTab("Quản lý Tài chính", new ImageIcon(), financePanel, "Hóa đơn / Thanh toán học phí");

            // --- Tab Lịch hoạt động (toàn trung tâm) ---
            CenterSchedulePanel schedulePanel = new CenterSchedulePanel(scheduleService);
            tabbedPane.addTab("Lịch hoạt động", new ImageIcon(), schedulePanel,
                    "Xem lịch hoạt động chung của trung tâm");

        } else if ("Teacher".equalsIgnoreCase(role)) {
            CoursePanel coursePanel = new CoursePanel(courseService);
            tabbedPane.addTab("Quản lý Khóa học", new ImageIcon(), coursePanel, "Xem thông tin khóa học");

            // --- Tab Lịch dạy (chỉ giáo viên đang đăng nhập) ---
            if (currentUser != null && currentUser.getTeacher() != null) {
                Long tid = currentUser.getTeacher().getTeacherId();

                TeacherSchedulePanel teacherSchedulePanel = new TeacherSchedulePanel(scheduleService, tid);
                tabbedPane.addTab("Lịch dạy", new ImageIcon(), teacherSchedulePanel, "Xem lịch dạy của bạn");

                // --- Tab Điểm danh ---
                AttendancePanel attendancePanel = new AttendancePanel(attendanceService, tid);
                tabbedPane.addTab("Điểm danh", new ImageIcon(), attendancePanel, "Điểm danh học viên cho lớp bạn dạy");
            }

        } else if ("Student".equalsIgnoreCase(role)) {
            // --- Tab Lịch học (chỉ học viên đang đăng nhập) ---
            if (currentUser != null && currentUser.getStudent() != null) {
                StudentSchedulePanel studentSchedulePanel = new StudentSchedulePanel(
                        scheduleService, currentUser.getStudent().getStudentId());
                tabbedPane.addTab("Lịch học", new ImageIcon(), studentSchedulePanel, "Xem lịch học của bạn");
            }
        }

        revalidate();
        repaint();
    }
}