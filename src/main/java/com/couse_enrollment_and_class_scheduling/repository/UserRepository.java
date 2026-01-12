package com.couse_enrollment_and_class_scheduling.repository;

import com.couse_enrollment_and_class_scheduling.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);  // Keep if needed for other purposes
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

        @Query("""
                        select count(distinct u)
                        from User u
                        join u.roles r
                        where u.enabled = true
                            and r.name = :roleName
                        """)
        long countEnabledUsersByRole(@Param("roleName") String roleName);

        @Query("""
                        select count(distinct u)
                        from User u
                        where u.enabled = true
                            and exists (select 1 from u.roles r where r.name = 'ROLE_LECTURER')
                            and not exists (select 1 from u.roles r2 where r2.name = 'ROLE_ADMIN')
                        """)
        long countEnabledLecturersNonAdmin();

        @Query("""
                        select count(distinct u)
                        from User u
                        where u.enabled = true
                            and exists (select 1 from u.roles r where r.name = 'ROLE_STUDENT')
                            and not exists (select 1 from u.roles r2 where r2.name in ('ROLE_ADMIN', 'ROLE_LECTURER'))
                        """)
        long countEnabledStudentsOnly();
}