package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.Faculty;
import com.sms.entity.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    private static final String SELECT_BASE =
            "SELECT u.id AS u_id, u.username, u.password, u.name, u.status, " +
            "       t.email AS t_email, t.phone AS t_phone, " +
            "       f.id AS f_id, f.code AS f_code, f.name AS f_name, f.head AS f_head " +
            "FROM teachers t " +
            "JOIN users u ON u.id = t.id " +
            "LEFT JOIN faculties f ON t.faculty_id = f.id";

    public List<Teacher> getAllTeacher() {
        List<Teacher> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " ORDER BY u.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllTeacher failed", e);
        }
        return out;
    }

    public Teacher getById(int id) {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE u.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById teacher failed", e);
        }
    }

    public List<Teacher> searchTeachers(String keyword) {
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        List<Teacher> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE LOWER(u.name) LIKE ? ORDER BY u.id")) {
            ps.setString(1, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("searchTeachers failed", e);
        }
        return out;
    }

    static Teacher map(ResultSet rs) throws SQLException {
        Faculty f = null;
        int fid = rs.getInt("f_id");
        if (!rs.wasNull()) {
            f = new Faculty(fid, rs.getString("f_code"), rs.getString("f_name"), rs.getString("f_head"));
        }
        return new Teacher(rs.getInt("u_id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("status"), f,
                rs.getString("t_email"), rs.getString("t_phone"));
    }
}
