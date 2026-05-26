package com.sms.entity;

public class Grade {
    private int id;
    private Student student;
    private ClassSection classSection;
    private double attendanceScore;  // điểm chuyên cần
    private double midtermScore;     // điểm giữa kỳ
    private double finalScore;       // điểm cuối kỳ
    private double totalScore;       // điểm tổng kết
    private String semester;

    public Grade() {}

    public Grade(int id, Student student, ClassSection classSection,
                 double attendanceScore, double midtermScore, double finalScore, String semester) {
        this.id = id;
        this.student = student;
        this.classSection = classSection;
        this.attendanceScore = attendanceScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
        this.semester = semester;
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalScore = attendanceScore * 0.1 + midtermScore * 0.3 + finalScore * 0.6;
    }

    // per spec: getGradeByStudentAndClass() and updateGrade()
    public void updateGrade(double attendance, double midterm, double finalScore) {
        this.attendanceScore = attendance;
        this.midtermScore = midterm;
        this.finalScore = finalScore;
        calculateTotal();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public ClassSection getClassSection() { return classSection; }
    public void setClassSection(ClassSection classSection) { this.classSection = classSection; }
    public double getAttendanceScore() { return attendanceScore; }
    public void setAttendanceScore(double attendanceScore) { this.attendanceScore = attendanceScore; calculateTotal(); }
    public double getMidtermScore() { return midtermScore; }
    public void setMidtermScore(double midtermScore) { this.midtermScore = midtermScore; calculateTotal(); }
    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; calculateTotal(); }
    public double getTotalScore() { return totalScore; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
