package com.sms.entity;

public class Teacher extends User {
    private String email;
    private String phone;

    public Teacher() { super(); }

    public Teacher(int id, String username, String password, String name, String status,
                   String email, String phone) {
        super(id, username, password, name, "TEACHER", status);
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() { return getName(); }
}
