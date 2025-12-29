package com.couse_enrollment_and_class_scheduling.controller;

import com.couse_enrollment_and_class_scheduling.entity.ClassSchedule;
import com.couse_enrollment_and_class_scheduling.service.ClassScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-schedules")
public class ClassScheduleController {

    private final ClassScheduleService classScheduleService;

    @Autowired
    public ClassScheduleController(ClassScheduleService classScheduleService) {
        this.classScheduleService = classScheduleService;
    }

    // --------------------
    // Get all schedules
    // --------------------
    @GetMapping
    public List<ClassSchedule> getAllSchedules() {
        return classScheduleService.getAllSchedules();
    }

    // --------------------
    // Create new schedule (IMPORTANT)
    // --------------------
    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ClassSchedule schedule) {
        try {
            ClassSchedule saved = classScheduleService.addSchedule(schedule);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --------------------
    // Delete schedule
    // --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        classScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
