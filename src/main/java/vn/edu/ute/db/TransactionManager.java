package vn.edu.ute.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * Lớp TransactionManager quản lý các giao dịch với cơ sở dữ liệu.
 * Nó đảm bảo rằng các thao tác được thực hiện một cách nguyên tử (atomic).
 *
 * DIP (Dependency Inversion Principle): TransactionManager phụ thuộc vào EntityManagerProvider (một abstraction)
 * thay vì Jpa (một concrete class). Điều này giúp dễ dàng inject một mock provider trong quá trình testing.
 *
 * SRP (Single Responsibility Principle): Lớp này chỉ chịu trách nhiệm quản lý vòng đời của một giao dịch
 * (bắt đầu, commit, rollback).
 */
public class TransactionManager {

    private final EntityManagerProvider emProvider;

    /**
     * Hàm khởi tạo nhận một EntityManagerProvider thông qua constructor injection.
     * @param emProvider Provider để tạo EntityManager.
     */
    public TransactionManager(EntityManagerProvider emProvider) {
        this.emProvider = emProvider;
    }

    /**
     * Hàm khởi tạo tương thích ngược (backward-compatible).
     * Sử dụng Jpa.getInstance() làm provider mặc định nếu không có provider nào được cung cấp.
     */
    public TransactionManager() {
        this(Jpa.getInstance());
    }

    /**
     * Thực thi một đơn vị công việc (JpaWork) trong một giao dịch cơ sở dữ liệu.
     *
     * @param <T> Kiểu dữ liệu trả về của công việc.
     * @param work Đơn vị công việc cần thực thi.
     * @return Kết quả của công việc.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình thực thi.
     */
    public <T> T runInTransaction(JpaWork<T> work) throws Exception {
        // Sử dụng try-with-resources để đảm bảo EntityManager luôn được đóng.
        try (EntityManager em = emProvider.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                // Bắt đầu giao dịch.
                tx.begin();
                // Thực thi công việc.
                T result = work.execute(em);
                // Nếu không có lỗi, commit giao dịch.
                tx.commit();
                return result;
            } catch (Exception ex) {
                // Nếu có lỗi xảy ra, rollback giao dịch nếu nó vẫn đang hoạt động.
                if (tx.isActive()) {
                    tx.rollback();
                }
                // Ném lại ngoại lệ để lớp gọi có thể xử lý.
                throw ex;
            }
        }
    }
}
