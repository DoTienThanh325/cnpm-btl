package com.sms.dao;

import com.sms.entity.Faculty;
import com.sms.entity.Major;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO {
    private static List<Major> majors = new ArrayList<>();
    private static int nextId = 4;
    private static FacultyDAO facultyDAO = new FacultyDAO();

    static {
        Faculty cntt = new FacultyDAO().getById(1);
        Faculty dtvt = new FacultyDAO().getById(2);
        Faculty ktkt = new FacultyDAO().getById(3);
        majors.add(new Major(1, "CNPM", "Công nghệ phần mềm", cntt));
        majors.add(new Major(2, "HTTT", "Hệ thống thông tin", cntt));
        majors.add(new Major(3, "DTVT", "Điện tử viễn thông", dtvt));
    }

    public List<Major> getAllMajors() {
        return new ArrayList<>(majors);
    }

    public Major getById(int id) {
        for (Major m : majors) if (m.getId() == id) return m;
        return null;
    }
}
