package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepository;
import java.util.List;

public class TeacherService {
    private final TeacherRepository teacherRepo;
    private final TransactionManager tx;

    public TeacherService(TeacherRepository teacherRepo, TransactionManager tx) {
        this.teacherRepo = teacherRepo;
        this.tx = tx;
    }

    public List<Teacher> getActiveTeachers() throws Exception {
        return tx.runInTransaction(em -> teacherRepo.findActiveTeachers(em));
    }
}