package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.repo.UserAccountRepository;

import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai của UserAccountRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng UserAccount.
 */
public class JpaUserAccountRepository implements UserAccountRepository {

    /**
     * {@inheritDoc}
     * Sử dụng `LEFT JOIN FETCH` để tải sẵn thông tin liên quan (staff, teacher, student)
     * ngay trong một câu truy vấn, giúp tối ưu và tránh lỗi lazy loading.
     */
    @Override
    public UserAccount findByUsername(EntityManager em, String username) {
        try {
            TypedQuery<UserAccount> query = em.createQuery(
                    "SELECT u FROM UserAccount u " +
                            "LEFT JOIN FETCH u.staff " +
                            "LEFT JOIN FETCH u.teacher " +

                            "LEFT JOIN FETCH u.student " +
                            "WHERE u.username = :username",
                    UserAccount.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            // Trả về null nếu không tìm thấy tài khoản nào với username tương ứng.
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * Sử dụng `DISTINCT` và `LEFT JOIN FETCH` để lấy danh sách duy nhất các tài khoản
     * và tải sẵn các thực thể liên quan.
     */
    @Override
    public List<UserAccount> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT DISTINCT u FROM UserAccount u " +
                        "LEFT JOIN FETCH u.staff " +
                        "LEFT JOIN FETCH u.teacher " +
                        "LEFT JOIN FETCH u.student",
                UserAccount.class).getResultList();
    }

    /**
     * {@inheritDoc}
     * Tương tự `findByUsername`, sử dụng `LEFT JOIN FETCH` để đảm bảo tính nhất quán
     * khi tải một tài khoản duy nhất.
     */
    @Override
    public Optional<UserAccount> findById(EntityManager em, Long id) {
        TypedQuery<UserAccount> query = em.createQuery(
                "SELECT u FROM UserAccount u " +
                        "LEFT JOIN FETCH u.staff " +
                        "LEFT JOIN FETCH u.teacher " +
                        "LEFT JOIN FETCH u.student " +
                        "WHERE u.userId = :id",
                UserAccount.class);
        query.setParameter("id", id);
        // Sử dụng getResultStream().findFirst() để trả về Optional, an toàn hơn getSingleResult().
        return query.getResultStream().findFirst();
    }

    /**
     * {@inheritDoc}
     * Kiểm tra ID để quyết định `persist` (tạo mới) hay `merge` (cập nhật).
     */
    @Override
    public UserAccount save(EntityManager em, UserAccount userAccount) {
        if (userAccount.getUserId() == null) {
            em.persist(userAccount);
            return userAccount;
        } else {
            return em.merge(userAccount);
        }
    }

    /**
     * {@inheritDoc}
     * Tìm đối tượng trước khi xóa để đảm bảo an toàn.
     */
    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }
}
