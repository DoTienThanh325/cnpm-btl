package com.sms.dao;

public class StudentEnrollment {
    private final int studentId;
    private final int classSectionId;

    public StudentEnrollment(int studentId, int classSectionId) {
        this.studentId = studentId;
        this.classSectionId = classSectionId;
    }

    public int getStudentId() { return studentId; }
    public int getClassSectionId() { return classSectionId; }
}
