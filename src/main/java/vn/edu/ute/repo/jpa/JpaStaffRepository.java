package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.repo.StaffRepository;

import java.util.List;
import java.util.Optional;

/**
 * Lớp triển khai của StaffRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Staff.
 */
public class JpaStaffRepository implements StaffRepository {

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL để lấy tất cả nhân viên.
     */
    @Override
    public List<Staff> findAll(EntityManager em) {
        return em.createQuery("SELECT s FROM Staff s", Staff.class).getResultList();
    }

    /**
     * {@inheritDoc}
     * Sử dụng `em.find` và bọc kết quả trong `Optional` để xử lý trường hợp không tìm thấy.
     */
    @Override
    public Optional<Staff> findById(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Staff.class, id));
    }

    /**
     * {@inheritDoc}
     * Kiểm tra nếu `staffId` là null thì dùng `persist` (tạo mới),
     * ngược lại dùng `merge` (cập nhật).
     */
    @Override
    public Staff save(EntityManager em, Staff staff) {
        if (staff.getStaffId() == null) {
            em.persist(staff);
            return staff;
        } else {
            return em.merge(staff);
        }
    }

    /**
     * {@inheritDoc}
     * Sử dụng `findById` để tìm nhân viên, sau đó dùng `ifPresent` để gọi `em.remove` nếu tìm thấy.
     * Đây là một cách viết ngắn gọn và an toàn để xóa.
     */
    @Override
    public void deleteById(EntityManager em, Long id) {
        findById(em, id).ifPresent(em::remove);
    }
}
