package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.Subtask;
import com.example.projectcalctool.model.TaskStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class SubtaskRepository {

    private final JdbcTemplate jdbc;

    public SubtaskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Subtask> mapper = (rs, rowNum) -> {

        Subtask s = new Subtask();

        s.setId(rs.getInt("id"));
        s.setTaskId(rs.getInt("task_id"));
        s.setName(rs.getString("name"));
        s.setDescription(rs.getString("description"));

        s.setEstimatedHours(rs.getDouble("estimated_hours"));
        s.setActualHours(rs.getObject("actual_hours", Double.class));

        s.setRate(rs.getDouble("rate"));
        s.setUserId(rs.getObject("user_id", Integer.class));

        Date d = rs.getDate("deadline");
        s.setDeadline(d != null ? d.toLocalDate() : null);

        String statusStr = rs.getString("status");
        s.setStatus(statusStr != null
                ? TaskStatus.valueOf(statusStr)
                : TaskStatus.NOT_STARTED);

        // ⭐ DET VIGTIGE
        s.setEmployeeName(rs.getString("employee_name"));

        return s;
    };

    // ============================
    // FIND
    // ============================

    public List<Subtask> findByTaskId(int taskId) {
        return jdbc.query(
                """
                SELECT s.*, u.username AS employee_name
                FROM subtask s
                LEFT JOIN user u ON s.user_id = u.id
                WHERE s.task_id = ?
                ORDER BY s.id
                """,
                mapper,
                taskId
        );
    }

    public Subtask findById(int id) {
        return jdbc.queryForObject(
                """
                SELECT s.*, u.username AS employee_name
                FROM subtask s
                LEFT JOIN user u ON s.user_id = u.id
                WHERE s.id = ?
                """,
                mapper,
                id
        );
    }

    // ============================
    // CREATE
    // ============================

    public void create(Subtask s) {

        TaskStatus status =
                s.getStatus() != null ? s.getStatus() : TaskStatus.NOT_STARTED;

        jdbc.update(
                """
                INSERT INTO subtask
                (task_id, name, description, estimated_hours, actual_hours,
                 rate, user_id, deadline, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                s.getTaskId(),
                s.getName(),
                s.getDescription(),
                s.getEstimatedHours(),
                s.getActualHours(),
                s.getRate(),
                s.getUserId(),
                s.getDeadline() != null ? Date.valueOf(s.getDeadline()) : null,
                status.name()
        );
    }

    // ============================
    // UPDATE
    // ============================

    public void update(Subtask s) {

        TaskStatus status =
                s.getStatus() != null ? s.getStatus() : TaskStatus.NOT_STARTED;

        jdbc.update(
                """
                UPDATE subtask
                SET task_id = ?, name = ?, description = ?, estimated_hours = ?,
                    actual_hours = ?, rate = ?, user_id = ?, deadline = ?, status = ?
                WHERE id = ?
                """,
                s.getTaskId(),
                s.getName(),
                s.getDescription(),
                s.getEstimatedHours(),
                s.getActualHours(),
                s.getRate(),
                s.getUserId(),
                s.getDeadline() != null ? Date.valueOf(s.getDeadline()) : null,
                status.name(),
                s.getId()
        );
    }

    // ============================
    // DELETE
    // ============================

    public void delete(int id) {
        jdbc.update("DELETE FROM subtask WHERE id = ?", id);
    }
}
