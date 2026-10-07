-- Demo skema til H2 (in-memory). Matcher produktions MySQL skema.

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    ai_provider VARCHAR(32),
    ai_api_key VARCHAR(255),
    ai_model VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS project (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    hourly_rate DECIMAL(10,2),
    estimated_hours DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS subproject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS task (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subproject_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    estimated_hours DECIMAL(10,2),
    actual_hours DECIMAL(10,2),
    rate DECIMAL(10,2) NOT NULL DEFAULT 0,
    user_id INT NULL,
    deadline DATE,
    status VARCHAR(50) DEFAULT 'NOT_STARTED',
    FOREIGN KEY (subproject_id) REFERENCES subproject(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS subtask (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    estimated_hours DECIMAL(10,2),
    actual_hours DECIMAL(10,2),
    rate DECIMAL(10,2) NOT NULL DEFAULT 0,
    user_id INT NULL,
    deadline DATE,
    status VARCHAR(50),
    FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS cost_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS share_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(50) NOT NULL,
    can_edit_project BOOLEAN DEFAULT FALSE,
    can_edit_subprojects BOOLEAN DEFAULT FALSE,
    can_edit_tasks BOOLEAN DEFAULT FALSE,
    can_edit_subtasks BOOLEAN DEFAULT FALSE,
    can_edit_costitems BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
