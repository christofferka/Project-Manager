package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.utils.DBUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    public User findById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void create(User user) {
        String sql = "INSERT INTO user (username, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(User user) {
        String sql = "UPDATE user SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Gemmer AI-indstillinger separat (vi vil ikke altid roere password)
    public void updateAiSettings(int userId, String provider, String apiKey, String model) {
        String sql = "UPDATE user SET ai_provider = ?, ai_api_key = ?, ai_model = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, provider);
            stmt.setString(2, apiKey);
            stmt.setString(3, model);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int userId) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean isUserTableEmpty() {
        String sql = "SELECT COUNT(*) FROM user";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("role")
        );
        // AI felter kan vaere null i gamle rows
        try {
            u.setAiProvider(rs.getString("ai_provider"));
            u.setAiApiKey(rs.getString("ai_api_key"));
            u.setAiModel(rs.getString("ai_model"));
        } catch (SQLException ignored) {
            // Felterne findes maaske ikke i aeldre skemaer; vi ignorerer stille
        }
        return u;
    }
}
