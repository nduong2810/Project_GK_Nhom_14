package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.EnrollmentRepository;
import java.util.List;

public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepo;
    private final TransactionManager tx;

    public EnrollmentService(EnrollmentRepository enrollmentRepo, TransactionManager tx) {
        this.enrollmentRepo = enrollmentRepo;
        this.tx = tx;
    }

    public void createEnrollment(Enrollment enrollment) throws Exception {
        tx.runInTransaction(em -> { enrollmentRepo.save(em, enrollment); return null; });
    }

    public void updateEnrollment(Enrollment enrollment) throws Exception {
        tx.runInTransaction(em -> { enrollmentRepo.update(em, enrollment); return null; });
    }

    public void deleteEnrollment(Long id) throws Exception {
        tx.runInTransaction(em -> { enrollmentRepo.delete(em, id); return null; });
    }

    public List<Enrollment> getAllEnrollments() throws Exception {
        return tx.runInTransaction(em -> enrollmentRepo.findAll(em));
    }
}