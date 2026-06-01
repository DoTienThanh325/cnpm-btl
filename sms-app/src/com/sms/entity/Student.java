package com.sms.entity;

public class Student extends User {
    private String mssv;
    private String dob;
    private String gender;
    private String address;
    private String email;
    private String phone;
    // private Faculty faculty;
    // private Major major;
    private String cohort;
    private String adminClass;
    private String studentStatus; // đang học, nghỉ học, tốt nghiệp

    public Student() { super(); }

    public Student(int id, String username, String password, String name, String status,
                   String mssv, String dob, String gender, String address,
                   String email, String phone,
                   String cohort, String adminClass, String studentStatus) {
        super(id, username, password, name, "STUDENT", status);
        this.mssv = mssv;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.phone = phone;
        // this.faculty = faculty;
        // this.major = major;
        this.cohort = cohort;
        this.adminClass = adminClass;
        this.studentStatus = studentStatus;
    }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    // public Faculty getFaculty() { return faculty; }
    // public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    // public Major getMajor() { return major; }
    // public void setMajor(Major major) { this.major = major; }
    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }
    public String getAdminClass() { return adminClass; }
    public void setAdminClass(String adminClass) { this.adminClass = adminClass; }
    public String getStudentStatus() { return studentStatus; }
    public void setStudentStatus(String studentStatus) { this.studentStatus = studentStatus; }

    // per spec - getStudentByClass() is called on Student entity
    public static String getStudentByClass() { return "getStudentByClass"; }

    @Override
    public String toString() { return mssv + " - " + getName(); }
}
