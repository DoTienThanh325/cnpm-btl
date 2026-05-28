package com.sms.dao;

import com.sms.entity.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassSectionDAO extends DAO {

    public List<ClassSection> getAllClassSections() {
        List<ClassSection> classes = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_class_sections", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) classes.add(mapClassSection(rs));
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public ClassSection getById(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_class_section_by_id", 1))) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapClassSection(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<ClassSection> searchClass(String keyword) {
        List<ClassSection> classes = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_search_class_sections", 1))) {
            ps.setString(1, keyword.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) classes.add(mapClassSection(rs));
            }
            return classes;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateClass(ClassSection classSection) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_update_class_section", 7))) {
            ps.setInt(1, classSection.getId());
            ps.setString(2, classSection.getCode());
            ps.setInt(3, classSection.getSubject().getId());
            ps.setInt(4, classSection.getTeacher().getId());
            ps.setInt(5, classSection.getCapacity());
            ps.setInt(6, classSection.getEnrolledCount());
            ps.setString(7, classSection.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean cancelClass(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_cancel_class_section", 1))) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static ClassSection mapClassSection(ResultSet rs) throws SQLException {
        Subject subject = new Subject(rs.getInt("subject_id"), rs.getString("subject_code"),
                rs.getString("subject_name"), rs.getInt("credits"), rs.getString("content"),
                rs.getString("subject_status"));
        Teacher teacher = new Teacher(rs.getInt("teacher_id"), rs.getString("username"),
                rs.getString("password"), rs.getString("teacher_name"), rs.getString("teacher_status"),
                rs.getString("teacher_email"), rs.getString("teacher_phone"));
        return new ClassSection(rs.getInt("id"), rs.getString("code"), subject, teacher,
                rs.getInt("capacity"), rs.getInt("enrolled_count"), rs.getString("status"));
    }
}
