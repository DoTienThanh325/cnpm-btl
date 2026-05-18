package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.Textbook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TextbookDAO {

    public List<Textbook> getAllTextbooks() {
        List<Textbook> out = new ArrayList<>();
        String sql = "SELECT id, name, author, year FROM textbooks ORDER BY id";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllTextbooks failed", e);
        }
        return out;
    }

    public Textbook getById(int id) {
        String sql = "SELECT id, name, author, year FROM textbooks WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById textbook failed", e);
        }
    }

    static Textbook map(ResultSet rs) throws SQLException {
        return new Textbook(rs.getInt("id"), rs.getString("name"),
                rs.getString("author"), rs.getInt("year"));
    }

    static Textbook mapPrefixed(ResultSet rs) throws SQLException {
        return new Textbook(rs.getInt("tb_id"), rs.getString("tb_name"),
                rs.getString("tb_author"), rs.getInt("tb_year"));
    }
}
