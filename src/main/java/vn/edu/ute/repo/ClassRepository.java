package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.ClassEntity;
import java.util.List;

public interface ClassRepository {
    List<ClassEntity> findAll(EntityManager em) throws Exception;
    ClassEntity findById(EntityManager em, Long id) throws Exception;
    void save(EntityManager em, ClassEntity classEntity) throws Exception;
    void update(EntityManager em, ClassEntity classEntity) throws Exception;
    void delete(EntityManager em, Long id) throws Exception;
}