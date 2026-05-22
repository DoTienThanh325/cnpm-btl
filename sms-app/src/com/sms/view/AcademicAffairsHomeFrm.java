package com.sms.view;

import com.sms.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AcademicAffairsHomeFrm extends JFrame {

    private final User currentUser;

    public AcademicAffairsHomeFrm(User user) {
        this.currentUser = user;
        setTitle("Phong Dao Tao - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("PHONG DAO TAO - QUAN LY MON HOC");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblUser = new JLabel("Xin chao: " + currentUser.getName());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        JPanel menuPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        menuPanel.setBackground(new Color(236, 240, 241));
        menuPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JButton btnSubjects = createMenuButton("Quan ly Mon hoc", new Color(46, 204, 113));
        JButton btnLogout = createMenuButton("Dang xuat", new Color(127, 140, 141));
        menuPanel.add(btnSubjects);
        menuPanel.add(btnLogout);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnSubjects.addActionListener((ActionEvent e) -> new SubjectFrm(currentUser).setVisible(true));
        btnLogout.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginFrm().setVisible(true);
        });
    }

    private JButton createMenuButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 70));
        return btn;
    }
}
