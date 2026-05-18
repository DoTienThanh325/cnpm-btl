package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.Faculty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public List<Faculty> getAllFaculties() {
        List<Faculty> out = new ArrayList<>();
        String sql = "SELECT id, code, name, head FROM faculties ORDER BY id";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllFaculties failed", e);
        }
        return out;
    }

    public Faculty getById(int id) {
        String sql = "SELECT id, code, name, head FROM faculties WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById faculty failed", e);
        }
    }

    static Faculty map(ResultSet rs) throws SQLException {
        return new Faculty(rs.getInt("id"), rs.getString("code"),
                rs.getString("name"), rs.getString("head"));
    }
}
