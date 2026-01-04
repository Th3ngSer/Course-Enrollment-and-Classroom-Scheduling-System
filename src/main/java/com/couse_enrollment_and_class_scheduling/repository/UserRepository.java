package com.couse_enrollment_and_class_scheduling.repository;

import com.couse_enrollment_and_class_scheduling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);  // Keep if needed for other purposes
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}