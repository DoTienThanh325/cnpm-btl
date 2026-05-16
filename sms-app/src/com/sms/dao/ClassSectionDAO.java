package com.sms.dao;

import com.sms.entity.*;
import java.util.*;

public class ClassSectionDAO {
    private static List<ClassSection> classSections = new ArrayList<>();
    private static List<StudentEnrollment> enrollments = new ArrayList<>();
    private static int nextId = 5;

    static {
        SubjectDAO sDao = new SubjectDAO();
        TeacherDAO tDao = new TeacherDAO();
        SessionDAO seDao = new SessionDAO();

        ClassSection c1 = new ClassSection(1, "INT1234", sDao.getById(1), tDao.getById(3), 60, 40, "active");
        c1.getSessions().add(seDao.getById(1));

        ClassSection c2 = new ClassSection(2, "INT1234_02", sDao.getById(1), tDao.getById(4), 50, 35, "active");
        c2.getSessions().add(seDao.getById(3));

        ClassSection c3 = new ClassSection(3, "B1", sDao.getById(2), tDao.getById(3), 40, 40, "active");
        c3.getSessions().add(seDao.getById(2));

        ClassSection c4 = new ClassSection(4, "INT1002_01", sDao.getById(3), tDao.getById(4), 45, 30, "active");
        c4.getSessions().add(seDao.getById(4));

        classSections.add(c1);
        classSections.add(c2);
        classSections.add(c3);
        classSections.add(c4);

        // enrollments: students in class sections
        enrollments.add(new StudentEnrollment(5, 1));  // sv01 in c1
        enrollments.add(new StudentEnrollment(6, 1));  // sv02 in c1
        enrollments.add(new StudentEnrollment(7, 1));  // sv03 in c1
        enrollments.add(new StudentEnrollment(5, 3));  // sv01 in B1
        enrollments.add(new StudentEnrollment(6, 3));  // sv02 in B1
        enrollments.add(new StudentEnrollment(7, 3));  // sv03 in B1
        enrollments.add(new StudentEnrollment(8, 3));  // sv04 in B1
    }

    public static List<StudentEnrollment> getEnrollments() {
        return enrollments;
    }

    public List<ClassSection> getAllClassSections() {
        return new ArrayList<>(classSections);
    }

    public ClassSection getById(int id) {
        for (ClassSection c : classSections) if (c.getId() == id) return c;
        return null;
    }

    // per spec: searchClass()
    public List<ClassSection> searchClass(String keyword) {
        List<ClassSection> result = new ArrayList<>();
        String kw = keyword.toLowerCase().trim();
        for (ClassSection c : classSections) {
            if (c.getCode().toLowerCase().contains(kw)
                    || (c.getSubject() != null && c.getSubject().getName().toLowerCase().contains(kw))) {
                result.add(c);
            }
        }
        return result;
    }

    // per spec: checkSchedule()
    public boolean checkSchedule(Teacher teacher, List<Session> newSessions) {
        for (ClassSection c : classSections) {
            if (c.getTeacher() != null && c.getTeacher().getId() == teacher.getId()) {
                for (Session existing : c.getSessions()) {
                    for (Session newSess : newSessions) {
                        if (existing.getDayOfWeek().equals(newSess.getDayOfWeek())) {
                            boolean overlap = newSess.getStartPeriod() <= existing.getEndPeriod()
                                    && newSess.getEndPeriod() >= existing.getStartPeriod();
                            if (overlap) return true; // conflict found
                        }
                    }
                }
            }
        }
        return false; // no conflict
    }

    // per spec: validateClassData()
    public boolean validateClassData(int capacity, List<Session> sessions) {
        if (capacity <= 0) return false;
        if (sessions == null || sessions.isEmpty()) return false;
        return true;
    }

    // per spec: createClass()
    public boolean createClass(ClassSection classSection) {
        classSection.setId(nextId++);
        classSections.add(classSection);
        return true;
    }

    // per spec: updateClass()
    public boolean updateClass(ClassSection classSection) {
        for (int i = 0; i < classSections.size(); i++) {
            if (classSections.get(i).getId() == classSection.getId()) {
                classSections.set(i, classSection);
                return true;
            }
        }
        return false;
    }

    public boolean cancelClass(int id) {
        for (ClassSection c : classSections) {
            if (c.getId() == id) { c.setStatus("cancelled"); return true; }
        }
        return false;
    }

    // get classes taught by a specific teacher
    public List<ClassSection> getByTeacher(int teacherId) {
        List<ClassSection> result = new ArrayList<>();
        for (ClassSection c : classSections) {
            if (c.getTeacher() != null && c.getTeacher().getId() == teacherId) {
                result.add(c);
            }
        }
        return result;
    }

    // enroll student in class
    public boolean enrollStudent(int studentId, int classSectionId) {
        for (StudentEnrollment e : enrollments) {
            if (e.getStudentId() == studentId && e.getClassSectionId() == classSectionId) return false;
        }
        ClassSection cs = getById(classSectionId);
        if (cs != null && cs.getEnrolledCount() < cs.getCapacity()) {
            enrollments.add(new StudentEnrollment(studentId, classSectionId));
            cs.setEnrolledCount(cs.getEnrolledCount() + 1);
            return true;
        }
        return false;
    }

    // cancel student enrollment
    public boolean cancelEnrollment(int studentId, int classSectionId) {
        boolean removed = enrollments.removeIf(e -> e.getStudentId() == studentId
                && e.getClassSectionId() == classSectionId);
        if (removed) {
            ClassSection cs = getById(classSectionId);
            if (cs != null) cs.setEnrolledCount(cs.getEnrolledCount() - 1);
        }
        return removed;
    }

    // get classes student is enrolled in
    public List<ClassSection> getByStudent(int studentId) {
        List<ClassSection> result = new ArrayList<>();
        for (StudentEnrollment e : enrollments) {
            if (e.getStudentId() == studentId) {
                ClassSection c = getById(e.getClassSectionId());
                if (c != null) result.add(c);
            }
        }
        return result;
    }
}
