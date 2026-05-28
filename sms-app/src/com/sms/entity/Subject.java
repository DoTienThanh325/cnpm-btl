package com.sms.entity;

public class Subject {
    private int id;
    private String code;
    private String name;
    private int credits;
    private String content;
    private String status;

    public Subject() {}

    public Subject(int id, String code, String name, int credits, String content, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.content = content;
        this.status = status;
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return code + " - " + name; }
}
