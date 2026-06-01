package com.sms.view;

import com.sms.dao.GradeDAO;
import com.sms.entity.Grade;
import com.sms.entity.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * StudentGradeViewFrm - per spec: sinh viên xem điểm cá nhân
 */
public class StudentGradeViewFrm extends JFrame {

    private Student student;
    private GradeDAO gradeDAO = new GradeDAO();

    public StudentGradeViewFrm(Student student) {
        this.student = student;
        setTitle("Xem điểm - " + student.getName() + " (" + student.getMssv() + ")");
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("Điểm học tập của: " + student.getName() + " - " + student.getMssv());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));

        // per spec columns: Điểm liên kết Sinh viên với Lớp học phần, gồm điểm thành phần, điểm thi, tổng kết, học kỳ
        String[] cols = {"Môn học", "Mã lớp", "Học kỳ", "Điểm chuyên cần", "Điểm giữa kỳ", "Điểm cuối kỳ", "Điểm tổng kết"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        List<Grade> grades = gradeDAO.getGradesByStudent(student.getId());
        for (Grade g : grades) {
            model.addRow(new Object[]{
                g.getClassSection() != null && g.getClassSection().getSubject() != null
                        ? g.getClassSection().getSubject().getName() : "",
                g.getClassSection() != null ? g.getClassSection().getCode() : "",
                g.getSemester(),
                g.getAttendanceScore(), g.getMidtermScore(), g.getFinalScore(),
                String.format("%.2f", g.getTotalScore())
            });
        }

        JButton btnBack = new JButton("Quay lại");
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.add(btnBack);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        btnBack.addActionListener(e -> dispose());
    }
}
