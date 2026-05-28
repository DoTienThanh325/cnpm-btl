package com.sms.view;

import com.sms.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * AcademicAffairsHomeFrm - module Cập nhật Lớp học phần
 * Chỉ hiển thị chức năng Quản lý Lớp học phần (theo yêu cầu nhóm trưởng)
 */
public class AcademicAffairsHomeFrm extends JFrame {

    private User currentUser;

    public AcademicAffairsHomeFrm(User user) {
        this.currentUser = user;
        setTitle("Phòng Đào Tạo - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("PHÒNG ĐÀO TẠO - HỆ THỐNG QUẢN LÝ SINH VIÊN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblUser = new JLabel("Xin chào: " + currentUser.getName());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // Center: chỉ 1 chức năng
        JPanel menuPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        menuPanel.setBackground(new Color(236, 240, 241));
        menuPanel.setBorder(new EmptyBorder(50, 80, 50, 80));

        JButton btnClasses = createMenuButton("🏫  Quản lý Lớp học phần", new Color(230, 126, 34));
        JButton btnLogout = createMenuButton("🚪  Đăng xuất", new Color(127, 140, 141));

        menuPanel.add(btnClasses);
        menuPanel.add(btnLogout);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnClasses.addActionListener((ActionEvent e) -> {
            new ManageClassFrm(currentUser).setVisible(true);
        });

        btnLogout.addActionListener((ActionEvent e) -> {
            this.dispose();
            new LoginFrm().setVisible(true);
        });
    }

    private JButton createMenuButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
