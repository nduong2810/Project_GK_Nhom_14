package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.repo.ClassRepository;
import java.util.List;

public class ClassService {
    private final ClassRepository classRepo;
    private final TransactionManager tx;

    public ClassService(ClassRepository classRepo, TransactionManager tx) {
        this.classRepo = classRepo;
        this.tx = tx;
    }

    public void createClass(ClassEntity cls) throws Exception {
        tx.runInTransaction(em -> {
            classRepo.save(em, cls);
            return null;
        });
    }

    public void updateClass(ClassEntity cls) throws Exception {
        tx.runInTransaction(em -> {
            ClassEntity existing = classRepo.findById(em, cls.getClassId());
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với ID: " + cls.getClassId());
            }
            classRepo.update(em, cls);
            return null;
        });
    }

    public void deleteClass(Long classId) throws Exception {
        tx.runInTransaction(em -> {
            ClassEntity existing = classRepo.findById(em, classId);
            if (existing == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với ID: " + classId);
            }
            classRepo.delete(em, classId);
            return null;
        });
    }

    public List<ClassEntity> getAllClasses() throws Exception {
        return tx.runInTransaction(em -> classRepo.findAll(em));
    }

    public ClassEntity getClassById(Long id) throws Exception {
        return tx.runInTransaction(em -> classRepo.findById(em, id));
    }
}