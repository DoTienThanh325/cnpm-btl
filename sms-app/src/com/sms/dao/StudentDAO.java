package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String SELECT_BASE =
            "SELECT u.id AS u_id, u.username, u.password, u.name, u.status AS u_status, " +
            "       s.mssv, s.dob, s.gender, s.address, s.email, s.phone, " +
            "       s.cohort, s.admin_class, s.student_status, " +
            "       f.id AS f_id, f.code AS f_code, f.name AS f_name, f.head AS f_head, " +
            "       m.id AS m_id, m.code AS m_code, m.name AS m_name, " +
            "       mf.id AS mf_id, mf.code AS mf_code, mf.name AS mf_name, mf.head AS mf_head " +
            "FROM students s " +
            "JOIN users u ON u.id = s.id " +
            "LEFT JOIN faculties f ON s.faculty_id = f.id " +
            "LEFT JOIN majors m ON s.major_id = m.id " +
            "LEFT JOIN faculties mf ON m.faculty_id = mf.id";

    public List<Student> getAllStudents() {
        List<Student> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " ORDER BY u.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllStudents failed", e);
        }
        return out;
    }

    public Student getById(int id) {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE u.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getById student failed", e);
        }
    }

    public Student getByMssv(String mssv) {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(SELECT_BASE + " WHERE s.mssv = ?")) {
            ps.setString(1, mssv);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getByMssv failed", e);
        }
    }

    public List<Student> getStudentByClass(int classSectionId) {
        List<Student> out = new ArrayList<>();
        String sql = SELECT_BASE + " JOIN enrollments e ON e.student_id = u.id WHERE e.class_section_id = ? ORDER BY u.id";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getStudentByClass failed", e);
        }
        return out;
    }

    public List<Student> searchStudents(String keyword) {
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        List<Student> out = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE LOWER(u.name) LIKE ? OR LOWER(s.mssv) LIKE ? ORDER BY u.id";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("searchStudents failed", e);
        }
        return out;
    }

    public boolean addStudent(Student student) {
        String insertUser = "INSERT INTO users (username, password, name, role, status) VALUES (?, ?, ?, 'STUDENT', ?)";
        String insertStudent = "INSERT INTO students (id, mssv, dob, gender, address, email, phone, " +
                "faculty_id, major_id, cohort, admin_class, student_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            try {
                int newId;
                try (PreparedStatement ps = c.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, student.getUsername());
                    ps.setString(2, student.getPassword());
                    ps.setString(3, student.getName());
                    ps.setString(4, student.getStatus() == null ? "active" : student.getStatus());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("no user id generated");
                        newId = keys.getInt(1);
                    }
                }
                String mssv = nextMssv(c);
                try (PreparedStatement ps = c.prepareStatement(insertStudent)) {
                    ps.setInt(1, newId);
                    ps.setString(2, mssv);
                    ps.setString(3, student.getDob());
                    ps.setString(4, student.getGender());
                    ps.setString(5, student.getAddress());
                    ps.setString(6, student.getEmail());
                    ps.setString(7, student.getPhone());
                    setIntOrNull(ps, 8, student.getFaculty() != null ? student.getFaculty().getId() : null);
                    setIntOrNull(ps, 9, student.getMajor() != null ? student.getMajor().getId() : null);
                    ps.setString(10, student.getCohort());
                    ps.setString(11, student.getAdminClass());
                    ps.setString(12, student.getStudentStatus() == null ? "đang học" : student.getStudentStatus());
                    ps.executeUpdate();
                }
                c.commit();
                student.setId(newId);
                student.setMssv(mssv);
                return true;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("addStudent failed", e);
        }
    }

    public boolean updateStudent(Student student) {
        String updUser = "UPDATE users SET name = ?, username = ?, password = ?, status = ? WHERE id = ?";
        String updStudent = "UPDATE students SET dob = ?, gender = ?, address = ?, email = ?, phone = ?, " +
                "faculty_id = ?, major_id = ?, cohort = ?, admin_class = ?, student_status = ? WHERE id = ?";
        try (Connection c = DBConnection.get()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(updUser)) {
                    ps.setString(1, student.getName());
                    ps.setString(2, student.getUsername());
                    ps.setString(3, student.getPassword());
                    ps.setString(4, student.getStatus() == null ? "active" : student.getStatus());
                    ps.setInt(5, student.getId());
                    ps.executeUpdate();
                }
                int rows;
                try (PreparedStatement ps = c.prepareStatement(updStudent)) {
                    ps.setString(1, student.getDob());
                    ps.setString(2, student.getGender());
                    ps.setString(3, student.getAddress());
                    ps.setString(4, student.getEmail());
                    ps.setString(5, student.getPhone());
                    setIntOrNull(ps, 6, student.getFaculty() != null ? student.getFaculty().getId() : null);
                    setIntOrNull(ps, 7, student.getMajor() != null ? student.getMajor().getId() : null);
                    ps.setString(8, student.getCohort());
                    ps.setString(9, student.getAdminClass());
                    ps.setString(10, student.getStudentStatus());
                    ps.setInt(11, student.getId());
                    rows = ps.executeUpdate();
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
            throw new RuntimeException("updateStudent failed", e);
        }
    }

    public boolean softDeleteStudent(int id) {
        String sql = "UPDATE students SET student_status = 'nghỉ học' WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("softDeleteStudent failed", e);
        }
    }

    private String nextMssv(Connection c) throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(mssv, 2) AS UNSIGNED)), 24000) + 1 AS nxt " +
                "FROM students WHERE mssv REGEXP '^B[0-9]+$'";
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int n = rs.next() ? rs.getInt("nxt") : 24001;
            return "B" + n;
        }
    }

    private static void setIntOrNull(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, v);
    }

    static Student map(ResultSet rs) throws SQLException {
        Faculty studentFaculty = null;
        int fid = rs.getInt("f_id");
        if (!rs.wasNull()) {
            studentFaculty = new Faculty(fid, rs.getString("f_code"),
                    rs.getString("f_name"), rs.getString("f_head"));
        }
        Major major = null;
        int mid = rs.getInt("m_id");
        if (!rs.wasNull()) {
            Faculty majorFaculty = null;
            int mfid = rs.getInt("mf_id");
            if (!rs.wasNull()) {
                majorFaculty = new Faculty(mfid, rs.getString("mf_code"),
                        rs.getString("mf_name"), rs.getString("mf_head"));
            }
            major = new Major(mid, rs.getString("m_code"), rs.getString("m_name"), majorFaculty);
        }
        return new Student(
                rs.getInt("u_id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("u_status"),
                rs.getString("mssv"), rs.getString("dob"), rs.getString("gender"),
                rs.getString("address"), rs.getString("email"), rs.getString("phone"),
                studentFaculty, major, rs.getString("cohort"),
                rs.getString("admin_class"), rs.getString("student_status"));
    }
}
