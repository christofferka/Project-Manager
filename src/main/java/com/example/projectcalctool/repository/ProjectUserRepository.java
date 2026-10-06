package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.model.ProjectRole;
import com.example.projectcalctool.model.ProjectUser;
import com.example.projectcalctool.utils.DBUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectUserRepository {

    // Opretter relation mellem bruger og projekt
    public void create(ProjectUser pu) {
        String sql = """
            INSERT INTO project_user
            (project_id, user_id, role,
             can_edit_project, can_edit_subprojects,
             can_edit_tasks, can_edit_subtasks, can_edit_costitems)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pu.getProjectId());
            stmt.setInt(2, pu.getUserId());
            stmt.setString(3, pu.getRole().name());
            stmt.setBoolean(4, pu.isCanEditProject());
            stmt.setBoolean(5, pu.isCanEditSubprojects());
            stmt.setBoolean(6, pu.isCanEditTasks());
            stmt.setBoolean(7, pu.isCanEditSubtasks());
            stmt.setBoolean(8, pu.isCanEditCostitems());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Opdaterer eksisterende projektbruger
    public void update(ProjectUser pu) {
        String sql = """
            UPDATE project_user
            SET role = ?,
                can_edit_project = ?,
                can_edit_subprojects = ?,
                can_edit_tasks = ?,
                can_edit_subtasks = ?,
                can_edit_costitems = ?
            WHERE id = ?
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pu.getRole().name());
            stmt.setBoolean(2, pu.isCanEditProject());
            stmt.setBoolean(3, pu.isCanEditSubprojects());
            stmt.setBoolean(4, pu.isCanEditTasks());
            stmt.setBoolean(5, pu.isCanEditSubtasks());
            stmt.setBoolean(6, pu.isCanEditCostitems());
            stmt.setInt(7, pu.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Finder relation mellem projekt og bruger
    public ProjectUser findByProjectAndUser(int projectId, int userId) {
        String sql = "SELECT * FROM project_user WHERE project_id = ? AND user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapProjectUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Finder alle brugere i et projekt
    public List<ProjectUser> findByProjectId(int projectId) {
        List<ProjectUser> result = new ArrayList<>();
        String sql = "SELECT * FROM project_user WHERE project_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(mapProjectUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Fjerner bruger fra projekt
    public void deleteByProjectAndUser(int projectId, int userId) {
        String sql = "DELETE FROM project_user WHERE project_id = ? AND user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sletter alle projektrelationer for en bruger
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM project_user WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Finder alle projekter for en bruger
    public List<Project> findProjectsByUserId(int userId) {

        String sql = """
            SELECT p.*
            FROM project p
            JOIN project_user pu ON p.id = pu.project_id
            WHERE pu.user_id = ?
            """;

        List<Project> result = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapProject(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Mapper database-række til ProjectUser
    private ProjectUser mapProjectUser(ResultSet rs) throws SQLException {
        return new ProjectUser(
                rs.getInt("id"),
                rs.getInt("project_id"),
                rs.getInt("user_id"),
                ProjectRole.valueOf(rs.getString("role")),
                rs.getBoolean("can_edit_project"),
                rs.getBoolean("can_edit_subprojects"),
                rs.getBoolean("can_edit_tasks"),
                rs.getBoolean("can_edit_subtasks"),
                rs.getBoolean("can_edit_costitems")
        );
    }

    // Mapper database-række til Project
    private Project mapProject(ResultSet rs) throws SQLException {

        LocalDate start = null;
        if (rs.getDate("start_date") != null) {
            start = rs.getDate("start_date").toLocalDate();
        }

        LocalDate end = null;
        if (rs.getDate("end_date") != null) {
            end = rs.getDate("end_date").toLocalDate();
        }

        return new Project(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("customer_name"),
                start,
                end,
                rs.getDouble("hourly_rate"),
                rs.getDouble("estimated_hours")
        );
    }
}
