package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.*;

import java.sql.*;
import java.util.*;

public class ClassSectionDAO {

    private static final String SELECT_BASE =
            "SELECT cs.id AS cs_id, cs.code AS cs_code, cs.capacity, cs.enrolled_count, cs.status AS cs_status, " +
            "       sub.id AS sub_id, sub.code AS sub_code, sub.name AS sub_name, sub.credits, sub.content, sub.status AS sub_status, " +
            "       sf.id AS sf_id, sf.code AS sf_code, sf.name AS sf_name, sf.head AS sf_head, " +
            "       u.id AS t_id, u.username AS t_username, u.password AS t_password, " +
            "       u.name AS t_name, u.status AS t_user_status, " +
            "       t.email AS t_email, t.phone AS t_phone, " +
            "       tf.id AS tf_id, tf.code AS tf_code, tf.name AS tf_name, tf.head AS tf_head " +
            "FROM class_sections cs " +
            "LEFT JOIN subjects sub ON cs.subject_id = sub.id " +
            "LEFT JOIN faculties sf ON sub.faculty_id = sf.id " +
            "LEFT JOIN teachers t ON cs.teacher_id = t.id " +
            "LEFT JOIN users u ON u.id = t.id " +
            "LEFT JOIN faculties tf ON t.faculty_id = tf.id";

    public static List<StudentEnrollment> getEnrollments() {
        List<StudentEnrollment> out = new ArrayList<>();
        String sql = "SELECT student_id, class_section_id FROM enrollments";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new StudentEnrollment(rs.getInt("student_id"), rs.getInt("class_section_id")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getEnrollments failed", e);
        }
        return out;
    }

    public List<ClassSection> getAllClassSections() {
        List<ClassSection> out = loadList(SELECT_BASE + " ORDER BY cs.id", null);
        attachSessions(out);
        return out;
    }

    public ClassSection getById(int id) {
        List<ClassSection> result = loadList(SELECT_BASE + " WHERE cs.id = ?", ps -> ps.setInt(1, id));
        if (result.isEmpty()) return null;
        attachSessions(result);
        return result.get(0);
    }

    public List<ClassSection> searchClass(String keyword) {
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        List<ClassSection> out = loadList(
                SELECT_BASE + " WHERE LOWER(cs.code) LIKE ? OR LOWER(sub.name) LIKE ? ORDER BY cs.id",
                ps -> { ps.setString(1, kw); ps.setString(2, kw); });
        attachSessions(out);
        return out;
    }

    public List<ClassSection> getByTeacher(int teacherId) {
        List<ClassSection> out = loadList(SELECT_BASE + " WHERE cs.teacher_id = ? ORDER BY cs.id",
                ps -> ps.setInt(1, teacherId));
        attachSessions(out);
        return out;
    }

    public List<ClassSection> getByStudent(int studentId) {
        String sql = SELECT_BASE + " JOIN enrollments e ON e.class_section_id = cs.id " +
                "WHERE e.student_id = ? ORDER BY cs.id";
        List<ClassSection> out = loadList(sql, ps -> ps.setInt(1, studentId));
        attachSessions(out);
        return out;
    }

    public boolean checkSchedule(Teacher teacher, List<Session> newSessions) {
        if (newSessions == null || newSessions.isEmpty()) return false;
        List<ClassSection> classes = getByTeacher(teacher.getId());
        for (ClassSection c : classes) {
            for (Session existing : c.getSessions()) {
                for (Session ns : newSessions) {
                    if (existing.getDayOfWeek().equals(ns.getDayOfWeek())
                            && ns.getStartPeriod() <= existing.getEndPeriod()
                            && ns.getEndPeriod() >= existing.getStartPeriod()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean validateClassData(int capacity, List<Session> sessions) {
        if (capacity <= 0) return false;
        if (sessions == null || sessions.isEmpty()) return false;
        return true;
    }

    public boolean createClass(ClassSection classSection) {
        String sql = "INSERT INTO class_sections (code, subject_id, teacher_id, capacity, enrolled_count, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get()) {
            int newId;
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, classSection.getCode());
                if (classSection.getSubject() != null) ps.setInt(2, classSection.getSubject().getId());
                else ps.setNull(2, Types.INTEGER);
                if (classSection.getTeacher() != null) ps.setInt(3, classSection.getTeacher().getId());
                else ps.setNull(3, Types.INTEGER);
                ps.setInt(4, classSection.getCapacity());
                ps.setInt(5, classSection.getEnrolledCount());
                ps.setString(6, classSection.getStatus() == null ? "active" : classSection.getStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("no class id generated");
                    newId = keys.getInt(1);
                }
            }
            classSection.setId(newId);
            replaceSessionLinks(c, newId, classSection.getSessions());
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("createClass failed", e);
        }
    }

    public boolean updateClass(ClassSection classSection) {
        String sql = "UPDATE class_sections SET code = ?, subject_id = ?, teacher_id = ?, " +
                "capacity = ?, enrolled_count = ?, status = ? WHERE id = ?";
        try (Connection c = DBConnection.get()) {
            int rows;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, classSection.getCode());
                if (classSection.getSubject() != null) ps.setInt(2, classSection.getSubject().getId());
                else ps.setNull(2, Types.INTEGER);
                if (classSection.getTeacher() != null) ps.setInt(3, classSection.getTeacher().getId());
                else ps.setNull(3, Types.INTEGER);
                ps.setInt(4, classSection.getCapacity());
                ps.setInt(5, classSection.getEnrolledCount());
                ps.setString(6, classSection.getStatus());
                ps.setInt(7, classSection.getId());
                rows = ps.executeUpdate();
            }
            replaceSessionLinks(c, classSection.getId(), classSection.getSessions());
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateClass failed", e);
        }
    }

