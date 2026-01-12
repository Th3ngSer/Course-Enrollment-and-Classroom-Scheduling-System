package com.couse_enrollment_and_class_scheduling.service;


import com.couse_enrollment_and_class_scheduling.entity.Lecturer;
//import com.course_enrollment_and_class_scheduling.repository.LecturerRepository;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import com.couse_enrollment_and_class_scheduling.repository.LecturerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LecturerService {

    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    // Create / Update
    public Lecturer saveLecturer(@NonNull Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    // Read all
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    // Read by ID
    public Lecturer getLecturerById(@NonNull Long id) {
        return lecturerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + id));
    }

    public Optional<Lecturer> findByUserId(@NonNull Long userId) {
        return lecturerRepository.findByUser_Id(userId);
    }

    public Optional<Lecturer> findUnlinkedByFullName(@NonNull String fullName) {
        return lecturerRepository.findByFullNameIgnoreCase(fullName).stream()
                .filter(l -> l.getUser() == null)
                .findFirst();
    }

    // Delete
    public void deleteLecturer(@NonNull Long id) {
        lecturerRepository.deleteById(id);
    }
}
