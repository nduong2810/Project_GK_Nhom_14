package vn.edu.ute.ui.role;

import vn.edu.ute.model.UserAccount;

import javax.swing.*;

/**
 * Giao diện `MenuBuilder` định nghĩa một hợp đồng (contract) cho việc xây dựng menu động
 * dựa trên vai trò của người dùng. Đây là một phần của mẫu thiết kế Strategy.
 *
 * OCP (Open/Closed Principle): Nguyên tắc này được áp dụng ở đây.
 * Khi cần hỗ trợ một vai trò người dùng mới, chúng ta chỉ cần tạo một lớp mới triển khai
 * giao diện `MenuBuilder` mà không cần phải sửa đổi mã nguồn của `MainFrame` hay các builder khác.
 * `MainFrame` sẽ "đóng" với việc sửa đổi nhưng "mở" cho việc mở rộng.
 */
public interface MenuBuilder {
    /**
     * Kiểm tra xem builder này có hỗ trợ (phụ trách) việc xây dựng menu
     * cho một vai trò (`UserAccount.Role`) cụ thể hay không.
     *
     * @param role Vai trò của người dùng cần kiểm tra.
     * @return `true` nếu builder này có thể xây dựng menu cho vai trò đó, ngược lại `false`.
     */
    boolean supports(UserAccount.Role role);

    /**
     * Xây dựng và thêm các tab (các panel chức năng) vào `JTabbedPane`
     * tương ứng với vai trò của người dùng.
     *
     * @param tabbedPane `JTabbedPane` chính của ứng dụng để thêm các tab vào.
     * @param currentUser Đối tượng `UserAccount` của người dùng hiện tại, chứa thông tin vai trò và ID.
     */
    void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser);
}
