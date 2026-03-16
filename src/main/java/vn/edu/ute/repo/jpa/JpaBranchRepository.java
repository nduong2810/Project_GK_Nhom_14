package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepository;

import java.util.List;

/**
 * Lớp triển khai của BranchRepository sử dụng JPA.
 * Cung cấp logic cụ thể để tương tác với cơ sở dữ liệu cho các đối tượng Branch.
 */
public class JpaBranchRepository implements BranchRepository {

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL, sắp xếp kết quả theo tên chi nhánh.
     */
    @Override
    public List<Branch> findAll(EntityManager em) {
        return em.createQuery("SELECT b FROM Branch b ORDER BY b.branchName", Branch.class)
                .getResultList();
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `find` của EntityManager.
     */
    @Override
    public Branch findById(EntityManager em, Long id) {
        return em.find(Branch.class, id);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `persist` của EntityManager.
     */
    @Override
    public void save(EntityManager em, Branch branch) {
        em.persist(branch);
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng phương thức `merge` của EntityManager.
     */
    @Override
    public void update(EntityManager em, Branch branch) {
        em.merge(branch);
    }

    /**
     * {@inheritDoc}
     * Tìm đối tượng trước khi xóa bằng phương thức `remove` của EntityManager.
     */
    @Override
    public void delete(EntityManager em, Long id) {
        Branch b = em.find(Branch.class, id);
        if (b != null) {
            em.remove(b);
        }
    }

    /**
     * {@inheritDoc}
     * Triển khai bằng JPQL, lọc các chi nhánh có trạng thái 'Active'.
     */
    @Override
    public List<Branch> findActiveBranches(EntityManager em) {
        return em.createQuery("SELECT b FROM Branch b WHERE b.status = :status ORDER BY b.branchName", Branch.class)
                .setParameter("status", Branch.Status.Active)
                .getResultList();
    }
}
