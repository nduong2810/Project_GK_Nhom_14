package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.StudentRepository;
import java.util.List;
import java.util.Optional;

public class StudentService {
    private final StudentRepository studentRepository;
    private final TransactionManager transactionManager;

    public StudentService(StudentRepository studentRepository, TransactionManager transactionManager) {
        this.studentRepository = studentRepository;
        this.transactionManager = transactionManager;
    }

    public List<Student> getAllStudents() {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.findAll(em));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Student> findStudentById(Long id) {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.findById(em, id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Student> findStudentByName(String name) {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.findByNameContaining(em, name));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Student saveStudent(Student student) {
        try {
            return transactionManager.runInTransaction(em -> studentRepository.save(em, student));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStudent(Long id) {
        try {
            transactionManager.runInTransaction(em -> {
                studentRepository.deleteById(em, id);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}