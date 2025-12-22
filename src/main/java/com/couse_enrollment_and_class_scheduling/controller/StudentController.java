package com.couse_enrollment_and_class_scheduling.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @GetMapping("/dashboard")
    public String studentDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String browseCourses(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Student");
        // TODO: Fetch all available courses from service
        return "student/courses";
    }

    @GetMapping("/courses/{courseId}")
    public String viewCourseDetails(@PathVariable Long courseId, 
                                   Model model, 
                                   Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("courseId", courseId);
        // TODO: Fetch course details by ID
        return "student/course-details";
    }

    @PostMapping("/courses/{courseId}/enroll")
    public String enrollInCourse(@PathVariable Long courseId,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        // TODO: Implement enrollment logic
        redirectAttributes.addFlashAttribute("success", 
                "Successfully enrolled in course!");
        return "redirect:/student/enrollments";
    }

    @GetMapping("/enrollments")
    public String viewEnrollments(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        // TODO: Fetch student's enrolled courses
        return "student/enrollments";
    }

    @PostMapping("/enrollments/{enrollmentId}/drop")
    public String dropCourse(@PathVariable Long enrollmentId,
                            RedirectAttributes redirectAttributes) {
        // TODO: Implement drop course logic
        redirectAttributes.addFlashAttribute("success", 
                "Successfully dropped the course!");
        return "redirect:/student/enrollments";
    }

    @GetMapping("/schedule")
    public String viewSchedule(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        // TODO: Fetch student's class schedule
        return "student/schedule";
    }

    @GetMapping("/grades")
    public String viewGrades(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        // TODO: Fetch student's grades
        return "student/grades";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Student");
        return "student/profile";
    }

    @GetMapping("/announcements")
    public String viewAnnouncements(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "student/announcements";
    }
}