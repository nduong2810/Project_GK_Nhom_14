package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.PlacementTest;
import java.util.List;

public interface PlacementTestRepository {
    void save(EntityManager em, PlacementTest test);
    void update(EntityManager em, PlacementTest test);
    void delete(EntityManager em, Long id);
    PlacementTest findById(EntityManager em, Long id);
    List<PlacementTest> findAll(EntityManager em);
    List<PlacementTest> findByStudentId(EntityManager em, Long studentId);
}