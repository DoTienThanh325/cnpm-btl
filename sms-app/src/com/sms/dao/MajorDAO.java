package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.Faculty;
import com.sms.entity.Major;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO {

    private static final String SELECT_BASE =
            "SELECT m.id AS m_id, m.code AS m_code, m.name AS m_name, " +
            "       f.id AS f_id, f.code AS f_code, f.name AS f_name, f.head AS f_head " +
            "FROM majors m LEFT JOIN faculties f ON m.faculty_id = f.id";

    public List<Major> getAllMajors() {
        List<Major> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " ORDER BY m.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllMajors failed", e);
        }
        return out;
    }

    public Major getById(int id) {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE m.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById major failed", e);
        }
    }

    static Major map(ResultSet rs) throws SQLException {
        Faculty f = null;
        int fid = rs.getInt("f_id");
        if (!rs.wasNull()) {
            f = new Faculty(fid, rs.getString("f_code"), rs.getString("f_name"), rs.getString("f_head"));
        }
        return new Major(rs.getInt("m_id"), rs.getString("m_code"), rs.getString("m_name"), f);
    }
}
