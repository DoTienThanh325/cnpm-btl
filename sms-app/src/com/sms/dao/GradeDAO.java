package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.ClassSection;
import com.sms.entity.Grade;
import com.sms.entity.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    public Grade getGradeByStudentAndClass(int studentId, int classSectionId) {
        String sql = "SELECT id, student_id, class_section_id, attendance_score, midterm_score, final_score, semester " +
                "FROM grades WHERE student_id = ? AND class_section_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? hydrate(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getGradeByStudentAndClass failed", e);
        }
    }

    public List<Grade> getGradesByStudent(int studentId) {
        return loadList("student_id = ?", studentId);
    }

    public List<Grade> getGradesByClass(int classSectionId) {
        return loadList("class_section_id = ?", classSectionId);
    }

    public boolean updateGrade(Grade grade) {
        String sql = "UPDATE grades SET attendance_score = ?, midterm_score = ?, final_score = ?, semester = ? WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, grade.getAttendanceScore());
            ps.setDouble(2, grade.getMidtermScore());
            ps.setDouble(3, grade.getFinalScore());
            ps.setString(4, grade.getSemester());
            ps.setInt(5, grade.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateGrade failed", e);
        }
    }

    public boolean addGrade(Grade grade) {
        String sql = "INSERT INTO grades (student_id, class_section_id, attendance_score, midterm_score, final_score, semester) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        } catch (SQLException e) {
            throw new RuntimeException("addGrade failed", e);
        }
    }

    public void ensureGradesForClass(int classSectionId, List<Student> students, ClassSection cs) {
        String sql = "INSERT IGNORE INTO grades (student_id, class_section_id, attendance_score, midterm_score, final_score, semester) " +
                "VALUES (?, ?, 0, 0, 0, ?)";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (Student s : students) {
                ps.setInt(1, s.getId());
                ps.setInt(2, classSectionId);
                ps.setString(3, "2024-2");
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("ensureGradesForClass failed", e);
        }
    }

    private List<Grade> loadList(String whereClause, int param) {
        String sql = "SELECT id, student_id, class_section_id, attendance_score, midterm_score, final_score, semester " +
                "FROM grades WHERE " + whereClause + " ORDER BY id";
        List<Grade> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(hydrate(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("loadList grades failed", e);
        }
        return out;
    }

    private Grade hydrate(ResultSet rs) throws SQLException {
        StudentDAO sDao = new StudentDAO();
        ClassSectionDAO cDao = new ClassSectionDAO();
        Student student = sDao.getById(rs.getInt("student_id"));
        ClassSection cs = cDao.getById(rs.getInt("class_section_id"));
        Grade g = new Grade(rs.getInt("id"), student, cs,
                rs.getDouble("attendance_score"), rs.getDouble("midterm_score"),
                rs.getDouble("final_score"), rs.getString("semester"));
        return g;
    }
}
