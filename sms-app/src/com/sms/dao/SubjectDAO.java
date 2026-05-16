package com.sms.dao;

import com.sms.entity.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    private static List<Subject> subjects = new ArrayList<>();
    private static int nextId = 5;

    static {
        FacultyDAO fDao = new FacultyDAO();
        Faculty cntt = fDao.getById(1);
        Faculty dtvt = fDao.getById(2);

        Subject s1 = new Subject(1, "INT1001", "Nhập môn CNPM", 3, "Giới thiệu về công nghệ phần mềm", cntt, "active");
        Subject s2 = new Subject(2, "MAT1001", "Giải tích 1", 4, "Giải tích toán học cơ bản", cntt, "active");
        Subject s3 = new Subject(3, "INT1002", "Lập trình Java", 3, "Ngôn ngữ lập trình Java", cntt, "active");
        Subject s4 = new Subject(4, "ELT1001", "Điện tử cơ bản", 3, "Kiến thức điện tử cơ bản", dtvt, "active");

        TextbookDAO tbDao = new TextbookDAO();
        s1.getTextbooks().add(tbDao.getById(1));
        s2.getTextbooks().add(tbDao.getById(2));
        s3.getTextbooks().add(tbDao.getById(3));
        s4.getTextbooks().add(tbDao.getById(4));

        subjects.add(s1);
        subjects.add(s2);
        subjects.add(s3);
        subjects.add(s4);
    }

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }

    // per spec: getAllSubject()
    public List<Subject> getAllSubject() {
        return getAllSubjects();
    }

    public Subject getById(int id) {
        for (Subject s : subjects) if (s.getId() == id) return s;
        return null;
    }

    public List<Subject> searchSubjects(String keyword) {
        List<Subject> result = new ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        for (Subject s : subjects) {
            if (s.getName().toLowerCase().contains(kw) || s.getCode().toLowerCase().contains(kw)) {
                result.add(s);
            }
        }
        return result;
    }

    public boolean createSubject(Subject subject) {
        for (Subject s : subjects) {
            if (s.getCode().equals(subject.getCode())) return false;
        }
        subject.setId(nextId++);
        subjects.add(subject);
        return true;
    }

    public boolean updateSubject(Subject subject) {
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getId() == subject.getId()) {
                subjects.set(i, subject);
                return true;
            }
        }
        return false;
    }

    public boolean deleteSubject(int id) {
        return subjects.removeIf(s -> s.getId() == id);
    }
}
