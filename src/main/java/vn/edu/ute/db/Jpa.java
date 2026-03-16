package vn.edu.ute.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Lớp Jpa quản lý việc tạo và cung cấp các đối tượng EntityManager.
 * Đây là một lớp Singleton để đảm bảo rằng chỉ có một EntityManagerFactory được tạo ra cho toàn bộ ứng dụng.
 *
 * DIP (Dependency Inversion Principle): Lớp Jpa triển khai giao diện EntityManagerProvider.
 *      Điều này cho phép TransactionManager không phụ thuộc trực tiếp vào lớp Jpa cụ thể này,
 *      mà thay vào đó phụ thuộc vào một abstraction (EntityManagerProvider).
 *
 * SRP (Single Responsibility Principle): Lớp này chỉ có một trách nhiệm duy nhất là cung cấp
 *      EntityManager từ persistence unit đã được định cấu hình.
 */
public final class Jpa implements EntityManagerProvider {
    // EntityManagerFactory (EMF) là một đối tượng tốn kém để tạo, vì vậy nó được tạo một lần và tái sử dụng.
    // "language-center-pu" là tên của persistence unit được định nghĩa trong file persistence.xml.
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("language-center-pu");

    // Thể hiện duy nhất của lớp Jpa (Singleton pattern).
    private static final Jpa INSTANCE = new Jpa();

    // Hàm khởi tạo private để ngăn việc tạo đối tượng từ bên ngoài.
    private Jpa() {}

    /**
     * Trả về thể hiện duy nhất của lớp Jpa.
     * @return Thể hiện của Jpa.
     */
    public static Jpa getInstance() {
        return INSTANCE;
    }

    /**
     * Tạo và trả về một EntityManager mới.
     * Phương thức này được định nghĩa trong giao diện EntityManagerProvider.
     * @return Một đối tượng EntityManager mới.
     */
    @Override
    public EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Phương thức tĩnh tương thích ngược (Backward-compatible).
     * Giữ lại để không phá vỡ code hiện tại có thể đang sử dụng Jpa.em().
     * @return Một đối tượng EntityManager mới.
     */
    public static EntityManager em() {
        return EMF.createEntityManager();
    }

    /**
     * Đóng EntityManagerFactory khi ứng dụng kết thúc.
     * Việc này giải phóng tất cả các tài nguyên mà EMF đang nắm giữ.
     */
    public static void shutdown() {
        EMF.close();
    }
}
