-- Demo data til screenshots.

INSERT INTO user (username, email, password, role) VALUES
    ('demo',  'demo@example.com',  'demo', 'ADMIN'),
    ('anna',  'anna@example.com',  'demo', 'USER'),
    ('bo',    'bo@example.com',    'demo', 'USER'),
    ('clara', 'clara@example.com', 'demo', 'USER');

INSERT INTO project (name, customer_name, start_date, end_date, hourly_rate, estimated_hours) VALUES
    ('Website Redesign',   'Alpha Solutions', '2026-08-15', '2026-12-20', 850.00, 240),
    ('CRM Integration',    'TechCorp',        '2026-09-01', '2027-02-15', 900.00, 380),
    ('Mobile App MVP',     'NordicApps',      '2026-10-01', '2027-05-30', 950.00, 520);

INSERT INTO subproject (project_id, name, description) VALUES
    (1, 'Frontend',           'Redesign af UI og ny komponentbibliotek'),
    (1, 'Backend',            'Refaktorering og API optimering'),
    (2, 'API Layer',          'Integration mod eksterne CRM systemer'),
    (2, 'Database Migration', 'Flytning af data til nyt skema'),
    (3, 'iOS',                'Native iOS klient'),
    (3, 'Android',            'Native Android klient');

INSERT INTO task (subproject_id, name, description, estimated_hours, actual_hours, rate, user_id, deadline, status) VALUES
    (1, 'UI Wireframes',     'Skitser og mockups af nye sider', 20, 12, 850, 2, '2026-02-01', 'IN_PROGRESS'),
    (1, 'Design System',     'Farver, typografi og komponenter', 40, 15, 850, 2, '2026-02-20', 'IN_PROGRESS'),
    (1, 'Component Rewrite', 'Rewrite af kernekomponenter',      60,  0, 850, 3, '2026-03-15', 'NOT_STARTED'),
    (2, 'Service Cleanup',   'Fjern deprecated services',        30, 30, 900, 3, '2026-02-10', 'DONE'),
    (2, 'Caching Layer',     'Redis cache foran API',            25,  8, 900, 4, '2026-03-01', 'IN_PROGRESS'),
    (3, 'Auth API',          'OAuth2 baseret login',             50, 20, 900, 2, '2026-03-20', 'IN_PROGRESS'),
    (3, 'Contact Sync',      'Synk af kontakter fra CRM',        35,  0, 900, 4, '2026-04-05', 'NOT_STARTED'),
    (4, 'Migration Script',  'Byg migrationsscripts',            60,  0, 900, 3, '2026-05-01', 'NOT_STARTED'),
    (5, 'Login Screen',      'Login flow til iOS',               15,  4, 950, 2, '2026-04-01', 'IN_PROGRESS'),
    (6, 'Push Notifications','Firebase integration',             20,  0, 950, 4, '2026-05-15', 'NOT_STARTED');

INSERT INTO subtask (task_id, name, description, estimated_hours, actual_hours, rate, user_id, deadline, status) VALUES
    (1, 'Header Layout',     'Skitser til header',        5, 3, 850, 2, '2026-01-25', 'IN_PROGRESS'),
    (1, 'Dashboard Layout',  'Skitser til dashboard',     8, 4, 850, 2, '2026-02-01', 'IN_PROGRESS'),
    (2, 'Color Tokens',      'Definér farvepalette',      6, 6, 850, 2, '2026-02-05', 'DONE'),
    (4, 'Remove UserLegacy', 'Slet legacy user service',  4, 4, 900, 3, '2026-02-05', 'DONE'),
    (6, 'Token Validator',   'JWT validator',             6, 2, 900, 2, '2026-03-10', 'IN_PROGRESS'),
    (6, 'Refresh Endpoint',  'Endpoint til refresh',      4, 0, 900, 2, '2026-03-18', 'NOT_STARTED');

INSERT INTO cost_item (project_id, name, quantity, unit_price) VALUES
    (1, 'Figma License',       3,  400),
    (1, 'External UX Review',  1, 4500),
    (1, 'Stock Photos',       20,   40),
    (2, 'Cloud Hosting',      12,  500),
    (2, 'Consultant Fees',    10,  850),
    (3, 'Apple Developer',     1,  900),
    (3, 'Play Store Fee',      1,  200),
    (3, 'Device Test Lab',     4,  800);

-- Demo bruger er global ADMIN og har fuldt tilstand for alle projekter,
-- men vi tilføjer også eksplicitte project_user relationer så member-oversigter viser data.
INSERT INTO project_user (project_id, user_id, role, can_edit_project, can_edit_subprojects, can_edit_tasks, can_edit_subtasks, can_edit_costitems) VALUES
    (1, 1, 'PROJECT_ADMIN', TRUE,  TRUE,  TRUE,  TRUE,  TRUE),
    (1, 2, 'EDITOR',        TRUE,  TRUE,  TRUE,  TRUE,  TRUE),
    (1, 3, 'VIEWER',        FALSE, FALSE, FALSE, FALSE, FALSE),
    (2, 1, 'PROJECT_ADMIN', TRUE,  TRUE,  TRUE,  TRUE,  TRUE),
    (2, 3, 'EDITOR',        TRUE,  TRUE,  TRUE,  TRUE,  TRUE),
    (2, 4, 'EDITOR',        TRUE,  TRUE,  TRUE,  TRUE,  TRUE),
    (3, 1, 'PROJECT_ADMIN', TRUE,  TRUE,  TRUE,  TRUE,  TRUE),
    (3, 2, 'EDITOR',        TRUE,  TRUE,  TRUE,  TRUE,  TRUE),
    (3, 4, 'VIEWER',        FALSE, FALSE, FALSE, FALSE, FALSE);
