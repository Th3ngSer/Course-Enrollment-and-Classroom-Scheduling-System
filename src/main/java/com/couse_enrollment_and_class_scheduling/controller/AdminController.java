package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.Course;
import com.couse_enrollment_and_class_scheduling.entity.Classroom;
import com.couse_enrollment_and_class_scheduling.entity.Lecturer;
import com.couse_enrollment_and_class_scheduling.entity.User;
import com.couse_enrollment_and_class_scheduling.repository.UserRepository;
import com.couse_enrollment_and_class_scheduling.service.ClassroomService;
import com.couse_enrollment_and_class_scheduling.service.CourseService;
import com.couse_enrollment_and_class_scheduling.service.LecturerService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CourseService courseService;
    private final LecturerService lecturerService;
    private final ClassroomService classroomService;
    private final UserRepository userRepository;

    public AdminController(
            CourseService courseService,
            LecturerService lecturerService,
            ClassroomService classroomService,
            UserRepository userRepository
    ) {
        this.courseService = courseService;
        this.lecturerService = lecturerService;
        this.classroomService = classroomService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "admin/dashboard";
    }

    @GetMapping("/courses")
    public String manageCourses(
            Model model,
            Authentication authentication,
            @RequestParam(name = "showCreate", defaultValue = "false") boolean showCreate
    ) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("courses", courseService.getAllCourses());
        // Legacy attributes (kept for backward compatibility if templates still reference them)
        model.addAttribute("course", new Course());
        model.addAttribute("showCreate", showCreate);
        return "admin/courses";
    }

    @GetMapping("/courses/create")
    public String createCourseForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("course", new Course());
        return "admin/create-course";
    }

    @PostMapping("/courses/create")
    public String createCourse(
            @Valid @ModelAttribute("course") Course course,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            return "admin/create-course";
        }

        try {
            courseService.addCourse(course);
            redirectAttributes.addFlashAttribute("success", "Course created successfully!");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("error", ex.getMessage());
            return "admin/create-course";
        }

        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourseAdmin(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
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
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        return "admin/assign-lecturer";
    }

    @PostMapping("/lecturers/assign")
    public String assignLecturerToCourse(
            @RequestParam @NonNull Long courseId,
            @RequestParam @NonNull Long lecturerId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Lecturer lecturer = lecturerService.getLecturerById(lecturerId);
            courseService.assignLecturerToCourse(courseId, lecturer);
            redirectAttributes.addFlashAttribute("success", "Lecturer assigned successfully!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/lecturers/assign";
        }

        return "redirect:/admin/lecturers";
    }

    @GetMapping("/classrooms")
    public String manageClassrooms(
            Model model,
            Authentication authentication,
            @RequestParam(name = "showCreate", defaultValue = "false") boolean showCreate
    ) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("classrooms", classroomService.getAllClassrooms());
        model.addAttribute("classroom", new Classroom());
        model.addAttribute("showCreate", showCreate);
        return "admin/classrooms";
    }

    @GetMapping("/classrooms/add")
    public String addClassroomForm(Model model, Authentication authentication) {
        // Keep backward compatibility if something still links here.
        return "redirect:/admin/classrooms?showCreate=true#create-classroom";
    }

    @PostMapping("/classrooms/create")
    public String createClassroom(
            @Valid @ModelAttribute("classroom") Classroom classroom,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("classrooms", classroomService.getAllClassrooms());
            model.addAttribute("showCreate", true);
            return "admin/classrooms";
        }

        try {
            classroomService.addClassroom(classroom);
            redirectAttributes.addFlashAttribute("success", "Classroom created successfully!");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("classrooms", classroomService.getAllClassrooms());
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("showCreate", true);
            return "admin/classrooms";
        }

        return "redirect:/admin/classrooms";
    }

    @PostMapping("/classrooms/delete/{id}")
    public String deleteClassroomAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            classroomService.deleteClassroom(id);
            redirectAttributes.addFlashAttribute("success", "Classroom deleted successfully.");
        } catch (EmptyResultDataAccessException ex) {
            redirectAttributes.addFlashAttribute("error", "Classroom not found.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Cannot delete this classroom because it is referenced by other records (courses/schedules)."
            );
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Delete failed: " + ex.getMessage());
        }
        return "redirect:/admin/classrooms";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "admin/create-user";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable @NonNull Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (authentication != null && user.getUsername().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "You cannot disable your own account.");
            return "redirect:/admin/users";
        }

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "User status updated successfully.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable @NonNull Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (authentication != null && user.getUsername().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "You cannot delete your own account.");
            return "redirect:/admin/users";
        }

        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete this user because it is referenced by other records.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Delete failed: " + ex.getMessage());
        }

        return "redirect:/admin/users";
    }
}