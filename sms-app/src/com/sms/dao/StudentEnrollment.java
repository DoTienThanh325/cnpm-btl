package com.sms.dao;

// Simple enrollment record (N-N relationship per spec: StudentEnrollment)
public class StudentEnrollment {
    private int studentId;
    private int classSectionId;

    public StudentEnrollment(int studentId, int classSectionId) {
        this.studentId = studentId;
        this.classSectionId = classSectionId;
    }

    public int getStudentId() { return studentId; }
    public int getClassSectionId() { return classSectionId; }
}
