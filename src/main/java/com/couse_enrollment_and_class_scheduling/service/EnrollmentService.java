package com.couse_enrollment_and_class_scheduling.service;

import com.couse_enrollment_and_class_scheduling.Enrollment;
import com.couse_enrollment_and_class_scheduling.entity.Course;
import com.couse_enrollment_and_class_scheduling.entity.User;
import com.couse_enrollment_and_class_scheduling.EnrollmentRepository;
import com.couse_enrollment_and_class_scheduling.repository.CourseRepository;
import com.couse_enrollment_and_class_scheduling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, 
                             CourseRepository courseRepository, 
                             UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // --------------------
    // Process Course Enrollment
    // --------------------
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        // Validation: Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        // Validation: Check if student exists
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        // Validation: Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course.");
        }

        // Validation: Check capacity
        long currentCount = enrollmentRepository.countByCourseId(courseId);
        if (currentCount >= course.getCapacity()) {
            throw new IllegalStateException("Course is full. Capacity reached.");
        }

        // Create and save enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    // --------------------
    // Get Student Schedule (Dashboard)
    // --------------------
    public List<Enrollment> getStudentSchedule(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
}