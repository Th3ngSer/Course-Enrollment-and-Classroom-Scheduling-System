-- Ensure a working admin login for demo/testing.
-- Username: admin
-- Password: 987654321
--
-- NOTE: The database schema may have been updated by Hibernate (ddl-auto=update).
-- Some environments require first_name/last_name to be non-null.

-- 1) If admin already exists, just update password (and fill required fields if needed)
UPDATE users
SET
	password = '$2a$10$7RJc3BgPhSrn1..PP7cm/OWNl84N8QUfJhzC9gPNoieMGkrUnvpei',
	enabled = 1,
	email = COALESCE(email, 'admin@test.com'),
	first_name = COALESCE(first_name, 'Admin'),
	last_name = COALESCE(last_name, 'User')
WHERE username = 'admin';

-- 2) If admin doesn't exist, insert it with required columns
INSERT INTO users (username, password, email, first_name, last_name, enabled)
SELECT
	'admin',
	'$2a$10$7RJc3BgPhSrn1..PP7cm/OWNl84N8QUfJhzC9gPNoieMGkrUnvpei',
	'admin@test.com',
	'Admin',
	'User',
	1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Give them the Admin Role (also idempotent)
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.username = 'admin';
