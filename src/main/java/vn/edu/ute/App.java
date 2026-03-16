package vn.edu.ute;

import vn.edu.ute.controller.LoginController;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.jpa.*;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.LoginView;
import vn.edu.ute.ui.MainFrame;
import vn.edu.ute.ui.UI;

import javax.swing.*;

/**
 * Lớp App là điểm khởi đầu của ứng dụng.
 * Lớp này chịu trách nhiệm khởi tạo các thành phần cốt lõi của hệ thống
 * như các service, repository, và giao diện người dùng chính.
 */
public class App {
    /**
     * Phương thức main, điểm vào của chương trình.
     * @param args Tham số dòng lệnh (không được sử dụng).
     */
    public static void main(String[] args) {
        // 1. Cài đặt giao diện Look & Feel giống hệ điều hành
        // Điều này giúp giao diện ứng dụng trông quen thuộc với người dùng trên các hệ điều hành khác nhau.
        UI.initLookAndFeel();

        // 2. Khởi tạo Quản lý Giao dịch (Transaction Manager)
        // TransactionManager quản lý các giao dịch với cơ sở dữ liệu, đảm bảo tính toàn vẹn dữ liệu.
        TransactionManager tx = new TransactionManager();

        // 3. Khởi tạo các Repositories (Tầng truy xuất dữ liệu)
        // Các repository cung cấp một giao diện để truy cập và quản lý dữ liệu trong cơ sở dữ liệu.
        // Ở đây, chúng ta sử dụng triển khai JPA (Java Persistence API) cho các repository.
        RoomRepository roomRepo = new JpaRoomRepository();
        CourseRepository courseRepo = new JpaCourseRepository();
        InvoiceRepository invoiceRepo = new JpaInvoiceRepository();
        PaymentRepository paymentRepo = new JpaPaymentRepository();
        EnrollmentRepository enrollmentRepo = new JpaEnrollmentRepository();
        UserAccountRepository userAccountRepo = new JpaUserAccountRepository();
        StaffRepository staffRepo = new JpaStaffRepository();
        ScheduleRepository scheduleRepo = new JpaScheduleRepository();
        AttendanceRepository attendanceRepo = new JpaAttendanceRepository();
        TeacherRepository teacherRepo = new JpaTeacherRepository();
        StudentRepository studentRepo = new JpaStudentRepository();
        ClassRepository classRepo = new JpaClassRepository();
        ResultRepository resultRepo = new JpaResultRepository();
        PromotionRepository promotionRepo = new JpaPromotionRepository();
        BranchRepository branchRepo = new JpaBranchRepository();
        NotificationRepository notificationRepo = new JpaNotificationRepository();
        PlacementTestRepository placementTestRepo = new JpaPlacementTestRepository();
        CertificateRepository certificateRepo = new JpaCertificateRepository();

        // 4. Khởi tạo các Services và tiêm Repositories + TX vào (Tầng nghiệp vụ)
        // Các service chứa logic nghiệp vụ của ứng dụng và sử dụng các repository để tương tác với dữ liệu.
        // Việc "tiêm" (inject) repository và transaction manager vào service giúp tách biệt các tầng.
        RoomService roomService = new RoomService(roomRepo, tx);
        CourseService courseService = new CourseService(courseRepo, tx);
        StaffService staffService = new StaffService(staffRepo, tx);
        ScheduleService scheduleService = new ScheduleService(scheduleRepo, tx);
        AttendanceService attendanceService = new AttendanceService(attendanceRepo, tx);
        TeacherService teacherService = new TeacherService(teacherRepo, tx);
        StudentService studentService = new StudentService(studentRepo, tx);
        ClassService classService = new ClassService(classRepo, tx);
        UserAccountService userAccountService = new UserAccountService(userAccountRepo, tx);
        EnrollmentService enrollmentService = new EnrollmentService(enrollmentRepo, tx);
        PromotionService promotionService = new PromotionService(promotionRepo, tx);
        BranchService branchService = new BranchService(branchRepo, tx);
        NotificationService notificationService = new NotificationService(notificationRepo, tx);
        PlacementTestService placementTestService = new PlacementTestService(placementTestRepo, tx);
        CertificateService certificateService = new CertificateService(certificateRepo, tx);


        // SRP (Single Responsibility Principle): FinanceService được tách thành 3 service riêng biệt
        // để mỗi service chỉ chịu trách nhiệm cho một phần của nghiệp vụ tài chính.
        InvoiceService invoiceService = new InvoiceService(invoiceRepo, enrollmentRepo, promotionRepo, paymentRepo,
                promotionService, tx);
        PaymentService paymentService = new PaymentService(paymentRepo, invoiceRepo, enrollmentRepo, tx);
        RefundService refundService = new RefundService(invoiceRepo, paymentRepo, tx);

        // SRP: ResultService được tách thành 2 service riêng biệt
        // để quản lý việc nhập điểm và xem điểm của sinh viên.
        GradeEntryService gradeEntryService = new GradeEntryService(resultRepo, tx);
        StudentGradeService studentGradeService = new StudentGradeService(resultRepo, tx);

        // 5. Khởi chạy MainFrame trên luồng sự kiện của Swing
        // SwingUtilities.invokeLater đảm bảo rằng mã giao diện người dùng được thực thi trên Event Dispatch Thread (EDT),
        // điều này là cần thiết để tránh các vấn đề về luồng trong Swing.
        SwingUtilities.invokeLater(() -> {
            // Khởi tạo giao diện đăng nhập
            LoginView loginView = new LoginView();
            // Khởi tạo cửa sổ chính của ứng dụng và truyền các service cần thiết
            MainFrame mainFrame = new MainFrame(roomService, courseService, classService, teacherService,
                    studentService, enrollmentService,
                    invoiceService, paymentService, refundService,
                    scheduleService, attendanceService, staffService, userAccountService,
                    gradeEntryService, studentGradeService,
                    promotionService, branchService, notificationService, placementTestService, certificateService,
                    loginView);
            // DIP (Dependency Inversion Principle): LoginController sử dụng UserAccountService (một abstraction)
            // thay vì UserAccountRepository (một implementation cụ thể).
            new LoginController(loginView, userAccountService, mainFrame);
            // Hiển thị cửa sổ đăng nhập
            loginView.setVisible(true);
        });
    }
}
