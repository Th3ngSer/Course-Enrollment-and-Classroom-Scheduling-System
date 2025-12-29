package com.couse_enrollment_and_class_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "courses", uniqueConstraints = {
        @UniqueConstraint(columnNames = "course_code")
})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course code is required")
    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Column(nullable = false)
    private Integer credits;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;
    
    @Formula("(SELECT COUNT(*) FROM enrollments e WHERE e.course_id = id)")
    private Integer currentStudents;

    // Constructors
    public Course() {
    }

    public Course(String courseCode, String courseName, Integer credits,
            String description, Integer capacity) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.description = description;
        this.capacity = capacity;
    }

    // Updated Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Lecturer getLecturer() { return lecturer; }
    public void setLecturer(Lecturer lecturer) { this.lecturer = lecturer; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public Integer getCurrentStudents() { 
        return currentStudents != null ? currentStudents : 0; 
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", capacity=" + capacity +
                '}';
    }
}
