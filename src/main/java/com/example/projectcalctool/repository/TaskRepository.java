package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.model.TaskStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class TaskRepository {

    // JdbcTemplate til databaseadgang
    private final JdbcTemplate jdbc;

    // Constructor injection
    public TaskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapper database-rækker til Task objekt
    private final RowMapper<Task> mapper = (rs, rowNum) -> {

        Task t = new Task();

        t.setId(rs.getInt("id"));
        t.setSubprojectId(rs.getInt("subproject_id"));
        t.setName(rs.getString("name"));
        t.setDescription(rs.getString("description"));

        t.setEstimatedHours(rs.getDouble("estimated_hours"));
        t.setActualHours(rs.getObject("actual_hours", Double.class));

        t.setRate(rs.getDouble("rate"));
        t.setUserId(rs.getObject("user_id", Integer.class));

        Date d = rs.getDate("deadline");
        t.setDeadline(d != null ? d.toLocalDate() : null);

        String statusStr = rs.getString("status");
        t.setStatus(statusStr != null
                ? TaskStatus.valueOf(statusStr)
                : TaskStatus.NOT_STARTED);

        return t;
    };

    // FIND METODER

    // Finder tasks for et delprojekt
    public List<Task> getTasksBySubprojectId(int subprojectId) {
        return jdbc.query(
                "SELECT * FROM task WHERE subproject_id = ? ORDER BY id",
                mapper,
                subprojectId
        );
    }

    // Finder tasks for et delprojekt
    public List<Task> findBySubprojectId(int subprojectId) {
        return jdbc.query(
                "SELECT * FROM task WHERE subproject_id = ? ORDER BY id",
                mapper,
                subprojectId
        );
    }

    // Finder task via id
    public Task findById(int id) {
        return jdbc.queryForObject(
                "SELECT * FROM task WHERE id = ?",
                mapper,
                id
        );
    }

    // Finder alle tasks
    public List<Task> findAll() {
        return jdbc.query(
                "SELECT * FROM task ORDER BY id",
                mapper
        );
    }

    // Tæller antal tasks
    public int count() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM task",
                Integer.class
        );
    }

    // CREATE

    // Opretter ny task
    public void create(Task t) {
        jdbc.update(
                """
                INSERT INTO task 
                (subproject_id, name, description, estimated_hours, actual_hours, rate, user_id, deadline, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,

                t.getSubprojectId(),
                t.getName(),
                t.getDescription(),
                t.getEstimatedHours(),
                t.getActualHours(),
                t.getRate(),
                t.getUserId(),
                t.getDeadline() != null ? Date.valueOf(t.getDeadline()) : null,
                t.getStatus().name()
        );
    }

    // UPDATE

    // Opdaterer eksisterende task
    public void update(Task t) {
        jdbc.update(
                """
                UPDATE task SET 
                subproject_id = ?, name = ?, description = ?, estimated_hours = ?, actual_hours = ?, 
                rate = ?, user_id = ?, deadline = ?, status = ?
                WHERE id = ?
                """,

                t.getSubprojectId(),
                t.getName(),
                t.getDescription(),
                t.getEstimatedHours(),
                t.getActualHours(),
                t.getRate(),
                t.getUserId(),
                t.getDeadline() != null ? Date.valueOf(t.getDeadline()) : null,
                t.getStatus().name(),
                t.getId()
        );
    }

    // DELETE

    // Sletter task
    public void delete(int id) {
        jdbc.update(
                "DELETE FROM task WHERE id = ?",
                id
        );
    }

    // TOTAL HOURS FOR PROJECT

    // Beregner samlede estimerede timer for et projekt
    public double getTotalHoursForProject(int projectId) {

        String sql = """
            SELECT COALESCE(SUM(t.estimated_hours), 0)
            FROM task t
            INNER JOIN subproject sp ON t.subproject_id = sp.id
            WHERE sp.project_id = ?
            """;

        return jdbc.queryForObject(
                sql,
                Double.class,
                projectId
        );
    }
}
