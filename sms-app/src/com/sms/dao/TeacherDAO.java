package com.sms.dao;

import com.sms.entity.Faculty;
import com.sms.entity.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO extends DAO {
    public List<Teacher> getAllTeacher() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = baseSql() + " ORDER BY u.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) teachers.add(mapTeacher(rs));
            return teachers;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Teacher getById(int id) {
        String sql = baseSql() + " WHERE u.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapTeacher(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Teacher> searchTeachers(String keyword) {
        List<Teacher> teachers = new ArrayList<>();
        String sql = baseSql() + " WHERE LOWER(u.name) LIKE ? ORDER BY u.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase().trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) teachers.add(mapTeacher(rs));
            }
            return teachers;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private String baseSql() {
        return "SELECT u.id, u.username, u.password, u.name, u.status, t.email, t.phone, "
                + "f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head "
                + "FROM teachers t JOIN users u ON u.id = t.user_id "
                + "JOIN faculties f ON f.id = t.faculty_id";
    }

    static Teacher mapTeacher(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty(rs.getInt("faculty_id"), rs.getString("faculty_code"),
                rs.getString("faculty_name"), rs.getString("head"));
        return new Teacher(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("status"), faculty, rs.getString("email"), rs.getString("phone"));
    }
}
