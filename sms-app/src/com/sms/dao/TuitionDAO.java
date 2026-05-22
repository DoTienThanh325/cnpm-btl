package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TuitionDAO extends DAO {
    public List<Tuition> getAllTuitions() {
        List<Tuition> tuitions = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_tuitions", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) tuitions.add(mapTuition(rs));
            return tuitions;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<Tuition> getByStudent(int studentId) {
        List<Tuition> tuitions = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_tuitions_by_student", 1))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_update_tuition", 6))) {
            ps.setInt(1, tuition.getId());
            ps.setString(2, tuition.getSemester());
            ps.setInt(3, tuition.getRegisteredCredits());
            ps.setDouble(4, tuition.getPricePerCredit());
            ps.setDouble(5, tuition.getPaid());
            ps.setString(6, tuition.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean applyDiscount(int tuitionId, String reason) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_apply_tuition_discount", 1))) {
            ps.setInt(1, tuitionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean payTuition(int tuitionId) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_pay_tuition", 1))) {
            ps.setInt(1, tuitionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
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
