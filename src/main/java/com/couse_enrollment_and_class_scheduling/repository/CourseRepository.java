package main.java.com.couse_enrollment_and_class_scheduling.repository;

import com.couse_enrollment_and_class_scheduling.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find a course by course code
    Optional<Course> findByCourseCode(String courseCode);

    // Check if a course with this code exists
    boolean existsByCourseCode(String courseCode);
}
