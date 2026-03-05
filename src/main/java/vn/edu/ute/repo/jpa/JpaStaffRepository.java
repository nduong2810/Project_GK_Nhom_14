package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.repo.StaffRepository;

import java.util.List;
import java.util.Optional;

public class JpaStaffRepository implements StaffRepository {

    @Override
    public List<Staff> findAll(EntityManager em) {
        return em.createQuery("SELECT s FROM Staff s", Staff.class).getResultList();
    }

    @Override
    public Optional<Staff> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Staff.class, id));
    }

    @Override
    public Staff save(EntityManager em, Staff staff) {
        if (staff.getStaffId() == null) {
            em.persist(staff);
            return staff;
        } else {
            return em.merge(staff);
        }
    }

    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }
}