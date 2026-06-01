package com.sms.dao;

import com.sms.entity.Faculty;
import com.sms.entity.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO extends DAO {
    public List<Teacher> getAllTeacher() {
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_teachers", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) teachers.add(mapTeacher(rs));
            return teachers;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Teacher getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_teacher_by_id", 1))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_search_teachers", 1))) {
            ps.setString(1, keyword.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) teachers.add(mapTeacher(rs));
            }
            return teachers;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Teacher mapTeacher(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty(rs.getInt("faculty_id"), rs.getString("faculty_code"),
                rs.getString("faculty_name"), rs.getString("head"));
        return new Teacher(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("status"), faculty, rs.getString("email"), rs.getString("phone"));
    }
}
