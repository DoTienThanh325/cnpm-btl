package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassSectionDAO extends DAO {
    public static List<StudentEnrollment> getEnrollments() {
        return new ClassSectionDAO().loadEnrollments();
    }

    public List<ClassSection> getAllClassSections() {
        List<ClassSection> classes = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_class_sections", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) classes.add(loadSessions(conn, mapClassSection(rs)));
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public ClassSection getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_class_section_by_id", 1))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_search_class_sections", 1))) {
            ps.setString(1, keyword.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) classes.add(loadSessions(conn, mapClassSection(rs)));
            }
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean checkSchedule(Teacher teacher, List<Session> newSessions) {
        if (teacher == null || newSessions == null) return false;
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_active_sessions_by_teacher", 1))) {
            ps.setInt(1, teacher.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    for (Session newSess : newSessions) {
                        boolean sameDay = rs.getString("day_of_week").equals(newSess.getDayOfWeek());
                        boolean overlap = newSess.getStartPeriod() <= rs.getInt("end_period")
                                && newSess.getEndPeriod() >= rs.getInt("start_period");
                        if (sameDay && overlap) return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean validateClassData(int capacity, List<Session> sessions) {
        return capacity > 0 && sessions != null && !sessions.isEmpty();
    }

    public boolean createClass(ClassSection classSection) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement ps = conn.prepareCall(call("sp_create_class_section", 7))) {
                ps.setString(1, classSection.getCode());
                ps.setInt(2, classSection.getSubject().getId());
                ps.setInt(3, classSection.getTeacher().getId());
                ps.setInt(4, classSection.getCapacity());
                ps.setInt(5, classSection.getEnrolledCount());
                ps.setString(6, classSection.getStatus());
                ps.registerOutParameter(7, Types.INTEGER);
                ps.executeUpdate();
                classSection.setId(ps.getInt(7));
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
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement ps = conn.prepareCall(call("sp_update_class_section", 7))) {
                ps.setInt(1, classSection.getId());
                ps.setString(2, classSection.getCode());
                ps.setInt(3, classSection.getSubject().getId());
                ps.setInt(4, classSection.getTeacher().getId());
                ps.setInt(5, classSection.getCapacity());
                ps.setInt(6, classSection.getEnrolledCount());
                ps.setString(7, classSection.getStatus());
                boolean updated = ps.executeUpdate() > 0;
                try (CallableStatement del = conn.prepareCall(call("sp_delete_class_section_sessions", 1))) {
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
             CallableStatement ps = conn.prepareCall(call("sp_cancel_class_section", 1))) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<ClassSection> getByTeacher(int teacherId) {
        List<ClassSection> classes = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_class_sections_by_teacher", 1))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_enroll_student", 2))) {
            ps.setInt(1, studentId);
            ps.setInt(2, classSectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean cancelEnrollment(int studentId, int classSectionId) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_cancel_enrollment", 2))) {
            ps.setInt(1, studentId);
            ps.setInt(2, classSectionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<ClassSection> getByStudent(int studentId) {
        List<ClassSection> classes = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_class_sections_by_student", 1))) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) classes.add(loadSessions(conn, mapClassSection(rs)));
            }
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private List<StudentEnrollment> loadEnrollments() {
        List<StudentEnrollment> enrollments = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_student_enrollments", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) enrollments.add(new StudentEnrollment(rs.getInt("student_id"), rs.getInt("class_section_id")));
            return enrollments;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private ClassSection loadSessions(Connection conn, ClassSection classSection) throws SQLException {
        try (CallableStatement ps = conn.prepareCall(call("sp_get_sessions_by_class_section", 1))) {
            ps.setInt(1, classSection.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) classSection.getSessions().add(SessionDAO.mapSession(rs));
            }
        }
        return classSection;
    }

    private void saveSessions(Connection conn, ClassSection classSection) throws SQLException {
        try (CallableStatement ps = conn.prepareCall(call("sp_add_class_section_session", 2))) {
            for (Session session : classSection.getSessions()) {
                ps.setInt(1, classSection.getId());
                ps.setInt(2, session.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    static ClassSection mapClassSection(ResultSet rs) throws SQLException {
        Faculty subjectFaculty = new Faculty(rs.getInt("subject_faculty_id"), rs.getString("subject_faculty_code"),
                rs.getString("subject_faculty_name"), rs.getString("subject_faculty_head"));
        Subject subject = new Subject(rs.getInt("subject_id"), rs.getString("subject_code"), rs.getString("subject_name"),
                rs.getInt("credits"), rs.getString("content"), subjectFaculty, rs.getString("subject_status"));
        Faculty teacherFaculty = new Faculty(rs.getInt("teacher_faculty_id"), rs.getString("teacher_faculty_code"),
                rs.getString("teacher_faculty_name"), rs.getString("teacher_faculty_head"));
        Teacher teacher = new Teacher(rs.getInt("teacher_id"), rs.getString("username"), rs.getString("password"),
                rs.getString("teacher_name"), rs.getString("teacher_status"), teacherFaculty,
                rs.getString("teacher_email"), rs.getString("teacher_phone"));
        return new ClassSection(rs.getInt("id"), rs.getString("code"), subject, teacher,
                rs.getInt("capacity"), rs.getInt("enrolled_count"), rs.getString("status"));
    }
}
