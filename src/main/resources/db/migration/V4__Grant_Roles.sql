CREATE ROLE 'admin';
CREATE ROLE 'lecturer';
CREATE ROLE 'student';

grant all on course_enrollment_and_class_scheduling.* to 'admin';

grant all on course_enrollment_and_class_scheduling.courses to 'lecturer';

grant all on course_enrollment_and_class_scheduling.student_list to 'lecturer';

grant select on course_enrollment_and_class_scheduling.courses to 'student';

grant select, insert on course_enrollment_and_class_scheduling.enrollments to 'student';