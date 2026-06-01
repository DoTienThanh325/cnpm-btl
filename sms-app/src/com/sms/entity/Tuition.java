package com.sms.entity;

public class Tuition {
    private int id;
    private Student student;
    private String semester;
    private int registeredCredits;
    private double pricePerCredit;
    private double totalFee;
    private double paid;
    private String status; // đã đóng, chưa đóng, miễn giảm

    public Tuition() {}

    public Tuition(int id, Student student, String semester, int registeredCredits,
                   double pricePerCredit, double paid, String status) {
        this.id = id;
        this.student = student;
        this.semester = semester;
        this.registeredCredits = registeredCredits;
        this.pricePerCredit = pricePerCredit;
        this.paid = paid;
        this.status = status;
        this.totalFee = registeredCredits * pricePerCredit;
    }

    public void calculateFee() {
        this.totalFee = registeredCredits * pricePerCredit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public int getRegisteredCredits() { return registeredCredits; }
    public void setRegisteredCredits(int registeredCredits) { this.registeredCredits = registeredCredits; calculateFee(); }
    public double getPricePerCredit() { return pricePerCredit; }
    public void setPricePerCredit(double pricePerCredit) { this.pricePerCredit = pricePerCredit; calculateFee(); }
    public double getTotalFee() { return totalFee; }
    public double getPaid() { return paid; }
    public void setPaid(double paid) { this.paid = paid; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
