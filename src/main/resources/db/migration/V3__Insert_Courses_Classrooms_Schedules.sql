-- 1. Insert Lecturers first
INSERT INTO lecturers (id, full_name, department, office_hours)
VALUES 
(1, 'Dr. Alice Smith', 'Computer Science', 'Mon 10:00-12:00'),
(2, 'Dr. Bob Johnson', 'Information Technology', 'Tue 14:00-16:00');

-- 2. Insert Classrooms
INSERT INTO classrooms (id, room_number, building, max_capacity)
VALUES
(1, 'Room 304', 'Building A', 40),
(2, 'Room 305', 'Building A', 30);

-- 3. Insert Courses (Linked to Lecturers and Classrooms)
-- Note: We include lecturer_id and classroom_id here so course.lecturer.fullName works!
INSERT INTO courses (id, course_code, course_name, credits, capacity, lecturer_id, classroom_id)
VALUES
(1, 'CS301', 'Database Systems', 3, 30, 1, 1),
(2, 'CS302', 'Operating Systems', 3, 2, 2, 2);

-- 4. Insert Schedules (For the calendar/time view)
INSERT INTO class_schedules (course_id, classroom_id, day_of_week, start_time, end_time)
VALUES
(1, 1, 'MONDAY', '09:00:00', '11:00:00'),
(2, 2, 'MONDAY', '11:00:00', '13:00:00');

-- 5. Insert a Test Student (so you can test enrollment)
INSERT INTO users (id, username, password, email, full_name, enabled)
VALUES 
(1, 'student1', '$2a$10$dXJ3SW6G7P50lGekeFaboe3QwvGz7z0yENRKSLYSlZhsFhQH.vA7u', 'john@test.com', 'John Doe', 1),
(2, 'student2', '$2a$10$dXJ3SW6G7P50lGekeFaboe3QwvGz7z0yENRKSLYSlZhsFhQH.vA7u', 'jane@test.com', 'Jane Smith', 1);