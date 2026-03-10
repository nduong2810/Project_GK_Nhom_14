package vn.edu.ute.repo.jpa;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;
import vn.edu.ute.repo.BranchRepository;

import java.util.List;

public class JpaBranchRepository implements BranchRepository {

    @Override
    public List<Branch> findAll(EntityManager em) {
        return em.createQuery("SELECT b FROM Branch b ORDER BY b.branchName", Branch.class)
                .getResultList();
    }

    @Override
    public Branch findById(EntityManager em, Long id) {
        return em.find(Branch.class, id);
    }

    @Override
    public void save(EntityManager em, Branch branch) {
        em.persist(branch);
    }

    @Override
    public void update(EntityManager em, Branch branch) {
        em.merge(branch);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        Branch b = em.find(Branch.class, id);
        if (b != null) {
            em.remove(b);
        }
    }

    @Override
    public List<Branch> findActiveBranches(EntityManager em) {
        return em.createQuery("SELECT b FROM Branch b WHERE b.status = :status ORDER BY b.branchName", Branch.class)
                .setParameter("status", Branch.Status.Active)
                .getResultList();
    }
}
