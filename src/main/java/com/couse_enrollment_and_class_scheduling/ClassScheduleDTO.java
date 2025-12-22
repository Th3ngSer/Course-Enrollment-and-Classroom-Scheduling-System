package com.couse_enrollment_and_class_scheduling;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class ClassScheduleDTO {
    private Long courseId;
    private Long classroomId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    
    // Constructors
    public ClassScheduleDTO() {}
    
    public ClassScheduleDTO(Long courseId, Long classroomId, DayOfWeek dayOfWeek, 
                           LocalTime startTime, LocalTime endTime) {
        this.courseId = courseId;
        this.classroomId = classroomId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
    
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
