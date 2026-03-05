package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.repo.ClassRepository;
import java.util.List;

public class JpaClassRepository implements ClassRepository {

    @Override
    public List<ClassEntity> findAll(EntityManager em) {
        // Dùng LEFT JOIN FETCH để nạp sẵn dữ liệu của Course, Teacher, Room.
        // Việc này giúp tránh lỗi "LazyInitializationException" khi hiển thị lên giao diện JTable.
        String jpql = "SELECT c FROM ClassEntity c " +
                "LEFT JOIN FETCH c.course " +
                "LEFT JOIN FETCH c.teacher " +
                "LEFT JOIN FETCH c.room " +
                "ORDER BY c.startDate DESC";
        return em.createQuery(jpql, ClassEntity.class).getResultList();
    }

    @Override
    public ClassEntity findById(EntityManager em, Long id) {
        return em.find(ClassEntity.class, id);
    }

    @Override
    public void save(EntityManager em, ClassEntity classEntity) {
        em.persist(classEntity);
    }

    @Override
    public void update(EntityManager em, ClassEntity classEntity) {
        em.merge(classEntity);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        ClassEntity c = em.find(ClassEntity.class, id);
        if (c != null) {
            em.remove(c);
        }
    }
}