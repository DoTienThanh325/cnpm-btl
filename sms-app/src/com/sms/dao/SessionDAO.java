package com.sms.dao;

import com.sms.entity.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO extends DAO {
    public List<Session> getAllSession() {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT id, day_of_week, start_period, end_period, room FROM sessions ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) sessions.add(mapSession(rs));
            return sessions;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Session getById(int id) {
        String sql = "SELECT id, day_of_week, start_period, end_period, room FROM sessions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSession(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Session mapSession(ResultSet rs) throws SQLException {
        return new Session(rs.getInt("id"), rs.getString("day_of_week"),
                rs.getInt("start_period"), rs.getInt("end_period"), rs.getString("room"));
    }
}
