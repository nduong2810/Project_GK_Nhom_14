package vn.edu.ute.repo;

/**
 * Giao diện ResultRepository hợp nhất hai giao diện nhỏ hơn là GradeEntryRepository và StudentGradeRepository.
 *
 * ISP (Interface Segregation Principle - Nguyên tắc Phân tách Giao diện):
 * Thay vì tạo một giao diện lớn duy nhất cho việc quản lý kết quả, chúng ta tách nó thành các giao diện nhỏ hơn,
 * có mục đích cụ thể hơn.
 *
 * - GradeEntryRepository: Cung cấp các phương thức dành cho Giáo viên hoặc người có thẩm quyền nhập và cập nhật điểm.
 * - StudentGradeRepository: Cung cấp các phương thức dành cho Học viên để xem điểm của mình.
 *
 * Các service nên phụ thuộc vào giao diện nhỏ phù hợp với nhu cầu của chúng:
 *   - GradeEntryService  → GradeEntryRepository
 *   - StudentGradeService → StudentGradeRepository
 *
 * Điều này giúp giảm sự phụ thuộc không cần thiết và làm cho hệ thống trở nên rõ ràng, dễ bảo trì hơn.
 * Một lớp có thể triển khai giao diện hợp nhất này để cung cấp tất cả các chức năng.
 */
public interface ResultRepository extends GradeEntryRepository, StudentGradeRepository {
    // Giao diện này không cần định nghĩa thêm phương thức nào.
    // Nó kế thừa và hợp nhất tất cả các phương thức từ GradeEntryRepository và StudentGradeRepository.
}
