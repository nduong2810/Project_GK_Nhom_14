package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.jpa.*;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.RoomService;
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

        // 4. Khởi tạo các Services và tiêm Repositories + TX vào (Tầng nghiệp vụ)
        RoomService roomService = new RoomService(roomRepo, tx);
        CourseService courseService = new CourseService(courseRepo, tx);
        FinanceService financeService = new FinanceService(invoiceRepo, paymentRepo, enrollmentRepo, tx);

        // 5. Khởi chạy MainFrame trên luồng sự kiện của Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame(roomService, courseService, financeService);
            f.setVisible(true);
        });
    }
}