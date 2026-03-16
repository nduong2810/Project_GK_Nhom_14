package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.repo.CertificateRepository;

import java.util.List;

/**
 * Lớp Service cho nghiệp vụ quản lý chứng chỉ (Certificate).
 * Điều phối các thao tác dữ liệu và quản lý giao dịch.
 */
public class CertificateService {
    private final CertificateRepository certRepo;
    private final TransactionManager tx;

    public CertificateService(CertificateRepository certRepo, TransactionManager tx) {
        this.certRepo = certRepo;
        this.tx = tx;
    }

    /**
     * Tạo một chứng chỉ mới.
     * @param certificate Đối tượng Certificate cần tạo.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void createCertificate(Certificate certificate) throws Exception {
        tx.runInTransaction(em -> {
            certRepo.save(em, certificate);
            return null;
        });
    }

    /**
     * Cập nhật thông tin một chứng chỉ.
     * @param certificate Đối tượng Certificate chứa thông tin cập nhật.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void updateCertificate(Certificate certificate) throws Exception {
        tx.runInTransaction(em -> {
            certRepo.update(em, certificate);
            return null;
        });
    }

    /**
     * Xóa một chứng chỉ.
     * @param id ID của chứng chỉ cần xóa.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public void deleteCertificate(Long id) throws Exception {
        tx.runInTransaction(em -> {
            certRepo.delete(em, id);
            return null;
        });
    }

    /**
     * Lấy danh sách tất cả các chứng chỉ.
     * @return Danh sách các đối tượng Certificate.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Certificate> getAllCertificates() throws Exception {
        return tx.runInTransaction(em -> certRepo.findAll(em));
    }
}
