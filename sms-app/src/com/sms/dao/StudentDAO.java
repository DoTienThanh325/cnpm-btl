package com.sms.dao;

import com.sms.entity.*;
import java.util.*;

public class StudentDAO {
    private static List<Student> students = new ArrayList<>();
    private static int nextId = 10;
    private static int mssvCounter = 24001;

    static {
        FacultyDAO fDao = new FacultyDAO();
        MajorDAO mDao = new MajorDAO();
        Faculty cntt = fDao.getById(1);
        Major cnpm = mDao.getById(1);
        Major httt = mDao.getById(2);

        students.add(new Student(5, "sv01", "sv123", "Phạm Quang Vinh", "active",
                "B23DCCE100", "2005-01-15", "Nam", "Hà Nội", "pqv@ptit.edu.vn", "0912000001",
                cntt, cnpm, "2023", "D23CQCN01-B", "đang học"));
        students.add(new Student(6, "sv02", "sv123", "Đỗ Tiến Thành", "active",
                "B23DCDT239", "2005-03-20", "Nam", "Hà Nội", "dtt@ptit.edu.vn", "0912000002",
                cntt, httt, "2023", "D23CQCN01-B", "đang học"));
        students.add(new Student(7, "sv03", "sv123", "Nguyễn Chí Ngọc", "active",
                "B23DCCE073", "2005-07-10", "Nam", "TP.HCM", "ncn@ptit.edu.vn", "0912000003",
                cntt, cnpm, "2023", "D23CQCN02-B", "đang học"));
        students.add(new Student(8, "sv04", "sv123", "Đỗ Thiện Minh", "active",
                "B23DCCE064", "2005-11-05", "Nam", "Đà Nẵng", "dtm@ptit.edu.vn", "0912000004",
                cntt, cnpm, "2023", "D23CQCN02-B", "đang học"));
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public Student getById(int id) {
        for (Student s : students) if (s.getId() == id) return s;
        return null;
    }

    public Student getByMssv(String mssv) {
        for (Student s : students) if (s.getMssv().equals(mssv)) return s;
        return null;
    }

    // per spec: getStudentByClass()
    public List<Student> getStudentByClass(int classSectionId) {
        List<Student> result = new ArrayList<>();
        List<StudentEnrollment> enrollments = ClassSectionDAO.getEnrollments();
        for (StudentEnrollment e : enrollments) {
            if (e.getClassSectionId() == classSectionId) {
                Student s = getById(e.getStudentId());
                if (s != null) result.add(s);
            }
        }
        return result;
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> result = new ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(kw) || s.getMssv().toLowerCase().contains(kw)) {
                result.add(s);
            }
        }
        return result;
    }

    // auto-generate MSSV per spec
    public boolean addStudent(Student student) {
        String mssv = "B" + (mssvCounter++);
        student.setMssv(mssv);
        student.setId(nextId++);
        students.add(student);
        return true;
    }

    public boolean updateStudent(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == student.getId()) {
                students.set(i, student);
                return true;
            }
        }
        return false;
    }

    // soft delete per spec
    public boolean softDeleteStudent(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                s.setStudentStatus("nghỉ học");
                return true;
            }
        }
        return false;
    }
}
