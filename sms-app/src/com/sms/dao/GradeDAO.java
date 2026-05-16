package com.sms.dao;

import com.sms.entity.Grade;
import com.sms.entity.Student;
import com.sms.entity.ClassSection;
import java.util.*;

public class GradeDAO {
    private static List<Grade> grades = new ArrayList<>();
    private static int nextId = 10;

    static {
        StudentDAO sDao = new StudentDAO();
        ClassSectionDAO cDao = new ClassSectionDAO();

        Student sv01 = sDao.getById(5);
        Student sv02 = sDao.getById(6);
        Student sv03 = sDao.getById(7);
        Student sv04 = sDao.getById(8);
        ClassSection c1 = cDao.getById(1);
        ClassSection c3 = cDao.getById(3);

        grades.add(new Grade(1, sv01, c1, 7, 8, 9, "2024-2"));
        grades.add(new Grade(2, sv02, c1, 8, 9, 8, "2024-2"));
        grades.add(new Grade(3, sv03, c1, 7, 7, 8, "2024-2"));
        grades.add(new Grade(4, sv01, c3, 8, 7, 9, "2024-2"));
        grades.add(new Grade(5, sv02, c3, 9, 8, 7, "2024-2"));
        grades.add(new Grade(6, sv03, c3, 7, 8, 8, "2024-2"));
        grades.add(new Grade(7, sv04, c3, 8, 9, 9, "2024-2"));
    }

    // per spec: getGradeByStudentAndClass()
    public Grade getGradeByStudentAndClass(int studentId, int classSectionId) {
        for (Grade g : grades) {
            if (g.getStudent().getId() == studentId && g.getClassSection().getId() == classSectionId) {
                return g;
            }
        }
        return null;
    }

    public List<Grade> getGradesByStudent(int studentId) {
        List<Grade> result = new ArrayList<>();
        for (Grade g : grades) {
            if (g.getStudent().getId() == studentId) result.add(g);
        }
        return result;
    }

    public List<Grade> getGradesByClass(int classSectionId) {
        List<Grade> result = new ArrayList<>();
        for (Grade g : grades) {
            if (g.getClassSection().getId() == classSectionId) result.add(g);
        }
        return result;
    }

    // per spec: updateGrade()
    public boolean updateGrade(Grade grade) {
        for (int i = 0; i < grades.size(); i++) {
            if (grades.get(i).getId() == grade.getId()) {
                grades.set(i, grade);
                return true;
            }
        }
        return false;
    }

    public boolean addGrade(Grade grade) {
        grade.setId(nextId++);
        grades.add(grade);
        return true;
    }

    // ensure grade record exists for all students in a class
    public void ensureGradesForClass(int classSectionId, List<Student> students, ClassSection cs) {
        for (Student s : students) {
            Grade existing = getGradeByStudentAndClass(s.getId(), classSectionId);
            if (existing == null) {
                grades.add(new Grade(nextId++, s, cs, 0, 0, 0, "2024-2"));
            }
        }
    }
}
