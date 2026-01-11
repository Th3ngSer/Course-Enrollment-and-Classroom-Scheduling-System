-- Seed a Lecturer user for demo/testing.
-- Username: teacher
-- Password: 987654321
--
-- Assumes application roles exist in the `roles` table (V2__Insert_Roles.sql).

-- 1) If teacher already exists, update password + ensure enabled + fill required fields
UPDATE users
SET
    password = '$2a$10$7RJc3BgPhSrn1..PP7cm/OWNl84N8QUfJhzC9gPNoieMGkrUnvpei',
    enabled = 1,
    email = COALESCE(email, 'teacher@test.com'),
    first_name = COALESCE(first_name, 'Teacher'),
    last_name = COALESCE(last_name, 'User')
WHERE username = 'teacher';

-- 2) If teacher doesn't exist, insert it
INSERT INTO users (username, password, email, first_name, last_name, enabled)
SELECT
    'teacher',
    '$2a$10$7RJc3BgPhSrn1..PP7cm/OWNl84N8QUfJhzC9gPNoieMGkrUnvpei',
    'teacher@test.com',
    'Teacher',
    'User',
    1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'teacher');

-- 3) Assign ROLE_LECTURER (idempotent)
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_LECTURER'
WHERE u.username = 'teacher';
