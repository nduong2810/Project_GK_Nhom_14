package vn.edu.ute.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

/**
 * Lớp tiện ích UI, chịu trách nhiệm khởi tạo Giao diện & Cảm nhận (Look & Feel)
 * và cài đặt các thuộc tính giao diện người dùng chung cho toàn bộ ứng dụng.
 * Lớp này sử dụng thư viện FlatLaf để có một giao diện hiện đại và phẳng.
 */
public final class UI {
    // Constructor private để ngăn việc tạo instance của lớp tiện ích này.
    private UI() {
    }

    /**
     * Khởi tạo và áp dụng Look & Feel cho ứng dụng.
     * Phương thức này nên được gọi một lần khi ứng dụng khởi động.
     */
    public static void initLookAndFeel() {
        try {
            // Cài đặt theme FlatLaf Light. Đây là một theme sáng, sạch sẽ.
            FlatLightLaf.setup();

            // === Cài đặt các thuộc tính UI chung (Global UI defaults) ===

            // Đặt font chữ mặc định cho toàn bộ ứng dụng.
            UIManager.put("defaultFont", UITheme.FONT_BODY);

            // Tùy chỉnh cho các nút (Button)
            // Đặt độ cong của góc cho các nút.
            UIManager.put("Button.arc", UITheme.BORDER_RADIUS);

            // Tùy chỉnh cho các trường nhập liệu (TextField, PasswordField, ComboBox)
            // Đặt độ cong của góc cho các thành phần này để tạo sự đồng nhất.
            UIManager.put("TextField.arc", 6);
            UIManager.put("PasswordField.arc", 6);
            UIManager.put("ComboBox.arc", 6);

            // Tùy chỉnh cho JTabbedPane (Thanh tab)
            // Màu nền của tab đang được chọn.
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            // Màu khi di chuột qua tab.
            UIManager.put("TabbedPane.hoverColor", UITheme.PRIMARY_LIGHT);
            // Màu khi tab được focus.
            UIManager.put("TabbedPane.focusColor", UITheme.PRIMARY);
            // Màu của đường gạch chân dưới tab đang được chọn.
            UIManager.put("TabbedPane.underlineColor", UITheme.PRIMARY);
            // Chiều cao của mỗi tab.
            UIManager.put("TabbedPane.tabHeight", 36);

            // Tùy chỉnh cho JScrollBar (Thanh cuộn)
            // Độ rộng của thanh cuộn.
            UIManager.put("ScrollBar.width", 10);
            // Độ cong của con trượt (thumb) và rãnh (track) để tạo thanh cuộn hình viên thuốc.
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);

            // Tùy chỉnh cho JTable (Bảng)
            // Hiển thị các đường kẻ ngang.
            UIManager.put("Table.showHorizontalLines", true);
            // Ẩn các đường kẻ dọc để giao diện thoáng hơn.
            UIManager.put("Table.showVerticalLines", false);

            // Tùy chỉnh cho JOptionPane (Hộp thoại thông báo)
            // Đặt font cho nội dung và các nút trong hộp thoại.
            UIManager.put("OptionPane.messageFont", UITheme.FONT_BODY);
            UIManager.put("OptionPane.buttonFont", UITheme.FONT_BUTTON);

        } catch (Exception e) {
            // Nếu có lỗi khi cài đặt FlatLaf (ví dụ: thiếu thư viện),
            // thử chuyển về Look & Feel mặc định của hệ điều hành.
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Bỏ qua nếu cả việc cài đặt L&F hệ thống cũng thất bại.
                // Ứng dụng sẽ chạy với L&F mặc định của Java (Metal).
            }
        }
    }
}
