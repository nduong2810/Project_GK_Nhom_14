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

import javax.swing.*;

/**
 * OCP: MenuBuilder cho Staff — thêm/sửa tab chỉ cần sửa class này.
 */
public class StaffMenuBuilder implements MenuBuilder {

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

    public StaffMenuBuilder(StudentService studentService, TeacherService teacherService,
            BranchService branchService, RoomService roomService,
            CourseService courseService, ClassService classService,
            EnrollmentService enrollmentService, InvoiceService invoiceService,
            PaymentService paymentService, RefundService refundService,
            PromotionService promotionService, ScheduleService scheduleService,
            NotificationService notificationService) {
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
    }

    @Override
    public boolean supports(UserAccount.Role role) {
        return role == UserAccount.Role.Staff;
    }

    @Override
    public void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser) {
        addTab(tabbedPane, "  Học viên  ", new StudentPanel(studentService), "Thêm/Sửa/Xóa Học viên");
        addTab(tabbedPane, "  Giáo viên  ", new TeacherPanel(teacherService), "Thêm/Sửa/Xóa Giáo viên");
        addTab(tabbedPane, "  Chi Nhánh  ", new BranchPanel(branchService), "Quản lý chi nhánh trung tâm");
        addTab(tabbedPane, "  Phòng học  ", new RoomPanel(roomService, branchService), "Thêm/Sửa/Xóa Phòng học");
        addTab(tabbedPane, "  Khóa học  ", new CoursePanel(courseService), "Thêm/Sửa/Xóa Khóa học");
        addTab(tabbedPane, "  Lớp học  ",
                new ClassPanel(classService, courseService, teacherService, roomService, branchService),
                "Mở/Sửa/Xóa Lớp học");
        addTab(tabbedPane, "  Ghi danh  ",
                new EnrollmentPanel(enrollmentService, studentService, classService),
                "Ghi danh học viên vào lớp học");
        addTab(tabbedPane, "  Tài chính  ",
                new FinancePanel(invoiceService, paymentService, refundService, promotionService),
                "Hóa đơn / Thanh toán học phí");
        addTab(tabbedPane, "  Khuyến mãi  ", new PromotionPanel(promotionService),
                "Quản lý chương trình khuyến mãi");
        addTab(tabbedPane, "  Lịch hoạt động  ", new CenterSchedulePanel(scheduleService),
                "Xem lịch hoạt động chung của trung tâm");
        addTab(tabbedPane, "  Thông Báo  ",
                new NotificationPanel(notificationService, currentUser),
                "Quản lý thông báo cho học viên, giáo viên");
    }

    private void addTab(JTabbedPane tabbedPane, String title, java.awt.Component component, String tooltip) {
        tabbedPane.addTab(title, component);
        tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, tooltip);
    }
}
