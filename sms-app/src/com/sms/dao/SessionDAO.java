package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public List<Session> getAllSession() {
        List<Session> out = new ArrayList<>();
        String sql = "SELECT id, day_of_week, start_period, end_period, room FROM sessions ORDER BY id";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllSession failed", e);
        }
        return out;
    }

    public Session getById(int id) {
        String sql = "SELECT id, day_of_week, start_period, end_period, room FROM sessions WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById session failed", e);
        }
    }

    static Session map(ResultSet rs) throws SQLException {
        return new Session(rs.getInt("id"), rs.getString("day_of_week"),
                rs.getInt("start_period"), rs.getInt("end_period"), rs.getString("room"));
    }
}
