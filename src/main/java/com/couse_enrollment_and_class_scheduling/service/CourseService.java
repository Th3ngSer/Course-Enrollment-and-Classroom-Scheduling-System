package com.couse_enrollment_and_class_scheduling.service;

import com.couse_enrollment_and_class_scheduling.entity.Course;
import com.couse_enrollment_and_class_scheduling.entity.Lecturer;
import com.couse_enrollment_and_class_scheduling.repository.CourseRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // List all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }


    // Find course by ID
    public Optional<Course> getCourseById(@NonNull Long id) {
        return courseRepository.findById(id);
    }

    // Add new course
    public Course addCourse(Course course) {
        // Validation: courseCode must be unique
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + course.getCourseCode());
        }

        // Validation: capacity must be positive
        if (course.getCapacity() <= 0) {
            throw new IllegalArgumentException("Course capacity must be greater than 0");
        }

        return courseRepository.save(course);
    }


    // Update existing course
    public Course updateCourse(@NonNull Long id, Course updatedCourse) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));

        // Validate if courseCode changed
        if (!course.getCourseCode().equals(updatedCourse.getCourseCode())
                && courseRepository.existsByCourseCode(updatedCourse.getCourseCode())) {
            throw new IllegalArgumentException("Course code already exists: " + updatedCourse.getCourseCode());
        }

        // Update fields
        course.setCourseName(updatedCourse.getCourseName());
        course.setCourseCode(updatedCourse.getCourseCode());
        course.setCredits(updatedCourse.getCredits());
        course.setDescription(updatedCourse.getDescription());
        course.setCapacity(updatedCourse.getCapacity());

        return courseRepository.save(course);
    }


    // Delete course
    public void deleteCourse(@NonNull Long id) {
        courseRepository.deleteById(id);
    }

    public Course assignLecturerToCourse(@NonNull Long courseId, Lecturer lecturer) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        course.setLecturer(lecturer);
        return courseRepository.save(course);
    }
}
