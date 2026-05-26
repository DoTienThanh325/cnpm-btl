package com.sms.dao;

import com.sms.entity.Student;
import com.sms.entity.Subject;
import com.sms.entity.Teacher;
import com.sms.entity.ClassSection;
import com.sms.entity.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO extends DAO {
    public Grade getGradeByStudentAndClass(int studentId, int classSectionId) {
        String sql = baseSql() + " WHERE g.student_id = ? AND g.class_section_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapGrade(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Grade> getGradesByStudent(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = baseSql() + " WHERE g.student_id = ? ORDER BY g.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) grades.add(mapGrade(rs));
            }
            return grades;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Grade> getGradesByClass(int classSectionId) {
        List<Grade> grades = new ArrayList<>();
        String sql = baseSql() + " WHERE g.class_section_id = ? ORDER BY g.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) grades.add(mapGrade(rs));
            }
            return grades;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateGrade(Grade grade) {
        String sql = "UPDATE grades SET attendance_score = ?, midterm_score = ?, final_score = ?, semester = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, grade.getAttendanceScore());
            ps.setDouble(2, grade.getMidtermScore());
            ps.setDouble(3, grade.getFinalScore());
            ps.setString(4, grade.getSemester());
            ps.setInt(5, grade.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean addGrade(Grade grade) {
        String sql = "INSERT INTO grades(student_id, class_section_id, attendance_score, midterm_score, final_score, semester) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, grade.getStudent().getId());
            ps.setInt(2, grade.getClassSection().getId());
            ps.setDouble(3, grade.getAttendanceScore());
            ps.setDouble(4, grade.getMidtermScore());
            ps.setDouble(5, grade.getFinalScore());
            ps.setString(6, grade.getSemester());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) grade.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public void ensureGradesForClass(int classSectionId, List<Student> students, ClassSection cs) {
        String sql = "INSERT IGNORE INTO grades(student_id, class_section_id, attendance_score, midterm_score, final_score, semester) "
                + "VALUES (?, ?, 0, 0, 0, '2024-2')";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Student student : students) {
                ps.setInt(1, student.getId());
                ps.setInt(2, classSectionId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private String baseSql() {
        return "SELECT g.id, g.attendance_score, g.midterm_score, g.final_score, g.semester, "
                + "su.id student_user_id, su.username student_username, su.password student_password, su.name student_name, su.status student_status_user, "
                + "st.mssv, st.dob, st.gender, st.address, st.email student_email, st.phone student_phone, st.cohort, st.admin_class, st.student_status, "
                + "sf.id student_faculty_id, sf.code student_faculty_code, sf.name student_faculty_name, sf.head student_faculty_head, "
                + "m.id major_id, m.code major_code, m.name major_name, "
                + "c.id class_id, c.code class_code, c.capacity, c.enrolled_count, c.status class_status, "
                + "sub.id subject_id, sub.code subject_code, sub.name subject_name, sub.credits, sub.content, sub.status subject_status, "
                + "subf.id subject_faculty_id, subf.code subject_faculty_code, subf.name subject_faculty_name, subf.head subject_faculty_head, "
                + "tu.id teacher_id, tu.username teacher_username, tu.password teacher_password, tu.name teacher_name, tu.status teacher_status, "
                + "t.email teacher_email, t.phone teacher_phone, tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head "
                + "FROM grades g "
                + "JOIN students st ON st.user_id = g.student_id JOIN users su ON su.id = st.user_id "
                + "JOIN faculties sf ON sf.id = st.faculty_id JOIN majors m ON m.id = st.major_id "
                + "JOIN class_sections c ON c.id = g.class_section_id "
                + "JOIN subjects sub ON sub.id = c.subject_id JOIN faculties subf ON subf.id = sub.faculty_id "
                + "JOIN teachers t ON t.user_id = c.teacher_id JOIN users tu ON tu.id = t.user_id JOIN faculties tf ON tf.id = t.faculty_id";
    }

    private Grade mapGrade(ResultSet rs) throws SQLException {
        // Faculty studentFaculty = new Faculty(rs.getInt("student_faculty_id"), rs.getString("student_faculty_code"),
                // rs.getString("student_faculty_name"), rs.getString("student_faculty_head"));
        // Major major = new Major(rs.getInt("major_id"), rs.getString("major_code"), rs.getString("major_name"), studentFaculty);
        Student student = new Student(rs.getInt("student_user_id"), rs.getString("student_username"), rs.getString("student_password"),
                rs.getString("student_name"), rs.getString("student_status_user"), rs.getString("mssv"), rs.getString("dob"),
                rs.getString("gender"), rs.getString("address"), rs.getString("student_email"), rs.getString("student_phone"),
                rs.getString("cohort"), rs.getString("admin_class"), rs.getString("student_status"));
        // Faculty subjectFaculty = new Faculty(rs.getInt("subject_faculty_id"), rs.getString("subject_faculty_code"),
        //         rs.getString("subject_faculty_name"), rs.getString("subject_faculty_head"));
        Subject subject = new Subject(rs.getInt("subject_id"), rs.getString("subject_code"), rs.getString("subject_name"),
                rs.getInt("credits"), rs.getString("content"), rs.getString("subject_status"));
        // Faculty teacherFaculty = new Faculty(rs.getInt("teacher_faculty_id"), rs.getString("teacher_faculty_code"),
                // rs.getString("teacher_faculty_name"), rs.getString("teacher_faculty_head"));
        Teacher teacher = new Teacher(rs.getInt("teacher_id"), rs.getString("teacher_username"), rs.getString("teacher_password"),
                rs.getString("teacher_name"), rs.getString("teacher_status"),
                rs.getString("teacher_email"), rs.getString("teacher_phone"));
        ClassSection classSection = new ClassSection(rs.getInt("class_id"), rs.getString("class_code"), subject, teacher,
                rs.getInt("capacity"), rs.getInt("enrolled_count"), rs.getString("class_status"));
        return new Grade(rs.getInt("id"), student, classSection, rs.getDouble("attendance_score"),
                rs.getDouble("midterm_score"), rs.getDouble("final_score"), rs.getString("semester"));
    }
}
