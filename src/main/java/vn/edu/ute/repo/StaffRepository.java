package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Staff;
import java.util.List;
import java.util.Optional;

public interface StaffRepository {
    List<Staff> findAll(EntityManager em);
    Optional<Staff> findById(EntityManager em, Long id);
    Staff save(EntityManager em, Staff staff);
    void deleteById(EntityManager em, Long id);
}