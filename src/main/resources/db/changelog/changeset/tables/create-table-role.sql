CREATE TABLE IF NOT EXISTS role
(
role_id INT PRIMARY KEY,
role_name VARCHAR(63) NOT NULL
);

INSERT INTO role (role_id, role_name) VALUES
(1, 'ADMIN'),
(2, 'USER');