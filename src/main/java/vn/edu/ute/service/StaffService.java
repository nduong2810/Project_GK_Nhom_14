package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.repo.StaffRepository;

import java.util.List;
import java.util.Optional;

public class StaffService {
    private final StaffRepository staffRepository;
    private final TransactionManager transactionManager;

    public StaffService(StaffRepository staffRepository, TransactionManager transactionManager) {
        this.staffRepository = staffRepository;
        this.transactionManager = transactionManager;
    }

    public List<Staff> getAllStaff() {
        try {
            return transactionManager.runInTransaction(em -> staffRepository.findAll(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Staff> findStaffById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> staffRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Staff saveStaff(Staff staff) {
        try {
            return transactionManager.runInTransaction(em -> staffRepository.save(em, staff));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStaff(Long id) {
        try {
            transactionManager.runInTransaction(em -> {
                staffRepository.deleteById(em, id);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}