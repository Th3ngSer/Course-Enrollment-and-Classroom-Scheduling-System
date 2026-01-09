package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.Course;
import com.couse_enrollment_and_class_scheduling.service.CourseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    // List all courses
    @GetMapping
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "courses/list";
    }

    // Show form to add a new course
    @GetMapping("/add")
    public String showAddForm(
            Model model,
            @RequestParam(name = "returnToAdmin", defaultValue = "false") boolean returnToAdmin
    ) {
        model.addAttribute("course", new Course());
        model.addAttribute("returnToAdmin", returnToAdmin);
        return "courses/add"; // Thymeleaf template
    }

    // Handle add course form submission
    @PostMapping("/add")
    public String addCourse(@Valid @ModelAttribute("course") Course course,
            BindingResult bindingResult,
            Model model,
            @RequestParam(name = "returnToAdmin", defaultValue = "false") boolean returnToAdmin) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("returnToAdmin", returnToAdmin);
            return "courses/add";
        }

        try {
            courseService.addCourse(course);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("returnToAdmin", returnToAdmin);
            return "courses/add";
        }

        return returnToAdmin ? "redirect:/admin/courses" : "redirect:/courses";
    }

    // Show form to edit a course
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable @NonNull Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        model.addAttribute("course", course);
        return "courses/edit";
    }

    // Handle edit course form submission
    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable @NonNull Long id,
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

    // Delete course
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("success", "Course deleted successfully.");
        } catch (EmptyResultDataAccessException ex) {
            redirectAttributes.addFlashAttribute("error", "Course not found.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete this course because it is referenced by other records (enrollments/schedules)."
            );
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Delete failed: " + ex.getMessage());
        }
        return "redirect:/courses";
    }
}
