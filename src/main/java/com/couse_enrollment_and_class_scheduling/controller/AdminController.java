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
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "admin/dashboard";
    }

    @GetMapping("/courses")
    public String manageCourses(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/courses";
    }

    @GetMapping("/courses/create")
    public String createCourseForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/create-course";
    }

    @PostMapping("/courses/create")
    public String createCourse(@RequestParam String courseCode,
                              @RequestParam String courseName,
                              @RequestParam int credits,
                              @RequestParam String description,
                              RedirectAttributes redirectAttributes) {
        // TODO: Save course to database
        redirectAttributes.addFlashAttribute("success", 
                "Course created successfully!");
        return "redirect:/admin/courses";
    }

    @GetMapping("/lecturers")
    public String manageLecturers(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/lecturers";
    }

    @GetMapping("/lecturers/assign")
    public String assignLecturerForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/assign-lecturer";
    }

    @GetMapping("/classrooms")
    public String manageClassrooms(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/classrooms";
    }

    @GetMapping("/classrooms/add")
    public String addClassroomForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/add-classroom";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/create-user";
    }
}