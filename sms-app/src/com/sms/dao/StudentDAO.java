package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO extends DAO {
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_students", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) students.add(mapStudent(rs));
            return students;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Student getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_student_by_id", 1))) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStudent(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Student getByMssv(String mssv) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_student_by_mssv", 1))) {
            ps.setString(1, mssv);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStudent(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Student> getStudentByClass(int classSectionId) {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_students_by_class", 1))) {
            ps.setInt(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.add(mapStudent(rs));
            }
            return students;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_search_students", 1))) {
            ps.setString(1, keyword.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.add(mapStudent(rs));
            }
            return students;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean addStudent(Student student) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_add_student", 16))) {
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getName());
            ps.setString(4, student.getStatus());
            ps.setString(5, student.getDob());
            ps.setString(6, student.getGender());
            ps.setString(7, student.getAddress());
            ps.setString(8, student.getEmail());
            ps.setString(9, student.getPhone());
            ps.setInt(10, student.getFaculty().getId());
            ps.setInt(11, student.getMajor().getId());
            ps.setString(12, student.getCohort());
            ps.setString(13, student.getAdminClass());
            ps.setString(14, student.getStudentStatus());
            ps.registerOutParameter(15, Types.INTEGER);
            ps.registerOutParameter(16, Types.VARCHAR);
            ps.executeUpdate();
            student.setId(ps.getInt(15));
            student.setMssv(ps.getString(16));
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateStudent(Student student) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_update_student", 16))) {
            ps.setInt(1, student.getId());
            ps.setString(2, student.getUsername());
            ps.setString(3, student.getPassword());
            ps.setString(4, student.getName());
            ps.setString(5, student.getStatus());
            ps.setString(6, student.getMssv());
            ps.setString(7, student.getDob());
            ps.setString(8, student.getGender());
            ps.setString(9, student.getAddress());
            ps.setString(10, student.getEmail());
            ps.setString(11, student.getPhone());
            ps.setInt(12, student.getFaculty().getId());
            ps.setInt(13, student.getMajor().getId());
            ps.setString(14, student.getCohort());
            ps.setString(15, student.getAdminClass());
            ps.setString(16, student.getStudentStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean softDeleteStudent(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_soft_delete_student", 1))) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Student mapStudent(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty(rs.getInt("faculty_id"), rs.getString("faculty_code"),
                rs.getString("faculty_name"), rs.getString("head"));
        Major major = new Major(rs.getInt("major_id"), rs.getString("major_code"), rs.getString("major_name"), faculty);
        return new Student(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("status"), rs.getString("mssv"), rs.getString("dob"),
                rs.getString("gender"), rs.getString("address"), rs.getString("email"), rs.getString("phone"),
                faculty, major, rs.getString("cohort"), rs.getString("admin_class"), rs.getString("student_status"));
    }
}
