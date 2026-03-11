package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.repo.PlacementTestRepository;

import java.util.List;

public class PlacementTestService {
    private final PlacementTestRepository testRepo;
    private final TransactionManager tx;

    public PlacementTestService(PlacementTestRepository testRepo, TransactionManager tx) {
        this.testRepo = testRepo;
        this.tx = tx;
    }

    public void createTest(PlacementTest test) throws Exception {
        tx.runInTransaction(em -> {
            testRepo.save(em, test);
            return null;
        });
    }

    public void updateTest(PlacementTest test) throws Exception {
        tx.runInTransaction(em -> {
            testRepo.update(em, test);
            return null;
        });
    }

    public void deleteTest(Long id) throws Exception {
        tx.runInTransaction(em -> {
            testRepo.delete(em, id);
            return null;
        });
    }

    public List<PlacementTest> getAllTests() throws Exception {
        return tx.runInTransaction(em -> testRepo.findAll(em));
    }
}