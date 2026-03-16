package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.StudentRepository;
import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai của StudentRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Student.
 */
public class JpaStudentRepository implements StudentRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Student> findAll(EntityManager em) {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Student> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Student.class, id));
    }

    /**
     * {@inheritDoc}
     * Sử dụng toán tử LIKE để tìm kiếm tên một cách tương đối.
     */
    @Override
    public List<Student> findByNameContaining(EntityManager em, String name) {
        return em.createQuery("SELECT s FROM Student s WHERE s.fullName LIKE :name", Student.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Kiểm tra ID để quyết định `persist` (tạo mới) hay `merge` (cập nhật).
     */
    @Override
    public Student save(EntityManager em, Student student) {
        if (student.getStudentId() == null) {
            em.persist(student);
            return student;
        } else {
            return em.merge(student);
        }
    }

    /**
     * {@inheritDoc}
     * Tìm đối tượng trước khi xóa để đảm bảo an toàn.
     */
    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }

    /**
     * {@inheritDoc}
     * Lọc các học viên có trạng thái 'Active'.
     */
    @Override
    public List<Student> findActiveStudents(EntityManager em) {
        return em.createQuery("SELECT s FROM Student s WHERE s.status = :status ORDER BY s.fullName", Student.class)
                .setParameter("status", Student.Status.Active)
                .getResultList();
    }
}
