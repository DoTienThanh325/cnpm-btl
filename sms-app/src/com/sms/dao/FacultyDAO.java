package com.sms.dao;

import com.sms.entity.Faculty;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO extends DAO {
    public List<Faculty> getAllFaculties() {
        List<Faculty> faculties = new ArrayList<>();
        String sql = "SELECT id, code, name, head FROM faculties ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) faculties.add(mapFaculty(rs));
            return faculties;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Faculty getById(int id) {
        String sql = "SELECT id, code, name, head FROM faculties WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapFaculty(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Faculty mapFaculty(ResultSet rs) throws SQLException {
        return new Faculty(rs.getInt("id"), rs.getString("code"), rs.getString("name"), rs.getString("head"));
    }
}
