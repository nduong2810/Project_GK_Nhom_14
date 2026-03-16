package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.ClassEntity;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Student;
import vn.edu.ute.repo.AttendanceRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp Service cho nghiệp vụ điểm danh (Attendance).
 * Chứa các logic nghiệp vụ để hỗ trợ giáo viên thực hiện việc điểm danh.
 */
public class AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final TransactionManager tx;

    public AttendanceService(AttendanceRepository attendanceRepo, TransactionManager tx) {
        this.attendanceRepo = attendanceRepo;
        this.tx = tx;
    }

    /**
     * Lấy danh sách các lớp học mà một giáo viên phụ trách (không bao gồm các lớp đã hủy).
     * Sắp xếp danh sách bằng Stream API: ưu tiên các lớp đang diễn ra ('Ongoing'),
     * sau đó là các lớp đang mở ('Open'), cuối cùng là các trạng thái khác,
     * và trong mỗi nhóm thì sắp xếp theo ngày bắt đầu mới nhất.
     * @param teacherId ID của giáo viên.
     * @return Danh sách ClassEntity đã được sắp xếp.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<ClassEntity> getClassesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            List<ClassEntity> classes = attendanceRepo.findClassesByTeacherId(em, teacherId);
            return classes.stream()
                    .sorted(Comparator
                            // Sắp xếp theo trạng thái: Ongoing (0) -> Open (1) -> Khác (2)
                            .comparing((ClassEntity c) -> c.getStatus() == ClassEntity.Status.Ongoing ? 0
                                    : c.getStatus() == ClassEntity.Status.Open ? 1 : 2)
                            // Nếu trạng thái bằng nhau, sắp xếp theo ngày bắt đầu giảm dần
                            .thenComparing(ClassEntity::getStartDate, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy danh sách học viên đang ghi danh ('Enrolled') trong một lớp học.
     * Sắp xếp danh sách theo tên học viên.
     * @param classId ID của lớp học.
     * @return Danh sách Enrollment đã được sắp xếp.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public List<Enrollment> getEnrolledStudents(Long classId) throws Exception {
        return tx.runInTransaction(em -> {
            List<Enrollment> enrollments = attendanceRepo.findEnrolledStudentsByClassId(em, classId);
            return enrollments.stream()
                    .sorted(Comparator.comparing(e -> e.getStudent().getFullName()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy thông tin điểm danh đã có của một lớp vào một ngày cụ thể.
     * Chuyển danh sách kết quả thành một Map để dễ dàng tra cứu (lookup) theo `studentId`.
     * @param classId ID của lớp học.
     * @param date Ngày cần lấy thông tin điểm danh.
     * @return Một Map với key là `studentId` và value là đối tượng `Attendance` tương ứng.
     * @throws Exception nếu có lỗi giao dịch.
     */
    public Map<Long, Attendance> getAttendanceMap(Long classId, LocalDate date) throws Exception {
        return tx.runInTransaction(em -> {
            List<Attendance> records = attendanceRepo.findByClassAndDate(em, classId, date);
            return records.stream()
                    .collect(Collectors.toMap(
                            a -> a.getStudent().getStudentId(), // Key của Map
                            a -> a                             // Value của Map
                    ));
        });
    }

    /**
     * Lưu hoặc cập nhật thông tin điểm danh cho một danh sách học viên của một lớp vào một ngày cụ thể.
     * @param classId ID của lớp học.
     * @param date Ngày điểm danh.
     * @param records Danh sách các bản ghi điểm danh (dưới dạng `AttendanceRecord`).
     * @throws Exception nếu có lỗi giao dịch.
     * @throws IllegalStateException nếu lớp học không ở trạng thái 'Ongoing'.
     */
    public void saveAttendance(Long classId, LocalDate date, List<AttendanceRecord> records) throws Exception {
        tx.runInTransaction(em -> {
            // Lấy danh sách điểm danh đã có và đưa vào Map để tra cứu nhanh
            Map<Long, Attendance> existingMap = getAttendanceMap(classId, date);

            // Tải đối tượng ClassEntity để dùng làm khóa ngoại và kiểm tra trạng thái
            ClassEntity classEntity = em.find(ClassEntity.class, classId);
            if (classEntity.getStatus() != ClassEntity.Status.Ongoing) {
                throw new IllegalStateException(
                        "Lớp '" + classEntity.getClassName() + "' không ở trạng thái Ongoing. Không thể điểm danh.");
            }

            // Duyệt qua từng bản ghi điểm danh từ UI để xử lý
            records.forEach(record -> {
                Attendance existing = existingMap.get(record.studentId());
                if (existing != null) {
                    // Nếu đã có bản ghi điểm danh cho sinh viên này -> Cập nhật
                    existing.setStatus(record.status());
                    existing.setNote(record.note());
                    em.merge(existing);
                } else {
                    // Nếu chưa có -> Tạo mới
                    Student student = em.getReference(Student.class, record.studentId()); // Dùng getReference để tối ưu
                    Attendance attendance = new Attendance();
                    attendance.setStudent(student);
                    attendance.setClassEntity(classEntity);
                    attendance.setAttendDate(date);
                    attendance.setStatus(record.status());
                    attendance.setNote(record.note());
                    em.persist(attendance);
                }
            });

            return null;
        });
    }

    /**
     * Một `record` (lớp dữ liệu bất biến) để đóng gói thông tin điểm danh nhận từ giao diện người dùng.
     * Ngắn gọn và tiện lợi hơn việc tạo một class thông thường.
     */
    public record AttendanceRecord(Long studentId, Attendance.Status status, String note) {
    }
}
