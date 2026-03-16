package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.CourseRepository;

import java.util.List;

/**
 * Lớp triển khai của CourseRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Course.
 */
public class JpaCourseRepository implements CourseRepository {

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL, sắp xếp kết quả theo tên khóa học.
     */
    @Override
    public List<Course> findAll(EntityManager em) {
        return em.createQuery("SELECT c FROM Course c ORDER BY c.courseName", Course.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `find` của EntityManager.
     */
    @Override
    public Course findById(EntityManager em, Long id) {
        return em.find(Course.class, id);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `persist` của EntityManager.
     */
    @Override
    public void save(EntityManager em, Course course) {
        em.persist(course);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `merge` của EntityManager.
     */
    @Override
    public void update(EntityManager em, Course course) {
        em.merge(course);
    }

    /**
     * {@inheritDoc}
     * Tìm đối tượng trước khi xóa bằng phương thức `remove` của EntityManager.
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Course c = em.find(Course.class, id);
        if (c != null) {
            em.remove(c);
        }
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL, lọc các khóa học có trạng thái 'Active'.
     */
    @Override
    public List<Course> findActiveCourses(EntityManager em) {
        // Sử dụng Course.Status.Active để chỉ rõ enum Status thuộc lớp Course.
        return em.createQuery("SELECT c FROM Course c WHERE c.status = :status ORDER BY c.courseName", Course.class)
                .setParameter("status", Course.Status.Active)
                .getResultList();
    }
}
