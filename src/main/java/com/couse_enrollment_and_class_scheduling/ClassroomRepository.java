package com.couse_enrollment_and_class_scheduling;

import com.couse_enrollment_and_class_scheduling.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
} 