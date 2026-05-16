package com.sms.entity;

public class Teacher extends User {
    private Faculty faculty;
    private String email;
    private String phone;

    public Teacher() { super(); }

    public Teacher(int id, String username, String password, String name, String status,
                   Faculty faculty, String email, String phone) {
        super(id, username, password, name, "TEACHER", status);
        this.faculty = faculty;
        this.email = email;
        this.phone = phone;
    }

    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() { return getName(); }
}
