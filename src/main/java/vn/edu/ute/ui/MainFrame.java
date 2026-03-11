package vn.edu.ute.ui;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.role.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * OCP + SRP: MainFrame không còn chứa logic xây dựng menu theo role.
 * Thay vào đó, nó nhận danh sách MenuBuilder và delegate sang builder phù hợp.
 * Thêm role mới: chỉ cần tạo MenuBuilder mới, KHÔNG sửa MainFrame.
 */
public class MainFrame extends JFrame {
    private final JTabbedPane tabbedPane;
    private final LoginView loginView;
    private final JLabel userInfoLabel;
    private final JButton logoutButton;

    /** OCP: Danh sách MenuBuilder — dễ mở rộng thêm role mà không sửa class này. */
    private final List<MenuBuilder> menuBuilders;

    public MainFrame(RoomService roomService, CourseService courseService, ClassService classService,
            TeacherService teacherService, StudentService studentService, EnrollmentService enrollmentService,
            InvoiceService invoiceService, PaymentService paymentService, RefundService refundService,
            ScheduleService scheduleService, AttendanceService attendanceService,
            StaffService staffService, UserAccountService userAccountService,
            GradeEntryService gradeEntryService, StudentGradeService studentGradeService,
            PromotionService promotionService, BranchService branchService, NotificationService notificationService,
            PlacementTestService placementTestService, CertificateService certificateService,
            LoginView loginView) {
        super("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ");
        this.loginView = loginView;

        // OCP: Khởi tạo các MenuBuilder — thêm role mới = thêm builder vào đây
        this.menuBuilders = List.of(
            new AdminMenuBuilder(staffService, userAccountService, studentService, teacherService,
                    branchService, roomService, courseService, classService, enrollmentService,
                    invoiceService, paymentService, refundService, promotionService, scheduleService,
                    notificationService, placementTestService, certificateService),
            new StaffMenuBuilder(studentService, teacherService, branchService, roomService,
                    courseService, classService, enrollmentService, invoiceService, paymentService,
                    refundService, promotionService, scheduleService, notificationService, placementTestService, certificateService),
            new TeacherMenuBuilder(courseService, scheduleService, attendanceService, gradeEntryService,
                    notificationService),
            new StudentMenuBuilder(scheduleService, studentGradeService, notificationService)
        );

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // ===== Header Panel (Gradient) =====
        JPanel headerPanel = UITheme.createGradientHeader();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 52));

        JLabel appTitle = new JLabel("    Trung Tâm Ngoại Ngữ");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);
        headerPanel.add(appTitle, BorderLayout.WEST);

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

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_MAIN);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public void setUser(UserAccount user) {
        String name = "Unknown";
        if (user.getStaff() != null) {
            name = user.getStaff().getFullName();
        } else if (user.getTeacher() != null) {
            name = user.getTeacher().getFullName();
        } else if (user.getStudent() != null) {
            name = user.getStudent().getFullName();
        }
        userInfoLabel.setText("Chào, " + name + "  (" + user.getRole() + ")");
        buildMenuForUser(user);
    }

    private void logout() {
        this.setVisible(false);
        loginView.setVisible(true);
    }

    /**
     * OCP: Tìm MenuBuilder phù hợp và delegate việc xây dựng menu.
     * Không cần if/else theo role — chỉ cần thêm builder mới vào danh sách.
     */
    private void buildMenuForUser(UserAccount user) {
        tabbedPane.removeAll();
        menuBuilders.stream()
                .filter(builder -> builder.supports(user.getRole()))
                .findFirst()
                .ifPresent(builder -> builder.buildMenu(tabbedPane, user));
        revalidate();
        repaint();
    }
}
