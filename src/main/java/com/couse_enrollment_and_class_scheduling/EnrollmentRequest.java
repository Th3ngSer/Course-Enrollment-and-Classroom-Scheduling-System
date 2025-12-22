package com.couse_enrollment_and_class_scheduling;
public class EnrollmentRequest {
    private Long studentId;
    private Long courseId;
    
    // Constructors
    public EnrollmentRequest() {}
    
    public EnrollmentRequest(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}
