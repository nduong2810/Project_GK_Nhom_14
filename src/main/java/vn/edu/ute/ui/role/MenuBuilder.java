package vn.edu.ute.ui.role;

import vn.edu.ute.model.UserAccount;

import javax.swing.*;

/**
 * OCP: Interface Strategy cho việc xây dựng menu theo từng role.
 * Thêm role mới chỉ cần tạo class mới implement interface này,
 * KHÔNG cần sửa MainFrame.showMenuByUserRole().
 */
public interface MenuBuilder {
    /**
     * Kiểm tra xem builder này có phụ trách role của user không.
     */
    boolean supports(UserAccount.Role role);

    /**
     * Xây dựng và thêm các tab vào tabbedPane cho role tương ứng.
     */
    void buildMenu(JTabbedPane tabbedPane, UserAccount currentUser);
}
