CREATE TABLE IF NOT EXISTS user_role
(
user_id INT,
role_id INT,
PRIMARY KEY (user_id, role_id),
FOREIGN KEY (user_id) REFERENCES my_user (user_id) ON DELETE CASCADE,
FOREIGN KEY (role_id) REFERENCES role (role_id) ON DELETE CASCADE
);