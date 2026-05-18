package com.sms.dao;

import com.sms.db.DBConnection;
import com.sms.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User checkLogin(String username, String password) {
        String sql = "SELECT id, username, password, name, role, status FROM users " +
                "WHERE username = ? AND password = ? AND status = 'active'";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("checkLogin failed", e);
        }
    }

    public List<User> getAllUsers() {
        List<User> out = new ArrayList<>();
        String sql = "SELECT id, username, password, name, role, status FROM users ORDER BY id";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("getAllUsers failed", e);
        }
        return out;
    }

    public List<User> searchUsers(String keyword) {
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        String sql = "SELECT id, username, password, name, role, status FROM users " +
                "WHERE LOWER(name) LIKE ? OR LOWER(username) LIKE ? OR CAST(id AS CHAR) LIKE ? " +
                "ORDER BY id";
        List<User> out = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("searchUsers failed", e);
        }
        return out;
    }

    public boolean addUser(User user) {
        String check = "SELECT 1 FROM users WHERE username = ?";
        String insert = "INSERT INTO users (username, password, name, role, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.get()) {
            try (PreparedStatement ps = c.prepareStatement(check)) {
                ps.setString(1, user.getUsername());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return false;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getName());
                ps.setString(4, user.getRole());
                ps.setString(5, user.getStatus() == null ? "active" : user.getStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) user.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("addUser failed", e);
        }
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("deleteUser failed", e);
        }
    }

    public boolean updateUserRole(int id, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("updateUserRole failed", e);
        }
    }

    static User map(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("role"), rs.getString("status"));
    }
}
