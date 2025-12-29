package com.couse_enrollment_and_class_scheduling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.couse_enrollment_and_class_scheduling.entity.ClassSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    /**
     * CRITICAL: Detects scheduling conflicts for a classroom
     * A conflict exists when:
     * 1. Same classroom
     * 2. Same day of week
     * 3. Time ranges overlap
     */
    @Query("""
                SELECT cs FROM ClassSchedule cs
                WHERE cs.classroom.id = :classroomId
                AND cs.dayOfWeek = :dayOfWeek
                AND cs.startTime < :endTime
                AND cs.endTime > :startTime
            """)
    List<ClassSchedule> findConflicts(
            @Param("classroomId") Long classroomId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    /**
     * Get all schedules for a specific student
     * Joins through enrollments to find student's courses
     */
    @Query("""
                SELECT cs FROM ClassSchedule cs
                JOIN Enrollment e ON e.course.id = cs.course.id
                WHERE e.student.id = :studentId
                ORDER BY cs.dayOfWeek, cs.startTime
            """)
    List<ClassSchedule> findStudentSchedule(@Param("studentId") Long studentId);

    /**
     * Detects scheduling conflicts for a course
     * A conflict exists when:
     * 1. Same course
     * 2. Same day of week
     * 3. Time ranges overlap
     */
    @Query("""
                SELECT cs FROM ClassSchedule cs
                WHERE cs.course.id = :courseId
                AND cs.dayOfWeek = :dayOfWeek
                AND cs.startTime < :endTime
                AND cs.endTime > :startTime
            """)
    List<ClassSchedule> findCourseConflicts(
            @Param("courseId") Long courseId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    /**
     * Find all schedules for a specific course
     */
    List<ClassSchedule> findByCourseId(Long courseId);

}
