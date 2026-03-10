package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Branch;

import java.util.List;

public interface BranchRepository {
    List<Branch> findAll(EntityManager em) throws Exception;

    Branch findById(EntityManager em, Long id) throws Exception;

    void save(EntityManager em, Branch branch) throws Exception;

    void update(EntityManager em, Branch branch) throws Exception;

    void delete(EntityManager em, Long id) throws Exception;

    List<Branch> findActiveBranches(EntityManager em) throws Exception;
}
