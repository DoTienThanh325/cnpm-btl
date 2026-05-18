package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.Student;
import com.sms.entity.Tuition;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TuitionDAO {

    public List<Tuition> getAllTuitions() {
        return loadList(null, -1);
    }

    public List<Tuition> getByStudent(int studentId) {
        return loadList("student_id = ?", studentId);
    }

    public boolean updateTuition(Tuition tuition) {
        String sql = "UPDATE tuitions SET semester = ?, registered_credits = ?, price_per_credit = ?, " +
                "paid = ?, status = ? WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tuition.getSemester());
            ps.setInt(2, tuition.getRegisteredCredits());
            ps.setDouble(3, tuition.getPricePerCredit());
            ps.setDouble(4, tuition.getPaid());
            ps.setString(5, tuition.getStatus());
            ps.setInt(6, tuition.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateTuition failed", e);
        }
    }

    public boolean applyDiscount(int tuitionId, String reason) {
        String sql = "UPDATE tuitions SET status = 'miễn giảm', paid = 0 WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tuitionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("applyDiscount failed", e);
        }
    }

    public boolean payTuition(int tuitionId) {
        String sql = "UPDATE tuitions SET paid = registered_credits * price_per_credit, status = 'đã đóng' WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tuitionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("payTuition failed", e);
        }
    }

    private List<Tuition> loadList(String whereClause, int param) {
        String sql = "SELECT id, student_id, semester, registered_credits, price_per_credit, paid, status " +
                "FROM tuitions" + (whereClause == null ? "" : " WHERE " + whereClause) + " ORDER BY id";
        List<Tuition> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (whereClause != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(hydrate(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("loadList tuitions failed", e);
        }
        return out;
    }

    private Tuition hydrate(ResultSet rs) throws SQLException {
        StudentDAO sDao = new StudentDAO();
        Student s = sDao.getById(rs.getInt("student_id"));
        return new Tuition(rs.getInt("id"), s, rs.getString("semester"),
                rs.getInt("registered_credits"), rs.getDouble("price_per_credit"),
                rs.getDouble("paid"), rs.getString("status"));
    }
}
