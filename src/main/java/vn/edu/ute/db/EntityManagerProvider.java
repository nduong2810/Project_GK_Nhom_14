package vn.edu.ute.db;

import jakarta.persistence.EntityManager;

/**
 * Giao diện này định nghĩa một hợp đồng cho việc cung cấp các đối tượng EntityManager.
 *
 * DIP (Dependency Inversion Principle): Đây là một abstraction cho việc cung cấp EntityManager.
 * TransactionManager phụ thuộc vào interface này thay vì lớp Jpa cụ thể.
 * Điều này giúp dễ dàng thay thế hoặc mock (giả lập) trong quá trình testing,
 * làm cho mã nguồn trở nên linh hoạt và dễ kiểm thử hơn.
 */
public interface EntityManagerProvider {
    /**
     * Tạo và trả về một đối tượng EntityManager mới.
     *
     * @return Một thể hiện mới của EntityManager.
     */
    EntityManager createEntityManager();
}
