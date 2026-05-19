package com.sms.dao;

import com.sms.entity.Major;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO extends DAO {
    public List<Major> getAllMajors() {
        List<Major> majors = new ArrayList<>();
        String sql = "SELECT m.id, m.code, m.name, f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head "
                + "FROM majors m JOIN faculties f ON f.id = m.faculty_id ORDER BY m.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) majors.add(mapMajor(rs));
            return majors;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Major getById(int id) {
        String sql = "SELECT m.id, m.code, m.name, f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head "
                + "FROM majors m JOIN faculties f ON f.id = m.faculty_id WHERE m.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapMajor(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Major mapMajor(ResultSet rs) throws SQLException {
        return new Major(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                new com.sms.entity.Faculty(
                        rs.getInt("faculty_id"),
                        rs.getString("faculty_code"),
                        rs.getString("faculty_name"),
                        rs.getString("head")));
    }
}
