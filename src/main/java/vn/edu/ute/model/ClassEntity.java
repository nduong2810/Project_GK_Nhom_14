package vn.edu.ute.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp ClassEntity đại diện cho một lớp học cụ thể được mở tại trung tâm.
 * Tên lớp là `ClassEntity` thay vì `Class` để tránh xung đột với từ khóa `class` trong Java.
 * Đây là một entity, được ánh xạ tới bảng 'classes' trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "classes", indexes = {
        // Tạo chỉ mục để tăng tốc độ truy vấn theo khóa học, giáo viên, phòng học và ngày.
        @Index(name = "idx_classes_course", columnList = "course_id"),
        @Index(name = "idx_classes_teacher", columnList = "teacher_id"),
        @Index(name = "idx_classes_room", columnList = "room_id"),
        @Index(name = "idx_classes_dates", columnList = "start_date,end_date")
})
public class ClassEntity {

    // ---- Enums ----

    /**
     * Enum định nghĩa các trạng thái có thể có của một lớp học.
     */
    public enum Status {
        Planned,   // Đã lên kế hoạch, chưa mở ghi danh
        Open,      // Đang mở ghi danh
        Ongoing,   // Đang diễn ra
        Completed, // Đã hoàn thành
        Cancelled  // Đã bị hủy
    }

    // ---- Fields ----

    /**
     * ID của lớp học, là khóa chính, tự động tăng.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;

    /**
     * Tên hoặc mã của lớp học.
     */
    @Column(name = "class_name", nullable = false, length = 150)
    private String className;

    /**
     * Mối quan hệ nhiều-một với Course. Mỗi lớp học phải thuộc về một khóa học.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_classes_course"))
    private Course course;

    /**
     * Mối quan hệ nhiều-một với Teacher. Một giáo viên được phân công dạy lớp học này.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", foreignKey = @ForeignKey(name = "fk_classes_teacher"))
    private Teacher teacher;

    /**
     * Ngày bắt đầu lớp học.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Ngày kết thúc dự kiến của lớp học.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Số lượng học viên tối đa của lớp, mặc định là 0.
     */
    @Column(name = "max_student", nullable = false, columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer maxStudent = 0;

    /**
     * Mối quan hệ nhiều-một với Room. Lớp học được tổ chức tại một phòng học cụ thể.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_classes_room"))
    private Room room;

    /**
     * Trạng thái của lớp học, mặc định là 'Planned'.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('Planned','Open','Ongoing','Completed','Cancelled') NOT NULL DEFAULT 'Planned'")
    private Status status = Status.Planned;

    /**
     * Mối quan hệ nhiều-một với Branch. Lớp học thuộc về một chi nhánh cụ thể.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "fk_classes_branch"))
    private Branch branch;

    /**
     * Thời gian tạo lớp học, tự động gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật thông tin lớp học lần cuối, tự động gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---- Relationships (inverse) ----

    /**
     * Mối quan hệ một-nhiều với Enrollment (một lớp học có nhiều học viên ghi danh).
     */
    @OneToMany(mappedBy = "classEntity")
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Schedule (một lớp học có nhiều buổi học trong lịch).
     * `cascade = CascadeType.ALL, orphanRemoval = true`: Khi xóa lớp học, các lịch học liên quan cũng bị xóa.
     */
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Attendance (một lớp học có nhiều bản ghi điểm danh).
     * `cascade = CascadeType.ALL, orphanRemoval = true`: Khi xóa lớp học, các bản ghi điểm danh liên quan cũng bị xóa.
     */
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Result (một lớp học có nhiều kết quả học tập của học viên).
     * `cascade = CascadeType.ALL, orphanRemoval = true`: Khi xóa lớp học, các kết quả liên quan cũng bị xóa.
     */
    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results = new ArrayList<>();

    /**
     * Mối quan hệ một-nhiều với Certificate (chứng chỉ có thể được cấp dựa trên việc hoàn thành lớp học này).
     */
    @OneToMany(mappedBy = "classEntity")
    private List<Certificate> certificates = new ArrayList<>();


    // ---- Constructors ----

    /**
     * Hàm khởi tạo mặc định.
     */
    public ClassEntity() {
    }

    // ---- Getters / Setters ----

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getMaxStudent() {
        return maxStudent;
    }

    public void setMaxStudent(Integer maxStudent) {
        this.maxStudent = maxStudent;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }
}
