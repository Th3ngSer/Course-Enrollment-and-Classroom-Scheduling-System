package com.couse_enrollment_and_class_scheduling.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.couse_enrollment_and_class_scheduling.entity.Lecturer;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    // No custom methods needed for basic CRUD
}
