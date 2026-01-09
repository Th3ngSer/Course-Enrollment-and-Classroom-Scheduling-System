-- Insert an Admin user (password is 'password')
-- Idempotent: safe if the user already exists.
INSERT INTO users (username, password, email, full_name, enabled)
VALUES (
	'admin',
	'$2a$10$dXJ3SW6G7P50lGekeFaboe3QwvGz7z0yENRKSLYSlZhsFhQH.vA7u',
	'admin@test.com',
	'Admin User',
	1
) AS new
ON DUPLICATE KEY UPDATE
	password = new.password,
	email = new.email,
	full_name = new.full_name,
	enabled = new.enabled;

-- Give them the Admin Role (also idempotent)
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.username = 'admin';