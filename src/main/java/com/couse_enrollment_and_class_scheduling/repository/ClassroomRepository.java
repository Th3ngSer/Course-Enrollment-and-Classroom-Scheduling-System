package main.java.com.couse_enrollment_and_class_scheduling.repository;

import com.couse_enrollment_and_class_scheduling.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    // Find a classroom by room number
    Optional<Classroom> findByRoomNumber(String roomNumber);

    // Check if a classroom with this room number exists
    boolean existsByRoomNumber(String roomNumber);
}
