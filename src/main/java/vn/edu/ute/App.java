package vn.edu.ute;

import vn.edu.ute.controller.LoginController;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.jpa.*;
import vn.edu.ute.service.AttendanceService;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.ScheduleService;
import vn.edu.ute.service.*;
import vn.edu.ute.ui.LoginView;
import vn.edu.ute.ui.MainFrame;
import vn.edu.ute.ui.UI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // 1. Cài đặt giao diện Look & Feel giống hệ điều hành
        UI.initLookAndFeel();

        // 2. Khởi tạo Quản lý Giao dịch (Transaction Manager)
        TransactionManager tx = new TransactionManager();

        // 3. Khởi tạo các Repositories (Tầng truy xuất dữ liệu)
        RoomRepository roomRepo = new JpaRoomRepository();
        CourseRepository courseRepo = new JpaCourseRepository();
        InvoiceRepository invoiceRepo = new JpaInvoiceRepository();
        PaymentRepository paymentRepo = new JpaPaymentRepository();
        EnrollmentRepository enrollmentRepo = new JpaEnrollmentRepository();
        UserAccountRepository userAccountRepo = new JpaUserAccountRepository();
        ScheduleRepository scheduleRepo = new JpaScheduleRepository();
        AttendanceRepository attendanceRepo = new JpaAttendanceRepository();
        TeacherRepository teacherRepo = new JpaTeacherRepository();
        ClassRepository classRepo = new JpaClassRepository();

        // 4. Khởi tạo các Services và tiêm Repositories + TX vào (Tầng nghiệp vụ)
        RoomService roomService = new RoomService(roomRepo, tx);
        CourseService courseService = new CourseService(courseRepo, tx);
        FinanceService financeService = new FinanceService(invoiceRepo, paymentRepo, enrollmentRepo, tx);
        ScheduleService scheduleService = new ScheduleService(scheduleRepo, tx);
        AttendanceService attendanceService = new AttendanceService(attendanceRepo, tx);
        TeacherService teacherService = new TeacherService(teacherRepo, tx);
        ClassService classService = new ClassService(classRepo, tx);

        // 5. Khởi chạy MainFrame trên luồng sự kiện của Swing
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            MainFrame mainFrame = new MainFrame(roomService, courseService, classService, teacherService,
                    financeService, scheduleService, attendanceService, loginView);
            new LoginController(loginView, userAccountRepo, mainFrame);
            loginView.setVisible(true);
        });
    }
}