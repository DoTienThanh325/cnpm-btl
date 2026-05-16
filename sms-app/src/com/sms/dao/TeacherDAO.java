package com.sms.dao;

import com.sms.entity.Faculty;
import com.sms.entity.Teacher;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {
    private static List<Teacher> teachers = new ArrayList<>();
    private static int nextId = 5;

    static {
        FacultyDAO fDao = new FacultyDAO();
        Faculty cntt = fDao.getById(1);
        Faculty dtvt = fDao.getById(2);
        teachers.add(new Teacher(3, "gv01", "gv123", "Nguyễn Văn A", "active", cntt, "nva@ptit.edu.vn", "0901000001"));
        teachers.add(new Teacher(4, "gv02", "gv123", "Trần Thị B", "active", cntt, "ttb@ptit.edu.vn", "0901000002"));
        teachers.add(new Teacher(6, "gv03", "gv123", "Lê Văn C", "active", dtvt, "lvc@ptit.edu.vn", "0901000003"));
    }

    // per spec: getAllTeacher()
    public List<Teacher> getAllTeacher() {
        return new ArrayList<>(teachers);
    }

    public Teacher getById(int id) {
        for (Teacher t : teachers) if (t.getId() == id) return t;
        return null;
    }

    public List<Teacher> searchTeachers(String keyword) {
        List<Teacher> result = new ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        for (Teacher t : teachers) {
            if (t.getName().toLowerCase().contains(kw)) result.add(t);
        }
        return result;
    }
}
