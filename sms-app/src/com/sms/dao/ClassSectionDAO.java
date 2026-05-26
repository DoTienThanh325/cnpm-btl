package com.sms.dao;

import com.sms.entity.ClassSection;
import com.sms.entity.Subject;
import com.sms.entity.Teacher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassSectionDAO extends DAO {
    // public static List<StudentEnrollment> getEnrollments() {
    //     return new ClassSectionDAO().loadEnrollments();
    // }

    public List<ClassSection> getAllClassSections() {
        List<ClassSection> classes = new ArrayList<>();
        String sql = baseSql() + " ORDER BY c.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) classes.add(loadSessions(conn, mapClassSection(rs)));
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public ClassSection getById(int id) {
        String sql = baseSql() + " WHERE c.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? loadSessions(conn, mapClassSection(rs)) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<ClassSection> searchClass(String keyword) {
        List<ClassSection> classes = new ArrayList<>();
        String sql = baseSql() + " WHERE LOWER(c.code) LIKE ? OR LOWER(s.name) LIKE ? ORDER BY c.id";
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) classes.add(loadSessions(conn, mapClassSection(rs)));
            }
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // public boolean checkSchedule(Teacher teacher, List<Session> newSessions) {
    //     if (teacher == null || newSessions == null) return false;
    //     String sql = "SELECT se.day_of_week, se.start_period, se.end_period FROM class_sections c "
    //             + "JOIN class_section_sessions css ON css.class_section_id = c.id "
    //             + "JOIN sessions se ON se.id = css.session_id WHERE c.teacher_id = ? AND c.status = 'active'";
    //     try (Connection conn = getConnection();
    //          PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setInt(1, teacher.getId());
    //         try (ResultSet rs = ps.executeQuery()) {
    //             while (rs.next()) {
    //                 for (Session newSess : newSessions) {
    //                     boolean sameDay = rs.getString("day_of_week").equals(newSess.getDayOfWeek());
    //                     boolean overlap = newSess.getStartPeriod() <= rs.getInt("end_period")
    //                             && newSess.getEndPeriod() >= rs.getInt("start_period");
    //                     if (sameDay && overlap) return true;
    //                 }
    //             }
    //         }
    //         return false;
    //     } catch (SQLException e) {
    //         throw dbError(e);
    //     }
    // }

    // public boolean validateClassData(int capacity, List<Session> sessions) {
    //     return capacity > 0 && sessions != null && !sessions.isEmpty();
    // }

    public boolean createClass(ClassSection classSection) {
        String sql = "INSERT INTO class_sections(code, subject_id, teacher_id, capacity, enrolled_count, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, classSection.getCode());
                ps.setInt(2, classSection.getSubject().getId());
                ps.setInt(3, classSection.getTeacher().getId());
                ps.setInt(4, classSection.getCapacity());
                ps.setInt(5, classSection.getEnrolledCount());
                ps.setString(6, classSection.getStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) classSection.setId(keys.getInt(1));
                }
                saveSessions(conn, classSection);
                conn.commit();
                return true;
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

    public boolean updateClass(ClassSection classSection) {
        String sql = "UPDATE class_sections SET code = ?, subject_id = ?, teacher_id = ?, capacity = ?, enrolled_count = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, classSection.getCode());
                ps.setInt(2, classSection.getSubject().getId());
                ps.setInt(3, classSection.getTeacher().getId());
                ps.setInt(4, classSection.getCapacity());
                ps.setInt(5, classSection.getEnrolledCount());
                ps.setString(6, classSection.getStatus());
                ps.setInt(7, classSection.getId());
                boolean updated = ps.executeUpdate() > 0;
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM class_section_sessions WHERE class_section_id = ?")) {
                    del.setInt(1, classSection.getId());
                    del.executeUpdate();
                }
                saveSessions(conn, classSection);
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

    public boolean cancelClass(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE class_sections SET status = 'cancelled' WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<ClassSection> getByTeacher(int teacherId) {
        List<ClassSection> classes = new ArrayList<>();
        String sql = baseSql() + " WHERE c.teacher_id = ? ORDER BY c.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) classes.add(loadSessions(conn, mapClassSection(rs)));
            }
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean enrollStudent(int studentId, int classSectionId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                ClassSection cs = getById(classSectionId);
                if (cs == null || cs.getEnrolledCount() >= cs.getCapacity()) return false;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO student_enrollments(student_id, class_section_id) VALUES (?, ?)")) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, classSectionId);
                    ps.executeUpdate();
                }
                updateEnrolledCount(conn, classSectionId, 1);
                conn.commit();
                return true;
            } catch (SQLIntegrityConstraintViolationException e) {
                conn.rollback();
                return false;
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

    public boolean cancelEnrollment(int studentId, int classSectionId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM student_enrollments WHERE student_id = ? AND class_section_id = ?")) {
                ps.setInt(1, studentId);
                ps.setInt(2, classSectionId);
                boolean removed = ps.executeUpdate() > 0;
                if (removed) updateEnrolledCount(conn, classSectionId, -1);
                conn.commit();
                return removed;
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

    public List<ClassSection> getByStudent(int studentId) {
        List<ClassSection> classes = new ArrayList<>();
        String sql = baseSql() + " JOIN student_enrollments se ON se.class_section_id = c.id "
                + "WHERE se.student_id = ? ORDER BY c.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) classes.add(loadSessions(conn, mapClassSection(rs)));
            }
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    // private List<StudentEnrollment> loadEnrollments() {
    //     List<StudentEnrollment> enrollments = new ArrayList<>();
    //     try (Connection conn = getConnection();
    //          PreparedStatement ps = conn.prepareStatement("SELECT student_id, class_section_id FROM student_enrollments");
    //          ResultSet rs = ps.executeQuery()) {
    //         while (rs.next()) enrollments.add(new StudentEnrollment(rs.getInt("student_id"), rs.getInt("class_section_id")));
    //         return enrollments;
    //     } catch (SQLException e) {
    //         throw dbError(e);
    //     }
    // }

    private String baseSql() {
        return "SELECT c.id, c.code, c.capacity, c.enrolled_count, c.status, "
                + "s.id subject_id, s.code subject_code, s.name subject_name, s.credits, s.content, s.status subject_status, "
                + "sf.id subject_faculty_id, sf.code subject_faculty_code, sf.name subject_faculty_name, sf.head subject_faculty_head, "
                + "u.id teacher_id, u.username, u.password, u.name teacher_name, u.status teacher_status, t.email teacher_email, t.phone teacher_phone, "
                + "tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head "
                + "FROM class_sections c "
                + "JOIN subjects s ON s.id = c.subject_id JOIN faculties sf ON sf.id = s.faculty_id "
                + "JOIN teachers t ON t.user_id = c.teacher_id JOIN users u ON u.id = t.user_id "
                + "JOIN faculties tf ON tf.id = t.faculty_id";
    }

    private ClassSection loadSessions(Connection conn, ClassSection classSection) throws SQLException {
        String sql = "SELECT se.id, se.day_of_week, se.start_period, se.end_period, se.room FROM sessions se "
                + "JOIN class_section_sessions css ON css.session_id = se.id WHERE css.class_section_id = ? ORDER BY se.id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classSection.getId());
            try (ResultSet rs = ps.executeQuery()) {
                // while (rs.next()) classSection.getSessions().add(SessionDAO.mapSession(rs));
            }
        }
        return classSection;
    }

    private void saveSessions(Connection conn, ClassSection classSection) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO class_section_sessions(class_section_id, session_id) VALUES (?, ?)")) {
            // for (Session session : classSection.getSessions()) {
            //     ps.setInt(1, classSection.getId());
            //     ps.setInt(2, session.getId());
            //     ps.addBatch();
            // }
            ps.executeBatch();
        }
    }

    private void updateEnrolledCount(Connection conn, int classSectionId, int delta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE class_sections SET enrolled_count = GREATEST(enrolled_count + ?, 0) WHERE id = ?")) {
            ps.setInt(1, delta);
            ps.setInt(2, classSectionId);
            ps.executeUpdate();
        }
    }

    static ClassSection mapClassSection(ResultSet rs) throws SQLException {
        // Faculty subjectFaculty = new Faculty(rs.getInt("subject_faculty_id"), rs.getString("subject_faculty_code"),
        //         rs.getString("subject_faculty_name"), rs.getString("subject_faculty_head"));
        Subject subject = new Subject(rs.getInt("subject_id"), rs.getString("subject_code"), rs.getString("subject_name"),
                rs.getInt("credits"), rs.getString("content"), rs.getString("subject_status"));
        // Faculty teacherFaculty = new Faculty(rs.getInt("teacher_faculty_id"), rs.getString("teacher_faculty_code"),
        //         rs.getString("teacher_faculty_name"), rs.getString("teacher_faculty_head"));
        Teacher teacher = new Teacher(rs.getInt("teacher_id"), rs.getString("username"), rs.getString("password"),
                rs.getString("teacher_name"), rs.getString("teacher_status"),
                rs.getString("teacher_email"), rs.getString("teacher_phone"));
        return new ClassSection(rs.getInt("id"), rs.getString("code"), subject, teacher,
                rs.getInt("capacity"), rs.getInt("enrolled_count"), rs.getString("status"));
    }
}
