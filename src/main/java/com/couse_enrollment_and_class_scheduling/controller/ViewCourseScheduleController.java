package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.service.CourseService;
import com.couse_enrollment_and_class_scheduling.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewCourseScheduleController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @Autowired
    public ViewCourseScheduleController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/class-schedule")
    
    public String showClassSchedule(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "class-schedule"; // Looks for templates/class-schedule.html
    }

    @GetMapping("/my-courses/{studentId}")
    public String showMyCourses(@PathVariable Long studentId, Model model) {
        model.addAttribute("myEnrollments", enrollmentService.getStudentSchedule(studentId));
        return "my-course"; // FIXED: Changed from "my-courses" to "my-course"
    }
}