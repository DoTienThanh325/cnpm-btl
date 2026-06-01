package com.sms.dao;

import com.sms.entity.Major;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO extends DAO {
    public List<Major> getAllMajors() {
        List<Major> majors = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_majors", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) majors.add(mapMajor(rs));
            return majors;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public Major getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_major_by_id", 1))) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapMajor(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static Major mapMajor(ResultSet rs) throws SQLException {
        return new Major(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getString("name"),
                new com.sms.entity.Faculty(
                        rs.getInt("faculty_id"),
                        rs.getString("faculty_code"),
                        rs.getString("faculty_name"),
                        rs.getString("head")));
    }
}
