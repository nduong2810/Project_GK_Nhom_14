package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.CourseRepository;

import java.util.List;

public class JpaCourseRepository implements CourseRepository {

    @Override
    public List<Course> findAll(EntityManager em) {
        return em.createQuery("SELECT c FROM Course c ORDER BY c.courseName", Course.class)
                .getResultList();
    }

    @Override
    public Course findById(EntityManager em, Long id) {
        return em.find(Course.class, id);
    }

    @Override
    public void save(EntityManager em, Course course) {
        em.persist(course);
    }

    @Override
    public void update(EntityManager em, Course course) {
        em.merge(course);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Course c = em.find(Course.class, id);
        if (c != null) {
            em.remove(c);
        }
    }

    @Override
    public List<Course> findActiveCourses(EntityManager em) {
        // Điểm mấu chốt: Truyền Course.Status.Active vào thay vì chỉ Status.Active
        return em.createQuery("SELECT c FROM Course c WHERE c.status = :status ORDER BY c.courseName", Course.class)
                .setParameter("status", Course.Status.Active)
                .getResultList();
    }
}