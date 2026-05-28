package com.sms.dao;

import com.sms.entity.Subject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO extends DAO {
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_subjects", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) subjects.add(mapSubject(rs));
            return subjects;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Subject> getAllSubject() {
        return getAllSubjects();
    }

    public Subject getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_subject_by_id", 1))) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSubject(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Subject mapSubject(ResultSet rs) throws SQLException {
        return new Subject(rs.getInt("id"), rs.getString("code"), rs.getString("name"),
                rs.getInt("credits"), rs.getString("content"), rs.getString("status"));
    }
}
