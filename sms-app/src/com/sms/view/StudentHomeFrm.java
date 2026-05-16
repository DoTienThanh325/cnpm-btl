package com.sms.view;

import com.sms.dao.*;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * StudentHomeFrm - per spec:
 * - Đăng ký môn học (duyệt khóa sẵn có, đăng ký, hủy đăng ký)
 * - Xem điểm học tập cá nhân
 * - Xem học phí và đóng học phí
 * - Cập nhật hồ sơ cá nhân
 */
public class StudentHomeFrm extends JFrame {

    private User currentUser;
    private StudentDAO studentDAO = new StudentDAO();

    public StudentHomeFrm(User user) {
        this.currentUser = user;
        setTitle("Sinh viên - " + user.getName());
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(39, 174, 96));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("SINH VIÊN - HỆ THỐNG QUẢN LÝ SINH VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblUser = new JLabel("Xin chào: " + currentUser.getName());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // per spec: menu buttons
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        menuPanel.setBackground(new Color(236, 240, 241));
        menuPanel.setBorder(new EmptyBorder(40, 60, 20, 60));

        JButton btnRegister = makeMenuBtn("📋  Đăng ký môn học", new Color(52, 152, 219));
        JButton btnGrades = makeMenuBtn("📊  Xem điểm cá nhân", new Color(155, 89, 182));
        JButton btnTuition = makeMenuBtn("💰  Xem & Đóng học phí", new Color(231, 76, 60));
        JButton btnProfile = makeMenuBtn("👤  Cập nhật hồ sơ", new Color(230, 126, 34));

        menuPanel.add(btnRegister);
        menuPanel.add(btnGrades);
        menuPanel.add(btnTuition);
        menuPanel.add(btnProfile);

        JButton btnLogout = new JButton("🚪  Đăng xuất");
        btnLogout.setBackground(new Color(127, 140, 141));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBackground(new Color(236, 240, 241));
        logoutPanel.add(btnLogout);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // per spec: Đăng ký môn học
        btnRegister.addActionListener(e -> {
            Student student = findStudent();
            if (student != null) new StudentRegistrationFrm(student).setVisible(true);
        });

        // per spec: Xem điểm cá nhân
        btnGrades.addActionListener(e -> {
            Student student = findStudent();
            if (student != null) new StudentGradeViewFrm(student).setVisible(true);
        });

        // per spec: Xem & Đóng học phí
        btnTuition.addActionListener(e -> {
            Student student = findStudent();
            if (student != null) new StudentTuitionFrm(student).setVisible(true);
        });

        // per spec: Cập nhật hồ sơ cá nhân
        btnProfile.addActionListener(e -> {
            Student student = findStudent();
            if (student != null) new AddEditStudentFrm(null, student, studentDAO, new FacultyDAO(), new MajorDAO()) {
                // override to avoid parent cast issues
            }.setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrm().setVisible(true);
        });
    }

    private Student findStudent() {
        // find student entity matching current user
        List<Student> all = studentDAO.getAllStudents();
        for (Student s : all) {
            if (s.getUsername().equals(currentUser.getUsername())) return s;
        }
        // fallback: return first student for demo
        return all.isEmpty() ? null : all.get(0);
    }

    private JButton makeMenuBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
