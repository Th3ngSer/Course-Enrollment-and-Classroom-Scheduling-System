package com.couse_enrollment_and_class_scheduling.service;

import com.couse_enrollment_and_class_scheduling.entity.ClassSchedule;
import com.couse_enrollment_and_class_scheduling.ClassScheduleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;

    @Autowired
    public ClassScheduleService(ClassScheduleRepository classScheduleRepository) {
        this.classScheduleRepository = classScheduleRepository;
    }

    // --------------------
    // List all schedules
    // --------------------
    public List<ClassSchedule> getAllSchedules() {
        return classScheduleRepository.findAll();
    }

    // --------------------
    // Find schedule by ID
    // --------------------
    public Optional<ClassSchedule> getScheduleById(Long id) {
        return classScheduleRepository.findById(id);
    }

    // --------------------
    // Add new schedule (WITH VALIDATION)
    // --------------------
    @Transactional
    public ClassSchedule addSchedule(ClassSchedule schedule) {

        // Validation 1: Classroom double booking
        List<ClassSchedule> classroomConflicts = classScheduleRepository.findConflicts(
                schedule.getClassroom().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime());

        if (!classroomConflicts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Classroom is already booked during this time");
        }

        // Validation 2: Course double scheduling 
        List<ClassSchedule> courseConflicts = classScheduleRepository.findCourseConflicts(
                schedule.getCourse().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime());

        if (!courseConflicts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Course already has a schedule during this time");
        }

        return classScheduleRepository.save(schedule);
    }

    // --------------------
    // Update existing schedule
    // --------------------
    @Transactional
    public ClassSchedule updateSchedule(Long id, ClassSchedule updatedSchedule) {

        ClassSchedule existing = classScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + id));

        // Re-validate classroom conflict (exclude current schedule)
        List<ClassSchedule> classroomConflicts = classScheduleRepository.findConflicts(
                updatedSchedule.getClassroom().getId(),
                updatedSchedule.getDayOfWeek(),
                updatedSchedule.getStartTime(),
                updatedSchedule.getEndTime());

        classroomConflicts.removeIf(cs -> cs.getId().equals(id));

        if (!classroomConflicts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Classroom is already booked during this time");
        }

        // Update fields
        existing.setCourse(updatedSchedule.getCourse());
        existing.setClassroom(updatedSchedule.getClassroom());
        existing.setDayOfWeek(updatedSchedule.getDayOfWeek());
        existing.setStartTime(updatedSchedule.getStartTime());
        existing.setEndTime(updatedSchedule.getEndTime());

        return classScheduleRepository.save(existing);
    }

    // --------------------
    // Delete schedule
    // --------------------
    public void deleteSchedule(Long id) {
        classScheduleRepository.deleteById(id);
    }
}
