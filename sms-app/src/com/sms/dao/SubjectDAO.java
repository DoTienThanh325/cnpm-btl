package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO extends DAO {
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String sql = baseSql() + " ORDER BY s.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) subjects.add(loadTextbooks(conn, mapSubject(rs)));
            return subjects;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Subject> getAllSubject() {
        return getAllSubjects();
    }

    public Subject getById(int id) {
        String sql = baseSql() + " WHERE s.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? loadTextbooks(conn, mapSubject(rs)) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Subject> searchSubjects(String keyword) {
        List<Subject> subjects = new ArrayList<>();
        String sql = baseSql() + " WHERE LOWER(s.name) LIKE ? OR LOWER(s.code) LIKE ? ORDER BY s.id";
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) subjects.add(loadTextbooks(conn, mapSubject(rs)));
            }
            return subjects;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean createSubject(Subject subject) {
        String sql = "INSERT INTO subjects(code, name, credits, content, faculty_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, subject.getCode());
            ps.setString(2, subject.getName());
            ps.setInt(3, subject.getCredits());
            ps.setString(4, subject.getContent());
            ps.setInt(5, subject.getFaculty().getId());
            ps.setString(6, subject.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) subject.setId(keys.getInt(1));
            }
            saveTextbooks(conn, subject);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateSubject(Subject subject) {
        String sql = "UPDATE subjects SET name = ?, credits = ?, content = ?, faculty_id = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getName());
            ps.setInt(2, subject.getCredits());
            ps.setString(3, subject.getContent());
            ps.setInt(4, subject.getFaculty().getId());
            ps.setString(5, subject.getStatus());
            ps.setInt(6, subject.getId());
            boolean updated = ps.executeUpdate() > 0;
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM subject_textbooks WHERE subject_id = ?")) {
                del.setInt(1, subject.getId());
                del.executeUpdate();
            }
            saveTextbooks(conn, subject);
            return updated;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean deleteSubject(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM subjects WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private String baseSql() {
        return "SELECT s.id, s.code, s.name, s.credits, s.content, s.status, "
                + "f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head "
                + "FROM subjects s JOIN faculties f ON f.id = s.faculty_id";
    }

    private Subject loadTextbooks(Connection conn, Subject subject) throws SQLException {
        String sql = "SELECT t.id, t.name, t.author, t.year FROM textbooks t "
                + "JOIN subject_textbooks st ON st.textbook_id = t.id WHERE st.subject_id = ? ORDER BY t.id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subject.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) subject.getTextbooks().add(TextbookDAO.mapTextbook(rs));
            }
        }
        return subject;
    }

    private void saveTextbooks(Connection conn, Subject subject) throws SQLException {
        String sql = "INSERT IGNORE INTO subject_textbooks(subject_id, textbook_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Textbook textbook : subject.getTextbooks()) {
                ps.setInt(1, subject.getId());
                ps.setInt(2, textbook.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    static Subject mapSubject(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty(rs.getInt("faculty_id"), rs.getString("faculty_code"),
                rs.getString("faculty_name"), rs.getString("head"));
        return new Subject(rs.getInt("id"), rs.getString("code"), rs.getString("name"),
                rs.getInt("credits"), rs.getString("content"), faculty, rs.getString("status"));
    }
}
