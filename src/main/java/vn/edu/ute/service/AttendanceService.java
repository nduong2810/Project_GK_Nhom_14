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

public class AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final TransactionManager tx;

    // Dependency Injection qua Constructor
    public AttendanceService(AttendanceRepository attendanceRepo, TransactionManager tx) {
        this.attendanceRepo = attendanceRepo;
        this.tx = tx;
    }

    /**
     * Lấy danh sách lớp của giáo viên (không bao gồm lớp đã hủy).
     * Sắp xếp bằng Stream API: lớp đang diễn ra lên trước, rồi theo ngày bắt đầu.
     */
    public List<ClassEntity> getClassesByTeacher(Long teacherId) throws Exception {
        return tx.runInTransaction(em -> {
            List<ClassEntity> classes = attendanceRepo.findClassesByTeacherId(em, teacherId);
            return classes.stream()
                    .sorted(Comparator
                            .comparing((ClassEntity c) -> c.getStatus() == ClassEntity.Status.Ongoing ? 0
                                    : c.getStatus() == ClassEntity.Status.Open ? 1 : 2)
                            .thenComparing(ClassEntity::getStartDate, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Lấy danh sách học viên đang enrolled trong một lớp.
     * Sắp xếp theo tên bằng Stream API.
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
     * Lấy điểm danh đã có cho một lớp + ngày.
     * Trả về Map: studentId → Attendance (dùng Stream Collectors.toMap).
     */
    public Map<Long, Attendance> getAttendanceMap(Long classId, LocalDate date) throws Exception {
        return tx.runInTransaction(em -> {
            List<Attendance> records = attendanceRepo.findByClassAndDate(em, classId, date);
            return records.stream()
                    .collect(Collectors.toMap(
                            a -> a.getStudent().getStudentId(),
                            a -> a));
        });
    }

    /**
     * Lưu/cập nhật điểm danh cho toàn bộ danh sách học viên của một lớp trong 1
     * ngày.
     * Sử dụng Stream forEach để xử lý từng record.
     *
     * @param classId ID lớp
     * @param date    Ngày điểm danh
     * @param records Danh sách AttendanceRecord (studentId, status, note)
     */
    public void saveAttendance(Long classId, LocalDate date, List<AttendanceRecord> records) throws Exception {
        tx.runInTransaction(em -> {
            // Lấy attendance đã có cho lớp + ngày → Map để lookup nhanh
            Map<Long, Attendance> existingMap = attendanceRepo.findByClassAndDate(em, classId, date)
                    .stream()
                    .collect(Collectors.toMap(a -> a.getStudent().getStudentId(), a -> a));

            // Load ClassEntity cho FK + kiểm tra trạng thái
            ClassEntity classEntity = em.find(ClassEntity.class, classId);
            if (classEntity.getStatus() != ClassEntity.Status.Ongoing) {
                throw new IllegalStateException(
                        "Lớp '" + classEntity.getClassName() + "' không ở trạng thái Ongoing. Không thể điểm danh.");
            }

            // Xử lý từng record bằng Stream forEach
            records.forEach(record -> {
                Attendance existing = existingMap.get(record.studentId());
                if (existing != null) {
                    // Cập nhật record đã tồn tại
                    existing.setStatus(record.status());
                    existing.setNote(record.note());
                    em.merge(existing);
                } else {
                    // Tạo mới
                    Student student = em.find(Student.class, record.studentId());
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
     * Record class để chứa dữ liệu điểm danh từ UI.
     */
    public record AttendanceRecord(Long studentId, Attendance.Status status, String note) {
    }
}
