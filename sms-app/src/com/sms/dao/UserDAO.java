package com.sms.dao;

import com.sms.entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {
    public User checkLogin(String username, String password) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_check_login", 2))) {
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
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_get_all_users", 0));
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapUser(rs));
            return users;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_search_users", 1))) {
            ps.setString(1, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean addUser(User user) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_add_user", 6))) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getStatus());
            ps.registerOutParameter(6, Types.INTEGER);
            ps.executeUpdate();
            user.setId(ps.getInt(6));
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean deleteUser(int id) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_delete_user", 1))) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw dbError(e);
        }
    }

    public boolean updateUserRole(int id, String role) {
        try (Connection conn = getConnection();
             CallableStatement ps = conn.prepareCall(call("sp_update_user_role", 2))) {
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
