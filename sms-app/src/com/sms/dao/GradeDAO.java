package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO extends DAO {
    public Grade getGradeByStudentAndClass(int studentId, int classSectionId) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_grade_by_student_and_class", 2))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_grades_by_student", 1))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_grades_by_class", 1))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_update_grade", 5))) {
            ps.setInt(1, grade.getId());
            ps.setDouble(2, grade.getAttendanceScore());
            ps.setDouble(3, grade.getMidtermScore());
            ps.setDouble(4, grade.getFinalScore());
            ps.setString(5, grade.getSemester());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean addGrade(Grade grade) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_add_grade", 7))) {
            ps.setInt(1, grade.getStudent().getId());
            ps.setInt(2, grade.getClassSection().getId());
            ps.setDouble(3, grade.getAttendanceScore());
            ps.setDouble(4, grade.getMidtermScore());
            ps.setDouble(5, grade.getFinalScore());
            ps.setString(6, grade.getSemester());
            ps.registerOutParameter(7, Types.INTEGER);
            ps.executeUpdate();
            grade.setId(ps.getInt(7));
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public void ensureGradesForClass(int classSectionId, List<Student> students, ClassSection cs) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_ensure_grade_for_student_class", 2))) {
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

    private Grade mapGrade(ResultSet rs) throws SQLException {
        Faculty studentFaculty = new Faculty(rs.getInt("student_faculty_id"), rs.getString("student_faculty_code"),
                rs.getString("student_faculty_name"), rs.getString("student_faculty_head"));
        Major major = new Major(rs.getInt("major_id"), rs.getString("major_code"), rs.getString("major_name"), studentFaculty);
        Student student = new Student(rs.getInt("student_user_id"), rs.getString("student_username"), rs.getString("student_password"),
                rs.getString("student_name"), rs.getString("student_status_user"), rs.getString("mssv"), rs.getString("dob"),
                rs.getString("gender"), rs.getString("address"), rs.getString("student_email"), rs.getString("student_phone"),
                studentFaculty, major, rs.getString("cohort"), rs.getString("admin_class"), rs.getString("student_status"));
        Faculty subjectFaculty = new Faculty(rs.getInt("subject_faculty_id"), rs.getString("subject_faculty_code"),
                rs.getString("subject_faculty_name"), rs.getString("subject_faculty_head"));
        Subject subject = new Subject(rs.getInt("subject_id"), rs.getString("subject_code"), rs.getString("subject_name"),
                rs.getInt("credits"), rs.getString("content"), subjectFaculty, rs.getString("subject_status"));
        Faculty teacherFaculty = new Faculty(rs.getInt("teacher_faculty_id"), rs.getString("teacher_faculty_code"),
                rs.getString("teacher_faculty_name"), rs.getString("teacher_faculty_head"));
        Teacher teacher = new Teacher(rs.getInt("teacher_id"), rs.getString("teacher_username"), rs.getString("teacher_password"),
                rs.getString("teacher_name"), rs.getString("teacher_status"), teacherFaculty,
                rs.getString("teacher_email"), rs.getString("teacher_phone"));
        ClassSection classSection = new ClassSection(rs.getInt("class_id"), rs.getString("class_code"), subject, teacher,
                rs.getInt("capacity"), rs.getInt("enrolled_count"), rs.getString("class_status"));
        return new Grade(rs.getInt("id"), student, classSection, rs.getDouble("attendance_score"),
                rs.getDouble("midterm_score"), rs.getDouble("final_score"), rs.getString("semester"));
    }
}
