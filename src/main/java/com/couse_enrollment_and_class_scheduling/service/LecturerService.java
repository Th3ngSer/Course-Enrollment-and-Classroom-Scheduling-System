package com.couse_enrollment_and_class_scheduling.service;


import com.couse_enrollment_and_class_scheduling.entity.Lecturer;
//import com.course_enrollment_and_class_scheduling.repository.LecturerRepository;
import org.springframework.stereotype.Service;

import com.couse_enrollment_and_class_scheduling.repository.LecturerRepository;

import java.util.List;

@Service
public class LecturerService {

    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    // Create / Update
    public Lecturer saveLecturer(Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    // Read all
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    // Read by ID
    public Lecturer getLecturerById(Long id) {
        return lecturerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + id));
    }

    // Delete
    public void deleteLecturer(Long id) {
        lecturerRepository.deleteById(id);
    }
}
