package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.CostItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CostItemRepository {

    // JdbcTemplate til databaseadgang
    private final JdbcTemplate jdbcTemplate;

    // Constructor injection
    public CostItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Mapper database-rækker til CostItem objekt
    private RowMapper<CostItem> mapper = (rs, rowNum) -> {
        CostItem ci = new CostItem();
        ci.setId(rs.getInt("id"));
        ci.setProjectId(rs.getInt("project_id"));
        ci.setName(rs.getString("name"));
        ci.setQuantity(rs.getDouble("quantity"));
        ci.setUnitPrice(rs.getDouble("unit_price"));
        return ci;
    };

    // Finder alle cost items for et projekt
    public List<CostItem> findByProjectId(int projectId) {
        String sql = "SELECT * FROM cost_item WHERE project_id = ?";
        return jdbcTemplate.query(sql, mapper, projectId);
    }

    // Opretter nyt cost item
    public void create(CostItem item) {
        String sql = "INSERT INTO cost_item (project_id, name, quantity, unit_price) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                item.getProjectId(),
                item.getName(),
                item.getQuantity(),
                item.getUnitPrice());
    }

    // Finder cost item via id
    public CostItem findById(int id) {
        String sql = "SELECT * FROM cost_item WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, mapper, id);
    }

    // Opdaterer eksisterende cost item
    public void update(CostItem item) {
        String sql = "UPDATE cost_item SET name = ?, quantity = ?, unit_price = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                item.getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getId());
    }

    // Sletter cost item
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM cost_item WHERE id = ?", id);
    }
}
