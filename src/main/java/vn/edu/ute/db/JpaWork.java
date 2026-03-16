package vn.edu.ute.db;

import jakarta.persistence.EntityManager;

/**
 * Một functional interface đại diện cho một đơn vị công việc (unit of work) sẽ được thực thi
 * trong một ngữ cảnh giao dịch JPA.
 *
 * @param <T> Kiểu dữ liệu trả về của công việc.
 */
@FunctionalInterface
public interface JpaWork<T> {
    /**
     * Thực thi công việc với một EntityManager được cung cấp.
     *
     * @param em EntityManager được quản lý bởi giao dịch.
     * @return Kết quả của công việc.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình thực thi.
     */
    T execute(EntityManager em) throws Exception;
}
