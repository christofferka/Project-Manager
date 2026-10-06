INSERT INTO user (username, email, password, role) VALUES
                                                       ('admin', 'admin@example.com', 'hashed', 'ADMIN'),
                                                       ('john', 'john@example.com', 'hashed', 'USER');

INSERT INTO project (name, customer_name, hourly_rate, estimated_hours) VALUES
    ('Test Project', 'Test Customer', 800.00, 100);

INSERT INTO subproject (project_id, name, description) VALUES
    (1, 'Test Subproject', 'Subproject for testing');

INSERT INTO task (subproject_id, name, description, estimated_hours, rate, status) VALUES
    (1, 'Test Task', 'Task for testing', 10, 700, 'NOT_STARTED');
