package com.couse_enrollment_and_class_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "lecturers")
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "Department is required")
    @Column(length = 50, nullable = false)
    private String department;

    @Column(name = "office_hours", length = 100)
    private String officeHours;

    // --------------------
    // Constructors
    // --------------------
    public Lecturer() {
    }

    public Lecturer(String fullName, String department, String officeHours) {
        this.fullName = fullName;
        this.department = department;
        this.officeHours = officeHours;
    }

    // --------------------
    // Getters & Setters
    // --------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

    // --------------------
    // Optional: toString for debugging
    // --------------------
    @Override
    public String toString() {
        return "Lecturer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", department='" + department + '\'' +
                ", officeHours='" + officeHours + '\'' +
                '}';
    }
}
