package com.sms.dao;

import com.sms.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static List<User> users = new ArrayList<>();
    private static int nextId = 10;

    static {
        users.add(new User(1, "admin", "admin123", "Quản trị viên", "ADMIN", "active"));
        users.add(new User(2, "pdt01", "pdt123", "Nguyễn Văn Phòng", "PDT", "active"));
        users.add(new User(3, "gv01", "gv123", "Nguyễn Văn A", "TEACHER", "active"));
        users.add(new User(4, "gv02", "gv123", "Trần Thị B", "TEACHER", "active"));
        users.add(new User(5, "sv01", "sv123", "Phạm Quang Vinh", "STUDENT", "active"));
    }

    public User checkLogin(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)
                    && "active".equals(u.getStatus())) {
                return u;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public List<User> searchUsers(String keyword) {
        List<User> result = new ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        for (User u : users) {
            if (u.getName().toLowerCase().contains(kw) || u.getUsername().toLowerCase().contains(kw)
                    || String.valueOf(u.getId()).contains(kw)) {
                result.add(u);
            }
        }
        return result;
    }

    public boolean addUser(User user) {
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) return false;
        }
        user.setId(nextId++);
        users.add(user);
        return true;
    }

    public boolean deleteUser(int id) {
        return users.removeIf(u -> u.getId() == id);
    }

    public boolean updateUserRole(int id, String role) {
        for (User u : users) {
            if (u.getId() == id) { u.setRole(role); return true; }
        }
        return false;
    }
}
