package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.repo.CertificateRepository;

import java.util.List;

public class CertificateService {
    private final CertificateRepository certRepo;
    private final TransactionManager tx;

    public CertificateService(CertificateRepository certRepo, TransactionManager tx) {
        this.certRepo = certRepo;
        this.tx = tx;
    }

    public void createCertificate(Certificate certificate) throws Exception {
        tx.runInTransaction(em -> {
            certRepo.save(em, certificate);
            return null;
        });
    }

    public void updateCertificate(Certificate certificate) throws Exception {
        tx.runInTransaction(em -> {
            certRepo.update(em, certificate);
            return null;
        });
    }

    public void deleteCertificate(Long id) throws Exception {
        tx.runInTransaction(em -> {
            certRepo.delete(em, id);
            return null;
        });
    }

    public List<Certificate> getAllCertificates() throws Exception {
        return tx.runInTransaction(em -> certRepo.findAll(em));
    }
}