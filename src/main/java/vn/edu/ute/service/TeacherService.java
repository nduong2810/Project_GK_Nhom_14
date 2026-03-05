package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repo.TeacherRepository;
import java.util.List;
import java.util.Optional;

public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final TransactionManager transactionManager;

    public TeacherService(TeacherRepository teacherRepository, TransactionManager transactionManager) {
        this.teacherRepository = teacherRepository;
        this.transactionManager = transactionManager;
    }

    public List<Teacher> getAllTeachers() {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findAll(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Teacher> findTeacherById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Teacher> findTeacherByName(String name) {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findByNameContaining(em, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Teacher> getActiveTeachers() {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.findActiveTeachers(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Teacher saveTeacher(Teacher teacher) {
        try {
            return transactionManager.runInTransaction(em -> teacherRepository.save(em, teacher));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTeacher(Long id) {
        try {
            transactionManager.runInTransaction(em -> {
                teacherRepository.deleteById(em, id);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}