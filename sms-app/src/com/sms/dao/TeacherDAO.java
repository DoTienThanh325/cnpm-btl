package com.sms.dao;

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

    static Teacher mapTeacher(ResultSet rs) throws SQLException {
        return new Teacher(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("status"), rs.getString("email"), rs.getString("phone"));
    }
}
