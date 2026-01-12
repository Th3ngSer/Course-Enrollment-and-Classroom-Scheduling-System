package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.EnrollmentRequest;
import com.couse_enrollment_and_class_scheduling.Enrollment;
import com.couse_enrollment_and_class_scheduling.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // --------------------
    // POST: Enroll a student
    // --------------------
    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@RequestBody EnrollmentRequest request, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Not authenticated");
            }

            if (request == null || request.getCourseId() == null) {
                return ResponseEntity.badRequest().body("courseId is required");
            }

            Enrollment enrollment = enrollmentService.enrollStudentByUsername(
                    authentication.getName(),
                    request.getCourseId()
            );
            return ResponseEntity.ok("Enroll success");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --------------------
    // GET: My Enrolled Courses (current authenticated student)
    // --------------------
    @GetMapping("/my-schedule")
    public ResponseEntity<?> getMySchedule(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Not authenticated");
            }
            return ResponseEntity.ok(enrollmentService.getStudentScheduleByUsername(authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --------------------
    // GET: My Enrolled Courses
    // --------------------
    @GetMapping("/my-schedule/{studentId}")
    public ResponseEntity<List<Enrollment>> getMySchedule(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getStudentSchedule(studentId));
    }
}