package com.sms.entity;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private int id;
    private String code;
    private String name;
    private int credits;
    private String content;
    private Faculty faculty;
    private List<Textbook> textbooks;
    private String status; // active, closed

    public Subject() {
        this.textbooks = new ArrayList<>();
    }

    public Subject(int id, String code, String name, int credits, String content, Faculty faculty, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.content = content;
        this.faculty = faculty;
        this.status = status;
        this.textbooks = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    public List<Textbook> getTextbooks() { return textbooks; }
    public void setTextbooks(List<Textbook> textbooks) { this.textbooks = textbooks; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return code + " - " + name; }
}
