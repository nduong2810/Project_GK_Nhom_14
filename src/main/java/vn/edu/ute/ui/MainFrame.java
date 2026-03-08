package vn.edu.ute.ui;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.attendance.AttendancePanel;
import vn.edu.ute.ui.classmgmt.ClassPanel;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.finance.FinancePanel;
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
    private LoginView loginView;

    private UserAccount currentUser;
    private JLabel userInfoLabel;
    private JButton logoutButton;

    public MainFrame(RoomService roomService, CourseService courseService, ClassService classService,
                     TeacherService teacherService, StudentService studentService, EnrollmentService enrollmentService,
                     FinanceService financeService, ScheduleService scheduleService, AttendanceService attendanceService,
                     StaffService staffService, UserAccountService userAccountService, ResultService resultService,
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

        if ("Admin".equalsIgnoreCase(role)) {
            StaffPanel staffPanel = new StaffPanel(staffService);
            tabbedPane.addTab("Quản lý Nhân viên", new ImageIcon(), staffPanel, "Thêm/Sửa/Xóa Nhân viên");

            UserAccountPanel userAccountPanel = new UserAccountPanel(userAccountService);
            tabbedPane.addTab("Quản lý Tài khoản", new ImageIcon(), userAccountPanel, "Thêm/Sửa/Xóa Tài khoản");
        }

        if ("Staff".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role)) {
            StudentPanel studentPanel = new StudentPanel(studentService);
            tabbedPane.addTab("Quản lý Học viên", new ImageIcon(), studentPanel, "Thêm/Sửa/Xóa Học viên");

            TeacherPanel teacherPanel = new TeacherPanel(teacherService);
            tabbedPane.addTab("Quản lý Giáo viên", new ImageIcon(), teacherPanel, "Thêm/Sửa/Xóa Giáo viên");

            RoomPanel roomPanel = new RoomPanel(roomService);
            tabbedPane.addTab("Quản lý Phòng học", new ImageIcon(), roomPanel, "Thêm/Sửa/Xóa Phòng học");

            CoursePanel coursePanel = new CoursePanel(courseService);
            tabbedPane.addTab("Quản lý Khóa học", new ImageIcon(), coursePanel, "Thêm/Sửa/Xóa Khóa học");

            ClassPanel classPanel = new ClassPanel(classService, courseService, teacherService, roomService);
            tabbedPane.addTab("Quản lý Lớp học", new ImageIcon(), classPanel, "Mở/Sửa/Xóa Lớp học");

            EnrollmentPanel enrollmentPanel = new EnrollmentPanel(enrollmentService, studentService, classService);
            tabbedPane.addTab("Ghi Danh Học Viên", new ImageIcon(), enrollmentPanel, "Ghi danh học viên vào lớp học");

            FinancePanel financePanel = new FinancePanel(financeService);
            tabbedPane.addTab("Quản lý Tài chính", new ImageIcon(), financePanel, "Hóa đơn / Thanh toán học phí");

            CenterSchedulePanel schedulePanel = new CenterSchedulePanel(scheduleService);
            tabbedPane.addTab("Lịch hoạt động", new ImageIcon(), schedulePanel,
                    "Xem lịch hoạt động chung của trung tâm");

        } else if ("Teacher".equalsIgnoreCase(role)) {
            CoursePanel coursePanel = new CoursePanel(courseService);
            tabbedPane.addTab("Quản lý Khóa học", new ImageIcon(), coursePanel, "Xem thông tin khóa học");

            if (currentUser != null && currentUser.getTeacher() != null) {
                Long tid = currentUser.getTeacher().getTeacherId();
                TeacherSchedulePanel teacherSchedulePanel = new TeacherSchedulePanel(scheduleService, tid);
                tabbedPane.addTab("Lịch dạy", new ImageIcon(), teacherSchedulePanel, "Xem lịch dạy của bạn");

                AttendancePanel attendancePanel = new AttendancePanel(attendanceService, tid);
                tabbedPane.addTab("Điểm danh", new ImageIcon(), attendancePanel, "Điểm danh học viên cho lớp bạn dạy");

                GradeEntryPanel gradeEntryPanel = new GradeEntryPanel(resultService, tid);
                tabbedPane.addTab("Nhập điểm", new ImageIcon(), gradeEntryPanel,
                        "Nhập điểm cho học viên các lớp bạn dạy");
            }

        } else if ("Student".equalsIgnoreCase(role)) {
            if (currentUser != null && currentUser.getStudent() != null) {
                Long sid = currentUser.getStudent().getStudentId();
                StudentSchedulePanel studentSchedulePanel = new StudentSchedulePanel(scheduleService, sid);
                tabbedPane.addTab("Lịch học", new ImageIcon(), studentSchedulePanel, "Xem lịch học của bạn");

                StudentGradePanel studentGradePanel = new StudentGradePanel(resultService, sid);
                tabbedPane.addTab("Xem điểm", new ImageIcon(), studentGradePanel, "Xem kết quả học tập của bạn");
            }
        }

        revalidate();
        repaint();
    }
}