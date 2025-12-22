package com.couse_enrollment_and_class_scheduling.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lecturer")
@PreAuthorize("hasRole('LECTURER')")
public class LecturerController {

    @GetMapping("/dashboard")
    public String lecturerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "lecturer/dashboard";
    }

    @GetMapping("/courses")
    public String viewCourses(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Lecturer");
        // TODO: Fetch lecturer's courses from service
        return "lecturer/courses";
    }

    @GetMapping("/courses/{courseId}/students")
    public String viewCourseStudents(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        // TODO: Fetch students enrolled in specific course
        return "lecturer/students";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Lecturer");
        return "lecturer/profile";
    }

    @GetMapping("/schedule")
    public String viewSchedule(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        // TODO: Fetch lecturer's class schedule
        return "lecturer/schedule";
    }

    @GetMapping("/announcements")
    public String viewAnnouncements(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "lecturer/announcements";
    }
}