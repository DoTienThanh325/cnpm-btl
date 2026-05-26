package com.sms.entity;

import java.util.ArrayList;
import java.util.List;

public class ClassSection {
    private int id;
    private String code;
    private Subject subject;
    private Teacher teacher;
    // private List<Session> sessions;
    private int capacity;
    private int enrolledCount;
    private String status; // active, cancelled

    // public ClassSection() {
    //     this.sessions = new ArrayList<>();
    // }

    public ClassSection(int id, String code, Subject subject, Teacher teacher,
                        int capacity, int enrolledCount, String status) {
        this.id = id;
        this.code = code;
        this.subject = subject;
        this.teacher = teacher;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
        this.status = status;
        // this.sessions = new ArrayList<>();
    }

    // Business methods per spec
    public void setSubject(Subject subject) { this.subject = subject; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    // public void setSession(List<Session> sessions) { this.sessions = sessions; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Subject getSubject() { return subject; }
    public Teacher getTeacher() { return teacher; }
    // public List<Session> getSessions() { return sessions; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return code + " - " + (subject != null ? subject.getName() : ""); }
}
