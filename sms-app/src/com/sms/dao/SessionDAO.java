package com.sms.dao;

import com.sms.entity.Session;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {
    private static List<Session> sessions = new ArrayList<>();
    private static int nextId = 7;

    static {
        sessions.add(new Session(1, "Thứ 2", 1, 3, "A101"));
        sessions.add(new Session(2, "Thứ 2", 4, 6, "A102"));
        sessions.add(new Session(3, "Thứ 3", 1, 3, "B201"));
        sessions.add(new Session(4, "Thứ 4", 4, 6, "B202"));
        sessions.add(new Session(5, "Thứ 5", 1, 3, "C301"));
        sessions.add(new Session(6, "Thứ 6", 4, 6, "C302"));
    }

    // per spec: getAllSession()
    public List<Session> getAllSession() {
        return new ArrayList<>(sessions);
    }

    public Session getById(int id) {
        for (Session s : sessions) if (s.getId() == id) return s;
        return null;
    }
}
