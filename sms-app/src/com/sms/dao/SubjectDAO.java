package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO extends DAO {
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_subjects", 0));
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_subject_by_id", 1))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_search_subjects", 1))) {
            ps.setString(1, keyword.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) subjects.add(loadTextbooks(conn, mapSubject(rs)));
            }
            return subjects;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean createSubject(Subject subject) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement ps = conn.prepareCall(call("sp_create_subject", 7))) {
                ps.setString(1, subject.getCode());
                ps.setString(2, subject.getName());
                ps.setInt(3, subject.getCredits());
                ps.setString(4, subject.getContent());
                ps.setInt(5, subject.getFaculty().getId());
                ps.setString(6, subject.getStatus());
                ps.registerOutParameter(7, Types.INTEGER);
                ps.executeUpdate();
                subject.setId(ps.getInt(7));
                saveTextbooks(conn, subject);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                if (e instanceof SQLIntegrityConstraintViolationException) return false;
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateSubject(Subject subject) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement ps = conn.prepareCall(call("sp_update_subject", 6))) {
                ps.setInt(1, subject.getId());
                ps.setString(2, subject.getName());
                ps.setInt(3, subject.getCredits());
                ps.setString(4, subject.getContent());
                ps.setInt(5, subject.getFaculty().getId());
                ps.setString(6, subject.getStatus());
                boolean updated = ps.executeUpdate() > 0;
                try (CallableStatement del = conn.prepareCall(call("sp_delete_subject_textbooks", 1))) {
                    del.setInt(1, subject.getId());
                    del.executeUpdate();
                }
                saveTextbooks(conn, subject);
                conn.commit();
                return updated;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean deleteSubject(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_delete_subject", 1))) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private Subject loadTextbooks(Connection conn, Subject subject) throws SQLException {
        try (CallableStatement ps = conn.prepareCall(call("sp_get_textbooks_by_subject", 1))) {
            ps.setInt(1, subject.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) subject.getTextbooks().add(TextbookDAO.mapTextbook(rs));
            }
        }
        return subject;
    }

    private void saveTextbooks(Connection conn, Subject subject) throws SQLException {
        try (CallableStatement ps = conn.prepareCall(call("sp_add_subject_textbook", 2))) {
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
