package com.sms.dao;

import com.sms.entity.Textbook;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TextbookDAO extends DAO {
    public List<Textbook> getAllTextbooks() {
        List<Textbook> textbooks = new ArrayList<>();
        String sql = "SELECT id, name, author, publish_year FROM textbooks ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) textbooks.add(mapTextbook(rs));
            return textbooks;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Textbook getById(int id) {
        String sql = "SELECT id, name, author, publish_year FROM textbooks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapTextbook(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Textbook mapTextbook(ResultSet rs) throws SQLException {
        return new Textbook(rs.getInt("id"), rs.getString("name"), rs.getString("author"), rs.getInt("publish_year"));
    }
}
