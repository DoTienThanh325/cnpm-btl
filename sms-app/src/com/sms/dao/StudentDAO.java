package com.sms.dao;

import com.sms.entity.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO extends DAO {
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = baseSql() + " ORDER BY u.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) students.add(mapStudent(rs));
            return students;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Student getById(int id) {
        String sql = baseSql() + " WHERE u.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapStudent(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Student getByMssv(String mssv) {
        String sql = baseSql() + " WHERE st.mssv = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = baseSql() + " JOIN student_enrollments se ON se.student_id = st.user_id "
                + "WHERE se.class_section_id = ? ORDER BY u.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = baseSql() + " WHERE LOWER(u.name) LIKE ? OR LOWER(st.mssv) LIKE ? ORDER BY u.id";
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.add(mapStudent(rs));
            }
            return students;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean addStudent(Student student) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = insertUser(conn, student);
                student.setId(userId);
                String mssv = generateMssv(conn);
                student.setMssv(mssv);
                String sql = "INSERT INTO students(user_id, mssv, dob, gender, address, email, phone, faculty_id, major_id, cohort, admin_class, student_status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    fillStudentStatement(ps, student, 1);
                    ps.executeUpdate();
                }
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

    public boolean updateStudent(Student student) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement userPs = conn.prepareStatement(
                        "UPDATE users SET username = ?, password = ?, name = ?, status = ? WHERE id = ?")) {
                    userPs.setString(1, student.getUsername());
                    userPs.setString(2, student.getPassword());
                    userPs.setString(3, student.getName());
                    userPs.setString(4, student.getStatus());
                    userPs.setInt(5, student.getId());
                    userPs.executeUpdate();
                }
                String sql = "UPDATE students SET mssv = ?, dob = ?, gender = ?, address = ?, email = ?, phone = ?, "
                        + "faculty_id = ?, major_id = ?, cohort = ?, admin_class = ?, student_status = ? WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, student.getMssv());
                    ps.setString(2, student.getDob());
                    ps.setString(3, student.getGender());
                    ps.setString(4, student.getAddress());
                    ps.setString(5, student.getEmail());
                    ps.setString(6, student.getPhone());
                    // ps.setInt(7, student.getFaculty().getId());
                    // ps.setInt(8, student.getMajor().getId());
                    ps.setString(9, student.getCohort());
                    ps.setString(10, student.getAdminClass());
                    ps.setString(11, student.getStudentStatus());
                    ps.setInt(12, student.getId());
                    boolean updated = ps.executeUpdate() > 0;
                    conn.commit();
                    return updated;
                }
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

    public boolean softDeleteStudent(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE students SET student_status = ? WHERE user_id = ?")) {
            ps.setString(1, "nghỉ học");
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private String baseSql() {
        return "SELECT u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone, "
                + "st.cohort, st.admin_class, st.student_status, "
                + "f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head, "
                + "m.id major_id, m.code major_code, m.name major_name "
                + "FROM students st JOIN users u ON u.id = st.user_id "
                + "JOIN faculties f ON f.id = st.faculty_id "
                + "JOIN majors m ON m.id = st.major_id";
    }

    private int insertUser(Connection conn, Student student) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users(username, password, name, role, status) VALUES (?, ?, ?, 'STUDENT', ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getPassword());
            ps.setString(3, student.getName());
            ps.setString(4, student.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Cannot create student user");
    }

    private String generateMssv(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(CASE WHEN mssv REGEXP '^B[0-9]+$' "
                + "THEN CAST(SUBSTRING(mssv, 2) AS UNSIGNED) END), 24000) + 1 next_no FROM students";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return "B" + rs.getInt("next_no");
        }
    }

    private void fillStudentStatement(PreparedStatement ps, Student student, int start) throws SQLException {
        ps.setInt(start, student.getId());
        ps.setString(start + 1, student.getMssv());
        ps.setString(start + 2, student.getDob());
        ps.setString(start + 3, student.getGender());
        ps.setString(start + 4, student.getAddress());
        ps.setString(start + 5, student.getEmail());
        ps.setString(start + 6, student.getPhone());
        // ps.setInt(start + 7, student.getFaculty().getId());
        // ps.setInt(start + 8, student.getMajor().getId());
        ps.setString(start + 9, student.getCohort());
        ps.setString(start + 10, student.getAdminClass());
        ps.setString(start + 11, student.getStudentStatus());
    }

    static Student mapStudent(ResultSet rs) throws SQLException {
        // Faculty faculty = new Faculty(rs.getInt("faculty_id"), rs.getString("faculty_code"),
        //         rs.getString("faculty_name"), rs.getString("head"));
        // Major major = new Major(rs.getInt("major_id"), rs.getString("major_code"), rs.getString("major_name"), faculty);
        return new Student(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("status"), rs.getString("mssv"), rs.getString("dob"),
                rs.getString("gender"), rs.getString("address"), rs.getString("email"), rs.getString("phone"),
                 rs.getString("cohort"), rs.getString("admin_class"), rs.getString("student_status"));
    }
}
