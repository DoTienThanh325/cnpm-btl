package com.sms.dao;

import com.sms.entity.Faculty;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO extends DAO {
    public List<Faculty> getAllFaculties() {
        List<Faculty> faculties = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_faculties", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) faculties.add(mapFaculty(rs));
            return faculties;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Faculty getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_faculty_by_id", 1))) {
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
