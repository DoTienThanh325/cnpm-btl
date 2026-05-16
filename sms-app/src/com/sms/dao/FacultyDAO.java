package com.sms.dao;

import com.sms.entity.Faculty;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {
    private static List<Faculty> faculties = new ArrayList<>();
    private static int nextId = 4;

    static {
        faculties.add(new Faculty(1, "CNTT", "Công nghệ Thông tin", "PGS. TS. Nguyễn Văn X"));
        faculties.add(new Faculty(2, "DTVT", "Điện tử Viễn thông", "TS. Trần Thị Y"));
        faculties.add(new Faculty(3, "KTKT", "Kỹ thuật Kinh tế", "TS. Lê Văn Z"));
    }

    public List<Faculty> getAllFaculties() {
        return new ArrayList<>(faculties);
    }

    public Faculty getById(int id) {
        for (Faculty f : faculties) if (f.getId() == id) return f;
        return null;
    }
}
