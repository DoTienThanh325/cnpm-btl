package com.sms.dao;

import com.sms.entity.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO extends DAO {
    public List<Session> getAllSession() {
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_sessions", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) sessions.add(mapSession(rs));
            return sessions;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Session getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_session_by_id", 1))) {
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
