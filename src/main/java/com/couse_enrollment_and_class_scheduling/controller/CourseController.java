package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.Course;
import com.couse_enrollment_and_class_scheduling.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // --------------------
    // List all courses
    // --------------------
    @GetMapping
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "courses/list"; // Thymeleaf template: src/main/resources/templates/courses/list.html
    }

    // --------------------
    // Show form to add a new course
    // --------------------
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/add"; // Thymeleaf template
    }

    // --------------------
    // Handle add course form submission
    // --------------------
    @PostMapping("/add")
    public String addCourse(@Valid @ModelAttribute("course") Course course,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "courses/add";
        }

        try {
            courseService.addCourse(course);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "courses/add";
        }

        return "redirect:/courses";
    }

    // --------------------
    // Show form to edit a course
    // --------------------
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        model.addAttribute("course", course);
        return "courses/edit"; // Thymeleaf template
    }

    // --------------------
    // Handle edit course form submission
    // --------------------
    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable Long id,
            @Valid @ModelAttribute("course") Course course,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "courses/edit";
        }

        try {
            courseService.updateCourse(id, course);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "courses/edit";
        }

        return "redirect:/courses";
    }

    // --------------------
    // Delete course
    // --------------------
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }
}
