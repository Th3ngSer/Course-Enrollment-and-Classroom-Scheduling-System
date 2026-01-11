package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.service.CourseService;
import com.couse_enrollment_and_class_scheduling.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewCourseScheduleController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @Autowired
    public ViewCourseScheduleController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/browse-course")
    
    public String showClassSchedule(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "/browse-course"; // Looks for templates/browse-available-course.html
    }

    @GetMapping("/my-courses")
    public String showMyCourses(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("myEnrollments", enrollmentService.getStudentScheduleByUsername(authentication.getName()));
        return "my-course"; // FIXED: Changed from "my-courses" to "my-course"
    }
}