package com.couse_enrollment_and_class_scheduling.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.couse_enrollment_and_class_scheduling.entity.Lecturer;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    // No custom methods needed for basic CRUD
    Optional<Lecturer> findByUser_Id(Long userId);
    List<Lecturer> findByFullNameIgnoreCase(String fullName);
}
