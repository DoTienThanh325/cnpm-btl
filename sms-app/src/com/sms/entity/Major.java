package com.sms.entity;

public class Major {
    private int id;
    private String code;
    private String name;
    private Faculty faculty;

    public Major() {}

    public Major(int id, String code, String name, Faculty faculty) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.faculty = faculty;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }

    @Override
    public String toString() { return name; }
}
