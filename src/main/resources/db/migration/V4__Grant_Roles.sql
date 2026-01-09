-- NOTE: These are *database server* roles (MySQL 8+), not application roles in the `roles` table.
-- This migration can fail if:
-- - Your MySQL version doesn't support roles (e.g. MySQL 5.x), or
-- - The Flyway DB user doesn't have privilege to CREATE ROLE / GRANT.
-- It's written to be idempotent if you recreate schema locally.

CREATE ROLE IF NOT EXISTS 'admin';
CREATE ROLE IF NOT EXISTS 'lecturer';
CREATE ROLE IF NOT EXISTS 'student';

GRANT ALL PRIVILEGES ON course_enrollment_and_class_scheduling.* TO 'admin';

GRANT ALL PRIVILEGES ON course_enrollment_and_class_scheduling.courses TO 'lecturer';
GRANT ALL PRIVILEGES ON course_enrollment_and_class_scheduling.student_list TO 'lecturer';

GRANT SELECT ON course_enrollment_and_class_scheduling.courses TO 'student';
GRANT SELECT, INSERT ON course_enrollment_and_class_scheduling.enrollments TO 'student';