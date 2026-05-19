package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TuitionDAO extends DAO {
    public List<Tuition> getAllTuitions() {
        List<Tuition> tuitions = new ArrayList<>();
        String sql = baseSql() + " ORDER BY tu.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) tuitions.add(mapTuition(rs));
            return tuitions;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Tuition> getByStudent(int studentId) {
        List<Tuition> tuitions = new ArrayList<>();
        String sql = baseSql() + " WHERE tu.student_id = ? ORDER BY tu.id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tuitions.add(mapTuition(rs));
            }
            return tuitions;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateTuition(Tuition tuition) {
        String sql = "UPDATE tuitions SET semester = ?, registered_credits = ?, price_per_credit = ?, paid = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tuition.getSemester());
            ps.setInt(2, tuition.getRegisteredCredits());
            ps.setDouble(3, tuition.getPricePerCredit());
            ps.setDouble(4, tuition.getPaid());
            ps.setString(5, tuition.getStatus());
            ps.setInt(6, tuition.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean applyDiscount(int tuitionId, String reason) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE tuitions SET status = ?, paid = 0 WHERE id = ?")) {
            ps.setString(1, "miễn giảm");
            ps.setInt(2, tuitionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean payTuition(int tuitionId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE tuitions SET paid = registered_credits * price_per_credit, status = ? WHERE id = ?")) {
            ps.setString(1, "đã đóng");
            ps.setInt(2, tuitionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    private String baseSql() {
        return "SELECT tu.id tuition_id, tu.semester, tu.registered_credits, tu.price_per_credit, tu.paid, tu.status tuition_status, "
                + "u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone, "
                + "st.cohort, st.admin_class, st.student_status, "
                + "f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head, "
                + "m.id major_id, m.code major_code, m.name major_name "
                + "FROM tuitions tu JOIN students st ON st.user_id = tu.student_id JOIN users u ON u.id = st.user_id "
                + "JOIN faculties f ON f.id = st.faculty_id JOIN majors m ON m.id = st.major_id";
    }

    private Tuition mapTuition(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty(rs.getInt("faculty_id"), rs.getString("faculty_code"),
                rs.getString("faculty_name"), rs.getString("head"));
        Major major = new Major(rs.getInt("major_id"), rs.getString("major_code"), rs.getString("major_name"), faculty);
        Student student = new Student(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("status"), rs.getString("mssv"), rs.getString("dob"),
                rs.getString("gender"), rs.getString("address"), rs.getString("email"), rs.getString("phone"),
                faculty, major, rs.getString("cohort"), rs.getString("admin_class"), rs.getString("student_status"));
        return new Tuition(rs.getInt("tuition_id"), student, rs.getString("semester"),
                rs.getInt("registered_credits"), rs.getDouble("price_per_credit"),
                rs.getDouble("paid"), rs.getString("tuition_status"));
    }
}
