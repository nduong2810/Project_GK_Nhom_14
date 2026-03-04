package vn.edu.ute.ui;

import vn.edu.ute.service.CourseService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.ui.course.CoursePanel; // Import từ thư mục con
import vn.edu.ute.ui.room.RoomPanel;     // Import từ thư mục con

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(RoomService roomService, CourseService courseService) {
        super("Hệ Thống Quản Lý Trung Tâm Ngoại Ngữ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // Căn giữa màn hình

        // Tạo JTabbedPane để chứa các màn hình độc lập
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Ráp màn hình Quản lý Phòng học
        RoomPanel roomPanel = new RoomPanel(roomService);
        tabbedPane.addTab("Quản lý Phòng học", new ImageIcon(), roomPanel, "Thêm/Sửa/Xóa Phòng học");

        // 2. Ráp màn hình Quản lý Khóa học (Đã bỏ comment)
        CoursePanel coursePanel = new CoursePanel(courseService);
        tabbedPane.addTab("Quản lý Khóa học", new ImageIcon(), coursePanel, "Thêm/Sửa/Xóa Khóa học");

        // Đặt tabbedPane làm nội dung chính của cửa sổ
        setContentPane(tabbedPane);
    }
}