package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.*;

import java.sql.*;
import java.util.*;

public class SubjectDAO {

    private static final String SELECT_BASE =
            "SELECT s.id AS s_id, s.code AS s_code, s.name AS s_name, s.credits, " +
            "       s.content, s.status AS s_status, " +
            "       f.id AS f_id, f.code AS f_code, f.name AS f_name, f.head AS f_head " +
            "FROM subjects s LEFT JOIN faculties f ON s.faculty_id = f.id";

    public List<Subject> getAllSubjects() {
        List<Subject> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " ORDER BY s.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllSubjects failed", e);
        }
        attachTextbooks(out);
        return out;
    }

    public List<Subject> getAllSubject() {
        return getAllSubjects();
    }

    public Subject getById(int id) {
        Subject result;
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE s.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                result = rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById subject failed", e);
        }
        if (result != null) attachTextbooks(Collections.singletonList(result));
        return result;
    }

    public List<Subject> searchSubjects(String keyword) {
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        List<Subject> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE +
                     " WHERE LOWER(s.name) LIKE ? OR LOWER(s.code) LIKE ? ORDER BY s.id")) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("searchSubjects failed", e);
        }
        attachTextbooks(out);
        return out;
    }

    public boolean createSubject(Subject subject) {
        String check = "SELECT 1 FROM subjects WHERE code = ?";
        String insert = "INSERT INTO subjects (code, name, credits, content, faculty_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get()) {
            try (PreparedStatement ps = c.prepareStatement(check)) {
                ps.setString(1, subject.getCode());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return false;
                }
            }
            int newId;
            try (PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, subject.getCode());
                ps.setString(2, subject.getName());
                ps.setInt(3, subject.getCredits());
                ps.setString(4, subject.getContent());
                if (subject.getFaculty() != null) ps.setInt(5, subject.getFaculty().getId());
                else ps.setNull(5, Types.INTEGER);
                ps.setString(6, subject.getStatus() == null ? "active" : subject.getStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("no subject id generated");
                    newId = keys.getInt(1);
                }
            }
            subject.setId(newId);
            replaceTextbookLinks(c, newId, subject.getTextbooks());
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("createSubject failed", e);
        }
    }

    public boolean updateSubject(Subject subject) {
        String sql = "UPDATE subjects SET code = ?, name = ?, credits = ?, content = ?, " +
                "faculty_id = ?, status = ? WHERE id = ?";
        try (Connection c = DBConnection.get()) {
            int rows;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, subject.getCode());
                ps.setString(2, subject.getName());
                ps.setInt(3, subject.getCredits());
                ps.setString(4, subject.getContent());
                if (subject.getFaculty() != null) ps.setInt(5, subject.getFaculty().getId());
                else ps.setNull(5, Types.INTEGER);
                ps.setString(6, subject.getStatus());
                ps.setInt(7, subject.getId());
                rows = ps.executeUpdate();
            }
            replaceTextbookLinks(c, subject.getId(), subject.getTextbooks());
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateSubject failed", e);
        }
    }

    public boolean deleteSubject(int id) {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM subjects WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("deleteSubject failed", e);
        }
    }

    private void attachTextbooks(List<Subject> subjects) {
        if (subjects.isEmpty()) return;
        Map<Integer, Subject> byId = new HashMap<>();
        StringBuilder placeholders = new StringBuilder();
        for (Subject s : subjects) {
            if (placeholders.length() > 0) placeholders.append(',');
            placeholders.append('?');
            byId.put(s.getId(), s);
        }
        String sql = "SELECT st.subject_id, tb.id AS tb_id, tb.name AS tb_name, " +
                "tb.author AS tb_author, tb.year AS tb_year " +
                "FROM subject_textbooks st JOIN textbooks tb ON tb.id = st.textbook_id " +
                "WHERE st.subject_id IN (" + placeholders + ")";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int idx = 1;
            for (Subject s : subjects) ps.setInt(idx++, s.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject s = byId.get(rs.getInt("subject_id"));
                    if (s != null) s.getTextbooks().add(TextbookDAO.mapPrefixed(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("attachTextbooks failed", e);
        }
    }

    private void replaceTextbookLinks(Connection c, int subjectId, List<Textbook> textbooks) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM subject_textbooks WHERE subject_id = ?")) {
            ps.setInt(1, subjectId);
            ps.executeUpdate();
        }
        if (textbooks == null || textbooks.isEmpty()) return;
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO subject_textbooks (subject_id, textbook_id) VALUES (?, ?)")) {
            for (Textbook tb : textbooks) {
                if (tb == null) continue;
                ps.setInt(1, subjectId);
                ps.setInt(2, tb.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    static Subject map(ResultSet rs) throws SQLException {
        Faculty f = null;
        int fid = rs.getInt("f_id");
        if (!rs.wasNull()) {
            f = new Faculty(fid, rs.getString("f_code"), rs.getString("f_name"), rs.getString("f_head"));
        }
        return new Subject(rs.getInt("s_id"), rs.getString("s_code"), rs.getString("s_name"),
                rs.getInt("credits"), rs.getString("content"), f, rs.getString("s_status"));
    }
}
