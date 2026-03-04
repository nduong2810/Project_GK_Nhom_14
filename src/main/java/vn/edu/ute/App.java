package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.CourseRepository;
import vn.edu.ute.repo.RoomRepository;
import vn.edu.ute.repo.jpa.JpaCourseRepository;
import vn.edu.ute.repo.jpa.JpaRoomRepository;
import vn.edu.ute.service.CourseService;
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

        // 4. Khởi tạo các Services và tiêm Repositories + TX vào (Tầng nghiệp vụ)
        RoomService roomService = new RoomService(roomRepo, tx);
        CourseService courseService = new CourseService(courseRepo, tx);

        // 5. Khởi chạy MainFrame trên luồng sự kiện của Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame(roomService, courseService);
            f.setVisible(true);
        });
    }
}