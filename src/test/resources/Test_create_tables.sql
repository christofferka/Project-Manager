DROP TABLE IF EXISTS cost_item;
DROP TABLE IF EXISTS subtask;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS subproject;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      email VARCHAR(100) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      role VARCHAR(20) NOT NULL
);

CREATE TABLE project (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         customer_name VARCHAR(100),
                         start_date DATE,
                         end_date DATE,
                         hourly_rate DECIMAL(10,2),
                         estimated_hours DECIMAL(10,2)
);

CREATE TABLE subproject (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            project_id INT NOT NULL,
                            name VARCHAR(100),
                            description VARCHAR(255),
                            CONSTRAINT fk_subproject_project
                                FOREIGN KEY (project_id) REFERENCES project(id)
);

CREATE TABLE task (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      subproject_id INT NOT NULL,
                      name VARCHAR(100),
                      description VARCHAR(255),
                      estimated_hours DECIMAL(10,2),
                      actual_hours DECIMAL(10,2),
                      rate DECIMAL(10,2),
                      user_id INT,
                      deadline DATE,
                      status VARCHAR(30),
                      CONSTRAINT fk_task_subproject
                          FOREIGN KEY (subproject_id) REFERENCES subproject(id),
                      CONSTRAINT fk_task_user
                          FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE subtask (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         task_id INT NOT NULL,
                         name VARCHAR(100),
                         description VARCHAR(255),
                         estimated_hours DECIMAL(10,2),
                         actual_hours DECIMAL(10,2),
                         rate DECIMAL(10,2),
                         user_id INT,
                         deadline DATE,
                         status VARCHAR(30),
                         CONSTRAINT fk_subtask_task
                             FOREIGN KEY (task_id) REFERENCES task(id),
                         CONSTRAINT fk_subtask_user
                             FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE cost_item (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           project_id INT NOT NULL,
                           name VARCHAR(100),
                           amount DECIMAL(10,2),
                           CONSTRAINT fk_cost_item_project
                               FOREIGN KEY (project_id) REFERENCES project(id)
);
