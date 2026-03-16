package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.repo.ClassRepository;
import java.util.List;

/**
 * Lớp triển khai của ClassRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng ClassEntity.
 */
public class JpaClassRepository implements ClassRepository {

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL.
     */
    @Override
    public List<ClassEntity> findAll(EntityManager em) {
        // Sử dụng LEFT JOIN FETCH để tải sẵn (eagerly fetch) dữ liệu của các thực thể liên quan
        // như Course, Teacher, Room, và Branch trong cùng một câu truy vấn.
        // Việc này giúp tránh lỗi "LazyInitializationException" khi truy cập các thuộc tính này
        // sau khi EntityManager đã đóng (ví dụ: khi hiển thị dữ liệu lên giao diện JTable).
        // Đây là một kỹ thuật tối ưu hóa để giải quyết vấn đề N+1 query.
        String jpql = "SELECT c FROM ClassEntity c " +
                "LEFT JOIN FETCH c.course " +
                "LEFT JOIN FETCH c.teacher " +
                "LEFT JOIN FETCH c.room " +
                "LEFT JOIN FETCH c.branch " +
                "ORDER BY c.startDate DESC";
        return em.createQuery(jpql, ClassEntity.class).getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `find` của EntityManager.
     */
    @Override
    public ClassEntity findById(EntityManager em, Long id) {
        return em.find(ClassEntity.class, id);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `persist` của EntityManager.
     */
    @Override
    public void save(EntityManager em, ClassEntity classEntity) {
        em.persist(classEntity);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `merge` của EntityManager.
     */
    @Override
    public void update(EntityManager em, ClassEntity classEntity) {
        em.merge(classEntity);
    }

    /**
     * {@inheritDoc}
     * Tìm đối tượng trước khi xóa bằng phương thức `remove` của EntityManager.
     */
    @Override
    public void delete(EntityManager em, Long id) {
        ClassEntity c = em.find(ClassEntity.class, id);
        if (c != null) {
            em.remove(c);
        }
    }
}
