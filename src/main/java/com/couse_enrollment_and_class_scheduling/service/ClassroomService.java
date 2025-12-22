package com.couse_enrollment_and_class_scheduling.service;

import com.couse_enrollment_and_class_scheduling.entity.Classroom;
import com.couse_enrollment_and_class_scheduling.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    // --------------------
    // List all classrooms
    // --------------------
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    // --------------------
    // Find classroom by ID
    // --------------------
    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    // --------------------
    // Add new classroom
    // --------------------
    public Classroom addClassroom(Classroom classroom) {
        // Validation: roomNumber must be unique
        if (classroomRepository.existsByRoomNumber(classroom.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + classroom.getRoomNumber());
        }

        // Validation: maxCapacity must be positive
        if (classroom.getMaxCapacity() <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0");
        }

        return classroomRepository.save(classroom);
    }

    // --------------------
    // Update existing classroom
    // --------------------
    public Classroom updateClassroom(Long id, Classroom updatedClassroom) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found with id: " + id));

        // Validate if roomNumber changed
        if (!classroom.getRoomNumber().equals(updatedClassroom.getRoomNumber())
                && classroomRepository.existsByRoomNumber(updatedClassroom.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + updatedClassroom.getRoomNumber());
        }

        // Update fields
        classroom.setRoomNumber(updatedClassroom.getRoomNumber());
        classroom.setBuilding(updatedClassroom.getBuilding());
        classroom.setMaxCapacity(updatedClassroom.getMaxCapacity());

        return classroomRepository.save(classroom);
    }

    // --------------------
    // Delete classroom
    // --------------------
    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }
}
