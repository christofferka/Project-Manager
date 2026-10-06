package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.Subproject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubprojectRepository {

    // JdbcTemplate til databaseadgang
    private final JdbcTemplate jdbc;

    // Constructor injection
    public SubprojectRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Mapper database-rækker til Subproject objekt
    private final RowMapper<Subproject> mapper = (rs, rowNum) ->
            new Subproject(
                    rs.getInt("id"),
                    rs.getInt("project_id"),
                    rs.getString("name"),
                    rs.getString("description")
            );

    // Finder alle delprojekter
    public List<Subproject> findAll() {
        return jdbc.query(
                "SELECT * FROM subproject",
                mapper
        );
    }

    // Finder delprojekter for et projekt
    public List<Subproject> findByProjectId(int projectId) {
        return jdbc.query(
                "SELECT * FROM subproject WHERE project_id = ?",
                mapper,
                projectId
        );
    }

    // Finder delprojekt via id
    public Subproject findById(int id) {
        return jdbc.queryForObject(
                "SELECT * FROM subproject WHERE id = ?",
                mapper,
                id
        );
    }

    // Opretter nyt delprojekt
    public void create(Subproject subproject) {
        jdbc.update(
                "INSERT INTO subproject (project_id, name, description) VALUES (?, ?, ?)",
                subproject.getProjectId(),
                subproject.getName(),
                subproject.getDescription()
        );
    }

    // Opdaterer eksisterende delprojekt
    public void update(Subproject subproject) {
        jdbc.update(
                "UPDATE subproject SET name = ?, description = ? WHERE id = ?",
                subproject.getName(),
                subproject.getDescription(),
                subproject.getId()
        );
    }

    // Sletter delprojekt
    public void delete(int id) {
        jdbc.update(
                "DELETE FROM subproject WHERE id = ?",
                id
        );
    }
}
