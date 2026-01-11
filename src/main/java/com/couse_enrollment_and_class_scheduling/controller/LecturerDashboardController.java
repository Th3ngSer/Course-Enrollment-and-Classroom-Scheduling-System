package com.couse_enrollment_and_class_scheduling.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lecturer")
@PreAuthorize("hasRole('LECTURER')")
public class LecturerDashboardController {

    @GetMapping("/dashboard")
    public String lecturerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "lecturer/dashboard";
    }

    @GetMapping("/courses")
    public String lecturerCourses(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Lecturer");
        return "lecturer/courses";
    }

    @GetMapping("/students")
    public String lecturerStudents(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "lecturer/students";
    }

    @GetMapping("/courses/{courseId}/students")
    public String lecturerCourseStudents(
            @PathVariable Long courseId,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("courseId", courseId);
        return "lecturer/students";
    }
}
