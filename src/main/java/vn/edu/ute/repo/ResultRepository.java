package vn.edu.ute.repo;

/**
 * ISP: ResultRepository hợp nhất 2 interface nhỏ hơn.
 * - GradeEntryRepository: dành cho GiaoVien nhập điểm
 * - StudentGradeRepository: dành cho HocVien xem điểm
 *
 * Các service nên phụ thuộc vào interface nhỏ phù hợp:
 *   GradeEntryService  → GradeEntryRepository
 *   StudentGradeService → StudentGradeRepository
 */
public interface ResultRepository extends GradeEntryRepository, StudentGradeRepository {
    // Hợp nhất toàn bộ phương thức từ 2 interface nhỏ hơn
}
