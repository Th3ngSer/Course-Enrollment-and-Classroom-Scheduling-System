package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.Course;
import com.couse_enrollment_and_class_scheduling.entity.Classroom;
import com.couse_enrollment_and_class_scheduling.entity.ClassSchedule;
import com.couse_enrollment_and_class_scheduling.entity.Lecturer;
import com.couse_enrollment_and_class_scheduling.entity.Role;
import com.couse_enrollment_and_class_scheduling.entity.User;
import com.couse_enrollment_and_class_scheduling.repository.CourseRepository;
import com.couse_enrollment_and_class_scheduling.repository.RoleRepository;
import com.couse_enrollment_and_class_scheduling.repository.UserRepository;
import com.couse_enrollment_and_class_scheduling.service.ClassroomService;
import com.couse_enrollment_and_class_scheduling.service.ClassScheduleService;
import com.couse_enrollment_and_class_scheduling.service.CourseService;
import com.couse_enrollment_and_class_scheduling.service.LecturerService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CourseService courseService;
    private final LecturerService lecturerService;
    private final ClassroomService classroomService;
    private final ClassScheduleService classScheduleService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            CourseService courseService,
            LecturerService lecturerService,
            ClassroomService classroomService,
            ClassScheduleService classScheduleService,
            CourseRepository courseRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.courseService = courseService;
        this.lecturerService = lecturerService;
        this.classroomService = classroomService;
        this.classScheduleService = classScheduleService;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static boolean hasRole(User user, String roleName) {
        if (user == null || user.getRoles() == null) {
            return false;
        }
        return user.getRoles().stream().anyMatch(r -> roleName.equals(r.getName()));
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());

        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("activeLecturers", userRepository.countEnabledLecturersNonAdmin());
        model.addAttribute("registeredStudents", userRepository.countEnabledStudentsOnly());

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

        List<Course> courses = courseService.getAllCourses();

        // Only show lecturer profiles that are actually usable:
        // - linked to a login user, OR
        // - currently assigned to a course.
        var assignedLecturerIds = courses.stream()
            .map(Course::getLecturer)
            .filter(l -> l != null && l.getId() != null)
            .map(Lecturer::getId)
            .collect(java.util.stream.Collectors.toSet());

        List<Lecturer> lecturers = lecturerService.getAllLecturers().stream()
            .filter(l -> l.getUser() != null || assignedLecturerIds.contains(l.getId()))
            .toList();

        Map<Long, String> lecturerCourses = lecturers.stream().collect(Collectors.toMap(
                Lecturer::getId,
                lecturer -> courses.stream()
                        .filter(c -> c.getLecturer() != null && c.getLecturer().getId().equals(lecturer.getId()))
                        .map(Course::getCourseCode)
                        .sorted()
                        .collect(Collectors.joining(", "))
        ));

        model.addAttribute("lecturers", lecturers);
        model.addAttribute("lecturerCourses", lecturerCourses);
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

    // --- LECTURER MANAGEMENT (Missing Logic) ---

    @GetMapping("/lecturers/add")
    public String addLecturerForm(Model model, Authentication authentication) {
        return "redirect:/admin/users/create";
    }

    @PostMapping("/lecturers/create")
    public String createLecturer(
            @Valid @ModelAttribute("lecturerForm") AdminLecturerCreateForm lecturerForm,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("error", "Please create lecturers from User Management.");
        return "redirect:/admin/users";
    }

    @GetMapping("/lecturers/edit/{id}")
    public String editLecturerForm(@PathVariable Long id, Model model, Authentication authentication) {
        return "redirect:/admin/users";
    }

    @PostMapping("/lecturers/update/{id}")
    public String updateLecturer(
            @PathVariable Long id,
            @Valid @ModelAttribute("lecturer") Lecturer updatedLecturer,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("error", "Lecturer profile editing is managed via User Management.");
        return "redirect:/admin/users";
    }

    @PostMapping("/lecturers/delete/{id}")
    public String deleteLecturer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Please manage lecturer accounts from User Management.");
        return "redirect:/admin/users";
    }

    // --- COURSE EDITING (Missing Logic) ---

    @GetMapping("/courses/edit/{id}")
    public String editCourseForm(@PathVariable @NonNull Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        model.addAttribute("username", authentication.getName());

        return courseService.getCourseById(id)
                .map(course -> {
                    model.addAttribute("course", course);
                    return "admin/edit-course";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Course not found.");
                    return "redirect:/admin/courses";
                });
    }

    @PostMapping("/courses/update/{id}")
        public String updateCourse(
            @PathVariable @NonNull Long id,
            @Valid @ModelAttribute("course") Course course,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("username", authentication.getName());

        if (bindingResult.hasErrors()) {
            return "admin/edit-course";
        }

        try {
            courseService.updateCourse(id, course);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
            return "redirect:/admin/courses";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Error updating course: " + e.getMessage());
            return "admin/edit-course";
        }
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

    @GetMapping("/schedules")
    public String manageSchedules(
            Model model,
            Authentication authentication,
            @RequestParam(name = "showCreate", defaultValue = "false") boolean showCreate
    ) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("schedules", classScheduleService.getAllSchedules());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("classrooms", classroomService.getAllClassrooms());
        model.addAttribute("showCreate", showCreate);
        return "admin/schedules";
    }

    @PostMapping("/schedules/create")
    public String createSchedule(
            @RequestParam @NonNull Long courseId,
            @RequestParam @NonNull Long classroomId,
            @RequestParam @NonNull String dayOfWeek,
            @RequestParam @NonNull String startTime,
            @RequestParam @NonNull String endTime,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Course course = courseService.getCourseById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
            Classroom classroom = classroomService.getClassroomById(classroomId)
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found with id: " + classroomId));

            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }

            ClassSchedule schedule = new ClassSchedule();
            schedule.setCourse(course);
            schedule.setClassroom(classroom);
            schedule.setDayOfWeek(day);
            schedule.setStartTime(start);
            schedule.setEndTime(end);

            classScheduleService.addSchedule(schedule);
            redirectAttributes.addFlashAttribute("success", "Schedule created successfully!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/schedules?showCreate=true#create-schedule";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Create failed: " + ex.getMessage());
            return "redirect:/admin/schedules?showCreate=true#create-schedule";
        }

        return "redirect:/admin/schedules";
    }

    @PostMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            classScheduleService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("success", "Schedule deleted successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Delete failed: " + ex.getMessage());
        }
        return "redirect:/admin/schedules";
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

        List<User> allUsers = userRepository.findAll();
        List<User> adminUsers = allUsers.stream()
            .filter(u -> hasRole(u, "ROLE_ADMIN"))
            .toList();
        List<User> lecturerUsers = allUsers.stream()
            .filter(u -> !hasRole(u, "ROLE_ADMIN") && hasRole(u, "ROLE_LECTURER"))
            .toList();
        List<User> studentUsers = allUsers.stream()
            .filter(u -> !hasRole(u, "ROLE_ADMIN") && !hasRole(u, "ROLE_LECTURER"))
            .toList();

        model.addAttribute("adminUsers", adminUsers);
        model.addAttribute("lecturerUsers", lecturerUsers);
        model.addAttribute("studentUsers", studentUsers);

        Map<Long, String> roleDisplay = allUsers.stream().collect(Collectors.toMap(
            User::getId,
            u -> u.getRoles().stream()
                .map(Role::getName)
                .map(name -> name != null && name.startsWith("ROLE_") ? name.substring("ROLE_".length()) : name)
                .collect(Collectors.joining(", "))
        ));
        model.addAttribute("roleDisplay", roleDisplay);
        return "admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("user", new User());
        return "admin/create-user";
    }

    @PostMapping("/users/create")
        @Transactional
    public String createUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(name = "role", required = false, defaultValue = "ROLE_STUDENT") String roleName,
            @RequestParam(name = "department", required = false) String department,
            @RequestParam(name = "officeHours", required = false) String officeHours,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("username", authentication.getName());

        if (bindingResult.hasErrors()) {
            return "admin/create-user";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
            return "admin/create-user";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email already registered");
            return "admin/create-user";
        }

        if (roleName == null || roleName.isBlank()) {
            roleName = "ROLE_STUDENT";
        }
        if (!roleName.equals("ROLE_STUDENT") && !roleName.equals("ROLE_LECTURER") && !roleName.equals("ROLE_ADMIN")) {
            model.addAttribute("error", "Invalid role selected");
            return "admin/create-user";
        }

        Optional<Role> selectedRole = roleRepository.findByName(roleName);
        if (selectedRole.isEmpty()) {
            model.addAttribute("error", "System error: Selected role not found");
            return "admin/create-user";
        }

        if ("ROLE_LECTURER".equals(roleName)) {
            if (department == null || department.isBlank()) {
                model.addAttribute("error", "Department is required for Lecturer accounts");
                return "admin/create-user";
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRoles(new HashSet<>());
        user.getRoles().add(selectedRole.get());

        try {
            User savedUser = userRepository.save(user);

            if ("ROLE_LECTURER".equals(roleName)) {
                String fullName = (user.getFirstName() + " " + user.getLastName()).trim();
                Lecturer lecturer = (!fullName.isBlank())
                    ? lecturerService.findUnlinkedByFullName(fullName).orElseGet(Lecturer::new)
                    : new Lecturer();
                lecturer.setFullName(fullName);
                lecturer.setDepartment(department.trim());
                lecturer.setOfficeHours(officeHours == null ? null : officeHours.trim());
                lecturer.setUser(savedUser);
                lecturerService.saveLecturer(lecturer);
            }

            redirectAttributes.addFlashAttribute("success", "User created successfully.");
            return "redirect:/admin/users";
        } catch (RuntimeException ex) {
            model.addAttribute("error", "Create failed: " + ex.getMessage());
            return "admin/create-user";
        }
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable @NonNull Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        model.addAttribute("username", authentication.getName());

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }

        User user = userOpt.get();
        AdminUserEditForm form = new AdminUserEditForm();
        form.setEmail(user.getEmail());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());

        boolean isLecturer = hasRole(user, "ROLE_LECTURER") && !hasRole(user, "ROLE_ADMIN");
        model.addAttribute("isLecturer", isLecturer);

        if (isLecturer) {
            Long userId = user.getId();
            if (userId != null) {
                lecturerService.findByUserId(userId).ifPresent(lecturer -> {
                    form.setDepartment(lecturer.getDepartment());
                    form.setOfficeHours(lecturer.getOfficeHours());
                });
            }
        }

        model.addAttribute("editUser", user);
        model.addAttribute("editForm", form);
        return "admin/edit-user";
    }

    @PostMapping("/users/update/{id}")
    @Transactional
    public String updateUser(
            @PathVariable @NonNull Long id,
            @Valid @ModelAttribute("editForm") AdminUserEditForm editForm,
            BindingResult bindingResult,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("username", authentication.getName());

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/users";
        }

        User user = userOpt.get();
        boolean isLecturer = hasRole(user, "ROLE_LECTURER") && !hasRole(user, "ROLE_ADMIN");
        model.addAttribute("isLecturer", isLecturer);
        model.addAttribute("editUser", user);

        if (bindingResult.hasErrors()) {
            return "admin/edit-user";
        }

        if (isLecturer) {
            if (editForm.getDepartment() == null || editForm.getDepartment().isBlank()) {
                model.addAttribute("error", "Department is required for Lecturer accounts");
                return "admin/edit-user";
            }
        }

        userRepository.findByEmail(editForm.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(user.getId())) {
                bindingResult.rejectValue("email", "email.exists", "Email already registered");
            }
        });
        if (bindingResult.hasErrors()) {
            return "admin/edit-user";
        }

        user.setEmail(editForm.getEmail());
        user.setFirstName(editForm.getFirstName());
        user.setLastName(editForm.getLastName());

        if (editForm.getPassword() != null && !editForm.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(editForm.getPassword()));
        }

        userRepository.save(user);

        if (isLecturer) {
            String fullName = (user.getFirstName() + " " + user.getLastName()).trim();
            Lecturer lecturer;
            Long userId = user.getId();
            if (userId != null) {
                lecturer = lecturerService.findByUserId(userId).orElseGet(() -> {
                    if (!fullName.isBlank()) {
                        return lecturerService.findUnlinkedByFullName(fullName).orElseGet(Lecturer::new);
                    }
                    return new Lecturer();
                });
            } else {
                lecturer = (!fullName.isBlank())
                        ? lecturerService.findUnlinkedByFullName(fullName).orElseGet(Lecturer::new)
                        : new Lecturer();
            }
            lecturer.setFullName((user.getFirstName() + " " + user.getLastName()).trim());
            lecturer.setDepartment(editForm.getDepartment().trim());
            lecturer.setOfficeHours(editForm.getOfficeHours() == null ? null : editForm.getOfficeHours().trim());
            lecturer.setUser(user);
            lecturerService.saveLecturer(lecturer);
        }

        redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/admin/users";
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