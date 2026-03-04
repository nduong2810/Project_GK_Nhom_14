package vn.edu.ute.ui;

import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.FinanceService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.ui.course.CoursePanel; // Import từ thư mục con
import vn.edu.ute.ui.finance.FinancePanel; // Import từ thư mục con
import vn.edu.ute.ui.room.RoomPanel; // Import từ thư mục con

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(RoomService roomService, CourseService courseService, FinanceService financeService) {
        super("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // Căn giữa màn hình

        // Tạo JTabbedPane để chứa các màn hình độc lập
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Ráp màn hình Quản lý Phòng học
        RoomPanel roomPanel = new RoomPanel(roomService);
        tabbedPane.addTab("Quản lý Phòng học", new ImageIcon(), roomPanel, "Thêm/Sửa/Xóa Phòng học");

        // 2. Ráp màn hình Quản lý Khóa học
        CoursePanel coursePanel = new CoursePanel(courseService);
        tabbedPane.addTab("Quản lý Khóa học", new ImageIcon(), coursePanel, "Thêm/Sửa/Xóa Khóa học");

        // 3. Ráp màn hình Quản lý Tài chính
        FinancePanel financePanel = new FinancePanel(financeService);
        tabbedPane.addTab("Quản lý Tài chính", new ImageIcon(), financePanel, "Hóa đơn / Thanh toán học phí");

        // Đặt tabbedPane làm nội dung chính của cửa sổ
        setContentPane(tabbedPane);
    }
}