package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ProjectRepository {

    // JdbcTemplate til databaseadgang
    private final JdbcTemplate jdbc;

    // Constructor injection
    public ProjectRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapper database-rækker til Project objekt
    private final RowMapper<Project> mapper = (rs, rowNum) -> {

        // Henter datoer fra databasen
        Date start = rs.getDate("start_date");
        Date end = rs.getDate("end_date");

        return new Project(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("customer_name"),
                start != null ? start.toLocalDate() : null,
                end != null ? end.toLocalDate() : null,
                rs.getDouble("hourly_rate"),
                rs.getDouble("estimated_hours")
        );
    };

    // Finder alle projekter
    public List<Project> findAll() {
        return jdbc.query("SELECT * FROM project", mapper);
    }

    // Finder projekt via id
    public Project findById(int id) {
        List<Project> results = jdbc.query(
                "SELECT * FROM project WHERE id = ?",
                mapper,
                id
        );
        return results.isEmpty() ? null : results.get(0);
    }

    // Opretter nyt projekt og returnerer genereret id
    public int create(Project p) {

        String sql = """
                INSERT INTO project
                (name, customer_name, start_date, end_date, hourly_rate, estimated_hours)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        // Holder til genereret primærnøgle
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getName());
            ps.setString(2, p.getCustomerName());
            ps.setDate(3, p.getStartDate() != null ? Date.valueOf(p.getStartDate()) : null);
            ps.setDate(4, p.getEndDate() != null ? Date.valueOf(p.getEndDate()) : null);
            ps.setDouble(5, p.getHourlyRate() != null ? p.getHourlyRate() : 0);
            ps.setDouble(6, p.getEstimatedHours() != null ? p.getEstimatedHours() : 0);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // Opdaterer eksisterende projekt
    public void update(Project p) {
        String sql = """
                UPDATE project SET
                name = ?, customer_name = ?, start_date = ?, end_date = ?, hourly_rate = ?, estimated_hours = ?
                WHERE id = ?
                """;

        jdbc.update(sql,
                p.getName(),
                p.getCustomerName(),
                p.getStartDate() != null ? Date.valueOf(p.getStartDate()) : null,
                p.getEndDate() != null ? Date.valueOf(p.getEndDate()) : null,
                p.getHourlyRate(),
                p.getEstimatedHours(),
                p.getId()
        );
    }

    // Sletter projekt
    public void delete(int id) {
        jdbc.update("DELETE FROM project WHERE id = ?", id);
    }
}
