package com.couse_enrollment_and_class_scheduling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "classrooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = "room_number")
})
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    @NotBlank(message = "Building is required")
    @Column(length = 50, nullable = false)
    private String building;

    @NotNull(message = "Max capacity is required")
    @Min(value = 1, message = "Max capacity must be at least 1")
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    // Constructors
    public Classroom() {
    }

    public Classroom(String roomNumber, String building, Integer maxCapacity) {
        this.roomNumber = roomNumber;
        this.building = building;
        this.maxCapacity = maxCapacity;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }


    // Optional: toString for debugging
    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", roomNumber='" + roomNumber + '\'' +
                ", building='" + building + '\'' +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
