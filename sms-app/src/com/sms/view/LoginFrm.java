package com.sms.view;

import com.sms.dao.UserDAO;
import com.sms.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LoginFrm - per spec section C.3.1
 * Has: username field, password field, Login button
 */
public class LoginFrm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO = new UserDAO();

    public LoginFrm() {
        setTitle("Hệ thống Quản lý Sinh viên - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 62, 80));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ SINH VIÊN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(236, 240, 241));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 35));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnLogin, gbc);

        // hint label
        JLabel lblHint = new JLabel("Gợi ý: admin/admin123 | pdt01/pdt123 | gv01/gv123 | sv01/sv123", JLabel.CENTER);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 10));
        lblHint.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(lblHint, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Action listener per spec
        btnLogin.addActionListener((ActionEvent e) -> actionPerformed());
        txtPassword.addActionListener((ActionEvent e) -> actionPerformed());
    }

    private void actionPerformed() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // per spec: call UserDAO.checkLogin()
        User user = userDAO.checkLogin(username, password);

        if (user == null) {
            // per spec exception: "Hệ thống báo sai tài khoản hoặc mật khẩu"
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // per spec: redirect based on role
        this.dispose();
        switch (user.getRole()) {
            case "PDT":
                new AcademicAffairsHomeFrm(user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Tài khoản này không thuộc module Cập nhật Lớp học phần!",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                this.setVisible(true); // show login again
        }
    }
}
