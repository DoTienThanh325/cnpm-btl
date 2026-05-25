package com.sms.dao;

import com.sms.entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {
    public User checkLogin(String username, String password) {
        String sql = "SELECT id, username, password, name, role, status FROM users "
                + "WHERE username = ? AND password = ? AND status = 'active'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, name, role, status FROM users ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapUser(rs));
            return users;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password, name, role, status FROM users "
                + "WHERE LOWER(name) LIKE ? OR LOWER(username) LIKE ? OR CAST(id AS CHAR) LIKE ? ORDER BY id";
        String kw = "%" + keyword.toLowerCase().trim() + "%";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, name, role, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean deleteUser(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateUserRole(int id, String role) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET role = ? WHERE id = ?")) {
            ps.setString(1, role);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    static User mapUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                rs.getString("name"), rs.getString("role"), rs.getString("status"));
    }
}
