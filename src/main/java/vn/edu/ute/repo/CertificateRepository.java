package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Certificate;
import java.util.List;

public interface CertificateRepository {
    void save(EntityManager em, Certificate certificate);
    void update(EntityManager em, Certificate certificate);
    void delete(EntityManager em, Long id);
    Certificate findById(EntityManager em, Long id);
    List<Certificate> findAll(EntityManager em);
    List<Certificate> findByStudentId(EntityManager em, Long studentId);
}