package com.couse_enrollment_and_class_scheduling;
import com.couse_enrollment_and_class_scheduling.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    /**
     * Check if student is already enrolled in course
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    /**
     * Count how many students enrolled in a course
     */
    long countByCourseId(Long courseId);
    
    /**
     * Get all courses a student is enrolled in
     */
    @Query("""
        SELECT e FROM Enrollment e
        JOIN FETCH e.course
        WHERE e.student.id = :studentId
        ORDER BY e.enrollmentDate DESC
    """)
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Get all students enrolled in a course
     */
    @Query("""
        SELECT e FROM Enrollment e
        JOIN FETCH e.student
        WHERE e.course.id = :courseId
        ORDER BY e.enrollmentDate
    """)
    List<Enrollment> findByCourseId(@Param("courseId") Long courseId);

    long deleteByCourse_Id(Long courseId);
}