    public boolean cancelClass(int id) {
        String sql = "UPDATE class_sections SET status = 'cancelled' WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("cancelClass failed", e);
        }
    }

    public boolean enrollStudent(int studentId, int classSectionId) {
        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT 1 FROM enrollments WHERE student_id = ? AND class_section_id = ?")) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, classSectionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) { c.rollback(); return false; }
                    }
                }
                int capacity, enrolled;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT capacity, enrolled_count FROM class_sections WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, classSectionId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { c.rollback(); return false; }
                        capacity = rs.getInt("capacity");
                        enrolled = rs.getInt("enrolled_count");
                    }
                }
                if (enrolled >= capacity) { c.rollback(); return false; }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO enrollments (student_id, class_section_id) VALUES (?, ?)")) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, classSectionId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE class_sections SET enrolled_count = enrolled_count + 1 WHERE id = ?")) {
                    ps.setInt(1, classSectionId);
                    ps.executeUpdate();
                }
                c.commit();
                return true;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("enrollStudent failed", e);
        }
    }

    public boolean cancelEnrollment(int studentId, int classSectionId) {
        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM enrollments WHERE student_id = ? AND class_section_id = ?")) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, classSectionId);
                    rows = ps.executeUpdate();
                }
                if (rows > 0) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE class_sections SET enrolled_count = GREATEST(enrolled_count - 1, 0) WHERE id = ?")) {
                        ps.setInt(1, classSectionId);
                        ps.executeUpdate();
                    }
                }
                c.commit();
                return rows > 0;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("cancelEnrollment failed", e);
        }
    }

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<ClassSection> loadList(String sql, Binder binder) {
        List<ClassSection> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("loadList class_sections failed", e);
        }
        return out;
    }

    private void attachSessions(List<ClassSection> classes) {
        if (classes.isEmpty()) return;
        Map<Integer, ClassSection> byId = new HashMap<>();
        StringBuilder placeholders = new StringBuilder();
        for (ClassSection cs : classes) {
            if (placeholders.length() > 0) placeholders.append(',');
            placeholders.append('?');
            byId.put(cs.getId(), cs);
        }
        String sql = "SELECT css.class_section_id, se.id, se.day_of_week, se.start_period, se.end_period, se.room " +
                "FROM class_section_sessions css JOIN sessions se ON se.id = css.session_id " +
                "WHERE css.class_section_id IN (" + placeholders + ") ORDER BY se.id";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int idx = 1;
            for (ClassSection cs : classes) ps.setInt(idx++, cs.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClassSection cs = byId.get(rs.getInt("class_section_id"));
                    if (cs != null) {
                        cs.getSessions().add(new Session(rs.getInt("id"), rs.getString("day_of_week"),
                                rs.getInt("start_period"), rs.getInt("end_period"), rs.getString("room")));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("attachSessions failed", e);
        }
    }

    private void replaceSessionLinks(Connection c, int classSectionId, List<Session> sessions) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "DELETE FROM class_section_sessions WHERE class_section_id = ?")) {
            ps.setInt(1, classSectionId);
            ps.executeUpdate();
        }
        if (sessions == null || sessions.isEmpty()) return;
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO class_section_sessions (class_section_id, session_id) VALUES (?, ?)")) {
            for (Session se : sessions) {
                if (se == null) continue;
                ps.setInt(1, classSectionId);
                ps.setInt(2, se.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    static ClassSection map(ResultSet rs) throws SQLException {
        Subject subject = null;
        int subId = rs.getInt("sub_id");
        if (!rs.wasNull()) {
            Faculty subFaculty = null;
            int sfid = rs.getInt("sf_id");
            if (!rs.wasNull()) {
                subFaculty = new Faculty(sfid, rs.getString("sf_code"),
                        rs.getString("sf_name"), rs.getString("sf_head"));
            }
            subject = new Subject(subId, rs.getString("sub_code"), rs.getString("sub_name"),
                    rs.getInt("credits"), rs.getString("content"), subFaculty, rs.getString("sub_status"));
        }
        Teacher teacher = null;
        int tid = rs.getInt("t_id");
        if (!rs.wasNull()) {
            Faculty tf = null;
            int tfid = rs.getInt("tf_id");
            if (!rs.wasNull()) {
                tf = new Faculty(tfid, rs.getString("tf_code"), rs.getString("tf_name"), rs.getString("tf_head"));
            }
            teacher = new Teacher(tid, rs.getString("t_username"), rs.getString("t_password"),
                    rs.getString("t_name"), rs.getString("t_user_status"), tf,
                    rs.getString("t_email"), rs.getString("t_phone"));
        }
        return new ClassSection(rs.getInt("cs_id"), rs.getString("cs_code"), subject, teacher,
                rs.getInt("capacity"), rs.getInt("enrolled_count"), rs.getString("cs_status"));
    }
}
