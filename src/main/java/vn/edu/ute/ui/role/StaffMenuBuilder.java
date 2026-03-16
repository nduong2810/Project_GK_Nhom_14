package vn.edu.ute.ui.role;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.*;
import vn.edu.ute.ui.branch.BranchPanel;
import vn.edu.ute.ui.classmgmt.ClassPanel;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.enrollment.EnrollmentPanel;
import vn.edu.ute.ui.finance.FinancePanel;
import vn.edu.ute.ui.notification.NotificationPanel;
import vn.edu.ute.ui.promotion.PromotionPanel;
import vn.edu.ute.ui.room.RoomPanel;
import vn.edu.ute.ui.schedule.CenterSchedulePanel;
import vn.edu.ute.ui.placementtest.PlacementTestPanel;
import vn.edu.ute.ui.certificate.CertificatePanel;

import javax.swing.*;

/**
 * Lớp `StaffMenuBuilder` triển khai `MenuBuilder` để xây dựng menu
 * dành riêng cho người dùng có vai trò là Nhân viên (Staff).
 *
 * OCP (Open/Closed Principle): Việc thay đổi các chức năng của nhân viên
 * chỉ cần được thực hiện trong lớp này.
 */
public class StaffMenuBuilder implements MenuBuilder {

    // Các service cần thiết để khởi tạo các panel chức năng
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final BranchService branchService;
    private final RoomService roomService;
    private final CourseService courseService;
    private final ClassService classService;
    private final EnrollmentService enrollmentService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final RefundService refundService;
    private final PromotionService promotionService;
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;
    private final PlacementTestService placementTestService;
    private final CertificateService certificateService;

    /**
     * Constructor nhận các service cần thiết qua Dependency Injection.
     */
    public StaffMenuBuilder(StudentService studentService, TeacherService teacherService,
            BranchService branchService, RoomService roomService,
            CourseService courseService, ClassService classService,
            EnrollmentService enrollmentService, InvoiceService invoiceService,
            PaymentService paymentService, RefundService refundService,
            PromotionService promotionService, ScheduleService scheduleService,
            NotificationService notificationService, PlacementTestService placementTestService, CertificateService certificateService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.branchService = branchService;
        this.roomService = roomService;
        this.courseService = courseService;
        this.classService = classService;
        this.enrollmentService = enrollmentService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.refundService = refundService;
        this.promotionService = promotionService;
        this.scheduleService = scheduleService;
        this.notificationService = notificationService;
        this.placementTestService = placementTestService;
        this.certificateService = certificateService;
    }

    /**
     * {@inheritDoc}
     * Builder này chỉ hỗ trợ vai trò `Staff`.
     */
    @Override
    public boolean supports(UserAccount.Role role) {
        return role == UserAccount.Role.Staff;
    }

    /**
     * {@inheritDoc}
     * Xây dựng menu cho nhân viên. Nhân viên có quyền truy cập hầu hết các chức năng
     * quản lý hoạt động hàng ngày của trung tâm, tương tự như Admin nhưng có thể bị
     * hạn chế một số quyền quản trị hệ thống (ví dụ: quản lý tài khoản người dùng).
     */
    @Override
    public void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser) {
        addTab(tabbedPane, "  Học viên  ", new StudentPanel(studentService), "Quản lý thông tin học viên");
        addTab(tabbedPane, "  Giáo viên  ", new TeacherPanel(teacherService), "Quản lý thông tin giáo viên");
        addTab(tabbedPane, "  Chi Nhánh  ", new BranchPanel(branchService), "Quản lý các chi nhánh của trung tâm");
        addTab(tabbedPane, "  Phòng học  ", new RoomPanel(roomService, branchService), "Quản lý phòng học");
        addTab(tabbedPane, "  Khóa học  ", new CoursePanel(courseService), "Quản lý các khóa học");
        addTab(tabbedPane, "  Lớp học  ",
                new ClassPanel(classService, courseService, teacherService, roomService, branchService),
                "Quản lý các lớp học");
        addTab(tabbedPane, "  Thi Xếp Lớp  ",
                new PlacementTestPanel(placementTestService, studentService),
                "Quản lý kết quả thi đầu vào");
        addTab(tabbedPane, "  Ghi danh  ",
                new EnrollmentPanel(enrollmentService, studentService, classService),
                "Ghi danh học viên vào các lớp học");
        addTab(tabbedPane, "  Chứng Chỉ  ",
                new CertificatePanel(certificateService, studentService, classService),
                "Quản lý và cấp phát chứng chỉ cho học viên");
        addTab(tabbedPane, "  Tài chính  ",
                new FinancePanel(invoiceService, paymentService, refundService, promotionService),
                "Quản lý hóa đơn và thanh toán học phí");
        addTab(tabbedPane, "  Khuyến mãi  ", new PromotionPanel(promotionService),
                "Quản lý các chương trình khuyến mãi");
        addTab(tabbedPane, "  Lịch hoạt động  ", new CenterSchedulePanel(scheduleService),
                "Xem lịch hoạt động tổng thể của trung tâm");
        addTab(tabbedPane, "  Thông Báo  ",
                new NotificationPanel(notificationService, currentUser),
                "Gửi và quản lý các thông báo");
    }

    /**
     * Phương thức tiện ích để thêm tab vào `JTabbedPane`.
     */
    private void addTab(JTabbedPane tabbedPane, String title, java.awt.Component component, String tooltip) {
        tabbedPane.addTab(title, component);
        tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, tooltip);
    }
}
