package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.ClassScheduleRepository;
import com.couse_enrollment_and_class_scheduling.Enrollment;
import com.couse_enrollment_and_class_scheduling.EnrollmentRepository;
import com.couse_enrollment_and_class_scheduling.entity.ClassSchedule;
import com.couse_enrollment_and_class_scheduling.entity.Course;
import com.couse_enrollment_and_class_scheduling.entity.Role;
import com.couse_enrollment_and_class_scheduling.entity.User;
import com.couse_enrollment_and_class_scheduling.repository.RoleRepository;
import com.couse_enrollment_and_class_scheduling.repository.UserRepository;
import com.couse_enrollment_and_class_scheduling.service.CourseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lecturer")
@PreAuthorize("hasRole('LECTURER')")
public class LecturerDashboardController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CourseService courseService;
    private final ClassScheduleRepository classScheduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    public LecturerDashboardController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CourseService courseService,
            ClassScheduleRepository classScheduleRepository,
            EnrollmentRepository enrollmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.courseService = courseService;
        this.classScheduleRepository = classScheduleRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private long requireUserId(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(u -> Objects.requireNonNull(u.getId(), "User id is null for username: " + username))
                .orElseThrow(() -> new IllegalStateException("User not found for username: " + username));
    }

    @GetMapping("/dashboard")
    public String lecturerDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());

        long userId = requireUserId(authentication);
        List<Course> myCourses = courseService.getCoursesForLecturerUserId(userId);
        List<ClassSchedule> mySchedules = classScheduleRepository.findForLecturerUserIdWithDetails(userId);

        model.addAttribute("myCoursesCount", myCourses.size());
        model.addAttribute("nextSchedules", mySchedules.stream().limit(5).toList());
        return "lecturer/dashboard";
    }

    @GetMapping("/courses")
    public String lecturerCourses(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Lecturer");

        long userId = requireUserId(authentication);
        List<Course> myCourses = courseService.getCoursesForLecturerUserId(userId);

        List<ClassSchedule> mySchedules;
        if (myCourses.isEmpty()) {
            mySchedules = Collections.emptyList();
        } else {
            mySchedules = classScheduleRepository.findForLecturerUserIdWithDetails(userId);
        }

        Map<Long, List<ClassSchedule>> schedulesByCourseId = mySchedules.stream()
                .filter(cs -> cs.getCourse() != null && cs.getCourse().getId() != null)
                .collect(Collectors.groupingBy(cs -> cs.getCourse().getId()));

        model.addAttribute("courses", myCourses);
        model.addAttribute("schedulesByCourse", schedulesByCourseId);
        return "lecturer/courses";
    }

    @GetMapping("/students")
    public String lecturerStudents() {
        // Student lists are per-course. Use the Courses page to pick one.
        return "redirect:/lecturer/courses";
    }

    @GetMapping("/courses/{courseId}/students")
    public String lecturerCourseStudents(
            @PathVariable long courseId,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("username", authentication.getName());

        long userId = requireUserId(authentication);

        Course course = courseService.getCourseForLecturerUserId(courseId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found or not assigned to you."));

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollments);

        // Optional: show edit form for one student
        model.addAttribute("editStudentId", null);
        return "lecturer/students";
    }

    @PostMapping("/courses/{courseId}/students/create")
    public String createAndEnrollStudent(
            @PathVariable long courseId,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        long lecturerUserId = requireUserId(authentication);

        // Ensure lecturer owns this course
        Course course = courseService.getCourseForLecturerUserId(courseId, lecturerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found or not assigned to you."));

        try {
            if (userRepository.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("error", "Username already exists.");
                return "redirect:/lecturer/courses/" + courseId + "/students";
            }
            if (userRepository.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email already exists.");
                return "redirect:/lecturer/courses/" + courseId + "/students";
            }

            Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                    .orElseThrow(() -> new IllegalStateException("ROLE_STUDENT not found in database"));

            User student = new User();
            student.setUsername(username.trim());
            student.setEmail(email.trim());
            student.setFirstName(firstName.trim());
            student.setLastName(lastName.trim());
            student.setPassword(passwordEncoder.encode(password));
            student.getRoles().add(studentRole);
            student.setEnabled(true);

            User saved = userRepository.save(student);

            if (enrollmentRepository.existsByStudentIdAndCourseId(saved.getId(), course.getId())) {
                redirectAttributes.addFlashAttribute("error", "Student is already enrolled.");
                return "redirect:/lecturer/courses/" + courseId + "/students";
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(saved);
            enrollment.setCourse(course);
            enrollmentRepository.save(enrollment);

            redirectAttributes.addFlashAttribute("success", "Student created and enrolled successfully.");
            return "redirect:/lecturer/courses/" + courseId + "/students";
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Create failed: username/email may already exist.");
            return "redirect:/lecturer/courses/" + courseId + "/students";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Create failed: " + ex.getMessage());
            return "redirect:/lecturer/courses/" + courseId + "/students";
        }
    }

    @PostMapping("/courses/{courseId}/students/{studentId}/update")
    public String updateStudent(
            @PathVariable long courseId,
            @PathVariable long studentId,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        long lecturerUserId = requireUserId(authentication);

        // Ensure lecturer owns this course
        courseService.getCourseForLecturerUserId(courseId, lecturerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found or not assigned to you."));

        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            redirectAttributes.addFlashAttribute("error", "Student is not enrolled in this course.");
            return "redirect:/lecturer/courses/" + courseId + "/students";
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        student.setEmail(email.trim());
        student.setFirstName(firstName.trim());
        student.setLastName(lastName.trim());

        try {
            userRepository.save(student);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Update failed: Email already exists.");
        }

        return "redirect:/lecturer/courses/" + courseId + "/students";
    }

    @PostMapping("/courses/{courseId}/enrollments/{enrollmentId}/remove")
    public String removeEnrollment(
            @PathVariable long courseId,
            @PathVariable long enrollmentId,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        long lecturerUserId = requireUserId(authentication);

        // Ensure lecturer owns this course
        courseService.getCourseForLecturerUserId(courseId, lecturerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found or not assigned to you."));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null || !enrollment.getCourse().getId().equals(courseId)) {
            redirectAttributes.addFlashAttribute("error", "Enrollment does not belong to this course.");
            return "redirect:/lecturer/courses/" + courseId + "/students";
        }

        enrollmentRepository.deleteById(enrollmentId);
        redirectAttributes.addFlashAttribute("success", "Student removed from this course.");
        return "redirect:/lecturer/courses/" + courseId + "/students";
    }
}
