package com.couse_enrollment_and_class_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.couse_enrollment_and_class_scheduling.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    
} 
