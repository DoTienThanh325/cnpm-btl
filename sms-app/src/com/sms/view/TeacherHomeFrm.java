package com.sms.view;

import com.sms.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * TeacherHomeFrm - per spec section B.1.2 / C.3.2
 * Has: chức năng sửa điểm button
 */
public class TeacherHomeFrm extends JFrame {

    private User currentUser;

    public TeacherHomeFrm(User user) {
        this.currentUser = user;
        setTitle("Giảng viên - " + user.getName());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("GIẢNG VIÊN - HỆ THỐNG QUẢN LÝ SINH VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblUser = new JLabel("Xin chào: " + currentUser.getName());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // Menu per spec: Xem lớp dạy, Nhập/Sửa điểm
        JPanel menuPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        menuPanel.setBackground(new Color(236, 240, 241));
        menuPanel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JButton btnViewClasses = new JButton("👁  Xem danh sách lớp đang giảng dạy");
        styleLargeButton(btnViewClasses, new Color(52, 152, 219));

        // per spec: nút sửa điểm - TeacherHomeView has "sửa điểm" button
        JButton btnGrades = new JButton("📝  Sửa điểm sinh viên");
        styleLargeButton(btnGrades, new Color(155, 89, 182));

        JButton btnLogout = new JButton("🚪  Đăng xuất");
        styleLargeButton(btnLogout, new Color(127, 140, 141));

        menuPanel.add(btnViewClasses);
        menuPanel.add(btnGrades);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBackground(new Color(236, 240, 241));
        logoutPanel.add(btnLogout);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // per spec: click Xem lớp dạy
        btnViewClasses.addActionListener(e -> {
            new TeacherClassListFrm(currentUser).setVisible(true);
        });

        // per spec: click Sửa điểm → SearchClassFrm / SearchClassForGradeFrm
        btnGrades.addActionListener(e -> {
            new SearchClassForGradeFrm(currentUser, true).setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrm().setVisible(true);
        });
    }

    private void styleLargeButton(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 60));
    }
}
